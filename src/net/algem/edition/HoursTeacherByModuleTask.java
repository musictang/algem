
/*
 * @(#)HoursTeacherByModuleTask.java 2.9.4.0 23/10/2015
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
import java.sql.SQLException;
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
 * @version 2.9.4.0
 * @since 2.9.4.0 23/10/2015
 */
public class HoursTeacherByModuleTask 
extends HoursTask
  {

    private PrintWriter out;
    private ResultSet rs;

  public HoursTeacherByModuleTask(HourEmployeeDlg dlg, ProgressMonitor pm, PrintWriter out, ResultSet rs, boolean detail) {
    super(dlg, pm, detail);
      this.out = out;
      this.rs = rs;
  }

  @Override
  protected Void doInBackground() throws Exception {
    printDetail();
      out.close();
      return null;
  }
  
  private void printDetail() throws SQLException {
      String tc = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Course.label");
      String totalModuleLabel = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Module.label");
      int prevMonth = -1;
      int prevIdper = -1;
      int prevModule = -1;
      int totalDay = 0;
      int totalMonth = 0;
      int totalModule = 0;
      int totalPeriod = 0;
      DateFr prevDate = null;
      String teacherName = "";
      String courseName = "";
      String moduleName = "";
      out.println(getHeader());
      while (rs.next()) {
        DateFr date = new DateFr(rs.getDate(9));
        Hour start = new Hour(rs.getString(10));
        Hour end = new Hour(rs.getString(11));
        Hour length = new Hour(rs.getString(12));
        int idaction = rs.getInt(4);
        int idper = rs.getInt(1);
        int idmodule = rs.getInt(7); 
        int idcourse = rs.getInt(5);
        courseName = rs.getString(6);
        moduleName = rs.getString(8);
        
        StringBuilder sb = new StringBuilder();
        
        if (idmodule != prevModule) {
          if (prevModule > 0) {
            totalPeriod += totalMonth;
//            totalModule += totalDay;
            if (!detail) {
              out.println(teacherName + ";;;"+ prevDate + ";;;" + numberFormat.format(totalDay / 60.0));
              out.println(";COUCOU1;;" + (prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;" + numberFormat.format(totalMonth / 60.0));
              out.println(totalPeriodLabel + ";COUCOU1;;;;;" + numberFormat.format(totalPeriod / 60.0));
              out.println(totalModuleLabel + ";;;;;;" + numberFormat.format(totalModule / 60.0));
            } else {
              out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;;;;" + numberFormat.format(totalMonth / 60.0));
              out.println(totalPeriodLabel + ";;;;;;" + numberFormat.format(totalPeriod / 60.0));
              out.println(totalModuleLabel + ";;;;;;" + numberFormat.format(totalModule / 60.0));
            }
            out.println();
          }
          out.println(";;"+moduleName+";;;;");
          prevModule = idmodule;
          prevDate = null;
          prevMonth = 0;
          totalDay = 0;
          totalMonth = 0;
          totalPeriod = 0;
          totalModule = 0;
        }
        
        if (idper != prevIdper) {
          if (prevIdper > 0 && prevDate != null) {
            totalPeriod += totalMonth;
            if (!detail) {
              out.println(teacherName + ";;;"+ prevDate + ";;;" + numberFormat.format(totalDay / 60.0));
              out.println(";COUCOU2;;" + (prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;" + numberFormat.format(totalMonth / 60.0));
              out.println(totalPeriodLabel + ";COUCOU2;;;;;" + numberFormat.format(totalPeriod / 60.0));
            } else {
              out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;;;;" + numberFormat.format(totalMonth / 60.0));
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
        teacherName = rs.getString(2) + " " + rs.getString(3);

        if (date.getMonth() != prevMonth) {
          if (prevMonth > 0) {
            if (!detail) {
              out.println(teacherName + ";COUCOU3;;"+ prevDate + ";;;" + numberFormat.format(totalDay / 60.0));
              out.println(";COUCOU3;;" + (prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;" + numberFormat.format(totalMonth / 60.0));
            } else {
              out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) +";;;;;;" + numberFormat.format(totalMonth / 60.0));
            }
            totalDay = 0;
            prevDate = null;
          }
          prevMonth = date.getMonth();
          totalPeriod += totalMonth;
          totalMonth = 0;
        }

        if (!date.equals(prevDate)) {
          if (!detail) {
            out.println(teacherName + ";COUCOU5;;"+ prevDate + ";;;" + numberFormat.format(totalDay / 60.0));
            totalDay = 0;
          }
          prevDate = new DateFr(date);
        }

        totalMonth += length.toMinutes();
        totalDay += length.toMinutes();
        totalModule += length.toMinutes();
//        Course course = (Course) DataCache.findId(idcourse, Model.Course);
//        Action action = (Action) DataCache.findId(idaction, Model.Action);
        if (detail) {
          sb.append(teacherName).append(';')
                  .append(courseName).append(';')
                  .append(moduleName).append(';')
                  .append(date).append(';')
                  .append(start).append(';')
                  .append(end).append(';')
                  .append(length);
          out.println(sb.toString());
          sb.delete(0, sb.length());
        }
      } // end while
      totalPeriod += totalMonth;
      
      if (!detail) {
        out.println(teacherName + ";;;"+ prevDate + ";;;" + numberFormat.format(totalDay / 60.0));
        out.println(";COUCOU4;;" + (prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase()) + ";;;" + numberFormat.format(totalMonth / 60.0));
        out.println(totalPeriodLabel + ";COUCOU4;;;;;" + numberFormat.format(totalPeriod / 60.0));
        out.println(totalModuleLabel + ";;;;;;" + numberFormat.format(totalModule / 60.0));
      } else {
        out.println((prevDate == null ? "" : simpleDateFmt.format(prevDate.getDate()).toUpperCase())
          + ";;;;;;" + numberFormat.format(totalMonth / 60.0));
        out.println(totalPeriodLabel + ";;;;;;" + numberFormat.format(totalPeriod / 60.0));
        out.println(totalModuleLabel + ";;;;;;" + numberFormat.format(totalModule / 60.0));
      }
     // end doInBackground
  }
  
  private String getHeader() {
    StringBuilder sb = new StringBuilder();
    String[] cols = {
      BundleUtil.getLabel("Teacher.label"),";",
      BundleUtil.getLabel("Course.label"),";",
      BundleUtil.getLabel("Module.label"),";",
      BundleUtil.getLabel("Day.label"),";",
      BundleUtil.getLabel("Start.label"),";",
      BundleUtil.getLabel("End.label"),";",
      BundleUtil.getLabel("Duration.label")};
    for(String s : cols) {
      sb.append(s);
    }
    return sb.toString();
    
  }


}
