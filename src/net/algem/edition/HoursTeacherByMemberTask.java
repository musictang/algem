/*
 * @(#)HoursTeacherByMemberTask.java	2.9.4.13 27/10/15
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
import java.sql.SQLException;
import javax.swing.ProgressMonitor;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;

/**
 * Task executed when editing hours of teachers. Sorting is performed by students, courses and dates.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.1 03/12/14
 */
class HoursTeacherByMemberTask
          extends HoursTask
  {

    private PrintWriter out;
    private ResultSet rs1, rs2;

    public HoursTeacherByMemberTask(HourEmployeeDlg dlg, ProgressMonitor pm, PrintWriter out, boolean detail) {
      super(dlg, pm, detail);
      this.out = out;
    }

    /**
     * Gets the resultset corresponding to individual courses.
     * @param rs a resultset
     */
    void setIndividualRS(ResultSet rs) {
      rs1 = rs;
    }

    /**
     * Gets the resultset corresponding to collective courses.
     * @param rs a resultset
     */
    void setCollectiveRS(ResultSet rs) {
      rs2 = rs;
    }

    @Override
    protected Void doInBackground() throws Exception {
      printDetailIndividual();
      printDetailCollective();
      out.close();
      return null;
    }

    private void printDetailCollective() throws SQLException {
      String tc = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Course.label");
      int prevCourse = -1;
      int prevIdper = -1;
      int prevMonth = -1;
      int totalDay = 0;
      int totalCourse = 0;
      int totalMonth = 0;
      int totalPeriod = 0;
      DateFr prevDate = null;
      String courseName = "";
      String teacherName = "";

      StringBuilder sb = new StringBuilder();
      out.println(";;;;;;");
      out.println(";;;;;;");
      out.println(BundleUtil.getLabel("Month.schedule.collective.course.tab").toUpperCase() + ";;;;;;");
      out.println(";;;;;;");
      while (rs2.next()) {
        int idper = rs2.getInt(1);
        int idcourse = rs2.getInt(4);

        DateFr date = new DateFr(rs2.getDate(6));
        Hour start = new Hour(rs2.getString(7));
        Hour end = new Hour(rs2.getString(8));
        Hour duration = new Hour(rs2.getString(9));
        if (idper != prevIdper) {
          if (prevIdper > 0) {
            totalPeriod += totalMonth;
            if (detail) {
              out.println(tc + ";;;;;;" + getTotal(totalCourse));
              out.println(this.totalPeriodLabel +";;;;;;" + getTotal(totalPeriod));
            } else {
              out.println(teacherName + ";"+courseName+";;;;;" + getTotal(totalCourse));
              out.println(this.totalPeriodLabel +";;;;;;" + getTotal(totalPeriod));
            }
            out.println();
          }

          prevIdper = idper;
          prevDate = null;
          prevMonth = 0;
          totalDay = 0;
          totalMonth = 0;
          totalPeriod = 0;
          totalCourse = 0;
        }

        if (idcourse != prevCourse) {
          if (prevCourse > 0 && totalCourse > 0) {
            if (detail) {
              out.println(tc + ";;;;;;" + getTotal(totalCourse));
            } else {
              out.println(teacherName +";"+courseName+";;;;;" + getTotal(totalCourse));
            }
            totalCourse = 0;
          }
          prevCourse = idcourse;
        }
        teacherName = rs2.getString(2) + " " + rs2.getString(3);
        courseName = rs2.getString(5);
        totalMonth += duration.toMinutes();
        totalDay += duration.toMinutes();
        totalCourse += duration.toMinutes();
        if (detail) {
          sb.append(teacherName).append(';').append(courseName).append(';').append("").append(';')
                  .append(date).append(';').append(start).append(';').append(end).append(';').append(duration);
          out.println(sb.toString());
          sb.delete(0, sb.length());
        }
      } //end while
      totalPeriod += totalMonth;
      if (detail) {
        out.println(tc + ";;;;;;" + getTotal(totalCourse));
        out.println(this.totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
      } else {
        out.println(teacherName +";"+courseName+";;;;;" + getTotal(totalCourse));
        out.println(this.totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
      }
    }

    private void printDetailIndividual() throws SQLException {
      String tc = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Course.label");
      int prevMember = -1;
      int prevCourse = -1;
      int prevMonth = -1;
      int prevIdper = -1;
      int totalDay = 0;
      int totalMonth = 0;
      int totalCourse = 0;
      int totalPeriod = 0;
      DateFr prevDate = null;
      String courseName = "";
      String teacherName = "";
      String memberName = "";

      StringBuilder sb = new StringBuilder();
      //header
      out.println(BundleUtil.getLabel("Teacher.label")
              + ";" + BundleUtil.getLabel("Course.label")
              + ";" + BundleUtil.getLabel("Member.label")
              + ";" + BundleUtil.getLabel("Day.label")
              + ";" + BundleUtil.getLabel("Start.label")
              + ";" + BundleUtil.getLabel("End.label")
              + ";" + BundleUtil.getLabel("Duration.label"));

      while (rs1.next()) {
        int idper = rs1.getInt(1);
        int memberId = rs1.getInt(2);
        int idcourse = rs1.getInt(5);

        if (idper != prevIdper) {
          if (prevIdper > 0) {
            totalPeriod += totalMonth;
            if (detail) {
              out.println(tc + ";;;;;;" + getTotal(totalCourse));
              out.println(this.totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
            } else {
              out.println(teacherName + ";" + courseName + ";" + memberName + ";;;;" + getTotal(totalCourse));
              out.println(this.totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
            }
            out.println();
          }
          prevIdper = idper;
          prevMember = memberId;
          prevDate = null;
          prevMonth = 0;
          totalDay = 0;
          totalMonth = 0;
          totalPeriod = 0;
          totalCourse = 0;
        }

        if (memberId != prevMember) {
          if (prevMember > 0) {
            if (detail) {
              out.println(tc + ";;;;;;" + getTotal(totalCourse));
            } else {
              out.println(teacherName + ";"+courseName+";"+memberName+";;;;" + getTotal(totalCourse));
            }
            totalCourse = 0;
            prevMember = memberId;
          }
        }

        if (idcourse != prevCourse) {
          if (prevCourse > 0 && totalCourse > 0) {
            if (detail) {
              out.println(tc +";;;;;;" + getTotal(totalCourse));
            } else {
              out.println(teacherName+";"+courseName+";;;;;" + getTotal(totalCourse));
            }
            totalCourse = 0;
          }
          prevCourse = idcourse;
        }
        teacherName = rs1.getString(3) + " " + rs1.getString(4);
        courseName = rs1.getString(6);
        memberName = rs1.getString(7) + " " + rs1.getString(8);
        DateFr date = new DateFr(rs1.getDate(9));
        Hour start = new Hour(rs1.getString(10));
        Hour end = new Hour(rs1.getString(11));
        Hour duration = new Hour(rs1.getString(12));
        totalMonth += duration.toMinutes();
        totalDay += duration.toMinutes();
        totalCourse += duration.toMinutes();
        if (detail) {
          sb.append(teacherName).append(';').append(courseName).append(';').append(memberName).append(';')
                  .append(date).append(';').append(start).append(';').append(end).append(';').append(duration);
          out.println(sb.toString());
          sb.delete(0, sb.length());
        }
      } // end while
      totalPeriod += totalMonth;
      if (detail) {
        out.println(tc +";;;;;;" + getTotal(totalCourse));
        out.println(this.totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
      } else {
        out.println(teacherName +";"+courseName+";"+memberName+";;;;" + getTotal(totalCourse));
        out.println(this.totalPeriodLabel + ";;;;;;" + getTotal(totalPeriod));
      }
    }

  } // end of HourTechnicianTask

