/*
 * @(#)HourTeacherDlg.java	2.7.a 08/01/13
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.AccountingService;
import net.algem.accounting.OrderLineIO;
import net.algem.config.Param;
import net.algem.course.Course;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningLib;
import net.algem.planning.ScheduleRange;
import net.algem.room.Establishment;
import net.algem.room.Room;
import net.algem.util.*;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Export hours of teacher activity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 14/12/1999
 */
public class HourTeacherDlg
        extends TransfertDlg
        implements ActionListener, PropertyChangeListener
{

  private final String total_day = MessageUtil.getMessage("total.day").toLowerCase();
  private final String total_month = MessageUtil.getMessage("total.month");
  private final String total_period = MessageUtil.getMessage("total.period");
  
  private HourTeacherView view;
  private NumberFormat nf = AccountUtil.getDefaultNumberFormat();
  private AccountingService service;
  private int teacher = 0;
  private ProgressMonitor pm;
  private Task task;
  private DataCache dataCache;

  public HourTeacherDlg(Frame _parent, String file, DataCache dataCache) {
    super(_parent, "Edition/Export Heure Prof", file, dataCache.getDataConnection());
    this.dataCache = dataCache;
    service = new AccountingService(dc);
    init(file, dc);
  }

  public HourTeacherDlg(Frame _parent, String file, int teacher, DataCache dataCache) {
    this(_parent, file, dataCache);
    this.teacher = teacher;
  }

  @Override
  public void init(String file, DataConnection dc) {
    super.init(file, dc);

    Container c = getContentPane();
    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

    GemPanel header = new GemPanel();
    header.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(header);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new JLabel(BundleUtil.getLabel("Menu.file.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(filepath, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(chooser, 2, 0, 1, 1, GridBagHelper.WEST);
    view = new HourTeacherView(dc);

    c.add(header);
    c.add(view);
    c.add(buttons);
    pack();
  }

  @Override
  public void transfert() {
    DateFr start = view.getDateStart();
    DateFr end = view.getDateEnd();

    Param school = view.getSchool();

    boolean detail = view.withDetail();
    String lf = FileUtil.LINE_SEPARATOR;
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    PrintWriter out = null;
    try {
      String f = filepath.getText();
      out = new PrintWriter(new FileWriter(f));
      out.println(MessageUtil.getMessage("export.hour.teacher.header", new Object[] {school.getValue(), start, end}) + lf);

      Vector<PlanningLib> plan = new Vector<PlanningLib>();
      if (teacher > 0) {
        plan = service.getPlanningLib(start.toString(), end.toString(), Integer.valueOf(school.getKey()), teacher);
      } else {
        plan = service.getPlanningLib(start.toString(), end.toString(), Integer.valueOf(school.getKey()));
      }

      pm = new ProgressMonitor(view, MessageUtil.getMessage("active.search.label"), "", 1, 100);
      pm.setMillisToDecideToPopup(10);

      task = new Task(out, plan, detail);
      task.addPropertyChangeListener(this);
      task.execute();
    } catch (IOException ex) {
      MessagePopup.warning(view, MessageUtil.getMessage("file.exception"));
      GemLogger.logException(ex);
    } catch (SQLException ex) {
      GemLogger.logException(MessageUtil.getMessage("export.exception"), ex, this);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    // if the operation is finished or has been canceled by
    // the user, take appropriate action
    if (pm.isCanceled() || task.isDone()) {
      task.cancel(true);
    } else if (event.getPropertyName().equals("progress")) {
      // get the % complete from the progress event
      // and set it on the progress monitor
      int pg = ((Integer) event.getNewValue()).intValue();
      pm.setProgress(pg);
    }
  }

  /**
   * Thread for file writing.
   */
  class Task
          extends SwingWorker<Void, Void>
  {

    private PrintWriter out;
    private Vector<PlanningLib> plan;
    private boolean detail;
    private int k;

    public Task(PrintWriter out, Vector<PlanningLib> plan, boolean detail) {
      this.out = out;
      this.plan = plan;
      this.detail = detail;
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

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
      MessagePopup.information(view, MessageUtil.getMessage("export.hour.teacher.done.info",filepath.getText()));
      setCursor(null); //turn off the wait cursor
      pm.close();
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
            out.println(" " + total_day + " : " + new Hour(totalDay).toString());
            totalMonth += totalDay;
            totalPeriod += totalDay;
            out.println(total_month + " : " + nf.format(totalMonth / 60.0) + " heures  ");
            out.print(total_period + " : " + nf.format(totalPeriod / 60.0) + " heures  ");
            if (detail) {
              out.print("L:" + nf.format(totalLeisure / 60.0));
              out.print(" P:" + nf.format(totalPro / 60.0));
              for (Map.Entry<Establishment, Integer> entry : totalEstab.entrySet()) {
                out.print(" " + entry.getKey().getName().charAt(0) + ":" + nf.format(entry.getValue() / 60.0));
              }
            }
            out.println();
          }
          //out.println("\n" + p.getTeacher());
          out.println(FileUtil.LINE_SEPARATOR + p.getTeacher()); // prénom et nom du prof
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
          out.println(" " + total_day + " : " + new Hour(totalDay).toString());// affichage du total pour le jour précédent
          totalMonth += totalDay;
          totalPeriod += totalDay;
          totalDay = 0;
          oldDay = p.getDay();
          // affichage du sous-total par mois
          if (p.getDay().getMonth() != oldMonth) {
            out.println(total_month + " : " + nf.format(totalMonth / 60.0) + " heures  ");
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
          nmin = p.getStart().getDuration(p.getEnd());
          totalDay += nmin;
          // Affichage détaillé
          if (detail) {
            if (plage.size() > 0) {
              boolean pro = false;
              for (int j = 0; j < plage.size(); j++) {
                ScheduleRange pl = plage.elementAt(j);
                if (OrderLineIO.isPro(pl.getMemberId(), dataCache)) { // TODO get schedule action status instead
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
            nmin = pl.getStart().getDuration(pl.getEnd());
            totalDay += nmin;
            if (detail) {
              if (OrderLineIO.isPro(pl.getMemberId(), dataCache)) {
                type = "P";
                totalPro += nmin;
              } else {
                type = "L";
                totalLeisure += nmin;
              }
            int tt = totalEstab.get(ee);
            totalEstab.put(ee, tt + nmin);
            out.print(" [" + ee.getName().charAt(0) + type + "] " + p.getStart() + " " + p.getEnd() + ",");
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
      out.println(total_month + " : " + nf.format(totalMonth / 60.0) + " heures  ");
      out.print(total_period + " : " + nf.format(totalPeriod / 60.0) + " heures  ");
      if (detail) {
        out.print("L:" + nf.format(totalLeisure / 60.0));
        out.print(" P:" + nf.format(totalPro / 60.0));
        for (Map.Entry<Establishment, Integer> entry : totalEstab.entrySet()) {
          out.print(" " + entry.getKey().getName().charAt(0) + ":" + nf.format(entry.getValue() / 60.0));
        }
      }
      out.println();
      out.close();
    } // end write method
  } // end Task class

}
