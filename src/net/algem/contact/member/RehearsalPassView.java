/*
 * @(#)RehearsalPassView.java 2.9.4.12 28/08/15
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
package net.algem.contact.member;

import java.awt.GridBagLayout;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 */
public class RehearsalPassView
        extends GemBorderPanel
{

  private GemField label;
  private JFormattedTextField amount;
  private HourField minTime;
  private GemDecimalField totalTime;
  private NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());;
  private RehearsalPass card;

  public RehearsalPassView() {

    label = new GemField(20);
    initAmountField();

    minTime = new HourField();
    minTime.setToolTipText(BundleUtil.getLabel("Pass.rehearsal.min.tip"));

    totalTime = new GemDecimalField(nf);
    totalTime.setColumns(5);
    totalTime.setToolTipText(BundleUtil.getLabel("Pass.rehearsal.total.tip"));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Amount.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Duration.min.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Total.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(label, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(amount, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(minTime, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(totalTime, 1, 3, 1, 1, GridBagHelper.WEST);
  }

  public void clear() {
    card = null;
    label.setText("");
    amount.setValue(0.0);
    minTime.setText(String.valueOf(RehearsalPass.MIN_DEFAULT));
  }

  public void setCard(RehearsalPass c) {
    if (c == null) {
      return;
    }
    card = new RehearsalPass();
    card.setId(c.getId());

    label.setText(c.getLabel());
    amount.setValue(c.getAmount());
    minTime.set(new Hour(c.getMin()));
    totalTime.setValue(c.getTotalTime() / 60d);

  }

  public RehearsalPass getCard() {
    if (card == null) {
      return null;
    }
    card.setLabel(label.getText());
    card.setAmount((Float) amount.getValue());
    card.setMin(minTime.getHour().toMinutes());
    card.setTotalTime( (int) ((double) totalTime.getValue() * 60));

    return card;
  }

  private void initAmountField() {
    nf.setMaximumFractionDigits(2);
    NumberFormatter formatter = new NumberFormatter(this.nf);
    formatter.setValueClass(Float.class);
    amount = new JFormattedTextField(formatter);
    amount.setColumns(5);
  }

}
