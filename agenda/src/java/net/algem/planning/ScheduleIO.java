/*
 * @(#)ScheduleIO.java	1.0.4 22/05/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import net.algem.room.Room;
import net.algem.util.AbstractGemDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * IO methods for class {@link net.algem.planning.Schedule}.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.4
 * @since 1.0.0 11/02/13
 */
@Repository
public class ScheduleIO
        extends AbstractGemDao
{

  public final static String TABLE = "planning";
  public final static String SEQUENCE = "planning_id_seq";
  public final static String COLUMNS = "p.id,p.jour,p.debut,p.fin,p.ptype,p.idper,p.action,p.lieux,p.note";

  public void insert(Schedule p) throws SQLException {

    int id = nextId(SEQUENCE);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + id
            + ",'" + p.getDate().toString() + "'"
            + ",'" + p.getStart() + "'"
            + ",'" + p.getEnd() + "'"
            + "," + p.getType()
            + "," + p.getIdPerson()
            + "," + p.getIdAction()
            + "," + p.getPlace()
            + "," + p.getNote()
            + ")";
  }

  public void update(Schedule p) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "jour = '" + p.getDate()
            + "',debut = '" + p.getStart()
            + "',fin = '" + p.getEnd()
            + "',ptype = " + p.getType()
            + ",idper = " + p.getIdPerson()
            + ",action = " + p.getIdAction()
            + ",lieux = " + p.getPlace()
            + ",note = " + p.getNote()
            + " WHERE id = " + p.getId();


  }

  public void delete(Schedule p) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
  }

  /**
   * Retrieves the schedules of the {@code date}.
   *
   * @param date selected date
   * @param estab establishment number
   * @return a list of schedule elements
   */
  public List<ScheduleElement> find(Date date, int estab) {
    String query = " SELECT p.*, c.titre, c.collectif, s.nom, t.prenom, t.nom"
            + " FROM " + TABLE + " p INNER JOIN action a LEFT OUTER JOIN cours c ON (a.cours = c.id)"
            + " ON (p.action = a.id) LEFT OUTER JOIN personne t ON (t.id = p.idper)"
            + ", salle s"
            + " where p.lieux = s.id"
            + " AND s.public = true"
            + " AND jour = ?"
            + " AND s.etablissement = ?"
            + " ORDER BY s.nom, p.debut";
    return jdbcTemplate.query(query, new RowMapper<ScheduleElement>()
    {

      @Override
      public ScheduleElement mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScheduleElement d = new ScheduleElement();
        d.setId(rs.getInt(1));
        d.setDate(new DateFr(rs.getString(2)));
        d.setStart(new Hour(rs.getString(3)));
        d.setEnd(new Hour(rs.getString(4)));
        d.setType(rs.getInt(5));
        d.setIdPerson(rs.getInt(6));
        d.setIdAction(rs.getInt(7));
        d.setPlace(rs.getInt(8));
        d.setNote(rs.getInt(9));
        d.setCourseName(rs.getString(10));
        d.setCollective(rs.getBoolean(11));
        d.setRoomName(rs.getString(12));
        d.setPersonName(rs.getString(13), rs.getString(14));
        if (d.type == Schedule.COURSE && !d.isCollective()) {
          d.setRanges(getTimeSlots(d.getId()));
        }
        return d;
      }
    }, date, estab);
  }

  /**
   * Gets the list of free public rooms (whithout any schedule for this
   * {@code date} in {@code estab}.
   * @param date selected date
   * @param estab establishment number
   * @return a list of rooms
   */
  public List<Room> getFreeRoom(Date date, int estab) {
    String query = "SELECT id, nom FROM salle s"
            + " WHERE s.public = true"
            + " AND s.etablissement = " + estab
            + " AND s.id NOT IN ("
            + " SELECT lieux FROM " + TABLE + " WHERE jour = '" + date + "') ORDER BY id";
    return jdbcTemplate.query(query, new RowMapper<Room>()
    {

      @Override
      public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt(1));
        r.setName(rs.getString(2));
        return r;
      }
    });
  }

  /**
   * Retrieves the time slots included in schedule which id is {@code id}.
   *
   * @param id schedule's id
   * @return a collection of schedule ranges
   */
  private Collection<ScheduleRange> getTimeSlots(int id) {
    String query = " SELECT pl.* FROM " + TABLE + " p," + ScheduleRangeIO.TABLE + " pl"
            + " WHERE pl.idplanning = p.id AND p.id = ?";
    return jdbcTemplate.query(query, new RowMapper<ScheduleRange>()
    {

      @Override
      public ScheduleRange mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScheduleRange r = new ScheduleRange();
        r.setId(rs.getInt(1));
        r.setScheduleId(rs.getInt(2));
        r.setStart(new Hour(rs.getString(3)));
        r.setEnd(new Hour(rs.getString(4)));
        r.setMemberId(rs.getInt(5));
        r.setNote(rs.getInt(6));

        return r;
      }
    }, id);
  }


}
