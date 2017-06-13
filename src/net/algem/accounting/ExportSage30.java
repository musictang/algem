/*
 * @(#)ExportSage30.java	2.14.0 12/06/17
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
 *
 */
package net.algem.accounting;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for exporting lines to CIEL accounting software.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.8.r 17/12/13
 */
public class ExportSage30
        extends CommonAccountExportService
{

  private final DateFormat dateFormat = new SimpleDateFormat("ddMMyy");
  private final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
  private static final char cd = 'C';// credit
  private static final char dc = 'D';//debit
  private static final String default_document_type = "OD";
  private String dossierName;

  public ExportSage30(DataConnection dc) {
    dbx = dc;
    journalService = new JournalAccountService(dc);
    dossierName = ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getKey());
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
  }

  @Override
  /**
   * Export to 105 characters SAGE pnp format.
   */
  public void export(String path, Vector<OrderLine> orderLines, String codeJournal, Account documentAccount) throws IOException {
    int totalDebit = 0;
    int totalCredit = 0;

    OrderLine e = null;
    PrintWriter out = new PrintWriter(new FileWriter(path));

    out.print(TextUtil.truncate(dossierName, 30) + (char) 13);

    for (int i = 0, n = orderLines.size(); i < n; i++) {
      e = orderLines.elementAt(i);
      if (e.getAmount() > 0) {
        totalDebit += e.getAmount();
      } else {
        totalCredit += Math.abs(e.getAmount());
      }
      out.print(TextUtil.padWithTrailingSpaces(codeJournal, 3) // code journal
              + dateFormat.format(new Date()) // date écriture
              + default_document_type
              + TextUtil.padWithTrailingSpaces(e.getAccount().getNumber(), 13) // numéro dompte
              + 'A' // code analytique
              + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 13) // numéro analytique
              + TextUtil.padWithTrailingSpaces(e.getDocument(), 13) // libellé pièce
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel() + getInvoiceNumber(e), 25), 25) // libellé
              + getModeOfPayment(e.getModeOfPayment()) // mode de paiement
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + (e.getAmount() > 0 ? cd : dc) // credit : debit
              + TextUtil.padWithLeadingSpaces(nf.format(e.getAmount() / 100.0), 20) // montant
              + 'N' // Type
              + (char) 13);
    }
    if (totalDebit > 0) {
      out.print(TextUtil.padWithTrailingSpaces(codeJournal, 3) // code journal
              + dateFormat.format(new Date()) // date écriture
              + default_document_type
              + TextUtil.padWithTrailingSpaces(e.getAccount().getNumber(), 13) // numéro dompte
              + " " // code analytique
              + TextUtil.padWithTrailingSpaces(null, 13) // numéro analytique
              + TextUtil.padWithTrailingSpaces(null, 13) // libellé pièce
              + TextUtil.padWithTrailingSpaces("TOTAL DEBIT", 25) // libellé
              + 'S'// mode de paiement
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + dc // débit
              + TextUtil.padWithLeadingSpaces(nf.format(totalDebit / 100.0), 20) // montant
              + 'N' // Type
              + (char) 13);
    }
    if (totalCredit > 0) {
      out.print(TextUtil.padWithTrailingSpaces(codeJournal, 3) // code journal
              + dateFormat.format(new Date()) // date écriture
              + default_document_type
              + TextUtil.padWithTrailingSpaces(e.getAccount().getNumber(), 13) // numéro dompte
              + " " // code analytique
              + TextUtil.padWithTrailingSpaces(null, 13) // numéro analytique
              + TextUtil.padWithTrailingSpaces(null, 13) // libellé pièce
              + TextUtil.padWithTrailingSpaces("TOTAL CREDIT", 25) // libellé
              + 'S'// mode de paiement
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + cd // crédit
              + TextUtil.padWithLeadingSpaces(nf.format(totalCredit / 100.0), 20) // montant
              + 'N' // Type
              + (char) 13);
    }
    out.close();
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> orderLines) throws IOException, SQLException {

    OrderLine e = null;
    int errors = 0;
    boolean m1 = false;
    boolean m2 = false;
    String message = "";
    StringBuilder logMessage = new StringBuilder();
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    String logpath = path + ".log";
    PrintWriter out = new PrintWriter(new FileWriter(path));

    int mouvement = 0;

    out.print(TextUtil.truncate(dossierName, 30) + (char) 13);

    for (int i = 0, n = orderLines.size(); i < n; i++) {
      e = orderLines.elementAt(i);
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

      mouvement++;
      // COMPTE DE PRODUIT (7xx)
      Account c = getAccount(p);
      String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
      String codeJournal = getCodeJournal(e.getAccount().getId());

      out.print(TextUtil.padWithTrailingSpaces(codeJournal, 3) // code journal
              + dateFormat.format(new Date()) // date écriture
              + default_document_type
              + TextUtil.padWithTrailingSpaces(c.getNumber(), 13) // numéro compte client
              + "A" // code analytique
              + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 13) // numéro analytique
              + TextUtil.padWithTrailingSpaces(e.getDocument(), 13) // libellé pièce
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel() + getInvoiceNumber(e), 25), 25) // libellé
              + getModeOfPayment(e.getModeOfPayment()) // mode de paiement
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + (e.getAmount() < 0 ? cd : dc) // credit : debit
              + TextUtil.padWithLeadingSpaces(m, 20) // montant
              + "N" // Type
              + (char) 13);

      // COMPTE D'ATTENTE (411)
      String debit = getAccount(e);
      out.print(TextUtil.padWithTrailingSpaces(codeJournal, 3) // code journal
              + dateFormat.format(new Date()) // date écriture
              + default_document_type
              + TextUtil.padWithTrailingSpaces(debit, 13) // numéro compte tiers
              + " " // code analytique
              + TextUtil.padWithTrailingSpaces(null, 13) // numéro analytique
              + TextUtil.padWithTrailingSpaces(null, 13) // libellé pièce
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 25), 25) // libellé
              + "S" // mode de paiement
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + (e.getAmount() < 0 ? dc : cd) // débit : crédit
              + TextUtil.padWithLeadingSpaces(m, 20) // montant
              + "N" // Type
              + (char) 13);
    }
    out.close();

    if (logMessage.length() > 0) {
      PrintWriter log = new PrintWriter(new FileWriter(logpath));
      log.println(logMessage.toString());
      log.close();
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

  private char getModeOfPayment(String p) {
    if (p.equals(ModeOfPayment.CHQ.toString())) {
      return 'C';
    }
    if (p.equals(ModeOfPayment.FAC.toString())) {
      return 'S';
    }
    if (p.equals(ModeOfPayment.ESP.toString())) {
      return 'E';
    }
    if (p.equals(ModeOfPayment.PRL.toString())) {
      return 'P';
    }
    if (p.equals(ModeOfPayment.VIR.toString())) {
      return 'V';
    }
    if (p.equals(ModeOfPayment.NUL.toString())) {
      return 'S';
    }
    return 'S';
  }
}
