/*
 * @(#)RentalOperationIO.java	2.17.1 29/09/2019
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.algem.Algem;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import static net.algem.enrolment.CourseOrderIO.TABLE;
import net.algem.enrolment.OrderIO;
import net.algem.group.Musician;
import net.algem.planning.ActionIO;
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
public class RentalOperationIO
        extends TableIO
        implements Cacheable
{

    public static final String TABLE = "location";
    private static final String SEQUENCE = "location_id_seq";
    private DataConnection dc;

    public RentalOperationIO(DataConnection dc) {
        this.dc = dc;
    }

    public void insert(RentalOperation o) throws SQLException {
        int id = nextId(SEQUENCE, dc);

        String query = "INSERT INTO " + TABLE + " VALUES("
                + id
                + ",'" + o.getRentableObjectId()
                + "','" + o.getStartDate()
                + "','" + o.getEndDate()
                + "','" + o.getMemberId()
                + "','" + o.getAmount()
                + "','" + escape(o.getDescription())
                + "')";

        dc.executeUpdate(query);
        o.setId(id);
    }

    /**
     *
     * @param n new room
     * @throws SQLException
     */
    public void update(RentalOperation o) throws SQLException {
        String query = "UPDATE " + TABLE + " SET "
                + "object = '"+o.getRentableObjectId()
                + "', debut ='" + o.getStartDate()
                + "', fin ='" + o.getEndDate()
                + "',adherent = '" + o.getMemberId()
                + "',montant = '" + o.getAmount()
                + "',libelle = '" + escape(o.getDescription())
                + "' WHERE id = " + o.getId();

        dc.executeUpdate(query);
    }

    /**
   * Deletes a rentable object.
     *
     * @param r
     */
    public void delete(RentalOperation r) throws RentException {
        try {
            String query = "DELETE FROM " + TABLE + " WHERE id = " + r.getId();
            dc.executeUpdate(query);
        } catch (SQLException e) {
            throw new RentException(MessageUtil.getMessage("delete.exception") + e.getMessage());
        }
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
        String query = "SELECT * FROM " + TABLE + " ORDER BY type";
        return findAll(query);
    }

    public Vector<RentableObject> find(String where) {
        System.out.println("RentableObjectIO.find query="+where);
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
                s.setType(unEscape(rs.getString(3).trim()));
                s.setMarque(unEscape(rs.getString(4).trim()));
                s.setIdentification(unEscape(rs.getString(5).trim()));
                s.setDescription(unEscape(rs.getString(6).trim()));
                s.setVendeur(unEscape(rs.getString(7).trim()));
                s.setActif(rs.getBoolean(8));
                v.addElement(s);
            }
            rs.close();
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

    public static List<RentalOperation> findRentals(int rentableObject, Date start, Date end, DataConnection dc) throws SQLException {

    List<RentalOperation> rentals = new ArrayList<RentalOperation>();
    String query = "SELECT l.id, l.objet, l.debut, l.fin, l.montant, l.libelle, p.id, p.nom, p.prenom, o.type, o.marque, o.identification FROM "
      + TABLE + " l "
      + " JOIN " + RentableObjectIO.TABLE + " o ON l.objet = o.id"
      + " JOIN " + PersonIO.TABLE + " p ON l.adherent = p.id"
      + " WHERE l.objet = ? AND l.debut BETWEEN ? AND ?";

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, rentableObject);
      ps.setDate(2, new java.sql.Date(start.getTime()));
      ps.setDate(3, new java.sql.Date(end.getTime()));

      GemLogger.info(ps.toString());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          RentalOperation r = new RentalOperation();
          r.setId(rs.getInt(1));
          r.setRentableObjectId(rs.getInt(2));
          r.setStartDate(new DateFr(rs.getString(3)));
          r.setEndDate(new DateFr(rs.getString(4)));
          r.setAmount(rs.getInt(5));
          r.setDescription(rs.getString(6).trim());
          r.setMemberId(rs.getInt(7));
          r.setMemberName(rs.getString(8)+" "+rs.getString(9));
          r.setRentableObjectName(rs.getString(10)+" "+rs.getString(11)+" "+rs.getString(12));

          rentals.add(r);
        }
      }
    }

    return rentals;
  }
        
    
    @Override
    public List<RentableObject> load() {
        return Algem.isFeatureEnabled("location") ? findAll() : null;
    }
}
