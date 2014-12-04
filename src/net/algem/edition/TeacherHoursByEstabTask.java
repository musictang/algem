/*
 * @(#)TeacherHoursByEstabTask.java	2.9.1 03/12/14
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
package net.algem.edition;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.ProgressMonitor;
import net.algem.accounting.AccountingService;
import net.algem.course.Course;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningLib;
import net.algem.planning.ScheduleRange;
import net.algem.room.Establishment;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.ui.MessagePopup;

/**
 * Task executed when editing hours of teachers. Sorting is performed by establisment.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.9.1 03/12/14
 */
class TeacherHoursByEstabTask
          extends HoursTask
  {

    private PrintWriter out;
    private Vector<PlanningLib> plan;
    private int k;
    private DataCache dataCache;
    private AccountingService service;
    private ExportService exportService;

    public TeacherHoursByEstabTask(HourEmployeeDlg dlg, ProgressMonitor pm, PrintWriter out, Vector<PlanningLib> plan, boolean detail) {
      super(dlg, pm, detail);
      this.out = out;
      this.plan = plan;
    }

    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() throws SQLException {
      k = 0;
      write(out, plan, detail);
      return null;
    }
    
    void set(DataCache cache, AccountingService service) {
      this.dataCache = cache;
      this.service = service;
      exportService = new ExportService(DataCache.getDataConnection());
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
      MessagePopup.information(dlg, MessageUtil.getMessage("export.hour.teacher.done.info", dlg.getPath()));
      dlg.setCursor(null); //turn off the wait cursor
      if (pgMonitor != null) {
        pgMonitor.close();
      }
    }

    private void write(final PrintWriter out, final Vector<PlanningLib> plan, final boolean detail) throws SQLException {
      int oldTeacher = 0;
      DateFr oldDay = new DateFr("27-09-1900");
      int oldMonth = 0;
      int totalDay = 0;
      int totalMonth = 0;
      int totalPeriod = 0;
      int totalPro = 0;
      int totalLeisure = 0;

      GemList<Establishment> estab = dataCache.getList(Model.Establishment);
      HashMap<Establishment, Integer> totalEstab = new HashMap<Establishment, Integer>();
      for (Establishment e : estab.getData()) {
        totalEstab.put(e, 0);
      }

      int nmin = 0;

      for (int i = 0, size = plan.size(); i < size; i++) { // parcours des plannings
        PlanningLib p = plan.elementAt(i);
        if (p.getTeacherId() != oldTeacher) {//au changement de prof
          if (oldTeacher != 0) {
            out.println(" " + this.totalDayLabel + " : " + new Hour(totalDay).toString());
            totalMonth += totalDay;
            totalPeriod += totalDay;
            out.println(this.totalMonthLabel + " : " + numberFormat.format(totalMonth / 60.0) + " heures  ");
            out.print(this.totalPeriodLabel + " : " + numberFormat.format(totalPeriod / 60.0) + " heures  ");
            if (detail) {
              out.print("L:" + numberFormat.format(totalLeisure / 60.0));
              out.print(" P:" + numberFormat.format(totalPro / 60.0));
              for (Map.Entry<Establishment, Integer> entry : totalEstab.entrySet()) {
                out.print(" " + entry.getKey().getName().charAt(0) + ":" + numberFormat.format(entry.getValue() / 60.0));
              }
            }
            out.println();
          }
          //out.println("\n" + p.getTeacher());
          out.println(TextUtil.LINE_SEPARATOR + p.getTeacher()); // prénom et nom du prof
          out.println(p.getDay().toString()); // date du jour
          totalDay = 0;
          totalMonth = 0;
          totalPeriod = 0;
          totalPro = 0;
          totalLeisure = 0;
          for (Establishment e : estab.getData()) {
            totalEstab.put(e, 0);// reset
          }

          oldTeacher = p.getTeacherId();
          oldDay = p.getDay();
          oldMonth = oldDay.getMonth();
        }
        // changement de jour
        if (!p.getDay().equals(oldDay)) {
          out.println(" " + this.totalDayLabel + " : " + new Hour(totalDay).toString());// affichage du total pour le jour précédent
          totalMonth += totalDay;
          totalPeriod += totalDay;
          totalDay = 0;
          oldDay = p.getDay();
          // affichage du sous-total par mois
          if (p.getDay().getMonth() != oldMonth) {
            out.println(this.totalMonthLabel + " : " + numberFormat.format(totalMonth / 60.0) + " heures  ");
            totalMonth = 0;
            oldMonth = p.getDay().getMonth();
          }
          out.println(oldDay.toString()); // affichage du nouveau jour
        }

//        Course c = ((CourseIO) DataCache.getDao(Model.Course)).findId(p.getCourseId());
        Course c = (Course) DataCache.findId(p.getCourseId(), Model.Course);
//        Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(p.getRoomId());
        Room s = (Room) DataCache.findId(p.getRoomId(), Model.Room);
//        Establishment ee = dataCache.getEstabFromId(s.getEstab());
        Establishment ee = (Establishment) DataCache.findId(s.getEstab(), Model.Establishment);
//        Person e = ee.getPerson();
        String type = "L";

        // Pour chaque planning, recherche des plages
        Vector<ScheduleRange> plage = service.getCourseScheduleRange(p.getID());
        if (c.isCollective()) {
          nmin = p.getStart().getLength(p.getEnd());
          totalDay += nmin;
          // Affichage détaillé
          if (detail) {
            if (plage.size() > 0) {
              boolean pro = false;
              for (int j = 0; j < plage.size(); j++) {
                ScheduleRange pl = plage.elementAt(j);
                //if (OrderLineIO.isPro(pl.getMemberId(), dataCache)) { // TODO get schedule action status instead
                if (exportService.isPro(p.getAction(), pl.getMemberId())) {
                  pro = true;
                  break;
                }
              }
              if (pro) {
                type = "P";
                totalPro += nmin;
              } else {
                type = "L";
                totalLeisure += nmin;
              }
            } else { // si pas de plage
              type = "L";
              totalLeisure += nmin;
            }

            int tt = totalEstab.get(ee);
            totalEstab.put(ee, tt + nmin);
            out.print(" [" + ee.getName().charAt(0) + type + "] " + p.getStart() + " " + p.getEnd() + ",");
          }
        } else { // si cours individuel
          for (int j = 0; j < plage.size(); j++) {
            ScheduleRange pl = plage.elementAt(j);
            nmin = pl.getStart().getLength(pl.getEnd());
            totalDay += nmin;
            if (detail) {
//              if (OrderLineIO.isPro(pl.getMemberId(), dataCache)) {
              if (exportService.isPro(p.getAction(), pl.getMemberId())) {
                type = "P";
                totalPro += nmin;
              } else {
                type = "L";
                totalLeisure += nmin;
              }
              int tt = totalEstab.get(ee);
              totalEstab.put(ee, tt + nmin);
              out.print(" [" + ee.getName().charAt(0) + type + "] " + pl.getStart() + " " + pl.getEnd() + ",");
            } // end detail individuel
          } // end for plage.size()
        } // end si cours individuel
        /* MISE A JOUR PROGRESS MONITOR */
        /* ============================ */
        k = (int) ((i * 100) / size);
        setProgress(k + 1);
        /* ============================ */
      } // end parcours des plannings for (int i=0; i < plan.size(); i++)
      out.println(" total jour : " + new Hour(totalDay).toString());
      totalMonth += totalDay;
      totalPeriod += totalDay;
      out.println(this.totalMonthLabel + " : " + numberFormat.format(totalMonth / 60.0) + " heures  ");
      out.print(this.totalPeriodLabel + " : " + numberFormat.format(totalPeriod / 60.0) + " heures  ");
      if (detail) {
        out.print("L:" + numberFormat.format(totalLeisure / 60.0));
        out.print(" P:" + numberFormat.format(totalPro / 60.0));
        for (Map.Entry<Establishment, Integer> entry : totalEstab.entrySet()) {
          out.print(" " + entry.getKey().getName().charAt(0) + ":" + numberFormat.format(entry.getValue() / 60.0));
        }
      }
      out.println();
      out.close();
    } // end write method
  } // end HourTeacherTask class
