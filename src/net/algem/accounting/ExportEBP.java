/*
 * @(#) ExportEBP.java Algem 2.15.0 10/07/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.accounting;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import net.algem.billing.VatIO;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 10/07/2017
 */
public class ExportEBP 
extends CommonAccountExportService
{

  private DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
  private NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
  private static char cd = 'C';// credit
  private static char dc = 'D';//debit
  
  public ExportEBP(DataConnection dc) {
    dbx = dc;
    journalService = new JournalAccountService(dc);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
  }
  @Override
  public String getFileExtension() {
    return ".txt";
  }

  @Override
  public void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException {
    int totalDebit = 0;
    int totalCredit = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    String label = (documentAccount == null) ? "" : TextUtil.stripDiacritics(documentAccount.getLabel());
    OrderLine e = null;
    Map<Integer,Contact> accountInfo = new HashMap<>();
    try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1), true)) {
      int lineNumber = 0;
      StringBuilder sb = new StringBuilder();
      
      for (int i = 0, n = lines.size(); i < n; i++) {
        e = lines.elementAt(i);
        if (e.getAmount() > 0) {
          totalDebit += e.getAmount();
        } else {
          totalCredit += Math.abs(e.getAmount());
        }
        String account = getAccount(e);
        int payer = Integer.parseInt(account.substring(3));
        if (accountInfo.get(payer) == null) {
          Contact cp = ContactIO.findId(payer, dbx);
          accountInfo.put(payer, cp);
        }
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(account).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append(TextUtil.stripDiacritics(e.getLabel())).append('"').append(',');// libelle
        sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
        sb.append(nf.format(e.getAmount() / 100.0)).append(',');//montant
        sb.append(e.getAmount() > 0 ? cd : dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
      }
      if (totalDebit > 0) {
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(number).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append("CENTRALISE").append('"').append(',');// libelle
        sb.append(',');// numéro de pièce
        sb.append(nf.format(totalDebit / 100.0)).append(',');//montant
        sb.append(dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
      }
      if (totalCredit > 0) {
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(number).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append("CENTRALISE C").append('"').append(',');// libelle
        sb.append(',');// numéro de pièce
        sb.append(nf.format(totalCredit / 100.0)).append(',');//montant
        sb.append(dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
      }
    }
    // write COMPTES.TXT
    for (Contact c : accountInfo.values()) {
      //TODO
    }
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException {
    VatIO vatIO = new VatIO(dbx);
    OrderLine e = null;
    int errors = 0;
    boolean m1 = false;
    boolean m2 = false;
    String message = "";
    StringBuilder logMessage = new StringBuilder();
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    String logpath = path + ".log";

    try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1), true)) {
      int lineNumber = 0;
      StringBuilder sb = new StringBuilder();
      for (int i = 0, n = lines.size(); i < n; i++) {
        e = lines.elementAt(i);
        if (!AccountUtil.isPersonalAccount(e.getAccount())) {
          errors++;
          logMessage.append(m1prefix).append(" -> ").append(e).append(" [").append(e.getAccount()).append("]").append(TextUtil.LINE_SEPARATOR);
          m1 = true;
          continue;
        }

        int p = getPersonalAccountId(e.getAccount().getId());
        if (p == 0) {
          errors++;
          logMessage.append(m2prefix).append(" -> ").append(e.getAccount()).append(TextUtil.LINE_SEPARATOR);
          m2 = true;
          continue;
        }

        Account taxAccount = null;
        double exclTax = 0;//HT
        double vat = 0;
        if (e.getTax() > 0.0) {
          taxAccount = getTaxAccount(e.getTax(), vatIO);
          double coeff = 100 / (100 + e.getTax());
          exclTax = AccountUtil.round((Math.abs(e.getAmount()) / 100d) * coeff);
          vat = AccountUtil.round((Math.abs(e.getAmount()) / 100d) - exclTax);
        }
        // COMPTE DE PRODUIT (7xx)
        Account c = getAccount(p);
        String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
        String codeJournal = getCodeJournal(e.getAccount().getId());

        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(c.getNumber()).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append(TextUtil.stripDiacritics(e.getLabel())).append('"').append(',');// libelle
        sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
        sb.append(exclTax > 0 ? nf.format(exclTax) : m).append(',');//montant
        sb.append(e.getAmount() < 0 ? cd : dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
        // ANALYTIQUE
        String ca = e.getCostAccount().getNumber();
        if (ca != null && ca.length() > 0) {
          sb.append('>').append(e.getCostAccount().getNumber()).append(',').append("100.00").append(',').append(exclTax > 0 ? nf.format(exclTax) : m).append("\r\n");
        }
        out.print(sb.toString());
        sb.delete(0, sb.length());
        // TVA
        if (vat > 0.0) {
          assert (taxAccount != null);
          sb.append(++lineNumber).append(','); // line number (ignored)
          sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
          sb.append(codeJournal).append(',');//journal
          sb.append(taxAccount.getNumber()).append(',');//numéro compte
          sb.append(',');// libellé auto
          sb.append('"').append(TextUtil.stripDiacritics(taxAccount.getLabel())).append('"').append(',');// libelle
          sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
          sb.append(nf.format(vat)).append(',');
          sb.append(e.getAmount() < 0 ? cd : dc).append(',');
          sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
          sb.append("\r\n");
          out.print(sb.toString());
          sb.delete(0, sb.length());
        }
        // COMPTE D'ATTENTE (411)
        String debit = getAccount(e);
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(debit).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append(TextUtil.stripDiacritics(e.getLabel())).append('"').append(',');// libelle
        sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
        sb.append(m).append(',');// montant
        sb.append(e.getAmount() < 0 ? dc : cd).append(','); // debit
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());

      }
    }// out.close()

    if (logMessage.length() > 0) {
      try (PrintWriter log = new PrintWriter(new FileWriter(logpath))) {
        log.println(logMessage.toString());
      }
    }

    if (errors > 0) {
      if (m1) {
        message += MessageUtil.getMessage("personal.account.export.warning");
      }
      if (m2) {
        message += MessageUtil.getMessage("no.revenue.matching.warning");
      }
      String err = MessageUtil.getMessage("error.count.warning", errors);
      String l = MessageUtil.getMessage("see.log.file", path);
      MessagePopup.warning(null, err + message + l);
    }
    return errors;
  }

}
