/*
 * @(#)BicIO.java	2.6.a 14/09/12
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
package net.algem.bank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.bank.Bic}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class BicIO
        extends TableIO
{

  private static final String TABLE = "rib";

  public static void insert(Bic r, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + r.getId()
            + "','" + r.getEstablishment()
            + "','" + r.getBranch()
            + "','" + r.getAccount()
            + "','" + r.getBicKey()
            + "'," + r.getBranchId()
            + ")";

    dc.executeUpdate(query);
  }

  public static void update(Bic r, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET"
            + " etablissement = '" + r.getEstablishment()
            + "',guichet = '" + r.getBranch()
            + "',compte = '" + r.getAccount()
            + "',clerib = '" + r.getBicKey()
            + "',guichetid = " + r.getBranchId()
            + " WHERE idper = " + r.getId();

    dc.executeUpdate(query);
  }

  public static void delete(Bic r, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE rib.idper = " + r.getId();
    dc.executeUpdate(query);
  }

  /**
   * Suppress the rib associated with person {@code idper}.
   *
   * @author jm
   * @param idper person id
   * @param dc
   * @throws SQLException
   */
  public static void delete(int idper, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper;
    dc.executeUpdate(query);

  }

  public static Bic findId(int n, DataConnection dc) {
    String query = "WHERE idper = " + n;
    Vector v = find(query, dc);
    if (v.size() > 0) {
      return (Bic) v.elementAt(0);
    }
    return null;
  }

  public static Vector<Bic> find(String where, DataConnection dc) {
    Vector<Bic> v = new Vector<Bic>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Bic r = new Bic(rs.getInt(1));
        r.setEstablishment(rs.getString(2));
        r.setBranch(rs.getString(3));
        r.setAccount(rs.getString(4));
        r.setBicKey(rs.getString(5));
        r.setBranchId(rs.getInt(6));

        v.addElement(r);
      }
    } catch (SQLException e) {
      GemLogger.logException("find Rib: ", e);
    }
    return v;
  }
}
