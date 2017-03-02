/*
 * @(#)ConflictListDlg.java	2.12.0 01/03/17
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.PopupDlg;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class ConflictListDlg
        implements ActionListener
{

  private JDialog dlg;
  private GemLabel title;
  private GemButton btClose;
  private ConflictListView listView;
  private String info;
  private PlanningService service;

  /**
   *
   * @param c parent
   * @param t label
   * @param info additional information
   */
  public ConflictListDlg(Component c, String t, String info, PlanningService service) {
    this(c, t, false, service);
    this.info = info;
  }

  public ConflictListDlg(Component c, String t, PlanningService service) {
    this(c, t, true, service);
  }

  public ConflictListDlg(Component c, String t, boolean modal, PlanningService service) {
    this.service = service;
    this.title = new GemLabel(t);

    this.dlg = new JDialog(PopupDlg.getTopFrame(c), modal);

    btClose = new GemButton(GemCommand.OK_CMD);
    btClose.addActionListener(this);

    listView = new ConflictListView(service);

    dlg.add(title, BorderLayout.NORTH);
    dlg.add(listView, BorderLayout.CENTER);
    dlg.add(btClose, BorderLayout.SOUTH);
    dlg.setSize(600, 300);
    dlg.setLocation(100, 100);
  }

  public void show() {
    listView.setStatus(info);
    dlg.setVisible(true);
  }

  public void setSize(int w, int h) {
    dlg.setSize(w, h);
  }

  public void addConflict(ScheduleTestConflict p) {
    listView.addConflict(p);
  }


  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      dlg.setVisible(false);
      //dlg.dispose();
    }
  }
}
