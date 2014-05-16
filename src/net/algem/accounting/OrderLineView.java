/*
 * @(#)OrderLineView.java	2.8.t 15/05/14
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.*;
import net.algem.config.*;
import net.algem.planning.DateFrField;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Single order line edition dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 1.0a 18/07/1999
 */
public class OrderLineView
        extends JDialog
        implements ActionListener
{

  private GemNumericField payer;
  private GemNumericField member;
  private GemNumericField group;
  private DateFrField date;
  private GemField label;

  private JFormattedTextField amount;
  private JComboBox modeOfPayment;
  private GemField document;
  private ParamChoice schoolChoice;
  private JCheckBox cbPaid;

  private GemChoice account;
  private ParamChoice costAccount;
  private GemField invoice;
  //private JComboBox monnaie; // obsolète
  private GemButton okBt;
  private GemButton cancelBt;
  private boolean validation;
  private OrderLine orderLine;
  private NumberFormat nf;

  /**
   *
   * @param frame
   * @param title
   * @param dataCache
   */
  public OrderLineView(Frame frame, String title, DataCache dataCache) throws SQLException {
    super(frame, title, true);

    nf = AccountUtil.getDefaultNumberFormat();

    GemPanel editPanel = new GemPanel();
    editPanel.setLayout(new java.awt.GridBagLayout());
    GridBagHelper gb = new GridBagHelper(editPanel);
    gb.insets = GridBagHelper.SMALL_INSETS;

    payer = new GemNumericField(8);
    member = new GemNumericField(8);
    group = new GemNumericField(8);
    date = new DateFrField();
    label = new GemField(24);
    amount = new JFormattedTextField(nf);
    amount.setColumns(8);
    modeOfPayment = new JComboBox(
            ParamTableIO.getValues(
            ModeOfPaymentCtrl.TABLE,
            ModeOfPaymentCtrl.COLUMN_NAME, dataCache.getDataConnection())
            );
    document = new GemField(10);
    schoolChoice = new ParamChoice(dataCache.getList(Model.School).getData());
    account = new AccountChoice(AccountIO.find(true, dataCache.getDataConnection()));
    costAccount = new ParamChoice(
            ActivableParamTableIO.findActive(
            CostAccountCtrl.tableName, CostAccountCtrl.columnName,
            CostAccountCtrl.columnFilter, dataCache.getDataConnection())
            );

    //monnaie = new JComboBox(monnaies);
    cbPaid = new JCheckBox();
    invoice = new GemField(10);

    gb.add(new JLabel(BundleUtil.getLabel("Payer.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Member.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Group.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Payment.schedule.date.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Label.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Amount.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Mode.of.payment.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Document.number.label")), 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("School.label")), 0, 8, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Account.label")), 0, 9, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Cost.account.label")), 0, 10, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Payment.schedule.cashing.tip")), 0, 11, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Invoice.label")), 0, 12, 1, 1, GridBagHelper.WEST);
    //gb.add(new JLabel("Monnaie"), 0, 11, 1, 1, GridBagHelper.EAST);

    gb.add(payer, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(member, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(group, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(date, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(label, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(amount, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(modeOfPayment, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(document, 1, 7, 1, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 8, 1, 1, GridBagHelper.WEST);
    gb.add(account, 1, 9, 1, 1, GridBagHelper.WEST);
    gb.add(costAccount, 1, 10, 1, 1, GridBagHelper.WEST);
    gb.add(cbPaid, 1, 11, 1, 1, GridBagHelper.WEST);
    gb.add(invoice, 1, 12, 1, 1, GridBagHelper.WEST);
    //gb.add(monnaie, 1, 11, 1, 1, GridBagHelper.WEST); //

    okBt = new GemButton(GemCommand.VALIDATION_CMD);
    okBt.addActionListener(this);
    cancelBt = new GemButton(GemCommand.CANCEL_CMD);
    cancelBt.addActionListener(this);

    JPanel buttons = new JPanel(new GridLayout(1,1));
    buttons.add(okBt);
    buttons.add(cancelBt);

    setLayout(new BorderLayout());
    add(editPanel,BorderLayout.CENTER);
    add(buttons,BorderLayout.SOUTH);
    setSize(500, 420);

    setLocationRelativeTo(frame);
  }

  boolean isValidation() {
    return validation;
  }

  void setIdEditable(boolean b) {
    payer.setEditable(b);
    member.setEditable(b);
    group.setEditable(b);
  }
  
  /**
   * Desactivates all but group field.
   * @param e editable
   */
  void setGroupEditable(boolean e) {
    setEditable(!e);
    date.setEditable(!e);
    document.setEditable(!e);
    
    group.setEditable(e);
  }

  void setInvoiceEditable(boolean b) {
    invoice.setEditable(b);
  }

  /**
   * Global desactivation of fields.
   * @param single simple or multiple selection
   */
  public void setEditable(boolean single) {
    if (!single) {
      setIdEditable(false);
      payer.setEditable(false);
      member.setEditable(false);
      group.setEditable(false);
      label.setEditable(false);
      amount.setEditable(false);
      modeOfPayment.setEnabled(false);
      schoolChoice.setEnabled(false);
      account.setEnabled(false);
      costAccount.setEnabled(false);
      cbPaid.setEnabled(false);
      invoice.setEditable(false);
      //monnaie.setEditable(false);
    }
  }

  void testValidation()
          throws NumberFormatException, IllegalArgumentException, ParseException {
    String s = payer.getText();
    if (s.length() < 1) {
      throw new IllegalArgumentException("Payeur non saisi");
    }
    s = label.getText();
    if (s.length() < 1) {
      throw new IllegalArgumentException("Libellé non saisi");
    }
    //if (!AccountUtil.isPersonalAccount(getAccount()) && getAmount() < 1.0) {
    if (!ModeOfPayment.FAC.toString().equals(modeOfPayment.getSelectedItem()) && getAmount() < 0.0) {
      if (!MessagePopup.confirm(this, MessageUtil.getMessage("payment.negative.amount.warning"))) {
        throw new IllegalArgumentException(MessageUtil.getMessage("invalid.amount"));
      }
    }

  }

  void setPayerId(int _payerid) {
    payer.setText(String.valueOf(_payerid));
    //payeur.setEditable(false);
  }

  void setMemberId(int memberId) {
    member.setText(String.valueOf(memberId));
  }

  void setOrderLine(OrderLine ol) {
    orderLine = ol;

    setPayerId(orderLine.getPayer());
    setMemberId(orderLine.getMember());
    group.setText(String.valueOf(orderLine.getGroup()));
    date.setText(orderLine.getDate().toString());
    label.setText(orderLine.getLabel());
    setAmount(orderLine.getDoubleAmount());
    modeOfPayment.setSelectedItem(orderLine.getModeOfPayment());
    document.setText(orderLine.getDocument());
    schoolChoice.setKey(orderLine.getSchool());
    setAccount(orderLine.getAccount());
    costAccount.setSelectedItem(orderLine.getCostAccount());
    cbPaid.setSelected(orderLine.isPaid());
    invoice.setText(orderLine.getInvoice());
    //monnaie.setSelectedItem(echeancier.getCurrency());
  }

  OrderLine getOrderLine() throws ParseException {
    if (orderLine == null) {
      orderLine = new OrderLine();
    }

    try {
      orderLine.setPayer(nf.parse(payer.getText()).intValue());
    } catch (ParseException ignore) {
      GemLogger.logException(ignore);
      orderLine.setPayer(0);
    }
    try {
      orderLine.setMember(nf.parse(member.getText()).intValue());
    } catch (ParseException ignore) {
      GemLogger.logException(ignore);
      orderLine.setMember(0);
    }
    try {
      orderLine.setGroup(nf.parse(group.getText()).intValue());
    } catch (ParseException ignore) {
      GemLogger.logException(ignore);
      orderLine.setGroup(0);
    }
    orderLine.setDate(date.getDate());
    orderLine.setModeOfPayment((String) modeOfPayment.getSelectedItem());
    orderLine.setLabel(label.getText());
    orderLine.setAmount(getAmount());
    orderLine.setDocument(document.getText());
    orderLine.setSchool(schoolChoice.getKey());
    orderLine.setAccount(getAccount());
    orderLine.setCostAccount(getCostAccount());
    orderLine.setPaid(cbPaid.isSelected());
    orderLine.setInvoice(invoice.getText());
    //echeancier.setCurrency((String) monnaie.getSelectedItem());

    return orderLine;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == okBt) {
      try {
        testValidation();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage()
                + " quantité saisie non numérique",
                                      MessageUtil.getMessage("entry.error"),
                                      JOptionPane.ERROR_MESSAGE);
        return;
      } catch (ParseException ex) {
        JOptionPane.showMessageDialog(this,
                                      "prix saisi non numérique",
                                      MessageUtil.getMessage("entry.error"),
                                      JOptionPane.ERROR_MESSAGE);
        return;
      } catch (IllegalArgumentException ex) {
        MessagePopup.error(this, MessageUtil.getMessage("entry.error") + " :\n" + ex.getMessage());
        return;
      }
      validation = true;
    }
    setVisible(false);
  }

  /**
   * Gets account choice.
   * @return an account
   */
  Account getAccount() {
    return (Account) account.getSelectedItem();
//    return new Account(p);
  }

  Account getCostAccount() {
    Param p = (Param) costAccount.getSelectedItem();
    return new Account(p);
  }

  /**
   * Selects an account for modification.
   * @param a an account
   */
  private void setAccount(Account a) {
    account.setSelectedItem(a);
  }

  private void setAmount(Double m) {
    amount.setValue(m);
  }

  /**
   * Retrieves the amount.
   * @return a double
   * @throws parseException in case of error format
   */
  private Double getAmount() throws ParseException {
    //echeancier.setAmount(nf.parse(montant.getText()).intValue());
    //class cast exception (Long -> Double) si 00 après la virgule avec : (Double)montant.getValue();
    amount.commitEdit();
    Number n = (Number) amount.getValue();
    return n.doubleValue();
  }
}
