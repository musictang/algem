/*
 * @(#)MemberIO.java	2.13.0 31/03/17
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
 *
 */
package net.algem.contact.member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.member.Member}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 1.0a 07/07/1999
 */
public class MemberIO
        extends TableIO
        implements Cacheable
{

  public static final String COLUMNS = "idper,profession,datenais,payeur,nadhesions,pratique,niveau";
  public static final String TABLE = "eleve";
  private static final String CREATE_QUERY = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?)";
  private static final String UPDATE_QUERY = "UPDATE " + TABLE + " SET profession=?,datenais=?,payeur=?,nadhesions=?,pratique=?,niveau=? WHERE idper=?";

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
    if (m.getPayer() == 0) {
      m.setPayer(m.getId());
    }
    DateFr birth =  m.getBirth() == null || m.getBirth().bufferEquals(DateFr.NULLDATE) ? null : m.getBirth();
    try (PreparedStatement createPs = dc.prepareStatement(CREATE_QUERY)) {
      createPs.setInt(1, m.getId());
      String occup = m.getOccupation();
      createPs.setString(2, occup == null || occup.isEmpty() ? BundleUtil.getLabel("None.label") : occup);
      createPs.setDate(3, birth == null ? null : new java.sql.Date(birth.getDate().getTime()));
      createPs.setInt(4, m.getPayer());
      createPs.setInt(5, m.getMembershipCount());
      createPs.setInt(6, m.getPractice());
      createPs.setInt(7, m.getLevel());
      createPs.executeUpdate();
    }
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
    DateFr birth = m.getBirth().bufferEquals(DateFr.NULLDATE) ? null : m.getBirth();
    try (PreparedStatement updatePs = dc.prepareStatement(UPDATE_QUERY)) {
      updatePs.setString(1, m.getOccupation());
      updatePs.setDate(2, birth == null ? null : new java.sql.Date(birth.getDate().getTime()));
      updatePs.setInt(3, m.getPayer());
      updatePs.setInt(4, m.getMembershipCount());
      updatePs.setInt(5, m.getPractice());
      updatePs.setInt(6, m.getLevel());
      updatePs.setInt(7, m.getId());
      updatePs.executeUpdate();
    }
    InstrumentIO.delete(m.getId(), Instrument.MEMBER, dc);
    InstrumentIO.insert(m.getInstruments(), m.getId(), Instrument.MEMBER, dc);
  }

  public void delete(final int m) throws SQLException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try(PreparedStatement deletePs = dc.prepareStatement("DELETE FROM " + TABLE + " WHERE idper = ?")) {
            deletePs.setInt(1, m);
            deletePs.executeUpdate();
          }
          InstrumentIO.delete(m, Instrument.MEMBER, dc);
          return null;
        }
      });
    } catch(Exception e) {
      throw new SQLException(e.getMessage());
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
    String occup = rs.getString(col++);
    member.setOccupation(occup == null ? BundleUtil.getLabel("None.label") : occup.trim());
    member.setBirth(new DateFr(rs.getString(col++)));
    member.setPayer(rs.getInt(col++));
    member.setMembershipCount(rs.getInt(col++));
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
