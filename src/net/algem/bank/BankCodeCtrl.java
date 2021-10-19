/*
 * @(#)BankCodeCtrl.java	2.13.0 31/03/17
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
package net.algem.bank;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;

/**
 * Search control for bank and branch from their codes.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 1.0a 07/07/1999
 */
public class BankCodeCtrl
        implements ActionListener, FocusListener
{

  static final String IBAN_CMD = "Iban.action.command";
  static final String BIC_CMD = "Bic.action.command";
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
  BankCodeCtrl(boolean flag, DataConnection dc, BankBranchIO branchIO) {
    this(null, flag, dc, branchIO);
  }

  /**
   *
   * @param view
   */
  void setBranchView(BankBranchView view) {
    branchView = view;
  }

  private void searchBank(String bankCode) {
    this.bankCode = bankCode;
    branchView.setBank(BankIO.findCode(bankCode, dc));
  }

  /**
   *
   * @param branch
   * @param dlg open dialog
   */
  private void searchBranch(String branchCode, boolean dlg) {
    this.branchCode = branchCode;
    bankCode = branchView.getBankCode();
    Bank b = BankIO.findCode(bankCode, dc);

    List<BankBranch> v = bankBranchIO.findCode(bankCode, branchCode);
    if (v.isEmpty()) { // s'il n'existe pas d'agence
      branchView.setBankBranch(null);
      //return;//creation si le codeguichet n'existe pas, commenté par jm depuis version 2.0pc
    } else if (v.size() == 1 && !b.isMulti() && !dlg) {
        branchView.setBankBranch((BankBranch) v.get(0));
        return;
    }
    if (createFlag) {
      return;
    }
    openBranchDlg(v);
  }
  
  private void searchBranch(String branchCode) {
    searchBranch(branchCode, false);
  }

  private void openBranchDlg(List<BankBranch> v) {
    Component parent = (Component) branchView;
    MultiBranchDlg branchDlg = new MultiBranchDlg(parent, "code guichet", bankCode, branchCode);
    branchDlg.loadBranchs(v);
    branchDlg.show();
    if (!branchDlg.isValid()) {
      return;
    }
    if (!branchDlg.isNewBranch()) {
      BankBranch a = branchDlg.getSelectedBranch();
      branchView.setBank(a.getBank());
      branchView.setBankBranch(a);
      return;
    }
    BranchCreateDlg gCreateDlg = new BranchCreateDlg(parent, BundleUtil.getLabel("Bank.branch.creation.auth"), dc);
    gCreateDlg.setBankCode(bankCode);
    gCreateDlg.setBankName(branchView.getBankName());
    gCreateDlg.setBranchCode(branchCode);
    gCreateDlg.enter();
    if (!gCreateDlg.isValid()) {
      return;
    }
    BankBranch a = gCreateDlg.getBranch();
    branchView.setBank(a.getBank());
    branchView.setBankBranch(a);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    String cmd = evt.getActionCommand();

    if (src instanceof BankCodeField) {
      searchBank(branchView.getBankCode());
    } else if (IBAN_CMD.equals(cmd)) {
        RibView bv = (RibView) branchView;
        bv.markIban(BankUtil.isIbanOk(bv.getIban()));
    } else if (BIC_CMD.equals(cmd)) {
        String bCode = branchView.getBranchCode();// code agence
        if (bCode == null) {
          return;
        }
        searchBranch(bCode, true);
    }

  }

  @Override
  public void focusGained(FocusEvent evt) {
    if (evt.getSource() instanceof BicCodeField) {
        String bic = branchView.getBicCode();
        branchView.markBic(BankUtil.isBicOk(bic));
    } 
  }

  @Override
  public void focusLost(FocusEvent evt) {

    Object src = evt.getSource();

    if (src instanceof IbanField) {
      if (branchView instanceof RibView) {
        RibView bv = (RibView) branchView;
        bv.markIban(BankUtil.isIbanOk(bv.getIban()));
        bv.setRibFromIban();
        String branch = bv.getBranchCode();
        if (branch != null && !branch.equals(branchCode)) {
          searchBranch(branch);
        }
      }
    } else if (src instanceof BicCodeField) {
      String bic = branchView.getBicCode();
      branchView.markBic(BankUtil.isBicOk(bic));
    } else if (src instanceof BankCodeField) {
      String bCode = branchView.getBankCode();
      if (bCode != null && !bCode.equals(bankCode)) {
        searchBank(branchView.getBankCode());
      }
    } else if (src instanceof BranchCodeField) {
      String bbCode = branchView.getBranchCode();
      if (bbCode != null && !bbCode.equals(branchCode)) {
        searchBranch(bbCode);
      }
    }

  }

}
