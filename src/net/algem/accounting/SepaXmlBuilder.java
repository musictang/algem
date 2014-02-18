/*
 * @(#)SepaXmlBuilder.java	2.8.s 17/02/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.algem.accounting;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import net.algem.bank.BankUtil;
import net.algem.planning.DateFr;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;

/**
 * Xml builder for creating documents in pain.008.001.02 standard scheme.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.s
 * @see <a href="http:\\www.iso20022.org">iso20022</a>
 * @since 2.8.r 24/12/13
 *
 */
public class SepaXmlBuilder
{

  public static DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static String TAB = "  ";
  private static short MAX_NAME_LENGTH = 70;
  private static int MAX_LENGTH = 140;
  private static int SHORT_LENGTH = 35;
  private DirectDebitService service;
  private StringBuilder sbMailing, sbLog;
  private String ibanRegex = "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}";
  private Pattern ibanPattern;
  private int totalDebit = 0;
  private int numberOfGlobalTx = 0;
  private int batch;
  private List<Integer> firstDebited = new ArrayList<Integer>();
  private List<Integer> debited = new ArrayList<Integer>();
  private NumberFormat nf;
  private String msgId;
  private String txRmtInf;

  public SepaXmlBuilder(DirectDebitService service) {
    this.service = service;
    ibanPattern = Pattern.compile(ibanRegex);
    sbMailing = new StringBuilder();
    sbLog = new StringBuilder();
    nf = NumberFormat.getInstance(Locale.UK);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
  }

  public int getNumberOfTx() {
    return numberOfGlobalTx;
  }

  public int getTotalTx() {
    return totalDebit;
  }

  public StringBuilder getLog() {
    return sbLog;
  }

  public StringBuilder getMailing() {
    return sbMailing;
  }

  List<Integer> getFirstDebited() {
    return firstDebited;
  }

  List<Integer> getDebited() {
    return debited;
  }

  String getDocument() {
    StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    indent(sb, 0);
    sb.append("<Document xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02\">");
    //sb.append(" xsi:schemaLocation=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02 file:/src/git/trunk/test/net/algem/pain.008.001.02.xsd\">");
    indent(sb, 1);
    sb.append("<CstmrDrctDbtInitn>");
    return sb.toString();
  }

  String getGroupHeader() {

    StringBuilder sb = new StringBuilder();
    indent(sb, 2);
    sb.append("<GrpHdr>");
    indent(sb, 3);
    sb.append("<MsgId>").append(getMessageId()).append("</MsgId>");
    indent(sb, 3);
    sb.append("<CreDtTm>").append(getIsoDateTime(new Date())).append("</CreDtTm>");
    indent(sb, 3);
    sb.append("<NbOfTxs>").append(numberOfGlobalTx).append("</NbOfTxs>");
    indent(sb, 3);
    sb.append("<CtrlSum>").append(formatAmount(totalDebit)).append("</CtrlSum>");
    indent(sb, 3);
    sb.append("<InitgPty><Nm>").append(service.getFirmName()).append("</Nm></InitgPty>");
    indent(sb, 2);
    sb.append("</GrpHdr>");

    return sb.toString();
  }

  String getPayment(int school, DateFr datePrl, DDSeqType seqType, int batchNumber) throws SQLException {

    StringBuilder sb = new StringBuilder();
    int totalPayment = 0;

    batch = batchNumber;
    int currentPayer = 0;
    int total = 0;
    int numberOfLocalTx = 0;
    String costAccount = null;

    ResultSet rs = service.getDirectDebit(school, datePrl, seqType);
    while (rs.next()) {
      int payer = rs.getInt(1);
      int amount = rs.getInt(2);
      costAccount = rs.getString(3);
      if (currentPayer == payer) {
        total += amount;
      } else {
        if (total > 0) {
          String tx = getDirectDebitTransaction(currentPayer, total, costAccount);
          if (tx != null && tx.length() > 0) {
            // ajout transaction au lot
            sb.append(tx);
            numberOfLocalTx++;
            numberOfGlobalTx++;
            totalPayment += total;
            totalDebit += total;
          }
        }
        total = amount;
        currentPayer = payer;
      }
    }
    if (total > 0) {
      String tx = getDirectDebitTransaction(currentPayer, total, costAccount);
      if (tx != null && tx.length() > 0) {
        sb.append(tx);
        numberOfLocalTx++;
        numberOfGlobalTx++;
        totalPayment += total;
        totalDebit += total;
      }
    }
    rs.close();

    if (numberOfLocalTx > 0) {
      sb.insert(0, getPaymentInformation(datePrl, seqType, batchNumber, totalPayment, numberOfLocalTx));
      indent(sb, 2);
      sb.append("</PmtInf>");
      batch++;

      return sb.toString();
    }
    return null;
  }

  int getBatch() {
    return batch;
  }

  StringBuilder getPaymentInformation(DateFr datePrl, DDSeqType seqType, int batchNumber, int total, int nbTx) {
    DirectDebitCreditor creditor = service.getCreditorInfo();
    StringBuilder sb = new StringBuilder();
    indent(sb, 2);
    sb.append("<PmtInf>");
    indent(sb, 3);
    sb.append("<PmtInfId>").append(msgId).append("-L").append(batchNumber).append("</PmtInfId>");
    indent(sb, 3);
    sb.append("<PmtMtd>DD</PmtMtd>");
    indent(sb, 3);
    sb.append("<BtchBookg>true</BtchBookg>");
    indent(sb, 3);
    sb.append("<NbOfTxs>").append(nbTx).append("</NbOfTxs>");
    indent(sb, 3);
    sb.append("<CtrlSum>").append(formatAmount(total)).append("</CtrlSum>");
    indent(sb, 3);
    sb.append("<PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl><LclInstrm><Cd>CORE</Cd></LclInstrm><SeqTp>").append(seqType.name()).append("</SeqTp></PmtTpInf>");
    indent(sb, 3);
    sb.append("<ReqdColltnDt>").append(getIsoDate(datePrl)).append("</ReqdColltnDt>");
    indent(sb, 3);
    sb.append("<Cdtr><Nm>").append(creditor.getFirmName()).append("</Nm></Cdtr>");
    indent(sb, 3);
    sb.append("<CdtrAcct><Id><IBAN>").append(creditor.getIban().toUpperCase()).append("</IBAN></Id></CdtrAcct>");
    indent(sb, 3);
    sb.append("<CdtrAgt><FinInstnId><BIC>").append(creditor.getBic()).append("</BIC></FinInstnId></CdtrAgt>");
    indent(sb, 3);
    sb.append("<CdtrSchmeId><Id><PrvtId><Othr><Id>").append(creditor.getIcs()).append("</Id><SchmeNm><Prtry>SEPA</Prtry></SchmeNm></Othr></PrvtId></Id></CdtrSchmeId>");

    return sb;
  }

  String getDirectDebitTransaction(int idper, int total, String analytique) throws SQLException {

    ResultSet rs = service.getTransaction(idper);
    String tx = null;

    if (rs.next()) {
      DDMandate mandate = getMandate(rs);
      tx = getTxElement(mandate, total);
      if (tx != null && tx.length() > 0) {
        if (isFirst(mandate.getSeqType())) {
          firstDebited.add(mandate.getId());
        }
        debited.add(mandate.getId());
        addMailingInfo(rs, total, analytique);
      }
    } else {
      addLogInfo(idper, null);
    }

    rs.close();

    return tx;

  }

  private String getMessageId() {
    return msgId;
  }

  void setMessageId() {
    msgId = String.valueOf(System.currentTimeMillis());
  }

  void setTxRmtInf(String label, DateFr datePrl) {
    String rmt = service.getTxInformationLabel(label, datePrl);
    if (rmt != null && rmt.length() > MAX_LENGTH) {
      rmt = rmt.substring(0, MAX_LENGTH);
    }
    txRmtInf = rmt;
  }

  private String getTxElement(DDMandate mandate, int amount) {

    StringBuilder sb = new StringBuilder();
    indent(sb, 3);
    sb.append("<DrctDbtTxInf>");
    indent(sb, 4);
    sb.append("<PmtId><EndToEndId>").append(getEnd2EndId()).append("</EndToEndId></PmtId>");
    indent(sb, 4);
    sb.append("<InstdAmt Ccy=\"EUR\">").append(formatAmount(amount)).append("</InstdAmt>");
    indent(sb, 4);
    assert (mandate.getRum() != null && mandate.getRum().length() > 0);
    sb.append("<DrctDbtTx><MndtRltdInf><MndtId>").append(mandate.getRum()).append("</MndtId>");
    sb.append("<DtOfSgntr>").append(getIsoDate(mandate.getDateSign())).append("</DtOfSgntr><AmdmntInd>false</AmdmntInd></MndtRltdInf></DrctDbtTx>");
    indent(sb, 4);
    String bic = mandate.getBic();
    if (bic != null && BankUtil.isBicOk(bic)) {
      sb.append("<DbtrAgt><FinInstnId><BIC>").append(mandate.getBic()).append("</BIC></FinInstnId></DbtrAgt>");
    } else {
      addLogInfo(mandate.getIdper(), " -> BIC");
      sb.append("<DbtrAgt><FinInstnId><Othr><Id>NOTPROVIDED</Id></Othr></FinInstnId></DbtrAgt>");
    }
    indent(sb, 4);
    sb.append("<Dbtr><Nm>").append(mandate.getName()).append("</Nm></Dbtr>");
    indent(sb, 4);
    String iban = mandate.getIban();
    if (iban != null && iban.length() > 0 && ibanPattern.matcher(iban).matches()) {
      sb.append("<DbtrAcct><Id><IBAN>").append(mandate.getIban().toUpperCase()).append("</IBAN></Id></DbtrAcct>");
    } else {
      addLogInfo(mandate.getIdper(), " -> IBAN");
      return null;
    }
    indent(sb, 4);
    sb.append("<RmtInf><Ustrd>").append(txRmtInf).append("</Ustrd></RmtInf>");

    indent(sb, 3);
    sb.append("</DrctDbtTxInf>");

    return sb.toString();
  }

  private DDMandate getMandate(ResultSet rs) throws SQLException {
    DDMandate mandate = new DDMandate(rs.getInt(1));
    String payerName = TextUtil.replaceChars(rs.getString(3));
    if (payerName.length() > MAX_NAME_LENGTH) {
      payerName = payerName.substring(0, MAX_NAME_LENGTH);
    }
    mandate.setName(payerName);
    mandate.setId(rs.getInt(9));
    mandate.setRum(rs.getString(10));
    mandate.setDateSign(new DateFr(rs.getDate(11)));
    mandate.setSeqType(DDSeqType.valueOf(rs.getString(12).trim()));
    mandate.setIban(rs.getString(13));
    mandate.setBic(rs.getString(14));

    return mandate;
  }

  private void addMailingInfo(ResultSet rs, int total, String costAccount) throws SQLException {
    if (sbMailing != null) {
      for (int i = 1; i <= 8; i++) {
        String info = rs.getString(i);
        sbMailing.append(info == null ? "" : info.trim()).append(";");
      }
      sbMailing.append(costAccount).append(";").append(total).append(TextUtil.LINE_SEPARATOR);
    }
  }

  private void addLogInfo(int id, String msg) {
    if (sbLog != null) {
      sbLog.append(MessageUtil.getMessage("payer.export.error", id)).append(msg == null ? "" : msg).append(TextUtil.LINE_SEPARATOR);
    }
  }

  private boolean isFirst(DDSeqType seqType) {
    switch (seqType) {
      case FRST:
        return true;
      default:
        return false;
    }
  }

  private String formatAmount(int sum) {
    /* String s = nf.format(sum / 100d);
     * s = s.replace(',', ' '); */
    return nf.format(sum / 100d);
  }

  private void indent(StringBuilder sb, int nbOfTabs) {
    sb.append(TextUtil.LINE_SEPARATOR);
    for (int i = 0; i < nbOfTabs; i++) {
      sb.append(TAB);
    }
  }

  private String getEnd2EndId() {
    UUID uuid = UUID.randomUUID();
    long lg = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    return Long.toString(lg, Character.MAX_RADIX).toUpperCase();
  }

  private String getIsoDate(DateFr date) {
    if (date == null || date.toString().equals(DateFr.NULLDATE)) {
      return ISO_DATE_FORMAT.format(new Date());
    }
    return ISO_DATE_FORMAT.format(date.getTime());
  }

  private String getIsoDate(String date) {
    if (date == null || date.equals(DateFr.NULLDATE)) {
      return ISO_DATE_FORMAT.format(new Date());
    }
    return ISO_DATE_FORMAT.format(new DateFr(date).getDate());
  }

  private String getIsoDateTime(DateFr date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    if (date == null || date.toString().equals(DateFr.NULLDATE)) {
      return df.format(new Date());
    }
    return df.format(date.getTime());
  }

  private String getIsoDateTime(Date date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    if (date == null) {
      return df.format(new Date());
    }
    return df.format(date.getTime());
  }
}
