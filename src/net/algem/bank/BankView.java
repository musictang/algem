/*
 * @(#)BankView.java	2.6.a 14/09/12
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
import javax.swing.JCheckBox;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 */
public class BankView
        extends GemBorderPanel
{

  private BankCodeField code;
  private GemField name;
  private JCheckBox multi;

  public BankView() {
    code = new BankCodeField();
    name = new GemField(30);
    multi = new JCheckBox();

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Code.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.multi.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(code, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(multi, 1, 2, 3, 1, GridBagHelper.WEST);
  }

  public void setCode(String s) {
    code.setText(s);
  }

  public void setBankName(String s) {
    name.setText(s);
  }

  public String getBankName() {
    return name.getText();
  }

  public String getBankCode() {
    return code.getText();
  }

  public void setBank(Bank b) {
    code.setText(b.getCode());
    name.setText(b.getName());
    multi.setSelected(b.isMulti());
  }

  public Bank getBank() {
    Bank b = new Bank(getBankCode(), getBankName());
    b.setMulti(multi.isSelected());
    return b;
  }

  public void clear() {
    code.setText("");
    name.setText("");
    multi.setSelected(false);
  }
}
