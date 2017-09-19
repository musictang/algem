/*
 * @(#) TrainingAgreementIO.java Algem 2.15.0 18/09/17
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
 */
package net.algem.enrolment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.contact.OrganizationIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 07/09/17
 */
public class TrainingAgreementIO
        extends TableIO
{

  public final static String TABLE = "conventionstage";
  private final static String COLUMNS = "id,ctype,idper,idorg,assurance,assuranceref,libelle,saison,debut,fin,datesign";
  private final static String SEQUENCE = "conventionstage_id_seq";

  private DataConnection dc;
  private final OrganizationIO organizationIO;

  public TrainingAgreementIO(DataConnection dc) {
    this.dc = dc;
    this.organizationIO = new OrganizationIO(dc);
  }

  public void create(TrainingAgreement t) throws SQLException {

    int nextId = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + "(" + COLUMNS + ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, nextId);
      ps.setByte(2, t.getType());
      ps.setInt(3, t.getPersonId());
      ps.setInt(4, t.getOrg().getId());
      ps.setString(5, t.getInsurance());
      ps.setString(6, t.getInsuranceRef());
      ps.setString(7, t.getLabel());
      ps.setString(8, t.getSeason());
      ps.setDate(9, new java.sql.Date(t.getStart().getTime()));
      ps.setDate(10, new java.sql.Date(t.getEnd().getTime()));
      ps.setDate(11, new java.sql.Date(t.getSignDate().getTime()));
      GemLogger.info(ps.toString());

      ps.executeUpdate();
      t.setId(nextId);
    }
  }

  public void update(TrainingAgreement t) throws SQLException {
    String query = "UPDATE " + TABLE + " SET idorg=?,assurance=?,assuranceref=?,libelle=?,saison=?,debut=?,fin=?datesign=? WHERE id = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, t.getOrg().getId());
      ps.setString(2, t.getInsurance());
      ps.setString(3,t.getInsuranceRef());
      ps.setString(4,t.getLabel());
      ps.setString(5,t.getSeason());
      ps.setDate(6, new java.sql.Date(t.getStart().getTime()));
      ps.setDate(7, new java.sql.Date(t.getEnd().getTime()));
      ps.setDate(8, new java.sql.Date(t.getSignDate().getTime()));

      ps.setInt(9, t.getId());
      GemLogger.info(ps.toString());

      ps.executeUpdate();

    }
  }

  public void delete(int id) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, id);

      ps.executeUpdate();
    }
  }

  public TrainingAgreement find(int id) throws SQLException {
    String query = "SELECT * FROM " + TABLE + " WHERE id = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          return getFromRS(rs);
        }
      }
    }
    return null;
  }

  public List<TrainingAgreement> findAll(int idper) throws SQLException {
    String query = "SELECT * FROM " + TABLE + " WHERE idper = ?";
    List<TrainingAgreement> agreements = new ArrayList<>();
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, idper);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          agreements.add(getFromRS(rs));
        }
      }
    }
    return agreements;
  }

  /**
   * Converts a result row into a training contract.
   *
   * @param rs resultset instance
   * @return a training contract
   * @throws SQLException
   */
  private TrainingAgreement getFromRS(ResultSet rs) throws SQLException {
    TrainingAgreement t = new TrainingAgreement(rs.getInt(1));
    t.setType(rs.getByte(2));
    t.setPersonId(rs.getInt(3));
    t.setOrg(organizationIO.findId(rs.getInt(4)));
    t.setInsurance(rs.getString(5));
    t.setInsuranceRef(rs.getString(6));
    t.setLabel(rs.getString(7));
    t.setSeason(rs.getString(8));
    t.setStart(rs.getDate(9));
    t.setEnd(rs.getDate(10));
    t.setSignDate(rs.getDate(11));

    return t;
  }


}
