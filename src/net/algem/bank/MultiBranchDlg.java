/*
 * @(#)MultiBranchDlg.java	2.6.a 14/09/12
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
package net.algem.bank;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JDialog;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MultiBranchDlg
        implements ActionListener
{

  private JDialog dlg;
  private GemLabel title;
  private boolean validation = false;
  private boolean createBranch = false;
  private GemButton btValidation;
  private GemButton btCancel;
  private GemButton btCreation;
  private MultiBranchView gv;

  public MultiBranchDlg(Component c, String t, String banque, String guichet) {
    dlg = new JDialog(PopupDlg.getTopFrame(c), true);

    title = new GemLabel(t);
    validation = false;

    gv = new MultiBranchView(banque, guichet);

    btValidation = new GemButton(GemCommand.OK_CMD);
    btValidation.addActionListener(this);
    btCreation = new GemButton(GemCommand.CREATE_CMD);
    btCreation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btValidation);
    buttons.add(btCreation);
    buttons.add(btCancel);

    dlg.getContentPane().add("North", title);
    dlg.getContentPane().add("Center", gv);
    dlg.getContentPane().add("South", buttons);
    dlg.setSize(new Dimension(600, 400));
    dlg.setLocation(100, 100);
  }

  public void saisie() {
    dlg.setVisible(true);
  }

  public BankBranch getSelectedBranch() {
    return gv.getSelectedAgence();
  }

  public boolean isValidEntry() {
    return true;
  }

  public boolean isValid() {
    return validation;
  }

  public boolean isNewBranch() {
    return createBranch;
  }

  public void loadBranchs(Vector v) {
    gv.loadBranches(v);
  }

  public void setBankCode(String s) {
    gv.setBankCode(s);
  }

  public void setBranchCode(String s) {
    gv.setBranchCode(s);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      if (!isValidEntry()) {
        return;
      }
      validation = true;
    } else if (evt.getActionCommand().equals(GemCommand.CREATE_CMD)) {
      createBranch = true;
      validation = true;
    } else {
      validation = false;
    }
    dlg.setVisible(false);
    //dlg.dispose();
  }
}
