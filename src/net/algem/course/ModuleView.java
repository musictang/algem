/*
 * @(#)ModuleView.java	2.9.4.12 29/09/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.text.Format;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import net.algem.accounting.AccountUtil;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Module View.
 * In this view are defined the courses that compose the module
 * and optionally the rates of this module.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 */
public class ModuleView
        extends GemPanel {

  private String id = "0";
  private GemNumericField no;
  private GemField title;
  private JComboBox type;
  private GemDecimalField basicPrice;
  private GemDecimalField monthRateReduc;
  private GemDecimalField quarterRateReduc;
  private CourseModuleView courseView;

  public ModuleView(DataCache dataCache) {   
    no = new GemNumericField(6);
    no.setEditable(false);
    no.setMinimumSize(new Dimension(60,no.getPreferredSize().height));
            
    title = new GemField(20, ModuleIO.TITLE_MAX_LEN);

    Format f = AccountUtil.getDefaultNumberFormat();

    basicPrice = new GemDecimalField(f);
    monthRateReduc = new GemDecimalField(f);
    quarterRateReduc = new GemDecimalField(f);
    basicPrice.setColumns(6);
    monthRateReduc.setColumns(3);
    quarterRateReduc.setColumns(3);

    GemPanel pricePanel = new GemPanel();
    pricePanel.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Module.rate.label")));
    pricePanel.add(new GemLabel(BundleUtil.getLabel("Module.basic.rate.label")));
    pricePanel.add(basicPrice);
    pricePanel.add(new GemLabel(BundleUtil.getLabel("Module.month.reduc.rate.label")));
    pricePanel.add(monthRateReduc);
    pricePanel.add(new GemLabel(BundleUtil.getLabel("Module.trim.reduc.rate.label")));
    pricePanel.add(quarterRateReduc);
    pricePanel.setMinimumSize(new Dimension(520,50));

    type = new JComboBox(new String[] {
      BundleUtil.getLabel("Leisure.training.label"),
      BundleUtil.getLabel("Professional.training.label")
    });

    courseView = new CourseModuleView(dataCache.getList(Model.CourseCode));
    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Number.abbrev.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Title.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(title, 1, 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(type, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(courseView, 0, 3, 2, 1, GridBagHelper.BOTH, GridBagHelper.WEST);
    gb.add(pricePanel, 0, 4, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    
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

    switch(type.getSelectedIndex()) {
      case 0 :
        m.setCode("L");
        break;
      case 1:
        m.setCode("P");
        break;
      default:
        m.setCode("L");
    }
    m.setCourses(courseView.get());

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
    String mcode = m.getCode() == null ? "L" : m.getCode();
    switch(mcode.toUpperCase().charAt(0)) {
      case 'L':
        type.setSelectedIndex(0);
        break;
      case 'P':
        type.setSelectedIndex(1);
        break;
      default:
        type.setSelectedIndex(0);
    }
    basicPrice.setValue(m.getBasePrice());
    monthRateReduc.setValue(m.getMonthReducRate());
    quarterRateReduc.setValue(m.getQuarterReducRate());
    courseView.set(m);
  }

  /**
   * Resets the view.
   */
  public void clear() {
    no.setText(null);
    title.setText(null);
    type.setSelectedIndex(0);
    basicPrice.setValue(0.0);
    monthRateReduc.setValue(0.0);
    quarterRateReduc.setValue(0.0);
    courseView.clear();
  }
}
