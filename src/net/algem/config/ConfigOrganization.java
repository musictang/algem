/*
 * @(#)ConfigOrganization.java 2.10.0 15/06/2016
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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFormattedTextField;
import javax.swing.border.Border;
import javax.swing.text.MaskFormatter;
import net.algem.contact.Address;
import net.algem.contact.AddressView;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Organization parameters and contact.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.2.p 23/01/12
 */
public class ConfigOrganization
        extends ConfigPanel
{

  private Config c1, c2, c3, c4, c5, c6, c7, c8, c9, c10;

  /** Organization name. */
  private GemField name;

  private JFormattedTextField siret;
  private JFormattedTextField naf;

  /**
   * Intra-community VAT code.
   * Code TVA intra-communautaire.
   * In France, it is composed of the letters FR, completed by a two-digits (or two-letters) key
   * assigned by the tax office from the place of exercise of the company
   * It ends by the SIREN 9-digits number.
   * VAT key = [ 12 + 3 * ( SIREN modulo 97 ) ] modulo 97
   * Pour la France, il est composé des lettres FR, complétées d'une clé de deux chiffres
   * ou lettres attribuée par le centre des impôts du lieu d'exercice de l'entreprise,
   * et du numéro SIREN à 9 chiffres. Par exemple pour l'entreprise déjà citée : FR 83 404 833 048.
   * La clé française suit la règle suivante :
   * Clé TVA = [ 12 + 3 * ( SIREN modulo 97 ) ] modulo 97
   */
  private JFormattedTextField tva;

  /**
   * Professional training code.
   * Code formation professionnelle.
   */
  private JFormattedTextField forPro;

  /** Address. */
  private AddressView address;

  /** Domain name. */
  private GemField domainName;

  public ConfigOrganization(String title, Map<String, Config> cm) {
    super(title, cm);
    init();
  }

  private void init() {
    c1 = confs.get(ConfigKey.ORGANIZATION_NAME.getKey());
    c2 = confs.get(ConfigKey.ORGANIZATION_ADDRESS1.getKey());
    c3 = confs.get(ConfigKey.ORGANIZATION_ADDRESS2.getKey());
    c4 = confs.get(ConfigKey.ORGANIZATION_ZIPCODE.getKey());
    c5 = confs.get(ConfigKey.ORGANIZATION_CITY.getKey());
    c6 = confs.get(ConfigKey.SIRET_NUMBER.getKey());
    c7 = confs.get(ConfigKey.CODE_NAF.getKey());
    c8 = confs.get(ConfigKey.CODE_TVA.getKey());
    c9 = confs.get(ConfigKey.CODE_FP.getKey());
    c10 = confs.get(ConfigKey.ORGANIZATION_DOMAIN.getKey());

    content = new GemPanel(new BorderLayout());

    Box pName = Box.createHorizontalBox();
		pName.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    pName.add(new GemLabel(BundleUtil.getLabel("Name.label")));
		pName.add(Box.createHorizontalStrut(3));
    name = new GemField(20);
    name.setText(c1.getValue());
		pName.add(name);
		pName.add(Box.createHorizontalGlue());

    MaskFormatter siretMask = MessageUtil.createFormatter("### ### ### #####");
    siretMask.setValueContainsLiteralCharacters(false);
    siret = new JFormattedTextField(siretMask);
    siret.setColumns(14);
    siret.setValue(c6.getValue());

    naf = new JFormattedTextField(MessageUtil.createFormatter("AAAAA"));
    naf.setColumns(5);
    naf.setValue(c7.getValue());

    MaskFormatter tvaMask = MessageUtil.createFormatter("AA AA AAA AAA AAA AAAAA");
    tvaMask.setValueContainsLiteralCharacters(false);

    tva = new JFormattedTextField(tvaMask);
    tva.setColumns(18);
    tva.setValue(c8.getValue());

    MaskFormatter forProMask = MessageUtil.createFormatter("## ## ##### ##");
    forProMask.setValueContainsLiteralCharacters(false);

    forPro = new JFormattedTextField(forProMask);
    forPro.setColumns(11);
    forPro.setValue(c9.getValue());

    address = new AddressView(null,false);

    Address a = new Address();
    a.setAdr1(c2.getValue());
    a.setAdr2(c3.getValue());
    a.setCdp(c4.getValue());
    a.setCity(c5.getValue());

    address.set(a);

    domainName = new GemField(10);
    domainName.setText(c10.getValue());

    GemPanel domainPanel = new GemPanel(new GridLayout(1,2));
    domainPanel.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.domain.tip"));
    domainPanel.add(new GemLabel(ConfigKey.ORGANIZATION_DOMAIN.getLabel()));
    domainPanel.add(domainName);

    GemPanel contactPanel = new GemPanel(new BorderLayout());
    contactPanel.add(address, BorderLayout.NORTH);

    Border topBorder = BorderFactory.createEmptyBorder(10,0,10,0);
    domainPanel.setBorder(topBorder);
    contactPanel.add(domainPanel, BorderLayout.SOUTH);

    GemPanel pCodes = new GemPanel(new GridLayout(4,2));
		((GridLayout) pCodes.getLayout()).setHgap(4);
		((GridLayout) pCodes.getLayout()).setVgap(4);
    pCodes.setBorder(topBorder);
    pCodes.add(new GemLabel(ConfigKey.SIRET_NUMBER.getLabel()));
    pCodes.add(siret);
    pCodes.add(new GemLabel(ConfigKey.CODE_NAF.getLabel()));
    pCodes.add(naf);
    pCodes.add(new GemLabel(ConfigKey.CODE_TVA.getLabel()));
    pCodes.add(tva);
    pCodes.add(new GemLabel(ConfigKey.CODE_FP.getLabel()));
    pCodes.add(forPro);

    content.add(pName, BorderLayout.NORTH);
    content.add(contactPanel, BorderLayout.CENTER);
    content.add(pCodes, BorderLayout.SOUTH);

    add(content);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    Address b = address.get();
    c1.setValue(name.getText().trim());
    c2.setValue(b == null ? "" : b.getAdr1().trim());
    c3.setValue(b == null ? "" : b.getAdr2().trim());
    c4.setValue(b == null ? "" : b.getCdp().trim());
    c5.setValue(b == null ? "" : b.getCity().trim());
    c6.setValue((String)siret.getValue());
    c7.setValue(naf.getText());
    c8.setValue((String)tva.getValue());
    c9.setValue((String)forPro.getValue());
    c10.setValue(domainName.getText());

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

    return conf;
  }

}


