/*
 * @(#)HourEmployeeDlg.java	2.9.1 27/11/14
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.*;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.AccountingService;
import net.algem.accounting.OrderLineIO;
import net.algem.config.Param;
import net.algem.contact.EmployeeType;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.planning.*;
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
 * @version 2.9.1
 * @since 2.8.v 10/06/14
 */
public class HourEmployeeDlg
        extends TransferDlg
        implements ActionListener, PropertyChangeListener
{

  private final String total_day = MessageUtil.getMessage("total.day").toLowerCase();
  private final String total_month = MessageUtil.getMessage("total.month");
  private final String total_period = MessageUtil.getMessage("total.period");

  private HourEmployeeView view;
  private NumberFormat nf = AccountUtil.getDefaultNumberFormat();
  private AccountingService service;
  private int employee = 0;
  private ProgressMonitor pm;
  private SwingWorker teacherTask;
  private SwingWorker employeeTask;
  private DataCache dataCache;
  private String path;

  public HourEmployeeDlg(Frame parent, String file, DataCache dataCache) {
    super(
      parent,
      BundleUtil.getLabel("Menu.edition.export.label") + " " + BundleUtil.getLabel("Menu.employee.hour.label"),
      file,
      DataCache.getDataConnection()
      );
    this.dataCache = dataCache;
    service = new AccountingService(dc);
    init(file, dc);
  }

  public HourEmployeeDlg(Frame parent, String file, int idper, DataCache dataCache) {
    this(parent, file, dataCache);
    this.employee = idper;
  }

  @Override
  public void init(String file, DataConnection dc) {
    super.init(file, dc);
    setLayout(new BorderLayout());

    GemPanel p = new GemPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GemPanel header = new GemPanel();
    header.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(header);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new JLabel(BundleUtil.getLabel("Menu.file.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(filepath, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(chooser, 2, 0, 1, 1, GridBagHelper.WEST);
    view = new HourEmployeeView(dc, dataCache.getList(Model.School), dataCache.getList(Model.EmployeeType));

    p.add(header);
    p.add(view);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setLocation(200,100);
    pack();
  }

  @Override
  public void transfer() {
    DateFr start = view.getDateStart();
    DateFr end = view.getDateEnd();

    Param school = view.getSchool();

    boolean detail = view.withDetail();
    int type = view.getType();

    String lf = TextUtil.LINE_SEPARATOR;
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    PrintWriter out = null;
    boolean catchup = EmployeeType.TEACHER.ordinal() == type;
    if (catchup && !MessagePopup.confirm(this, MessageUtil.getMessage("export.hour.teacher.catchup.warning"))) {
      catchup = false;
    }
    try {
      path = filepath.getText();
      if (EmployeeType.TECHNICIAN.ordinal() == type && !path.endsWith(".csv")) {
        path = path.substring(0, path.lastIndexOf('.'));
        path = path.concat(".csv");
      }
      out = new PrintWriter(new FileWriter(path));
      if (EmployeeType.TEACHER.ordinal() == type) {
        out.println(MessageUtil.getMessage("export.hour.teacher.header", new Object[] {school.getValue(), start, end}) + lf);

        Vector<PlanningLib> plan = new Vector<PlanningLib>();
        if (employee > 0) {
          plan = service.getPlanningLib(start.toString(), end.toString(), school.getId(), employee, catchup);
        } else {
          plan = service.getPlanningLib(start.toString(), end.toString(), school.getId(), catchup);
        }

        pm = new ProgressMonitor(view, MessageUtil.getMessage("active.search.label"), "", 1, 100);
        pm.setMillisToDecideToPopup(10);
        
        // test csv detail
        ResultSet rs = service.getDetailTeacher(start.toString(), end.toString());
        teacherTask = new HourTeacherCSVTask(out, rs, detail);
        teacherTask.addPropertyChangeListener(this);
        teacherTask.execute();
        // DETAIL TXT DECOMMENTER
/*
        teacherTask = new HourTeacherTask(out, plan, detail);
        teacherTask.addPropertyChangeListener(this);
        teacherTask.execute();*/
      } else if (EmployeeType.TECHNICIAN.ordinal() == type) {
        ResultSet rs = service.getDetailTechnician(start.toString(), end.toString(), Schedule.TECH);
        pm = new ProgressMonitor(view, MessageUtil.getMessage("active.search.label"), "", 1, 100);
        pm.setMillisToDecideToPopup(10);
        employeeTask = new HourTechnicianTask(out, rs, detail);
        employeeTask.addPropertyChangeListener(this);
        employeeTask.execute();
      }
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
    if (teacherTask != null && teacherTask.isDone()) {
      teacherTask.cancel(true);
    } else if (employeeTask != null && employeeTask.isDone()) {
      employeeTask.cancel(true);
    } else if (pm.isCanceled()) {
      if (teacherTask != null) {
        teacherTask.cancel(true);
      } else if (employeeTask != null) {
        employeeTask.cancel(true);
      }
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
  class HourTeacherTask
          extends SwingWorker<Void, Void>
  {

    private PrintWriter out;
    private Vector<PlanningLib> plan;
    private boolean detail;
    private int k;

    public HourTeacherTask(PrintWriter out, Vector<PlanningLib> plan, boolean detail) {
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
      MessagePopup.information(view, MessageUtil.getMessage("export.hour.teacher.done.info",path));
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
          nmin = p.getStart().getLength(p.getEnd());
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
            nmin = pl.getStart().getLength(pl.getEnd());
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
  } // end HourTeacherTask class

class HourTechnicianTask
extends SwingWorker<Void, Void>
{

  private PrintWriter out;
  private ResultSet rs;
  private boolean detail;
  private String tm = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Month.label");
  private String tp = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Period.label");

  public HourTechnicianTask(PrintWriter out, ResultSet rs, boolean detail) {
    this.out = out;
    this.rs = rs;
    this.detail = detail;
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
        + ";" + BundleUtil.getLabel("Group.label")
        + ";" + BundleUtil.getLabel("Start.label")
        + ";" + BundleUtil.getLabel("End.label")
        + ";" + BundleUtil.getLabel("Duration.label"));

      while (rs.next()) {
        DateFr date = new DateFr(rs.getDate(1));
        int groupId = rs.getInt(2);
        Hour start = new Hour(rs.getString(3));
        Hour end = new Hour(rs.getString(4));
        Hour length = new Hour(rs.getString(5));
        int idper = rs.getInt(6);

        if (idper != prevIdper) {
          if (prevIdper > 0) {
            if (!detail) {
              out.println(df.format(prevDate.getDate()) + ";;;;" + nf.format(totalDay / 60.0));
            }
            out.println(tm + ";;;;" + nf.format(totalMonth / 60.0));
            totalPeriod += totalMonth;
            out.println(tp + ";;;;" + nf.format(totalPeriod / 60.0));
            out.println();
          }

          out.println(((Person) DataCache.findId(idper, Model.Person)).getFirstnameName() + ";;;;");
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
              out.println(df.format(prevDate.getDate()) + ";;;;" + nf.format(totalDay / 60.0));
            }
            totalDay = 0;
            prevDate = null;
            out.println(tm + ";;;;" + nf.format(totalMonth / 60.0));
          }

          out.println(dmf.format(date.getDate()).toUpperCase() + ";;;;");//out.println(cal.get(Calendar.MONTH));
          prevMonth = date.getMonth();
          totalPeriod += totalMonth;
          totalMonth = 0;
        }

        if (!date.equals(prevDate)) {
          if (!detail && prevDate != null) {
            out.println(df.format(prevDate.getDate()) + ";;;;" + nf.format(totalDay / 60.0));
            totalDay = 0;
          }
          prevDate = new DateFr(date);
        }

        totalMonth += length.toMinutes();
        totalDay += length.toMinutes();
        if (detail) {
          sb.append(date).append(';').append(groupId).append(';').append(start).append(';').append(end).append(';').append(length);
          out.println(sb.toString());
          sb.delete(0, sb.length());
        }
      } // end while

      if (!detail && prevDate != null) {
        out.println(df.format(prevDate.getDate()) + ";;;;" + nf.format(totalDay / 60.0));
      }
      out.println(tm + ";;;;" + nf.format(totalMonth / 60.0));
      totalPeriod += totalMonth;
      out.println(tp + ";;;;" + nf.format(totalPeriod / 60.0));
      out.close();
      return null;
    } // end doInBackground

    @Override
    public void done() {
      MessagePopup.information(view, MessageUtil.getMessage("export.hour.employee.done.info",path));
      setCursor(null); //turn off the wait cursor
      if (pm != null) {
        pm.close();
      }
    }

  } // end of HourTechnicianTask




class HourTeacherCSVTask
extends SwingWorker<Void, Void>
{

  private PrintWriter out;
  private ResultSet rs;
  private boolean detail;
  private String tm = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Month.label");
  private String tp = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Period.label");

  public HourTeacherCSVTask(PrintWriter out, ResultSet rs, boolean detail) {
    this.out = out;
    this.rs = rs;
    this.detail = detail;
  }


    @Override
    protected Void doInBackground() throws Exception {
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

      Format dmf = new SimpleDateFormat("MMM yyyy");
      Format df = new SimpleDateFormat("EEE dd-MM-yyyy");

      StringBuilder sb = new StringBuilder();
      //header
      out.println(BundleUtil.getLabel("Teacher.label")
        + ";" + BundleUtil.getLabel("Course.label")
        + ";" + BundleUtil.getLabel("Member.label")  
        + ";" + BundleUtil.getLabel("Day.label")  
        + ";" + BundleUtil.getLabel("Start.label")
        + ";" + BundleUtil.getLabel("End.label")
        + ";" + BundleUtil.getLabel("Duration.label"));

      while (rs.next()) {
        int idper = rs.getInt(1);
        int memberId = rs.getInt(2);
        String teacherName = rs.getString(3) + " " + rs.getString(4);
        String courseName = rs.getString(6);
        String memberName = rs.getString(7) + " " + rs.getString(8);
        DateFr date = new DateFr(rs.getDate(9));
        Hour start = new Hour(rs.getString(10));
        Hour end = new Hour(rs.getString(11));
        Hour duration = new Hour(rs.getString(12));
        int idcourse = rs.getInt(5);

        if (idper != prevIdper) {
          if (prevIdper > 0) {
            if (!detail) {
              out.println("test non détaillé");
//              out.println(";;;" + df.format(prevDate.getDate()) + ";;;" + nf.format(totalDay / 60.0));
            }
            out.println(tc + ";;;;;;" +  nf.format(totalCourse / 60.0));
            totalPeriod += totalMonth;
            out.println(tp + ";;;;;;" + nf.format(totalPeriod / 60.0));
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
            out.println(tc + ";;;;;;" +  nf.format(totalCourse / 60.0));
            totalCourse = 0;
            prevMember = memberId;
          }
        }

        if (idcourse != prevCourse) {
          if (prevCourse > 0 && totalCourse > 0) {
            out.println(tc + ";;;;;;" + nf.format(totalCourse / 60.0));
            totalCourse = 0;
          }
          prevCourse = idcourse;
        }
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

      if (!detail && prevDate != null) {
        out.println(";;;" + df.format(prevDate.getDate()) + ";;;" + nf.format(totalDay / 60.0));
//        out.println(df.format(prevDate.getDate()) + ";;;;;;" + nf.format(totalDay / 60.0));
      }
      out.println(tm + ";;;;;;" + nf.format(totalCourse / 60.0));
      totalPeriod += totalMonth;
      out.println(tp + ";;;;;;" + nf.format(totalPeriod / 60.0));
      out.close();
      return null;
    } // end doInBackground

    @Override
    public void done() {
      MessagePopup.information(view, MessageUtil.getMessage("export.hour.employee.done.info",path));
      setCursor(null); //turn off the wait cursor
      if (pm != null) {
        pm.close();
      }
    }

  } // end of HourTechnicianTask

}