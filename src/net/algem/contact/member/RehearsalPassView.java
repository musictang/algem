/*
 * @(#)RehearsalPassView.java 2.13.2 09/05/17
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
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
 */
public class RehearsalPassView
        extends GemBorderPanel
{

  private static final int MAX_LABEL_LENGTH = 128;
  private static final float MAX_AMOUNT = 1000f;
  private static final int MIN_TIME_LENGTH_1 = 30;
  private static final int MIN_TIME_LENGTH_2 = 480;
  private static final int MAX_TIME_LENGTH = 360 * 24 * 60;

  private GemField label;
  private JFormattedTextField amount;
  private HourField minTime;
  private GemDecimalField totalTime;
  private NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());;
  private RehearsalPass card;

  public RehearsalPassView() {

    label = new GemField(20);
    initAmountField();

    GemLabel minTimeLabel = new GemLabel(BundleUtil.getLabel("Duration.min.label"));
    minTimeLabel.setToolTipText(BundleUtil.getLabel("Pass.rehearsal.min.tip"));
    minTime = new HourField();
    minTime.setToolTipText(BundleUtil.getLabel("Pass.rehearsal.min.tip"));

    GemLabel totalLabel = new GemLabel(BundleUtil.getLabel("Total.label"));
    totalLabel.setToolTipText(BundleUtil.getLabel("Pass.rehearsal.total.tip"));
    totalTime = new GemDecimalField(nf);
    totalTime.setColumns(5);
    totalTime.setToolTipText(BundleUtil.getLabel("Pass.rehearsal.total.tip"));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Amount.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(minTimeLabel, 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(totalLabel, 0, 3, 1, 1, GridBagHelper.WEST);

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
  
  boolean check() {
    String lb = label.getText();
    if (lb == null || lb.isEmpty() || lb.length() > MAX_LABEL_LENGTH) {
      MessagePopup.warning(this, MessageUtil.getMessage("subscription.card.label.warning", MAX_LABEL_LENGTH));
      return false;
    }
    float a = (Float) amount.getValue();
    if (a < 0f || a > MAX_AMOUNT) {
      MessagePopup.warning(this, MessageUtil.getMessage("subscription.card.amount.warning", MAX_AMOUNT));
      return false;
    }
    try {
      totalTime.commitEdit();
    } catch (ParseException ex) {
      GemLogger.log(ex.getMessage());
    }
    int total = (int) ((double) totalTime.getValue() * 60);
    int min = minTime.getHour().toMinutes();
    if (min < MIN_TIME_LENGTH_1 || min > MIN_TIME_LENGTH_2) {
      MessagePopup.warning(this, MessageUtil.getMessage("subscription.card.min.length.warning", new Object[] {MIN_TIME_LENGTH_1, MIN_TIME_LENGTH_2/60}));
      return false;
    }
    if (total < min) {
      MessagePopup.warning(this, MessageUtil.getMessage("subscription.card.min.total.length.warning"));
      return false;
    }
    
    if (total > MAX_TIME_LENGTH) {
      MessagePopup.warning(this, MessageUtil.getMessage("subscription.card.total.length.warning", MAX_TIME_LENGTH/60));
      return false;
    }
    return true;
  }

  public RehearsalPass getCard() {
    if (card == null) {
      return null;
    }
    
    card.setLabel(label.getText());
    card.setAmount((Float) amount.getValue());
    card.setMin(minTime.getHour().toMinutes());
    card.setTotalTime((int) ((double) totalTime.getValue() * 60));

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
