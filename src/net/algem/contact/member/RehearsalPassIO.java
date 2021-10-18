/*
 * @(#)RehearsalPassIO.java 2.9.2 12/01/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact.member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Rehearsal pass persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class RehearsalPassIO
        extends TableIO
{

  public final static String TABLE = "carteaborepet";
  public final static String SEQUENCE = "carteaborepet_id_seq";
  public final static String COLUMNS = "id, libelle, montant, dureemin, totalmin";
  public final static String UPDATE_STATEMENT = "UPDATE " + TABLE + " SET libelle = ?, montant = ?, dureemin = ?, totalmin = ? WHERE id = ?";

  public static RehearsalPass find(int id, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);

    if (!rs.next()) {
      return null;
    }
    RehearsalPass c = new RehearsalPass();
    c.setId(rs.getInt(1));
    c.setLabel(rs.getString(2));
    c.setAmount(rs.getFloat(3));
    c.setMin(rs.getInt(4));
    c.setTotalTime(rs.getInt(5));

    return c;
  }

  public static List<RehearsalPass> findAll(String where, DataConnection dc) throws SQLException {
    List<RehearsalPass> v = new ArrayList<>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      RehearsalPass c = new RehearsalPass();
      c.setId(rs.getInt(1));
      c.setLabel(rs.getString(2));
      c.setAmount(rs.getFloat(3));
      c.setMin(rs.getInt(4));
      c.setTotalTime(rs.getInt(5));
      v.add(c);
    }
    return v;
  }
  
  public static boolean isActive(int id, DataConnection dc) throws SQLException {
    String query = "SELECT count(DISTINCT id) FROM " + PersonSubscriptionCardIO.TABLE + " WHERE idpass = " + id;
    int n = 0;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      n = rs.getInt(1);
    }   
    return n > 0;
  }

  public static void insert(RehearsalPass card, DataConnection dc) throws SQLException {
    card.setId(nextId(SEQUENCE, dc));
    dc.executeUpdate(getInsertQuery(card));
  }

  public static boolean delete(int id, DataConnection dc) throws SQLException {
    int deleted = dc.executeUpdate(getDeleteQuery(id));
    return deleted > 0;
  }

  public static int update(RehearsalPass card, DataConnection dc) throws SQLException {
    PreparedStatement ps = dc.prepareStatement(UPDATE_STATEMENT);
    ps.setString(1, escape(card.getLabel()));
    ps.setFloat(2, card.getAmount());
    ps.setInt(3, card.getMin());
    ps.setInt(4, card.getTotalTime());
    ps.setInt(5, card.getId());
    return ps.executeUpdate();
  }

  public static String getInsertQuery(RehearsalPass card) {
    return "INSERT INTO " + TABLE + " VALUES("
            + card.getId()
            + ", '" + escape(card.getLabel())
            + "', " + card.getAmount()
            + ", " + card.getMin()
            + ", " + card.getTotalTime()
            + ")";
   }

  public static String getDeleteQuery(int id) {
    return "DELETE FROM " + TABLE + " WHERE id = " + id;
  }
}
