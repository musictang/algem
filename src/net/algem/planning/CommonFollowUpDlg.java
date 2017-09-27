/*
 * @(#)CommonFollowUpDlg.java	2.15.2 27/09/17
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JDialog;
import net.algem.enrolment.FollowUp;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Dialog for educational monitoring of courses.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.15.2
 * @since 2.9.4.12 17/09/15
 */
public class CommonFollowUpDlg
        implements ActionListener
{

  private DataCache dc;
  private ScheduleObject scheduleObject;
  private JDialog dlg;
  private FollowUpView pv;
  private GemLabel title;
  private boolean validation;
  private GemButton btValid;
  private GemButton btCancel;
  private PlanningService service;
  private boolean onlyCollective;

  public CommonFollowUpDlg(GemDesktop desktop, PlanningService service, ScheduleObject plan, String courseTitle, boolean collective)
          throws SQLException
  {

    dc = desktop.getDataCache();
    this.service = service;
    this.scheduleObject = plan;
    this.onlyCollective = collective;
    String title = BundleUtil.getLabel("Follow.up.label") + " : " + courseTitle;
    dlg = new JDialog(desktop.getFrame(), title, true);
    pv = new FollowUpView(this.scheduleObject.getDate(), this.scheduleObject.getStart(), this.scheduleObject.getEnd());

    pv.set(scheduleObject.getFollowUp(), collective);

    btValid = new GemButton(GemCommand.VALIDATE_CMD);
    btValid.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel btPanel = new GemPanel();
    btPanel.setLayout(new GridLayout(1, 2));

    btPanel.add(btValid);
    btPanel.add(btCancel);

    dlg.getContentPane().add(pv, BorderLayout.CENTER);
    dlg.getContentPane().add(btPanel, BorderLayout.SOUTH);
    dlg.setSize(400, 200);
    Point pt = desktop.getFrame().getLocation();
    pt.translate(200, 200);
    dlg.setLocation(pt);
  }

  public FollowUp getFollowUp() {
    return pv.get();
  }

  public void clear() {
    pv.clear();
  }

  public void entry() {
    dlg.setVisible(true);
  }

  public boolean isEntryValid() {
    return true;
  }

  public boolean isValidation() {
    return validation;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.VALIDATE_CMD)) {
      try {
        if (scheduleObject.getNote() == 0) {
          if (onlyCollective) {
            service.createCollectiveFollowUp(scheduleObject, pv.get());
          } else {
              if (((CourseSchedule) scheduleObject).getCourse().isCollective()) {
                service.createCollectiveFollowUp(scheduleObject, pv.get());
              } else {
                service.createIndividualFollowUp(scheduleObject.getId(), pv.get());
              }
          }
        } else {
          service.updateFollowUp(scheduleObject.getNote(), pv.get());
        }
      } catch (SQLException e) {
        GemLogger.logException(MessageUtil.getMessage("activity.thread.update.exception"), e, dlg);
      } catch (PlanningException e) {
        GemLogger.logException(MessageUtil.getMessage("activity.thread.create.exception"), e, dlg);
      }

      validation = true;
      exit();
    } else if (evt.getActionCommand().equals(GemCommand.CANCEL_CMD)) {
      validation = false;
      exit();
    }
  }

  public void exit() {
    dlg.setVisible(false);
    dlg.dispose();
  }
}
