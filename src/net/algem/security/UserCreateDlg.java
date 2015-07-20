/*
 * @(#)UserCreateDlg.java	2.9.4.10 20/07/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.security;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.contact.Person;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * User creation and modification dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.10
 */
public class UserCreateDlg
        implements ActionListener
{

  private Person person;
  private JDialog dlg;
  private boolean validation;
  private GemButton btValidation;
  private GemButton btCancel;
  private UserView userView;

  public UserCreateDlg(Component c, String t, Person p) {
    person = p;

    dlg = new JDialog(PopupDlg.getTopFrame(c), true);

    userView = new UserView(person);

    btValidation = new GemButton(GemCommand.OK_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 2));
    buttons.add(btCancel);
    buttons.add(btValidation);

    Container ct = dlg.getContentPane();
    ct.setLayout(new BorderLayout());

    ct.add(userView, BorderLayout.CENTER);
    ct.add(buttons, BorderLayout.SOUTH);

    dlg.setTitle(BundleUtil.getLabel("Login.creation.label"));
    dlg.setSize(440, 180);
    dlg.setLocationRelativeTo(c);
  }

  public void display() {
    dlg.setVisible(true);
  }

  public void dispose() {
    dlg.dispose();
  }

  public boolean isEntryValid() {
    User u = getUser();
    if ("".equals(u.getLogin()) && "".equals(u.getPassword())) {
      return false;
    }
    return true;
  }

  /**
   * Checks status of validation command.
   *
   * @return validation status
   */
  public boolean isValidation() {
    return validation;
  }

  public void setUser(User u) {
    userView.set(u);
  }

  public User getUser() {
    return userView.get();
  }

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
