/*
 * @(#)RoomRateView.java	2.9.4.13 07/10/15
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
 * @version 2.9.4.13
 * @since 2.1a
 */
public class RoomRateView
        extends GemBorderPanel
{

  private GemField label;
  private JComboBox type;
  private JFormattedTextField offpeak;
  private JFormattedTextField peak;
  private JFormattedTextField max;
  private JFormattedTextField passOffpeak;
  private JFormattedTextField passPeak;
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

    offpeak = new JFormattedTextField(nf);
    offpeak.setPreferredSize(dim);
    peak = new JFormattedTextField(nf);
    peak.setPreferredSize(dim);
    max = new JFormattedTextField(nf);
    max.setPreferredSize(dim);
    passOffpeak = new JFormattedTextField(nf);
    passOffpeak.setPreferredSize(dim);
    passPeak = new JFormattedTextField(nf);
    passPeak.setPreferredSize(dim);

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
    gb.add(offpeak, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(peak, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(max, 1, 4, 3, 1, GridBagHelper.WEST);
    gb.add(passOffpeak, 1, 5, 3, 1, GridBagHelper.WEST);
    gb.add(passPeak, 1, 6, 3, 1, GridBagHelper.WEST);
  }

  public void setRate(RoomRate tarif) {
    this.rate = tarif;
    this.label.setText(tarif.getLabel());
    setType((RoomRateEnum) tarif.getType());
    this.offpeak.setValue(tarif.getOffpeakRate());
    this.peak.setValue(tarif.getFullRate());
    this.max.setValue(tarif.getMax());
    this.passOffpeak.setValue(tarif.getPassOffPeakPrice());
    this.passPeak.setValue(tarif.getPassFullPrice());
  }

  public RoomRate getRate() {
    RoomRate t = new RoomRate();
    if (rate != null) {
      t.setId(rate.getId());
    } else {
      t.setId(0);
    }
    t.setLabel(label.getText());
    t.setType(getType());
    t.setOffPeakRate((Double) offpeak.getValue());
    t.setFullRate((Double) peak.getValue());
    t.setMax((Double) max.getValue());
    t.setPassOffPeakPrice((Double) passOffpeak.getValue());
    t.setPassFullPrice((Double) passPeak.getValue());

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
    label.setText(null);
    setType(null);
    offpeak.setValue(0.0);
    peak.setValue(0.0);
    max.setValue(0.0);
    passOffpeak.setValue(0.0);
    passPeak.setValue(0.0);
  }
}
