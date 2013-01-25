/*
 * @(#)RehearsalCardView.java 2.6.a 03/10/12
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
package net.algem.contact.member;

import java.awt.GridBagLayout;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class RehearsalCardView extends GemBorderPanel
{

  private GemField label;
  private JFormattedTextField amount;
  private GemNumericField nbSessions;
  private GemNumericField minDuration;
  private NumberFormat format;
  private RehearsalCard card;

  public RehearsalCardView()
  {

    label = new GemField(20);
    initAmountField();

    nbSessions = new GemNumericField(5);
    minDuration = new GemNumericField(5);
    minDuration.setToolTipText(BundleUtil.getLabel("Duration.min.tip"));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Amount.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Sessions.number.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Duration.min.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(label, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(amount, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(nbSessions, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(minDuration, 1, 3, 3, 1, GridBagHelper.WEST);
  }

  public void clear()
  {
    card = null;
    label.setText("");
    amount.setValue(0.0);
    nbSessions.setText(String.valueOf(RehearsalCard.NB_SESSIONS_DEFAULT));
    minDuration.setText(String.valueOf(RehearsalCard.MIN_DURATION_DEFAULT));
  }

  public void setCard(RehearsalCard c)
  {
    if (c == null) {
      return;
    }
    card = new RehearsalCard();
    card.setId(c.getId());
    
    label.setText(c.getLabel());
    amount.setValue(c.getAmount());
    nbSessions.setText(String.valueOf(c.getSessionsNumber()));
    minDuration.setText(String.valueOf(c.getDuration()));

  }

  public RehearsalCard getCard()
  {
    if (card == null) {
      return null;
    }
    card.setLabel(label.getText());
    card.setAmount((Float) amount.getValue());
    card.setSessionsNumber(Integer.parseInt(nbSessions.getText()));
    card.setDuration(Integer.parseInt(minDuration.getText()));

    return card;
  }

  private void initAmountField()
  {
    format = NumberFormat.getNumberInstance(Locale.getDefault());
    format.setMinimumFractionDigits(2);
    format.setMaximumFractionDigits(2);
    NumberFormatter nf = new NumberFormatter(format);
    nf.setValueClass(Float.class);
    amount = new JFormattedTextField(nf);
    amount.setColumns(5);
  }


}
