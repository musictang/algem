/*
 * @(#)CanvasCalendar.java	2.9.6 16/03/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.6
 */
public class CanvasCalendar
        extends GemPanel
        implements MouseListener
{

  private Calendar dd;
  private SimpleDateFormat df;
  private String[] dayLabels;
  private Dimension dim;
  private int pas_x;
  private int pas_y;
  private Rectangle curect;
  private Font font;
  private Font titleFont;
  private boolean vacancy[];
  private boolean busy[];
  private ActionListener actionListener;

  public CanvasCalendar() {

    font = new Font("Helvetica", Font.PLAIN, 12);
    titleFont = new Font("Helvetica", Font.PLAIN, 14);

    dd = Calendar.getInstance(Locale.FRANCE);
    df = new SimpleDateFormat("EEEEE dd MMMMM yyyy", Locale.FRANCE);
    dayLabels = new DateFormatSymbols(Locale.FRANCE).getShortWeekdays();

    vacancy = new boolean[31];
    for (int i = 0; i < 31; i++) {
      vacancy[i] = false;
    }
    busy = new boolean[31];
    for (int i = 0; i < 31; i++) {
      busy[i] = false;
    }

    addMouseListener(this);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    int cx = (x / pas_x) + 1;
    int cy = (y / pas_y) - 2;
    int abs = cx + (cy * 7);

    Calendar mm = (Calendar) dd.clone();
    mm.set(Calendar.DAY_OF_MONTH, 1);
    mm.setTime(mm.getTime());
    int deb = mm.get(Calendar.DAY_OF_WEEK) - 1;

    int jj = abs - deb;

    if (jj <= 0 || jj > DateLib.daysInMonth(dd.getTime()) || cx > 7) {
      return;
    }
    dd.set(Calendar.DAY_OF_MONTH, jj);

    Graphics g = this.getGraphics();
    g.setColor(getBackground());
    g.drawRect(curect.x, curect.y, curect.width, curect.height);

    int xx = (cx - 1) * pas_x;
    curect = new Rectangle(xx, (cy + 2) * pas_y, pas_x, pas_y);
    g.setColor(Color.red);
    g.drawRect(curect.x, curect.y, curect.width, curect.height);

    g.setColor(Color.black);
    paintDate(g);

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(dd.getTime(), ActionEvent.ACTION_PERFORMED, "click"));
    }
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void setDate(Date d) {
    dd.setTime(d);
    repaint();
  }

  public Date getDate() {
    return dd.getTime();
  }

  public void setMonth(int m) {
    dd.set(Calendar.MONTH, m);
    repaint();
  }

  public void setOccupation(boolean[] d) {
    System.arraycopy(d, 0, busy, 0, d.length);
  }

  public void setVacance(boolean[] d) {
    System.arraycopy(d, 0, vacancy, 0, d.length);
  }

  @Override
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(210, 180);
  }

  void paintDate(Graphics g) {
    g.setColor(getBackground());
    g.fill3DRect(0, 0, dim.width, pas_y, true);

    g.setColor(this.getForeground());
    g.setFont(titleFont);
    FontMetrics fm = g.getFontMetrics();
    String libel = df.format(dd.getTime());
    int sz = fm.stringWidth(libel);
    g.drawString(libel, (dim.width - sz) / 2, 20);
    g.setFont(font);
  }

  @Override
  public void paint(Graphics g) {
    dim = getSize();
    g.setColor(getBackground());
    if (g instanceof Graphics2D) {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    g.fillRect(0, 0, dim.width, dim.height);
    draw(g);
  }

  public void draw(Graphics g) {
    pas_x = dim.width / 7;
    pas_y = dim.height / 8;

    g.setColor(getBackground());
    if (curect != null) {
      g.drawRect(curect.x, curect.y, curect.width, curect.height);
    }
//		g.drawRect(0,0,dim.width,dim.height);
    g.setFont(font);
    paintDate(g);

    FontMetrics fm = g.getFontMetrics();

    int x = 1;
    int y = pas_y;

    for (int i = 1; i <= 7; i++, x += pas_x) {
      int sz = fm.stringWidth(dayLabels[i]);
      g.drawString(dayLabels[i], x + (pas_x - sz) / 2, y + (pas_y / 2) + 5);
    }

    g.drawLine(2, y + pas_y - 2, dim.width - 4, y + pas_y - 2);
    int curday = dd.get(Calendar.DAY_OF_MONTH);
    int nbj = DateLib.daysInMonth(dd.getTime());

    Calendar mm = (Calendar) dd.clone();
    mm.set(Calendar.DAY_OF_MONTH, 1);
    mm.setTime(mm.getTime());
    int j = mm.get(Calendar.DAY_OF_WEEK) - 1;

    int line = pas_y * 2;
    int i;
    for (i = 1; i <= nbj; i++) {
      x = j * pas_x;
      if (i == curday) {
        curect = new Rectangle(x, line, pas_x, pas_y);
        g.setColor(Color.red);
        g.drawRect(curect.x, curect.y, curect.width, curect.height);
        g.setColor(Color.black);
      }
      if (busy[i - 1]) {
        Rectangle actif;
        actif = new Rectangle(x + 1, line + 1, pas_x - 2, pas_y - 2);
        g.setColor(Color.cyan);
        g.fillRect(actif.x, actif.y, actif.width, actif.height);
        g.setColor(Color.black);
      }
      String num = String.valueOf(i);
      int sz = fm.stringWidth(num);
      g.drawString(num, x + (pas_x - sz) / 2, line + (pas_y / 2) + 5);
      if (++j > 6) {
        j = 0;
        line += pas_y;
      }
    }
  }
}
