/*
 * @(#)MultiBranchDlg.java	2.8.i 08/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
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

  public MultiBranchDlg(Component c, String t, String bankCode, String branchCode) {
    dlg = new JDialog(PopupDlg.getTopFrame(c), true);

    title = new GemLabel(t);
    validation = false;

    gv = new MultiBranchView(bankCode, branchCode);

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
    dlg.setSize(GemModule.L_SIZE);
    dlg.setLocation(100, 100);
  }

  public void show() {
    dlg.setVisible(true);
  }

  BankBranch getSelectedBranch() {
    return gv.getSelectedBranch();
  }

  private boolean isValidEntry() {
    return true;
  }

  boolean isValid() {
    return validation;
  }

  boolean isNewBranch() {
    return createBranch;
  }

  void loadBranchs(Vector<BankBranch> v) {
    gv.loadBranches(v);
  }

  void setBankCode(String s) {
    gv.setBankCode(s);
  }

  void setBranchCode(String s) {
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
