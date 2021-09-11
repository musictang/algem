/*
 * @(#)RentableObjectIO.java	2.17.1 29/09/2019
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
package net.algem.rental;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.Algem;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.rental.RentableObject}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/09/2019
 */
public class RentableObjectIO
        extends TableIO
        implements Cacheable
{

    public static final String TABLE = "objetalouer";
    private static final String SEQUENCE = "objetalouer_id_seq";
    private DataConnection dc;

    public RentableObjectIO(DataConnection dc) {
        this.dc = dc;
    }

    public void insert(RentableObject o) throws SQLException {
        int id = nextId(SEQUENCE, dc);

        String query = "INSERT INTO " + TABLE + " VALUES("
                + id
                + ",'" + o.getDateAchat()
                + "','" + escape(o.getType())
                + "','" + escape(o.getMarque())
                + "','" + escape(o.getIdentification())
                + "','" + escape(o.getDescription())
                + "','" + escape(o.getVendeur())
                + "','" + (o.isActif() ? "t" : "f")
                + "')";

        dc.executeUpdate(query);
        o.setId(id);
    }

    /**
     *
     * @param n new room
     * @throws SQLException
     */
    public void update(RentableObject o) throws SQLException {
        String query = "UPDATE " + TABLE + " SET "
                + "creation = '"+o.getDateAchat()
                + "', type ='" + escape(o.getType())
                + "',marque = '" + escape(o.getMarque())
                + "',identification = '" + escape(o.getIdentification())
                + "',description = '" + escape(o.getDescription())
                + "',vendeur = '" + escape(o.getVendeur())
                + "',actif = '" + (o.isActif() ? "t" : "f")
                + "' WHERE id = " + o.getId();

        dc.executeUpdate(query);
    }

    /**
   * Deletes a rentable object.
     *
     * @param r
     * @throws RentmException if rentableobject is used
     */
    public void delete(RentableObject r) throws SQLException {
            String query = "DELETE FROM " + TABLE + " WHERE id = " + r.getId();
            dc.executeUpdate(query);
    }


    public RentableObject findId(int n) {

        String query = "WHERE id = " + n;
        Vector<RentableObject> v = find(query);
        if (v != null && v.size() > 0) {
            return v.elementAt(0);
        }
        return null;
    }


    public Vector<RentableObject> findAll() {
        String query = "SELECT * FROM " + TABLE + " ORDER BY type,marque";
        return findAll(query);
    }

    public List<RentableObject> findAvailable() {
        String query = "SELECT * FROM " + TABLE + " o LEFT JOIN location l ON o.id = l.objet WHERE l.debut is null OR l.fin is not NULL ORDER BY type,marque";
        return findAll(query);
    }

    public Vector<RentableObject> find(String where) {
        String query = "SELECT " + TABLE + ".* FROM " + TABLE + " " + where;
        return findAll(query);
    }

    private Vector<RentableObject> findAll(String query) {
        Vector<RentableObject> v = new Vector<RentableObject>();
        try {
            ResultSet rs = dc.executeQuery(query);
            while (rs.next()) {
                RentableObject s = new RentableObject();
                s.setId(rs.getInt(1));
                s.setDateAchat(new DateFr(rs.getDate(2)));
                s.setType(rs.getString(3).trim());
                s.setMarque(rs.getString(4).trim());
                s.setIdentification(rs.getString(5).trim());
                s.setDescription(rs.getString(6).trim());
                s.setVendeur(rs.getString(7).trim());
                s.setActif(rs.getBoolean(8));
                v.addElement(s);
            }
            rs.close();
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

    @Override
    public List<RentableObject> load() {
        return Algem.isFeatureEnabled("location") ? findAll() : null;
    }
}
