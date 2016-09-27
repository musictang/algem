/*
 * @(#)FollowUpDlg.java	2.11.0 20/09/16
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.enrolment.FollowUp;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * Dialog for followup editing.
 * A Maj-click in the schedule detail view opens the dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.11.0
 */
public class FollowUpDlg
        implements ActionListener
{

  private JDialog dlg;
  private FollowUpView pv;
  private boolean validation;
  private GemButton btOk;
  private GemButton btCancel;

  public FollowUpDlg(GemDesktop desktop, ScheduleRangeObject range, String courseName, boolean collective) {
    String title = BundleUtil.getLabel("Follow.up.label") + " " + range.getMember();
    dlg = new JDialog(desktop.getFrame(), title, true);
    
    pv = new FollowUpView(courseName, range.getDate(), range.getStart(), range.getEnd());
    pv.set(range, collective);
    btOk = new GemButton(GemCommand.OK_CMD);
    btOk.setEnabled(!collective);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel btPanel = new GemPanel();
    btPanel.setLayout(new GridLayout(1, 2));
    btPanel.add(btCancel);
    btPanel.add(btOk);
  
    dlg.add(pv, BorderLayout.CENTER);
    dlg.add(btPanel, BorderLayout.SOUTH);
    dlg.setSize(400, 200);
    dlg.pack();
    dlg.setLocationRelativeTo(desktop.getFrame());
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
    Object src = evt.getSource();
    if (src == btOk) {
      validation = true;
    } else if (src == btCancel) {
      validation = false;
    }
    exit();
  }

  public void exit() {
    dlg.setVisible(false);
    dlg.dispose();
  }
}
