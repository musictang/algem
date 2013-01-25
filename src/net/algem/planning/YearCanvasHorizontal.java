/*
 * @(#)YearCanvasHorizontal.java	2.6.a 19/09/12
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class YearCanvasHorizontal
        extends YearCanvas
{

  private static final int MARGEH = 30;
  private static final int MARGED = 40;

  public YearCanvasHorizontal() {
    dayLabels = new DateFormatSymbols(Locale.FRANCE).getShortWeekdays();
    monthLabels = new DateFormatSymbols(Locale.FRANCE).getShortMonths();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    int cy = ((y - MARGEH) / pas_y) - 1;
    int month = ((x - MARGED) / pas_x);
    month += 8;
    if (month >= 12) {
      month -= 12;
      cal.set(year + 1, month, 1, 9, 0);
    } else {
      cal.set(year, month, 1, 9, 0);
    }

    cal.setTime(cal.getTime());
    int hop = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
    if (hop < 0) {
      hop = 6;
    }
    int day = (cy - hop + 1);
    cal.set(Calendar.DATE, day);
    cal.setTime(cal.getTime());

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "click"));
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(450, 570);
  }

  @Override
  public void paint(Graphics g) {
    dim = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, dim.width, dim.height);
    draw(g);
  }

  public void draw(Graphics g) {
    int sz, x, y;

    pas_x = (dim.width - MARGED) / 12;
    pas_y = (dim.height - MARGEH) / 38;

    g.setColor(getBackground());
    g.fill3DRect(0, 0, dim.width, MARGEH, true);
    g.setColor(this.getForeground());
    g.setFont(titleFont);
    FontMetrics fm = g.getFontMetrics();
    String label = "Année scolaire " + year + "/" + (year + 1);
    sz = fm.stringWidth(label);
    g.drawString(label, (dim.width - sz) / 2, 20);

    g.setFont(font);

    x = MARGED;
    y = MARGEH;

    int i, jj;
    for (i = 0; i < 12; i++) {
      int m = i + 8;
      if (m >= 12) {
        m -= 12;
      }
      g.setColor(Color.gray);
      g.drawLine(x, y, x, MARGED + (pas_y * 37));
      g.setColor(Color.black);
      sz = fm.stringWidth(monthLabels[m]);
      g.drawString(monthLabels[m], x + (pas_x - sz) / 2, y + (pas_y / 2) + 5);
      x += pas_x;
    }
    g.drawLine(x, y, x, MARGED + (pas_y * 37));

    x = 2;
    y += pas_y;
    g.setColor(Color.black);
    g.drawLine(2, y, MARGED + (pas_x * 12), y);
    for (i = 0, jj = Calendar.MONDAY; i < 37; i++) {
      g.drawString(dayLabels[jj], 2, y + (pas_y / 2) + 5);
      if (jj == 1) {
        g.setColor(Color.gray);
        g.fillRect(MARGED + 1, y + 1, (pas_x * 12) - 1, pas_y - 1);
        g.setColor(Color.black);
      }
      if (++jj > 7) {
        jj = 1;
      }
      y += pas_y;
      g.drawLine(2, y, MARGED + (pas_x * 12), y);
    }

    x = MARGED;
    for (i = 0; i < 12; i++) {
      int m = i + 8;
      if (m >= 12) {
        m -= 12;
        cal.set(year + 1, m, 1, 9, 0);
      } else {
        cal.set(year, m, 1, 9, 0);
      }
      cal.setTime(cal.getTime());
      int skip = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
      if (skip < 0) {
        skip = 6;
      }
      y = MARGEH + ((skip + 1) * pas_y);
      for (int j = 1; j <= DateLib.daysInMonth(m + 1, year); j++) {
        String s = String.valueOf(j);
        sz = fm.stringWidth(s);
        g.drawString(s, x + (pas_x - sz) / 2, y + (pas_y / 2) + 5);
        y += pas_y;
      }
      x += pas_x;
    }
  }
}
