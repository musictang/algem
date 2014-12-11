/*
 * @(#)HourEmployeeDlg.java	2.9.1 08/12/14
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
import java.util.Vector;
import javax.swing.*;
import net.algem.accounting.AccountingService;
import net.algem.config.Param;
import net.algem.contact.EmployeeType;
import net.algem.planning.*;
import net.algem.util.*;
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

  static String[] SORTING_CMD = {"DefaultSorting", "DateSorting", "MemberSorting"};

  private HourEmployeeView view;
  private AccountingService service;
  private int employeeId = 0;
  private ProgressMonitor pm;
  private SwingWorker employeeTask;
  private DataCache dataCache;
  private String path;

  public HourEmployeeDlg(Frame parent, String file, DataCache dataCache) {
    super(parent, BundleUtil.getLabel("Menu.edition.export.label") + " " + BundleUtil.getLabel("Menu.employee.hour.label"), file, DataCache.getDataConnection()
    );
    this.dataCache = dataCache;
    service = new AccountingService(dc);
    init(file, dc);
  }

  public HourEmployeeDlg(Frame parent, String file, int idper, DataCache dataCache) {
    this(parent, file, dataCache);
    this.employeeId = idper;
  }

  @Override
  public void init(String file, DataConnection dc) {
    super.init(file, dc);
    setLayout(new BorderLayout());
    setPath(EmployeeType.TEACHER.ordinal(), SORTING_CMD[0]);
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
    view = new HourEmployeeView(this, dataCache.getList(Model.School), dataCache.getList(Model.EmployeeType));

    p.add(header);
    p.add(view);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setLocation(200, 100);
    pack();
  }

  String getPath() {
    return path;
  }

  void setPath(int type, String cmd) {
    path = filepath.getText();
    if (path == null || path.isEmpty()) {
      filepath.setText(System.getProperty("user.dir") + FileUtil.FILE_SEPARATOR + BundleUtil.getLabel("File.export.hours.name") + ".txt");
      path = filepath.getText();
    }
    int idx = path.lastIndexOf('.');
    if (EmployeeType.TECHNICIAN.ordinal() == type || (!SORTING_CMD[0].equals(cmd))) {
      if (idx >= 0) {
        path = path.substring(0, idx);
        path = path.concat(".csv");
        filepath.setText(path);
      }
    } else if (idx > 0) {
        path = path.substring(0, idx);
        path = path.concat(".txt");
        filepath.setText(path);
    }
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
      String sorting = view.getSorting();
      setPath(type, sorting);
      out = new PrintWriter(new FileWriter(path));
      if (EmployeeType.TEACHER.ordinal() == type) {
        out.println(MessageUtil.getMessage("export.hour.teacher.header", new Object[]{school.getValue(), start, end}) + lf);

        pm = new ProgressMonitor(view, MessageUtil.getMessage("active.search.label"), "", 1, 100);
        pm.setMillisToDecideToPopup(10);

        if (SORTING_CMD[0].equals(sorting)) {
          Vector<PlanningLib> plan = new Vector<PlanningLib>();
          if (employeeId > 0) {
            plan = service.getPlanningLib(start.toString(), end.toString(), school.getId(), employeeId, catchup);
          } else {
            plan = service.getPlanningLib(start.toString(), end.toString(), school.getId(), catchup);
          }
          employeeTask = new HoursTeacherByEstabTask(this, pm, out, plan, detail);
          ((HoursTeacherByEstabTask) employeeTask).set(dataCache, service);
        } else if (SORTING_CMD[1].equals(sorting)) {
          ResultSet rs = service.getDetailTeacherByDate(start.toString(), end.toString(), catchup, employeeId, school.getId());
          employeeTask = new HoursTeacherByDateTask(this, pm, out, rs, detail);
        }else if (SORTING_CMD[2].equals(sorting)) {
          employeeTask = new HoursTeacherByMemberTask1(this, pm, out, detail);
          ResultSet rsInd = service.getDetailIndTeacherByMember(start.toString(), end.toString(), catchup, employeeId, school.getId());
          ResultSet rsCo = service.getDetailCoTeacherByMember(start.toString(), end.toString(), catchup, employeeId, school.getId());
          ((HoursTeacherByMemberTask1) employeeTask).setIndividualRS(rsInd);
          ((HoursTeacherByMemberTask1) employeeTask).setCollectiveRS(rsCo);
        }
      } else if (EmployeeType.TECHNICIAN.ordinal() == type) {
        ResultSet rs = service.getDetailTechnician(start.toString(), end.toString(), Schedule.TECH);
        pm = new ProgressMonitor(view, MessageUtil.getMessage("active.search.label"), "", 1, 100);
        pm.setMillisToDecideToPopup(10);
        employeeTask = new HoursTechnicianTask(this, pm, out, rs, detail);
      }
      employeeTask.addPropertyChangeListener(this);
      employeeTask.execute();
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
    if (employeeTask != null && employeeTask.isDone()) {
      employeeTask.cancel(true);
    } else if (pm.isCanceled() && employeeTask != null) {
        employeeTask.cancel(true);
    } else if (event.getPropertyName().equals("progress")) {
      // get the % complete from the progress event
      // and set it on the progress monitor
      int pg = ((Integer) event.getNewValue()).intValue();
      pm.setProgress(pg);
    }
  }


}
