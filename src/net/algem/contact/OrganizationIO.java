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
  public static final String COLUMNS = "id,nom,idper,raison,siret,naf,codefp,codetva";
  public static final String LOGO_COL = "logo";
  public static final String STAMP_COL = "stamp";
  private static final String SEQUENCE = "organisation_id_seq";

  private DataConnection dc;

  public OrganizationIO(DataConnection dc) {
    this.dc = dc;
  }

  public Organization findId(int id) throws SQLException {

    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=?";
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

  public List<Organization> find(String name) throws SQLException {
    List<Organization> orgs = new ArrayList<>();
    if (name != null) {
      String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE lower(nom) LIKE ?";
      try (PreparedStatement ps = dc.prepareStatement(query)) {
        ps.setString(1, name.toLowerCase() + "%");
        GemLogger.info(ps.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          orgs.add(getFromRS(rs));
        }
      }
    }
    return orgs;
  }

  public List<Person> findMembers(int orgId) throws SQLException {
    List<Person> pers = new ArrayList<>();
    if (orgId > 0) {
      String query = "SELECT p.id,CASE WHEN p.nom IS NULL OR p.nom = '' THEN o.nom ELSE p.nom END,p.prenom FROM " + PersonIO.TABLE + " p JOIN organisation o ON p.organisation = o.id WHERE o.id = ? ORDER BY p.prenom";
      try (PreparedStatement ps = dc.prepareStatement(query)) {
        ps.setInt(1, orgId);
        GemLogger.info(ps.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          Person p = new Person(rs.getInt(1));
          p.setName(rs.getString(2));
          p.setOrganization(new Organization());
          p.setFirstName(rs.getString(3));
          pers.add(p);
        }
      }
    }
    return pers;
  }

  private Organization getFromRS(ResultSet rs) throws SQLException {
    Organization org = new Organization(rs.getInt(1));
    org.setName(rs.getString(2));
    org.setReferent(rs.getInt(3));
    org.setCompanyName(rs.getString(4));
    org.setSiret(rs.getString(5));
    org.setNafCode(rs.getString(6));
    org.setFpCode(rs.getString(7));
    org.setVatCode(rs.getString(8));
    return org;
  }

  public void create(Organization org) throws SQLException {
    int nextId = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?)";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, nextId);
      ps.setString(2, org.getName());
      ps.setInt(3,org.getReferent());
      if (org.getCompanyName() == null || org.getCompanyName().trim().isEmpty()) {
        ps.setNull(4, java.sql.Types.VARCHAR);
      } else {
        ps.setString(4, org.getCompanyName());
      }
      if (org.getSiret() == null || org.getSiret().trim().isEmpty()) {
        ps.setNull(5, java.sql.Types.VARCHAR);
      } else {
        ps.setString(5, org.getSiret());
      }
      if (org.getNafCode() == null || org.getNafCode().trim().isEmpty()) {
        ps.setNull(6, java.sql.Types.VARCHAR);
      } else {
        ps.setString(6, org.getNafCode());
      }
      if (org.getFpCode() == null || org.getFpCode().trim().isEmpty()) {
        ps.setNull(7, java.sql.Types.VARCHAR);
      } else {
        ps.setString(7, org.getFpCode());
      }
      if (org.getVatCode() == null || org.getVatCode().trim().isEmpty()) {
        ps.setNull(8, java.sql.Types.VARCHAR);
      } else {
        ps.setString(8, org.getVatCode());
      }

      GemLogger.info(ps.toString());
      ps.executeUpdate();
      org.setId(nextId);
    }

  }

  public void update(Organization org) throws SQLException {
    String query = "UPDATE " + TABLE + " SET nom=?,idper=?,raison=?,siret=?,naf=?,codefp=?,codetva=? WHERE id=?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, org.getName());
      ps.setInt(2, org.getReferent());
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

      ps.setInt(8, org.getId());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }

  }

  public Company getDefault() throws SQLException {

    String query = "SELECT p.id,s.domaine,s.logo,s.stamp FROM personne p JOIN societe s ON p.id = s.idper WHERE s.id=1";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Company c = new Company();
        
        c.setDomain(rs.getString(2));
        c.setLogo(rs.getBytes(3));
        c.setStamp(rs.getBytes(4));

        c.setContact(ContactIO.findId(rs.getInt(1), dc));
        c.setOrg(OrganizationIO.this.findId(c.getContact().getOrganization().getId()));
        c.setReferent(ContactIO.findId(c.getOrg().getReferent(), dc));

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
