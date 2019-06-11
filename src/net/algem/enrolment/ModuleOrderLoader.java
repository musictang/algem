/*
 * @(#)ModuleOrderLoader.java	2.10.0 17/05/16
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
package net.algem.enrolment;

import net.algem.util.model.AsyncLoader;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.ui.ProgressMonitorHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.9.4.13 06/11/15
 */
public class ModuleOrderLoader
  implements AsyncLoader {

  private final ExtendeModuleOrderListCtrl listCtrl;
  private final EnrolmentService service;
  private final Date start;
  private final Date end;

  /**
   *
   * @param parent controller
   * @param service enrolment service
   * @param start start of period
   * @param end end of period
   */
  public ModuleOrderLoader(ExtendeModuleOrderListCtrl parent, EnrolmentService service, Date start, Date end) {
    this.listCtrl = parent;
    this.service = service;
    this.start = start;
    this.end = end;
  }

  @Override
  public void load() {
    final ProgressMonitor monitor = new ProgressMonitor(listCtrl, BundleUtil.getLabel("Loading.label"), "", 1, 100);

    monitor.setProgress(0);
    monitor.setMillisToDecideToPopup(10);
    try {
      List<ExtendedModuleOrder> orders = service.getExtendedModuleList(start, end);
      SwingWorker<Void, String> task = new ModuleOrderTask(orders, monitor);
      ProgressMonitorHandler progressManager = new ProgressMonitorHandler(monitor, task);
      task.addPropertyChangeListener(progressManager);
      task.execute();
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
  }

  private class ModuleOrderTask extends SwingWorker<Void, String> {

    private final List<ExtendedModuleOrder> orders;
    private final ProgressMonitor monitor;

    public ModuleOrderTask(List<ExtendedModuleOrder> orders, ProgressMonitor monitor) {
      this.orders = orders;
      this.monitor = monitor;
    }

    @Override
    protected Void doInBackground() throws Exception {
      int i = 0;
      int size = orders.size();
      for (ExtendedModuleOrder em : orders) {
        if (isCancelled()) {
          break;
        }
        em.setCompleted(service.getCompletedTime(em.getIdper(), em.getId(), start, end));
        setProgress(++i * 100 / size);
        publish(i  + "/" + size);// + " : " + em.getTitle());
      }

      return null;
    }

    @Override
    public void process(List<String> data) {
      for (String n : data) {
        monitor.setNote(n);
      }
    }

    @Override
    public void done() {
      super.done();
      listCtrl.load(orders);
    }

  }

}
