/*
 * @(#)DateBar.java	2.6.a 21/09/12
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
package net.algem.planning;

import java.awt.AWTEventMulticaster;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class DateBar
        extends GemPanel
        implements ActionListener
{

  private String[] monthLabels;
  private GemButton[] monthButtons;
  private GemButton btBefore;
  private GemButton btAfter;
  private Calendar cal;
  private ActionListener actionListener;

  public DateBar(Date d) {
    cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(d);

    setLayout(new GridLayout(1, 14));

    btBefore = new GemButton("<<<");
    btBefore.setMargin(new Insets(2, 2, 2, 2));
    add(btBefore);
    btBefore.addActionListener(this);

    monthLabels = new DateFormatSymbols(Locale.FRANCE).getShortMonths();
    monthButtons = new GemButton[12];
    for (int i = 0; i < 12; i++) {
      monthButtons[i] = new GemButton(monthLabels[i]);
      monthButtons[i].setMargin(new Insets(2, 2, 2, 2));
      add(monthButtons[i]);
      monthButtons[i].addActionListener(this);
    }
    btAfter = new GemButton(">>>");
    btAfter.setMargin(new Insets(2, 2, 2, 2));
    add(btAfter);
    btAfter.addActionListener(this);
  }

  public DateBar() {
    this(new Date());
  }

  public Date getDate() {
    return cal.getTime();
  }

  public void setDate(Date d) {
    cal.setTime(d);
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object c = evt.getSource();
    if (c == btBefore) {
      cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
    } else if (c == btAfter) {
      cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
    } else {
      for (int i = 0; i < 12; i++) {
        if (c == monthButtons[i]) {
          cal.set(cal.get(Calendar.YEAR), i, 1);
          break;
        }
      }
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "date"));
    }
  }
}
