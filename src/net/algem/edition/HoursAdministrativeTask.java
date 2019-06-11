/*
 * @(#)HoursAdministrativeTask.java 2.9.4.13 27/10/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.edition;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import javax.swing.ProgressMonitor;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.11 22/07/2015
 */
public class HoursAdministrativeTask    extends HoursTask
  {

    private PrintWriter out;
    private ResultSet rs;

    public HoursAdministrativeTask(HourEmployeeDlg dlg, ProgressMonitor pm, PrintWriter out, ResultSet rs, boolean detail) {
      super(dlg, pm, detail);
      this.out = out;
      this.rs = rs;
    }

    @Override
    protected Void doInBackground() throws Exception {
      int prevMonth = -1;
      int prevIdper = -1;
      int totalDay = 0;
      int totalMonth = 0;
      int totalPeriod = 0;
      DateFr prevDate = null;

      Format dmf = new SimpleDateFormat("MMM yyyy");
      Format df = new SimpleDateFormat("EEE dd-MM-yyyy");

      StringBuilder sb = new StringBuilder();
      //header
      out.println(BundleUtil.getLabel("Date.label")
              + ";" + BundleUtil.getLabel("Person.label")
              + ";" + BundleUtil.getLabel("Room.label")
              + ";" + BundleUtil.getLabel("Start.label")
              + ";" + BundleUtil.getLabel("End.label")
              + ";" + BundleUtil.getLabel("Duration.label"));

      while (rs.next()) {
        DateFr date = new DateFr(rs.getDate(1));
        int idper = rs.getInt(2);
        Hour start = new Hour(rs.getString(3));
        Hour end = new Hour(rs.getString(4));
        Hour length = new Hour(rs.getString(5));
        String room = rs.getString(6);

        if (idper != prevIdper) {
          if (prevIdper > 0) {
            if (!detail) {
              out.println(df.format(prevDate.getDate()) + ";;;;;" + getTotal(totalDay));
            }
            out.println(totalMonthLabel + ";;;;;" + getTotal(totalMonth));
            totalPeriod += totalMonth;
            out.println(totalPeriodLabel + ";;;;;" + getTotal(totalPeriod));
            out.println();
          }

          out.println(((Person) DataCache.findId(idper, Model.Person)).getFirstnameName() + ";;;;;");
          prevIdper = idper;
          prevDate = null;
          prevMonth = 0;
          totalDay = 0;
          totalMonth = 0;
          totalPeriod = 0;
        }

        if (date.getMonth() != prevMonth) {
          if (prevMonth > 0) {
            if (!detail && prevDate != null) {
              out.println(df.format(prevDate.getDate()) + ";;;;;" + getTotal(totalDay));
            }
            totalDay = 0;
            prevDate = null;
            out.println(totalMonthLabel + ";;;;;" + getTotal(totalMonth));
          }

          out.println(dmf.format(date.getDate()).toUpperCase() + ";;;;;");//out.println(cal.get(Calendar.MONTH));
          prevMonth = date.getMonth();
          totalPeriod += totalMonth;
          totalMonth = 0;
        }

        if (!date.equals(prevDate)) {
          if (!detail && prevDate != null) {
            out.println(df.format(prevDate.getDate()) + ";;;;;" + getTotal(totalDay));
            totalDay = 0;
          }
          prevDate = new DateFr(date);
        }

        totalMonth += length.toMinutes();
        totalDay += length.toMinutes();
        if (detail) {
          sb.append(date).append(';').append(idper).append(';').append(room).append(';').append(start).append(';').append(end).append(';').append(length);
          out.println(sb.toString());
          sb.delete(0, sb.length());
        }
      } // end while

      if (!detail && prevDate != null) {
        out.println(df.format(prevDate.getDate()) + ";;;;;" + getTotal(totalDay));
      }
      out.println(totalMonthLabel + ";;;;;" + getTotal(totalMonth));
      totalPeriod += totalMonth;
      out.println(totalPeriodLabel + ";;;;;" + getTotal(totalPeriod));
      out.close();
      return null;
    } // end doInBackground

  }
