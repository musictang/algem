/*
 * @(#)BankCodeCtrl.java	2.6.a 14/09/12
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
import java.awt.event.*;
import java.util.Vector;
import javax.swing.JOptionPane;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;

/**
 * Search control for bank and branch from their codes.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class BankCodeCtrl
        extends MouseAdapter
        implements ActionListener, FocusListener
{

  private DataConnection dc;
  private BankBranchView branchView;
  private String bankCode;
  private String branchCode;
  private boolean createFlag = false;
  private BankBranchIO bankBranchIO;

  /**
   *
   * @param branchView
   * @param flag
   * @param dc
   */
  public BankCodeCtrl(BankBranchView branchView, boolean flag, DataConnection dc, BankBranchIO branchIO) {
    this.dc = dc;
    this.branchView = branchView;
    createFlag = flag;
    bankBranchIO = branchIO;
  }

  /**
   *
   * @param dc
   */
  public BankCodeCtrl(DataConnection dc, BankBranchIO branchIO) {
    this(null, false, dc, branchIO);
  }

  /**
   *
   * @param flag
   * @param dc
   */
  public BankCodeCtrl(boolean flag, DataConnection dc, BankBranchIO branchIO) {
    this(null, flag, dc, branchIO);
  }

  /**
   *
   * @param view
   */
  void setBranchView(BankBranchView view) {
    branchView = view;
  }

  private void searchBank(BankCodeField bank) {
    bankCode = bank.getText();
    branchView.setBank(BankIO.findCode(bankCode, dc));
  }

  /**
   *
   * @param branch
   */
  private void searchBranch(BranchCodeField branch) {
    bankCode = branchView.getBankCode();
    Bank b = BankIO.findCode(bankCode, dc);
    branchCode = branch.getText();
    Vector<BankBranch> v = bankBranchIO.findCode(bankCode, branchCode);
    if (v.isEmpty()) // s'il n'existe pas d'agence
    {
      branchView.setAgenceBancaire(null);
      //return;//XXX creation si le codeguichet n'existe pas, commenté par jm depuis version 2.0pc
    } else if (v.size() == 1 && !b.isMulti()) {
      //if (flagCreate) // commenté par jm depuis version 2.0pc
      //{
      JOptionPane.showMessageDialog(null,
              MessageUtil.getMessage("bank.branch.creation.error"),
              "Erreur création guichet",
              JOptionPane.ERROR_MESSAGE);
      branchView.setAgenceBancaire((BankBranch) v.elementAt(0));
      return;
    }
    if (createFlag) {
      return;
    }
    Component parent = (Component) branchView;
    MultiBranchDlg branchDlg = new MultiBranchDlg(parent, "code guichet", bankCode, branchCode);
    branchDlg.loadBranchs(v);
    branchDlg.saisie();
    if (!branchDlg.isValid()) {
      return;
    }
    if (!branchDlg.isNewBranch()) {
      BankBranch a = branchDlg.getSelectedBranch();
      branchView.setBank(a.getBank());
      branchView.setAgenceBancaire(a);
      return;
    }
    BranchCreateDlg gCreateDlg = new BranchCreateDlg(parent, "nouvelle adresse guichet", dc);
    gCreateDlg.setBankCode(bankCode);
    gCreateDlg.setBankName(branchView.getBankName());
    gCreateDlg.setBranchCode(branchCode);
    gCreateDlg.enter();
    if (!gCreateDlg.isValid()) {
      return;
    }
    BankBranch a = gCreateDlg.getBranch();
    branchView.setBank(a.getBank());
    branchView.setAgenceBancaire(a);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() instanceof BankCodeField) {
      searchBank((BankCodeField) evt.getSource());
    } else if (evt.getSource() instanceof BranchCodeField) {
      searchBranch((BranchCodeField) evt.getSource());
    }
  }

  @Override
  public void focusGained(FocusEvent evt) {
  }

  @Override
  public void focusLost(FocusEvent evt) {
    Object src = evt.getSource();
    if (src instanceof BankCodeField) {
      BankCodeField bank = (BankCodeField) evt.getSource();
      if (bankCode != null && bankCode.equals(bank.getText())) {
        return;
      }
      searchBank(bank);
    } else if (src instanceof BranchCodeField) {
      BranchCodeField branch = (BranchCodeField) evt.getSource();
      if (branchCode != null && branchCode.equals(branch.getText())) {
        return;
      }
      searchBranch(branch);
    }
    /* else if (src instanceof ChampsNumerique) { Bic r =
     * ((BicView)vueAgence).getBic(); if (r != null && !r.toString().isEmpty())
     * { if (!r.isValid()) { //MessagePopup.warning(null,
     * MessageUtil.getMessage("invalid.rib", new Object[] {r.toString()}));
     * ((BicView)vueAgence).markBic(true); } else
     * ((BicView)vueAgence).markBic(false); }
    } */
  }

  @Override
  public void mouseExited(MouseEvent e) {
    Bic r = ((BicView) branchView).getBic();
    if (r != null && !r.toString().isEmpty()) {
      if (!r.isValid()) {
        ((BicView) branchView).markBic(true);
      } else {
        ((BicView) branchView).markBic(false);
      }
    }
  }
}
