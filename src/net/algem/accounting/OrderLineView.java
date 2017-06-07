/*
 * @(#)OrderLineView.java	2.14.0 05/06/17
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
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
 * @version 2.14.0
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
  private ParamChoice tax;
  private JLabel taxLabel;
  private List<Param> taxList;
  private JLabel  inclTax;
  private GemField invoice;
  private GemButton okBt;
  private GemButton cancelBt;
  private boolean validation;
  private OrderLine orderLine;
  private NumberFormat nf;
  private ActionListener listener;

  /**
   *
   * @param frame
   * @param title
   * @param dataCache
   * @param modal status
   * @throws java.sql.SQLException
   */
  public OrderLineView(Frame frame, String title, final DataCache dataCache, boolean modal) throws SQLException {
    super(frame, title, modal);

    nf = AccountUtil.getDefaultNumberFormat();

    GemPanel editPanel = new GemPanel();
    editPanel.setLayout(new java.awt.GridBagLayout());
    final GridBagHelper gb = new GridBagHelper(editPanel);

    payer = new GemNumericField(8);
    payer.setMinimumSize(new Dimension(60, payer.getPreferredSize().height));
    member = new GemNumericField(8);
    member.setMinimumSize(new Dimension(60, member.getPreferredSize().height));
    group = new GemNumericField(8);
    group.setMinimumSize(new Dimension(60, group.getPreferredSize().height));
    date = new DateFrField();
    label = new GemField(24);
    amount = new JFormattedTextField(nf);
    amount.setColumns(8);
    amount.setMinimumSize(new Dimension(60, amount.getPreferredSize().height));

    taxList = dataCache.getList(Model.Vat).getData();
    taxLabel = new JLabel(BundleUtil.getLabel("Invoice.item.vat.label"));
    taxLabel.setEnabled(false);
    tax = new ParamChoice(taxList);
    tax.setEnabled(false);

    amount.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (isInvoicePayment()) {
          showPriceTaxeIncluded(tax.getValue());
        }
      }
    });
    inclTax = new JLabel();
    inclTax.setEnabled(false);
    DataConnection dc = DataCache.getDataConnection();
    modeOfPayment = new JComboBox(
            ParamTableIO.getValues(
                    ModeOfPaymentCtrl.TABLE,
                    ModeOfPaymentCtrl.COLUMN_NAME, dc)
    );
    modeOfPayment.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (isInvoicePayment()) {
          taxLabel.setEnabled(true);
          tax.setEnabled(true);
        } else {
          taxLabel.setEnabled(false);
          tax.setEnabled(false);
          inclTax.setText(null);
        }

      }
    });

    tax.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (isInvoicePayment() && e.getStateChange() == ItemEvent.SELECTED) {
          showPriceTaxeIncluded(tax.getValue());
        }
      }
    });

    document = new GemField(8);
    document.setMinimumSize(new Dimension(amount.getPreferredSize().width, document.getPreferredSize().height));
    schoolChoice = new ParamChoice(dataCache.getList(Model.School).getData());
    account = new AccountChoice(AccountIO.find(true, dc));
    costAccount = new ParamChoice(
            ActivableParamTableIO.findActive(
                    CostAccountCtrl.tableName, CostAccountCtrl.columnName,
                    CostAccountCtrl.columnFilter, dc)
    );
    costAccount.setPreferredSize(account.getPreferredSize());
    cbPaid = new JCheckBox();
    invoice = new GemField(10);
    invoice.setPreferredSize(document.getPreferredSize());
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

    gb.add(payer, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(member, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(group, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(date, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(label, 1, 4, 3, 1, GridBagHelper.WEST);
    gb.add(amount, 1, 5, 2, 1, GridBagHelper.WEST);


    gb.add(inclTax, 3, 5, 1, 1, GridBagHelper.WEST);
    gb.add(modeOfPayment, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(taxLabel, 2, 6, 1, 1, GridBagHelper.WEST);
    gb.add(tax, 3, 6, 1, 1, GridBagHelper.WEST);
    gb.add(document, 1, 7, 3, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 8, 3, 1, GridBagHelper.WEST);
    gb.add(account, 1, 9, 3, 1, GridBagHelper.WEST);
    gb.add(costAccount, 1, 10, 3, 1, GridBagHelper.WEST);
    gb.add(cbPaid, 1, 11, 3, 1, GridBagHelper.WEST);
    gb.add(invoice, 1, 12, 3, 1, GridBagHelper.WEST);

    okBt = new GemButton(GemCommand.VALIDATION_CMD);
    okBt.addActionListener(this);
    cancelBt = new GemButton(GemCommand.ABORT);
    cancelBt.addActionListener(this);

    JPanel buttons = new JPanel(new GridLayout(1, 1));
    buttons.add(okBt);
    buttons.add(cancelBt);

    setLayout(new BorderLayout());
    add(editPanel, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(new Dimension(500, 480));

    setLocationRelativeTo(frame);
  }

  /**
   * Calculates and show price full taxes.
   * @param value tax value
   */
  private void showPriceTaxeIncluded(String value) {
    float val = Float.parseFloat(value);
    if (val <= 0) {
      inclTax.setText(null);
    } else {
      try {
        double exclTax = getTotalTaxesExcluded();
        String suff = " " +  BundleUtil.getLabel("Invoice.ati.label");
        inclTax.setText(nf.format(exclTax + (exclTax * val / 100d)) + suff);
      } catch (ParseException pe) {
        GemLogger.logException(pe);
      }
    }
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
   *
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

  void setStandardEditable() {
    date.setEditable(false);
    payer.setEditable(false);
    member.setEditable(false);
    cbPaid.setEnabled(false);
    invoice.setEditable(false);
    group.setEditable(false);
  }

  /**
   * Global desactivation of fields.
   *
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
    if (!ModeOfPayment.FAC.toString().equals(modeOfPayment.getSelectedItem()) && getTotalTaxesIncluded() < 0.0) {
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
    modeOfPayment.setSelectedItem(orderLine.getModeOfPayment());
    document.setText(orderLine.getDocument());
    schoolChoice.setKey(orderLine.getSchool());
    setAccount(orderLine.getAccount());
    costAccount.setSelectedItem(orderLine.getCostAccount());
    cbPaid.setSelected(orderLine.isPaid());
    invoice.setText(orderLine.getInvoice());
    setVat(orderLine.getTax());
    setAmount(orderLine.getDoubleAmount());
    showPriceTaxeIncluded(tax.getValue());
  }

  private void setVat(float v) {
    for(Param p: taxList) {
      if (p.getValue().equals(String.valueOf(v))) {
        tax.setSelectedItem(p);
        break;
      }
    }
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
    orderLine.setAmount(getTotalTaxesIncluded());
    orderLine.setDocument(document.getText());
    orderLine.setSchool(schoolChoice.getKey());
    orderLine.setAccount(getAccount());
    orderLine.setCostAccount(getCostAccount());
    orderLine.setPaid(cbPaid.isSelected());
    orderLine.setInvoice(invoice.getText());

    if (isInvoicePayment()) {
      orderLine.setTax(Float.parseFloat(tax.getValue()));
    }

    return orderLine;
  }

  private boolean isInvoicePayment() {
    return ModeOfPayment.FAC.name().equals(modeOfPayment.getSelectedItem());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == okBt) {
      try {
        testValidation();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage() + " quantité saisie non numérique",
                MessageUtil.getMessage("entry.error"),
                JOptionPane.ERROR_MESSAGE);
        validation = false;
        return;
      } catch (ParseException ex) {
        JOptionPane.showMessageDialog(this,
                "prix saisi non numérique",
                MessageUtil.getMessage("entry.error"),
                JOptionPane.ERROR_MESSAGE);
        validation = false;
        return;
      } catch (IllegalArgumentException ex) {
        MessagePopup.error(this, MessageUtil.getMessage("entry.error") + " :\n" + ex.getMessage());
        validation = false;
        return;
      }
      validation = true;
      if (listener != null) {
        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "orderline.view.validate"));
      } else {
        setVisible(false);// if modal
      }
    } else {
      validation = false;
      if (listener != null) {
        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "orderline.view.cancel"));
        // setVisible(false);// when non modal, the dialog may be reopened later
      } else {
        setVisible(false);
      }

    }
  }

  /**
   * Gets account choice.
   *
   * @return an account
   */
  Account getAccount() {
    return (Account) account.getSelectedItem();
  }

  Account getCostAccount() {
    Param p = (Param) costAccount.getSelectedItem();
    return new Account(p);
  }

  /**
   * Selects an account for modification.
   *
   * @param a an account
   */
  private void setAccount(Account a) {
    account.setSelectedItem(a);
  }

  private void setAmount(Double m) {
    float v = Float.parseFloat(tax.getValue());
    if (v > 0) {
      double coeff = 100 / (100 + v);
      Double et = AccountUtil.round(Math.abs(m) * coeff);
      amount.setValue(m < 0 ? -et : et);
    } else {
      amount.setValue(m);
    }
  }

  /**
   * Gets the amount, all taxes included.
   *
   * @return a total
   * @throws parseException in case of error format
   */
  private double getTotalTaxesIncluded() throws ParseException {
    //echeancier.setAmount(nf.parse(montant.getText()).intValue());
    //class cast exception (Long -> Double) si 00 après la virgule avec : (Double)montant.getValue();
    if (amount.getValue() != null) {
      amount.commitEdit();
      Number n = (Number) amount.getValue();
      float t = Float.parseFloat(tax.getValue());
      if (t > 0) {
        double et = n.doubleValue();
        return et + (et * t / 100d);
      } else {
        return n.doubleValue();
      }
    }
    return 0.0;
  }

  /**
   * Gets the amount, excluding taxes.
   * @return a total
   * @throws ParseException 
   */
  private double getTotalTaxesExcluded() throws ParseException {
    if (amount.getValue() != null) {
      amount.commitEdit();
      Number n = (Number) amount.getValue();
      return n.doubleValue();
    }
    return 0.0;
  }

  void addActionListener(ActionListener listener) {
    this.listener = listener;
  }
}
