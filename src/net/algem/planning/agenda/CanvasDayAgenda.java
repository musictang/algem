/*
 * @(#)CanvasDayAgenda.java	2.6.a 20/09/12
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
package net.algem.planning.agenda;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import net.algem.planning.*;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CanvasDayAgenda
        extends GemPanel
        implements MouseListener, ScheduleView
{

  int clickx;
  int clicky;
  ScheduleObject clickPlan;
  int maxplage;
  int top;
  int nlignes;
  int margeh = 12;
  int pas_y = 12;
  String[] heures;
  int minutes;
  List<PlanningLib> plan;
  Calendar cal;
  Dimension dim;
  ActionListener actionListener;

  public CanvasDayAgenda(int max) {

    maxplage = max;

    cal = Calendar.getInstance(Locale.FRANCE);
    plan = new ArrayList();

    heures = new String[maxplage];
    Hour heure = new Hour("09:00");
    for (int i = 0; i < maxplage; i++) {
      heures[i] = heure.toString();
      heure.incMinute(30);
    }

    addMouseListener(this);
  }

  public void set(PlanningLib p) {
    minutes += p.getEnd().toMinutes() - p.getStart().toMinutes();
    plan.add(p);
  }

  public void setDate(Date d) {
    cal.setTime(d);
    clear();
    repaint();
  }

  public Schedule getSchedule() {
    return clickPlan;
  }

  public java.util.List getScheduleRanges() {
    return null;
  }

  public void clear() {
    top = 0;
    minutes = 0;
    plan.clear();
  }

  public void setTop(int t) {
    top = t;
    repaint();
  }

  public int getTop() {
    return top;
  }

  public int getRowCount() {
    return nlignes;
  }

  public void paint(Graphics g) {
    dim = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, dim.width, dim.height);
    dessine(g);
  }

  public void dessine(Graphics g) //public void paint(Graphics g)
  {

    g.setColor(Color.lightGray);
    g.fillRect(0, 0, dim.width, margeh);

    FontMetrics fm = g.getFontMetrics();
    SimpleDateFormat df = new SimpleDateFormat("EEEEEE dd MMMMMM yyyy", Locale.FRANCE);

//		cal.add(Calendar.HOUR_OF_DAY,1);
    String libel = df.format(cal.getTime());
//		cal.add(Calendar.HOUR_OF_DAY,-1);
    int sz = fm.stringWidth(libel);
    g.setColor(Color.black);
    g.drawString(libel, (dim.width - sz) / 2, 10);

    if (minutes > 0) {
      g.setColor(Color.red);
      g.drawString(new Hour(minutes).toString(), 2, 10);
    }


//		g.setColor(Color.yellow);
    g.setColor(Color.white);
    g.fillRect(0, margeh, dim.width, dim.height - margeh);

    g.setColor(Color.black);
    g.drawRect(0, 0, dim.width - 1, dim.height - 1);
    g.drawLine(0, margeh, dim.width, margeh);

    nlignes = (dim.height - margeh) / pas_y;
    int y = margeh + 10;
    for (int i = top; i < top + nlignes && i < heures.length; i++, y += pas_y) {
      g.drawString(heures[i], 2, y);
    }

    for (int i = 0; i < plan.size(); i++) {
      PlanningLib p = (PlanningLib) plan.get(i);
      int debpl = p.getStart().toMinutes() - 540;
      int finpl = p.getEnd().toMinutes() - 540;
      int duree = finpl - debpl;
      int debvue = top * 30;
      int finvue = debvue + (nlignes * 30);
      if ((debvue >= debpl && debvue < finpl)
              || (finvue > debpl && finvue <= finpl)
              || (debvue <= debpl && finvue >= finpl)) {
        int yh, yb;
        if (debvue >= debpl) {
          yh = margeh;
        } else {
          yh = margeh + ((debpl - debvue) * 12) / 30;
        }
        if (finvue <= finpl) {
          yb = yh + (finvue * 12) / 30;
        } else {
          yb = margeh + ((finpl - debvue) * 12) / 30;
        }
        g.setXORMode(Color.white);
//				g.drawRect(40,yh, 10,yb-yh);
        g.fillRect(0, yh, 35, yb - yh);
        g.setXORMode(Color.white);
        g.drawString(p.getRoom() + " " + p.getCourse(), 40, yh + 10);
      }
    }
  }

  public Point getClickPosition() {
    Point p = getLocationOnScreen();

    p.x += clickx;
    p.y += clicky;

    return p;
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
    clickx = e.getX();
    clicky = e.getY();

    int debvue = top * 30;

    int y = clicky - margeh - 1;

    int hh = 540 + debvue + ((y / pas_y) * 30);
    int mm = hh % 60;
    hh /= 60;

    Calendar c = (Calendar) cal.clone();
    c.set(cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DATE), hh, mm);
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(c.getTime(), ActionEvent.ACTION_PERFORMED, "Date"));
    }

  }
}
