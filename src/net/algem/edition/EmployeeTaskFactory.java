/*
 * @(#)EmployeeTaskFactory.java 2.10.0 07/06/16
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.edition;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.accounting.AccountingServiceImpl;
import net.algem.course.Module;
import net.algem.course.ModulePresetDlg;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningLib;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;

/**
 * Class used to select a task for hours reporting.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.9.4.13 26/10/15
 */
public class EmployeeTaskFactory {

  private HourEmployeeDlg parent;
  private AccountingServiceImpl service;
  private DataCache dataCache;
  private ProgressMonitor monitor;
  private PrintWriter out;
  private DateFr start;
  private DateFr end;
  private int idper;
  private int school;
  private int estab;
  private boolean catchup;
  private boolean detail;
  private Map<String, Object> props;

  public EmployeeTaskFactory(HourEmployeeDlg parent, AccountingServiceImpl service, DataCache dataCache, ProgressMonitor pm, PrintWriter out) {
    this.parent = parent;
    this.service = service;
    this.dataCache = dataCache;
    this.monitor = pm;
    this.out = out;
  }

  public void setProperties(DateFr start, DateFr end, int idper, int school, int estab, boolean catchup, boolean detail) {
    this.start = start;
    this.end = end;
    this.idper = idper;
    this.school = school;
    this.estab = estab;
    this.catchup = catchup;
    this.detail = detail;
    props = new HashMap<>();
    props.put("start", start);
    props.put("end", end);
    props.put("idper", idper);
    props.put("school", school);
    props.put("estab", estab);
    props.put("catchup", catchup);
    props.put("detail", detail);
    props.put("path", parent.getPath());    //ERIC 2.17.3 24/02/2020 export excel
  }

  public Map<String, Object> getProps() {
    return props;
  }

  public SwingWorker<Void, Void> getTask(String cmd) throws SQLException {
    SwingWorker<Void, Void> task = null;
    ResultSet rs = null;
    switch (cmd) {
      case "DefaultSorting":
        Vector<PlanningLib> plan = new Vector<>();
        if (idper > 0) {
          plan = service.getPlanningLib(start.toString(), end.toString(), school, idper, catchup);
        } else {
          plan = service.getPlanningLib(start.toString(), end.toString(), school, catchup);
        }
        task = new HoursTeacherByEstabTask(parent, monitor, out, plan, detail);
        ((HoursTeacherByEstabTask) task).set(dataCache, service);
        break;
      case "DateSorting":
        rs = service.getReportByDate(start.toString(), end.toString(), catchup, idper, school, estab);
        task = new HoursTeacherByDateTask(parent, monitor, out, rs, detail);
        break;
      case "MemberSorting":
        task = new HoursTeacherByMemberTask(parent, monitor, out, detail);
        ResultSet rsInd = service.getReportByMember(start.toString(), end.toString(), catchup, false, idper, school, estab);
        ResultSet rsCo = service.getReportByMember(start.toString(), end.toString(), catchup, true, idper, school, estab);
        ((HoursTeacherByMemberTask) task).setIndividualRS(rsInd);
        ((HoursTeacherByMemberTask) task).setCollectiveRS(rsCo);
        break;
      case "ModuleSorting":
        ModulePresetDlg dlg = new ModulePresetDlg(parent, dataCache);
        dlg.initUI();
        if (dlg.isValidated()) {
          List<Module> modules = dlg.getSelectedModules();
          assert (modules.size() > 0);
          rs = service.getReportByModule(start.toString(), end.toString(), catchup, idper, school, estab, modules);
          task = new HoursTeacherByModuleTask(parent, monitor, out, rs, detail);
        }
        break;
      case "Technician":
        rs = service.getReportByEmployee(start.toString(), end.toString(), idper, Schedule.TECH);
        task = new HoursTechnicianTask(parent, monitor, out, rs, detail);
        break;
      case "Administrator":
        rs = service.getReportByEmployee(start.toString(), end.toString(), idper, Schedule.ADMINISTRATIVE);
        task = new HoursAdministrativeTask(parent, monitor, out, rs, detail);
        break;
      case "Custom":
        HoursTaskExecutor executor = HoursTaskFactory.getInstance();
        executor.setProperties(props);
        executor.setOut(out);
        task = new HoursTeacherCustomTask(parent, monitor, detail);
        ((HoursTeacherCustomTask) task).setExecutor(executor);
        break;
    }
    return task;
  }

}
