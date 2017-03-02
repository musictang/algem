/*
 * @(#)ActionView.java	2.12.0 01/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import net.algem.config.ColorPlan;
import net.algem.config.ColorPlanListener;
import net.algem.config.ColorPrefs;
import net.algem.config.ParamChoice;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.contact.teacher.TeacherEvent;
import net.algem.course.Course;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceActiveModel;
import net.algem.course.CourseCodeType;
import net.algem.planning.day.DayChoice;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * View for course planification.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class ActionView
        extends GemPanel
        implements GemEventListener
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
  protected ParamChoice vacancy;
  protected JComboBox periodicity;
  protected HourField courseLength;
  protected GemNumericField intervall;
  protected GemPanel colorPanel;
  protected int defaultBgColor;
  protected ColorPrefs colorPrefs;

  public ActionView(GemDesktop desktop) {

    this.desktop = desktop;
    this.colorPrefs = new ColorPrefs();
    dataCache = desktop.getDataCache();
    GemList<Course> courseList = dataCache.getList(Model.Course);
    course = new CourseChoice(new CourseChoiceActiveModel(courseList, true));
    course.addItemListener(new CourseScheduleItemListener());
    datePanel = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfYear());
    hourPanel = new HourRangePanel(new Hour("16:00"), new Hour("17:00"));
    GemList<Teacher> teacherList = dataCache.getList(Model.Teacher);
    teacher = new TeacherChoice(teacherList, true);
    if (teacherList.getSize() > 0) {
      teacher.setSelectedIndex(0);
    }
    room = new RoomChoice(dataCache.getList(Model.Room));//salles actives par défaut
    room.addItemListener(new RoomItemListener());
    day = new DayChoice();
    periodicity = new JComboBox(new Enum[]{Periodicity.WEEK, Periodicity.FORTNIGHT, Periodicity.DAY, Periodicity.MONTH});
    sessions = new GemNumericField(2);
    sessions.setText("33");
    places = new GemNumericField(2);
    colorPanel = new GemPanel();
    colorPanel.setToolTipText(BundleUtil.getLabel("Scheduling.color.tip"));

    colorPanel.addMouseListener(new ColorPlanListener());
    vacancy = new ParamChoice(dataCache.getVacancyCat());
    courseLength = new HourField();
    intervall = new GemNumericField(2);
    if (courseList.getSize() > 0) {
      load(((Course) course.getSelectedItem()));
    }

  }

  private void load(Course c) {
    courseLength.setEditable(c.isCourseCoInst());
    intervall.setEditable(c.isCourseCoInst());
    if (c.isCollective()) {
      //places.setText(String.valueOf(((Room)room.getSelectedItem()).getNPers()));
      places.setEditable(true);
    } else {
      places.setText("");
      places.setEditable(false);
    }
    setColor(getColor(c));
  }

  public void init() {

    desktop.addGemEventListener(this);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Day.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Periodicity.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Sessions.label")), 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Menu.holidays.label")), 0, 8, 1, 1, GridBagHelper.WEST);

    Dimension d = new Dimension(datePanel.getPreferredSize().width, course.getPreferredSize().height);
    course.setPreferredSize(d);
    gb.add(course, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 1, 1, 1, 1, GridBagHelper.NORTHWEST);
    day.setPreferredSize(d);
    gb.add(day, 1, 2, 1, 1, GridBagHelper.WEST);
    periodicity.setPreferredSize(d);
    gb.add(periodicity, 1, 3, 1, 1, GridBagHelper.WEST);

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

    gb.add(hp, 1, 4, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    teacher.setPreferredSize(d);
    gb.add(teacher, 1, 5, 1, 1, GridBagHelper.WEST);
    room.setPreferredSize(d);
    gb.add(room, 1, 6, 2, 1, GridBagHelper.WEST);

    GemPanel s = new GemPanel();
    s.setLayout(new BoxLayout(s, BoxLayout.X_AXIS));
    s.add(sessions);
    s.add(Box.createHorizontalStrut(4));
    s.add(new GemLabel(BundleUtil.getLabel("Place.number.label")));
    s.add(Box.createHorizontalStrut(4));
    s.add(places);
    s.add(Box.createHorizontalStrut(4));
    s.add(new GemLabel(BundleUtil.getLabel("Color.label")));
    s.add(Box.createHorizontalStrut(4));
    colorPanel.setPreferredSize(places.getPreferredSize());
    colorPanel.setBorder(places.getBorder());
    s.add(colorPanel);

    gb.add(s, 1, 7, 1, 1, GridBagHelper.WEST);
    vacancy.setPreferredSize(d);
    gb.add(vacancy, 1, 8, 1, 1, GridBagHelper.WEST);
  }

  public Action get() {
    Action a = new Action();

    a.setCourse(course.getKey());
    a.setStartDate(datePanel.getStartFr());
    a.setEndDate(datePanel.getEndFr());
    a.setStartTime(hourPanel.getStart());
    a.setEndTime(hourPanel.getEnd());
    a.setLength(getCourseLength());
    a.setIdper(teacher.getKey());
    a.setRoom(room.getKey());
    a.setDay(day.getKey());
    a.setVacancy(vacancy.getKey());
    a.setPeriodicity((Periodicity) periodicity.getSelectedItem());
    try {
      a.setNSessions((short) Integer.parseInt(sessions.getText()));
    } catch (NumberFormatException e) {
      a.setNSessions((short) 1);
    }
    a.setPlaces(getPlaces());
    int color = colorPanel.getBackground().getRGB();
    if (defaultBgColor != color) {
      a.setColor(color);
    }

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
    return length >= 10;
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

  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof TeacherEvent && evt.getOperation() == GemEvent.CREATION) {
      ((GemChoiceFilterModel) teacher.getModel()).load(dataCache.getList(Model.Teacher));
    }
  }

  class CourseScheduleItemListener implements ItemListener {
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
      setColor(getColor(c));
    }
  }

  private Color getColor(Course c) {
    int code = c.getCode();
    if (CourseCodeType.INS.getId() == code) {
      if (c.isCollective()) {
        return colorPrefs.getColor(ColorPlan.INSTRUMENT_CO);
      } else {
        return colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
      }
    }
    if (CourseCodeType.ATL.getId() == code) {
      return colorPrefs.getColor(ColorPlan.COURSE_CO);
    }
     if (CourseCodeType.FMU.getId() == code) {
      return colorPrefs.getColor(ColorPlan.COURSE_CO);
    }
    if (CourseCodeType.ATP.getId() == code) {
      return colorPrefs.getColor(ColorPlan.WORKSHOP);
    }
    if (CourseCodeType.STG.getId() == code) {
      return colorPrefs.getColor(ColorPlan.TRAINING);
    }
    return null;

  }

  private void setColor(Color c) {
    if (c != null) {
      colorPanel.setBackground(c);
      defaultBgColor = colorPanel.getBackground().getRGB();
    }
  }

  /**
   * Room item state changed listener.
   *
   */
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
//        places.setText(n == 0 || !c.isCollective() ? "0" : String.valueOf(n));
        int p = getPlaces();
        if (c.isCollective() && p > 0 && n > 0 && p > n) {
          if (!MessagePopup.confirm(null, MessageUtil.getMessage("max.room.place.planification.warning"))) {
            places.setText(String.valueOf(n));
          }
        }
      }
    }

  }
}
