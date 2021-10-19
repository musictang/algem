/*
 * @(#)MonthPlanDetailView.java	2.9.4.8 18/06/15
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
package net.algem.planning.month;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import net.algem.planning.DateBar;
import net.algem.planning.ScheduleDetailEvent;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.8
 */
public abstract class MonthPlanDetailView
        extends GemPanel
        implements ItemListener, PropertyChangeListener
{

  protected GemChoice choice;
  protected DateBar dateBar;
  protected GemField status;
  
  private GemField dateLabel;
  private Calendar cal;
  private MonthPlanView view;
  private String[] monthNames;
  private List<ScheduleObject> schedules = new ArrayList<>();
  private List<ScheduleRangeObject> ranges = new ArrayList<>();

  public MonthPlanDetailView(GemChoice choice) {
    this.choice = choice;
    cal = Calendar.getInstance(Locale.FRANCE);
    status = new GemField();
    status.setFont(new Font("Helvetica", Font.BOLD, 12));
    status.setEditable(false);
    view = new MonthPlanView(status);

    if (choice != null) {
      choice.addItemListener(this);
    }

    dateLabel = new GemField(24);
    dateLabel.setEditable(false);
    dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD));

    cal.setTime(new Date());

    monthNames = new DateFormatSymbols(Locale.FRANCE).getMonths();
    dateLabel.setText(monthNames[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR));

    dateBar = new DateBar();

    GemPanel bottom = new GemPanel();
    bottom.setLayout(new BorderLayout());
    bottom.add(status, BorderLayout.NORTH);
    bottom.add(dateBar, BorderLayout.CENTER);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(dateLabel, 0, 0, 1, 1, GridBagHelper.WEST);
    if (choice != null) {
      gb.add(choice, 1, 0, 1, 1, GridBagHelper.WEST);
    }
    gb.add(view, 0, 1, 2, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(bottom, 0, 2, 2, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
  }

  public void addActionListener(ActionListener l) {
    dateBar.addActionListener(l);
    view.addActionListener(l);
  }

  public void removeActionListener(ActionListener l) {
    dateBar.removeActionListener(l);
    view.removeActionListener(l);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() instanceof JComboBox) {
      load();
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    List<? extends ScheduleObject> v = (List<? extends ScheduleObject>) evt.getNewValue();
    //System.out.println("MonthPlanDetailView.propertyChange: "+evt.getPropertyName()+"  v.size:"+v.size());
    if ("planning".equals(evt.getPropertyName())) {
      schedules = (List<ScheduleObject>) v;
      cal.setTime(((MonthSchedule) evt.getSource()).getStart());
      load();
    } else if ("plage".equals(evt.getPropertyName())) {
      ranges = (List<ScheduleRangeObject>) v;
      load();
    }

  }

  public void load() {
    load(cal.getTime(), schedules, ranges);
  }

  public void load(Date date, List<ScheduleObject> _schedules, List<ScheduleRangeObject> _ranges) {
    //System.out.println("MonthPlanDetailView.load: PL:"+_plannings.size()+" PG:"+plages.size());
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);

    dateLabel.setText(monthNames[month] + " " + year);
    dateBar.setDate(date);
    this.schedules = _schedules;
    this.ranges = _ranges;

    EventQueue.invokeLater(new Runnable()
    {
      public void run() {
        List<ScheduleObject> v1 = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
          ScheduleObject p = schedules.get(i);
          if (isNotFiltered(p)) {
            v1.add(p);
          }
        }
        List<ScheduleRangeObject> v2 = new ArrayList<>();
        for (int i = 0; i < ranges.size(); i++) {
          ScheduleRangeObject p = (ScheduleRangeObject) ranges.get(i);
          if (isNotFiltered(p)) {
            v2.add(p);
          }
        }
        view.load(cal.getTime(), v1, v2);
      }
    });

  }

  public MonthPlanView getCanvas() {
    return view;
  }

  public abstract boolean isNotFiltered(ScheduleObject p);

  public abstract void detailChange(ScheduleDetailEvent evt);
}
