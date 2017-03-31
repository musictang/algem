/*
 * @(#)BranchView.java	2.13.0 31/03/17
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import net.algem.config.ColorPrefs;
import net.algem.contact.Address;
import net.algem.contact.AddressView;
import net.algem.contact.CodePostalCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Bank branch view.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 */
public class BranchView
        extends GemBorderPanel
        implements BankBranchView
{

  private GemField no;
  private BankCodeField bankCode;
  private GemField bankName;
  private BranchCodeField branchCode;
  private GemField domiciliation;
  private AddressView addressView;
  private Bank bankRef = null;
  private BankBranch branchRef = null;
  private GemField bicCode;
  private int branchId;

  public BranchView() {

    no = new GemField();
    no.setEditable(false);
    no.setMinimumSize(new Dimension(60, no.getPreferredSize().height));
    no.setBackground(Color.lightGray);

    bankCode = new BankCodeField();
    bankCode.setMinimumSize(new Dimension(60, bankCode.getPreferredSize().height));
    
    bankName = new GemField(24);
    bankName.setMinimumSize(new Dimension(300, bankName.getPreferredSize().height));
    branchCode = new BranchCodeField();
    branchCode.setMinimumSize(new Dimension(60, branchCode.getPreferredSize().height));
    domiciliation = new GemField(24);
    domiciliation.setMinimumSize(new Dimension(300, domiciliation.getPreferredSize().height));
    bicCode = new BicCodeField();
    bicCode.setMinimumSize(new Dimension(200, bicCode.getPreferredSize().height));
    bicCode.setActionCommand(BankCodeCtrl.BIC_CMD);
    bicCode.setToolTipText(BundleUtil.getLabel("Bic.code.tip"));

    addressView = new AddressView();

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Id.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.code.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.name.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.branch.code.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("Domiciliation"), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bic.code.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(bankCode, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(bankName, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(branchCode, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(domiciliation, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(bicCode, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(addressView, 0, 6, 2, 1, GridBagHelper.BOTH, 1.0, 1.0);
  }

  @Override
  public void setBankCodeCtrl(BankCodeCtrl ctrl) {
    bankCode.addFocusListener(ctrl);
    bankCode.addActionListener(ctrl);
    branchCode.addFocusListener(ctrl);
    branchCode.addActionListener(ctrl);
    bicCode.addFocusListener(ctrl);
    bicCode.addActionListener(ctrl);
    ctrl.setBranchView(this);
  }

  @Override
  public void setPostalCodeCtrl(CodePostalCtrl ctrl) {
    addressView.setCodePostalCtrl(ctrl);
  }

  public void setCodeBanque(String s) {
    bankCode.setText(s);
  }

  @Override
  public void setBankName(String s) {
    bankName.setText(s);
  }

  @Override
  public String getBankName() {
    return bankName.getText();
  }

  public void setBranchCode(String s) {
    branchCode.setText(s);
  }

  @Override
  public void setBankBranch(BankBranch a) {
    branchRef = a;
    if (branchRef != null) {
      branchId = a.getId();
      no.setText(String.valueOf(branchId));
      setBank(a.getBank());
      branchCode.setText(a.getCode());
      
      domiciliation.setText(a.getDomiciliation());
      bicCode.setText(a.getBicCode());//XXX efface saisie si bic null
      
      Address adr = a.getAddress();
      if (adr != null) {
        addressView.set(adr);
      }
    }
  }

  @Override
  public BankBranch getBankBranch() {
    BankBranch a = new BankBranch();
    Bank b = new Bank(bankCode.getText(), bankName.getText());
    
    a.setId(getId());
    a.setCode(branchCode.getText());
    a.setDomiciliation(domiciliation.getText());
    a.setBicCode(bicCode.getText());
    a.setAddress(addressView.get());

    a.setBank(b);
    
    return a;
  }

  @Override
  public Bank getBank() {
    return new Bank(bankCode.getText(), bankName.getText());
  }

  @Override
  public void setBank(Bank b) {
    bankRef = b;
    if (bankRef != null) {
      bankName.setText(bankRef.getName());
      bankCode.setText(bankRef.getCode());
    }
  }

  boolean isNewBank() {
    return bankRef == null;
  }

  boolean isNewBranch() {
    return branchRef == null;
  }

  public int getId() {
    int id = 0;
    try {
      id = Integer.parseInt(no.getText());
    } catch (Exception e) {
    }

    return id;
  }

  private void clearBranch() {
    domiciliation.setText(null);
    bicCode.setText(null);
    addressView.clear();
  }

  public void clear() {
    no.setText(null);
    bankCode.setText(null);
    bankName.setText(null);
    branchCode.setText(null);
    clearBranch();
  }

  @Override
  public String getBankCode() {
    return bankCode.getText();
  }

  @Override
  public String getBranchCode() {
    return branchCode.getText();
  }

  @Override
  public String getBicCode() {
    return bicCode.getText().toUpperCase().trim();
  }

  @Override
  public void markBic(boolean ok) {
    bicCode.setBackground(ok ? Color.WHITE : ColorPrefs.ERROR_BG_COLOR);
  }
}
