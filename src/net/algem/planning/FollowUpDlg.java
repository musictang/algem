/*
 * @(#)FollowUpDlg.java	2.6.a 21/09/12
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.course.Course;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Dialog for followup editing.
 * A (CTRL OR Maj) click in the schedule detail view opens the dialog
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 */
public class FollowUpDlg
        implements ActionListener
{

  private GemDesktop desktop;
  private Course course;
  private ScheduleRangeObject range;
  private JDialog dlg;
  private FollowUpView pv;
  private GemLabel title;
  private boolean validation;
  private GemButton btOk;
  private GemButton btCancel;

  public FollowUpDlg(GemDesktop _desktop, ScheduleRangeObject pl, String _cours) {
    desktop = _desktop;
    range = pl;

    dlg = new JDialog(desktop.getFrame(), true);
    title = new GemLabel("Suivi " + range.getMember());
    pv = new FollowUpView(_cours, range.getDate(), range.getStart(), range.getEnd());
    pv.setText(pl.getFollowUp());

    btOk = new GemButton(GemCommand.OK_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel btPanel = new GemPanel();
    btPanel.setLayout(new GridLayout(1, 2));
    btPanel.add(btCancel);
    btPanel.add(btOk);

    dlg.getContentPane().add(title, BorderLayout.NORTH);
    dlg.getContentPane().add(pv, BorderLayout.CENTER);
    dlg.getContentPane().add(btPanel, BorderLayout.SOUTH);
    dlg.setSize(400, 200);
    dlg.setLocationRelativeTo(desktop.getFrame());
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
