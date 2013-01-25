/*
 * @(#)ScheduleIO.java	2.7.a 26/11/12
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
package net.algem.planning;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.group.Group;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.planning.Schedule}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class ScheduleIO
        extends TableIO
{

  public final static String TABLE = "planning";
  public final static String SEQUENCE = "planning_id_seq";
  public final static String FOLLOW_UP_SEQUENCE = "idsuivi";
  public final static String COLUMNS = "p.id,p.jour,p.debut,p.fin,p.ptype,p.idper,p.action,p.lieux,p.note";
  public static String BUSY_ROOM_STMT = "SELECT count(id) FROM planning WHERE lieux = ? AND jour > '01-01-1999'";
  //private static String findHistoRepet = "SELECT "+COLUMNS+" FROM planning p WHERE p.ptype="+Schedule.MEMBER_SCHEDULE+" AND p.idper= ? AND (day BETWEEN ? AND ?) ORDER BY day,start";
  
  public static void insert(Schedule p, DataConnection dc) throws SQLException {

    int id = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + id
            + ",'" + p.getDay().toString() + "'"
            + ",'" + p.getStart() + "'"
            + ",'" + p.getEnd() + "'"
            + "," + p.getType()
            + "," + p.getIdPerson()
            + "," + p.getIdAction()
            + "," + p.getPlace()
            + "," + p.getNote()
            + ")";

    dc.executeUpdate(query);
    p.setId(id);
  }

  public static void insert(ScheduleDTO p, DataConnection dc) throws SQLException {
    
    int id = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + id
            + ",'" + p.getDay().toString() + "'"
            + ",'" + p.getStart() + "'"
            + ",'" + p.getEnd() + "'"
            + "," + p.getType()
            + "," + p.getPersonId()
            + "," + p.getAction()
            + "," + p.getPlace()
            + "," + p.getNote()
            + ")";

    dc.executeUpdate(query);
  }

  public static void update(Schedule p, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "jour = '" + p.getDay()
            + "',debut = '" + p.getStart()
            + "',fin = '" + p.getEnd()
            + "',ptype = " + p.getType()
            + ",idper = " + p.getIdPerson()
            + ",action = " + p.getIdAction()
            + ",lieux = " + p.getPlace()
            + ",note = " + p.getNote()
            + " WHERE id = " + p.getId();

    dc.executeUpdate(query);
  }

  public static void delete(Schedule p, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
    dc.executeUpdate(query);
  }

  public static void delete(String conditions, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE " + conditions;
    dc.executeUpdate(query);
  }

  public static void deleteRehearsal(DateFr startDate, DateFr endDate, ScheduleObject sched, DataConnection dc) throws SQLException {
    String query = "jour >= '" + startDate + "' AND jour <= '" + endDate + "'"
            + " AND (ptype = " + Schedule.GROUP_SCHEDULE + " OR ptype=" + Schedule.MEMBER_SCHEDULE + ")"
            + " AND action = " + sched.getIdAction();
    //+ " AND idper = " + plan.getIdPerson()
    //+ " AND start = '" + plan.getDateStart() + "' AND end ='" + plan.getDateEnd() + "'"
    //+ " AND place = " + plan.getPlace();
    delete(query, dc);
    //dc.executeUpdate(query);
  }

  /**
   * Suppression d'un planning.
   * La suppression n'est possible que si le planning ne comporte plus de plages durant la période choisie.
   * @param dc
   * @param action object de planification
   * @return 0 si le planning est vide, le nombre de plages existantes sinon
   * @throws SQLException
   */
  public static int deleteSchedule(Action action, DataConnection dc) throws SQLException {
    int rows = containRanges(action, dc);
    // s'il n'existe aucune plage active pour ce planning
    if (rows == 0) {
      String query = getDeleteScheduleSelection(action);
      dc.executeUpdate(query);
    }
    return rows;
  }

  public static Vector<Schedule> find(String where, DataConnection dc) {
    return findClass(Schedule.class, where, dc);
  }

  public static Vector<Schedule> findCourse(String where, DataConnection dc) {
    return findClass(CourseScheduleObject.class, where, dc);
  }

  public static Schedule findId(int id, DataConnection dc) {
    //String query = "SELECT id,day,dateDebut,dateFin,ptype,idper,action,place,idsuivi from planning where id="+id;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " p WHERE id = " + id;
    Schedule p = null;
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        p = new Schedule();
        p.setId(rs.getInt(1));
        p.setDay(new DateFr(rs.getString(2)));
        p.setStart(new Hour(rs.getString(3)));
        p.setEnd(new Hour(rs.getString(4)));
        p.setType(rs.getInt(5));
        p.setIdPerson(rs.getInt(6));
        p.setIdAction(rs.getInt(7));
        p.setPlace(rs.getInt(8));
        p.setNote(rs.getInt(9));
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return p;
  }

  /* NON utilisée */
  /*public static Vector<Planning> findCoursInscript(DataCache dc, String where, DateFr day, int cours) {
  Vector<Planning> v = new Vector<Planning>();
  String query = "SELECT p.id,p.day,p.start,p.end,p.ptype,p.idper,p.action,p.place,p.idsuivi from planning p, commande_module cm, commande_cours cc " + where + " and cm.start <= '" + day + "' and cm.end >= '" + day + "' and cm.idcmd=cc.idcmd and cc.cours=" + cours;
  try {
  ResultSet rs = dc.executeQuery(query);
  while (rs.next()) {
  Schedule p = new Schedule();
  p.setId(rs.getInt(1));
  p.setDay(new DateFr(rs.getString(2)));
  p.setDateStart(new Hour(rs.getString(3)));
  p.setDateEnd(new Hour(rs.getString(4)));
  p.setType(rs.getInt(5));
  p.setIdPerson(rs.getInt(6));
  p.setAction(rs.getInt(7));
  p.setLieux(rs.getInt(8));
  p.setNote(rs.getInt(9));

  v.addElement(p);
  }
  rs.close();
  } catch (Exception e) {
  dc.logException(e);
  }
  return v;
  }*/
  
  static Vector<Schedule> findClass(Class c, String where, DataConnection dc) {
    Vector<Schedule> v = new Vector<Schedule>();
    //String query = "SELECT p.id,p.day,p.dateDebut,p.dateFin,p.ptype,p.idper,p.action,p.place,p.idsuivi from planning p "+where;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " p " + where;

    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Constructor ct = c.getConstructor();
        Schedule p = (Schedule) ct.newInstance();
        p.setId(rs.getInt(1));
        p.setDay(new DateFr(rs.getString(2)));
        p.setStart(new Hour(rs.getString(3)));
        p.setEnd(new Hour(rs.getString(4)));
        p.setType(rs.getInt(5));
        p.setIdPerson(rs.getInt(6));
        p.setIdAction(rs.getInt(7));
        p.setPlace(rs.getInt(8));
        p.setNote(rs.getInt(9));

        v.addElement(p);
      }
      rs.close();
    } catch (Exception e) {
      GemLogger.logException(e);
    }
    return v;
  }

  /*public static Vector<PlanningObject> find(DataCache dc, int idper, DateFr start, DateFr end) throws SQLException {
  PreparedStatement ps = dc.prepareStatement(findHistoRepet);
  ps.setInt(1, idper);
  ps.setDate(2, new java.sql.Date(start.getTime()));
  ps.setDate(3, new java.sql.Date(end.getTime()));
  return getLoadRS(dc, ps);
  }*/
  private static void fillPlanning(ResultSet rs, ScheduleObject p, DataConnection dc)
          throws SQLException {
    p.setId(rs.getInt(1));
    p.setDay(new DateFr(rs.getString(2)));
    p.setStart(new Hour(rs.getString(3)));
    p.setEnd(new Hour(rs.getString(4)));
    p.setType(rs.getInt(5));
    p.setIdPerson(rs.getInt(6));
    p.setIdAction(rs.getInt(7));
    p.setPlace(rs.getInt(8));
    p.setNote(rs.getInt(9));

    p.setRoom((Room) DataCache.findId(p.getPlace(), Model.Room));
    
  }

  public static Vector<ScheduleObject> getLoadRS(PreparedStatement ps, DataConnection dc)
          throws SQLException {
    Vector<ScheduleObject> v = new Vector<ScheduleObject>();
    PlanningService service = new PlanningService(dc);

    ResultSet rs = ps.executeQuery();
    while (!Thread.interrupted() && rs.next()) {
      ScheduleObject p = planningObjectFactory(rs, service, dc);
      v.addElement(p);
    }
    rs.close();
    return v;
  }
  
  private static ScheduleObject planningObjectFactory(ResultSet rs, PlanningService service, DataConnection dc)
          throws SQLException {
    ScheduleObject p = null;
    
    switch (rs.getInt(5)) {
      case Schedule.COURSE_SCHEDULE:
        p = new CourseSchedule();
        fillPlanning(rs, p, dc);
        ((CourseSchedule) p).setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Teacher));
        Action a = service.getAction(p.getIdAction());
        ((CourseSchedule) p).setAction(a);
//        ((CourseSchedule) p).setCourse(((CourseIO) DataCache.getDao(Model.Course)).findIdByAction(p.getIdAction())) ;
        ((CourseSchedule) p).setCourse((Course) DataCache.findId(a.getCourse(), Model.Course));

        break;

      //case Schedule.ACTION_SCHEDULE :
      //	p = new ScheduleObject();
      //	fillPlanning(dc, rs, p);
      //	break;

      case Schedule.MEMBER_SCHEDULE:
        p = new MemberRehearsalSchedule();
        fillPlanning(rs, p, dc);
        ((MemberRehearsalSchedule) p).setMember((Person) DataCache.findId(p.getIdPerson(), Model.Person));
        break;

      case Schedule.GROUP_SCHEDULE:
        p = new GroupRehearsalSchedule();
        fillPlanning(rs, p, dc);
        ((GroupRehearsalSchedule) p).setGroup((Group) DataCache.findId(p.getIdPerson(), Model.Group));
        break;

      case Schedule.WORKSHOP_SCHEDULE:
        p = new WorkshopSchedule();
        fillPlanning(rs, p, dc);
//        ((WorkshopSchedule) p).setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Person));
        ((WorkshopSchedule) p).setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Teacher));
        int w = service.getAction(p.getIdAction()).getCourse();
        ((WorkshopSchedule) p).setWorkshop((Course) DataCache.findId(w, Model.Course));
        break;
    }
    return p;

  }

  public static Vector<ScheduleObject> findObject(String where, DataConnection dc)
          throws SQLException {
    Vector<ScheduleObject> v = new Vector<ScheduleObject>();
    PlanningService service = new PlanningService(dc);
    //String query = "SELECT id,day,dateDebut,dateFin,ptype,idper,action,place,idsuivi from planning  "+where;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " p " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ScheduleObject p = planningObjectFactory(rs, service, dc);
      v.addElement(p);
    }
    rs.close();
    return v;
  }

  public static int count(String where, DataConnection dc) {
    int n = 1;
    String query = "SELECT count(*) FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        n = rs.getInt(1);
      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return n;
  }

  public static void createFollowUp(Schedule sched, String text, DataConnection dc) throws PlanningException {

    try {
      dc.setAutoCommit(false);
      int num = nextId(FOLLOW_UP_SEQUENCE, dc);
      String query = "INSERT INTO suivi VALUES(" + num + ",'" + escape(text) + "')";
      //+ "," + plan.getId()
      dc.executeUpdate(query);
      sched.setNote(num);
      update(sched, dc);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public static void updateFollowUp(int idFollow, String text, DataConnection dc) throws SQLException {
    String query = "UPDATE suivi SET texte = '" + escape(text) + "' WHERE id = " + idFollow;
    dc.executeUpdate(query);
  }

  public static String findFollowUp(int note, DataConnection dc) throws SQLException {
    String text = "";
    String query = "SELECT texte FROM suivi WHERE id = " + note;

    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      text = unEscape(rs.getString(1));
    }
    return text;
  }

  private static String getDeleteScheduleSelection(Action a) {
    String query = "DELETE FROM " + TABLE
            + " WHERE jour >= '" + a.getDateStart() + "' AND jour <= '" + a.getDateEnd() + "'"
            + " AND action = " + a.getId();
    /*+ " AND date_part('dow', day) = " + (a.getDay())
    //+ "' and ptype=1 and action="+plan.getAction()
    + " AND action=" + a.getCourse()
    + " AND idper=" + a.getTeacher()
    + " AND start='" + a.getHourStart()
    + "' AND end='" + a.getHourEnd()
    + "' AND place=" + a.getRoom();*/
    return query;
  }

  /**
   * Gets the number of schedule ranges for the planification  {@code a}.
   * 
   * @param dc dataCache
   * @param a action
   * @return a number of ranges (may be 0)
   * @throws SQLException
   */
  public static int containRanges(Action a, DataConnection dc) throws SQLException {
    int rows = 0;
    String query = "SELECT COUNT(pg.debut) AS nb_cours FROM " + ScheduleRangeIO.TABLE + " pg, " + TABLE + " p"
            + " WHERE pg.idplanning = p.id AND p.action = " + a.getId()
            + " AND p.jour >= '" + a.getDateStart() + "' AND p.jour <= '" + a.getDateEnd() + "'";
    /*+ " WHERE day >= '" + a.getDateStart() + "' AND day <= '" + a.getDateEnd() + "'"
    + " AND date_part('dow', day) = " + (a.getDay()) // lundi = 1 pour postgresql
    + " AND cours=" + a.getCourse()
    + " and prof=" + a.getTeacher()
    + " AND start >='" + a.getHourStart() + "' AND end <='" + a.getHourEnd() + "'"
    + " AND salle=" + a.getRoom();*/
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      rows = rs.getInt("nb_cours");
    }
    return rows;
  }

  /**
   * Gets a result set listing start and end time of schedules
   * which type, date and action equal to model {@code p}.
   * 
   * @param dc
   * @param p model
   * @return a résultset
   * @throws SQLException 
   */
  public static ResultSet getRSCourseRange(Schedule p, DataConnection dc) throws SQLException {

    String query = "SELECT debut, fin FROM " + TABLE + " WHERE ptype = " + p.getType()
            + " AND jour = '" + p.getDay() + "'"
            + " AND action = " + p.getIdAction() + " ORDER BY debut";
    return dc.executeQuery(query);
  }
}
