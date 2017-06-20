/*
 * @(#) StandardOrderLineIO.java Algem 2.14.0 20/06/17
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
 */

package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.algem.config.Param;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import static net.algem.util.model.TableIO.escape;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.10.0 18/05/16
 */
public class StandardOrderLineIO {

  public static final String TABLE = "echeance";
  private static final String SEQUENCE = "echeance_id_seq";
  private DataConnection dc;

  public StandardOrderLineIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(StandardOrderLine ol) throws SQLException {
    int nextid = TableIO.nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES("
              + nextid
              + ",'" + ol.getModeOfPayment()
              + "','" + TableIO.escape(ol.getLabel())
              + "'," + ol.getAmount()
              + ",'" + ol.getDocument()
              + "'," + ol.getSchool()
              + "," + ol.getAccount().getId()
              + ",'" + ol.getCostAccount().getNumber()
              + ", " + (ol.getDate() == null || DateFr.NULLDATE.equals(ol.getDate().toString()) ? "NULL" : "'" + ol.getDate().toString() + "'")
              + "')";

      dc.executeUpdate(query);

      ol.setId(nextid);
  }

    public void update(StandardOrderLine ol) throws SQLException {
    String query = "UPDATE " + TABLE + " SET"
            + " libelle = '" + escape(ol.getLabel())
            + "', reglement = '" + ol.getModeOfPayment()
            + "', montant = " + ol.getAmount()
            + ", piece = '" + ol.getDocument()
            + "', ecole = " + ol.getSchool()
            + ", compte = " + ol.getAccount().getId()
            + ", analytique = '" + ol.getCostAccount().getNumber()
            + "', echeance = " + (ol.getDate() == null || DateFr.NULLDATE.equals(ol.getDate().toString()) ? "NULL" : "'" + ol.getDate().toString() + "'")
            + " WHERE id = " + ol.getId();
    dc.executeUpdate(query);

  }

    public void delete(int id) throws SQLException {
      String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
      dc.executeUpdate(query);
    }

    public List<OrderLine> find() throws SQLException {
      List<OrderLine> defs = new ArrayList<>();
      String query = "SELECT id,libelle,reglement,montant,piece,ecole,compte,analytique,echeance FROM " + TABLE;
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        StandardOrderLine def = new StandardOrderLine();
        def.setId(rs.getInt(1));
        def.setLabel(rs.getString(2));
        def.setModeOfPayment(rs.getString(3).trim());
        def.setAmount(rs.getInt(4));
        def.setDocument(rs.getString(5));
        def.setSchool(rs.getInt(6));
        Account c = (Account) DataCache.findId(rs.getInt(7), Model.Account);
        def.setAccount(c);
        String ca = rs.getString(8);
        Param p = DataCache.getCostAccount(ca);
        Account a = null;
        if (p != null) {
          a = new Account(p);
          a.setLabel(p.getValue());
        } else {
          a = new Account(ca);
          a.setLabel(ca);
        }

        def.setCostAccount(a);
        def.setDate(rs.getDate(9));
        defs.add(new OrderLine(def));
      }
      return defs;
    }

    /**
     * Checks if an order line already exists between {@code start} and {@code end} dates
     * for this {@code member}.
     * @param o order line to check
     * @param start start date
     * @param end end date
     * @param member member id
     * @return true if any line exists, else false
     * @throws SQLException 
     */
    public boolean exists(StandardOrderLine o, Date start, Date end, int member) throws SQLException {
      String query = "SELECT e.oid FROM " + OrderLineIO.TABLE + " e"
        + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end
        //+ " AND e.reglement LIKE '" + o.getModeOfPayment() + "%'"
        //+ "' AND e.libelle = '" + o.getLabel()
        + "' AND e.montant = " + o.getAmount()
        + " AND e.compte = " + o.getAccount().getId()
        + " AND e.analytique = '" + o.getCostAccount().getNumber()
        + "' AND e.adherent = " + member;
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        return true;
      }
      return false;
    }


}
