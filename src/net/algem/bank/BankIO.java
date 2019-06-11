/*
 * @(#)BankIO.java	2.8.i 04/07/13
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
 * Io methods for class {@link net.algem.bank.Bank}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class BankIO
        extends TableIO
{

  private static final String TABLE = "banque";

  public static void insert(Bank b, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + b.getCode()
            + "','" + escape(b.getName())
            + "','" + (b.isMulti() ? "t" : "f")
            + "')";

    dc.executeUpdate(query);
  }

  public static void update(Bank b, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "nom='" + escape(b.getName())
            + "',multiguichet='" + (b.isMulti() ? "t" : "f")
            + "' WHERE code='" + b.getCode() + "'";

    dc.executeUpdate(query);
  }

  public static void delete(Bank b, DataConnection dc) throws SQLException {
  }

  /**
   * Search a bank from code identifier {@code code}.
   *
   * @param dc
   * @param code identifier code
   * @return a bank instance or null
   */
  public static Bank findCode(String code, DataConnection dc) {
    String query = "WHERE code = '" + code + "'";
    Vector<Bank> v = find(query, dc);
    if (v.size() > 0) {
      return (Bank) v.elementAt(0);
    }
    return null;
  }

  public static Vector<Bank> find(String where, DataConnection dc) {
    Vector<Bank> v = new Vector<Bank>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    query += " ORDER BY code";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Bank b = new Bank();
        b.setCode(rs.getString(1));
        b.setName(unEscape(rs.getString(2)).trim());
        b.setMulti(rs.getBoolean(3));

        v.addElement(b);
      }
      rs.close();
    } catch (Exception e) {
      GemLogger.logException(query, e);
    }
    return v;
  }
}
