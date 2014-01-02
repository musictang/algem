/*
 * @(#)ExportDvlogPGI.java	2.8.r 13/12/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.model.ModelException;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for exporting lines to DVLOG PGI accounting software.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 13/12/13
 */
public class ExportDvlogPGI
  extends  CommunAccountExportService
{

  private DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
  private NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
  private static char cd = 'C';// credit
  private static char dc = 'D';//debit

  public ExportDvlogPGI(DataConnection dc) {
    dbx = dc;
    journalService = new JournalAccountService(dc);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2); 
  }
  
  @Override
  public void export(String path, Vector<OrderLine> orderLines, String codeJournal, Account documentAccount) throws IOException {
    int total = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    OrderLine e = null;
    PrintWriter out = new PrintWriter(new FileWriter(path));

    for (int i = 0, n = orderLines.size(); i < n ; i++) {
      e =  orderLines.elementAt(i);
      total += e.getAmount();
      //String f = (AccountUtil.isPersonalAccount(e.getAccount()) && e.getInvoice() != null) ? e.getInvoice() : e.getInvoiceNumber();
      //out.print(padWithTrailingZeros(e.getAccount().getNumber(), 10)
      out.print(TextUtil.padWithTrailingZeros(getAccount(e), 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces(e.getDocument(), 10)
              // La valeur 13 ne semble pas obligatoire. On peut étendre la taille du champ.
              //+ "#" + padWithTrailingSpaces(truncate(e.getLabel(), 13), 13)
              + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel() + getInvoiceNumber(e), 24), 24) // numéro de facture pour les echéances correspondant à une facture.
              + "#" + TextUtil.padWithLeadingZeros(nf.format(e.getAmount() / 100.0), 13)
              + "#" + cd
              + "#" + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
              + "#" + (char) 13);
    }
    if (total > 0) {
      out.print(
              TextUtil.padWithTrailingZeros(number, 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + TextUtil.padWithTrailingSpaces("CENTRALISE", 24)
              + "#" + TextUtil.padWithLeadingZeros(nf.format(total / 100.0), 13)
              + "#" + dc
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
    }
    out.close();
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> orderLines) throws IOException, SQLException, ModelException {
    //
    OrderLine e = null;
    int errors = 0;
    boolean m1 = false;
    boolean m2 = false;
    String message = "";
    StringBuilder logMessage = new StringBuilder();
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    String logpath = path+".log";
    PrintWriter out = new PrintWriter(new FileWriter(path));

    for (int i = 0, n = orderLines.size(); i < n ; i++) {
      e =  orderLines.elementAt(i);
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

      Account c = getAccount(p);
      String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
      String codeJournal = getCodeJournal(e.getAccount().getId());
      String f = (e.getInvoice() == null) ? "" : e.getInvoice();
      out.print(TextUtil.padWithTrailingZeros(c.getNumber(), 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces(e.getDocument(), 10)
              + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 24), 24)
              + "#" + TextUtil.padWithLeadingZeros(m, 13)
              + "#" + cd //Crédit
              + "#" + TextUtil.padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
              + "#" + (char) 13);

      out.print(
              TextUtil.padWithTrailingZeros(getAccount(e), 10) // compte client
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + TextUtil.padWithTrailingSpaces(f, 10)
              + "#" + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 24), 24)
              + "#" + TextUtil.padWithLeadingZeros(m, 13)
              + "#" + dc //Débit
              + "#" + TextUtil.padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
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
      MessagePopup.warning(null, err+message+l);
    }

    return errors;
  }

}
