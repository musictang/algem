/*
 * @(#)MemberIO.java	2.7.a 10/01/13
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
import java.util.List;
import java.util.Vector;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.member.Member}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class MemberIO
        extends TableIO
        implements Cacheable
{

  public static final String COLUMNS = "idper,profession,datenais,payeur,nadhesions,pratique,niveau";
  public static final String TABLE = "eleve";
  
  private DataConnection dc;

  public MemberIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Inserts a new member.
   * Transaction should be processed at higher level.
   *
   * @param m
   * @throws SQLException
   */
  public void insert(Member m) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + m.getId()
            + "','" + m.getOccupation() + "'";
    if (!m.getBirth().equals(DateFr.NULLDATE)) {
      query += ",'" + m.getBirth().toString() + "'";
    } else {
      query += ",null";
    }
    if (m.getPayer() == 0) {
      m.setPayer(m.getId());
    }
    query += ",'" + m.getPayer()
            + "','" + m.getNMembership()
            + "','" + m.getPractice()
            + "','" + m.getLevel()
            + "'";
    query += ")";

    dc.executeUpdate(query);
    InstrumentIO.insert(m.getInstruments(), m.getId(), Instrument.MEMBER, dc);
  }

  /**
   * Updates a member.
   * Transaction should be processed at higher level.
   *
   * @param m
   * @throws SQLException
   */
  public void update(Member m) throws SQLException {
    String query = "UPDATE " + TABLE + " SET profession='" + m.getOccupation() + "'";
    if (!m.getBirth().equals(DateFr.NULLDATE)) {
      query += ",datenais='" + m.getBirth().toString() + "'";
    }
    query += ", payeur='" + m.getPayer()
            + "', nadhesions='" + m.getNMembership()
            + "', pratique='" + m.getPractice()
            + "', niveau='" + m.getLevel() + "'"
            + " WHERE idper=" + m.getId();
    InstrumentIO.delete(m.getId(), Instrument.MEMBER, dc);
    InstrumentIO.insert(m.getInstruments(), m.getId(), Instrument.MEMBER, dc);

  }

  public void delete(Member m) throws SQLException {

    String query = "DELETE FROM " + TABLE + " WHERE idper = " + m.getId();
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query);
      InstrumentIO.delete(m.getId(), Instrument.MEMBER, dc);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.logException(ex);
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public Member findId(int n) throws SQLException {
    String query = "WHERE idper = " + n;
    Vector<Member> v = find(query);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public Member getFromRS(ResultSet rs, int col) throws SQLException {
    Member member = new Member(rs.getInt(col++));
    member.setOccupation(rs.getString(col++).trim());
    member.setBirth(new DateFr(rs.getString(col++)));
    member.setPayer(rs.getInt(col++));
    member.setNMemberShip(rs.getInt(col++));
    member.setPractice(rs.getInt(col++));
    member.setLevel(rs.getInt(col++));

    return member;
  }

  public Vector<Member> find(String where) throws SQLException {
    Vector<Member> v = new Vector<Member>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE;
    if (where != null) {
      query += " " + where;
    }
    ResultSet rs;
    rs = dc.executeQuery(query);
    while (rs.next()) {
      Member m = getFromRS(rs, 1);
      m.setInstruments(InstrumentIO.find(m.getId(), Instrument.MEMBER, dc));
      v.addElement(m);
    }
    rs.close();
    return v;
  }

  @Override
  public List<Member> load() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
