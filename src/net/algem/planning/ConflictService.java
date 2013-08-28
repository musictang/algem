/*
 * @(#)ConflictService.java	2.8.h 03/06/13
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import net.algem.contact.Person;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;

/**
 * Service class for conflict verification.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.h
 * @since 2.4.a 08/05/12
 */
public class ConflictService
{

  private DataConnection dc;
  private PreparedStatement testRoomPS;
  private PreparedStatement testRoomPS3;
  private PreparedStatement testTeacherPS, testTeacherPS2;
  private PreparedStatement testMemberPS;
  private PreparedStatement testMemberSchedulePS;
  private PreparedStatement testHourPS;

  public ConflictService(DataConnection dc) {
    this.dc = dc;
    testHourPS = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p "
			+ "WHERE jour=? AND lieux=? "
			+ "AND ((debut >= ? AND debut < ?) "
			+ "OR (fin > ? AND fin <= ?) "
			+ "OR (debut <= ? AND fin >= ?)) and p.idper!=?");
    testRoomPS = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p "
			+ "WHERE jour = ? AND lieux = ? "
			+ "AND ((debut >= ? AND debut < ?) "
			+ "OR (fin > ? and fin <= ?) OR (debut <= ? AND fin >= ?))");
//    testRoomPS2 = this.dc.prepareStatement("SELECT "+ScheduleIO.COLUMNS+" FROM planning p "
//			+ "WHERE jour = ? AND lieux = ? AND debut = ? AND fin = ?");
    testRoomPS3 = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p "
			+ "WHERE jour = ? AND lieux = ?"
			+ " AND ((debut >= ? AND debut < ?)"
			+ " OR (fin > ? and fin <= ?) OR (debut <= ? AND fin >= ?))"
            + " AND p.id != ?");
    testTeacherPS = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p "
			+ "WHERE jour = ? AND idper = ?"
			+ " AND ((debut >= ? AND debut < ?) OR (fin > ? AND fin <= ?) "
			+ " OR (debut <= ? AND fin >= ?))");

    testTeacherPS2 = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p "
			+ "WHERE jour = ? AND idper = ?"
			+ " AND ((debut >= ? AND debut < ?) OR (fin > ? AND fin <= ?)"
			+ " OR (debut <= ? AND fin >= ?))"
            + " AND p.id != ?");
    testMemberPS = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p "
			+ "WHERE (ptype = "+Schedule.COURSE_SCHEDULE+" OR ptype = "+Schedule.MEMBER_SCHEDULE+") "
			+ "AND jour = ? AND idper = ? "
			+ "AND ((debut >= ? AND debut < ?) "
			+ "OR (fin > ? AND fin <= ?) OR (debut <= ? AND fin >= ?))");
    testMemberSchedulePS = this.dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p, plage pl "
			+ "WHERE ptype = "+Schedule.COURSE_SCHEDULE
			+ " AND p.id = pl.idplanning"
			+ " AND p.jour = ? AND pl.adherent = ?"
			+ " AND ((pl.debut >= ? AND pl.debut < ?)"
			+ " OR (pl.fin > ? AND pl.fin <= ?) OR (pl.debut <= ? AND pl.fin >= ?))");
    
  }

  /**
   * Test time occupation for hour modification dialog.
   * @param plan planning
   * @param startDate
   * @param endDate
   * @param hStart start time
   * @param hEnd end time
   * @return a list of conflicts
   * @throws SQLException
   */
  public Vector<ScheduleTestConflict> testHourConflict(ScheduleObject plan, DateFr startDate, DateFr endDate, Hour hStart, Hour hEnd) throws SQLException {
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();

    // tous les plannings entre 2 dates avec même action et même lieu (peu importe le date de la semaine ou le prof)
    String query = "WHERE jour >='" + startDate + "' AND jour <= '" + endDate + "'"
            + " AND action=" + plan.getIdAction()
            + " AND lieux=" + plan.getPlace();

    Vector<Schedule> v = ScheduleIO.find(query, dc);
    String end = hEnd.toString();
    if (end.equals("24:00")) {
      end = "23:59:59";
    } else {
      end = hEnd.toString() + ":00";
    }
    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);

      testHourPS.setDate(1, new java.sql.Date(p.getDate().getTime()));
      testHourPS.setInt(2, plan.getPlace());
      testHourPS.setTime(3, java.sql.Time.valueOf(hStart.toString() + ":00"));
      //testHeurePS.setTime(4, java.sql.Time.valueOf(hf.toString()+":00"));
      testHourPS.setTime(4, java.sql.Time.valueOf(end));
      testHourPS.setTime(5, java.sql.Time.valueOf(hStart.toString() + ":00"));
      //testHeurePS.setTime(6, java.sql.Time.valueOf(hf.toString()+":00"));
      testHourPS.setTime(6, java.sql.Time.valueOf(end));
      testHourPS.setTime(7, java.sql.Time.valueOf(hStart.toString() + ":00"));
      //testHeurePS.setTime(8, java.sql.Time.valueOf(hf.toString()+":00"));
      testHourPS.setTime(8, java.sql.Time.valueOf(end));
      testHourPS.setInt(9, plan.getIdPerson());

      Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testHourPS, dc);
      for (int j = 0; j < v2.size(); j++) {
        ScheduleObject pc = v2.elementAt(j);
        ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
        conflict.setRoomFree(false);
        conflict.setDetail(pc.getScheduleDetail());
        conflicts.addElement(conflict);
      }
    }
    return conflicts;
  }

  /**
   * Test room occupation for room modification dialog.
   * Time and date of week rest the same.
   * Don't work for single workshops when new date is before actual date.
   * @param plan schedule
   * @param dateStart new date start
   * @param dateEnd new date end
   * @param roomId new room id
   * @return a list of conflicts
   * @throws java.sql.SQLException
   */
  public Vector<ScheduleTestConflict> testRoomConflict(ScheduleObject plan, DateFr dateStart, DateFr dateEnd, int roomId) throws SQLException {
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(plan.getDate().getDate());
    int dow = cal.get(Calendar.DAY_OF_WEEK);

    // tous les plannings avec la même action ayant lieu le même jour de la semaine dans la même salle
    // et aux mêmes heures
    String query = "WHERE jour >= '" + dateStart + "' AND jour <= '" + dateEnd + "'"
            + " AND date_part('dow',jour)=" + (dow - 1) // SUNDAY=1
            + " AND debut = '" + plan.getStart() + "' AND fin = '" + plan.getEnd() + "'"
            + " AND action = " + plan.getIdAction()
            + " AND lieux = " + plan.getPlace();
    // sélection des plannings dont on veut modifier la salle
    Vector<Schedule> v = ScheduleIO.find(query, dc);
    // pour chaque instance de planning, vérifier l'occupation
    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      Hour hfin = p.getEnd();
      hfin.decMidnight();
      testRoomPS.setDate(1, new java.sql.Date(p.getDate().getTime()));
      testRoomPS.setInt(2, roomId);
      testRoomPS.setTime(3, java.sql.Time.valueOf(p.getStart().toString() + ":00"));
      testRoomPS.setTime(4, java.sql.Time.valueOf(hfin.toString() + ":00"));
      testRoomPS.setTime(5, java.sql.Time.valueOf(p.getStart().toString() + ":00"));
      testRoomPS.setTime(6, java.sql.Time.valueOf(hfin.toString() + ":00"));
      testRoomPS.setTime(7, java.sql.Time.valueOf(p.getStart().toString() + ":00"));
      testRoomPS.setTime(8, java.sql.Time.valueOf(hfin.toString() + ":00"));
      // Stocker les conflits dans un vecteur
      Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testRoomPS, dc);
      for (int j = 0; j < v2.size(); j++) {
        ScheduleObject pc = v2.elementAt(j);
        ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
        conflict.setRoomFree(false);
        conflict.setDetail(pc.getScheduleDetail());
        conflicts.addElement(conflict);
      }
    }
    return conflicts;
  }

  /**
   * Used for member pass rehearsal.
   * @param dateStart
   * @param newHourStart
   * @param newHourEnd
   * @param roomId
   * @return a list of conflicts
   * @throws SQLException 
   */
  public Vector<ScheduleTestConflict> testRoomConflict(DateFr dateStart, Hour newHourStart, Hour newHourEnd, int roomId) throws SQLException {
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    newHourEnd.decMidnight();
    testRoomPS.setDate(1, new java.sql.Date(dateStart.getTime()));
    testRoomPS.setInt(2, roomId);
    testRoomPS.setTime(3, java.sql.Time.valueOf(newHourStart.toString() + ":00"));
    testRoomPS.setTime(4, java.sql.Time.valueOf(newHourEnd.toString() + ":00"));
    testRoomPS.setTime(5, java.sql.Time.valueOf(newHourStart.toString() + ":00"));
    testRoomPS.setTime(6, java.sql.Time.valueOf(newHourEnd.toString() + ":00"));
    testRoomPS.setTime(7, java.sql.Time.valueOf(newHourStart.toString() + ":00"));
    testRoomPS.setTime(8, java.sql.Time.valueOf(newHourEnd.toString() + ":00"));

    Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testRoomPS, dc);
    for (int j = 0; j < v2.size(); j++) {
      ScheduleObject pc = v2.elementAt(j);
      ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
      conflict.setRoomFree(false);
      conflict.setDetail(pc.getScheduleDetail());
      conflicts.addElement(conflict);
    }
    return conflicts;
  }
  
  /**
   * Test room occupation for schedule time modification dialog and schedule postpone.
   * 
   * @param origScheduleId
   * @param newPlan
   * @return a list of schedules
   * @throws SQLException 
   */
  public Vector<ScheduleTestConflict> testRoomConflict(int origScheduleId, ScheduleObject newPlan) throws SQLException {
    
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    Hour newHourEnd = newPlan.getEnd();
    newHourEnd.decMidnight();
    testRoomPS3.setDate(1, new java.sql.Date(newPlan.getDate().getTime()));
    testRoomPS3.setInt(2, newPlan.getPlace());
    testRoomPS3.setTime(3, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
    testRoomPS3.setTime(4, java.sql.Time.valueOf(newHourEnd.toString() + ":00"));
    testRoomPS3.setTime(5, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
    testRoomPS3.setTime(6, java.sql.Time.valueOf(newHourEnd.toString() + ":00"));
    testRoomPS3.setTime(7, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
    testRoomPS3.setTime(8, java.sql.Time.valueOf(newHourEnd.toString() + ":00"));
    testRoomPS3.setInt(9, origScheduleId);

    Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testRoomPS3, dc);
    for (int j = 0; j < v2.size(); j++) {
      ScheduleObject pc = v2.elementAt(j);
      ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
      conflict.setRoomFree(false);
      conflict.setDetail(pc.getScheduleDetail());
      conflicts.addElement(conflict);
    }
    return conflicts;
  }


  /**
   * Test room occupation for modification of planning length.
   *
   * @param plan schedule object
   * @param hStart new start time
   * @param hEnd new end time
   * @param lastDate date of modification
   * @return a list of conflicts
   * @throws SQLException
   */
  public Vector<ScheduleTestConflict> testRoomConflict(ScheduleObject plan, Hour hStart, Hour hEnd, DateFr lastDate) throws SQLException {
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    String query = "WHERE jour BETWEEN '" + plan.getDate() + "' AND '" + lastDate + "'"
            + " AND action = " + plan.getIdAction() + " AND lieux = " + plan.getPlace()
            + " AND debut = '" + plan.getStart() + "' AND fin = '" + plan.getEnd() + "'";

    Vector<Schedule> v = ScheduleIO.find(query, dc);
    hEnd.decMidnight();
    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);

      testRoomPS3.setDate(1, new java.sql.Date(p.getDate().getTime()));
      testRoomPS3.setInt(2, plan.getPlace());
      testRoomPS3.setTime(3, java.sql.Time.valueOf(hStart.toString() + ":00"));
      testRoomPS3.setTime(4, java.sql.Time.valueOf(hEnd.toString() + ":00"));
      testRoomPS3.setTime(5, java.sql.Time.valueOf(hStart.toString() + ":00"));
      testRoomPS3.setTime(6, java.sql.Time.valueOf(hEnd.toString() + ":00"));
      testRoomPS3.setTime(7, java.sql.Time.valueOf(hStart.toString() + ":00"));
      testRoomPS3.setTime(8, java.sql.Time.valueOf(hEnd.toString() + ":00"));
      testRoomPS3.setInt(9, p.getId());

      Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testRoomPS3, dc);
      for (int j = 0; j < v2.size(); j++) {
        ScheduleObject pc = v2.elementAt(j);
        ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
        conflict.setRoomFree(false);
        conflict.setDetail(pc.getScheduleDetail());
        conflicts.addElement(conflict);
      }
    }
    return conflicts;
  }
  
  /**
   * Test schedule occupation for modification of planning length.
   * @param plan schedule
   * @param hStart new start time
   * @param hEnd new end time
   * @param lastDate last date of modification
   * @return a list of conflicts
   * @throws SQLException 
   */
  public Vector<ScheduleTestConflict> testRange(ScheduleObject plan, Hour hStart, Hour hEnd,  DateFr lastDate) 
          throws SQLException {
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    String where = "pg WHERE pg.idplanning IN ("
            + "SELECT id FROM " + ScheduleIO.TABLE 
            + " WHERE jour BETWEEN '" + plan.getDate() + "' AND '" + lastDate
            + "' AND lieux = " + plan.getPlace()
            + " AND action = " + plan.getIdAction()
            + " AND debut = '" + plan.getStart() + "' AND fin = '" + plan.getEnd() + "')";
    Vector<ScheduleRange> v = ScheduleRangeIO.find(where, dc);
    for (int i = 0; i < v.size(); i++) {
      ScheduleRange range = v.elementAt(i);
      if ((hStart.after(range.getStart()) && plan.getStart().toMinutes() <= range.getStart().toMinutes())
              || (hEnd.before(range.getEnd()) && plan.getEnd().toMinutes() >= range.getEnd().toMinutes())) {
        Schedule s = ScheduleIO.findId(range.getScheduleId(), dc);
        if (s != null) {
          ScheduleTestConflict conflict = new ScheduleTestConflict(s.getDate(), range.getStart(), range.getEnd());
          conflict.setTeacherFree(false);
          conflicts.add(conflict);
        }
      }
    }
    return conflicts;
  }

  /**
   * Test teacher occupation for modification teacher dialog.
   * @param orig initial schedule
   * @param newPlan new schedule
   * @param dateStart start date
   * @param dateEnd end date
   * @return a list of conflicts
   * @throws SQLException
   */
  public Vector<ScheduleTestConflict> testTeacherConflict(ScheduleObject orig, ScheduleObject newPlan, DateFr dateStart, DateFr dateEnd) throws SQLException {

    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    String query = "WHERE jour >= '" + dateStart + "' AND jour <= '" + dateEnd + "' AND action = " + orig.getIdAction() + " AND idper = " + orig.getIdPerson();
    Vector<Schedule> v = ScheduleIO.find(query, dc);

    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);

      testTeacherPS.setDate(1, new java.sql.Date(p.getDate().getTime()));
      testTeacherPS.setInt(2, newPlan.getIdPerson());
      testTeacherPS.setTime(3, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
      testTeacherPS.setTime(4, java.sql.Time.valueOf(newPlan.getEnd().toString() + ":00"));
      testTeacherPS.setTime(5, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
      testTeacherPS.setTime(6, java.sql.Time.valueOf(newPlan.getEnd().toString() + ":00"));
      testTeacherPS.setTime(7, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
      testTeacherPS.setTime(8, java.sql.Time.valueOf(newPlan.getEnd().toString() + ":00"));    

      Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testTeacherPS, dc);
      for (int j = 0; j < v2.size(); j++) {
        ScheduleObject pc = v2.elementAt(j);
        ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
        conflict.setTeacherFree(false);
        conflict.setDetail(pc.getScheduleDetail());
        conflicts.addElement(conflict);
      }
    }
    // search schedule range overlap
    //conflicts.addAll(getRangeConflicts(orig, newPlan, dateStart, dateEnd));
    
    return conflicts;
  }
  
  Vector<ScheduleTestConflict> getRangeConflicts(ScheduleObject orig, ScheduleObject newPlan, DateFr dateStart, DateFr dateEnd)
          throws SQLException {
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();

    Vector<ScheduleRange> vr = getScheduleRanges(orig, dateStart, dateEnd);
    for (ScheduleRange sr : vr) {
      if (newPlan.getEnd().inside(sr.getStart(), sr.getEnd())
              || newPlan.getStart().inside(sr.getStart(), sr.getEnd())) {
        
        Schedule s = ScheduleIO.findId(sr.getScheduleId(), dc);
        ScheduleTestConflict conflict = new ScheduleTestConflict(s.getDate(), sr.getStart(), sr.getEnd());
        conflict.setMemberFree(false);
        Person p = (Person) DataCache.findId(sr.getMemberId(), Model.Person);
        conflict.setDetail(p.getFirstnameName());
        conflicts.addElement(conflict);
      }
    }
    return conflicts;
  }
  
  Vector<ScheduleTestConflict> getCollectiveRangeConflicts(ScheduleObject orig, ScheduleObject newPlan, DateFr dateStart, DateFr dateEnd)
          throws SQLException {
    
    Vector<ScheduleRange> vr = getScheduleRanges(orig, dateStart, dateEnd);
    Vector<ScheduleRange> origRanges = getScheduleRanges(orig);
    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();
    for (ScheduleRange sr : vr) {
      for (ScheduleRange or : origRanges) {
        if (or.getMemberId() == sr.getMemberId() 
                && (newPlan.getEnd().inside(sr.getStart(), sr.getEnd())
                || newPlan.getStart().inside(sr.getStart(), sr.getEnd()))) {

          Schedule s = ScheduleIO.findId(sr.getScheduleId(), dc);
          ScheduleTestConflict conflict = new ScheduleTestConflict(s.getDate(), sr.getStart(), sr.getEnd());
          conflict.setMemberFree(false);
          Person p = (Person) DataCache.findId(sr.getMemberId(), Model.Person);
          conflict.setDetail(p.getFirstnameName());
          conflicts.addElement(conflict);
        }
      }
    }
    return conflicts;
  }
  
  private Vector<ScheduleRange> getScheduleRanges(ScheduleObject schedule, DateFr dateStart, DateFr dateEnd) throws SQLException {
    String query = "pg WHERE idplanning IN("
            + "SELECT id FROM planning "
            + "WHERE jour >= '" + dateStart + "' AND jour <= '" + dateEnd 
            + "' AND action = " + schedule.getIdAction() 
            + " AND idper = " + schedule.getIdPerson()
            + ")";

    return ScheduleRangeIO.find(query, dc);
  }
  
  private Vector<ScheduleRange> getScheduleRanges(ScheduleObject schedule) throws SQLException {
    String query = "pg WHERE idplanning = " + schedule.getId();
    return ScheduleRangeIO.find(query, dc);
  }

  /**
   * Test teacher occupation for course postpone modification.
   * @param plan orig schedule
   * @param day new date of schedule
   * @param nhStart new start time
   * @param nhEnd new end time
   * @return a list of conflicts
   * @throws java.sql.SQLException
   * @since 2.0je
   */
  public Vector<ScheduleTestConflict> testTeacherConflict(ScheduleObject plan, DateFr day, Hour nhStart, Hour nhEnd) throws SQLException {

    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();

    testTeacherPS2.setDate(1, new java.sql.Date(day.getTime()));
    testTeacherPS2.setInt(2, plan.getIdPerson());
    testTeacherPS2.setTime(3, java.sql.Time.valueOf(nhStart.toString() + ":00"));
    testTeacherPS2.setTime(4, java.sql.Time.valueOf(nhEnd.toString() + ":00"));
    testTeacherPS2.setTime(5, java.sql.Time.valueOf(nhStart.toString() + ":00"));
    testTeacherPS2.setTime(6, java.sql.Time.valueOf(nhEnd.toString() + ":00"));
    testTeacherPS2.setTime(7, java.sql.Time.valueOf(nhStart.toString() + ":00"));
    testTeacherPS2.setTime(8, java.sql.Time.valueOf(nhEnd.toString() + ":00"));
    testTeacherPS2.setInt(9, plan.getId());

    Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testTeacherPS2, dc);
    for (int j = 0; j < v2.size(); j++) {
      ScheduleObject pc = v2.elementAt(j);
      ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
      conflict.setTeacherFree(false);
      conflict.setDetail(pc.getScheduleDetail());
      conflicts.addElement(conflict);
    }

    return conflicts;
  }
  
  /**
   * Test teacher occupation for schedule copy.
   *
   * @param teacherId teacher id
   * @param newPlan new schedule
   * @return a list of conflicts
   * @throws java.sql.SQLException
   * @since 2.0je
   */
  public Vector<ScheduleTestConflict> testTeacherConflict(int teacherId, ScheduleObject newPlan) throws SQLException {

    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();

    testTeacherPS.setDate(1, new java.sql.Date(newPlan.getDate().getTime()));
    testTeacherPS.setInt(2, teacherId);
    testTeacherPS.setTime(3, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
    testTeacherPS.setTime(4, java.sql.Time.valueOf(newPlan.getEnd().toString() + ":00"));
    testTeacherPS.setTime(5, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
    testTeacherPS.setTime(6, java.sql.Time.valueOf(newPlan.getEnd().toString() + ":00"));
    testTeacherPS.setTime(7, java.sql.Time.valueOf(newPlan.getStart().toString() + ":00"));
    testTeacherPS.setTime(8, java.sql.Time.valueOf(newPlan.getEnd().toString() + ":00"));

    Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testTeacherPS, dc);
    for (int j = 0; j < v2.size(); j++) {
      ScheduleObject pc = v2.elementAt(j);
      ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
      conflict.setTeacherFree(false);
      conflict.setDetail(pc.getScheduleDetail());
      conflicts.addElement(conflict);
    }

    return conflicts;
  }

  public Vector<ScheduleTestConflict> testMemberConflict(ScheduleObject plan, DateFr jour, Hour nhd, Hour nhf) throws SQLException {

    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();

    testMemberPS.setDate(1, new java.sql.Date(jour.getTime()));
    testMemberPS.setInt(2, plan.getIdPerson());
    testMemberPS.setTime(3, java.sql.Time.valueOf(nhd.toString() + ":00"));
    testMemberPS.setTime(4, java.sql.Time.valueOf(nhf.toString() + ":00"));
    testMemberPS.setTime(5, java.sql.Time.valueOf(nhd.toString() + ":00"));
    testMemberPS.setTime(6, java.sql.Time.valueOf(nhf.toString() + ":00"));
    testMemberPS.setTime(7, java.sql.Time.valueOf(nhd.toString() + ":00"));
    testMemberPS.setTime(8, java.sql.Time.valueOf(nhf.toString() + ":00"));

    Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testMemberPS, dc);
    for (int j = 0; j < v2.size(); j++) {
      ScheduleObject pc = v2.elementAt(j);
      ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
      conflict.setMemberFree(false);
      conflict.setDetail(pc.getScheduleDetail());
      conflicts.addElement(conflict);
    }

    return conflicts;
  }
	
	public Vector<ScheduleTestConflict> testMemberScheduleConflict(ScheduleObject plan, DateFr day, Hour nhStart, Hour nhEnd) throws SQLException {

    Vector<ScheduleTestConflict> conflicts = new Vector<ScheduleTestConflict>();

    testMemberSchedulePS.setDate(1, new java.sql.Date(day.getTime()));
    testMemberSchedulePS.setInt(2, plan.getIdPerson());
    testMemberSchedulePS.setTime(3, java.sql.Time.valueOf(nhStart.toString() + ":00"));
    testMemberSchedulePS.setTime(4, java.sql.Time.valueOf(nhEnd.toString() + ":00"));
    testMemberSchedulePS.setTime(5, java.sql.Time.valueOf(nhStart.toString() + ":00"));
    testMemberSchedulePS.setTime(6, java.sql.Time.valueOf(nhEnd.toString() + ":00"));
    testMemberSchedulePS.setTime(7, java.sql.Time.valueOf(nhStart.toString() + ":00"));
    testMemberSchedulePS.setTime(8, java.sql.Time.valueOf(nhEnd.toString() + ":00"));

    Vector<ScheduleObject> v2 = ScheduleIO.getLoadRS(testMemberSchedulePS, dc);
    for (int j = 0; j < v2.size(); j++) {
      ScheduleObject pc = v2.elementAt(j);
      ScheduleTestConflict conflict = new ScheduleTestConflict(pc.getDate(), pc.getStart(), pc.getEnd());
      conflict.setMemberFree(false);
      conflict.setDetail(pc.getScheduleDetail());
      conflicts.addElement(conflict);
    }

    return conflicts;
  }

}
