/*
 * @(#)HourEmployeeDlg.java	2.10.0 09/06/2016
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.swing.*;
import net.algem.accounting.AccountingService;
import net.algem.config.Param;
import net.algem.contact.EmployeeType;
import net.algem.contact.Person;
import net.algem.planning.*;
import net.algem.room.Establishment;
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
 * @version 2.10.0
 * @since 2.8.v 10/06/14
 */
public class HourEmployeeDlg
        extends TransferDlg
        implements ActionListener, PropertyChangeListener
{

  static String[] SORTING_CMD = {"DefaultSorting", "DateSorting", "MemberSorting", "ModuleSorting", "Custom"};

  private HourEmployeeView view;
  private AccountingService service;
  private int employeeId = 0;
  private ProgressMonitor pm;
  private SwingWorker employeeTask;
  private DataCache dataCache;
  private String path;
  private GemList<Establishment> allEstabList;

  public HourEmployeeDlg(Frame parent, DataCache dataCache) {
    super(parent, BundleUtil.getLabel("Menu.edition.export.label") + " " + BundleUtil.getLabel("Menu.employee.hour.label"), DataCache.getDataConnection());
    this.dataCache = dataCache;
    service = new AccountingService(dc);
  }

  public HourEmployeeDlg(Frame parent, int idper, DataCache dataCache) {
    this(parent, dataCache);
    this.employeeId = idper;
  }

  @Override
  public void init(String fileName, DataConnection dc) {
    super.init(fileName, dc);
    setLayout(new BorderLayout());
    setPath(EmployeeType.TEACHER.ordinal(), SORTING_CMD[0]);
    GemPanel p = new GemPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GemPanel header = new GemPanel();
    header.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(header);
    gb.add(new JLabel(BundleUtil.getLabel("Menu.file.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(filepath, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(chooser, 2, 0, 1, 1, GridBagHelper.WEST);

    allEstabList = dataCache.getList(Model.Establishment);
    allEstabList.addElement(new Establishment(new Person(0, BundleUtil.getLabel("All.label"))));
    view = new HourEmployeeView(this, dataCache.getList(Model.School), dataCache.getList(Model.EmployeeType), allEstabList);

    p.add(header);
    p.add(view);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setLocation(200, 100);
    setSize(480, 440);
    pack();
    setVisible(true);
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
    int estab = view.getEstab();

    String lf = TextUtil.LINE_SEPARATOR;
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    PrintWriter out = null;
    try {
      String sorting = view.getSorting();
      setPath(type, sorting);
      out = new PrintWriter(new File(path), "UTF-16LE"); // this is the best solution
      pm = new ProgressMonitor(view, MessageUtil.getMessage("active.search.label"), "", 1, 100);
      pm.setMillisToDecideToPopup(10);

      EmployeeTaskFactory factory = new EmployeeTaskFactory(this, service, dataCache, pm, out);
      String cmd = null;
      if (EmployeeType.TEACHER.ordinal() == type) {
        out.println(MessageUtil.getMessage("export.hour.teacher.header", new Object[]{school.getValue(), start, end}) + lf);
        cmd = sorting;
      } else if (EmployeeType.TECHNICIAN.ordinal() == type) {
        cmd = "Technician";
      } else if (EmployeeType.ADMINISTRATOR.ordinal() == type) {
        cmd = "Administrator";
      }
      boolean catchup = EmployeeType.TEACHER.ordinal() == type && !"Custom".equals(cmd);
      if (catchup && !MessagePopup.confirm(this, MessageUtil.getMessage("export.hour.teacher.catchup.warning"))) {
        catchup = false;
      }
      factory.setProperties(start, end, employeeId, school.getId(), estab, catchup, detail);
      employeeTask = factory.getTask(cmd);

      if (employeeTask != null) {
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

  @Override
  public void close() {
    allEstabList.removeElement((Establishment) allEstabList.getItem(0));
    super.close();
  }

}
