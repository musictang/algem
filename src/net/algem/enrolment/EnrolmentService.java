/*
 * @(#)EnrolmentService.java	2.7.a 11/01/13
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
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
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.GemService;
import net.algem.util.model.Model;
import net.algem.util.model.SQLkey;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
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

    DateFr end = new DateFr(p.getDay());
    end.incDay(7);
    //XXX et si le reste de la semaine tombe pendant les vacances ?
    int type = c.isATP() ? Schedule.WORKSHOP_SCHEDULE : Schedule.COURSE_SCHEDULE;
    String query = ",salle, action WHERE p.ptype = " + type
            + " AND p.jour >= '" + p.getDay() + "' AND p.jour < '" + end + "'"
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
   * Specifies if member {@code a} is already scheduled for {@code p}.
   *
   * @param a member id
   * @param p schedule
   * @return true if scheduled
   * @throws EnrolmentException
   */
  public boolean isOnRange(int a, Schedule p) throws EnrolmentException {

    try {
      String where = "WHERE pg.adherent = " + a + " AND pg.idplanning = " + p.getId();

      Vector<ScheduleRange> vp = ScheduleRangeIO.find(where, dc);
      if (vp.size() > 0) {
        return true;
      }
    } catch (SQLException sqe) {
      throw new EnrolmentException("Erreur recherche plage adhérent" + " :\n" + sqe.getMessage());
    }
    return false;
  }

  /**
   * Updates schedule ranges after order validation.
   *
   * @param module
   * @param cc
   * @param memberId
   * @return the number of scheduled ranges inserted
   * @throws java.sql.SQLException
   */
  public int updateRange(ModuleOrder module, CourseOrder cc, int memberId) throws SQLException {

    // prise en compte les ateliers
    /*
     * int type; boolean atp = c.getCode().equalsIgnoreCase("ATP"); if (atp) {
     * type = Schedule.WORKSHOP_SCHEDULE; } else { type =
     * Schedule.COURSE_SCHEDULE; }
     */

    String query = "WHERE action = " + cc.getAction()
            + " AND jour >='" + module.getStart() + "'"
            + " AND jour <= '" + module.getEnd() + "'";
    //+ " AND extract(dow from day)=" + courseOrder.getDay();

    Vector<Schedule> v = ScheduleIO.findCourse(query, dc);
    if (v.size() < 1) {
      return 0;
    }
    ScheduleRange h = new ScheduleRange();
    h.setMemberId(memberId);

    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      h.setScheduleId(p.getId());
      // TODO VERIFIER heures de début/fin si planning a été copié/déplacé
      // et tenir compte du décalage si oui
      if (!cc.getStart().between(p.getStart(), p.getEnd())
              || !cc.getEnd().between(p.getStart(), p.getEnd())) {
        continue;
      }
      h.setStart(cc.getStart());
      h.setEnd(cc.getEnd());
      ScheduleRangeIO.insert(h, dc); // insertion des plages a décommenter après test
    }
    return v.size();
  }

  /**
   * Gets the day when the member {@code member} is scheduled for the action {@code action}.
   *
   * @param action
   * @param start start date of enrolment
   * @param member member id
   * @return an integer representing the day of week
   */
  public int getCourseDayMember(int action, DateFr start, int member) {
    int j = 0;
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    DateFr d = null;

    try {
      Course course = getCourse(action);
      // pas de recherche pour les cours à définir
      if (course.isUndefined()) {
        return j;
      }
      String debut = "00-00-0000".equals(start.toString()) ? "01-01-1900" : start.toString();
      String query = "SELECT jour FROM planning p, plage pl WHERE p.id = pl.idplanning  AND p.jour >= '" + debut
              + "' AND p.action = " + action
              + " AND pl.adherent=" + member + " LIMIT 1";

      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        d = new DateFr(rs.getString(1));
      } else { // pour les anciennes commandes
        query = "SELECT jour FROM planning p, plage pl, action a WHERE p.jour >= '" + debut
                + "' AND p.id = pl.idplanning AND pl.adherent = " + member
                + " AND p.action = a.id AND a.cours = " + course.getId() + "  LIMIT 1";
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
    } catch (SQLException e) {
      GemLogger.log(getClass().getName(), "getJourCoursAdh", e);
    }
    return j;
  }

  /**
   * Stops a course from a date {@code from}.
   *
   * @param cmd order id
   * @param cc course order id
   * @param c course id
   * @param from start date
   * @throws EnrolmentException
   */
  public void stopCourse(Order cmd, CourseOrder cc, Course c, DateFr from) throws EnrolmentException {
    String query = "DELETE FROM plage WHERE adherent = " + cmd.getMember() + " AND idplanning IN (SELECT id FROM planning WHERE jour >= '" + from + "' AND action = " + cc.getAction() + ")";
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query);
      String libADefinir = "A Definir";
      int idADefinir = 0;
      Course cad = getCourseUndefined(c.getCode());
      if (cad != null) {
        idADefinir = getActionFromCourse(cad.getId());
        libADefinir = cad.getLabel();
      } else {
        return;
      }

      Hour duree = new Hour(cc.getStart().getLength(cc.getEnd()));

      // si la demande d'arret est postérieure à la date d'inscription à ce cours
      if (from.after(cc.getDateStart())) {
        //DateFr datefin = ccours.getDateEnd();
        if (!c.isATP()) {
          cc.setDateEnd(from);	// on change la date de end de l'ancienne commande_cours
          CourseOrderIO.update(cc, dc);// on update l'ancienne commande
        } else {
          ////CommandeCoursIO.deleteOID(ccours, dc);//oid (seule la commande_cours courante)
        }
        cc.setDateStart(from);
        cc.setDateEnd(dataCache.getEndOfYear());
        cc.setAction(idADefinir);
        cc.setTitle(libADefinir);
        cc.setStart(new Hour());
        cc.setEnd(duree);
        cc.setDay(0);
        // on insère une nouvelle commande_cours (à définir)
        CourseOrderIO.insert(cc, dc);
      } else {// si la demande d'arret est antérieure ou égale à la date d'inscription à ce cours
        cc.setAction(idADefinir);
        cc.setTitle(libADefinir);
        cc.setStart(new Hour());
        cc.setEnd(duree);
        cc.setDay(0);
        if (c.isATP()) {
          cc.setDateStart(from);
          cc.setDateEnd(dataCache.getEndOfYear());
          cc.setEnd(new Hour());
        }
        // on modifie la commande_cours existante
        CourseOrderIO.update(cc, dc);
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
      updateRange(cc, memberId);// 1.1d ajout de la salle
      CourseOrderIO.update(cc, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new EnrolmentException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
//        desktop.postEvent(new ModifPlanEvent(this, cc.getDateStart(), cc.getDateEnd()));// test
//        // Rafraichissement de la vue inscription
//        desktop.postEvent(new UpdateInscriptionEvent(this, dossier.getID()));
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
    return OrderIO.findMember(dc);
  }

  public PersonFile getMemberFile(int idMember) throws SQLException {
    return (PersonFile) DataCache.findId(idMember, Model.PersonFile);
  }

  /**
   * Gets the members scheduled for this {@code plan}.
   */
  public static Vector<PersonFile> findMembersByPlanning(Schedule plan) {
    //23092003 String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,p.note,e.instrument1 from personne p, eleve e, commande c, commande_cours cc WHERE cc.cours = "+cours+" AND c.id = cc.idcmd and cc.datedebut <= '"+day+"' and cc.datefin >= '"+day+"' AND p.id = c.adh and p.id = e.idper ORDER by p.nom";
    //String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,p.note,p.droit_img,e.idper,e.instrument1,e.instrument2,e.profession,e.datenais,e.payeur,e.nadhesions,e.pratique,e.niveau  from personne p, eleve e, plage pl WHERE pl.cours = " + cours + " AND pl.day = '" + day + "' AND pl.prof = " + idprof + " AND p.id = pl.adherent and p.id = e.idper ORDER by p.nom";
    String query = "SELECT " + PersonIO.COLUMNS + "," + MemberIO.COLUMNS
            + " FROM " + PersonIO.TABLE + ", " + MemberIO.TABLE + ", plage pl "
            + " WHERE pl.idplanning = " + plan.getId()
            + " AND personne.id = pl.adherent "
            + " AND personne.id = eleve.idper ORDER BY personne.nom";
    return ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findRegistered(query);
//    return ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findId(query, false);
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
    return CourseOrderIO.find(" AND cc.idcmd=" + i + " AND cc.module=" + m, dc);
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
   * Updates the scheduled ranges when course order is modified.
   *
   * @param ccours course order
   * @param start date
   * @param m member id
   * @throws SQLException
   */
  private void updateRange(CourseOrder ccours, int m) throws SQLException {

    String query = "WHERE action = " + ccours.getAction()
            + " AND jour >='" + ccours.getDateStart() + "'"
            + " AND jour <= '" + ccours.getDateEnd() + "'";
    Vector<Schedule> v = ScheduleIO.findCourse(query, dc);

    if (v.size() < 1) {
      return;
    }
    ScheduleRange h = new ScheduleRange();
    h.setMemberId(m);

    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      h.setScheduleId(p.getId());
      h.setStart(ccours.getStart());
      h.setEnd(ccours.getEnd());
      ScheduleRangeIO.insert(h, dc);
    }
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

  public void create(Order cmd) {
  }

  public void delete(Order cmd) throws Exception {
    OrderIO.delete(cmd, dc);
  }
}
