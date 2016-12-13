/*
 * @(#) ExportOpenConcerto.java Algem 2.11.0 13/12/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import net.algem.util.TextUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.11.0 13/12/2016
 */
public class ExportOpenConcerto
        extends  CommunAccountExportService
{

  private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  private NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
  private static char cd = 'C';// credit
  private static char dc = 'D';//debit
  @Override
  public void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException {
    int total = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    OrderLine e = null;
    try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0, n = lines.size(); i < n ; i++) {
        e =  lines.elementAt(i);
        total += e.getAmount();
        //String f = (AccountUtil.isPersonalAccount(e.getAccount()) && e.getInvoice() != null) ? e.getInvoice() : e.getInvoiceNumber();
        //out.print(padWithTrailingZeros(e.getAccount().getNumber(), 10)
        
        sb.append(dateFormat.format(e.getDate().getDate()));
        sb.append(';').append(codeJournal);
        sb.append(';').append(getAccount(e));
        sb.append(';').append(e.getDocument());
        sb.append(';').append(e.getLabel()).append(' ').append(getInvoiceNumber(e));
        sb.append(';').append(0.0);
        sb.append(';').append(nf.format(e.getAmount() / 100.0));
        sb.append(';').append(e.getCostAccount().getNumber());
        out.println(sb.toString());
      }
      sb.delete(0, sb.length());
      if (total > 0) {
        sb.append(dateFormat.format(e.getDate().getDate()));
        sb.append(';').append(codeJournal);
        sb.append(';').append(number);
        sb.append(';').append(e.getDocument());
        sb.append(';').append("CENTRALISE");
        
        sb.append(';').append(nf.format(e.getAmount() / 100.0));
        sb.append(';').append(0.0);
        sb.append(';').append(e.getCostAccount().getNumber());
        out.println(sb.toString());
        out.print(sb.toString());
      }
    }
    
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException {
    return -1;
  }

}
