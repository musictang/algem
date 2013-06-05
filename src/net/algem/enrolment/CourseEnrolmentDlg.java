/*
 * @(#)CourseEnrolmentDlg.java	2.8.g 31/05/13
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
package net.algem.enrolment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.Course;
import net.algem.course.CourseModuleInfo;
import net.algem.planning.*;
import net.algem.room.EstabChoice;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.model.SQLkey;
import net.algem.util.model.SqlList;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;


/**
 * Dialog for course choice.
 * Dialog for choosing a course during enrolment process
 * and defining a time slot within available schedules.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.g
 * @since 1.0a 07/07/1999
 */
public class CourseEnrolmentDlg
        implements ActionListener, ListSelectionListener, ItemListener {

  private DataCache dataCache;
  private GemBorderPanel bgPanel;
  private int code = 0;
  private CourseModuleInfo courseInfo;
  
  /** Combo box establishment. */
  private GemChoice estabChoice;
  
  private GemField module; // non visible
  private GemField startEnd;
  
  /** Course list. */
  private SqlList courseList;
  
  /** Avalaible days for course selection (page 2). */
  private JComboBox cbDay;
  
  /** Busy time slot list (page 2). */
  private SqlList range;
  
  /** 
   * Beginning time. 
   * Editable field with checking (page 2).
   */
  private HourField hour;

  /** Time slot duration. (page 2). */
  private HourField courseLength;

  private int roomId;
  
  private TeacherChoice teacher;// (a supprimer ?)

  /** Room info field. */
  private GemField roomInfo;
  
  private CourseOrder courseOrder;
  
  private Course course;

  private Vector<ScheduleRange> pl;	//Plage
  
  private Vector<HourRange> vph;	//Plage horaire
  
  private int estab;
  
  private Frame parent;
  
  private JDialog dlg;
  
  /** Page for course choice. */
  private GemPanel page1;
  
  /** Modification page for time slot. */
  private GemPanel page2;
  
  private boolean validation; 
  private GemButton btNext;
  private int memberId;
  private EnrolmentService service;
  private PlanningService pService;

  public CourseEnrolmentDlg(GemDesktop desktop, EnrolmentService s, int adh) {
    parent = desktop.getFrame();
    // horizontal label at the top of the window
    GemLabel tl = new GemLabel(BundleUtil.getLabel("Course.modification.label"));
    validation = false;
    dataCache = desktop.getDataCache();
    service = s;
    pService = new PlanningService(dataCache.getDataConnection());
    memberId = adh;

    estabChoice = new EstabChoice(dataCache.getList(Model.Establishment));
    module = new GemField();
    courseList = new SqlList(5, dataCache);// 5 lignes visibles
    cbDay = new JComboBox();
    startEnd = new GemField(24);

    range = new SqlList(8, dataCache);
    hour = new HourField();
    courseLength = new HourField();
    teacher = new TeacherChoice(dataCache.getList(Model.Teacher));
    roomInfo = new GemField();
    roomInfo.setEditable(false);

    teacher.setEditable(false);
    teacher.setEnabled(false);//1.1d
    courseLength.setEditable(false);
    cbDay.setEditable(false);

    page1 = new GemBorderPanel();
    page1.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(page1);
    Insets padding = new Insets(10, 2, 10, 0);
    Insets padding2 = new Insets(0, 2, 0, 0);
    gb.add(new GemLabel(BundleUtil.getLabel("Place.label")), 0, 0, 1, 1, padding2, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 1, 1, 1, padding, GridBagHelper.NORTHWEST);
    gb.add(estabChoice, 1, 0, 1, 1, padding, GridBagHelper.HORIZONTAL, 0.0, 0.0);
    gb.add(new JScrollPane(courseList), 1, 1, 1, 1, padding, GridBagHelper.BOTH, 1.0, 1.0);

    page2 = new GemBorderPanel();
    page2.setLayout(new GridBagLayout());
    gb = new GridBagHelper(page2);
    gb.add(new GemLabel(BundleUtil.getLabel("Day.label")), 0, 0, 1, 1, padding2, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")), 0, 1, 1, 1, padding2, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 2, 1, 1, padding2, GridBagHelper.WEST);
    gb.add(new GemLabel("<html>"+BundleUtil.getLabel("Range.occupation.label")+"</html>"), 0, 3, 1, 1, padding, GridBagHelper.NORTHWEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 4, 1, 1, padding2, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Duration.label")), 0, 5, 1, 1, padding2, GridBagHelper.WEST);


    gb.add(cbDay, 1, 0, 1, 1, padding2, GridBagHelper.HORIZONTAL, 0.0, 0.0);
    gb.add(teacher, 1, 1, 1, 1, padding2, GridBagHelper.HORIZONTAL, 0.0, 0.0);
    gb.add(roomInfo, 1, 2, 1, 1, padding2, GridBagHelper.HORIZONTAL, 0.0, 0.0);
    gb.add(new JScrollPane(range), 1, 3, 1, 1, padding, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(hour, 1, 4, 1, 1, padding2, GridBagHelper.HORIZONTAL, 0.0, 0.0);
    gb.add(courseLength, 1, 5, 1, 1, padding2, GridBagHelper.HORIZONTAL, 0.0, 0.0);


    bgPanel = new GemBorderPanel();
    bgPanel.setLayout(new CardLayout());

    bgPanel.add("page1", page1);
    bgPanel.add("page2", page2);

    dlg = new JDialog(parent, true);//1.1d le false désactive la validation

    GemButton btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    btNext = new GemButton(GemCommand.NEXT_CMD);
    btNext.addActionListener(this);
    GemButton btPrevious = new GemButton(GemCommand.BACK_CMD);
    btPrevious.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btPrevious);
    buttons.add(btCancel);
    buttons.add(btNext);

    dlg.getContentPane().add(tl, BorderLayout.NORTH);
    dlg.getContentPane().add(bgPanel, BorderLayout.CENTER);
    dlg.getContentPane().add(buttons, BorderLayout.SOUTH);

    dlg.setSize(340, 400);
    dlg.setTitle(BundleUtil.getLabel("Enrolment.label"));
    dlg.setLocation(100, 100);

    estabChoice.addItemListener(this);
    cbDay.addItemListener(this);
    courseList.addListSelectionListener(this);
  }

  public CourseEnrolmentDlg(GemDesktop d, EnrolmentService s) {
    this(d, s, 0);
  }

  public String getField(int n) {
    DayRange pg = (DayRange) cbDay.getSelectedItem();
    switch (n) {
      case 0:
        //XXXreturn place.getLabel();//OK
        break;
      case 1:
        return module.getText();
      case 2:
        return String.valueOf(pg.getPlanning().getIdAction());
      case 3:
        return courseList.getLabel();
      case 4:
        return String.valueOf(pg.getDay());
      case 5:
        return hour.getText();
      case 6:
        return courseLength.getText();
      case 7:
        return pg.getPlanning().getDate().toString();
    }
    return null;
  }

  public void entry() {
    dlg.setVisible(true);
  }

  public boolean isValidation() {
    return validation;
  }

  public void setCode(int c) {
    code = c;
  }

  public Course getCourse() {
    return course;
  }
  
  int getEstab() {
    return estabChoice.getKey();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (GemCommand.NEXT_CMD.equals(cmd)) {// affichage de la page 2
      if (courseList.getKey() >= 0) {
        ((CardLayout) bgPanel.getLayout()).show(bgPanel, "page2");
        btNext.setText(GemCommand.OK_CMD);
      }
    } else if (GemCommand.BACK_CMD.equals(cmd)) {
      ((CardLayout) bgPanel.getLayout()).show(bgPanel, "page1");
      btNext.setText(GemCommand.NEXT_CMD);
    } else if (GemCommand.OK_CMD.equals(cmd)) {
      if (!isEntryValid()) {
        return;
      }
      validation = true;
      dlg.setVisible(false);
    } else if (GemCommand.CANCEL_CMD.equals(cmd)) {
      validation = false;
      dlg.setVisible(false);
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent evt) {
    if (evt.getSource() == courseList) {
      loadCourse(courseList.getKey(), estabChoice.getKey());
      //((CardLayout)fond.getLayout()).show(fond,"page2");
    }
  }

  public void setRoomId(int salle_id) {
    roomId = salle_id;
  }

  public int getRoomId() {
    return roomId;
  }

  public void setCourseInfo(CourseModuleInfo courseInfo) {
    this.courseInfo = courseInfo;
  }

  /**
   * Displays the occupied time slots for a specific date schedule range.
   * 
   */
  void loadDay() {
    DayRange pj = (DayRange) cbDay.getSelectedItem();
    // Bug fixed
    if (pj == null) {
      return;
    }
    Schedule p = pj.getPlanning();
    teacher.setKey(p.getIdPerson());
    Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(p.getPlace());
    setRoomId(s.getId()); // récupération du numéro (id) de salle
    roomInfo.setText(s.toString());// affichage de la salle
    hour.setText(p.getStart().toString());

    // durée independante pour les ateliers découverte et les cours d'instrument collectif
    if (p.getType() == Schedule.WORKSHOP_SCHEDULE || course.isCourseCoInst()) {
      courseLength.setText(new Hour(p.getStart().getLength(p.getEnd())).toString());
    }

    range.removeAll();

    if (course.isCollective()) {
      return;
    }
    pl = service.getBusyTimeSlot(
        p.getIdAction(),
        course.getId(),
        courseOrder.getDateStart().toString(),
        courseOrder.getDateEnd().toString()
    );
    // liste des plages occupées
    range.setListData(pl);
  }

  /**
   * Runs when establishment or course is modified in combo boxes.
   *
   * @param evt
   */
  @Override
  public void itemStateChanged(ItemEvent evt) {
//    System.out.println("CourseEnrolmentDlg.itemStateChanged:"+evt);
    if (evt.getStateChange() == ItemEvent.SELECTED) {
      if (evt.getSource() == estabChoice) {
        try {
          loadEstab(estabChoice.getKey());       
        } catch (SQLException ex) {
          System.err.println(ex.getMessage());
        }
      } else if (evt.getSource() == cbDay) {
        loadDay();
      }
    }
  }

  /**
   * Sets the course characteristics for the course order {@code co}.
   *
   * @param co course order
   */
  public void loadEnrolment(CourseOrder co) throws EnrolmentException {

    courseOrder = co;
    
    if (co.getEstab() <= 0) {
      co.setEstab(getDefaultEstab());
    }

    try {
      loadEstab(co.getEstab());
      module.setText(String.valueOf(co.getModuleOrder()));
      hour.setText(co.getStart().toString());
      courseLength.setText(new Hour(co.getStart().getLength(co.getEnd())).toString());
    } catch (SQLException sqe) {
      throw new EnrolmentException(MessageUtil.getMessage("enrolment.loading.exception") + " :\n" + sqe.getMessage());
    }
  }

  private int getDefaultEstab() {

    try {
      return Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey(), dataCache.getDataConnection()));
    } catch (NumberFormatException nfe) {
      GemLogger.log(nfe.getMessage());
    }
    return 0;
  }

  /**
   * Loads the list of available courses in the establishment for the period.
   * ~ : regex matching case sensitive
   * ~* : regex matching case unsensitive
   *
   * @param id establishment id
   */
  public void loadEstab(int id) throws SQLException {

    courseClear();

    estabChoice.setKey(id);
//    courseOrder.setEstab(id);
    // tous les cours d'un type, d'un code et à partir d'une date spécifiés pour un établissement donné.
    Vector<SQLkey> sqlist = service.getCoursesFromEstab(id, courseOrder.getDateStart(), courseInfo);
    courseList.loadSQL(sqlist); // recupere id et title des cours
  }

  /**
   * Loads the course {@code id}.
   * @param id
   * @param estab 
   */
  private void loadCourse(int id, int estab) {//TODOGEM +etab
    Course c = null;
    try {
      c = (Course) DataCache.findId(id, Model.Course);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#loadCours :" + ex.getMessage());
    }
    if (c == null) {
      return;
    }
    loadCourse(c, estab);
  }

  /**
   * Loads the list of days when the course {@code c} is scheduled.
   *
   * @param c the course
   * @param estab estab id
   */
  private void loadCourse(Course c, int estab) //TODOGEM +etab
  {
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    course = c;
    //boolean isAtP = "ATP".equalsIgnoreCase(cours.getCode());
    Vector<Schedule> v = null;
    cbDay.removeAllItems();
    v = service.getCourseWeek(course, courseOrder.getDateStart(), estab);

    if (v == null || v.isEmpty()) {
      return;
    }

    Schedule po = new Schedule();// permet d'éviter de compter deux fois le même cours

    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      if (p.getIdAction() == po.getIdAction() && p.getDate().equals(po.getDate())) {
        continue;
      }
      po = p;
      cal.setTime(p.getDate().getDate());
      StringBuilder bf = new StringBuilder();
      
      HourRange ph = new HourRange(p.getStart(), p.getEnd());
        if (c.isATP()) {
          bf.append(p.getDate().toString());
        } else {
          bf.append(ph.getStart().toString());
          bf.append("-");
          bf.append(ph.getEnd().toString());
        }
      // recherche du nom du professeur
      bf.append(service.getTeacher(p.getIdPerson()));
      // ajout des jours dans la combobox date
      cbDay.addItem(new DayRange(p, bf.toString()));
    }
    loadDay();

  }

  public void clear() {
    ((CardLayout) bgPanel.getLayout()).show(bgPanel, "page1");
    estabChoice.setSelectedIndex(0);
    courseClear();
    btNext.setText(GemCommand.NEXT_CMD);
  }

  public void courseClear() {
    courseList.removeAll();
    range.removeAll();
    pl = null;
    course = null;
    hour.setText("00:00");
    startEnd.setText("");
    //duree.setText("00:00"); // IMPORTANT ! la durée ne doit pas être remise à 0
    teacher.setSelectedIndex(0);
    //salle.setSelectedIndex(0);
    cbDay.removeAllItems();
  }

  /**
   * Checks the validity of time slot entry.
   */
  public boolean isEntryValid() {
    try {
      if (estabChoice.getKey() == 0 || course == null) {
        throw new EnrolmentException(MessageUtil.getMessage("invalid.course.selection"));
      }

      if (hour.getText().equals(Hour.NULL_HOUR) 
              || courseLength.get().toString().equals(Hour.NULL_HOUR)) {
        throw new EnrolmentException(MessageUtil.getMessage("invalid.time.slot"));
      }

      Hour rangeStart = hour.get();
      Hour rangeEnd = rangeStart.end(courseLength.get());

      DayRange pj = (DayRange) cbDay.getSelectedItem();

      //vérification adhérent déjà inscrit à un atelier
      if (Course.ATP_CODE == code || course.isCollective()) {
        if (service.isOnRange(memberId, pj.getPlanning(), courseOrder)) {
          throw new EnrolmentException(MessageUtil.getMessage("member.enrolment.existing.range"));
        }
      }
      // pas de vérification des chevauchements de plages pour les plannings de type atelier.
      if (pj.getPlanning().getType() == Schedule.WORKSHOP_SCHEDULE) {
        return true;
      }
      
      // time slot must be included in schedule
      Schedule p = pj.getPlanning();
      if (!rangeStart.between(p.getStart(), p.getEnd()) || !rangeEnd.between(p.getStart(), p.getEnd())) {
        throw new EnrolmentException(MessageUtil.getMessage("time.slot.out.of.schedule"));
      }
      
      // Vérification nombre de places pour les cours de type collectif.
      Action a = pService.getAction(p.getIdAction());
      if (!isFree(a, courseOrder, service)) {
        throw new EnrolmentException(MessageUtil.getMessage("max.place.number.warning"));
      }

      if (pl != null) {
        Enumeration<ScheduleRange> enu = pl.elements();
        while (enu.hasMoreElements()) {
          ScheduleRange occup = enu.nextElement();
          if (rangeStart.ge(occup.getStart()) && rangeStart.lt(occup.getEnd())
                  || rangeEnd.gt(occup.getStart()) && rangeEnd.le(occup.getEnd())
                  || rangeStart.ge(occup.getStart()) && rangeEnd.le(occup.getEnd())
                  || rangeStart.lt(occup.getStart()) && rangeEnd.gt(occup.getEnd())
                  ) {
            throw new EnrolmentException(MessageUtil.getMessage("time.slot.conflict"));
          }
        }
      }
      
      
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return false;
    } catch (EnrolmentException iex) {
      MessagePopup.error(dlg, iex.getMessage());
      return false;
    }

    return true;
  }

  /**
   * Checks if the max number of places is reached.
   * @param a action schedule
   * @param c course order
   * @param service enrolment service
   * @return true if there is enough places
   * @throws SQLException 
   */
  private boolean isFree(Action a, CourseOrder c, EnrolmentService service) throws SQLException {

    int n = service.getPlaceNumber(a.getId(), c.getDateStart(), c.getDateEnd()) + 1;

    if (a.getPlaces() > 0 && a.getPlaces() < n) {
      return false;
    }
    return true;
  }
  
}

class DayRange {

  static final String[] dayLabels = PlanningService.WEEK_DAYS;
  private Schedule plan;
  private int dayOfWeek;
  private String range;
  
  DayRange(Schedule p, String l) {
    plan = p;
    range = l;
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(p.getDate().getDate());
    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
  }

  int getDay() {
    return dayOfWeek - 1;
  }

  String getRange() {
    return range;
  }

  Schedule getPlanning() {
    return plan;
  }

  @Override
  public String toString() {
    return dayLabels[dayOfWeek] + " " + range;//+" "+teacherName;
  }
  
  
}
