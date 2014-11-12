/*
 * @(#)PopupDlg.java	2.8.w 23/07/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import net.algem.util.GemCommand;

/**
 * JDialog container.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public abstract class PopupDlg
        implements ActionListener
{

  protected Frame parent;
  protected JDialog dlg;
  protected String dlgTitle;
  protected boolean validation;
  protected GemButton btValid;
  protected GemButton btCancel;
  protected boolean modal = true;

  public PopupDlg() {
  }

  public PopupDlg(Component c, String t) {
    parent = getTopFrame(c);
    dlgTitle = t;
    validation = false;
  }

  public PopupDlg(Component c, String t, boolean modal) {
    parent = getTopFrame(c);
    dlgTitle = t;
    validation = false;
    this.modal = modal;
  }

  public void init() {
    if (parent == null) {
      dlg = new JDialog((JDialog) null, modal);
    }
     else {
      dlg = new JDialog(parent, modal);
    }
    btValid = new GemButton(GemCommand.OK_CMD);
    btValid.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 2));
    buttons.add(btCancel);
    buttons.add(btValid);

    GemPanel panel = new GemPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    panel.add(getMask(), BorderLayout.CENTER);

    dlg.add(panel, BorderLayout.CENTER);
    dlg.add(buttons, BorderLayout.SOUTH);
    dlg.setTitle(dlgTitle);
    dlg.pack();
    dlg.setLocation(100, 100);
  }

  public static Frame getTopFrame(Component cp) {
    while (cp.getParent() != null) {
      cp = cp.getParent();
    }
    if (cp instanceof Frame) {
      return (Frame) cp;
    }
    return null;
  }

  public void setSize(int w, int h) {
    dlg.setSize(w, h);
  }

  public void show() {
    if (dlg != null) {
      dlg.setVisible(true);
    }
  }

  public boolean isEntryValid() {
    return true;
  }

  public boolean isValidation() {
    return validation;
  }

  public void setValidation(boolean b) {
    validation = b;
  }

  public abstract GemPanel getMask();

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      if (!isEntryValid()) {
        return;
      }
      validation = true;
    } else {
      validation = false;
    }

    dlg.setVisible(false);
    //dlg.dispose();
  }
}
