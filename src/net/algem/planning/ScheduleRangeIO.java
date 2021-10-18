/*
 * @(#)ScheduleRangeIO.java	2.17.0 20/03/2019
 *                              2.15.6 29/11/17
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
package net.algem.planning;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.algem.contact.Organization;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.enrolment.FollowUp;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.planning.ScheduleRange}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 1.0a 7/7/1999
 */
public class ScheduleRangeIO
        extends TableIO {

    public final static String TABLE = "plage";
    public final static String COLUMNS = "pg.id, pg.idplanning, pg.debut, pg.fin, pg.adherent, pg.note";
    public final static String SEQUENCE = "plage_id_seq";

    public static void insert(ScheduleRange p, DataConnection dc) throws SQLException {

        int id = nextId(SEQUENCE, dc);
        String query = "INSERT INTO " + TABLE + " VALUES("
                + id
                + ", " + p.getScheduleId()
                + ",'" + p.getStart()
                + "','" + p.getEnd()
                + "'," + p.getMemberId()
                + "," + p.getNote()
                + ")";
        dc.executeUpdate(query);
    }

    public static void update(ScheduleRange p, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " SET debut = '" + p.getStart()
                + "', fin = '" + p.getEnd()
                + "', adherent = " + p.getMemberId()
                + ", note = " + p.getNote()
                + " WHERE id =" + p.getId();
        dc.executeUpdate(query);
    }

    public static void update(ScheduleRangeObject p, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " SET debut = '" + p.getStart()
                + "', fin = '" + p.getEnd()
                + "', adherent = " + p.getMember().getId()
                + ", note = " + p.getNote()
                + " WHERE id = " + p.getId();
        dc.executeUpdate(query);
    }

    public static void update(String _query, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " " + _query;
        dc.executeUpdate(query);
    }

    public static void delete(ScheduleRange p, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
        dc.executeUpdate(query);
    }

    public static void delete(ScheduleRangeObject p, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
        dc.executeUpdate(query);
    }

    /**
     * Suppress ranges by action.
     *
     * @param a action
     * @param dc data connection
     * @throws SQLException
     */
    public static void delete(Action a, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE idplanning IN ("
                + "SELECT id FROM " + ScheduleIO.TABLE
                + " WHERE action = " + a.getId()
                + " AND jour >= '" + a.getStartDate() + "' AND jour <= '" + a.getEndDate() + "')";
        dc.executeUpdate(query);
    }

    /**
     * Deletes all ranges scheduled with the action {@literal a} and enclosed in
     * the selected time slots.
     *
     * @param a action
     * @param s schedule
     * @param dc data connection
     * @throws SQLException
     */
    public static void delete(Action a, Schedule s, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE idplanning IN ("
                + "SELECT id FROM " + ScheduleIO.TABLE
                + " WHERE action = " + a.getId()
                + " AND jour >= '" + a.getStartDate() + "' AND jour <= '" + a.getEndDate()
                + "' AND debut >= '" + s.getStart() + "' AND fin <= '" + s.getEnd() + "')";
        dc.executeUpdate(query);
    }

    public static void delete(String where, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE " + where;
        dc.executeUpdate(query);
    }

    public static List<ScheduleRange> find(String where, DataConnection dc) throws SQLException {
        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;
        return ifind(query, dc);
    }

    private static List<ScheduleRange> ifind(String query, DataConnection dc) throws SQLException {
        List<ScheduleRange> v = new ArrayList<>();
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                ScheduleRange p = new ScheduleRange();
                p.setId(rs.getInt(1));
                p.setScheduleId(rs.getInt(2));
                p.setStart(new Hour(rs.getString(3)));
                p.setEnd(new Hour(PlanningService.getTime(rs.getString(4))));
                p.setMemberId(rs.getInt(5));
                p.setNote(rs.getInt(6));

                v.add(p);
            }
        }

        return v;
    }

    private static ScheduleRangeObject rangeObjectFactory(ResultSet rs, PlanningService service)
            throws SQLException {
        ScheduleRangeObject p = new ScheduleRangeObject();
        p.setId(rs.getInt(1));
        p.setScheduleId(rs.getInt(2));
        p.setStart(new Hour(rs.getString(3)));
        p.setEnd(new Hour(PlanningService.getTime(rs.getString(4))));
        //ERIC 2.17 profite du planning jour pour précharger le cache PERSON dans getDayRangeStmt()
        if (!DataCache.personCacheLoaded() && !DataCache.existId(rs.getInt(5), Model.Person)) {
            try {
                Person member = new Person(rs.getInt(12));
                member.setType((short) rs.getInt(13));
                member.setName(rs.getString(14));
                member.setFirstName(rs.getString(15));
                member.setGender(rs.getString(16));
                member.setImgRights(rs.getBoolean(17));
                member.setPartnerInfo(rs.getBoolean(18));
                member.setNickName(rs.getString(19));
                Organization o = new Organization(rs.getInt(20));
                o.setName(rs.getString(21));
                o.setCompanyName(rs.getString(22));
                member.setOrganization(o);
                p.setMember(member);

                DataCache.addToPersonCache(member);
            } catch (Exception e) { // c'est pas une requête planning jour avec 22 colonnes
                //System.out.println("ScheduleRangeIO DataCache.addToPersonCache:" + e);
                p.setMember((Person) DataCache.findId(rs.getInt(5), Model.Person)); // 
            }
        } else {
            p.setMember((Person) DataCache.findId(rs.getInt(5), Model.Person)); // 
        }
        p.setNote(rs.getInt(6));

        p.setDate(new DateFr(rs.getString(7)));
        p.setIdAction(rs.getInt(8));
        p.setIdPerson(rs.getInt(9));// idper in schedule
        p.setIdRoom(rs.getInt(10));
        p.setType(rs.getInt(11));

        p.setRoom((Room) DataCache.findId(p.getIdRoom(), Model.Room));
        p.setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Teacher));

        p.setAction(service.getAction(p.getIdAction()));
        p.setCourse((Course) DataCache.findId(p.getAction().getCourse(), Model.Course));

        return p;
    }

    public static List<ScheduleRangeObject> findRangeObject(String and, PlanningService service, DataConnection dc) throws SQLException {
        String query = "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype"
                + " FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
                + " WHERE pg.idplanning = p.id " + and;
        return findObject(query, service, dc);
    }

    public static List<ScheduleRangeObject> findObject(String query, PlanningService service, DataConnection dc) throws SQLException {
        List<ScheduleRangeObject> v = new ArrayList<>();

        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                ScheduleRangeObject p = rangeObjectFactory(rs, service);
                v.add(p);
            }
        }

        return v;
    }

    public static List<ScheduleRangeObject> getLoadRS(PreparedStatement ps, DataConnection dc) {
        List<ScheduleRangeObject> v = new ArrayList<>();
        PlanningService service = new PlanningService(dc);
        try (ResultSet rs = ps.executeQuery()) {
            while (!Thread.interrupted() && rs.next()) {
                ScheduleRangeObject p = rangeObjectFactory(rs, service);
                v.add(p);
            }

        } catch (SQLException e) {
            GemLogger.logException("plage prepared stmt", e);
        }
        return v;
    }

    public static List<ScheduleRangeObject> findFollowUp(String where, boolean action, DataConnection dc) throws SQLException {
        List<ScheduleRangeObject> v = new ArrayList<>();
        String query = getFollowUpRequest(action);
        query += where;
        PlanningService pService = new PlanningService(dc);

        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                ScheduleRangeObject p = rangeObjectFactory(rs, pService);
                FollowUp up = new FollowUp();
                up.setContent(rs.getString(12));
                up.setNote(rs.getString(13));
                up.setStatus(rs.getShort(14));
                p.setFollowUp(up);
                p.setNote2(rs.getString(15));

                v.add(p);
            }
        }
        return v;
    }

    /**
     * Retun a list of ranges from a list of schedules. This method is used to
     * find all individual rehearsals of a person between @{code start} and
     *
     * @{code end}. As rehearsals are of schedule type, schedule entries are
     * converted in schedule range objects.
     *
     * @param idper member's id
     * @param start start date
     * @param end end date
     * @param dc data connexion instance
     * @return a list of ranges
     * @throws SQLException
     */
    public static List<ScheduleRangeObject> findMemberRehearsals(int idper, int type, Date start, Date end, DataConnection dc) throws SQLException {

        List<ScheduleRangeObject> ranges = new ArrayList<>();

        String query = "SELECT p.id,p.jour,p.debut,p.fin,p.action FROM planning p"
                + " WHERE p.ptype = " + Schedule.MEMBER
                + " AND p.jour BETWEEN ? AND ?"
                + " AND p.idper = ?"
                + " ORDER BY p.jour";
        if (Schedule.GROUP == type) {
            query = "SELECT p.id, p.jour, p.debut,p.fin,p.action"
                    + " FROM planning p JOIN groupe g ON p.idper = g.id JOIN groupe_det gd ON gd.id = g.id"
                    + " WHERE p.ptype = " + Schedule.GROUP
                    + " AND p.jour BETWEEN ? AND ?"
                    + " AND gd.musicien = ?"
                    + " ORDER BY p.jour";
        }
        try (PreparedStatement ps = dc.prepareStatement(query)) {
            ps.setDate(1, new java.sql.Date(start.getTime()));
            ps.setDate(2, new java.sql.Date(end.getTime()));
            ps.setInt(3, idper);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleRangeObject p = new ScheduleRangeObject();
                    p.setId(rs.getInt(1));
                    p.setScheduleId(rs.getInt(1));
                    p.setDate(new DateFr(rs.getString(2)));
                    p.setStart(new Hour(rs.getString(3)));
                    p.setEnd(new Hour(PlanningService.getTime(rs.getString(4))));
                    p.setNote(0);
                    p.setIdAction(rs.getInt(5));
                    p.setIdPerson(idper);// idper in schedule
                    p.setIdRoom(0);
                    p.setType(Schedule.MEMBER);

                    ranges.add(p);
                }
            }
        }

        return ranges;
    }

    private static String getFollowUpRequest(boolean action) {//TODO !!!!
        if (action) {
            return "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype, s1.texte, CASE WHEN pg.note = 0 AND s1.note = '0' THEN NULL ELSE s1.note END AS note1, s1.statut, s2.texte"
                    + " FROM " + TABLE + " pg, planning p, action a, " + ScheduleIO.FOLLOW_UP_TABLE + " s1, " + ScheduleIO.FOLLOW_UP_TABLE + " s2"
                    + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING
                    + ") AND p.id = pg.idplanning";

        } else {
            return "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype, s1.texte, CASE WHEN pg.note = 0 AND s1.note = '0' THEN NULL ELSE s1.note END AS note1, s1.statut, s2.texte"
                    + " FROM " + TABLE + " pg, planning p, " + ScheduleIO.FOLLOW_UP_TABLE + " s1, " + ScheduleIO.FOLLOW_UP_TABLE + " s2"
                    + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING
                    + ") AND p.id = pg.idplanning";
        }
    }

    public static void createNote(ScheduleRangeObject range, FollowUp up, DataConnection dc) throws PlanningException {
        try {
            dc.setAutoCommit(false);
            int id = nextId(ScheduleIO.FOLLOW_UP_SEQUENCE, dc);
            String content = up.getContent() == null || up.getContent().isEmpty() ? "NULL," : "'" + escape(up.getContent()) + "',";
            String note = up.getNote() == null || up.getNote().isEmpty() ? "NULL," : "'" + escape(up.getNote()) + "',";
            String query = "INSERT INTO " + ScheduleIO.FOLLOW_UP_TABLE + " VALUES("
                    + id + ","
                    + content
                    + note
                    + up.getStatus() + ")";
            dc.executeUpdate(query);
            range.setNote(id);
            if (range.getId() > 0) {
                update(range, dc);
            }
            dc.commit();
        } catch (SQLException sqe) {
            dc.rollback();
            throw new PlanningException(sqe.getMessage());
        } finally {
            dc.setAutoCommit(true);
        }
    }

    public static void updateNote(int note, FollowUp up, DataConnection dc) throws SQLException {
        String content = up.getContent() == null || up.getContent().isEmpty() ? "NULL," : "'" + escape(up.getContent()) + "',";
        String n = up.getNote() == null || up.getNote().isEmpty() ? "NULL," : "'" + escape(up.getNote()) + "',";
        String query = "UPDATE " + ScheduleIO.FOLLOW_UP_TABLE
                + " SET texte = " + content
                + " note = " + n
                + " statut = " + up.getStatus()
                + " WHERE id = " + note;
        dc.executeUpdate(query);
    }

    public static void deleteNote(int note, DataConnection dc) throws SQLException {
        if (note == 0) {
            return;
        }
        String query = "DELETE FROM " + ScheduleIO.FOLLOW_UP_TABLE + " WHERE id = " + note;
        dc.executeUpdate(query);
    }

    //ERIC 2.17 02/04/2019    ajoute une jointure avec personnevue pour préremplir le cache personne tant que la taille du cache < CONSTANTE
    //                        la lecture du resultset avec 11 ou 22 colonnes se fait dans rangeObjectFactory()
    public static String getMonthRangeStmt() {
        String query;
        if (!DataCache.personCacheLoaded()) { //ERIC 2.17 27/03/2019 
            query = "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype"
                    + ",pv.id,pv.ptype,pv.nom,pv.prenom,pv.civilite,pv.droit_img,pv.partenaire,pv.pseudo,pv.organisation,pv.onom,pv.oraison" //ERIC 27/03/2019
                    + " FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
                    + ", personnevue pv"
                    + " WHERE pg.idplanning = p.id"
                    + " AND pv.id = pg.adherent"
                    + " AND p.jour >= ? AND p.jour <= ? ORDER BY p.jour, pg.debut";
        } else {
            query = "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype"
                    + " FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
                    + " WHERE pg.idplanning = p.id"
                    + " AND p.jour >= ? AND p.jour <= ? ORDER BY p.jour, pg.debut";
        }
        return query;
    }

    //ERIC 2.17 27/03/2019    ajoute une jointure avec personnevue pour préremplir le cache personne tant que la taille du cache < CONSTANTE
    //                        la lecture du resultset avec 11 ou 22 colonnes se fait dans rangeObjectFactory()
    public static String getDayRangeStmt() {
        String query;

        if (!DataCache.personCacheLoaded()) { //ERIC 2.17 27/03/2019 
            query = "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype"
                    + ",pv.id,pv.ptype,pv.nom,pv.prenom,pv.civilite,pv.droit_img,pv.partenaire,pv.pseudo,pv.organisation,pv.onom,pv.oraison" //ERIC 27/03/2019
                    + " FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
                    + ", personnevue pv"
                    + " WHERE pg.idplanning = p.id"
                    + " AND pv.id = pg.adherent"
                    + " AND p.jour = ? ORDER BY p.debut, pg.debut";
        } else {
            query = "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype"
                    + " FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
                    + " WHERE pg.idplanning = p.id"
                    + " AND p.jour = ? ORDER BY p.debut, pg.debut";

        }
        return query;
    }
}
