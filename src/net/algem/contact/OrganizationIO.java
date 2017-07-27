/*
 * @(#) OrganizationIO.java Algem 2.15.0 25/07/2017
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
package net.algem.contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.config.Company;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 25/07/2017
 */
public class OrganizationIO extends TableIO {

  public static final String TABLE = "organisation";
  public static final String LOGO_COL = "logo";
  public static final String STAMP_COL = "stamp";
  private static final String SEQUENCE = "organisation_id_seq";

  private DataConnection dc;

  public OrganizationIO(DataConnection dc) {
    this.dc = dc;
  }

  public Organization findId(int id) throws SQLException {
    if (id == 0) {
      return null;
    }
    String query = "SELECT id,nom,raison,siret,naf,codefp,tva FROM " + TABLE + " WHERE id=?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, id);
      GemLogger.info(ps.toString());
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        return getFromRS(rs);
      }
      return null;
    }
  }

  public Organization findId(String name) throws SQLException {
    if (name == null) {
      return null;
    }
    String query = "SELECT id,nom,raison,siret,naf,codefp,tva FROM " + TABLE + " WHERE nom ~* ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, name);
      GemLogger.info(ps.toString());
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        return getFromRS(rs);
      }
      return null;
    }
  }

  public List<Organization> find(String name) throws SQLException {
    List<Organization> orgs = new ArrayList<>();
    if (name != null) {
      String query = "SELECT id,nom,raison,siret,naf,codefp,tva FROM " + TABLE + " WHERE nom ~* ?";
      try (PreparedStatement ps = dc.prepareStatement(query)) {
        ps.setString(1, name);
        GemLogger.info(ps.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          orgs.add(getFromRS(rs));
        }
      }
    }
    return orgs;
  }

  private Organization getFromRS(ResultSet rs) throws SQLException {
    Organization org = new Organization(rs.getInt(1));
    org.setName(rs.getString(2));
    org.setCompanyName(rs.getString(3));
    org.setSiret(rs.getString(4));
    org.setNafCode(rs.getString(5));
    org.setFpCode(rs.getString(6));
    org.setVatCode(rs.getString(7));
    return org;
  }

  public void create(Organization org) throws SQLException {
    int next = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?)";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, next);
      ps.setString(2, org.getName());
      if (org.getCompanyName() == null || org.getCompanyName().trim().isEmpty()) {
        ps.setNull(3, java.sql.Types.VARCHAR);
      } else {
        ps.setString(3, org.getCompanyName());
      }
      if (org.getSiret() == null || org.getSiret().trim().isEmpty()) {
        ps.setNull(4, java.sql.Types.VARCHAR);
      } else {
        ps.setString(4, org.getSiret());
      }
      if (org.getNafCode() == null || org.getNafCode().trim().isEmpty()) {
        ps.setNull(5, java.sql.Types.VARCHAR);
      } else {
        ps.setString(5, org.getNafCode());
      }
      if (org.getFpCode() == null || org.getFpCode().trim().isEmpty()) {
        ps.setNull(6, java.sql.Types.VARCHAR);
      } else {
        ps.setString(6, org.getFpCode());
      }
      if (org.getVatCode() == null || org.getVatCode().trim().isEmpty()) {
        ps.setNull(7, java.sql.Types.VARCHAR);
      } else {
        ps.setString(7, org.getVatCode());
      }

      GemLogger.info(ps.toString());
      ps.executeUpdate();
      org.setId(next);
    }

  }

  public void update(Organization org) throws SQLException {
    String query = "UPDATE " + TABLE + " SET nom=?,raison=?,siret=?,naf=?,codefp=?,tva=? WHERE id=?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, org.getName());
      if (org.getCompanyName() == null || org.getCompanyName().trim().isEmpty()) {
        ps.setNull(2, java.sql.Types.VARCHAR);
      } else {
        ps.setString(2, org.getCompanyName());
      }
      if (org.getSiret() == null || org.getSiret().trim().isEmpty()) {
        ps.setNull(3, java.sql.Types.VARCHAR);
      } else {
        ps.setString(3, org.getSiret());
      }
      if (org.getNafCode() == null || org.getNafCode().trim().isEmpty()) {
        ps.setNull(4, java.sql.Types.VARCHAR);
      } else {
        ps.setString(4, org.getNafCode());
      }
      if (org.getFpCode() == null || org.getFpCode().trim().isEmpty()) {
        ps.setNull(5, java.sql.Types.VARCHAR);
      } else {
        ps.setString(5, org.getFpCode());
      }
      if (org.getVatCode() == null || org.getVatCode().trim().isEmpty()) {
        ps.setNull(6, java.sql.Types.VARCHAR);
      } else {
        ps.setString(6, org.getVatCode());
      }

      ps.setInt(7, org.getId());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }

  }

  public Company getDefault() throws SQLException {

    String query = "SELECT idper,domaine,logo,stamp FROM societe WHERE id=1";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Company c = new Company(rs.getInt(1));
        c.setDomain(rs.getString(2));
        c.setLogo(rs.getBytes(3));
        c.setStamp(rs.getBytes(4));

        c.setOrg(OrganizationIO.this.findId(c.getIdper()));
        c.setContact(ContactIO.findId(c.getIdper(), dc));

        return c;
      }
    }

    return null;
  }

  public void saveDefault(Company comp) throws SQLException {
    String query = "UPDATE societe SET domaine=? WHERE id=1";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, comp.getDomain());
      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }
  }

  public void saveImage(String col, byte[] img) throws SQLException {
    String query = "UPDATE societe SET " + col + " = ?  WHERE id=1";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      if (img == null) {
        ps.setNull(1, java.sql.Types.OTHER);
      } else {
        ps.setBytes(1, img);
      }
      ps.executeUpdate();
    }
  }
}
