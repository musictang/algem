/*
 * @(#)ActionView.java	2.8.k 23/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import net.algem.config.ParamChoice;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.Course;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceActiveModel;
import net.algem.planning.day.DayChoice;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemNumericField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * View for course planification.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 */
public class ActionView
        extends GemPanel
{

  protected DataCache dataCache;
  protected GemDesktop desktop;
  protected CourseChoice course;
  protected DateRangePanel datePanel;
  protected HourRangePanel hourPanel;
  protected TeacherChoice teacher;
  protected RoomChoice room;
  protected DayChoice day;
  protected GemNumericField sessions;
  protected GemNumericField places;
  protected ParamChoice vChoix;
  protected JComboBox periodicity;
  protected HourField courseLength; 
  protected GemNumericField intervall;

  public ActionView(GemDesktop desktop) {

    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    course = new CourseChoice(new CourseChoiceActiveModel(dataCache.getList(Model.Course), true));//modification 1.1d ajout d'un filtre
    course.addItemListener(new CourseCoItemListener());
    datePanel = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfYear());
    hourPanel = new HourRangePanel();
    teacher = new TeacherChoice(dataCache.getList(Model.Teacher));
    teacher.setSelectedIndex(0);
    room = new RoomChoice(dataCache.getList(Model.Room));//salles actives par défaut
    room.addItemListener(new RoomItemListener());
    day = new DayChoice();
    periodicity = new JComboBox(new Enum[]{Periodicity.SEMAINE, Periodicity.QUINZAINE, Periodicity.JOUR, Periodicity.MOIS});
    sessions = new GemNumericField(2);
    places = new GemNumericField(2);
    places.setText(String.valueOf(((Room)room.getSelectedItem()).getNPers()));
    vChoix = new ParamChoice(dataCache.getVacancyCat());
    courseLength = new HourField();
    intervall = new GemNumericField(2);
    load(((Course) course.getSelectedItem()));
  }
  
  private void load(Course c) {
    courseLength.setEditable(c.isCourseCoInst());
    intervall.setEditable(c.isCourseCoInst());
    if (c.isCollective()) {
      places.setText(String.valueOf(((Room)room.getSelectedItem()).getNPers()));
      places.setEditable(true);
    } else {
      places.setText("0");
      places.setEditable(false);
    }
  }

  public void init() {

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Day.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Periodicity.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 6, 1, 1, GridBagHelper.WEST);    
    gb.add(new GemLabel(BundleUtil.getLabel("Sessions.label")), 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Menu.holidays.label")), 0, 8, 1, 1, GridBagHelper.WEST);

    gb.add(course, 1, 0, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(datePanel, 1, 1, 1, 1, GridBagHelper.NORTHWEST);
    gb.add(day, 1, 2, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(periodicity, 1, 3, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);

    GemPanel hp = new GemPanel(new BorderLayout());
    hp.setBorder(null);
    hp.add(hourPanel, BorderLayout.WEST);
    
    GemPanel p = new GemPanel();
    p.add(new GemLabel(BundleUtil.getLabel("Course.length.label")));
    courseLength.setToolTipText(BundleUtil.getLabel("Course.length.tip"));
    p.add(courseLength);
    p.add(new GemLabel(BundleUtil.getLabel("Course.interval.label")));
    intervall.setToolTipText(BundleUtil.getLabel("Course.interval.tip"));
    p.add(intervall);
    hp.add(p, BorderLayout.EAST);

    gb.add(hp, 1, 4, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(teacher, 1, 5, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(room, 1, 6, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    
    GemPanel s = new GemPanel();
    s.setLayout(new BoxLayout(s, BoxLayout.X_AXIS));
    s.add(sessions);
    s.add(Box.createHorizontalStrut(4));
    s.add(new GemLabel(BundleUtil.getLabel("Place.number.label")));
    s.add(Box.createHorizontalStrut(4));
    s.add(places);
    
    gb.add(s, 1, 7, 1, 1, GridBagHelper.WEST);
    gb.add(vChoix, 1, 8, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
  }

  public Action get() {
    Action a = new Action();

    a.setCourse(course.getKey());
    a.setDateStart(datePanel.getStartFr());
    a.setDateEnd(datePanel.getEndFr());
    a.setHourStart(hourPanel.getStart());
    a.setHourEnd(hourPanel.getEnd());
    a.setLength(getCourseLength());
    a.setTeacher(teacher.getKey());
    a.setRoom(room.getKey());
    a.setDay(day.getDay());
    a.setVacancy(vChoix.getKey());
    a.setPeriodicity((Periodicity) periodicity.getSelectedItem());
    try {
      a.setNSessions((short) Integer.parseInt(sessions.getText()));
    } catch (NumberFormatException e) {
      a.setNSessions((short) 1);
    }
    a.setPlaces(getPlaces());
    
    return a;
  }

  /**
   * Gets the selected course.
   *
   * @return a course
   */
  Course getCourse() {
    return (Course) course.getSelectedItem();
  }

  /**
   * Gets the course's length entered in field.
   *
   * @return a course's length in minutes
   */
  int getCourseLength() {
    return courseLength.get().toMinutes();
  }

  /**
   * Gets the intervall between two schedules for collective instrument batch planification.
   *
   * @return a number of minutes
   */
  int getIntervall() {
    try {
      return Integer.parseInt(intervall.getText());
    } catch (NumberFormatException nfe) {
      return 0;
    }
  }
  
  short getPlaces() {
    try {
      return Short.parseShort(places.getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Checks if the time range is valid.
   * 
   * @return true if range length >= 10
   */
  boolean hasValidLength() {
    int length = hourPanel.getStart().getLength(hourPanel.getEnd());
    return length < 10 ? false : true;
  }

  public void clear() {
    datePanel.setStart(dataCache.getStartOfYear());
    datePanel.setEnd(dataCache.getEndOfYear());
    hourPanel.clear();
    course.setSelectedIndex(0);
    teacher.setSelectedIndex(0);
    room.setSelectedIndex(0);
    day.setSelectedIndex(0);
    periodicity.setSelectedIndex(0);
    sessions.setText("");
    courseLength.set(new Hour());
    intervall.setText(null);
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  class CourseCoItemListener implements ItemListener {
    // This method is called only if a new item has been selected.

    @Override
    public void itemStateChanged(ItemEvent evt) {
      // Get the affected item
      Course c = (Course) evt.getItem();
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        load(c);
      } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
        courseLength.setText(null);
      }
    }
  }
  
  class RoomItemListener implements ItemListener {
    /**
     * Gets and diplays the maximum capacity of this room.
     * @param e 
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
      
      if (e.getStateChange() == ItemEvent.SELECTED) {
        int n = ((Room) e.getItem()).getNPers();
        Course c = (Course) course.getSelectedItem();
        places.setText(n == 0 || !c.isCollective() ? "0" : String.valueOf(n));
      } else if (e.getStateChange() == ItemEvent.DESELECTED) {
        places.setText(null);
      }
    }
    
  }
}
