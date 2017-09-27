/*
 * @(#)CalendarDlg.java	2.15.2 27/09/17
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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JDialog;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 */
public class CalendarDlg
  implements ActionListener {

  private Frame parent;
  private JDialog dlg;
  private boolean validation;
  private GemButton btCancel;
  private GemButton btValidate;
  private CalendarView calView;

  public CalendarDlg(Component c, String t) {
    parent = PopupDlg.getTopFrame(c);
    validation = false;

    calView = new CalendarView();
    calView.addActionListener(this);

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    btValidate = new GemButton(GemCommand.VALIDATE_CMD);
    btValidate.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 2));

    buttons.add(btValidate);
    buttons.add(btCancel);

    dlg = new JDialog(parent, true);
    dlg.add(calView, BorderLayout.CENTER);
    dlg.add(buttons, BorderLayout.SOUTH);

    dlg.setLocation(c.getLocation());
    dlg.setTitle(t);

    dlg.pack();
  }

  public void setDate(Date d) {
    calView.setDate(d);
  }

  public void open() {
    dlg.setVisible(true);
  }

  public boolean isValidate() {
    return validation;
  }

  public Date getDate() {
    return calView.getDate();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if ("click".equals(cmd)) {
      validation = true;
    } else if (GemCommand.VALIDATE_CMD.equals(cmd)) {
      validation = true;
    } else if ("date".equals(cmd)) {
      return;
    } else {
      validation = false;
    }
    close();
  }

  private void close() {
    dlg.setVisible(false);
    //dlg.dispose();
  }
}
