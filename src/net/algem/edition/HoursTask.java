/*
 * @(#)HoursTask.java	2.10.0 07/06/16
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

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.accounting.AccountUtil;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.MessagePopup;

/**
 * Abstract class used to execute tasks when editing hours of employees.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.9.1 03/12/14
 */
public abstract class HoursTask
  extends SwingWorker<Void, Void> {

  protected String totalDayLabel = MessageUtil.getMessage("total.day").toLowerCase();
  protected String totalMonthLabel = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Month.label");
  protected String totalPeriodLabel = BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Period.label");
  protected Format simpleDateFmt = new SimpleDateFormat("MMM yyyy");
  protected Format fullDateFormat = new SimpleDateFormat("EEE dd-MM-yyyy");
  protected NumberFormat numberFormat = AccountUtil.getDefaultNumberFormat();

  protected HourEmployeeDlg dlg;
  protected ProgressMonitor pgMonitor;
  protected boolean detail;

  public HoursTask() {
  }

  /**
   *
   * @param dlg parent dialog
   * @param monitor progress monitor instance
   * @param detail full report
   */
  public HoursTask(HourEmployeeDlg dlg, ProgressMonitor monitor, boolean detail) {
    this.dlg = dlg;
    this.pgMonitor = monitor;
    this.detail = detail;
  }

  @Override
  public void done() {
    MessagePopup.information(dlg, MessageUtil.getMessage("export.hour.teacher.done.info", dlg.getPath()));
    dlg.setCursor(null); //turn off the wait cursor
    if (pgMonitor != null) {
      pgMonitor.close();
    }
  }

  /**
   * Gets a string representing the total time spent on the month.
   *
   * @param totalmin total length in minutes
   * @return a string decimal-formatted
   */
  protected String getTotal(int totalmin) {
    return numberFormat.format(totalmin / 60.0);
  }

  public void setStep(int progress) {
    setProgress(progress);
  }

}
