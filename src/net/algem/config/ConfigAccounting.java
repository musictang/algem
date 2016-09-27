/*
 * @(#)ConfigAccounting.java 2.11.0 27/09/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import net.algem.accounting.AccountingExportFormat;
import net.algem.bank.BankUtil;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Bic infos for the organization.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.11.0
 * @since 2.2.d
 */
public class ConfigAccounting
  extends ConfigPanel {

  private Config c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14;
  private GemField firmName;
  private GemField issuer;
  private GemField branch;
  private GemField estab;
  private GemField account;
  private GemField iban;
  private GemField bic;
  private GemField ics;
  private GemField document;
  private GemField invoice;
//  private GemField dossierName;
  private JComboBox accountingFormat;
  private JSpinner defaultDueDay;
  private JCheckBox roundFractionalPayments;
  private JCheckBox chargeEnrolmentLines;

  public ConfigAccounting(String title, Map<String, Config> cm) {
    super(title, cm);
    init();
  }

  private void init() {
    c1 = confs.get(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getKey());//raison
    c2 = confs.get(ConfigKey.DIRECT_DEBIT_CREDITOR_NNE.getKey());//emetteur
    c3 = confs.get(ConfigKey.DIRECT_DEBIT_BANK_BRANCH.getKey());//guichet
    c4 = confs.get(ConfigKey.DIRECT_DEBIT_BANKHOUSE_CODE.getKey());//etablissement
    c5 = confs.get(ConfigKey.DIRECT_DEBIT_ACCOUNT.getKey());//compte
    c6 = confs.get(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getKey());//piece
    c7 = confs.get(ConfigKey.ACCOUNTING_INVOICE_NUMBER.getKey());//facture
//    c8 = confs.get(ConfigKey.ACCOUNTING_DOSSIER_NAME.getKey());//nom du dossier comptable
    c8 = confs.get(ConfigKey.ACCOUNTING_EXPORT_FORMAT.getKey());//format export
    c9 = confs.get(ConfigKey.DIRECT_DEBIT_IBAN.getKey());//iban
    c10 = confs.get(ConfigKey.DIRECT_DEBIT_BIC.getKey());//bic
    c11 = confs.get(ConfigKey.DIRECT_DEBIT_ICS.getKey());//ics
    c12 = confs.get(ConfigKey.DEFAULT_DUE_DAY.getKey());//jour d'échéance par défaut
    c13 = confs.get(ConfigKey.ROUND_FRACTIONAL_PAYMENTS.getKey());//jour d'échéance par défaut
    c14 = confs.get(ConfigKey.CHARGE_ENROLMENT_LINES.getKey());//jour d'échéance par défaut
    
    firmName = new GemField(20);
    firmName.setText(c1.getValue());
    issuer = new GemField(10);
    issuer.setText(c2.getValue());
    branch = new GemField(5);
    branch.setText(c3.getValue());
    estab = new GemField(5);
    estab.setText(c4.getValue());
    account = new GemField(10);
    account.setText(c5.getValue());
    document = new GemField(10);
    document.setText(c6.getValue());
    invoice = new GemField(10);
    invoice.setText(c7.getValue());
    accountingFormat = new JComboBox(new String[]{
      AccountingExportFormat.CIEL.getLabel(),
      AccountingExportFormat.DVLOG.getLabel(),
      AccountingExportFormat.SAGE.getLabel()});
    accountingFormat.setSelectedItem(c8.getValue());

    initIban();
    iban.setText(c9.getValue());

    initBic();
    bic.setText(c10.getValue());

    ics = new GemField(10);
    ics.setText(c11.getValue());

    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(Integer.parseInt(c12.getValue()), 1, 28, 1);
    defaultDueDay = new JSpinner(spinnerModel);
    defaultDueDay.setToolTipText(BundleUtil.getLabel("ConfEditor.default.due.date.tip"));

    roundFractionalPayments = new JCheckBox(ConfigKey.ROUND_FRACTIONAL_PAYMENTS.getLabel());
    roundFractionalPayments.setToolTipText(BundleUtil.getLabel("ConfEditor.round.fractional.payments.tip"));
    roundFractionalPayments.setSelected(c13 == null || c13.getValue() == null ? false : c13.getValue().toLowerCase().startsWith("t"));

    chargeEnrolmentLines = new JCheckBox(ConfigKey.CHARGE_ENROLMENT_LINES.getLabel());
    chargeEnrolmentLines.setToolTipText(BundleUtil.getLabel("ConfEditor.charge.enrolment.lines.tip"));
    chargeEnrolmentLines.setSelected(c14 == null || c14.getValue() == null ? false : c14.getValue().toLowerCase().startsWith("t"));
    content = new GemPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

    GemPanel creditorPanel = new GemPanel(new GridBagLayout());
    creditorPanel.setMinimumSize(new Dimension(400,240));
    creditorPanel.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Menu.debiting.label")));
    
    GridBagHelper gb = new GridBagHelper(creditorPanel);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getLabel()), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(firmName, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_CREDITOR_NNE.getLabel()), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(issuer, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_BANK_BRANCH.getLabel()), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(branch, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_BANKHOUSE_CODE.getLabel()), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(estab, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_ACCOUNT.getLabel()), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(account, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_IBAN.getLabel()), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(iban, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_BIC.getLabel()), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(bic, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_ICS.getLabel()), 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(ics, 1, 7, 1, 1, GridBagHelper.WEST);

    
    GemPanel options = new GemPanel(new GridBagLayout());
    options.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Options.label")));
    options.setMinimumSize(new Dimension(400,200));
    
    GridBagHelper gb2 = new GridBagHelper(options);
    gb2.add(new GemLabel(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getLabel()), 0, 0, 1, 1, GridBagHelper.WEST);
    gb2.add(document, 1, 0, 1, 1, GridBagHelper.WEST);
    gb2.add(new GemLabel(ConfigKey.ACCOUNTING_INVOICE_NUMBER.getLabel()), 0, 1, 1, 1, GridBagHelper.WEST);
    gb2.add(invoice, 1, 1, 1, 1, GridBagHelper.WEST);
    GemLabel accountingFormatLabel = new GemLabel(ConfigKey.ACCOUNTING_EXPORT_FORMAT.getLabel());
    accountingFormatLabel.setToolTipText(BundleUtil.getLabel("ConfEditor.accounting.export.format.tip"));
    GemLabel defaultDueDayLabel = new GemLabel(ConfigKey.DEFAULT_DUE_DAY.getLabel());
    defaultDueDayLabel.setToolTipText(BundleUtil.getLabel("ConfEditor.default.due.date.tip"));
    gb2.add(accountingFormatLabel, 0, 2, 1, 1, GridBagHelper.WEST);
    gb2.add(accountingFormat, 1, 2, 1, 1, GridBagHelper.WEST);
    gb2.add(defaultDueDayLabel, 0, 3, 1, 1, GridBagHelper.WEST);
    gb2.add(defaultDueDay, 1, 3, 1, 1, GridBagHelper.WEST);
    gb2.add(roundFractionalPayments, 0, 4, 2, 1, GridBagHelper.WEST);
    gb2.add(chargeEnrolmentLines, 0, 5, 2, 1, GridBagHelper.WEST);

    content.add(creditorPanel);
    content.add(options);

    add(content);
  }

  private void initBic() {
    bic = new GemField(10);
    bic.addFocusListener(new FocusAdapter() {

      @Override
      public void focusGained(FocusEvent e) {
        markBic();
      }

      @Override
      public void focusLost(FocusEvent e) {
        markBic();
      }
    });

    bic.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        markBic();
      }
    });
  }

  private void initIban() {
    iban = new GemField(20);
    iban.addFocusListener(new FocusAdapter() {

      @Override
      public void focusGained(FocusEvent e) {
        markIban();
      }

      @Override
      public void focusLost(FocusEvent e) {
        markIban();
      }
    });

    iban.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        markIban();
      }
    });
  }

  private void markBic() {
    bic.setBackground(BankUtil.isBicOk(bic.getText()) ? Color.WHITE : ColorPrefs.ERROR_BG_COLOR);
  }

  private void markIban() {
    iban.setBackground(BankUtil.isIbanOk(iban.getText()) ? Color.WHITE : ColorPrefs.ERROR_BG_COLOR);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();

    c1.setValue(firmName.getText().trim());
    c2.setValue(issuer.getText().trim());
    c3.setValue(branch.getText().trim());
    c4.setValue(estab.getText().trim());
    c5.setValue(account.getText().trim());
    c6.setValue(document.getText().trim());
    c7.setValue(invoice.getText().trim());
//    c8.setValue(dossierName.getText().trim());
    c8.setValue(accountingFormat.getSelectedItem().toString());
    c9.setValue(iban.getText().trim());
    c10.setValue(bic.getText().trim());
    c11.setValue(ics.getText().trim());
    c12.setValue(String.valueOf(defaultDueDay.getValue()));
    c13.setValue(roundFractionalPayments.isSelected() ? "t" : "f");
    c14.setValue(chargeEnrolmentLines.isSelected() ? "t" : "f");

    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    conf.add(c6);
    conf.add(c7);
    conf.add(c8);
    conf.add(c9);
    conf.add(c10);
    conf.add(c11);
    conf.add(c12);
    conf.add(c13);
    conf.add(c14);

    return conf;
  }
}
