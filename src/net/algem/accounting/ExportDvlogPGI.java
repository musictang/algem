/*
 * @(#)ExportDvlogPGI.java	2.14.0 14/06/17
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
import java.util.Locale;
import java.util.Vector;
import net.algem.billing.VatIO;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for exporting lines to DVLOG PGI accounting software.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.8.r 13/12/13
 */
public class ExportDvlogPGI
  extends  CommonAccountExportService
{

  private static char cd = 'C';// credit
  private static char dc = 'D';//debit
  private DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
  private NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);

  public ExportDvlogPGI(DataConnection dc) {
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
    OrderLine e = null;
    PrintWriter out = new PrintWriter(new FileWriter(path));

    for (int i = 0, n = orderLines.size(); i < n ; i++) {
      e =  orderLines.elementAt(i);
      if (e.getAmount() > 0) {
        totalDebit += e.getAmount();
      } else {
        totalCredit += Math.abs(e.getAmount());
      }
      //String f = (AccountUtil.isPersonalAccount(e.getAccount()) && e.getInvoice() != null) ? e.getInvoice() : e.getInvoiceNumber();
      //out.print(padWithTrailingZeros(e.getAccount().getNumber(), 10)
      out.print(TextUtil.padWithTrailingZeros(getAccount(e), 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces(e.getDocument(), 10)
              // La valeur 13 ne semble pas obligatoire. On peut étendre la taille du champ.
              //+ "#" + padWithTrailingSpaces(truncate(e.getLabel(), 13), 13)
              + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel() + getInvoiceNumber(e), 24), 24) // numéro de facture pour les echéances correspondant à une facture.
              + "#" + TextUtil.padWithLeadingZeros(nf.format(Math.abs(e.getAmount()) / 100.0), 13)
              + "#" + (e.getAmount() > 0 ? cd : dc)
              + "#" + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
              + "#" + (char) 13);
    }
    if (totalDebit > 0) {
      out.print(
              TextUtil.padWithTrailingZeros(number, 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + TextUtil.padWithTrailingSpaces("CENTRALISE", 24) //  CENTRALISE
              + "#" + TextUtil.padWithLeadingZeros(nf.format(totalDebit / 100.0), 13)
              + "#" + dc
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
    }
    if (totalCredit > 0) {
      out.print(
              TextUtil.padWithTrailingZeros(number, 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + TextUtil.padWithTrailingSpaces("CENTRALISE C", 24)
              + "#" + TextUtil.padWithLeadingZeros(nf.format(totalCredit / 100.0), 13)
              + "#" + cd
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
    }
    out.close();
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> orderLines) throws IOException, SQLException {
    VatIO vatIO = new VatIO(dbx);
    int errors = 0;
    boolean m1 = false;
    boolean m2 = false;
    String message = "";
    StringBuilder log = new StringBuilder();
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    String logpath = path+".log";
    PrintWriter out = new PrintWriter(new FileWriter(path));
    OrderLine e = null;
    for (int i = 0, n = orderLines.size(); i < n ; i++) {
      e =  orderLines.elementAt(i);
      if (!AccountUtil.isPersonalAccount(e.getAccount())) {
        errors++;
        log.append(m1prefix).append(" -> ").append(e).append(" [").append(e.getAccount()).append("]").append(TextUtil.LINE_SEPARATOR);
        m1 = true;
        continue;
      }

      int p = getPersonalAccountId(e.getAccount().getId());
      if (p == 0) {
        errors++;
        log.append(m2prefix).append(" -> ").append(e.getAccount()).append(TextUtil.LINE_SEPARATOR);
        m2 = true;
        continue;
      }

      Account c = getAccount(p);
      int amount = e.getAmount();//TTC

      String codeJournal = getCodeJournal(e.getAccount().getId());
      String f = (e.getInvoice() == null) ? "" : e.getInvoice();
      Account taxAccount = null;
      double exclTax = 0;//HT
      double vat = 0;
      if (e.getTax() > 0.0) {
        taxAccount = getTaxAccount(e.getTax(), vatIO);
        double coeff = 100 / (100 + e.getTax());
        exclTax = AccountUtil.round((Math.abs(amount) /100d) * coeff);
        vat = AccountUtil.round((Math.abs(amount) /100d) - exclTax);
      }

      String m = nf.format(Math.abs(amount) / 100.0); // le montant doit être positif
      //COMPTE DE PRODUITS (7xx)
      out.print(TextUtil.padWithTrailingZeros(c.getNumber(), 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces(e.getDocument(), 10)
              + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 24), 24)
              + "#" + TextUtil.padWithLeadingZeros(exclTax > 0 ? nf.format(exclTax) : m, 13)
              + "#" + (e.getAmount() < 0 ? cd : dc) // cd Crédit
              + "#" + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
              + "#" + (char) 13);
      // TVA
      if (vat > 0.0) {
       assert(taxAccount != null);
       out.print(TextUtil.padWithTrailingZeros(taxAccount.getNumber(), 10)
          + "#" + dateFormat.format(e.getDate().getDate())
          + "#" + codeJournal
          + "#" + TextUtil.padWithTrailingSpaces(e.getDocument(), 10)
          + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 24), 24)
          + "#" + TextUtil.padWithLeadingZeros(nf.format(vat), 13)
          + "#" + (e.getAmount() < 0 ? cd : dc) // cd Crédit
          + "#" + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
          + "#" + (char) 13);
      }

      //COMPTE D'ATTENTE (411)
      out.print(
              TextUtil.padWithTrailingZeros(getAccount(e), 10) // compte client
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces(f, 10)
              + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 24), 24)
              + "#" + TextUtil.padWithLeadingZeros(m, 13)
              + "#" + (e.getAmount() < 0 ? dc : cd) //dc Débit
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
    }
    out.close();

    if (log.length() > 0) {
      PrintWriter pw = new PrintWriter(new FileWriter(logpath));
      pw.println(log.toString());
      pw.close();
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
      MessagePopup.warning(null, err+message+l);
    }

    return errors;
  }

}
