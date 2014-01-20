/*
 * @(#)RibIO.java	2.8.i 08/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
 * IO methods for class {@link net.algem.bank.Rib}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class RibIO
        extends TableIO
{

  public static final String TABLE = "rib";
  private static final String COLUMNS = "idper,etablissement,guichet,compte,clerib,guichetid,iban";

  public static void insert(Rib r, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + r.getId()
            + "','" + r.getEstablishment()
            + "','" + r.getBranch()
            + "','" + r.getAccount()
            + "','" + r.getRibKey()
            + "'," + r.getBranchId()
            + ",'" + r.getIban()
            + "')";

    dc.executeUpdate(query);
  }

  public static void update(Rib r, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET"
            + " etablissement = '" + r.getEstablishment()
            + "', guichet = '" + r.getBranch()
            + "', compte = '" + r.getAccount()
            + "', clerib = '" + r.getRibKey()
            + "', guichetid = " + r.getBranchId()
            + ", iban = '" + r.getIban()
            + "' WHERE idper = " + r.getId();

    dc.executeUpdate(query);
  }

  public static void delete(Rib r, DataConnection dc) throws SQLException {
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

  public static Rib findId(int n, DataConnection dc) {
    String query = "WHERE idper = " + n;
    Vector<Rib> v = find(query, dc);
    if (v.size() > 0) {
      return (Rib) v.elementAt(0);
    }
    return null;
  }

  public static Vector<Rib> find(String where, DataConnection dc) {
    Vector<Rib> v = new Vector<Rib>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Rib r = new Rib(rs.getInt(1));
        r.setEstablishment(rs.getString(2));
        r.setBranch(rs.getString(3));
        r.setAccount(rs.getString(4));
        r.setRibKey(rs.getString(5));
        r.setBranchId(rs.getInt(6));
        r.setIban(rs.getString(7));
        
        v.addElement(r);
      }
    } catch (SQLException e) {
      v.clear();
      GemLogger.logException("find Rib: ", e);
    }
    return v;
  }
}
