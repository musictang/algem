/*
 * @(#)CreditorConfig.java 2.8.r 02/01/14
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

package net.algem.config;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import net.algem.accounting.AccountingExportFormat;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Bic infos for the organization.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.r
 * @since 2.2.d
 */
public class CreditorConfig
    extends ConfigPanel {

  private  Config c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11;

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

  public CreditorConfig(String title, Map<String, Config> cm) {
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
    /*dossierName = new GemField(20);
    dossierName.setText(c8.getValue());*/
    accountingFormat = new JComboBox(new String[]{
              AccountingExportFormat.CIEL.getLabel(),
              AccountingExportFormat.DVLOG.getLabel(),
              AccountingExportFormat.SAGE.getLabel()});
    accountingFormat.setSelectedItem(c8.getValue());
		iban = new GemField(20);
    iban.setText(c9.getValue());
		bic = new GemField(10);
    bic.setText(c10.getValue());
		ics = new GemField(10);
    ics.setText(c11.getValue());
    content = new GemPanel();

    content.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(content);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getLabel()),0,0,1,1,GridBagHelper.EAST);
    gb.add(firmName,1,0,1,1, GridBagHelper.WEST);

    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_CREDITOR_NNE.getLabel()),0,1,1,1,GridBagHelper.EAST);
    gb.add(issuer,1,1,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_BANK_BRANCH.getLabel()),0,2,1,1,GridBagHelper.EAST);
    gb.add(branch,1,2,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_BANKHOUSE_CODE.getLabel()),0,3,1,1,GridBagHelper.EAST);
    gb.add(estab,1,3,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_ACCOUNT.getLabel()),0,4,1,1,GridBagHelper.EAST);
    gb.add(account,1,4,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_IBAN.getLabel()),0,5,1,1,GridBagHelper.EAST);
    gb.add(iban,1,5,1,1, GridBagHelper.WEST);
		gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_BIC.getLabel()),0,6,1,1,GridBagHelper.EAST);
    gb.add(bic,1,6,1,1, GridBagHelper.WEST);
		gb.add(new GemLabel(ConfigKey.DIRECT_DEBIT_ICS.getLabel()),0,7,1,1,GridBagHelper.EAST);
    gb.add(ics,1,7,1,1, GridBagHelper.WEST);
		gb.add(new GemLabel(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getLabel()),0,8,1,1,GridBagHelper.EAST);
    gb.add(document,1,8,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.ACCOUNTING_INVOICE_NUMBER.getLabel()),0,9,1,1,GridBagHelper.EAST);
    gb.add(invoice,1,9,1,1, GridBagHelper.WEST);
    /*GemLabel accountingDossierName = new GemLabel(ConfigKey.ACCOUNTING_DOSSIER_NAME.getLabel());
    accountingDossierName.setToolTipText(BundleUtil.getLabel("ConfEditor.accounting.export.dossier.tip"));
    gb.add(accountingDossierName,0,7,1,1,GridBagHelper.EAST);
    gb.add(dossierName,1,7,1,1,GridBagHelper.EAST);*/
    GemLabel accountingFormatLabel = new GemLabel(ConfigKey.ACCOUNTING_EXPORT_FORMAT.getLabel());
    accountingFormatLabel.setToolTipText(BundleUtil.getLabel("ConfEditor.accounting.export.format.tip"));
    gb.add(accountingFormatLabel,0,10,1,1,GridBagHelper.EAST);
    gb.add(accountingFormat,1,10,1,1, GridBagHelper.WEST);
    
    add(content);
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

    return conf;
  }

}
