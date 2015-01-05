/*
 * @(#)PersonSubscriptionCardIO.java 2.9.2 26/12/14
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class PersonSubscriptionCardIO
        extends TableIO
{

  private final static String TABLE = "carteabopersonne";
  private final static String SEQUENCE = "carteabopersonne_id_seq";
  private final static String SESSION_TABLE = "carteabopersessions";
  private final static String SESSION_SEQUENCE = "carteabopersessions_id_seq";
  private final static String COLUMNS = "id, idper, idpass, date_achat, restant";
  private DataConnection dc;

  public PersonSubscriptionCardIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Gets the last available card for the person {@code idper}.
   *
   * @param idper
   * @param and
   * @param complete
   * @return a subscription card
   * @throws SQLException
   */
  public PersonSubscriptionCard find(int idper, String and, boolean complete) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE idper = " + idper;
    if (and != null) {
      query += " AND " + and;
    }
    query += " ORDER BY id DESC LIMIT 1";

    ResultSet rs = dc.executeQuery(query);
    PersonSubscriptionCard pc = null;
    while(rs.next()) {
      pc = new PersonSubscriptionCard();
      pc.setId(rs.getInt(1));
      pc.setIdper(rs.getInt(2));
      pc.setPassId(rs.getInt(3));
      pc.setPurchaseDate(new DateFr(rs.getDate(4)));
      pc.setRest(rs.getInt(5));
      if (complete) {
        List<PersonalCardSession> sessions = findSessions(pc.getId());
        for (PersonalCardSession s : sessions) {
          pc.addSession(s);
        }
      }
    }
    return pc;
  }

  public PersonSubscriptionCard find(int id) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);

    if (!rs.next()) {
      return null;
    }
    PersonSubscriptionCard c = new PersonSubscriptionCard();
    c.setId(rs.getInt(1));
    c.setIdper(rs.getInt(2));
    c.setPassId(rs.getInt(3));
    c.setPurchaseDate(new DateFr(rs.getDate(4)));
    c.setRest(rs.getInt(5));

    return c;
  }

  List<PersonalCardSession> findSessions(int cardId) throws SQLException {
    List<PersonalCardSession> sessions = new ArrayList<PersonalCardSession>();
    String query = "SELECT * FROM carteabopersessions WHERE idcarte = " +  cardId;
    ResultSet rs = dc.executeQuery(query);
    while(rs.next()) {
      PersonalCardSession s = new PersonalCardSession();
      s.setId(rs.getInt(1));
      s.setCardId(rs.getInt(2));
      s.setScheduleId(rs.getInt(3));
      sessions.add(s);
    }
    return sessions;
  }

  public PersonSubscriptionCard insert(PersonSubscriptionCard card) {
    try {
      dc.setAutoCommit(false);
      card.setId(nextId(SEQUENCE, dc));
      dc.executeUpdate(getInsertQuery(card));
      updateSessions(card);
      dc.commit();
      return card;
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.log(ex.getMessage());
      return null;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Adds or remove sessions from database to reflect the actual sessions on this {@code card}.
   * @param card personal subscription card
   * @throws SQLException
   */
  private void updateSessions(PersonSubscriptionCard card) throws SQLException {
    List<PersonalCardSession> currentSessions = card.getSessions();
    List<PersonalCardSession> savedSessions = findSessions(card.getId());
    if (currentSessions.size() > savedSessions.size()) {
      for (int i = savedSessions.size(); i < currentSessions.size(); i++) {
        PersonalCardSession s = currentSessions.get(i);
        s.setCardId(card.getId());
        insertSession(currentSessions.get(i));
      }
    } else if (currentSessions.size() < savedSessions.size()) {
      for (int i = currentSessions.size(); i < savedSessions.size(); i++) {
        deleteSession(savedSessions.get(i).getId());
      }
    }
  }

  private void insertSession(PersonalCardSession s) throws SQLException {
    int nextId = nextId(SESSION_SEQUENCE, dc);
    String query = "INSERT INTO " + SESSION_TABLE + " VALUES("
      + nextId
      + "," + s.getCardId()
      + "," + s.getScheduleId()
      + ",'" + s.getStart()
      + "','" + s.getEnd()
       + "')";
    dc.executeUpdate(query);
    s.setId(nextId);
  }

  private void deleteSession(int id) throws SQLException {
    String query = "DELETE FROM carteabopersessions WHERE id = " + id;
    dc.executeUpdate(query);
  }

  public int update(PersonSubscriptionCard pc) {
    try {
      dc.setAutoCommit(false);
      String query = "UPDATE " + TABLE + " SET restant = " + pc.getRest() + " WHERE id = " + pc.getId();
      updateSessions(pc);
      dc.commit();
      return dc.executeUpdate(query);
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.log(ex.getMessage());
      return 0;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void delete(int id) throws SQLException {
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
    query.append(RIGHT_COL_SEPARATOR).append(pc.getPassId());
    query.append(RIGHT_COL_SEPARATOR).append(pc.getPurchaseDate().toString());
    query.append(RIGHT_COL_SEPARATOR).append(pc.getRest());
    query.append(END_OF_QUERY);
    return query.toString();
  }
}
