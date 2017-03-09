/*
 * @(#) ExportOpenConcerto.java Algem 2.12.0 08/03/17
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
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.11.4 13/12/2016
 */
public class ExportOpenConcerto
  extends CommunAccountExportService {

  private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  private final NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);

  public ExportOpenConcerto(DataConnection dc) {
    dbx = dc;
    journalService = new JournalAccountService(dc);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
  }

  @Override
  public void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException {
    int total = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    OrderLine e = null;
     if (path.endsWith(".txt")) {
      path = path.replace(".txt", ".csv");
    }
    try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0, n = lines.size(); i < n; i++) {
        e = lines.elementAt(i);
        total += e.getAmount();
        sb.append(dateFormat.format(e.getDate().getDate()));
        sb.append(';').append(codeJournal);
        sb.append(';').append(getAccount(e));
        sb.append(';').append(e.getDocument());
        sb.append(';').append(e.getLabel()).append(' ').append(getInvoiceNumber(e));
        sb.append(';').append(nf.format(0.0));
        sb.append(';').append(nf.format(e.getAmount() / 100.0));
        Account a = e.getCostAccount();
        sb.append(';').append(
          (a == null || a.getNumber() == null || "null".equals(a.getNumber()))
            ? ""
            : a.getNumber());
        out.println(sb.toString());
        sb.delete(0, sb.length());
      }
      assert(e != null);
      if (total > 0) {
        sb.append(dateFormat.format(e.getDate().getDate()));
        sb.append(';').append(codeJournal);
        sb.append(';').append(number);
        sb.append(';').append(e.getDocument());
        sb.append(';').append("CENTRALISE");

        sb.append(';').append(nf.format(total / 100.0));
        sb.append(';').append(nf.format(0.0));
        sb.append(';').append(e.getCostAccount().getNumber());
        out.println(sb.toString());
      }
    }

  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException {
    OrderLine e = null;
    int errors = 0;
    StringBuilder logMessage = new StringBuilder();
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    boolean m1 = false;
    boolean m2 = false;
    if (path.endsWith(".txt")) {
      path = path.replace(".txt", ".csv");
    }
    String logpath = path + ".log";

    try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
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

        Account c = getAccount(p);
        String total = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
        String codeJournal = getCodeJournal(e.getAccount().getId());
        String f = (e.getInvoice() == null) ? "" : e.getInvoice();

        sb.append(dateFormat.format(e.getDate().getDate()));
        sb.append(';').append(codeJournal);
        sb.append(';').append(c.getNumber());
        sb.append(';').append(e.getDocument());
        sb.append(';').append(e.getLabel()).append(' ').append(getInvoiceNumber(e));
        if (e.getAmount() < 0) {
          sb.append(';').append(total);
          sb.append(';').append(nf.format(0.0));
        } else {
          sb.append(';').append(nf.format(0.0));
          sb.append(';').append(total);
        }
        sb.append(';').append(e.getCostAccount().getNumber());
        out.println(sb.toString());
        sb.delete(0, sb.length());

        sb.append(dateFormat.format(e.getDate().getDate()));
        sb.append(';').append(codeJournal);
        sb.append(';').append(getAccount(e));
        sb.append(';').append(e.getDocument());
        //TODO maybe remove " or ' from label
        sb.append(';').append(e.getLabel()).append(' ').append(getInvoiceNumber(e));
        //+ "#" + (e.getAmount() < 0 ? cd : dc) // cd Crédit
        if (e.getAmount() < 0) {
          sb.append(';').append(nf.format(0.0));
          sb.append(';').append(total);
        } else {
          sb.append(';').append(total);
          sb.append(';').append(nf.format(0.0));
        }
        sb.append(';').append(e.getCostAccount().getNumber());
        out.println(sb.toString());
        sb.delete(0, sb.length());
      }
    }

    if (logMessage.length() > 0) {
      PrintWriter log = new PrintWriter(new FileWriter(logpath));
      log.println(logMessage.toString());
      log.close();
    }

    String errorsMessage = null;
    if (errors > 0) {
      if (m1) {
        errorsMessage += MessageUtil.getMessage("personal.account.export.warning");
      }
      if (m2) {
        errorsMessage += MessageUtil.getMessage("no.revenue.matching.warning");
      }
      String err = MessageUtil.getMessage("error.count.warning", errors);
      String log = MessageUtil.getMessage("see.log.file", path);
      MessagePopup.warning(null, err + errorsMessage + log);
    }

    return errors;
  }

}
