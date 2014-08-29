/*
 * @(#)DayPlanTableView.java	2.8.w 27/08/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.day;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Vector;
import javax.swing.JScrollBar;
import net.algem.planning.DateDayBar;
import net.algem.planning.DateFrField;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Abstract class for day views.
 * Main view {@literal DayPlanView} is composed of columns {@literal DayPlan}.
 * The field {@literal date} and button bar {@literal dayBar} permit to navigate in the dayPlanViewning.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public abstract class DayPlanTableView
        extends GemPanel
        implements AdjustmentListener, PropertyChangeListener, KeyListener
{

  protected DayPlanView dayPlanView;
  private JScrollBar sb;
  protected DateDayBar dayBar;
  protected DateFrField date;
  protected GemButton btNow;

  public DayPlanTableView(String label) {

    dayPlanView = new DayPlanView();
    date = new DateFrField();
    dayBar = new DateDayBar();

    GemPanel p = new GemPanel();
    p.setLayout(new BorderLayout());
    p.add(new GemLabel(BundleUtil.getLabel("Day.schedule.prefix.label") + " " + label), BorderLayout.WEST);
    p.add(date, BorderLayout.CENTER);
    btNow = new GemButton(BundleUtil.getLabel("Action.today.label"));
    p.add(btNow, BorderLayout.EAST);
    sb = new JScrollBar(JScrollBar.HORIZONTAL);
    sb.addAdjustmentListener(this);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(p, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dayPlanView, 0, 1, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(sb, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(dayBar, 0, 3, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

    dayPlanView.addKeyListener(this);
  }

  public DateDayBar getBar() {
    return dayBar;
  }

  public void setBar() {
    Rectangle r = dayPlanView.computeScroll();
    sb.setValues(r.x, r.y, r.width, r.height);
  }

  @Override
  public void adjustmentValueChanged(AdjustmentEvent e) {
    dayPlanView.setTop(e.getValue());
    Rectangle r = dayPlanView.computeScroll();
    sb.setValues(r.x, r.y, r.width, r.height+1);//on ajoute 1 pour englober toutes les colonnes
  }

  public void addActionListener(ActionListener l) {
    dayBar.addActionListener(l);
    dayPlanView.addActionListener(l);
    date.addActionListener(l);
    btNow.addActionListener(l);
  }

  public void removeActionListener(ActionListener l) {
    dayBar.removeActionListener(l);
    dayPlanView.removeActionListener(l);
    date.removeActionListener(l);
    btNow.removeActionListener(l);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    if (evt.getSource() instanceof DaySchedule) {
      DaySchedule model = (DaySchedule) evt.getSource();
      Date d = (Date) evt.getNewValue();
      load(d, model.getSchedules(), model.getRanges());
      dayBar.setDate(d);
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
    System.out.println("KEYTYPE:" + e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    System.out.println("KEYREL:" + e);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    System.out.println("KEYPRESS:" + e);
    if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
      System.out.println("PGDOWN");
      e.consume();
    }
  }

  public DayPlanView getCanvas() {
    return dayPlanView;
  }

  public abstract void load(java.util.Date d, Vector<ScheduleObject> schedules, Vector<ScheduleRangeObject> ranges);
}
