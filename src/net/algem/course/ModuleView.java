/*
 * @(#)ModuleView.java	2.7.a 08/01/13
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
package net.algem.course;

import java.awt.GridBagLayout;
import java.text.Format;
import javax.swing.BorderFactory;
import net.algem.accounting.AccountUtil;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class ModuleView
        extends GemPanel {

  private String id = "0";
  private GemNumericField no;
  private GemField title;
  private GemField code;
  private GemDecimalField basicPrice;
  private GemDecimalField monthRateReduc;
  private GemDecimalField quarterRateReduc;
  private ModuleCodeView viewCode;

  public ModuleView() {
    no = new GemNumericField(6);
    no.setEditable(false);
    code = new GemField(7);
    title = new GemField(ModuleIO.TITLE_MAX_LEN);
    title.setColumns(ModuleIO.TITLE_MAX_LEN);
		
    Format f = AccountUtil.getDefaultNumberFormat();
    
    basicPrice = new GemDecimalField(f);

    monthRateReduc = new GemDecimalField(f);
    quarterRateReduc = new GemDecimalField(f);
    basicPrice.setColumns(6);
    monthRateReduc.setColumns(3);
    quarterRateReduc.setColumns(3);
    
    GemPanel panelPrix = new GemPanel();
    panelPrix.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Module.rate.label")));
    panelPrix.add(new GemLabel(BundleUtil.getLabel("Module.basic.rate.label")));
    panelPrix.add(basicPrice);
    panelPrix.add(new GemLabel(BundleUtil.getLabel("Module.month.reduc.rate.label")));
    panelPrix.add(monthRateReduc);
    panelPrix.add(new GemLabel(BundleUtil.getLabel("Module.trim.reduc.rate.label")));
    panelPrix.add(quarterRateReduc);

    viewCode = new ModuleCodeView();

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Number.abbrev.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(viewCode, 0, 1, 2, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Title.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(panelPrix, 0, 3, 2, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(title, 1, 2, 1, 1, GridBagHelper.WEST);
  }

  public String getId() {
    return id;
  }

  public void setId(String i) {
    id = i;
  }

  /**
   * 
   * Gets the (updated) module.
   *
   * @return un Module
   */
  public Module get() {
    Module m = new Module();
    try {
      m.setId(Integer.parseInt(no.getText()));
    } catch (NumberFormatException e) {
      m.setId(0);
    }
    m.setTitle(title.getText());
    m.setCode(viewCode.getCode());
    
    try {
      m.setBasePrice((Double) basicPrice.getValue());
    } catch (Exception e) {
      m.setBasePrice(0.0);
    } 
    try {
      m.setMonthReducRate((Double) monthRateReduc.getValue());
    } catch (Exception e) {
      m.setMonthReducRate(0.0);
    }
    try {
      m.setQuarterReducRate((Double) quarterRateReduc.getValue());
    } catch (Exception e) {
      m.setQuarterReducRate(0.0);
    }

    return m;
  }

  /**
   * Sets the module infos.
   *
   * @param m current module
   */
  public void set(Module m) {
    no.setText(String.valueOf(m.getId()));
    title.setText(m.getTitle());
    code.setText(m.getCode());
    viewCode.setCode(m.getCode());
    basicPrice.setValue(m.getBasePrice());
    monthRateReduc.setValue(m.getMonthReducRate());
    quarterRateReduc.setValue(m.getQuarterReducRate());
  }

  /**
   * Resets the view.
   */
  public void clear() {
    no.setText(null);
    title.setText(null);
    code.setText(null);
    viewCode.setCode("L00000000 ");
    basicPrice.setValue(0.0);
    monthRateReduc.setValue(0.0);
    quarterRateReduc.setValue(0.0);
  }
}
