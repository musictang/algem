/*
 * @(#)SubstituteTeacherIO.java	2.7.d 24/01/13
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
package net.algem.contact.teacher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.d
 * @since 2.0n
 */
public class SubstituteTeacherIO
        extends TableIO {

    public static String TABLE = "remplacement";
    public static String COLUMNS = "id_etab, id_cours, id_prof, id_remplacant, jours, favori";

    public static void insert(SubstituteTeacher r, DataConnection dc) throws SQLException {
        String query = "INSERT INTO " + TABLE + " VALUES("
                + r.getEstablishment()
                + "," + r.getCourse().getId()
                + "," + r.getTeacher().getId()
                + "," + r.getSubstitute().getId()
                + ",'" + escape(r.getDays())
                + "','" + (r.isFavorite() ? "t" : "f") + "')";

        dc.executeUpdate(query);
    }

    public static void update(SubstituteTeacher old, SubstituteTeacher r, DataConnection dc)
            throws SQLException {
        String query = "UPDATE " + TABLE + " SET jours = '" + r.getDays()
                + "', id_etab = " + r.getEstablishment()
                + ", id_cours = " + r.getCourse().getId()
                + ", id_remplacant = " + r.getSubstitute().getId()
                + ", favori = '" + (r.isFavorite() ? "t" : "f") + "'"
                + " WHERE id_etab = " + old.getEstablishment()
                + " AND id_cours = " + old.getCourse().getId()
                + " AND id_prof = " + old.getTeacher().getId()
                + " AND id_remplacant = " + old.getSubstitute().getId();
        dc.executeUpdate(query);
    }

    public static void delete(SubstituteTeacher r, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE
                + " WHERE id_etab = " + r.getEstablishment()
                + " AND id_cours = " + r.getCourse().getId()
                + " AND id_prof = " + r.getTeacher().getId()
                + " AND id_remplacant = " + r.getSubstitute().getId();

        dc.executeUpdate(query);
    }

    /**
     * Finds a substitute for a course, estab or specific teacher. Favorite are
     * first displayed in the list (marked by stars).
     *
     * @param dc
     * @param estabId establishment id
     * @param course course id
     * @param teacherId teacher id
     * @param day day of week
     * @return a list of teachers
     * @throws SQLException
     */
    public static List<SubstituteTeacher> find(int estabId, int course, int teacherId, int day, DataConnection dc) throws SQLException {
        List<SubstituteTeacher> vr = new ArrayList<>();
        String query = "SELECT " + COLUMNS + " FROM " + TABLE
                + " WHERE id_etab = " + estabId
                + " AND (id_prof =" + teacherId + " OR id_prof=0) AND id_cours= " + course + " ORDER BY favori DESC,id_prof DESC";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                SubstituteTeacher r = getSubstitute(rs);
                if (r.daysToArray()[day] == true) {
                    vr.add(r);
                }
            }
        }

        return vr;
    }

    /**
     * Finds if a substitute already exists.
     *
     * @param dc
     * @param r
     * @return a substitute if exists, else null
     */
    public static SubstituteTeacher find(SubstituteTeacher r, DataConnection dc) throws SQLException {
        SubstituteTeacher found = null;
        String query = "SELECT " + COLUMNS + " FROM " + TABLE
                + " WHERE id_etab = " + r.getEstablishment()
                + " AND id_cours = " + r.getCourse().getId()
                + " AND id_prof = " + r.getTeacher().getId()
                + " AND id_remplacant = " + r.getSubstitute().getId() + " LIMIT 1";

        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                found = getSubstitute(rs);
            }
        }

        return found;
    }

    public static List<SubstituteTeacher> findAll(DataConnection dc) throws SQLException {
        List<SubstituteTeacher> v = new ArrayList<>();
        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " r, personne p1, personne p2"
                + " WHERE r.id_prof = p1.id"
                + " AND r.id_remplacant = p2.id"
                + " ORDER BY r.id_etab,p1.prenom,p1.nom,p2.prenom,p2.nom";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                SubstituteTeacher r = getSubstitute(rs);
                v.add(r);
            }
        }

        return v;
    }

    private static SubstituteTeacher getSubstitute(ResultSet rs) throws SQLException {
        int estab = rs.getInt(1);
        Course c = (Course) DataCache.findId(rs.getInt(2), Model.Course);
        Person p = (Person) DataCache.findId(rs.getInt(3), Model.Person);
        Person pr = (Person) DataCache.findId(rs.getInt(4), Model.Person);
        return new SubstituteTeacher(estab, c, p, pr, rs.getString(5), rs.getBoolean(6));
    }
}
