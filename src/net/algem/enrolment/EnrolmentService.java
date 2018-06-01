/*
 * @(#)EnrolmentService.java	2.15.8 25/03/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.algem.Algem;
import net.algem.config.*;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.contact.teacher.Teacher;
import net.algem.course.*;
import net.algem.group.Musician;
import net.algem.planning.*;
import net.algem.room.RoomIO;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.model.SQLkey;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.4.a 20/04/12
 */
public class EnrolmentService
  extends GemService {

  private final RoomIO roomIO;
  private final ActionIO actionIO;
  private final PlanningService planningService;
  private final DataCache dataCache;

  public EnrolmentService(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = DataCache.getDataConnection();
    roomIO = (RoomIO) DataCache.getDao(Model.Room);
    actionIO = (ActionIO) DataCache.getDao(Model.Action);
    planningService = new PlanningService(dc);
  }

  public DataCache getDataCache() {
    return dataCache;
  }

  Module getModule(int id) throws SQLException {
    return (Module) DataCache.findId(id, Model.Module);
  }

  public Course getCourse(int idAction) throws SQLException {
    return planningService.getCourseFromAction(idAction);
  }

  /**
   * Gets a list of courses(id, title) scheduled from {@literal startDate} in {@literal estab}.
   * Depending course's code, schedule's type and length may be used to filter the results.
   *
   * @param estab estab id
   * @param startDate from date
   * @param courseInfo course module info
   * @return a list of (id,title)
   * @throws SQLException
   */
  Vector<SQLkey> getCoursesFromEstab(int estab, DateFr startDate, CourseModuleInfo courseInfo) throws SQLException {

    int code = courseInfo.getCode().getId();
//    int type = CourseCodeType.ATP.getId() == code ? Schedule.WORKSHOP : Schedule.COURSE;
    int type = getScheduleType(courseInfo.getCode().getId());

    String query = "SELECT DISTINCT cours.id, cours.titre FROM cours, planning, action, salle"
      + " WHERE planning.ptype = " + type
      + " AND planning.jour >= '" + startDate + "'"
      + " AND planning.action = action.id"
      + " AND action.cours = cours.id"
      + " AND cours.code = " + code
      + " AND planning.lieux = salle.id AND salle.etablissement = " + estab;
    if (Algem.getAppClient().equals("PaysRoiMorvan")) {
      if (code == CourseCodeType.ATL.getId() || code == CourseCodeType.FMU.getId()) {
        query += " AND planning.fin - planning.debut <= '" + new Hour(courseInfo.getTimeLength()) + "'";
      } else if (code != CourseCodeType.ATP.getId() && code != CourseCodeType.INS.getId() && code != CourseCodeType.STG.getId()) {
        query += " AND planning.fin - planning.debut = '" + new Hour(courseInfo.getTimeLength()) + "'";
      }
    } else if (code != CourseCodeType.ATP.getId() && code != CourseCodeType.INS.getId() && code != CourseCodeType.STG.getId()) {
      query += " AND planning.fin - planning.debut = '" + new Hour(courseInfo.getTimeLength()) + "'";
    }
    query += " ORDER BY cours.titre";

    Vector<SQLkey> values = new Vector<SQLkey>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      values.addElement(new SQLkey(rs.getInt(1), rs.getString(2)));
    }

    return values;
  }

  /**
   * Used for enrolment in {@link net.algem.enrolment.CourseEnrolmentDlg }.
   * Gets all schedules for the course {@literal c } in a week.
   *
   * @version 1.1d
   * @param c
   * @param start
   * @param idEstab
   * @return a list of schedules
   * @see net.algem.enrolment.CourseEnrolmentDlg
   */
  Vector<Schedule> getCourseWeek(Course c, DateFr start, int idEstab) {

    Schedule p = get1PlanCourse(c, start, idEstab);
    if (p == null) {
      return new Vector<Schedule>();
    }

    DateFr end = new DateFr(p.getDate());
    end.incDay(7);
    //XXX et si le reste de la semaine tombe pendant les vacances ?
//    int type = c.isATP() ? Schedule.WORKSHOP : Schedule.COURSE;

    int type = getScheduleType(c.getCode());
    String query = ",salle, action WHERE p.ptype = " + type
      + " AND p.jour >= '" + p.getDate() + "' AND p.jour < '" + end + "'"
      + " AND p.action = action.id"
      + " AND action.cours = " + c.getId()
      + " AND p.lieux = salle.id AND salle.etablissement = " + idEstab
      + " AND salle.nom NOT LIKE 'RATTRAP%' ORDER BY jour,debut,idper";
    return ScheduleIO.find(query, dc);
  }

  List<CourseSchedule> getSchedules(CourseModuleInfo cmi, DateFr start, int action, int estab) throws SQLException {
    DateFr s = new DateFr(start);
    DateFr end = new DateFr(s);
    end.incDay(6);
    DateRange dates = new DateRange(s, end);
    return actionIO.getAvailableSchedules(cmi, dates, action, estab);
  }

  /**
   * Gets the date when the member {@literal member} is scheduled for the action {@literal action}.
   *
   * @param action
   * @param start start date of enrolment
   * @param member member id
   * @return an integer representing the date of week
   * @throws java.sql.SQLException
   */
  public int getCourseDayMember(int action, DateFr start, int member) throws SQLException {

    int j = 0;
    if (action == 0) {
      return j;
    }

    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    DateFr d = null;

    Course course = getCourse(action);
    // pas de recherche pour les cours à définir
    if (course == null || course.isUndefined()) {
      return j;
    }
    String dateStart = "00-00-0000".equals(start.toString()) ? "01-01-1900" : start.toString();
    String query = "SELECT jour FROM " + ScheduleIO.TABLE + " p, " + ScheduleRangeIO.TABLE + " pl"
      + " WHERE p.id = pl.idplanning"
      + " AND p.jour >= '" + dateStart + "' AND p.action = " + action
      + " AND pl.adherent = " + member
      + " LIMIT 1";

    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      d = new DateFr(rs.getString(1));
    } else { // pour les anciennes commandes
      query = "SELECT jour FROM " + ScheduleIO.TABLE + " p, "
        + ScheduleRangeIO.TABLE + " pl, action a"
        + " WHERE p.jour >= '" + dateStart + "' AND p.id = pl.idplanning"
        + " AND pl.adherent = " + member
        + " AND p.action = a.id"
        + " AND a.cours = " + course.getId()
        + " LIMIT 1";
      rs = dc.executeQuery(query);
      if (rs.next()) {
        d = new DateFr(rs.getString(1));
      }
    }
    rs.close();
    if (d != null) {
      cal.setTime(d.getDate());
      j = cal.get(Calendar.DAY_OF_WEEK);
    }
    return j;
  }

  /**
   * Gets the type of schedule corresponding to the {@literal courseCode}.
   *
   * @param courseCode course code
   * @return an integer representing the type constant
   */
  private int getScheduleType(int courseCode) {

    if (courseCode == CourseCodeType.ATP.getId()) {
      return Schedule.WORKSHOP;
    }
    if (courseCode == CourseCodeType.STG.getId()) {
      return Schedule.TRAINING;
    }
    return Schedule.COURSE;
  }

  /**
   * Gets the first course {@literal courseId} scheduled from the {@literal date} selected.
   *
   * @param courseId course id
   * @param start start date
   * @return a schedule
   */
  private Schedule get1PlanCours(int courseId, DateFr start) {

    String query = " ,action WHERE ptype = " + Schedule.COURSE
      + " AND jour >= '" + start + "'"
      + " AND p.action = action.id"
      + " AND action.cours = " + courseId + " ORDER BY jour LIMIT 1";
    Vector<Schedule> v = ScheduleIO.find(query, dc);

    return (v.size() > 0 ? v.elementAt(0) : null);
  }

  /**
   * Used in {@link net.algem.enrolment.CourseEnrolmentDlg} for time slot verification.
   *
   * @param p schedule
   * @return a list of hour ranges
   * @throws net.algem.enrolment.EnrolmentException
   * @see net.algem.enrolment.CourseEnrolmentDlg
   * @deprecated
   */
  public Vector<HourRange> getPlageCours(Schedule p) throws EnrolmentException {
    Vector<HourRange> v = new Vector<HourRange>();
    try {
      ResultSet rs = ScheduleIO.getRSCourseRange(p, dc);

      while (rs.next()) {
        HourRange ph = new HourRange();
        ph.setStart(new Hour(rs.getString(1)));
        ph.setEnd(new Hour(rs.getString(2)));
        v.addElement(ph);
      }
      rs.close();
    } catch (SQLException e) {
      throw new EnrolmentException("Erreur recherche plage horaire " + " :\n" + e.getMessage());
    }
    return v;
  }

  Vector<ScheduleRange> getBusyTimeSlot(int idAction, int idCourse, String start, String end) {
    Vector<ScheduleRange> ranges = new Vector<ScheduleRange>();
    try {
      String query = "SELECT DISTINCT pg.debut, pg.fin FROM plage pg, planning p WHERE pg.idplanning = p.id"
        + " AND p.action = " + idAction
        + " AND p.jour >= '" + start + "'"
        + " AND p.jour <= '" + end + "'";

      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        ScheduleRange pg = new ScheduleRange();
        pg.setCourseId(idCourse); //?
        pg.setStart(new Hour(rs.getString(1)));
        pg.setEnd(new Hour(rs.getString(2)));
        ranges.addElement(pg);
        //plage.addItem(p.getStart()+" - "+p.getEnd());
      }
      rs.close();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return ranges;

  }

  int getPlaceNumber(int a, DateFr debut, DateFr fin) throws SQLException {
    int n = 0;
    String query = "SELECT count(pg.id) FROM plage pg, planning p "
      + " WHERE p.action = " + a
      + " AND pg.idplanning = p.id"
      + " AND p.jour BETWEEN '" + debut + "' AND '" + fin + "'"
      + " GROUP BY p.id";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      if (rs.getInt(1) > n) {
        n++;
      }
    }
    return n;
  }

  String getTeacher(int teacherId) {
    try {
      Teacher p = (Teacher) DataCache.findId(teacherId, Model.Teacher);
      return p.toString();
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "getTeacher", ex);
      return null;
    }
  }

  /**
   * Gets the list of enrolments for the member {@literal id}.
   *
   * @param idper member id
   * @return a list of enrolments
   */
  List<Enrolment> getEnrolments(int idper) {
    try {
      return EnrolmentIO.find("WHERE adh = " + idper + " ORDER BY id", dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return null;
  }

  /**
   * Get the list of enrolments in pro training.
   *
   * @param idper member id
   * @param from start date
   * @param to end date
   * @return a list of enrolments or an empty list if no enrolment was found
   */
  List<Enrolment> getProEnrolments(Date from, Date to) {
    try {
      return EnrolmentIO.find(" JOIN personne p on(adh = p.id)"
        + " WHERE creation BETWEEN '" + from + "' AND '" + to
        + "' AND " + OrderIO.TABLE + ".id IN("
        + "SELECT idcmd FROM commande_module cm join module m on (cm.module = m.id) "
        + " where m.code like 'P%')"
        + " ORDER BY p.nom", dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return null;
  }

  Vector<MemberOrder> getOrders() {
    return OrderIO.findMemberOrders(dc);
  }

  /**
   * Gets the list of course orders associated with the enrolment {@literal i}
   * and the module {@literal m}.
   *
   * @param i enrolment id
   * @param m module id
   * @return a liste of course order
   * @throws java.sql.SQLException
   */
  public Vector<CourseOrder> getCourseOrder(int i, int m) throws SQLException {
    return CourseOrderIO.find(" AND cc.idcmd = " + i + " AND cc.module = " + m + " ORDER BY idaction, datedebut", dc);
  }

  PersonFile getMemberFile(int idMember) throws SQLException {
    return (PersonFile) DataCache.findId(idMember, Model.PersonFile);
  }

  /**
   * Gets the members scheduled for this {@literal plan}.
   *
   * @param plan schedule
   * @return a list of person files
   */
  public static Vector<PersonFile> findMembersByPlanning(Schedule plan) {
    String query = "SELECT " + PersonIO.COLUMNS + "," + MemberIO.COLUMNS
      + " FROM " + PersonIO.VIEW + " p JOIN " + MemberIO.TABLE + " m ON p.id = m.idper"
      + " JOIN " + ScheduleRangeIO.TABLE + " pl ON p.id = pl.adherent "
      + " WHERE pl.idplanning = " + plan.getId();
    return ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findRegistered(query);
//    return ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findByIdOrder(query, false);
  }

  /**
   * Gets the list of students taking this course {@code id}.
   *
   * @param course course id
   * @param start start date
   * @param end end date
   * @return a list of students or an empty list if no student was found
   * @throws SQLException
   */
  List<Musician> findCourseMembers(int course, Date start, Date end) throws SQLException {
    return CourseOrderIO.findCourseMembers(course, start, end, dc);
  }

  /**
   * Gets the list of students enrolled in this module {@code id}.
   *
   * @param id module id
   * @param start start date
   * @param end end date
   * @return a list of students or an empty list if no student was found
   * @throws SQLException
   */
  public List<Musician> findModuleMembers(int id, Date start, Date end) throws SQLException {
    return ModuleOrderIO.findModuleMembers(id, start, end, dc);
  }

  /**
   * Gets the undefined course corresponding to code {@literal code}.
   *
   * @param code integer code representation
   * @return a course with status undefined
   */
  public Course getCourseUndefined(int code) {
    Vector<Course> vc = null;
    try {
      String where = "WHERE c.code = '" + code + "' AND trim(c.titre) ~* 'd[eéÉ]finir' LIMIT 1";
      vc = ((CourseIO) DataCache.getDao(Model.Course)).find(where);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (vc != null && vc.size() > 0) {
      return vc.elementAt(0);
    }
    return null;
  }

  /**
   * Gets action id for the course {@literal id}.
   *
   * @param id course id
   * @return an integer
   * @throws SQLException
   */
  public int getActionFromCourse(int id) throws SQLException {
    return actionIO.findId("WHERE cours = " + id);
  }

  /**
   * Gets the first scheduled course {@literal c} from {@literal start}
   * in the establishment {@literal estab}.
   *
   * @param idCours
   * @param start
   * @param estab
   * @return a schedule
   */
  private Schedule get1PlanCourse(Course c, DateFr debut, int estab) {
    //XXX voir pour les ateliers ponctuels
//    int type = c.isATP() ? Schedule.WORKSHOP : Schedule.COURSE;

    int type = getScheduleType(c.getCode());
    String query = " ,salle, action WHERE ptype = " + type
      + " AND jour >= '" + debut + "'"
      + " AND p.action = action.id"
      + " AND action.cours = " + c.getId()
      + " AND lieux = salle.id AND salle.etablissement = " + estab
      + " AND salle.nom NOT LIKE 'RATTRAP%' ORDER BY jour LIMIT 1";

    Vector<Schedule> v = ScheduleIO.find(query, dc);
    if (v.size() > 0) {
      return v.elementAt(0); // retourne un seul planning
    } else {
      return null;
    }
  }

  String[] getListOfPayment() {
    return ParamTableIO.getValues(ModeOfPaymentCtrl.TABLE, ModeOfPaymentCtrl.COLUMN_NAME, dc);
  }

  /**
   * Checks if member width the id {@code m} is already scheduled between start of schedule {@code p} and end of enrolment.
   *
   * @param m member id
   * @param p schedule
   * @param courseOrder
   * @return true if scheduled
   * @throws EnrolmentException
   */
  boolean isOnRange(int m, Schedule p, CourseOrder courseOrder) throws EnrolmentException {

    try {
      String where = "pg WHERE pg.adherent = " + m + " AND pg.idplanning IN("
        + "SELECT id FROM " + ScheduleIO.TABLE
        + " WHERE action = " + p.getIdAction()
        //+ " AND jour >= '" + courseOrder.getDateStart() + "' AND jour <= '" + courseOrder.getDateEnd()
        + " AND jour >= '" + p.getDate() + "' AND jour <= '" + courseOrder.getDateEnd()
        + "')";
      Vector<ScheduleRange> vp = ScheduleRangeIO.find(where, dc);
      if (vp.size() > 0) {
        return true;
      }
    } catch (SQLException sqe) {
      throw new EnrolmentException("Erreur recherche plage adhérent" + " :\n" + sqe.getMessage());
    }
    return false;
  }

  public List<ScheduleRange> getRangeOverlap(DateFr start, int member, Hour hStart, Hour hEnd, int action) throws SQLException {
    String where = ConflictQueries.getRangeOverlapSelection(start, member, hStart, hEnd, action);
    return ScheduleRangeIO.find(where, dc);
  }

  private void insertRange(CourseOrder co, Course c, Vector<Schedule> v, ScheduleRange h)
    throws SQLException, EnrolmentScheduleException {

    List<Schedule> notInRange = new ArrayList<Schedule>();

    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      h.setScheduleId(p.getId());

      if (c.isCollective()) {
        h.setStart(p.getStart());
        h.setEnd(p.getEnd());
      } else {
        // TODO VERIFIER heures de début/fin si planning a été copié/déplacé
        // et tenir compte du décalage si oui
        if (!co.getStart().between(p.getStart(), p.getEnd())
          || !co.getEnd().between(p.getStart(), p.getEnd())) {
          notInRange.add(p);
          continue;
        }
        h.setStart(co.getStart());
        h.setEnd(co.getEnd());
      }
      ScheduleRangeIO.insert(h, dc); // insertion des plages
    }
    if (!notInRange.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (Schedule s : notInRange) {
        sb.append(s.getDate()).append("->").append(s.getStart()).append("-").append(s.getEnd()).append("\n");
      }
      String msgKey = notInRange.size() == 1 ? "member.enrolment.schedule.session.warning"
        : "member.enrolment.schedule.sessions.warning";
      String msg2 = MessageUtil.getMessage(msgKey, new Object[]{notInRange.size(), v.size()});
      if (!MessagePopup.confirm(null,
        MessageUtil.getMessage("member.enrolment.schedule.warning",
          new Object[]{c.getTitle(), sb.toString(),}) + msg2)) {
        throw new EnrolmentScheduleException();
      }
    }
  }

  /**
   * Updates schedule ranges after order validation.
   *
   * @param module
   * @param co
   * @param memberId
   * @return the number of scheduled ranges inserted
   * @throws java.sql.SQLException
   */
  int updateRange(ModuleOrder module, CourseOrder co, int memberId) throws SQLException {

    String query = "WHERE action = " + co.getAction()
      + " AND jour >='" + module.getStart() + "'"
      + " AND jour <= '" + module.getEnd() + "'"
      + " ORDER BY p.jour";

    Vector<Schedule> v = ScheduleIO.findCourse(query, dc);
    if (v.size() < 1) {
      return 0;
    }
    Course c = planningService.getCourseFromAction(co.getAction());
    ScheduleRange h = new ScheduleRange();
    h.setMemberId(memberId);
    try {
      insertRange(co, c, v, h);
    } catch (EnrolmentScheduleException ex) {
      throw new SQLException(MessageUtil.getMessage("enrolment.cancel.info"));
    }

    return v.size();
  }

  /**
   * Updates the scheduled ranges when course order is modified.
   *
   * @param co course order
   * @param m member id
   *
   * @throws SQLException
   */
  public void updateRange(CourseOrder co, int m) throws SQLException {

    String query = "WHERE action = " + co.getAction()
      + " AND jour >='" + co.getDateStart() + "'"
      + " AND jour <= '" + co.getDateEnd() + "'";
    Vector<Schedule> v = ScheduleIO.findCourse(query, dc);

    if (v.isEmpty()) {
      return;
    }
    Course c = planningService.getCourseFromAction(co.getAction());
    ScheduleRange h = new ScheduleRange();
    h.setMemberId(m);
    try {
      insertRange(co, c, v, h);
    } catch (EnrolmentScheduleException ex) {
      throw new SQLException(BundleUtil.getLabel("Action.cancel.label"));
    }
  }

  public void deleteRange(DateFr from, int member, int action) throws SQLException {
    String query = "DELETE FROM " + ScheduleRangeIO.TABLE
      + " WHERE adherent = " + member
      + " AND idplanning IN ("
      + " SELECT id FROM planning"
      + " WHERE jour >= '" + from + "' AND action = " + action
      + ")";
    dc.executeUpdate(query);
  }

  void changeHour(int member, CourseOrder co, Course c, DateFr from) throws EnrolmentException {
    String query = "DELETE FROM " + ScheduleRangeIO.TABLE
      + " WHERE adherent = " + member
      + " AND idplanning IN ("
      + " SELECT id FROM planning"
      + " WHERE jour >= '" + from + "' AND action = " + co.getAction()
      + ")";
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query);
      // si la demande d'arret est postérieure à la date d'inscription à ce cours
      if (from.after(co.getDateStart())) {
        co.setDateStart(from);
        co.setDateEnd(dataCache.getEndOfYear());
        CourseOrderIO.insert(co, dc);
      } else {// si la demande d'arret est antérieure ou égale à la date d'inscription à ce cours
        CourseOrderIO.update(co, dc);
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  void create(Order order) throws SQLException {
    OrderIO.insert(order, dc);
  }

  void delete(Order order) throws Exception {
    OrderIO.delete(order, dc);
  }

  void create(ModuleOrder mo) throws SQLException {
    ModuleOrderIO.insert(mo, dc);
  }

  void delete(ModuleOrder mo, int member) throws EnrolmentException {
    try {
      dc.setAutoCommit(false);

      ModuleOrderIO.delete(mo.getId(), dc);
      Vector<CourseOrder> courseOrders = CourseOrderIO.find(" AND cc.module = " + mo.getId(), dc);
      for (int i = 0; i < courseOrders.size(); i++) {
        CourseOrder cc = courseOrders.elementAt(i);
        // suppression des plages de cours
        String query = "idplanning IN (SELECT id FROM " + ScheduleIO.TABLE
          + " WHERE action = " + cc.getAction() + ")"
          + " AND adherent = " + member;
        ScheduleRangeIO.delete(query, dc);
      }
      // suppression de la commande_cours
      CourseOrderIO.deleteByIdModule(mo.getId(), dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void create(CourseOrder co) throws SQLException {
    CourseOrderIO.insert(co, dc);
  }

  void createCourse(CourseOrder cc, int memberId) throws EnrolmentException {
    try {
      dc.setAutoCommit(false);
      updateRange(cc, memberId);
      CourseOrderIO.insert(cc, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void update(CourseOrder co) throws SQLException {
    CourseOrderIO.update(co, dc);
  }

  public void update(ModuleOrder mo) throws SQLException {
    ModuleOrderIO.update(mo, dc);
  }

  public void updateOrderDate(Order o) throws SQLException {
    OrderIO.updateOrderDate(o, dc);
  }

  /**
   * Redefines a course to define.
   *
   * @param co course order
   * @param memberId member id
   * @throws EnrolmentException en cas d'erreur SQL
   */
  void modifyCourse(CourseOrder co, int memberId) throws EnrolmentException {

    try {
      dc.setAutoCommit(false);
      updateRange(co, memberId);
      CourseOrderIO.update(co, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Removes the course order with id {@literal id}.
   *
   * @param id course order's id
   */
  void stopCourse(int id) {
    try {
      CourseOrderIO.deleteById(id, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  /**
   * Stops a member's course from the date {@literal from}.
   *
   * @param member member's id
   * @param co course order
   * @param c course
   * @param from the date from which to stop
   * @param redefine If true, a new course order to define is created. If false, course order is only updated.
   * @throws EnrolmentException
   */
  public void stopCourse(int member, final CourseOrder co, final Course c, final DateFr from, final boolean redefine) throws EnrolmentException {
    try {
      final String query = "DELETE FROM " + ScheduleRangeIO.TABLE
        + " WHERE adherent = " + member
        + " AND idplanning IN ("
        + " SELECT id FROM planning"
        + " WHERE jour >= '" + from + "' AND action = " + co.getAction()
        + ")";
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          dc.executeUpdate(query);
          refreshCourseOrder(from, co, c, redefine);
          return null;
        }
      });
    } catch (Exception ex) {
      throw new EnrolmentException(ex.getMessage());
    }
  }

  private void refreshCourseOrder(DateFr from, CourseOrder co, Course c, boolean redefine) throws SQLException {
    String label = ((GemParam) DataCache.findId(c.getCode(), Model.CourseCode)).getLabel()
      + " " + BundleUtil.getLabel("To.define.label");
    Hour timeLength = new Hour(co.getStart().getLength(co.getEnd()));
    if (from.after(co.getDateStart())) {
      if (!c.isATP()) {
        co.setDateEnd(from);	// on change la date de fin de l'ancienne commande_cours
        CourseOrderIO.update(co, dc);// on update l'ancienne commande
      }
      if (redefine) {
        co.setDateStart(from);
        co.setDateEnd(dataCache.getEndOfYear());
        co.setAction(0);
        co.setTitle(label);
        co.setStart(new Hour());
        co.setEnd(timeLength);
        co.setDay(0);
        // on insère une nouvelle commande_cours (à définir)
        CourseOrderIO.insert(co, dc);
      }
    } else// si la demande d'arret est antérieure ou égale à la date d'inscription à ce cours
    if (redefine) {
      co.setAction(0);
      co.setTitle(label);
      co.setStart(new Hour());
      co.setEnd(timeLength);
      co.setDay(0);
      if (c.isATP()) {
        co.setDateStart(from);
        co.setDateEnd(dataCache.getEndOfYear());
        co.setEnd(new Hour());
      }
      // on modifie la commande_cours existante
      CourseOrderIO.update(co, dc);
    } else {
      CourseOrderIO.deleteById(co.getId(), dc);
    }
  }

  void stopCourseFromModule(int member, CourseOrder co, DateFr from) throws EnrolmentException {
    String query = "DELETE FROM " + ScheduleRangeIO.TABLE
      + " WHERE adherent = " + member
      + " AND idplanning IN ("
      + " SELECT id FROM planning"
      + " WHERE jour >= '" + from + "' AND action = " + co.getAction()
      + ")";
    try {
      if (co.getAction() > 0) {
        dc.executeUpdate(query);
      }

      String label = ((GemParam) DataCache.findId(co.getCode(), Model.CourseCode)).getLabel()
        + " " + BundleUtil.getLabel("To.define.label");

      Hour timeLength = new Hour(co.getStart().getLength(co.getEnd()));

      // si la demande d'arret est postérieure à la date d'inscription à ce cours
      if (from.after(co.getDateStart())) {
        co.setDateEnd(from);	// on change la date de fin de la commande_cours
      } else {// si la demande d'arret est antérieure ou égale à la date d'inscription à ce cours
        co.setAction(0);
        co.setTitle(label);
        co.setStart(new Hour());
        co.setEnd(timeLength);
        co.setDay(0);
        co.setDateStart(from);
        co.setDateEnd(from);
      }
      // on updateAdministrativeEvent la commande_cours
      CourseOrderIO.update(co, dc);
    } catch (SQLException sqe) {
      throw new EnrolmentException(sqe.getMessage());
    }
  }

  /**
   * Stops a module and delete (if exist) the schedule ranges after the date of {@literal stop}.
   *
   * @param mo module order
   * @param orders maps course order to course
   * @param member member's id
   * @param stop stop date
   * @throws EnrolmentException if sql exception is thrown
   */
  void stopModule(ModuleOrder mo, List<CourseOrder> orders, int member, DateFr stop) throws EnrolmentException {
    try {
      // on regroupe tout dans une transaction
      dc.setAutoCommit(false);
      for (CourseOrder co : orders) {
        stopCourseFromModule(member, co, stop);
      }
      ModuleOrderIO.update(mo, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Gets the time length of the sessions already performed by the member {@literal idper} between {@code start} and {@code end},
   * corresponding to the module order {@literal mOrderId}.
   * A session is seen as completed if it was scheduled, even though it has not actually occurred.
   *
   * @param idper member's id
   * @param mOrderId id of the module order corresponding to the training performed
   * @param start start date
   * @param end end date
   * @return a length in minutes
   */
  public int getCompletedTime(int idper, int mOrderId, Date start, Date end) {
    try {
      return ModuleOrderIO.getCompletedTime(idper, mOrderId, start, end, dc);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#getCompletedTime " + ex.getMessage());
      return 0;
    }
  }

  /**
   * Gets the time length of the sessions already performed by the member {@literal idper} from {@code start},
   * corresponding to the module order {@literal mOrderId}.
   * A session is seen as completed if it was scheduled, even though it has not actually occurred.
   *
   * @param idper member's id
   * @param mOrderId id of the module order corresponding to the training performed
   * @param start start date
   * @return a length in minutes
   */
  public int getCompletedTime(int idper, int mOrderId, Date start) {
    try {
      return ModuleOrderIO.getCompletedTime(idper, mOrderId, start, dc);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#getCompletedTime " + ex.getMessage());
      return 0;
    }
  }

  public Date getLastScheduleByModuleOrder(int idper, int mOrderId) {
    try {
      return ModuleOrderIO.getLastSchedule(idper, mOrderId, dc);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#getLastScheduledDate " + ex.getMessage());
      return null;
    }
  }

  public Date getLastSchedule(int idper, int courseOrderId) {
    try {
      return CourseOrderIO.getLastSchedule(idper, courseOrderId, dc);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#getLastScheduledDate " + ex.getMessage());
      return null;
    }
  }

  public List<ExtendedModuleOrder> getExtendedModuleList(final Date start, final Date end) throws SQLException {
    return ModuleOrderIO.findExtendedModuleList(start, end, dc);
  }

}
