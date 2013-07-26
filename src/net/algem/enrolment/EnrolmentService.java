/*
 * @(#)EnrolmentService.java	2.8.g 31/05/13
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.algem.config.*;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.contact.teacher.Teacher;
import net.algem.course.Course;
import net.algem.course.CourseIO;
import net.algem.course.CourseModuleInfo;
import net.algem.course.Module;
import net.algem.group.Musician;
import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.model.SQLkey;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.g
 * @since 2.4.a 20/04/12
 */
public class EnrolmentService
        extends GemService
{

  private RoomIO roomIO;
  private ActionIO actionIO;
  private PlanningService planningService;
  private DataCache dataCache;

  public EnrolmentService(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = dataCache.getDataConnection();
    roomIO = (RoomIO) DataCache.getDao(Model.Room);
    actionIO = (ActionIO) DataCache.getDao(Model.Action);
    planningService = new PlanningService(dc);
  }

  public DataCache getDataCache() {
    return dataCache;
  }

  public Room getRoomOnPeriod(int c) throws SQLException {
    return roomIO.getRoom(c, ConfigUtil.getStartOfPeriod(dc));
  }

  public Room getRoom(int id) throws SQLException {
    return roomIO.findId(id);
  }

  public Module getModule(int id) throws SQLException {
    return (Module) DataCache.findId(id, Model.Module);
  }

  public boolean hasModule(int m, int order) throws SQLException {
    String where = " AND cm.module = " + m + " AND cm.idcmd = " + order;
    Vector<ModuleOrder> vm = ModuleOrderIO.find(where, dc);
    return vm.size() > 0;
  }

  public int getIdAction(int code) throws SQLException {
    int id = 0;
    String where = ", cours WHERE action.cours = cours.id AND cours.code = '" + code + "' AND trim(cours.titre) ~* 'd[é|e|É]finir' LIMIT 1";
    id = actionIO.findId(where);
    assert (id > 0);

    return id;
  }

  public Course getCourse(int idAction) throws SQLException {
    return planningService.getCourseFromAction(idAction);
  }

  /**
   * Gets a list of courses(id, title) scheduled from {@code startDate} in {@code estab}.
   * Depending course's code, schedule's type and length may be used to filter the results.
   *
   * @param estab estab id
   * @param startDate from date
   * @param courseInfo course module info
   * @return a list of (id,title)
   * @throws SQLException
   */
  public Vector<SQLkey> getCoursesFromEstab(int estab, DateFr startDate, CourseModuleInfo courseInfo) throws SQLException {

    int code = courseInfo.getCode().getId();
    int type = Course.ATP_CODE == code ? Schedule.WORKSHOP_SCHEDULE : Schedule.COURSE_SCHEDULE;
    String query = "SELECT DISTINCT cours.id, cours.titre FROM cours, planning, action, salle"
            + " WHERE planning.ptype = " + type
            + " AND planning.jour >= '" + startDate + "'"
            + " AND planning.action = action.id"
            + " AND action.cours = cours.id"
            + " AND cours.code = " + code
            + " AND planning.lieux = salle.id AND salle.etablissement = " + estab;
    if (code != Course.ATP_CODE && code != Course.PRIVATE_INSTRUMENT_CODE) {
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
   * Gets all schedules for the course {@code c } in a week.
   *
   * @version 1.1d
   * @param c
   * @param start
   * @param idEstab
   * @return a list of schedules
   * @see net.algem.enrolment.CourseEnrolmentDlg
   */
  public Vector<Schedule> getCourseWeek(Course c, DateFr start, int idEstab) {

    Schedule p = get1PlanCourse(c, start, idEstab);
    if (p == null) {
      return new Vector<Schedule>();
    }

    DateFr end = new DateFr(p.getDate());
    end.incDay(7);
    //XXX et si le reste de la semaine tombe pendant les vacances ?
    int type = c.isATP() ? Schedule.WORKSHOP_SCHEDULE : Schedule.COURSE_SCHEDULE;
    String query = ",salle, action WHERE p.ptype = " + type
            + " AND p.jour >= '" + p.getDate() + "' AND p.jour < '" + end + "'"
            + " AND p.action = action.id"
            + " AND action.cours = " + c.getId()
            + " AND p.lieux = salle.id AND salle.etablissement = " + idEstab
            + " AND salle.nom NOT LIKE 'RATTRAP%' ORDER BY jour,debut,idper";
    return ScheduleIO.find(query, dc);
  }

  /**
   * Gets the first course {@code c} scheduled from the start of period.
   *
   * @param c course id
   * @return a schedule
   */
  public Schedule get1PlanCours(int c) {
    return get1PlanCours(c, dataCache.getStartOfPeriod());
  }

  /**
   * Gets the first course {@code courseId} scheduled from the {@code date} selected.
   *
   * @param courseId course id
   * @param start start date
   * @return a schedule
   */
  public Schedule get1PlanCours(int courseId, DateFr start) {

    String query = " ,action WHERE ptype = " + Schedule.COURSE_SCHEDULE
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
   * @see net.algem.enrolment.CourseEnrolmentDlg
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

  public Vector<ScheduleRange> getBusyTimeSlot(int idAction, int idCourse, String start, String end) {
    Vector<ScheduleRange> pl = new Vector<ScheduleRange>();
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
        pl.addElement(pg);
        //plage.addItem(p.getStart()+" - "+p.getEnd());
      }
      rs.close();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } finally {
      return pl;
    }

  }

  public int getPlaceNumber(int a, DateFr debut, DateFr fin) throws SQLException {
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

  public String getTeacher(int teacherId) {
    try {
      Teacher p = (Teacher) DataCache.findId(teacherId, Model.Teacher);
      return p.toString();
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "getTeacher", ex);
      return null;
    }
  }

  /**
   * Specifies if member {@code a} is already scheduled for {@code p} between the dates of enrolment.
   *
   * @param m member id
   * @param p schedule
   * @return true if scheduled
   * @throws EnrolmentException
   */
  public boolean isOnRange(int m, Schedule p, CourseOrder courseOrder) throws EnrolmentException {

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

  /**
   * Updates schedule ranges after order validation.
   *
   * @param module
   * @param co
   * @param memberId
   * @return the number of scheduled ranges inserted
   * @throws java.sql.SQLException
   */
  public int updateRange(ModuleOrder module, CourseOrder co, int memberId) throws SQLException {

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
      String msg2 = MessageUtil.getMessage(msgKey, new Object[] {notInRange.size(), v.size()});
      if (!MessagePopup.confirm(null, 
              MessageUtil.getMessage("member.enrolment.schedule.warning",
              new Object[]{c.getTitle(), sb.toString(), }) + msg2)) {
        throw new EnrolmentScheduleException();
      }
    }
  }
  

  /**
   * Gets the date when the member {@code member} is scheduled for the action {@code action}.
   *
   * @param action
   * @param start start date of enrolment
   * @param member member id
   * @return an integer representing the date of week
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
  
  public void deleteRange(DateFr from, int member, int action) throws SQLException {
    String query = "DELETE FROM " + ScheduleRangeIO.TABLE
            + " WHERE adherent = " + member
            + " AND idplanning IN ("
            + " SELECT id FROM planning"
            + " WHERE jour >= '" + from + "' AND action = " + action
            + ")";
      dc.executeUpdate(query);
  }

  /**
   * Stops a member's course from a date {@code from}.
   *
   * @param member id
   * @param co course order
   * @param c course
   * @param from date
   * @throws EnrolmentException
   */
  public void stopCourse(int member, CourseOrder co, Course c, DateFr from) throws EnrolmentException {
    String query = "DELETE FROM " + ScheduleRangeIO.TABLE
            + " WHERE adherent = " + member
            + " AND idplanning IN ("
            + " SELECT id FROM planning"
            + " WHERE jour >= '" + from + "' AND action = " + co.getAction()
            + ")";
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query);
      
      String label = ((GemParam) DataCache.findId(c.getCode(), Model.CourseCode)).getLabel()
              + " " + BundleUtil.getLabel("To.define.label");

      Hour timeLength = new Hour(co.getStart().getLength(co.getEnd()));

      // si la demande d'arret est postérieure à la date d'inscription à ce cours
      if (from.after(co.getDateStart())) {
        if (!c.isATP()) {
          co.setDateEnd(from);	// on change la date de fin de l'ancienne commande_cours
          CourseOrderIO.update(co, dc);// on update l'ancienne commande
        } 
        co.setDateStart(from);
        co.setDateEnd(dataCache.getEndOfYear());
        co.setAction(0);
        co.setTitle(label);
        co.setStart(new Hour());
        co.setEnd(timeLength);
        co.setDay(0);
        // on insère une nouvelle commande_cours (à définir)
        CourseOrderIO.insert(co, dc);
      } else {// si la demande d'arret est antérieure ou égale à la date d'inscription à ce cours
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
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }
  
  public void changeHour(int member, CourseOrder co, Course c, DateFr from) throws EnrolmentException {
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

  /**
   * Modifies a course enrolment.
   *
   * @param cc course order
   * @param memberId member id
   * @throws EnrolmentException en cas d'erreur SQL
   */
  public void modifyCourse(CourseOrder cc, int memberId) throws EnrolmentException {

    try {
      dc.setAutoCommit(false);
      updateRange(cc, memberId);
      CourseOrderIO.update(cc, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void createCourse(CourseOrder cc, int memberId) throws EnrolmentException {
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

  /**
   * Gets the list of enrolments for the member {@code id}.
   *
   * @param id member id
   * @return a list of enrolments
   */
  public Vector<Enrolment> getEnrolments(int id) {
    try {
      return EnrolmentIO.find("WHERE adh = " + id + " ORDER BY id", dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return null;
  }

  public Vector<MemberOrder> getOrders() {
    return OrderIO.findMemberOrders(dc);
  }

  public PersonFile getMemberFile(int idMember) throws SQLException {
    return (PersonFile) DataCache.findId(idMember, Model.PersonFile);
  }

  /**
   * Gets the members scheduled for this {@code plan}.
   */
  public static Vector<PersonFile> findMembersByPlanning(Schedule plan) {
    //23092003 String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,p.note,e.instrument1 from personne p, eleve e, commande c, commande_cours cc WHERE cc.cours = "+cours+" AND c.id = cc.idcmd and cc.datedebut <= '"+date+"' and cc.datefin >= '"+date+"' AND p.id = c.adh and p.id = e.idper ORDER by p.nom";
    //String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,p.note,p.droit_img,e.idper,e.instrument1,e.instrument2,e.profession,e.datenais,e.payeur,e.nadhesions,e.pratique,e.niveau  from personne p, eleve e, plage pl WHERE pl.cours = " + cours + " AND pl.date = '" + date + "' AND pl.prof = " + idprof + " AND p.id = pl.adherent and p.id = e.idper ORDER by p.nom";
    String query = "SELECT " + PersonIO.COLUMNS + "," + MemberIO.COLUMNS
            + " FROM " + PersonIO.TABLE + ", " + MemberIO.TABLE + ", plage pl "
            + " WHERE pl.idplanning = " + plan.getId()
            + " AND personne.id = pl.adherent "
            + " AND personne.id = eleve.idper ORDER BY personne.nom";
    return ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findRegistered(query);
//    return ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findByIdOrder(query, false);
  }

  public Vector<Musician> findCourseMembers(int course) throws SQLException {

    Vector<Musician> vm = new Vector<Musician>();

    String query = "SELECT DISTINCT p.id, p.nom, p.prenom, pi.instrument FROM "
            + PersonIO.TABLE + " p, "
            + MemberIO.TABLE + " e, "
            + OrderIO.TABLE + " c, "
            + CourseOrderIO.TABLE + " cc, "
            + ActionIO.TABLE + " a, "
            + InstrumentIO.PERSON_INSTRUMENT_TABLE + " pi"
            + " WHERE p.id = e.idper AND cc.idaction = a.id AND a.cours = " + course
            + " AND c.adh = p.id AND cc.idcmd = c.id AND pi.idper = p.id "
            + " AND pi.ptype = " + Instrument.MEMBER + " AND pi.idx = 0";

    ResultSet rs = dc.executeQuery(query);
    for (int i = 0; rs.next(); i++) {
      Musician a = new Musician();
      a.setId(rs.getInt(1));
      a.setName(rs.getString(2).trim());
      a.setFirstName(rs.getString(3).trim());
      a.setInstrument(rs.getInt(4));
      vm.addElement(a);
    }

    return vm;
  }

  /**
   * Gets the list of course order associated with the enrolment {@code i}
   * and the module {@code m}.
   *
   * @param i enrolment id
   * @param m module id
   * @return a liste of course order
   */
  public Vector<CourseOrder> getCourseOrder(int i, int m) throws SQLException {
    return CourseOrderIO.find(" AND cc.idcmd = " + i + " AND cc.module = " + m + " ORDER BY idaction, datedebut", dc);
  }

  /**
   * Gets the course undefined by {@code code}.
   *
   * @param code
   * @return a course
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
   * Gets action id for the course {@code id}.
   *
   * @param id course id
   * @return an integer
   * @throws SQLException
   */
  public int getActionFromCourse(int id) throws SQLException {
    return actionIO.findId("WHERE cours = " + id);
  }

  /**
   * Gets the first scheduled course {@code c} from {@code start}
   * in the establishment {@code estab}.
   *
   * @param idCours
   * @param start
   * @param estab
   * @return a schedule
   */
  private Schedule get1PlanCourse(Course c, DateFr debut, int estab) {
    //XXX voir pour les ateliers ponctuels
    int type = c.isATP() ? Schedule.WORKSHOP_SCHEDULE : Schedule.COURSE_SCHEDULE;
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

  public String[] getListOfPayment() {
    return ParamTableIO.getValues(ModeOfPaymentCtrl.TABLE, ModeOfPaymentCtrl.COLUMN_NAME, dc);
  }

  public void create(Order order) throws SQLException {
    OrderIO.insert(order, dc);
  }

  public void create(CourseOrder co) throws SQLException {
    CourseOrderIO.insert(co, dc);
  }
  
  public void update(CourseOrder co) throws SQLException {
    CourseOrderIO.update(co, dc);
  }

  void create(ModuleOrder mo) throws SQLException {
    ModuleOrderIO.insert(mo, dc);
  }

  public void delete(Order order) throws Exception {
    OrderIO.delete(order, dc);
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
}
