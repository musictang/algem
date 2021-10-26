/*
 * @(#)OrderIO.java	2.17.0 20/03/2019
 *                      2.15.8 21/03/2018
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.accounting.AccountPrefIO;
import net.algem.accounting.OrderLineIO;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Preference;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.Order}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 1.0a 07/07/1999
 */
public class OrderIO
        extends TableIO {

    public static final String TABLE = "commande";
    private static final String SEQUENCE = "idcommande";

    public static void insert(Order c, DataConnection dc) throws SQLException {

        int n = nextId(SEQUENCE, dc);
        String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = dc.prepareStatement(query)) {
            ps.setInt(1, n);
            ps.setInt(2, c.getMember());
            ps.setInt(3, c.getPayer());
            ps.setDate(4, new java.sql.Date(c.getCreation().getTime()));
            if (c.getInvoice() == null) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, c.getInvoice());
            }
            ps.executeUpdate();
            c.setId(n);
        }

    }

    public static void update(Order c, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " SET adh=?,payeur=?,creation=?,facture=? WHERE id=?";
        try (PreparedStatement ps = dc.prepareStatement(query)) {
            ps.setInt(1, c.getMember());
            ps.setInt(2, c.getPayer());
            ps.setDate(3, new java.sql.Date(c.getCreation().getTime()));
            if (c.getInvoice() == null) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, c.getInvoice());
            }
            ps.setInt(5, c.getId());

            GemLogger.info(ps.toString());
            ps.executeUpdate();
        }
    }

    public static void updateOrderDate(Order o, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " SET creation = ? WHERE id = ?";
        try (PreparedStatement ps = dc.prepareStatement(query)) {
            ps.setDate(1, new java.sql.Date(o.getCreation().getTime()));
            ps.setInt(2, o.getId());

            GemLogger.info(ps.toString());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes an order. The scheduled courses are also deleteByIdd.
     *
     * @param c the order
     * @param dc dataConnection
     * @throws Exception
     */
    public static void delete(Order c, DataConnection dc) throws Exception {

        try {
            dc.setAutoCommit(false);
            // suppression de la commande_module
            ModuleOrderIO.deleteByOrder(c.getId(), dc);
            List<CourseOrder> cours = CourseOrderIO.findId(c.getId(), dc);
            for (int i = 0; i < cours.size(); i++) {
                CourseOrder cc = cours.get(i);
                // suppression des plages de cours
                String query = "idplanning IN (SELECT id FROM " + ScheduleIO.TABLE + " WHERE action = " + cc.getAction() + ")"
                        + " AND adherent = " + c.getMember();
                ScheduleRangeIO.delete(query, dc);
            }
            // suppression de la commande_cours
            CourseOrderIO.delete(c.getId(), dc);

            // suppression de la commande
            String query = "DELETE FROM " + TABLE + " WHERE id = " + c.getId();
            dc.executeUpdate(query);

            // suppression des échéances
            int memberAccount = 0;
            // on ne supprime pas les échéances correspondant à des adhésions
              Preference p = DataCache.getPreference(AccountPrefIO.MEMBERSHIP);
            if (p != null && p.getValues() != null && p.getValues().length > 0) {
                memberAccount = (Integer) p.getValues()[0];
            }
            query = "DELETE FROM " + OrderLineIO.TABLE + " WHERE commande = " + c.getId() + " AND compte != '" + memberAccount + "' AND transfert = 'f'";
            dc.executeUpdate(query);

            dc.commit();
        } catch (SQLException e1) {
            dc.rollback();
            throw e1;
        } finally {
            dc.setAutoCommit(true);
        }
    }

    public static Order findId(int n, DataConnection dc) throws SQLException {
        String query = "WHERE id = " + n;
        List<Order> v = find(query, dc);
        if (v.size() > 0) {
            return v.get(0);
        }
        return null;
    }

    public static List<Order> find(String where, DataConnection dc) throws SQLException {
        List<Order> v = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE + " " + where;

        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Order c = new Order();
                c.setId(rs.getInt(1));
                c.setMember(rs.getInt(2));
                c.setPayer(rs.getInt(3));
                c.setCreation(new DateFr(rs.getString(4)));
                c.setInvoice(rs.getString(5));

                v.add(c);
            }
        }

        return v;
    }

    public static List<MemberOrder> findMemberOrders(DataConnection dc) {

        String start = ConfigUtil.getConf(ConfigKey.BEGINNING_PERIOD.getKey());
        String end = ConfigUtil.getConf(ConfigKey.END_PERIOD.getKey());
        List<MemberOrder> v = new ArrayList<>();
        String query = "SELECT c.id,c.adh,c.payeur,c.creation,c.facture,p.nom,p.prenom"
                + " FROM " + TABLE + " c, " + PersonIO.TABLE + " p"
                + " WHERE c.adh = p.id"
                + " AND c.creation >= '" + start + "' AND c.creation <= '" + end + "'"
                + " ORDER BY c.id DESC";
        try (ResultSet rs = dc.executeQuery(query))  {
            while (rs.next()) {
                MemberOrder c = new MemberOrder();
                c.setId(rs.getInt(1));
                c.setMember(rs.getInt(2));
                c.setPayer(rs.getInt(3));
                c.setCreation(new DateFr(rs.getString(4)));
                c.setInvoice(rs.getString(5));
                c.setMemberName(rs.getString(6));
                c.setMemberFirstname(rs.getString(7));

                v.add(c);
            }
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

    //FIXME ajout eric pour polynotes
    public static List<Person> findMembersWithOrders(DataConnection dc) {

        String start = ConfigUtil.getConf(ConfigKey.BEGINNING_PERIOD.getKey());
        String end = ConfigUtil.getConf(ConfigKey.END_PERIOD.getKey());
        List<Person> v = new ArrayList<>();
        String query = "SELECT DISTINCT c.adh,p.nom,p.prenom"
                + " FROM " + TABLE + " c, " + PersonIO.TABLE + " p"
                + " WHERE c.adh = p.id"
                + " AND c.creation >= '" + start + "' AND c.creation <= '" + end + "'"
                + " ORDER BY p.nom,p.prenom";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Person p = new Person();
                p.setId(rs.getInt(1));
                p.setName(rs.getString(2));
                p.setFirstName(rs.getString(3));

                v.add(p);
            }
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

}
