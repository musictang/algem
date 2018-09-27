/*
 * @(#)ExportCiel.java	2.15.10 27/09/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
import java.util.Locale;
import java.util.Vector;
import net.algem.billing.VatIO;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for exporting lines to CIEL accounting software.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.10
 * @since 2.8.r 13/12/13
 */
public class ExportCiel
        extends CommonAccountExportService
{

  private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
  private NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
  private static char cd = 'C';// credit
  private static char dc = 'D';//debit
  private static final String VERSION = "2003";

  public ExportCiel(DataConnection dc) {
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
  public void export(String path, Vector<OrderLine> orderLines, String codeJournal, Account documentAccount) throws IOException {
    int totalDebit = 0;
    int totalCredit = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    String label = (documentAccount == null) ? "" : TextUtil.stripDiacritics(documentAccount.getLabel());
    OrderLine e = null;
    try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1), true)) {
      String movement = "1";
      for (int i = 0, n = orderLines.size(); i < n; i++) {
        e = orderLines.elementAt(i);
        if (e.getAmount() > 0) {
          totalDebit += e.getAmount();
        } else {
          totalCredit += Math.abs(e.getAmount());
        }
        out.print(TextUtil.padWithLeadingSpaces(movement, 5) // n° mouvement
                + TextUtil.padWithTrailingSpaces(codeJournal, 2) // code journal
                + dateFormat.format(e.getDate().getDate()) // date écriture
                + dateFormat.format(new Date()) // date échéance
                + TextUtil.padWithTrailingSpaces(e.getDocument(), REF_MAX_LENGTH) // libellé pièce
                + TextUtil.padWithTrailingSpaces(getAccount(e), 11) // n° compte
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(e.getLabel()) + getInvoiceNumber(e), 25), 25) // numéro de facture pour les echéances correspondant à une facture.
                + TextUtil.padWithLeadingSpaces(nf.format(e.getAmount() / 100.0), 13) // montant
                + (e.getAmount() > 0 ? cd : dc) // credit - debit
                + TextUtil.padWithTrailingSpaces(e.getDocument(), REF_MAX_LENGTH) // numéro pointage
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getCostAccount().getNumber(), 6), 6) // code analytique
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(e.getAccount().getLabel()), 34), 34) // libellé compte
                + "O" // lettre O pour Euro = Oui
                + VERSION
                + "\r\n");
      }
      if (totalDebit > 0) {
        out.print(TextUtil.padWithLeadingSpaces(movement, 5) // n° mouvement
                + TextUtil.padWithTrailingSpaces(codeJournal, 2) // code journal
                + dateFormat.format(e.getDate().getDate()) // date écriture
                + dateFormat.format(new Date()) // date échéance
                + TextUtil.padWithTrailingSpaces(null, 12) // libellé pièce
                + TextUtil.padWithTrailingSpaces(number, 11) // numéro dompte
                + TextUtil.padWithTrailingSpaces("CENTRALISE", 25)
                + TextUtil.padWithLeadingSpaces(nf.format(totalDebit / 100.0), 13) // montant
                + dc // débit
                + TextUtil.padWithTrailingSpaces(null, 12) // numéro pointage
                + TextUtil.padWithTrailingSpaces(null, 6) // code analytique
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(label, 34), 34) // libellé compte // todo null
                + "O" // lettre O pour Euro = Oui
                + VERSION
                + "\r\n");
      }
      if (totalCredit > 0) {
        out.print(TextUtil.padWithLeadingSpaces(movement, 5) // n° mouvement
                + TextUtil.padWithTrailingSpaces(codeJournal, 2) // code journal
                + dateFormat.format(e.getDate().getDate()) // date écriture
                + dateFormat.format(new Date()) // date échéance
                + TextUtil.padWithTrailingSpaces(null, 12) // libellé pièce
                + TextUtil.padWithTrailingSpaces(number, 11) // numéro dompte
                + TextUtil.padWithTrailingSpaces("CENTRALISE C", 25)
                + TextUtil.padWithLeadingSpaces(nf.format(totalCredit / 100.0), 13) // montant
                + cd // credit
                + TextUtil.padWithTrailingSpaces(null, 12) // numéro pointage
                + TextUtil.padWithTrailingSpaces(null, 6) // code analytique
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(label, 34), 34) // libellé compte // todo null
                + "O" // lettre O pour Euro = Oui
                + VERSION
                + "\r\n");
      }
    }
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> orderLines) throws IOException, SQLException {
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
      int movement = 1;
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

        Account taxAccount = null;
        double exclTax = 0;//HT
        double vat = 0;
        if (e.getTax() > 0.0) {
          taxAccount = getTaxAccount(e.getTax(), vatIO);
          double coeff = 100 / (100 + e.getTax());
          exclTax = AccountUtil.round((Math.abs(e.getAmount()) / 100d) * coeff);
          vat = AccountUtil.round((Math.abs(e.getAmount()) / 100d) - exclTax);
        }
        //mouvement++; // maybe do not increment, one operation/movement  ??
        // COMPTE DE PRODUIT (7xx)
        Account c = getAccount(p);
        String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
        String codeJournal = getCodeJournal(e.getAccount().getId());

        out.print(TextUtil.padWithLeadingSpaces(String.valueOf(movement), 5) // n° mouvement
                + TextUtil.padWithTrailingSpaces(codeJournal, 2) // code journal
                + dateFormat.format(e.getDate().getDate()) // date écriture
                + dateFormat.format(new Date()) // date échéance
                + TextUtil.padWithTrailingSpaces(e.getDocument(), REF_MAX_LENGTH) // libellé pièce
                + TextUtil.padWithTrailingSpaces(c.getNumber(), 11) // numéro compte
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(e.getLabel()) + getInvoiceNumber(e), 25), 25) // numéro de facture pour les echéances correspondant à une facture.
                + TextUtil.padWithLeadingSpaces(exclTax > 0 ? nf.format(exclTax) : m, 13) // montant
                + (e.getAmount() < 0 ? cd : dc) //cd crédit
                + TextUtil.padWithTrailingSpaces(e.getDocument(), REF_MAX_LENGTH) // numéro pointage
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getCostAccount().getNumber(), 6), 6) // code analytique
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(e.getAccount().getLabel()), 34), 34) // libellé compte
                + "O" // lettre O pour Euro = Oui
                + VERSION
                + "\r\n");
        // TVA
        if (vat > 0.0) {
          assert (taxAccount != null);
          out.print(TextUtil.padWithLeadingSpaces(String.valueOf(movement), 5) // n° mouvement
                  + TextUtil.padWithTrailingSpaces(codeJournal, 2) // code journal
                  + dateFormat.format(e.getDate().getDate()) // date écriture
                  + dateFormat.format(new Date()) // date échéance
                  + TextUtil.padWithTrailingSpaces(e.getDocument(), REF_MAX_LENGTH) // libellé pièce
                  + TextUtil.padWithTrailingSpaces(taxAccount.getNumber(), 11) // numéro dompte
                  + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(taxAccount.getLabel()) + getInvoiceNumber(e), 25), 25) // numéro de facture pour les echéances correspondant à une facture.
                  + TextUtil.padWithLeadingSpaces(nf.format(vat), 13) // montant
                  + (e.getAmount() < 0 ? cd : dc) //cd crédit
                  + TextUtil.padWithTrailingSpaces(e.getDocument(), REF_MAX_LENGTH) // numéro pointage
                  + TextUtil.padWithTrailingSpaces(TextUtil.truncate(null, 6), 6) // code analytique
                  + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(taxAccount.getLabel()), 34), 34) // libellé compte
                  + "O" // lettre O pour Euro = Oui
                  + VERSION
                  + "\r\n");
        }
        // COMPTE D'ATTENTE (411)
        String debit = getAccount(e);
        String debitLabel = debit.startsWith("411") && debit.length() > 3 ? "client " + debit.substring(3) : e.getAccountLabel();
        out.print(TextUtil.padWithLeadingSpaces(String.valueOf(movement), 5) // n° mouvement
                + TextUtil.padWithTrailingSpaces(codeJournal, 2) // code journal
                + dateFormat.format(e.getDate().getDate()) // date écriture
                + dateFormat.format(new Date()) // date échéance
                + TextUtil.padWithTrailingSpaces(null, 12) // libellé pièce
                + TextUtil.padWithTrailingSpaces(debit, 11) // numéro compte tiers
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(e.getLabel()), 25), 25) // libelle ecriture
                + TextUtil.padWithLeadingSpaces(m, 13) // montant
                + (e.getAmount() < 0 ? dc : cd) //dc débit
                + TextUtil.padWithTrailingSpaces(null, 12) // numéro pointage
                + TextUtil.padWithTrailingSpaces(null, 6) // code analytique
                + TextUtil.padWithTrailingSpaces(TextUtil.truncate(TextUtil.stripDiacritics(debitLabel), 34), 34) // libellé compte tiers
                + "O" // lettre O pour Euro = Oui
                + VERSION
                + "\r\n");
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
