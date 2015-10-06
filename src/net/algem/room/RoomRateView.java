/*
 * @(#)RoomRateView.java	2.6.a 24/09/12
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
package net.algem.room;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1a
 */
public class RoomRateView
        extends GemBorderPanel
{

  private GemField label;
  private JComboBox type;
  private JFormattedTextField nh;
  private JFormattedTextField ph;
  private JFormattedTextField max;
  private JFormattedTextField fixedNh;
  private JFormattedTextField fixedPh;
  private NumberFormat format;
  private RoomRate rate;

  public RoomRateView() {
    label = new GemField(15);
    type = new JComboBox(RoomRateEnum.values());

    format = NumberFormat.getNumberInstance(Locale.getDefault());
    format.setMinimumFractionDigits(2);
    format.setMaximumFractionDigits(2);

    NumberFormatter nf = new NumberFormatter(format);
    nf.setValueClass(Double.class);

    Dimension dim = new Dimension(50, 20);

    nh = new JFormattedTextField(nf);
    nh.setPreferredSize(dim);
    ph = new JFormattedTextField(nf);
    ph.setPreferredSize(dim);
    max = new JFormattedTextField(nf);
    max.setPreferredSize(dim);
    fixedNh = new JFormattedTextField(nf);
    fixedNh.setPreferredSize(dim);
    fixedPh = new JFormattedTextField(nf);
    fixedPh.setPreferredSize(dim);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = new Insets(2, 5, 2, 5);
    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Type.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.rate.offpeak.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.rate.peak.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.rate.max.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.rate.fixed.offpeak.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.rate.fixed.peak.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(label, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(type, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(nh, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(ph, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(max, 1, 4, 3, 1, GridBagHelper.WEST);
    gb.add(fixedNh, 1, 5, 3, 1, GridBagHelper.WEST);
    gb.add(fixedPh, 1, 6, 3, 1, GridBagHelper.WEST);
  }

  private double getFixedNh() {
    return (Double) fixedNh.getValue();
  }

  private void setFixedNh(double forfaithc) {
    this.fixedNh.setValue(forfaithc);
  }

  private double getFixedPh() {
    return (Double) fixedPh.getValue();
  }

  private void setFixedPh(double forfaithp) {
    this.fixedPh.setValue(forfaithp);
  }

  private double getNh() {
    return (Double) nh.getValue();
  }

  private void setNh(double hc) {
    this.nh.setValue(hc);
  }

  private double getPh() {
    return (Double) ph.getValue();
  }

  private void setPh(double hp) {
    this.ph.setValue(hp);
  }

  private String getLabel() {
    return label.getText();
  }

  private void setLabel(String libelle) {
    this.label.setText(libelle);
  }

  private double getMax() {
    return (Double) max.getValue();
  }

  private void setMax(double max) {
    this.max.setValue(max);
  }

  public void setRate(RoomRate tarif) {
    this.rate = tarif;
    setLabel(tarif.getLabel());
    setType((RoomRateEnum) tarif.getType());
    setNh(tarif.getOffpeakRate());
    setPh(tarif.getFullRate());
    setMax(tarif.getMax());
    setFixedNh(tarif.getPassOffPeakPrice());
    setFixedPh(tarif.getPassFullPrice());
  }

  public RoomRate getRate() {
    RoomRate t = new RoomRate();
    if (rate != null) {
      t.setId(rate.getId());
    } else {
      t.setId(0);
    }
    t.setLabel(getLabel());
    t.setType(getType());
    t.setOffPeakRate(getNh());
    t.setFullRate(getPh());
    t.setMax(getMax());
    t.setPassOffPeakPrice(getFixedNh());
    t.setPassFullPrice(getFixedPh());

    return t;
  }

  private RoomRateEnum getType() {
    return (RoomRateEnum) type.getSelectedItem();
  }

  private void setType(RoomRateEnum type) {
    this.type.setSelectedItem(type);
  }

  public void clear() {
    rate = null;
    setLabel(null);
    setType(null);
    setNh(0.0);
    setPh(0.0);
    setMax(0.0);
    setFixedNh(0.0);
    setFixedPh(0.0);
  }
}
