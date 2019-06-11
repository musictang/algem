/*
 * @(#)PersonSubscriptionCardIO.java 2.9.2 07/01/15
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
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

  public final static String TABLE = "carteabopersonne";
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
   * @param idper person's id
   * @param and and request
   * @param complete complete with sessions
   * @param limit limit of the request
   * @return a subscription card
   * @throws SQLException
   */
  public List<PersonSubscriptionCard> find(int idper, String and, boolean complete, int limit) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE idper = " + idper;
    if (and != null) {
      query += " AND " + and;
    }
    query += " ORDER BY id DESC";
    if (limit > 0) {
      query += " LIMIT " + limit;
    }
    List<PersonSubscriptionCard> cards = new ArrayList<PersonSubscriptionCard>();
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
        List<SubscriptionCardSession> sessions = findSessions(pc.getId(), null);
        for (SubscriptionCardSession s : sessions) {
          pc.addSession(s);
        }
      }
      cards.add(pc);
    }
    return cards;
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

  List<SubscriptionCardSession> findSessions(int cardId, String where) throws SQLException {
    List<SubscriptionCardSession> sessions = new ArrayList<SubscriptionCardSession>();
    String query = "SELECT * FROM carteabopersessions WHERE idcarte = " +  cardId;
    query += (where == null ? "" : where);
    ResultSet rs = dc.executeQuery(query);
    while(rs.next()) {
      SubscriptionCardSession s = new SubscriptionCardSession();
      s.setId(rs.getInt(1));
      s.setCardId(rs.getInt(2));
      s.setScheduleId(rs.getInt(3));
      s.setStart(new Hour(rs.getString(4)));
      s.setEnd(new Hour(rs.getString(5)));
      sessions.add(s);
    }
    return sessions;
  }

  public void insert(PersonSubscriptionCard c) {
    try {
      dc.setAutoCommit(false);
      c.setId(nextId(SEQUENCE, dc));
      String query = "INSERT INTO " + TABLE + " VALUES("
              + c.getId()
              + ", " + c.getIdper()
              + ", " + c.getPassId()
              + ", '" + c.getPurchaseDate().toString()
              + "', " + c.getRest()
              + ")";
      dc.executeUpdate(query);
      updateSessions(c);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.log(ex.getMessage());
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
    List<SubscriptionCardSession> currentSessions = card.getSessions();
    List<SubscriptionCardSession> savedSessions = findSessions(card.getId(), null);
    int min = Math.min(currentSessions.size(), savedSessions.size());
    for (int i = 0; i < min; i++) {
      updateSession(currentSessions.get(i));
    }
    if (currentSessions.size() > min) {
      for (int j = min; j < currentSessions.size(); j++) {
        SubscriptionCardSession s = currentSessions.get(j);
        s.setCardId(card.getId());
        insertSession(currentSessions.get(j));
      }
    } else if (savedSessions.size() > min) {
      for (int j = min; j < savedSessions.size(); j++) {
        deleteSession(savedSessions.get(j).getId());
      }
    }
  }

  private void insertSession(SubscriptionCardSession s) throws SQLException {
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
  
  private void updateSession(SubscriptionCardSession s) throws SQLException {
    String query = "UPDATE " + SESSION_TABLE  
            + " SET debut = '" + s.getStart() 
            + "', fin = '" + s.getEnd() 
            + "' WHERE id = " + s.getId();
    dc.executeUpdate(query);
  }

  private void deleteSession(int id) throws SQLException {
    String query = "DELETE FROM " + SESSION_TABLE + " WHERE id = " + id;
    dc.executeUpdate(query);
  }

  public int update(PersonSubscriptionCard pc) throws MemberException {
    try {
      dc.setAutoCommit(false);
      String query = "UPDATE " + TABLE + " SET restant = " + pc.getRest() + " WHERE id = " + pc.getId();
      updateSessions(pc);
      int n = dc.executeUpdate(query);
      dc.commit();
      return n;
    } catch (SQLException ex) {
      dc.rollback();
      throw new MemberException(ex.getMessage());
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

}
