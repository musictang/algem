/*
 * @(#) OrganizationView.java Algem 2.15.0 30/07/2017
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
 */
package net.algem.contact;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemNumericField;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/07/2017
 */
public class OrganizationView
  extends JPanel {

  private GemNumericField orgId = new GemNumericField(6);
  private GemField name = new GemField(20);
  private EmployeeSelector referent;
  private GemField companyName = new GemField(20);
  private GemField siret = new GemField(20);
  private GemField naf = new GemField(20);
  private GemField codeFP = new GemField(20);
  private GemField vatId = new GemField(20);
  List<Person> personList;

  public OrganizationView(List<Person> pers) {
    referent = new EmployeeSelector(pers);
  }

  void createUI() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagHelper gb2 = new GridBagHelper(this);

    JLabel cpNameL = new JLabel(BundleUtil.getLabel("ConfEditor.corporate.label"));

    JLabel siretL = new JLabel(BundleUtil.getLabel("Organization.SIRET.label"));
    siretL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.SIRET.number.label"));
    JLabel nafL = new JLabel(BundleUtil.getLabel("Organization.NAF.label"));
    nafL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.Code.NAF.label"));
    JLabel codeFPL = new JLabel(BundleUtil.getLabel("Organization.FP.label"));
    codeFPL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.Code.FP.label"));
    JLabel vatL = new JLabel(BundleUtil.getLabel("Organization.VAT.label"));
    vatL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.Code.VAT.label"));

    gb2.add(new JLabel(BundleUtil.getLabel("Id.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb2.add(new JLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb2.add(new JLabel(BundleUtil.getLabel("Referent.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb2.add(cpNameL, 0, 3, 1, 1, GridBagHelper.WEST);
    gb2.add(siretL, 0, 4, 1, 1, GridBagHelper.WEST);
    gb2.add(nafL, 0, 5, 1, 1, GridBagHelper.WEST);
    gb2.add(codeFPL, 0, 6, 1, 1, GridBagHelper.WEST);
    gb2.add(vatL, 0, 7, 1, 1, GridBagHelper.WEST);

    orgId.setEditable(false);
    gb2.add(orgId, 1, 0, 1, 1, GridBagHelper.WEST);
    gb2.add(name, 1, 1, 1, 1, GridBagHelper.WEST);
    referent.setPreferredSize(new Dimension(name.getPreferredSize().width,referent.getPreferredSize().height));
    gb2.add(referent, 1, 2, 1, 1, GridBagHelper.WEST);
    gb2.add(companyName, 1, 3, 1, 1, GridBagHelper.WEST);
    gb2.add(siret, 1, 4, 1, 1, GridBagHelper.WEST);
    gb2.add(naf, 1, 5, 1, 1, GridBagHelper.WEST);
    gb2.add(codeFP, 1, 6, 1, 1, GridBagHelper.WEST);
    gb2.add(vatId, 1, 7, 1, 1, GridBagHelper.WEST);

  }

  void setDetails(Organization org) {
    orgId.setText(String.valueOf(org.getId()));
    name.setText(org.getName());
    if (org.getReferent() == 0) {
      referent.setSelectedIndex(0);
    } else {
      referent.setKey(org.getReferent());
    }
    companyName.setText(org.getCompanyName());
    siret.setText(org.getSiret());
    naf.setText(org.getNafCode());
    codeFP.setText(org.getFpCode());
    vatId.setText(org.getVatCode());
  }

  Organization getDetails() {
    Organization org = new Organization();
    org.setId(Integer.parseInt(orgId.getText()));//XXX
    org.setName(name.getText());
    org.setReferent(referent.getKey());
    org.setCompanyName(companyName.getText());
    org.setSiret(siret.getText());
    org.setNafCode(naf.getText());
    org.setFpCode(codeFP.getText());
    org.setVatCode(vatId.getText());

    return org;
  }

}
