/*
 * @(#)BranchView.java	2.6.a 14/09/12
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
import java.awt.GridBagLayout;
import net.algem.contact.Address;
import net.algem.contact.AddressView;
import net.algem.contact.CodePostalCtrl;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Bank branch view.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
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
  private int branchId;

  public BranchView() {

    no = new GemField(6);
    no.setEditable(false);
    no.setBackground(Color.lightGray);

    bankCode = new BankCodeField();
    bankName = new GemField(30);

    branchCode = new BranchCodeField();
    domiciliation = new GemField(24);

    addressView = new AddressView();

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel("id"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("code banque"), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("nom banque"), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("code guichet"), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("domiciliation"), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(bankCode, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(bankName, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(branchCode, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(domiciliation, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(addressView, 0, 5, 2, 1, GridBagHelper.BOTH, 1.0, 1.0);
  }

  @Override
  public void setBankCodeCtrl(BankCodeCtrl ctrl) {
    bankCode.addFocusListener(ctrl);
    bankCode.addActionListener(ctrl);
    branchCode.addFocusListener(ctrl);
    branchCode.addActionListener(ctrl);
    ctrl.setBranchView(this);
  }

  @Override
  public void setPostalCodeCtrl(CodePostalCtrl ctrl) {
    addressView.setCodePostalCtrl(ctrl);
  }

  public void setCodeBanque(String s) {
    bankCode.setText(s);
    //codeBanque.setValue(s);
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
    //return codeBanque.getValue().toString();
  }

  public void setCodeGuichet(String s) {
    branchCode.setText(s);
  }

  @Override
  public String getCodeGuichet() {
    return branchCode.getText();
  }

  @Override
  public void setAgenceBancaire(BankBranch a) {
    branchRef = a;
    if (branchRef != null) {
      branchId = a.getId();
      no.setText(String.valueOf(branchId));
      setBank(a.getBank());
      branchCode.setText(a.getCode());
      domiciliation.setText(a.getDomiciliation());
      Address adr = a.getAddress();
      if (adr != null) {
        addressView.set(adr);
      }
    }
  }

  @Override
  public BankBranch getAgenceBancaire() {
    BankBranch a = new BankBranch();

    //Banque b = new Bank(codeBanque.getText(), nomBanque.getText());
    Bank b = new Bank(getBankCode(), bankName.getText());
    a.setId(getId());
    a.setBank(b);
    a.setCode(branchCode.getText());
    a.setDomiciliation(domiciliation.getText());
    a.setAddress(addressView.get());

    return a;
  }

  @Override
  public Bank getBank() {
    //Banque b = new Bank(codeBanque.getText(), nomBanque.getText());
    Bank b = new Bank(getBankCode(), bankName.getText());
    return b;
  }

  @Override
  public void setBank(Bank b) {
    bankRef = b;
    if (bankRef != null) {
      bankName.setText(bankRef.getName());
      bankCode.setText(bankRef.getCode());
      //codeBanque.setValue(banqueRef.getCode());
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

  public void clearAgence() {
    domiciliation.setText("");
    addressView.clear();
  }

  public void clear() {
    no.setText("");
    bankCode.setText("");
    //codeBanque.setValue(null);
    bankName.setText("");
    branchCode.setText("");
    clearAgence();
  }
}
