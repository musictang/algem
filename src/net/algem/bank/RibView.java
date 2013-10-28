/*
 * @(#)RibView.java	2.8.p 17/10/13
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import net.algem.config.ColorPrefs;
import net.algem.contact.Address;
import net.algem.contact.AddressView;
import net.algem.contact.CodePostalCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Rib tab.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 * @since 1.0a 07/07/1999
 */
public class RibView
        extends FileTab
        implements BankBranchView
{
 
  private int idper; // id dossier personne
  private GemField bankName;
  private GemField domiciliation;
  private BankCodeField bankCodeField;
  private BranchCodeField branchCodeField;
  private GemField accountField;
  private GemNumericField keyField;
  private JFormattedTextField ibanField;
  private GemField bicCodeField;
  private GemButton selectBranchBt;
  private AddressView addressView;
  private Bank bankRef = null;
  private BankBranch branchRef = null;
  private int branchId;
  private JLabel ribError;

  /**
   * Constructs a rib view.
   * @param desktop
   * @param id person's id
   */
  public RibView(GemDesktop desktop, int id) {
    super(desktop);
    this.idper = id;

    bankName = new GemField(32);
    domiciliation = new GemField(32);
    bankCodeField = new BankCodeField();
    branchCodeField = new BranchCodeField();
    accountField = new AccountCodeField(11);
    keyField = new GemNumericField(2);

    setRibEnabled(false);
    
    ibanField = new IbanField();
    ibanField.setToolTipText(BundleUtil.getLabel("Iban.tip"));

    bicCodeField = new BicCodeField();
    bicCodeField.setToolTipText(BundleUtil.getLabel("Bic.code.tip"));

    ribError = new JLabel();
    ribError.setForeground(Color.RED);
    addressView = new AddressView();
    addressView.setBorder(null);

    GemPanel bbanPanel = new GemPanel();
    bbanPanel.setLayout(new GridBagLayout());
    GridBagHelper gh = new GridBagHelper(bbanPanel);
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
    gh.add(bankCodeField, 0, 1, 1, 1, GridBagHelper.WEST);
    gh.add(branchCodeField, 1, 1, 1, 1, GridBagHelper.WEST);
    gh.add(accountField, 2, 1, 2, 1, GridBagHelper.WEST);
    gh.add(keyField, 4, 1, 1, 1, GridBagHelper.WEST);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;
    GemLabel bicLabel = new GemLabel(BundleUtil.getLabel("Bic.label"));
    bicLabel.setToolTipText(BundleUtil.getLabel("Bic.code.tip"));
    gb.add(bicLabel, 0, 1, 1, 1, GridBagHelper.NORTHWEST);
    
    GemLabel ibanLabel = new GemLabel(BundleUtil.getLabel("Iban.label"));
    ibanLabel.setToolTipText(BundleUtil.getLabel("Iban.tip"));
    gb.add(ibanLabel, 0, 2, 1, 1, GridBagHelper.WEST);
    
    gb.add(new GemLabel(BundleUtil.getLabel("Bic.code.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    GemLabel dom = new GemLabel("Dom.");
    dom.setToolTipText("Domiciliation");
    gb.add(dom, 0, 5, 1, 1, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Address.label")), 0, 6, 1, 1, GridBagHelper.NORTHWEST);
    
    GemPanel ibanPanel = new GemPanel(new BorderLayout());
    ibanPanel.add(ibanField, BorderLayout.WEST);
    ibanPanel.add(ribError, BorderLayout.EAST);
    
    GemPanel branchPanel = new GemPanel(new BorderLayout());
    branchPanel.add(bicCodeField, BorderLayout.WEST);
    selectBranchBt = new GemButton(ImageUtil.createImageIcon(ImageUtil.SEARCH_ICON));
    selectBranchBt.setBorder(null);
    selectBranchBt.setRolloverIcon(ImageUtil.createImageIcon("cherche_roll.png"));
    branchPanel.add(selectBranchBt, BorderLayout.EAST);
    
    gb.add(bbanPanel, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(ibanPanel, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(branchPanel, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(bankName, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(domiciliation, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(addressView, 1, 6, 1, 1, GridBagHelper.WEST);
  }

  private void setRibEnabled(boolean enabled) {
    if (enabled) {
      bankCodeField.setEditable(true);
      branchCodeField.setEditable(true);
      accountField.setEditable(true);
      keyField.setEditable(true);
    } else {
      bankCodeField.setEditable(false);
      branchCodeField.setEditable(false);
      accountField.setEditable(false);
      keyField.setEditable(false);
    }
  }

  void setRibFromIban() {
    String iban = getIban();
    if (iban == null || iban.isEmpty()) {
      return;
    }

    Rib r = new Rib(idper);
    r.setRib(iban);
    bankCodeField.setText(r.getEstablishment());
    branchCodeField.setText(r.getBranch());
    accountField.setText(r.getAccount());
    keyField.setText(r.getRibKey());

  }

  private boolean bbanIsEmpty() {
    return bankCodeField.getText().isEmpty()
            && branchCodeField.getText().isEmpty()
            && accountField.getText().isEmpty()
            && keyField.getText().isEmpty();
  }

  @Override
  public void setPostalCodeCtrl(CodePostalCtrl ctrl) {
    addressView.setCodePostalCtrl(ctrl);
  }

  @Override
  public void setBankCodeCtrl(BankCodeCtrl ctrl) {
    
    ibanField.addFocusListener(ctrl);
    ibanField.addActionListener(ctrl);
//    bankCodeField.addMouseListener(ctrl);
//    branchCodeField.addMouseListener(ctrl);
//    accountField.addMouseListener(ctrl);
//    keyField.addMouseListener(ctrl);
    
    bicCodeField.addActionListener(ctrl);
    bicCodeField.addFocusListener(ctrl);
    selectBranchBt.addActionListener(ctrl);
    ctrl.setBranchView(this);
  }

  void setAddress(Address a) {
    if (a != null) {
      addressView.set(a);
    }
  }

  Address getAddress() {
    return addressView.get();
  }

  public void setRib(Rib r) {
    if (r != null) {
      bankCodeField.setText(r.getEstablishment());
      branchCodeField.setText(r.getBranch());
      accountField.setText(r.getAccount());
      keyField.setText(r.getRibKey());
      branchId = r.getBranchId();
      ibanField.setValue(r.getIban() == null ? BankUtil.ribToIban(r.toString()) : r.getIban());
    }
  }

  public Rib getRib() {
    Rib r = new Rib(idper); // id initialise idper dans Rib

    r.setEstablishment(bankCodeField.getText());
    r.setBranch(branchCodeField.getText());
    r.setAccount(accountField.getText());
    r.setRibKey(keyField.getText());
    r.setBranchId(branchId);
    Object ibanValue = ibanField.getValue();
    r.setIban(ibanValue == null ? null : ibanValue.toString());

//    r.setBicCode(bicCodeField.getText());
    return r;
  }

  void markRib(boolean error) {
    if (error) {
      ribError.setText(MessageUtil.getMessage("rib.error"));
    } else {
      ribError.setText(null);
    }
  }
  
  void markIban(boolean ok) {
    if (ok) {
      ibanField.setBackground(Color.WHITE);
      ribError.setText(null);
    } else {
//      ibanField.requestFocusInWindow();
//      ibanField.setCaretPosition(ibanField.getText().length());
      ibanField.setBackground(ColorPrefs.ERROR_BG_COLOR);
      ribError.setText(MessageUtil.getMessage("rib.error"));
    }
  }
  
  @Override
  public void markBic(boolean ok) {
    bicCodeField.setBackground(ok ? Color.WHITE : ColorPrefs.ERROR_BG_COLOR);
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
  public Bank getBank() {
    if (bankRef != null) {
      return bankRef;
    }

    Bank b = new Bank();
    
    b.setCode(getBankCode());
    b.setName(bankName.getText());
    return b;
  }

  @Override
  public void setBank(Bank bank) {
    bankRef = bank;
    if (bankRef != null) {
//      if (!bankName.getText().equals(bankRef.getName())) {
//        clearBranch();
//      }
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

  void setBranchCode(String s) {
    branchCodeField.setText(s);
  }


  @Override
  public BankBranch getBankBranch() {

    BankBranch bb = new BankBranch();
    bb.setId(branchId);
    bb.setCode(getBranchCode());
    bb.setDomiciliation(domiciliation.getText());
    bb.setBank(getBank());
    bb.setAddress(getAddress());
    bb.setBicCode(getBicCode());
    return bb;
  }

  /**
   * Gets iban value.
   *
   * @return a string not formatted
   */
  String getIban() {
    if (ibanField == null) {
      return null;
    }
    try {
      ibanField.commitEdit();
    } catch (ParseException ex) {
      markIban(false);
      //return null;
    }

    return (ibanField.getValue() == null) ? null : ibanField.getValue().toString().toUpperCase();
  }

  /**
   * Gets the 5-characters (french) bank code from an iban number.
   * @return a string
   */
  @Override
  public String getBankCode() {
    String ibanCode = getIban();
    return (ibanCode == null || ibanCode.length() < 9) ? null : ibanCode.substring(4,9);
  }

  /**
   * Gets the 5-characters (french) branch code from an iban number.
   * @return a string
   */
  @Override
  public String getBranchCode() {
    String ibanCode = getIban();
    return (ibanCode == null || ibanCode.length() < 14) ? null : ibanCode.substring(9,14);
  }

  @Override
  public void setBankBranch(BankBranch g) {
    branchRef = g;
    if (branchRef != null) {
      branchId = branchRef.getId();
      setBank(branchRef.getBank());
      domiciliation.setText(branchRef.getDomiciliation());
      domiciliation.setEditable(false);
      domiciliation.setBackground(Color.LIGHT_GRAY);
      String bicCode = branchRef.getBicCode();

      bicCodeField.setText(bicCode);
      addressView.set(branchRef.getAddress());
      if (branchRef.getBank().isMulti()) {
        addressView.setEditable(true);
        addressView.setBgColor(Color.WHITE);
      } else {
        addressView.setEditable(false);
        addressView.setBgColor(Color.LIGHT_GRAY);
      }
    } else {
      domiciliation.setText(null);
      domiciliation.setEditable(true);
      domiciliation.setBackground(Color.WHITE);
      bicCodeField.setText(null);
      bicCodeField.setEditable(true);
      bicCodeField.setBackground(Color.WHITE);
//      if (bankRef == null) {
        bankName.setText(null);
        bankName.setBackground(Color.WHITE);
        bankName.setEditable(true);
//      }
      addressView.clear();
      addressView.setEditable(true);
      addressView.setBgColor(Color.WHITE);
      branchId = 0;
    }
  }

  public boolean isNewBank() {
    return bankRef == null;
  }

  public boolean isNewBranch() {
    return branchRef == null;
  }

  public void clear() {
    idper = 0;
    bankName.setText("");
    clearBban();
    ibanField.setValue(null);
    clearBranch();
  }

  private void clearBban() {
    bankCodeField.setText("");
    branchCodeField.setText("");
    accountField.setText("");
    keyField.setText("");    
  }

  private void clearBranch() {
    branchId = 0;
    domiciliation.setBackground(Color.white);
    domiciliation.setText(null);
    bicCodeField.setText(null);
    addressView.clear();
  }

  @Override
  public boolean isLoaded() {
    return idper != 0;
  }

  @Override
  public void load() {
  }

  @Override
  public String getBicCode() {
    return bicCodeField.getText().trim().toUpperCase();
  }
}
