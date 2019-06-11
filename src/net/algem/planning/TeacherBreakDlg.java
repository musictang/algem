/*
 * @(#)TeacherBreakDlg.java 2.15.2 27/09/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Dialog for editing teacher breaks.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 1.0a 08/10/2001
 */
public class TeacherBreakDlg
        implements ActionListener {

  private GemDesktop desktop;
  private DataCache dataCache;
  private int teacher;
  private JDialog dlg;
  private TeacherBreakView pv;
  private boolean validation;
  private GemButton btValid;
  private GemButton btCancel;
  private PlanningService service;
  private String error;

  public <T extends Schedule> TeacherBreakDlg(GemDesktop desktop,  int idper) {
    this.desktop = desktop;

    dataCache = desktop.getDataCache();
    service = new PlanningService(DataCache.getDataConnection());
    teacher = idper;
    dlg = new JDialog(desktop.getFrame(), BundleUtil.getLabel("Teacher.break.tip"), true);
    pv = new TeacherBreakView(dataCache, service, teacher);

    btValid = new GemButton(GemCommand.VALIDATE_CMD);
    btValid.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttonPanel = new GemPanel();
    buttonPanel.setLayout(new GridLayout(1, 2));
    buttonPanel.add(btValid);
    buttonPanel.add(btCancel);

    dlg.getContentPane().add(pv, BorderLayout.CENTER);
    dlg.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    dlg.pack();
    dlg.setLocation(100, 100);
  }

  public <T extends Schedule> TeacherBreakDlg(GemDesktop desktop,  T plan) {
    this(desktop, plan.getIdPerson());
    set(plan);
  }

  private <T extends Schedule> void set(T plan) {
    if (plan instanceof CourseSchedule) {
      pv.set((CourseSchedule) plan);
      pv.lock(true);
    }
  }

  public void clear() {
    pv.clear();
  }

  public void entry() {
    dlg.setVisible(true);
  }

  public boolean isEntryValid() {
    if (pv.getHourEnd().before(pv.getHourStart())) {
      error = MessageUtil.getMessage("hour.range.error");
      return false;
    }
    if (pv.getDateEnd().before(pv.getDateStart())) {
      error = MessageUtil.getMessage("end.date.invalid.choice");
      return false;
    }
    return true;
  }

  public boolean isValidation() {
    return validation;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (GemCommand.VALIDATE_CMD.equals(cmd)) {
      validation = isEntryValid();
    } else if (GemCommand.CANCEL_CMD.equals(cmd)) {
      validation = false;
    }
    exit();
  }

  public void save() {
    Action a = new Action();
    a.setStartDate(pv.getDateStart());
    a.setEndDate(pv.getDateEnd());
    a.setStartTime(pv.getHourStart());
    a.setEndTime(pv.getHourEnd());
    a.setCourse(pv.getCourse());
    a.setRoom(pv.getRoom());
    a.setIdper(teacher);
    try {
      service.createBreak(a);
      desktop.postEvent(new ModifPlanEvent(this, pv.getDateStart(), pv.getDateEnd()));
    } catch (PlanningException ex) {
      MessagePopup.warning(pv, ex.getMessage());
    }
  }

  /**
   * Gets an error message.
   * @return a string
   */
  public String getError() {
    return error;
  }

  public void exit() {
    dlg.setVisible(false);
    dlg.dispose();
  }
}
