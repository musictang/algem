/*
 * @(#)CollectiveFollowUpDlg.java	2.6.a 21/09/12
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JDialog;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Dialog for pedagogic follow-up of collective courses.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.5.b
 */
public class CollectiveFollowUpDlg
        implements ActionListener
{

  private DataCache dc;
  private ScheduleObject sched;
  private JDialog dlg;
  private FollowUpView pv;
  private GemLabel title;
  private boolean validation;
  private GemButton btValid;
  private GemButton btCancel;
  private PlanningService service;

  public CollectiveFollowUpDlg(GemDesktop desktop, PlanningService service, ScheduleObject plan, String cours)
          throws SQLException {
    dc = desktop.getDataCache();
    this.service = service;
    this.sched = plan;

    dlg = new JDialog(desktop.getFrame(), true);
    title = new GemLabel("Suivi cours : " + cours);
    pv = new FollowUpView(this.sched.getDay(), this.sched.getStart(), this.sched.getEnd());

    if (this.sched.getNote() != 0) {
      pv.setText(service.getCollectiveFollowUp(plan.getNote()));
    }

    btValid = new GemButton(GemCommand.OK_CMD);
    btValid.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel btPanel = new GemPanel();
    btPanel.setLayout(new GridLayout(1, 2));
    btPanel.add(btCancel);
    btPanel.add(btValid);

    dlg.getContentPane().add(title, BorderLayout.NORTH);
    dlg.getContentPane().add(pv, BorderLayout.CENTER);
    dlg.getContentPane().add(btPanel, BorderLayout.SOUTH);
    dlg.setSize(400, 200);
    Point pt = desktop.getFrame().getLocation();
    pt.translate(200, 200);
    dlg.setLocation(pt);
  }

  public String getText() {
    return pv.getText();
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
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      try {
        if (sched.getNote() == 0) {
          service.createFollowUp(sched, pv.getText());
        } else {
          service.updateFollowUp(sched.getNote(), pv.getText());
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
