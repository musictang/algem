/*
 * @(#)TeleIO.java	2.6.a 01/08/2012
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
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * IO methods for class net.algem.contact.Telephone.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TeleIO
        extends TableIO {

    public final static String TABLE = "telephone";
    public final static String COLUMNS = "idper, idx, numero, typetel";
    public static final String TYPE_TABLE = "typetel";

    public static void insert(Telephone t, int idx, DataConnection dc) throws SQLException {
        String query = "INSERT INTO " + TABLE + " VALUES("
                + t.getIdper()
                + "," + idx
                + ",'" + t.getNumber()
                + "','" + t.getTypeTel()
                + "')";

        dc.executeUpdate(query);
    }

    public static void update(Telephone t, int idx, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " SET "
                + "numero='" + t.getNumber()
                + "',typetel='" + t.getTypeTel()
                + "'"
                + " WHERE idper=" + t.getIdper() + " AND idx=" + idx;

        dc.executeUpdate(query);
    }

    public static void update(Telephone t, DataConnection dc) throws SQLException {
        String query = "UPDATE " + TABLE + " SET "
                + "numero='" + t.getNumber()
                + "',typetel='" + t.getTypeTel()
                + "'"
                + " WHERE idper=" + t.getIdper() + " AND idx=" + t.getIdx();

        dc.executeUpdate(query);
    }

    public static void delete(int idper, int idx, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE idper=" + idper + " AND idx=" + idx;
        dc.executeUpdate(query);
    }

    public static void delete(int idper, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE idper=" + idper;
        int rs = dc.executeUpdate(query);
    }

    public static void delete(Telephone t, DataConnection dc) throws SQLException {
        delete(t.getIdper(), t.getIdx(), dc);
    }

    public static List<Telephone> findId(int id, DataConnection dc) throws SQLException {
        String query = "WHERE idper = " + id;
        return find(query, dc);
    }

    public static List<Telephone> findId(String id, DataConnection dc) throws SQLException {
        String query = "WHERE idper = " + id;
        return find(query, dc);
    }

    public static List<Telephone> find(String where, DataConnection dc) throws SQLException {
        List<Telephone> v = new ArrayList<>();
        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where + " ORDER BY idx";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Telephone t = new Telephone();
                t.setIdper(rs.getInt(1));
                t.setIdx(rs.getInt(2));
                t.setNumber(rs.getString(3).trim());
                t.setTypeTel(rs.getInt(4));
                v.add(t);
            }
        }
        return v;
    }

    public static String getTypeTel(int idtype, DataConnection dc) throws SQLException {
        String t = String.valueOf(idtype);
        String query = "SELECT type FROM " + TYPE_TABLE + " WHERE id = " + idtype;
        try (ResultSet rs = dc.executeQuery(query)) {
            if (rs.next()) {
                t = rs.getString(1);
            }
        }
        return t;
    }
}
