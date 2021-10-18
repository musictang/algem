/*
 * @(#)CanvasPlanTrim.java	2.6.a 19/09/12
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

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import net.algem.util.ui.GemPanel;


/**
 * 
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class CanvasPlanTrim
        extends GemPanel
        implements MouseListener, AdjustmentListener, ScheduleView
{

  private int margeh = 40;
  private int marged = 60;
  private int margd2 = 20;
  private int clickx;
  private int clicky;
  private Schedule clickPlan;
  private int year = 1999;
  private Calendar cal;
  private SimpleDateFormat df;
  private String[] dayLabels;
  private String[] monthLabels;
  private List[] plan;
  private Dimension dim;
  private int pas_x;
  private int pas_y;
  //private Rectangle curect;
  private Font font;
  private Font titleFont;
  private Scrollbar sb;
  private int top;
  private Hour hStart;
  private Hour hEnd;
  private int nbmin;
  private ActionListener actionListener;

  public CanvasPlanTrim() {
    font = new Font("Helvetica", Font.PLAIN, 12);
    titleFont = new Font("Helvetica", Font.BOLD, 14);

    cal = Calendar.getInstance(Locale.FRANCE);
    df = new SimpleDateFormat("EEEEE dd MMMMM yyyy", Locale.FRANCE);

    dayLabels = PlanningService.WEEK_DAYS;
    monthLabels = new DateFormatSymbols(Locale.FRANCE).getMonths();
    setLayout(new BorderLayout());
    sb = new Scrollbar(Scrollbar.HORIZONTAL, 0, 3, 0, 12);
    sb.addAdjustmentListener(this);
    add("South", sb);
    addMouseListener(this);

    hStart = new Hour("09:00");
    hEnd = new Hour("22:30");
    nbmin = hEnd.toMinutes() - hStart.toMinutes();
  }

  public void setDate(Date d) {
    cal.setTime(d);
    repaint();
  }

  public Date getDate() {
    return cal.getTime();
  }

  @Override
  public Schedule getSchedule() {
    return clickPlan;
  }

  @Override
  public List getScheduleRanges() {
    return null;
  }

  public void loadPlanning(List<Schedule> pl) {

    if (pl == null) {
      plan = null;
    } else {
      Calendar d = (Calendar) cal.clone();
      plan = new ArrayList[12];
      for (int i = 0; i < 12; i++) {
        plan[i] = new ArrayList<Schedule>();
      }
      int x = 0;
      for (int i = 0; i < pl.size(); i++) {
        Schedule p = pl.get(i);
        d.setTime(p.getDate().getDate());
        int m = d.get(Calendar.MONTH);
        plan[m].add(p);
      }
    }
    repaint();
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

    int cy = ((y - margeh) / pas_y) - 1;
    int mois = ((x - marged) / pas_x) + top;
    int hh = x - marged - margd2 - (pas_x * (mois - top));
    hh = (nbmin * hh) / (pas_x - margd2);
    hh += 540;
    int mm = hh % 60;
    hh /= 60;

    mois += 8;
    if (mois >= 12) {
      mois -= 12;
      cal.set(year + 1, mois, 1, hh, mm);
    } else {
      cal.set(year, mois, 1, hh, mm);
    }

    cal.setTime(cal.getTime());
    int saut = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
    if (saut < 0) {
      saut = 6;
    }
    int jour = (cy - saut + 1);
    cal.set(Calendar.DATE, jour);
    cal.setTime(cal.getTime());

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "click"));
    }
  }

  @Override
  public Point getClickPosition() {
    Point p = getLocationOnScreen();

    p.x += clickx;
    p.y += clicky;

    return p;
  }

  @Override
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(210, 180);
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void paint(Graphics g) {
    dim = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, dim.width, dim.height);
    draw(g);
  }

  public void draw(Graphics g) //public void paint(Graphics g)
  {
    int sz, x, y;

//		pas_y = (dim.height-margeh-sb.getSize().height) / 38;
    pas_x = (dim.width - marged) / 3;
    pas_y = (dim.height - margeh) / 38;

//		g.setColor(getBackground());
//		g.fill3DRect(0,0,dim.width,margeh,true);
//		g.setColor(this.getForeground());
    g.setFont(titleFont);
    FontMetrics fm = g.getFontMetrics();
    String libel = "Année scolaire " + year + "/" + (year + 1);
    sz = fm.stringWidth(libel);
    g.drawString(libel, (dim.width - sz) / 2, 20);

    g.setFont(font);

    x = 2;
    y = margeh + pas_y;
    g.setColor(Color.black);
    g.drawLine(2, y, marged + (pas_x * 12), y);

    int i, jj;
    for (i = 0, jj = Calendar.MONDAY; i < 37; i++) {
      g.drawString(dayLabels[jj], 2, y + (pas_y / 2) + 5);
      if (jj == 1) {
        g.setColor(net.algem.Algem.BGCOLOR_PLANNING);
        g.fillRect(marged + 1, y + 1, (pas_x * 12) - 1, pas_y - 1);
        g.setColor(Color.black);
      }
      if (++jj > 7) {
        jj = 1;
      }
      y += pas_y;
      g.drawLine(2, y, marged + (pas_x * 12), y);
    }

    x = marged;
    y = margeh;

    for (i = top; i < top + 3 && i < 12; i++) {
      int m = i + 8;
      if (m >= 12) {
        m -= 12;
      }
      g.drawLine(x, y, x, margeh + (pas_y * 38));
      String s = monthLabels[m].toUpperCase();
      sz = fm.stringWidth(s);
      g.drawString(s, x + (pas_x - sz) / 2, y - 15 + (pas_y / 2) + 5);
      g.drawString("9h", x + margd2, y + (pas_y / 2) + 5);
      int x1 = ((pas_x - margd2) * 300) / nbmin;
      g.drawString("14h", x + margd2 + x1, y + (pas_y / 2) + 5);
      int x2 = ((pas_x - margd2) * 540) / nbmin;
      g.drawString("18h", x + margd2 + x2, y + (pas_y / 2) + 5);

      g.setColor(net.algem.Algem.BGCOLOR_PLANNING);
      g.drawLine(x + margd2, y + (pas_y / 2) + 5, x + margd2, margeh + (pas_y * 38));
      g.drawLine(x + margd2 + x1, y + (pas_y / 2) + 5, x + margd2 + x1, margeh + (pas_y * 38));
      g.drawLine(x + margd2 + x2, y + (pas_y / 2) + 5, x + margd2 + x2, margeh + (pas_y * 38));
      g.setColor(Color.black);

      x += pas_x;
    }
    g.drawLine(x, y, x, marged + (pas_y * 37));


    x = marged;
    for (i = top; i < top + 3 && i < 12; i++) {
      int m = i + 8;
      if (m >= 12) {
        m -= 12;
        cal.set(year + 1, m, 1, 9, 0);
      } else {
        cal.set(year, m, 1, 9, 0);
      }
      cal.setTime(cal.getTime());
      int saut = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
      if (saut < 0) {
        saut = 6;
      }
      y = margeh + ((saut + 1) * pas_y);
      for (int j = 1; j <= DateLib.daysInMonth(m + 1, year); j++) {
        String s = String.valueOf(j);
        sz = fm.stringWidth(s);
        g.drawString(s, x + (margd2 - sz) / 2, y + (pas_y / 2) + 5);
        y += pas_y;
      }
      y = margeh + (saut * pas_y);
      Calendar d = (Calendar) cal.clone();
      g.setColor(Color.red);
      if (plan != null) {
        for (int j = 0; j < plan[m].size(); j++) {
          Schedule p = (Schedule) plan[m].get(j);

          d.setTime(p.getDate().getDate());
          int y1 = y + (d.get(Calendar.DAY_OF_MONTH) * pas_y);
          Hour hd = p.getStart();
          int x1 = ((pas_x - margd2) * (hd.toMinutes() - 540)) / nbmin;
          Hour hf = p.getEnd();
          int x2 = ((pas_x - margd2) * (hf.toMinutes() - hd.toMinutes())) / nbmin;
          g.fillRect(margd2 + x + x1 + 1, y1 + 1, x2 - 2, pas_y - 2);
        }
      }
      g.setColor(Color.black);
      x += pas_x;
    }
  }

  @Override
  public void adjustmentValueChanged(AdjustmentEvent e) {
    top = e.getValue();
    compscroll();
//		bim = null;
    repaint();
  }

  private void compscroll() {
//		if (bim == null)
//			return;		// not visible
    sb.setValues(top, 3, 0, 12);
  }
}
