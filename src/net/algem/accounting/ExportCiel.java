/*
 * @(#)ExportCiel.java	2.8.x.3 24/09/14
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
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for exporting lines to CIEL accounting software.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.x.3
 * @since 2.8.r 13/12/13
 */
public class ExportCiel 
  extends  CommunAccountExportService
{

  private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
  private NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
  private static char cd = 'C';// credit
  private static char dc = 'D';//debit
  private static String version = "2003";

  public ExportCiel(DataConnection dc) {
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
    String mouvement = "1";
    for (int i = 0, n = orderLines.size(); i < n ; i++) {
      e =  orderLines.elementAt(i);
      total += e.getAmount();

      out.print(TextUtil.padWithLeadingSpaces(mouvement, 5) // n° mouvement
              + TextUtil.padWithTrailingSpaces(codeJournal,2) // code journal
              + dateFormat.format(new Date()) // date écriture
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + TextUtil.padWithTrailingSpaces(e.getDocument(), 12) // libellé pièce
              + TextUtil.padWithTrailingSpaces(e.getAccount().getNumber(), 11) // numéro dompte
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel() + getInvoiceNumber(e), 25), 25) // numéro de facture pour les echéances correspondant à une facture.
              + TextUtil.padWithLeadingSpaces(nf.format(e.getAmount() / 100.0), 13) // montant
              + cd // débit - crédit
              + TextUtil.padWithTrailingSpaces(e.getDocument(), 12) // numéro pointage
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getCostAccount().getNumber(), 6),6) // code analytique
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getAccount().getLabel(), 34), 34) // libellé compte
              + "O" // lettre O pour Euro = Oui
              + version
              + (char) 13);
    }
    if (total > 0) {
      out.print(TextUtil.padWithLeadingSpaces(mouvement, 5) // n° mouvement
              + TextUtil.padWithTrailingSpaces(codeJournal,2) // code journal
              + dateFormat.format(new Date()) // date écriture
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + TextUtil.padWithTrailingSpaces(null, 12) // libellé pièce
              + TextUtil.padWithTrailingSpaces(number, 11) // numéro dompte
              + TextUtil.padWithTrailingSpaces("CENTRALISE", 25) // numéro de facture pour les echéances correspondant à une facture.
              + TextUtil.padWithLeadingSpaces(nf.format(total / 100.0), 13) // montant
              + dc // débit
              + TextUtil.padWithTrailingSpaces(null, 12) // numéro pointage
              + TextUtil.padWithTrailingSpaces(null, 6) // code analytique
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(documentAccount.getLabel(), 34), 34) // libellé compte
              + "O" // lettre O pour Euro = Oui
              + version
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
    String logpath = path+".log";
    PrintWriter out = new PrintWriter(new FileWriter(path));

    int mouvement = 0;
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

      mouvement ++;
      Account c = getAccount(p);
      String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
      String codeJournal = getCodeJournal(e.getAccount().getId());
//      String f = (e.getInvoice() == null) ? "" : e.getInvoice();
      out.print(TextUtil.padWithLeadingSpaces(String.valueOf(mouvement), 5) // n° mouvement
              + TextUtil.padWithTrailingSpaces(codeJournal,2) // code journal
              + dateFormat.format(new Date()) // date écriture
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + TextUtil.padWithTrailingSpaces(e.getDocument(), 12) // libellé pièce
              + TextUtil.padWithTrailingSpaces(c.getNumber(), 11) // numéro dompte
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel() + getInvoiceNumber(e), 25), 25) // numéro de facture pour les echéances correspondant à une facture.
              + TextUtil.padWithLeadingSpaces(m, 13) // montant
              + cd // crédit
              + TextUtil.padWithTrailingSpaces(e.getDocument(), 12) // numéro pointage
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getCostAccount().getNumber(), 6),6) // code analytique
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getAccount().getLabel(), 34), 34) // libellé compte
              + "O" // lettre O pour Euro = Oui
              + version
              + (char) 13);

      String debit = getAccount(e);
      String debitLabel = debit.charAt(0) == 'C' ? "Compte client " + debit : e.getAccountLabel();
              out.print(TextUtil.padWithLeadingSpaces(String.valueOf(mouvement), 5) // n° mouvement
              + TextUtil.padWithTrailingSpaces(codeJournal,2) // code journal
              + dateFormat.format(new Date()) // date écriture
              + dateFormat.format(e.getDate().getDate()) // date échéance
              + TextUtil.padWithTrailingSpaces(null, 12) // libellé pièce
              + TextUtil.padWithTrailingSpaces(debit, 11) // numéro compte tiers
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(e.getLabel(), 25), 25) 
              + TextUtil.padWithLeadingSpaces(m, 13) // montant
              + dc // débit
              + TextUtil.padWithTrailingSpaces(null, 12) // numéro pointage
              + TextUtil.padWithTrailingSpaces(null, 6) // code analytique
              + TextUtil.padWithTrailingSpaces(TextUtil.truncate(debitLabel, 34), 34) // libellé compte tiers
              + "O" // lettre O pour Euro = Oui
              + version
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
      MessagePopup.warning(null, err+message+l);
    }
// 
    return errors;
  }
}
