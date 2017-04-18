/*
 * @(#)ModuleView.java	2.13.1 17/04/17
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
package net.algem.course;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.text.Format;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import net.algem.accounting.AccountUtil;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
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
 * @version 2.13.1
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
  private GemDecimalField yearRateReduc;
  private CourseModuleView courseView;
  private JCheckBox active;

  public ModuleView(DataCache dataCache) {

    no = new GemNumericField(6);
    no.setEditable(false);
    no.setMinimumSize(new Dimension(60, no.getPreferredSize().height));

    title = new GemField(20, ModuleIO.TITLE_MAX_LEN);

    Format f = AccountUtil.getDefaultNumberFormat();

    basicPrice = new GemDecimalField(f);
    basicPrice.setToolTipText(BundleUtil.getLabel("Module.basic.rate.tip"));
    monthRateReduc = new GemDecimalField(f);
    monthRateReduc.setToolTipText(BundleUtil.getLabel("Module.month.reduc.rate.tip"));
    quarterRateReduc = new GemDecimalField(f);
    quarterRateReduc.setToolTipText(BundleUtil.getLabel("Module.trim.reduc.rate.tip"));
    basicPrice.setColumns(6);
    monthRateReduc.setColumns(3);
    quarterRateReduc.setColumns(3);
    yearRateReduc = new GemDecimalField(f);
    yearRateReduc.setToolTipText(BundleUtil.getLabel("Module.year.reduc.rate.tip"));
    yearRateReduc.setColumns(3);

    GemPanel pricePanel = new GemPanel(new GridBagLayout());
    String periodConfig = ConfigUtil.getConf(ConfigKey.DEFAULT_PRICING_PERIOD.getKey());
    String basePeriod = "";
    switch (periodConfig) {
      case "MNTH":
        basePeriod = BundleUtil.getLabel("Payment.frequency.month.label");
        break;
      case "QTER":
        basePeriod = BundleUtil.getLabel("Payment.frequency.quarter.label");
        break;
      case "YEAR":
        basePeriod = BundleUtil.getLabel("Payment.frequency.year.label");
        break;
      case "HOUR":
        basePeriod = BundleUtil.getLabel("Payment.frequency.hour.label");
        break;
      default:
        basePeriod = BundleUtil.getLabel("Payment.frequency.year.label");
    }
    basePeriod = basePeriod.toLowerCase();
    pricePanel.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Module.rate.label")));
    GemPanel bp = new GemPanel();
    bp.add(new GemLabel(BundleUtil.getLabel("Module.basic.rate.full.label", new Object[]{basePeriod})));
    bp.add(basicPrice);

    GemPanel rp = new GemPanel(new GridLayout(1, 6, 10, 4));
    GemPanel rp1 = new GemPanel(new BorderLayout());
    GemPanel rp2 = new GemPanel(new BorderLayout());
    GemPanel rp3 = new GemPanel(new BorderLayout());

    rp1.add(new GemLabel(BundleUtil.getLabel("Module.month.reduc.rate.label")), BorderLayout.NORTH);
    rp1.add(monthRateReduc, BorderLayout.SOUTH);
    rp2.add(new GemLabel(BundleUtil.getLabel("Module.trim.reduc.rate.label")), BorderLayout.NORTH);
    rp2.add(quarterRateReduc, BorderLayout.SOUTH);
    rp3.add(new GemLabel(BundleUtil.getLabel("Module.year.reduc.rate.label")), BorderLayout.NORTH);
    rp3.add(yearRateReduc, BorderLayout.SOUTH);
    rp.add(rp1);
    rp.add(rp2);
    rp.add(rp3);
//    pricePanel.setMinimumSize(new Dimension(520,50));
    GridBagHelper gb2 = new GridBagHelper(pricePanel);
    gb2.add(bp, 0,0,1,1);
    gb2.add(rp, 0,1,1,1);

    type = new JComboBox(new String[]{
      BundleUtil.getLabel("Leisure.training.label"),
      BundleUtil.getLabel("Professional.training.label")
    });

    courseView = new CourseModuleView(dataCache.getList(Model.CourseCode));
    active = new JCheckBox(BundleUtil.getLabel("Active.label"));

    GemPanel mainPanel = new GemPanel();
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    mainPanel.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(mainPanel);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.abbrev.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(active, 2, 0, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Title.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(title, 1, 1, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(type, 1, 2, 2, 1, GridBagHelper.WEST);

    gb.add(courseView, 0, 3, 3, 1, GridBagHelper.BOTH, GridBagHelper.WEST);
    gb.add(pricePanel, 0, 4, 3, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);

    JScrollPane scroll = new JScrollPane(mainPanel);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setPreferredSize(new Dimension(580, 370));
    add(scroll);
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

    switch (type.getSelectedIndex()) {
      case 0:
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
    try {
      m.setYearReducRate((Double) yearRateReduc.getValue());
    } catch (Exception e) {
      m.setYearReducRate(0.0);
    }
    m.setActive(active.isSelected());

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
    switch (mcode.toUpperCase().charAt(0)) {
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
    yearRateReduc.setValue(m.getYearReducRate());
    courseView.set(m);
    active.setSelected(m.isActive());
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
