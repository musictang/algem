/*
 * @(#)MemberIO.java	2.17.0 04/06/219
 *                      2.15.0 30/07/2017
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import net.algem.Algem;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.member.Member}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 1.0a 07/07/1999
 */
public class MemberIO
        extends TableIO
        implements Cacheable
{

  public static final String COLUMNS = "idper,profession,datenais,payeur,nadhesions,pratique,niveau,assurance,assuranceref,famille";
  public static final String TABLE = "eleve";
  private static final String CREATE_QUERY = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
  private static final String UPDATE_QUERY = "UPDATE " + TABLE + " SET profession=?,datenais=?,payeur=?,nadhesions=?,pratique=?,niveau=?,assurance=?,assuranceref=?,famille=? WHERE idper=?";

  private DataConnection dc;

  public MemberIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Inserts a new member.
   * Transaction should be processed at higher level.
   *
   * @param m the member to create
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

      if (m.getInsurance() == null || m.getInsurance().isEmpty()) {
        createPs.setNull(8, Types.VARCHAR);
      } else {
        createPs.setString(8, m.getInsurance());
      }
       if (m.getInsuranceRef() == null || m.getInsuranceRef().isEmpty()) {
        createPs.setNull(9, Types.VARCHAR);
      } else {
        createPs.setString(9, m.getInsuranceRef());
      }
      //TODO archiv ?? default false
      createPs.setBoolean(10, false);
      if (!Algem.isFeatureEnabled("cc-mdl")) {
          m.setFamily(m.getPayer());
      }
      createPs.setInt(11, m.getFamily());
      GemLogger.info(createPs.toString());
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
      if (m.getInsurance() == null || m.getInsurance().isEmpty()) {
        updatePs.setNull(7, Types.VARCHAR);
      } else {
        updatePs.setString(7, m.getInsurance());
      }
       if (m.getInsuranceRef() == null || m.getInsuranceRef().isEmpty()) {
        updatePs.setNull(8, Types.VARCHAR);
      } else {
        updatePs.setString(8, m.getInsuranceRef());
      }
      if (!Algem.isFeatureEnabled("cc-mdl")) {
          m.setFamily(m.getPayer());
      }
      updatePs.setInt(9, m.getFamily());

      updatePs.setInt(10, m.getId());

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
    List<Member> v = find(query);
    if (v.size() > 0) {
      return v.get(0);
    }
    return null;
  }

  public Member getFromRS(ResultSet rs, int col) throws SQLException {
    Member m = new Member(rs.getInt(col++));
    String occup = rs.getString(col++);
    m.setOccupation(occup == null ? BundleUtil.getLabel("None.label") : occup.trim());
    m.setBirth(new DateFr(rs.getString(col++)));
    m.setPayer(rs.getInt(col++));
    m.setMembershipCount(rs.getInt(col++));
    m.setPractice(rs.getInt(col++));
    m.setLevel(rs.getInt(col++));
    m.setInsurance(rs.getString(col++));
    m.setInsuranceRef(rs.getString(col++));
    m.setFamily(rs.getInt(col++));

    return m;
  }

  public List<Member> find(String where) throws SQLException {
    List<Member> v = new ArrayList<>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE;
    if (where != null) {
      query += " " + where;
    }
    try (ResultSet rs = dc.executeQuery(query)) {
    while (rs.next()) {
      Member m = getFromRS(rs, 1);
      m.setInstruments(InstrumentIO.find(m.getId(), Instrument.MEMBER, dc));
      v.add(m);
    }
    }
    return v;
  }

  @Override
  public List<Member> load() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
