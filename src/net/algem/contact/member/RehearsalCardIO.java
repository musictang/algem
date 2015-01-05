/*
 * @(#)RehearsalCardIO.java 2.9.2 26/12/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Persistence for rehearsal card.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class RehearsalCardIO
        extends TableIO
{

  public final static String TABLE = "carteaborepet";
  public final static String SEQUENCE = "carteaborepet_id_seq";
  public final static String COLUMNS = "id, libelle, montant, nbseances, dureemin";
  public final static String UPDATE_STATEMENT = "UPDATE " + TABLE + " SET libelle = ?, montant = ?, nbseances = ?, dureemin = ? WHERE id = ?";

  public static RehearsalCard find(int id, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);

    if (!rs.next()) {
      return null;
    }
    RehearsalCard c = new RehearsalCard();
    c.setId(rs.getInt(1));
    c.setLabel(rs.getString(2));
    c.setAmount(rs.getFloat(3));
    c.setSessionsNumber(rs.getInt(4));
    c.setLength(rs.getInt(5));

    return c;
  }

  public static Vector<RehearsalCard> findAll(String where, DataConnection dc) throws SQLException {
    Vector<RehearsalCard> v = new Vector<RehearsalCard>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      RehearsalCard c = new RehearsalCard();
      c.setId(rs.getInt(1));
      c.setLabel(rs.getString(2));
      c.setAmount(rs.getFloat(3));
      c.setSessionsNumber(rs.getInt(4));
      c.setLength(rs.getInt(5));
      v.addElement(c);
    }
    return v;
  }

  public static void insert(RehearsalCard card, DataConnection dc) throws SQLException {
    card.setId(nextId(SEQUENCE, dc));
    dc.executeUpdate(getInsertQuery(card));
  }

  public static boolean delete(int id, DataConnection dc) throws SQLException {
    int deleted = dc.executeUpdate(getDeleteQuery(id));
    return deleted > 0;
  }

  public static int update(RehearsalCard card, DataConnection dc) throws SQLException {
    PreparedStatement ps = dc.prepareStatement(UPDATE_STATEMENT);
    ps.setString(1, unEscape(card.getLabel()));
    ps.setFloat(2, card.getAmount());
    ps.setInt(3, card.getSessionsNumber());
    ps.setInt(4, card.getLength());
    ps.setInt(5, card.getId());
    return ps.executeUpdate();
  }

  public static String getInsertQuery(RehearsalCard card) {
    StringBuilder query = new StringBuilder("INSERT INTO " + TABLE + " VALUES(");
    query.append(card.getId());
    query.append(LEFT_COL_SEPARATOR).append(escape(card.getLabel())).append(RIGHT_COL_SEPARATOR).append(card.getAmount()).append(RIGHT_COL_SEPARATOR).append(card.getSessionsNumber()).append(RIGHT_COL_SEPARATOR).append(card.getLength()).append(END_OF_QUERY);
    return query.toString();
  }

  public static String getDeleteQuery(int id) {
    return "DELETE FROM " + TABLE + " WHERE id = " + id;
  }
}
