/*
 * @(#)BicConfig.java 2.6.a 14/09/12
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

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.algem.config.Config;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Bic infos for the organization.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 * @since 2.2.d
 */
public class BicConfig
    extends ConfigPanel {

  private  Config c1, c2, c3, c4, c5, c6, c7;

  private GemField firmName;
  private GemField issuer;
  private GemField branch;
  private GemField estab;
  private GemField account;
  private GemField document;
  private GemField invoice;

  public BicConfig(String title, Map<String, Config> cm) {
    super(title, cm);
    init();
  }

  private void init() {
    c1 = confs.get(ConfigKey.STANDING_ORDER_FIRM_NAME.getKey());//raison
    c2 = confs.get(ConfigKey.STANDING_ORDER_ISSUER.getKey());//emetteur
    c3 = confs.get(ConfigKey.STANDING_ORDER_BANK_BRANCH.getKey());//guichet
    c4 = confs.get(ConfigKey.STANDING_ORDER_BANKHOUSE_CODE.getKey());//etablissement
    c5 = confs.get(ConfigKey.STANDING_ORDER_ACCOUNT.getKey());//compte
    c6 = confs.get(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getKey());//piece
    c7 = confs.get(ConfigKey.ACCOUNTING_INVOICE_NUMBER.getKey());//facture

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
    
    content = new GemPanel();

    content.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(content);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(ConfigKey.STANDING_ORDER_FIRM_NAME.getLabel()),0,0,1,1,GridBagHelper.EAST);
    gb.add(firmName,1,0,1,1, GridBagHelper.WEST);

    gb.add(new GemLabel(ConfigKey.STANDING_ORDER_ISSUER.getLabel()),0,1,1,1,GridBagHelper.EAST);
    gb.add(issuer,1,1,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.STANDING_ORDER_BANK_BRANCH.getLabel()),0,2,1,1,GridBagHelper.EAST);
    gb.add(branch,1,2,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.STANDING_ORDER_BANKHOUSE_CODE.getLabel()),0,3,1,1,GridBagHelper.EAST);
    gb.add(estab,1,3,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.STANDING_ORDER_ACCOUNT.getLabel()),0,4,1,1,GridBagHelper.EAST);
    gb.add(account,1,4,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getLabel()),0,5,1,1,GridBagHelper.EAST);
    gb.add(document,1,5,1,1, GridBagHelper.WEST);
    gb.add(new GemLabel(ConfigKey.ACCOUNTING_INVOICE_NUMBER.getLabel()),0,6,1,1,GridBagHelper.EAST);
    gb.add(invoice,1,6,1,1, GridBagHelper.WEST);
    
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

    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    conf.add(c6);
    conf.add(c7);

    return conf;
  }

}
