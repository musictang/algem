/*
 * @(#)EnrolmentWishIO.java	2.17.0 13/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.wishes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import static net.algem.util.model.TableIO.nextId;

/**
 * IO methods for class {@link net.algem.planning.wishes.EnrolmentWish}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 13/03/2019
 */
public class EnrolmentWishIO
        extends TableIO {

    public static int NOTE_MAX_LENGTH = 256;

    public static final String TABLE = "reinscription";
    public static final String COLUMNS = "id, creation, prof, jour, cours, student, preference, hour, duration, note, colonne, selected, mailinfo, mailconfirm, action";
    public static final String SEQUENCE = "reinscription_id_seq";

    private static final String FIND_BY_ID_QUERY = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = ?";

    private DataConnection dc;
    private PreparedStatement insertPS;
    private PreparedStatement updatePS;
    private PreparedStatement updateSelectPS;
    private PreparedStatement findPS;

    public EnrolmentWishIO(DataConnection _dc) {
        dc = _dc;
        insertPS = dc.prepareStatement("INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?,?,?,?,'f',NULL,NULL,?)");
        updatePS = dc.prepareStatement("UPDATE " + TABLE + " SET prof=?,jour=?,cours=?,student=?,preference=?,hour=?,duration=?,note=?,colonne=? WHERE id = ?");
        updateSelectPS = dc.prepareStatement("UPDATE " + TABLE + " SET selected=? WHERE id=?");
        findPS = dc.prepareStatement(FIND_BY_ID_QUERY);
    }

    public void insert(EnrolmentWish w) throws SQLException {

        int n = nextId(SEQUENCE, dc);

        insertPS.setInt(1, n);
        insertPS.setObject(2, LocalDateTime.now());
        insertPS.setInt(3, w.getTeacher());
        insertPS.setShort(4, w.getDay());
        insertPS.setInt(5, w.getCourse());
        insertPS.setInt(6, w.getStudentId());
        insertPS.setShort(7, w.getPreference());
        insertPS.setObject(8, LocalTime.parse(w.getHour().toString()));
        insertPS.setObject(9, LocalTime.parse(w.getDuration().toString()));
        insertPS.setString(10, escape(w.getNote().length() > NOTE_MAX_LENGTH ? w.getNote().substring(0, 256) : w.getNote()));
        insertPS.setInt(11, w.getColumn());
        insertPS.setInt(12, w.getAction());

        GemLogger.info(insertPS.toString());
        insertPS.executeUpdate();

        w.setId(n);
    }

    public void update(EnrolmentWish w) throws SQLException {

        updatePS.setInt(1, w.getTeacher());
        updatePS.setShort(2, w.getDay());
        updatePS.setInt(3, w.getCourse());
        updatePS.setInt(4, w.getStudentId());
        updatePS.setShort(5, w.getPreference());
        updatePS.setObject(6, LocalTime.parse(w.getHour().toString()));
        updatePS.setObject(7, LocalTime.parse(w.getDuration().toString()));
        updatePS.setString(8, escape(w.getNote().length() > NOTE_MAX_LENGTH ? w.getNote().substring(0, 256) : w.getNote()));
        updatePS.setInt(9, w.getColumn());
        updatePS.setInt(10, w.getId());

        GemLogger.info(updatePS.toString());
        updatePS.executeUpdate();

    }

    public void updateSelected(EnrolmentWish w) throws SQLException {
        updateSelectPS.setBoolean(1, w.isSelected());
        updateSelectPS.setInt(2, w.getId());
        GemLogger.info(updateSelectPS.toString());
        updateSelectPS.executeUpdate();

    }

    public void updateMailInfoDate(int student, LocalDateTime mailDate) throws SQLException {
        String query = "UPDATE " + TABLE 
                + " SET mailinfo='"+mailDate+"' WHERE student = " + student; //TODO AND creation between année scolaire en cours
        dc.executeUpdate(query);
    }

    public void updateMailConfirmDate(EnrolmentWish w) throws SQLException {
        String query = "UPDATE " + TABLE 
                + " SET mailconfirm='"+w.getDateMailConfirm()+"' WHERE id = " + w.getId();
        dc.executeUpdate(query);
    }

    public void delete(EnrolmentWish w) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE id = " + w.getId();
        dc.executeUpdate(query);
    }

    public EnrolmentWish findId(int n) throws SQLException {
        findPS.setInt(1, n);
        ResultSet rs = findPS.executeQuery();
        while (rs.next()) {
            return getEnrolmentWishesFromRS(rs);
        }
        return null;
    }

    public List<EnrolmentWish> find(String where) throws SQLException {
        List<EnrolmentWish> v = new ArrayList<>();

        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                v.add(getEnrolmentWishesFromRS(rs));
            }
        }

        return v;
    }

    public int count(String where) {
        int count=0;
        String query = "SELECT count(id) FROM " + TABLE + where;
        try (ResultSet rs = dc.executeQuery(query)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ignore) {
        }
        return count;
    }
    
    public int count(int student) {
        return count(" WHERE student=" + student);
    }
    
    public int countGroupWishes(int student) {
        return count(" WHERE student=" + student + "AND action != 0");
    }
    
    public int count(int student, int teacher, int course) {
        return count(" WHERE student=" + student + "AND prof=" + teacher + " AND cours="+ course);
    }
    
    public int count(int student, int action) {
        return count(" WHERE student=" + student + "AND action=" + action);
    }

    public static int dow2isodow(int d) {
        return d == 0 ? 7 : d - 1;
    }

    private EnrolmentWish getEnrolmentWishesFromRS(ResultSet rs) throws SQLException {
        EnrolmentWish w = new EnrolmentWish();
        w.setId(rs.getInt(1));
        w.setCreationDate((LocalDate)rs.getObject(2, LocalDate.class));
        w.setTeacher(rs.getInt(3));
        w.setDay(rs.getShort(4));
        w.setCourse(rs.getInt(5));
        w.setStudentId(rs.getInt(6));
        w.setPreference(rs.getShort(7));
        w.setHour(new Hour(rs.getString(8)));
        w.setDuration(new Hour(rs.getString(9)));
        w.setNote(rs.getString(10));
        w.setColumn(rs.getInt(11));
        w.setSelected(rs.getBoolean(12));
        w.setDateMailInfo((LocalDateTime)rs.getObject(13, LocalDateTime.class));
        w.setDateMailConfirm((LocalDateTime)rs.getObject(14, LocalDateTime.class));
        w.setAction(rs.getInt(15));
        
        w.setTeacherLabel(((Person) DataCache.findId(w.getTeacher(), Model.Teacher)).getName());
        w.setCourseLabel(((Course) DataCache.findId(w.getCourse(), Model.Course)).getTitle());
        w.setDayLabel(DayOfWeek.of(dow2isodow(w.getDay())).getDisplayName(TextStyle.FULL, Locale.FRANCE));


        return w;
    }
}
