/*
 * @(#)TeacherIO.java	2.15.0 30/07/2017
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
package net.algem.contact.teacher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.PersonIO;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.teacher.Teacher}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 */
public class TeacherIO
        extends TableIO
        implements Cacheable {

    private static final String TABLE = "prof";
    private static final String COLUMNS = "idper, diplome1, diplome2, diplome3, actif";
    private DataConnection dc;

    public TeacherIO(DataConnection dc) {
        this.dc = dc;
    }

    /**
     * Inserts a new teacher. Transaction should be processed at higher level.
     *
     * @param t
     * @throws SQLException
     */
    public void insert(Teacher t) throws SQLException {
        String query = "INSERT INTO " + TABLE + " VALUES("
                + "'" + t.getId()
                + "','" + escape(t.getCertificate1())
                + "','" + escape(t.getCertificate2())
                + "','" + escape(t.getCertificate3())
                + "','" + (t.isActive() ? "t" : "f")
                + "'";
        query += ")";

        dc.executeUpdate(query);
        InstrumentIO.insert(t.getInstruments(), t.getId(), Instrument.TEACHER, dc);
    }

    /**
     * Updates a teacher. Transaction should be processed at higher level.
     *
     * @param t teacher instance
     * @throws SQLException
     * @see net.algem.contact.PersonFileEditor#save()
     */
    public void update(Teacher t) throws SQLException {
        String query = "UPDATE " + TABLE + " SET "
                + "diplome1 = '" + escape(t.getCertificate1())
                + "',diplome2 = '" + escape(t.getCertificate2())
                + "',diplome3 = '" + escape(t.getCertificate3())
                + "',actif = '" + (t.isActive() ? "t" : "f") + "'"
                + " WHERE idper = " + t.getId();

        dc.executeUpdate(query);
        InstrumentIO.delete(t.getId(), Instrument.TEACHER, dc);
        InstrumentIO.insert(t.getInstruments(), t.getId(), Instrument.TEACHER, dc);
    }

    /**
     * Gets the number of schedules occupied by a teacher.
     *
     * @param id teacher id
     * @return 0 or an integer > 0
     * @throws SQLException
     */
    public int hasSchedules(int id) throws SQLException {
        String query = "SELECT count(idper) FROM " + ScheduleIO.TABLE
                + " WHERE idper = " + id
                + " AND ptype IN (" + Schedule.COURSE + ", " + Schedule.WORKSHOP + ")";
        ResultSet rs = dc.executeQuery(query);
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

    /**
     * Deletes a teacher.
     *
     * @param idper
     * @throws SQLException
     */
    public void delete(int idper) {
//    if (hasSchedules(id))
        String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper;
        try {
            dc.setAutoCommit(false);
            dc.executeUpdate(query);
            InstrumentIO.delete(idper, Instrument.TEACHER, dc);
            dc.commit();
        } catch (SQLException ex) {
            dc.rollback();
            GemLogger.logException(ex);
        } finally {
            dc.setAutoCommit(true);
        }
    }

    public Teacher findId(int n) throws SQLException {
        String query = "AND idper = " + n;
        List<Teacher> v = find(query);
        if (v.size() > 0) {
            return v.get(0);
        }
        return null;
    }

    public List<Teacher> find(String where) throws SQLException {
        List<Teacher> v = new ArrayList<>();
        String query = "SELECT " + COLUMNS + ", nom, prenom FROM " + TABLE + " t, " + PersonIO.VIEW + " p WHERE t.idper = p.id " + where;
        String order = ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey());

        query += " ORDER BY " + (order == null || order.toLowerCase().startsWith("n") ? "p.nom, p.prenom" : "p.prenom, p.nom");

        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Teacher t = new Teacher(rs.getInt(1));
                t.setCertificate1(rs.getString(2).trim());
                t.setCertificate2(rs.getString(3).trim());
                t.setCertificate3(rs.getString(4).trim());
                t.setActive(rs.getBoolean(5));
                t.setName(rs.getString(6));
                t.setFirstName(rs.getString(7));
                //t.setInstruments(InstrumentIO.find(t.getId(), Instrument.TEACHER, dc));
                t.setInstruments(DataCache.getTeacherInstruments(t.getId()));

                v.add(t);
            }
        }

        return v;
    }

    @Override
    public List<Teacher> load() throws SQLException {
        return find("");
    }
}
