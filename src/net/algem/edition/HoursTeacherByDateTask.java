/*
 * @(#)HoursTeacherByDateTask.java	2.9.4.13 27/10/15
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.algem.edition;

import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.swing.ProgressMonitor;
import net.algem.course.Course;
import net.algem.planning.Action;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.1 05/12/14
 */
public class HoursTeacherByDateTask
extends HoursTask
  {

    private final PrintWriter out;
    private final ResultSet rs;

    public HoursTeacherByDateTask(HourEmployeeDlg dlg, ProgressMonitor pm, PrintWriter out, ResultSet rs, boolean detail) {
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
      String teacherName = "";
      String memberName = "";
      String courseName = "";
      //header
      StringBuilder sb = new StringBuilder(BundleUtil.getLabel("Teacher.label"));
      if (detail) {
        sb.append(";").append(BundleUtil.getLabel("Course.label")).append(";").append(BundleUtil.getLabel("Member.label"));
      }
      sb.append(";").append(BundleUtil.getLabel("Day.label"));
      if (detail) {
        sb.append(";").append(BundleUtil.getLabel("Start.label")).append(";").append(BundleUtil.getLabel("End.label"));
      }
      sb.append(";").append(BundleUtil.getLabel("Duration.label"));
      out.println(sb.toString());
      sb.delete(0, sb.length());

      while (rs.next()) {
        DateFr date = new DateFr(rs.getDate(9));
        Hour start = new Hour(rs.getString(10));
        Hour end = new Hour(rs.getString(11));
        Hour length = new Hour(rs.getString(12));
        int idaction = rs.getInt(13);
        int idper = rs.getInt(1);
        int memberId = rs.getInt(2);
        int idcourse = rs.getInt(5);

        courseName = rs.getString(6);
        memberName = rs.getString(7) + " " + rs.getString(8);

        if (idper != prevIdper) {
          if (prevIdper > 0) {
            totalPeriod += totalMonth;
            if (!detail) {
              out.println(teacherName + ";"+ prevDate + ";" + getTotal(totalDay));
              out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;" + getTotal(totalMonth));
              out.println(totalPeriodLabel + ";;" + numberFormat.format(totalPeriod / 60.0));
            } else {
              out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;;;;" + getTotal(totalMonth));
              out.println(totalPeriodLabel + ";;;;;;" + numberFormat.format(totalPeriod / 60.0));
            }
            out.println();
          }
          prevIdper = idper;
          prevDate = null;
          prevMonth = 0;
          totalDay = 0;
          totalMonth = 0;
          totalPeriod = 0;
        }
        teacherName = rs.getString(3) + " " + rs.getString(4);

        if (date.getMonth() != prevMonth) {
          if (prevMonth > 0) {
            if (!detail && prevDate != null) {
              out.println(teacherName + ";"+ prevDate + ";" + getTotal(totalDay));
              out.println(simpleDateFmt.format(prevDate.getDate()).toUpperCase() + " ;;" + getTotal(totalMonth));
            } else {
              out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) +";;;;;;" + getTotal(totalMonth));
            }
            totalDay = 0;
            prevDate = null;
          }
          prevMonth = date.getMonth();
          totalPeriod += totalMonth;
          totalMonth = 0;
        }

        if (!date.equals(prevDate)) {
          if (!detail && prevDate != null) {
            out.println(teacherName + ";"+ prevDate + ";" + getTotal(totalDay));
            totalDay = 0;
          }
          prevDate = new DateFr(date);
        }

        totalMonth += length.toMinutes();
        totalDay += length.toMinutes();
        Course course = (Course) DataCache.findId(idcourse, Model.Course);
        Action action = (Action) DataCache.findId(idaction, Model.Action);
        if (detail) {
          sb.append(teacherName).append(';')
                  .append(courseName).append(';')
                  .append((!course.isCollective() || action.getPlaces() <= 1) ? memberName : "").append(';')
                  .append(date).append(';')
                  .append(start).append(';')
                  .append(end).append(';')
                  .append(length);
          out.println(sb.toString());
          sb.delete(0, sb.length());
        }
      } // end while
      totalPeriod += totalMonth;
      if (!detail && prevDate != null) {
        out.println(teacherName + ";"+ prevDate + ";" + getTotal(totalDay));
        out.println(simpleDateFmt.format(prevDate.getDate()).toUpperCase() + ";;" + getTotal(totalMonth));
        out.println(totalPeriodLabel + ";;" + getTotal(totalPeriod));
      } else {
        out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;;;;" + getTotal(totalMonth));
        out.println(totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
      }
      out.close();
      return null;
    } // end doInBackground

  }