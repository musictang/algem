/*
 * @(#)WorkshopIO.java	2.7.a 30/11/12
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
package net.algem.course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.group.Musician;
import net.algem.planning.ActionIO;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.course.Workshop}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class WorkshopIO
        extends TableIO {

    private DataConnection dc;

    public WorkshopIO(DataConnection _dc) {
        this.dc = _dc;
    }

    /**
     *
     * @param dc
     * @param where
     * @return une liste d'ateliers
     * @throws SQLException
     * @deprecated
     */
    public static List<Workshop> find(String where, DataConnection dc) throws SQLException {
        List<Workshop> v = new ArrayList<>();
        String query = "SELECT * FROM atelier " + where;

        try (ResultSet rs = dc.executeQuery(query)) {
        while (rs.next()) {
            Workshop a = new Workshop();
            a.setId(rs.getInt(1));
            a.setName(rs.getString(2).trim());
            a.setTeacher(rs.getInt(3));

            v.add(a);
        }
        }

        return v;
    }

    /**
     * Recherche des élèves inscrits à un atelier {@code c}.
     *
     * @param c numéro d'atelier
     * @param dc datacache
     * @return une liste de personnes
     */
    public static List<Person> findMember(Course c, DataConnection dc) throws SQLException {
        return findMember(c.getId(), dc);
    }

    /**
     *
     * @param id course id
     * @param dc connection
     * @return a list of persons
     * @throws SQLException
     */
    public static List<Person> findMember(int id, DataConnection dc) throws SQLException {
        List<Person> v = new ArrayList<>();

        String query = "SELECT DISTINCT pi.idper, pi.instrument"
                + " FROM " + ScheduleRangeIO.TABLE + " pl, "
                + ScheduleIO.TABLE + " p, "
                + ActionIO.TABLE + " a, "
                + InstrumentIO.PERSON_INSTRUMENT_TABLE + " pi"
                + " WHERE pl.adherent = pi.idper"
                + " AND pl.idplanning = p.id"
                + " AND p.ptype = " + Schedule.WORKSHOP
                + " AND p.action = a.id"
                + " AND a.cours = " + id
                + " AND pi.ptype = " + Instrument.MEMBER + " AND pi.idx = " + 0
                + " UNION" // old data table
                + " SELECT DISTINCT adherent, ti.id FROM atelier_ins at," + InstrumentIO.TABLE + " ti"
                + " WHERE at.id = " + id + " AND at.instrument = ti.nom";

        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                int idper = rs.getInt(1);
                Person p = ((PersonIO) DataCache.getDao(Model.Person)).findById(idper);
                Musician m = new Musician(p);
                m.setInstrument(rs.getInt(2));
                v.add(m);
            }
        }

        return v;
    }

    /**
     *
     * @param c
     * @param dc
     * @param p
     * @throws SQLException
     * @deprecated
     */
    public static void insertAdherent(Course c, Musician p, DataConnection dc) throws SQLException {
        String query = "INSERT INTO atelier_ins values("
                + c.getId()
                + "," + p.getId()
                + ",'" + p.getInstrument()
                + "')";

        dc.executeUpdate(query);
    }

    /**
     *
     * @param _id
     * @param dc
     * @param idper
     * @throws SQLException
     * @deprecated
     */
    public static void deleteAdherent(int _id, int idper, DataConnection dc) throws SQLException {
        String query = "DELETE FROM atelier_ins where id=" + _id + " and adherent=" + idper;
        dc.executeUpdate(query);
    }
}
