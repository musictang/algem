/*
 * @(#)BicView.java	2.6.a 14/09/12
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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import net.algem.contact.Address;
import net.algem.contact.AddressView;
import net.algem.contact.CodePostalCtrl;
import net.algem.util.module.GemDesktop;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.*;

/**
 * Bic tab.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class BicView
        extends FileTab
        implements BankBranchView
{
  private int id; // id dossier personne
  private GemField bankName;
  private GemField domiciliation;
  private BankCodeField bankCode;
  private BranchCodeField branchCode;
  private GemField account;
  private GemNumericField key;
  private AddressView addressView;
  private Bank bankRef = null;
  private BankBranch branchRef = null;
  private int branchId;
  private JLabel bicError;

  public BicView(GemDesktop _desktop, int _id) {
    super(_desktop);
    id = _id;

    bankName = new GemField(32);
    domiciliation = new GemField(32);
    bankCode = new BankCodeField();
    branchCode = new BranchCodeField();
    account = new AccountCodeField(11);
    key = new GemNumericField(2);

    bicError = new JLabel();
    bicError.setForeground(Color.red);
    addressView = new AddressView();

    GemPanel p1 = new GemPanel();

    p1.setLayout(new GridBagLayout());
    GridBagHelper gh = new GridBagHelper(p1);
    Font labelFont = new Font("Dialog", Font.PLAIN, 10);
    GemLabel labels[] = new GemLabel[]{new GemLabel(BundleUtil.getLabel("Bank.label")),
                                     new GemLabel(BundleUtil.getLabel("Branch.label")),
                                     new GemLabel(BundleUtil.getLabel("Account.label")),
                                     new GemLabel(BundleUtil.getLabel("Key.label"))};
    for (int i = 0; i < labels.length; i++) {
      labels[i].setFont(labelFont);
    }

    gh.add(labels[0], 0, 0, 1, 1, GridBagHelper.WEST);
    gh.add(labels[1], 1, 0, 1, 1, GridBagHelper.WEST);
    gh.add(labels[2], 2, 0, 2, 1, GridBagHelper.WEST);
    gh.add(labels[3], 4, 0, 1, 1, GridBagHelper.WEST);
    gh.add(bankCode, 0, 1, 1, 1, GridBagHelper.WEST);
    gh.add(branchCode, 1, 1, 1, 1, GridBagHelper.WEST);
    gh.add(account, 2, 1, 2, 1, GridBagHelper.WEST);
    gh.add(key, 4, 1, 1, 1, GridBagHelper.WEST);
    gh.add(bicError,5, 1, 1,1,GridBagHelper.EAST);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Bic.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel("domiciliation"), 0, 3, 1, 2, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Address.label")), 0, 5, 2, 1, GridBagHelper.WEST);

    gb.add(p1, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(bankName, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(domiciliation, 1, 3, 1, 2, GridBagHelper.WEST);
    gb.add(addressView, 0, 6, 2, 2, GridBagHelper.BOTH, 1.0, 1.0);
  }

  @Override
  public void setPostalCodeCtrl(CodePostalCtrl ctrl) {
    addressView.setCodePostalCtrl(ctrl);
  }

  @Override
  public void setBankCodeCtrl(BankCodeCtrl ctrl) {
    bankCode.addFocusListener(ctrl);
    bankCode.addActionListener(ctrl);
    //codeGuichet.addFocusListener(ctrl);// redondant
    branchCode.addActionListener(ctrl);
    
    bankCode.addMouseListener(ctrl);
    branchCode.addMouseListener(ctrl);
    account.addMouseListener(ctrl);
    key.addMouseListener(ctrl);

    ctrl.setBranchView(this);
  }

  public void setAddress(Address a) {
    if (a != null) {
      addressView.set(a);
    }
  }

  public Address getAddress() {
    return addressView.get();
  }

  public void setBic(Bic r) {
    if (r != null) {
      bankCode.setText(r.getEstablishment());
      branchCode.setText(r.getBranch());
      account.setText(r.getAccount());
      key.setText(r.getBicKey());
      branchId = r.getBranchId();
    }
  }

  public Bic getBic() {
    Bic r = new Bic(id); // id initialise idper dans Bic

    r.setEstablishment(bankCode.getText());
    r.setBranch(branchCode.getText());
    r.setAccount(account.getText());
    r.setBicKey(key.getText());
    r.setBranchId(branchId);

    //05/09/2000 return (r.hasCorrectLength() ? r : null);
    return r;
  }

  public void markBic(boolean error) {
    if (error) {
      bicError.setText(MessageUtil.getMessage("rib.error"));
    } else {
      bicError.setText(null);
    }
  }

  @Override
  public void setBankName(String s) {
    bankName.setText(s);
  }

  @Override
  public String getBankName() {
    return bankName.getText();
  }

  @Override
  public String getBankCode() {
    return bankCode.getText();
  }

  @Override
  public Bank getBank() {
    if (bankRef != null) {
      return bankRef;
    }

    Bank bq = new Bank();
    bq.setCode(bankCode.getText());
    bq.setName(bankName.getText());
    return bq;
  }

  @Override
  public void setBank(Bank bque) {
    bankRef = bque;
    if (bankRef != null) {
      if (!bankName.getText().equals(bankRef.getName())) {
        clearBranch();
      }
      bankName.setText(bankRef.getName());
      bankName.setEditable(false);
      bankName.setBackground(Color.lightGray);
    } else {
      clearBranch();
      bankName.setText("");
      bankName.setEditable(true);
      bankName.setBackground(Color.white);
    }
  }

  public void setBranchCode(String s) {
    branchCode.setText(s);
  }

  @Override
  public String getCodeGuichet() {
    return branchCode.getText();
  }

  @Override
  public BankBranch getAgenceBancaire() {
    if (branchRef != null) {
      return branchRef;
    }

    BankBranch ab = new BankBranch();
    ab.setId(branchId);
    ab.setCode(branchCode.getText());
    ab.setDomiciliation(domiciliation.getText());
    ab.setBank(getBank());
    ab.setAddress(getAddress());

    return ab;
  }

  @Override
  public void setAgenceBancaire(BankBranch g) {
    branchRef = g;
    if (branchRef != null) {
      branchId = branchRef.getId();
      setBank(branchRef.getBank());
      domiciliation.setText(branchRef.getDomiciliation());
      domiciliation.setEditable(false);
      domiciliation.setBackground(Color.lightGray);
      addressView.set(branchRef.getAddress());
      if (branchRef.getBank().isMulti()) {
        addressView.setEditable(true);
        addressView.setBgColor(Color.white);
      } else {
        addressView.setEditable(false);
        addressView.setBgColor(Color.lightGray);
      }
    } else {
      domiciliation.setText("");
      domiciliation.setEditable(true);
      domiciliation.setBackground(Color.white);
      if (bankRef == null) {
        bankName.setBackground(Color.white);
        bankName.setEditable(true);
      }
      addressView.clear();
      addressView.setEditable(true);
      addressView.setBgColor(Color.white);
    }
  }

  public boolean isNewBank() {
    return bankRef == null;
  }

  public boolean isNewBranch() {
    return branchRef == null;
  }

  public void clear() {
    id = 0;
    bankName.setText("");
    clearBic();
    clearBranch();
  }

  public void clearBic() {
    bankCode.setText("");
    branchCode.setText("");
    account.setText("");
    key.setText("");
  }

  public void clearBranch() {
    domiciliation.setBackground(Color.white);
    domiciliation.setText("");
    addressView.clear();
  }

  @Override
  public boolean isLoaded() {
    return id != 0;
  }

  @Override
  public void load() {
  }
}
