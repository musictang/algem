/*
 * @(#)PersonIO.java 2.15.0 14/09/17
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
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.algem.billing.InvoiceIO;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.OrderIO;
import net.algem.planning.Action;
import net.algem.planning.ActionIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

import java.sql.PreparedStatement;
import static java.lang.String.format;

/**
 * IO methods for class {@link net.algem.contact.Person}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 */
public class PersonIO
  extends TableIO
  implements Cacheable {

  public static final String TABLE = "personne";
  public static final String VIEW = "personnevue";
  public static final String SEQUENCE = "idper";
  public static final String ALIAS = "p";
  public static final String COLUMNS = "p.id,p.ptype,p.nom,p.prenom,p.civilite,p.droit_img,p.partenaire,p.pseudo,p.organisation,p.onom,p.oraison";

  public static final String PRE_QUERY = "SELECT DISTINCT " + COLUMNS + " FROM " + VIEW + " p ";

  /** Next column number in joined queries. */
  public static final int PERSON_COLUMNS_OFFSET = 12;
  private static final String FIND_BY_ID_QUERY = PRE_QUERY + "WHERE p.id = ? LIMIT 1";

  private DataConnection dc;
  private PreparedStatement findByIdStmt;

  public PersonIO(DataConnection _dc) {
    this.dc = _dc;
    findByIdStmt = dc.prepareStatement(FIND_BY_ID_QUERY);
  }

  public void insert(final Person p) throws SQLException {
    try {
      final int n = nextId(SEQUENCE, dc);
      final String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?,?)";
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try (PreparedStatement ps = dc.prepareStatement(query)) {
            ps.setInt(1, n);
            ps.setInt(2, p.getType());
            ps.setString(3, p.getName().toUpperCase());
            ps.setString(4, p.getFirstName());
            if (p.getGender() == null || p.getGender().isEmpty()) {
              ps.setNull(5, java.sql.Types.CHAR);
            } else {
              ps.setString(5, p.getGender());
            }
            ps.setBoolean(6, p.hasImgRights());
            ps.setBoolean(7, p.isPartnerInfo());

            if (p.getNickName() == null || p.getNickName().isEmpty()) {
              ps.setNull(8, java.sql.Types.VARCHAR);
            } else {
              ps.setString(8, p.getNickName());
            }
            Organization o = p.getOrganization();
            if (o != null && o.getName() != null && !o.getName().isEmpty()) {
              //TODO check exists ??
              ps.setInt(9, n);
              o.setId(n);
              o.setReferent(n);
            } else {
              ps.setInt(9, 0);
            }

            GemLogger.info(ps.toString());

            ps.executeUpdate();
            p.setId(n);
            createOrganization(p);
          }
          return null;
        }
      });
    } catch (Exception ex) {
      throw new SQLException(ex.getMessage());
    }

  }

  private void createOrganization(Person p) throws SQLException {
    if (p.getOrganization() != null && p.getOrganization().getId() > 0) {
        new OrganizationIO(dc).create(p.getOrganization());
    }
  }

  public void update(Person p) throws SQLException {
    String query = "UPDATE " + TABLE + " SET nom=?,prenom=?,civilite=?,droit_img=?,partenaire=?,pseudo=?,organisation=? WHERE id=?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, p.getName().toUpperCase());
      ps.setString(2, p.getFirstName());
      if (p.getGender() == null || p.getGender().isEmpty()) {
        ps.setNull(3, java.sql.Types.CHAR);
      } else {
        ps.setString(3, p.getGender());
      }
      ps.setBoolean(4, p.hasImgRights());

      ps.setBoolean(5, p.isPartnerInfo());
      if (p.getNickName() == null || p.getNickName().isEmpty()) {
        ps.setNull(6, java.sql.Types.VARCHAR);
      } else {
        ps.setString(6, p.getNickName());
      }
      ps.setInt(7, p.getOrganization() == null ? 0 : p.getOrganization().getId());
      ps.setInt(8, p.getId());

      GemLogger.info(ps.toString());

      ps.executeUpdate();
    }
  }

  public void delete(Person p) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId() + " AND ptype = " + p.getType();
    dc.executeUpdate(query);
  }

  public Person findById(String n) {
    return PersonIO.this.findById(new Integer(n));
  }

  public Person findById(int n) {
    ResultSet rs = null;
    try {
      if (findByIdStmt == null || findByIdStmt.isClosed()) {
        findByIdStmt = dc.prepareStatement(FIND_BY_ID_QUERY);
      }
      findByIdStmt.setInt(1, n);
      rs = findByIdStmt.executeQuery();
      while (rs.next()) {
        return getFromRS(rs);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } finally {
      closeRS(rs);
    }
    return null;
  }

  public static Person getFromRS(ResultSet rs) throws SQLException {
    Person p = new Person();
    p.setId(rs.getInt(1));
    p.setType(rs.getShort(2));
    String name = unEscape(rs.getString(3));
    p.setName(name != null ? name.trim() : null);
    String firstname = unEscape(rs.getString(4));
    p.setFirstName(firstname != null ? firstname.trim() : null);
    String cv = rs.getString(5);
    p.setGender(cv != null ? cv.trim() : null);
    p.setImgRights(rs.getBoolean(6));
    p.setPartnerInfo(rs.getBoolean(7));
    String nickname = rs.getString(8);
    p.setNickName(nickname != null ? unEscape(nickname.trim()) : null);
    int org = rs.getInt(9);
    if (org > 0) {
      Organization o = new Organization(rs.getInt(9));
      o.setName(rs.getString(10));
      o.setCompanyName(rs.getString(11));
      p.setOrganization(o);
    }

    return p;
  }

  public static Vector<Person> find(String where, DataConnection dc) {
    Vector<Person> v = new Vector<Person>();
    String query = PRE_QUERY + where + " ORDER BY p.nom";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        v.addElement(getFromRS(rs));
      }
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  public static int count(String where, DataConnection dc) {
    int cpt = 0;
    String query = "SELECT count(id) FROM " + TABLE + " " + where;
    try (ResultSet rs = dc.executeQuery(query)) {
      if (rs.next()) {
        cpt = rs.getInt(1);
      }
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return cpt;
  }

  @Override
  /**
   * @deprecated
   */
  public List<Person> load() throws SQLException {
    List<Person> lp = new ArrayList<Person>();
    String query = "SELECT " + COLUMNS + " FROM  " + VIEW + " p"
      + " WHERE p.id IN (SELECT debiteur FROM " + InvoiceIO.TABLE + ")"
      + " OR p.id IN (SELECT adherent FROM " + InvoiceIO.TABLE + ")";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        lp.add(getFromRS(rs));
      }
    }
    return lp;
  }

  public List<Integer> getPersonsIdsForAction(int idAction) throws SQLException {
    String query = format(
      "SELECT DISTINCT p.id, p.nom, p.prenom FROM " + PersonIO.TABLE + " p\n"
      + "JOIN " + OrderIO.TABLE + " c ON c.adh = p.id\n"
      + "JOIN " + CourseOrderIO.TABLE + " cc ON cc.idcmd = c.id\n"
      + "JOIN " + ActionIO.TABLE + " a ON cc.idaction = a.id\n"
      + "WHERE a.id = %d\n"
      + "ORDER BY p.prenom, p.nom ", idAction
    );

    List<Integer> result = new ArrayList<>();
    ResultSet resultSet = dc.executeQuery(query);
    while (resultSet.next()) {
      result.add(resultSet.getInt(1));
    }
    return result;
  }

  public List<Person> getPersonsForAction(Action action) throws Exception {
    List<Integer> ids = getPersonsIdsForAction(action.getId());
    List<Person> result = new ArrayList<>(ids.size());
    for (Integer id : ids) {
      result.add(PersonIO.this.findById(id));
    }
    return result;
  }

}
