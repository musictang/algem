/*
 * @(#)JournalAccountIO.java	2.6.a 02/08/2012
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
package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Journal account persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.a
 */
public class JournalAccountIO
        extends TableIO {

    public static final String TABLE = "journalcompta";
    public static final String SEQUENCE = "journalcompta_id_seq";
    public static final String COLUMNS = "id,code,libelle,compte";

    public static List<JournalAccount> find(DataConnection dc) {
        String query = "SELECT " + COLUMNS + " FROM " + TABLE;
        List<JournalAccount> journaux = new ArrayList<>();
        try (ResultSet rs = dc.executeQuery(query)) {

            while (rs.next()) {
                JournalAccount jc = new JournalAccount(rs.getString(1), rs.getString(2));
                jc.setLabel(rs.getString(3));
                Account c = AccountIO.find(rs.getInt(4), dc);
                jc.setAccount(c);

                journaux.add(jc);
            }
        } catch (SQLException ignore) {
        }
        return journaux.isEmpty() ? null : journaux;
    }

    public static JournalAccount find(JournalAccount j, DataConnection dc) {
        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + j.getKey();
        try (ResultSet rs = dc.executeQuery(query)) {
            if (!rs.next()) {
                return null;
            }
            JournalAccount jc = new JournalAccount(rs.getString(1), rs.getString(2));
            jc.setLabel(rs.getString(3));
            Account c = AccountIO.find(rs.getInt(4), dc);
            jc.setAccount(c);
            return jc;
        } catch (SQLException ignore) {
        }
        return null;
    }

    public static JournalAccount find(int account, DataConnection dc) throws SQLException {
        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE compte='" + account + "'";
        try (ResultSet rs = dc.executeQuery(query)) {
        if (!rs.next()) {
            return null;
        }
        JournalAccount jc = new JournalAccount(rs.getString(1), rs.getString(2));
        jc.setLabel(rs.getString(3));
        Account c = AccountIO.find(rs.getInt(4), dc);
        jc.setAccount(c);
        return jc;
        } catch (SQLException ignore) {
        }
        return null;
    }

    public static void insert(JournalAccount j, DataConnection dc) throws SQLException {

        int id = TableIO.nextId(SEQUENCE, dc);

        String query = "INSERT INTO " + TABLE + " VALUES("
                + id + ",'" + j.getValue() + "','" + j.getLabel() + "','" + j.getAccount().getId() + "')";

        dc.executeUpdate(query);
        j.setKey(String.valueOf(id));
    }

    public static void update(JournalAccount j, DataConnection dc) throws SQLException {
        String[] columns = COLUMNS.split(",");

        String query = "UPDATE " + TABLE + " SET " + columns[1] + " = '" + j.getValue()
                + "', " + columns[2] + " = '" + j.getLabel()
                + "', " + columns[3] + " = '" + j.getAccount().getId()
                + "' WHERE id = " + j.getKey();
        dc.executeUpdate(query);

    }

    public static void delete(JournalAccount j, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE id = " + j.getKey();
        dc.executeUpdate(query);
    }
}
