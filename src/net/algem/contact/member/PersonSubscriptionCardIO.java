/*
 * @(#)PersonSubscriptionCardIO.java 2.6.a 18/09/12
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
package net.algem.contact.member;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PersonSubscriptionCardIO
        extends TableIO
{

  private final static String TABLE = "carteabopersonne";
  private final static String SEQUENCE = "carteabopersonne_id_seq";
  private final static String COLUMNS = "id, idper, idcarte, date_achat, restant";
  private DataConnection dc;

  public PersonSubscriptionCardIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Gets the last available card for the person {@code idper}.
   *
   * @param idper
   * @return a subscription card
   * @throws SQLException
   */
  public PersonSubscriptionCard find(int idper, String conditions)
          throws SQLException {
    String query = null;
    if (conditions == null) {
      query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE idper = " + idper + " ORDER BY id desc LIMIT 1";
    } else {
      query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE idper = " + idper + "AND " + conditions + " ORDER BY id desc LIMIT 1";
    }
    //System.out.println(query);
    ResultSet rs = dc.executeQuery(query);

    if (!rs.next()) {
      return null;
    }
    PersonSubscriptionCard pc = new PersonSubscriptionCard();
    pc.setId(rs.getInt(1));
    pc.setIdper(rs.getInt(2));
    pc.setRehearsalCardId(rs.getInt(3));
    pc.setPurchaseDate(new DateFr(rs.getDate(4)));
    pc.setRest(rs.getInt(5));

    return pc;
  }

  public PersonSubscriptionCard find(int id)
          throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);

    if (!rs.next()) {
      return null;
    }
    PersonSubscriptionCard c = new PersonSubscriptionCard();
    c.setId(rs.getInt(1));
    c.setIdper(rs.getInt(2));
    c.setRehearsalCardId(rs.getInt(3));
    c.setPurchaseDate(new DateFr(rs.getDate(4)));
    c.setRest(rs.getInt(5));

    return c;
  }

  public PersonSubscriptionCard insert(PersonSubscriptionCard card)
          throws SQLException {
    //System.out.println("cap == null ?? "+ (cap == null));
    card.setId(nextId(SEQUENCE, dc));
    dc.executeUpdate(getInsertQuery(card));
    return card;
  }

  public int update(PersonSubscriptionCard pc)
          throws SQLException {
    String query = "UPDATE " + TABLE + " SET restant = " + pc.getRest() + " WHERE id = " + pc.getId();
    return dc.executeUpdate(query);
  }

  public void delete(int id)
          throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
    dc.executeUpdate(query);
  }
  
  public void deleteByIdper(int idper) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper;
    dc.executeUpdate(query);
  }

  private static String getInsertQuery(PersonSubscriptionCard pc) {
    StringBuilder query = new StringBuilder("INSERT INTO " + TABLE + " VALUES(");

    query.append(pc.getId());
    query.append(LEFT_COL_SEPARATOR).append(pc.getIdper());
    query.append(RIGHT_COL_SEPARATOR).append(pc.getRehearsalCardId());
    query.append(RIGHT_COL_SEPARATOR).append(pc.getPurchaseDate().toString());
    query.append(RIGHT_COL_SEPARATOR).append(pc.getRest());
    query.append(END_OF_QUERY);
    return query.toString();
  }
}
