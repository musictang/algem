/*
 * @(#)CourseEnrolmentDlg.java	2.7.a 03/12/12
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
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.Course;
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
 * Dialog for choosing a course during enrolment processus
 * and defining a time slot within available schedules.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class CourseEnrolmentDlg
        implements ActionListener, ListSelectionListener, ItemListener {

  private DataCache dataCache;
  private GemBorderPanel bgPanel;
  private String code = "";
  
  /** Combo box establishment. */
  private GemChoice place;
  
  private GemField module; // non visible
  private GemField startEnd;
  
  /** Course list. */
  private SqlList courseList;

  
  /** Avalaible days for course selection (page 2). */
  private JComboBox cbDay;
  
  /** Busy time slot list (page 2). */
  private SqlList range;
  
  /** Beginning time. Champ éditable soumis à vérification (page 2). */
  private HourField hour;

  /** Time slot duration. (page 2). */
  private HourField duration;

  private int roomId;
  
  private TeacherChoice teacher;// (a supprimer ?)

  /** Room info field. */
  private GemField roomInfo;
  
  private CourseOrder courseOrder;
  
  private Course course;

  private Vector<ScheduleRange> pl;	//Plage
  
  private Vector<HourRange> vph;	//Plage horaire
  
  private int currentPlace;
  
  private Frame parent;
  
  private JDialog dlg;
  
  private GemLabel title;
  
  /** Page for course choice. */
  private GemPanel page1;
  
  /** Modification page for time slot. */
  private GemPanel page2;
  
  private boolean validation; 
  private GemButton btCancel;
  private GemButton btNext;
  private GemButton btPrevious;
  private int memberId;
  private EnrolmentService service;
  private PlanningService pService;

  public CourseEnrolmentDlg(GemDesktop desktop, EnrolmentService s, int adh) {
    parent = desktop.getFrame();
    // horizontal label at the top of the window
    GemLabel tl = new GemLabel("modification cours");
    validation = false;
    dataCache = desktop.getDataCache();
    service = s;
    pService = new PlanningService(dataCache.getDataConnection());
    memberId = adh;

    place = new EstabChoice(dataCache.getList(Model.Establishment));
    module = new GemField();
    courseList = new SqlList(5, dataCache);// 5 lignes visibles
    cbDay = new JComboBox();
    startEnd = new GemField(24);

    range = new SqlList(8, dataCache);
    hour = new HourField();
    duration = new HourField();
    teacher = new TeacherChoice(dataCache.getList(Model.Teacher));
    roomInfo = new GemField();
    roomInfo.setEditable(false);

    teacher.setEditable(false);
    teacher.setEnabled(false);//1.1d
    duration.setEditable(false);
    cbDay.setEditable(false);

    page1 = new GemBorderPanel();
    page1.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(page1);
    Insets padding = new Insets(10, 2, 10, 0);
    Insets padding2 = new Insets(0, 2, 0, 0);
    //Insets padding3 = new Insets(10,2,10,0);
    gb.add(new GemLabel(BundleUtil.getLabel("Place.label")), 0, 0, 1, 1, padding2, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 1, 1, 1, padding, GridBagHelper.NORTHWEST);
    gb.add(place, 1, 0, 1, 1, padding, GridBagHelper.HORIZONTAL, 0.0, 0.0);
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
    gb.add(duration, 1, 5, 1, 1, padding2, GridBagHelper.HORIZONTAL, 0.0, 0.0);


    bgPanel = new GemBorderPanel();
    bgPanel.setLayout(new CardLayout());

    bgPanel.add("page1", page1);
    bgPanel.add("page2", page2);

    dlg = new JDialog(parent, true);//1.1d le false désactive la validation

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    btNext = new GemButton(GemCommand.NEXT_CMD);
    btNext.addActionListener(this);
    btPrevious = new GemButton(GemCommand.BACK_CMD);
    btPrevious.addActionListener(this);

    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 3));
    boutons.add(btPrevious);
    boutons.add(btCancel);
    boutons.add(btNext);

    dlg.getContentPane().add(tl, BorderLayout.NORTH);
    dlg.getContentPane().add(bgPanel, BorderLayout.CENTER);
    dlg.getContentPane().add(boutons, BorderLayout.SOUTH);

    dlg.setSize(340, 400);
    dlg.setTitle("Inscription");
    dlg.setLocation(100, 100);

    place.addItemListener(this);
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
        //PgJour pg = (DayRange) day.getSelectedItem();
        return String.valueOf(pg.getDay());
      case 5:
        return hour.getText();
      case 6:
        return duration.getText();
      case 7:
        return pg.getWorkshopDay().toString();
    }
    return null;
  }

  public void entry() {
    dlg.setVisible(true);
  }

  public boolean isValid() {
    return validation;
  }

  public void setCode(String c) {
    code = c;
  }

  /*
   * public void set(PlanCours p) { //System.out.println("InscriptonCoursDlg.set
   * plan:"+p); coursListe.setKey(p.getCourseFromAction()); //
   * heure.setText(p.getStart().toString()); //
   * getLength.setText(p.getLength().toString()); prof.setKey(p.getTeacher());
   * //salle.setKey(p.getRoomOnPeriod());
  }
   */
  public Course getCourse() {
    return course;
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
      }  else if (GemCommand.CANCEL_CMD.equals(cmd)) {
      validation = false;
      dlg.setVisible(false);
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent evt) {
    if (evt.getSource() == courseList) {
      loadCourse(courseList.getKey(), place.getKey());
      //((CardLayout)fond.getLayout()).show(fond,"page2");
    }
  }

  public void setRoomId(int salle_id) {
    roomId = salle_id;
  }

  public int getRoomId() {
    return roomId;
  }

  /**
   * Displays the occupied time slots for a specific day schedule range.
   * 
   */
  void loadDay() {
    DayRange pj = (DayRange) cbDay.getSelectedItem();//planning day
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

    // getLength independante pour les ateliers découverte et les cours instrument collective.
    if (p.getType() == Schedule.WORKSHOP_SCHEDULE || course.isCourseCoInst()) {
      duration.setText(new Hour(p.getStart().getLength(p.getEnd())).toString());
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
      if (evt.getSource() == place) {
        try {
          loadEstab(place.getKey());
        } catch (SQLException ex) {
          System.err.println(ex.getMessage());
        }
      } else if (evt.getSource() == cbDay) {
        loadDay();
      }
    }
  }

  /**
   * Sets the course characteristics for the course order {@code cc}.
   *
   * @param cc commande cours
   */
  public void loadEnrolment(CourseOrder cc) throws EnrolmentException {
    System.out.println(".................................");
    System.out.println(cc.getAction());
    System.out.println(".................................");
    courseOrder = cc;
    try {
      Course c = service.getCourse(cc.getAction());
      if (c == null) {
        return;
      }
      Room s = service.getRoomOnPeriod(c.getId());
      if (s == null) {
        System.out.println("salle null");
        return;
      }
      System.out.println("ca passe");
      System.out.println(".................................");
      System.out.println("salle id " + s.getId());
      System.out.println("etablissement id " + s.getEstab());
      loadEstab(s.getEstab());
      /*
       * if (c.isUndefined()) { //coursListe.setSelectedIndex(0); c =
       * service.getCourseFromId(coursListe.getElementAt(0).getKey());
      }
       */
      courseList.setKey(c.getId());
      loadCourse(c, s.getEstab());

      module.setText(String.valueOf(cc.getModule()));
      hour.setText(cc.getStart().toString());

      duration.setText(new Hour(cc.getStart().getLength(cc.getEnd())).toString());
    } catch (SQLException e) {
      throw new EnrolmentException(MessageUtil.getMessage("enrolment.loading.exception") + " :\n" + e.getMessage());
    }
  }

  /**
   * Loads the list of available course in the establishment for the period.
   * ~ : regex matching case sensitive
   * ~* : regex matching case unsensitive
   *
   * @param id establishment id
   */
  public void loadEstab(int id) throws SQLException {

    int type = 0;
    courseClear();
    if (id != currentPlace) {
      currentPlace = id;
      place.setKey(id);
    }
    if (Course.ATP_CODE.equals(code.trim())) {
      type = Schedule.WORKSHOP_SCHEDULE;
    } else {
      type = Schedule.COURSE_SCHEDULE;
    }

    // tous les cours d'un type, d'un code et à partir d'une date spécifiés pour un établissement donné.
    Vector<SQLkey> sqlist = service.getCoursFromEtab(id, type, courseOrder.getDateStart(), code);
    courseList.loadSQL(sqlist); // recupere id et title des cours
  }

  /**
   * Loads the course {@code id}.
   * @param id
   * @param estab 
   */
  public void loadCourse(int id, int estab) {//TODOGEM +etab
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
  public void loadCourse(Course c, int estab) //TODOGEM +etab
  {
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    course = c;
    //boolean isAtP = "ATP".equalsIgnoreCase(cours.getCode());
    Vector<Schedule> v = null;
    cbDay.removeAllItems();
    v = service.getCourseWeek(course, courseOrder.getDateStart(), currentPlace);

    if (v == null || v.size() < 1) {
      return;
    }

    Schedule po = new Schedule();// permet d'éviter de compter deux fois le même cours

    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      if (p.getIdAction() == po.getIdAction() && p.getDay().equals(po.getDay())) {
        //p.getDay().equals(po.getDay()) && p.getIdPerson() == po.getIdPerson() && p.getLieux() == po.getLieux()) {
        continue;
      }
      po = p;
      cal.setTime(p.getDay().getDate());
      StringBuilder bf = new StringBuilder();
      /*
       * Boucle de recherche de tous les horaires de start et de end des
       * plannings pour un prof et un day donnés. les heures de end ou de début
       * peuvent différer de celles du planning officiel en raison de la
       * compression ou de l'extension possible du planning pour le day en
       * question.
       */
      try {
        vph = service.getPlageCours(p);
        /*
         * if (isAtP) { vph = dataCache.getPlageAteliers(p.getDay(),
         * p.getIdPerson(), p.getLieux()); } else { vph =
         * dataCache.getPlageCours(c.getId(), p.getDay(), p.getIdPerson(),
         * p.getLieux());
        }
         */
      } catch (EnrolmentException ie) {
        MessagePopup.warning(dlg, ie.getMessage());
        return;
      }
      for (int j = 0; j < vph.size(); j++) {
        HourRange ph = vph.elementAt(j);
        if (course.isATP()) {
          bf.append(p.getDay().toString());
        } else {
          bf.append(ph.getStart().toString());
          bf.append("-");
          bf.append(ph.getEnd().toString());
        }
        bf.append(" ");
      }
      // recherche du nom du professeur
      bf.append(service.getTeacher(p.getIdPerson()));
      // ajout des jours dans la combobox day
      cbDay.addItem(new DayRange(p, bf.toString()));
    }
    loadDay();

  }

  public void clear() {
    ((CardLayout) bgPanel.getLayout()).show(bgPanel, "page1");
    place.setSelectedIndex(0);
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
      if (place.getKey() == 0 || course == null) {
        throw new EnrolmentException(MessageUtil.getMessage("invalid.course.selection"));
      }

      if (hour.getText().equals("00:00") || duration.get().toString().equals("00:00") || vph == null || vph.size() < 1) {
        throw new EnrolmentException(MessageUtil.getMessage("invalid.time.slot"));
      }

      Hour deb = hour.get();
      Hour fin = deb.end(duration.get());
      boolean inplage = false;
      DayRange pj = (DayRange) cbDay.getSelectedItem();

      //vérification adhérent déjà inscrit à un atelier
      if (Course.ATP_CODE.equals(code)) {
        if (service.isOnRange(memberId, pj.getPlanning())) {
          throw new EnrolmentException(MessageUtil.getMessage("member.enrolment.existing.range"));
        }
      }
      // pas de vérification des chevauchements de plages pour les plannings de type atelier.
      if (pj.getPlanning().getType() == Schedule.WORKSHOP_SCHEDULE) {
        return true;
      }

      // vérification des conflits plage horaire.
      vph = service.getPlageCours(pj.getPlanning());
      for (int i = 0; i < vph.size(); i++) {
        HourRange ph = vph.elementAt(i);
        if (deb.ge(ph.getStart()) && fin.le(ph.getEnd())) {
          inplage = true;
          break;
        }
      }
      // Vérification tranche horaire
      if (!inplage) {
        throw new EnrolmentException(MessageUtil.getMessage("time.slot.out.of.range"));
      }

      // Vérification nombre de places pour les cours de type instrument collectif.
      Action a = pService.getAction(pj.getPlanning().getIdAction());
      if (!isFree(a, courseOrder, service)) {
        throw new EnrolmentException(MessageUtil.getMessage("max.place.number.warning"));
      }

      if (pl != null) {
        Enumeration<ScheduleRange> enu = pl.elements();
        while (enu.hasMoreElements()) {
          ScheduleRange p = enu.nextElement();
          if ((deb.ge(p.getStart()) && deb.lt(p.getEnd())) || (fin.gt(p.getStart()) && fin.le(p.getEnd()))) {
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

  private boolean isFree(Action a, CourseOrder c, EnrolmentService service) throws SQLException {

    int n = service.getPlaces(a.getId(), c.getDateStart(), c.getDateEnd()) + 1;

    if (a.getPlaces() > 0 && a.getPlaces() < n) {
      return false;
    }
    return true;
  }
}
class DayRange {

  static final String[] dayLabels = PlanningService.WEEK_DAYS;
  Schedule plan;
  int day;
  String teacherName;
  String range;
  DateFr workshopDay;
  Calendar cal = Calendar.getInstance(Locale.FRANCE);

  DayRange(Schedule p, String l) {
    plan = p;
    range = l;
    cal.setTime(p.getDay().getDate());
    day = cal.get(Calendar.DAY_OF_WEEK);
    if (p.getType() == Schedule.WORKSHOP_SCHEDULE) {
      workshopDay = p.getDay();
    }
  }

  int getDay() {
    return day - 1;
  }

  DateFr getWorkshopDay() {
    return workshopDay;
  }

  String getRange() {
    return range;
  }

  Schedule getPlanning() {
    return plan;
  }

  @Override
  public String toString() {
    return dayLabels[day] + " " + range;//+" "+teacherName;
  }
}
