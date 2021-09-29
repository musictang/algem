/*
 * @(#)UserIO.java 2.17.2 27/10/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.security;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.room.EstablishmentIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;
import org.apache.commons.codec.binary.Base64;

/**
 * IO methods for class {@link net.algem.security.User}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.17.0
 * @since 1.0a 07/07/1999
 */
public class UserIO
  extends TableIO
  implements Cacheable {

  static final String TABLE = "login";
  static final String T_MENU = "menu2";
  static final String T_PROFILE = "menuprofil";
  static final String T_ACCESS = "menuaccess";
  static final String T_RIGHTS = "droits";
  private DataConnection dc;

  public UserIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(User u) throws SQLException {

    String query = "INSERT INTO " + TABLE + " (idper,login,profil,pass,clef) VALUES(?,?,?,?,?)";
    PreparedStatement statement = dc.prepareStatement(query);
    statement.setInt(1, u.getId());
    statement.setString(2, u.getLogin());
    statement.setInt(3, u.getProfile());
    String b64pass = Base64.encodeBase64String(u.getPassInfo().getPass());
    statement.setString(4, b64pass);
    String b64salt = Base64.encodeBase64String(u.getPassInfo().getKey());
    statement.setString(5, b64salt);
    statement.executeUpdate();
    statement.close();

  }

  public void update(User u) throws SQLException {
    String query = "UPDATE " + TABLE + " SET login = ?, profil = ?, pass = ?, clef = ? WHERE idper = " + u.getId();
    UserPass pass = u.getPassInfo();
    String b64pass = null;
    String b64salt = null;
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, u.getLogin());
      ps.setInt(2, u.getProfile());

      if (pass != null) {
        b64pass = Base64.encodeBase64String(pass.getPass());
        b64salt = Base64.encodeBase64String(pass.getKey());
      }

      ps.setString(3, pass == null ? null : b64pass);
      ps.setString(4, pass == null ? null : b64salt);
      ps.executeUpdate();
    }

  }

  public void delete(final int userId) throws UserException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {

        @Override
        public Void run(DataConnection conn) throws Exception {
          String query = "DELETE FROM " + TABLE + " WHERE idper = " + userId;
          dc.executeUpdate(query);
          query = "DELETE FROM " + T_ACCESS + " WHERE idper = " + userId;
          dc.executeUpdate(query);
          query = "DELETE FROM " + EstablishmentIO.TABLE + " WHERE idper = " + userId;
          dc.executeUpdate(query);
          return null;
        }
      });
    } catch (Exception ex) {
      throw new UserException(ex.getMessage(), "SUPPRESSION");
    }

  }

  /**
   * Menus access initialization.
   *
   * @param u user
   * @throws java.sql.SQLException
   */
  public void initMenus(User u) throws SQLException {
    String query = "SELECT id, auth FROM " + T_MENU + ", " + T_PROFILE + " WHERE id = idmenu AND profil = " + u.getProfile();
    String batchQuery = "INSERT INTO " + T_ACCESS + " VALUES(?,?,?)";//idper,idmenu,auth

    try (PreparedStatement ps = dc.prepareStatement(batchQuery); ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        int id = rs.getInt(1);
        boolean b = rs.getBoolean(2);
        ps.setInt(1, u.getId());
        ps.setInt(2, id);
        ps.setBoolean(3, b);
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }

  /**
   * Rights initialization.
   *
   * @param u user
   * @throws java.sql.SQLException
   */
  public void initRights(User u) throws SQLException {
    String query = "SELECT relname FROM pg_class WHERE relkind = 'r' AND relname !~ '^pg' AND relname !~ '^sql_' AND relname !~ '^Inv'";
    String batchQuery = "INSERT INTO " + T_RIGHTS + " VALUES(?,?,?,?,?,?)";

    try (PreparedStatement ps = dc.prepareStatement(batchQuery); ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        String table = rs.getString(1);
        ps.setInt(1, u.getId());
        ps.setString(2, table);
        ps.setBoolean(3, true);
        ps.setBoolean(4, false);
        ps.setBoolean(5, false);
        ps.setBoolean(6, false);
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }

  /**
   * Establishment active status initialization.
   *
   * @param idper user id
   * @throws SQLException
   */
  public void initEstabStatus(int idper) throws SQLException {
    String query = "INSERT INTO " + EstablishmentIO.TABLE + " SELECT p.id," + idper + ",true FROM personne p WHERE p.ptype = " + Person.ESTABLISHMENT;
    dc.executeUpdate(query);
  }

  public User findId(int n) throws SQLException {
    String query = "WHERE idper = " + n;
    List<User> v = find(query);

    return v.isEmpty() ? null : v.get(0);
  }

  public User findLogin(String login) throws SQLException {
    String query = "WHERE login = '" + login + "'";
    List<User> v = find(query);
    if (v.size() > 0) {
      return v.get(0);
    }
    return null;
  }

  public List<User> find(String where) throws SQLException {
    List<User> v = new ArrayList<User>();
    String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,u.login,u.profil,u.pass,u.clef,u.desktop"
      + " FROM " + PersonIO.TABLE + " p JOIN " + TABLE + " u ON (p.id = u.idper)";
    if (where != null) {
      query += " " + where;
    }
    query += " ORDER BY p.nom, p.prenom";
    ResultSet rs = dc.executeQuery(query);

    while (rs.next()) {
      User u = new User();
      u.setId(rs.getInt(1));
      u.setType(rs.getShort(2));
      String n = rs.getString(3);
      String f = rs.getString(4);
      String g = rs.getString(5);
      String l = rs.getString(6);
      u.setName(n == null ? null : n.trim());
      u.setFirstName(f == null ? null : f.trim());
      u.setGender(g == null ? null : g.trim());
      u.setLogin(l == null ? null : l.trim());
      u.setProfile(rs.getInt(7));
      u.setDesktop(rs.getInt(10));

      byte[] b64pass = Base64.decodeBase64(rs.getString(8));
      byte[] b64salt = Base64.decodeBase64(rs.getString(9));

      u.setPassInfo(new UserPass(b64pass, b64salt));
      v.add(u);
    }
    rs.close();
    return v;
  }

  public List<User> findPostitUserList() throws SQLException {
    String order = ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey());
    List<User> v = new ArrayList<User>();
    String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.pseudo,p.civilite,u.login,u.profil"
      + " FROM " + PersonIO.TABLE + " p JOIN " + TABLE + " u ON (p.id = u.idper)"
      + ("n".equals(order) ? " ORDER BY p.nom, p.prenom" : " ORDER BY p.prenom, p.nom");
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        User u = new User();
        u.setId(rs.getInt(1));
        u.setType(rs.getShort(2));
        String n = rs.getString(3);
        String f = rs.getString(4);
        String p = rs.getString(5);
        String g = rs.getString(6);
        String l = rs.getString(7);
        u.setName(n == null ? null : n.trim());
        u.setFirstName(f == null ? null : f.trim());
        u.setNickName(p == null ? null : p.trim());
        u.setGender(g == null ? null : g.trim());
        u.setLogin(l == null ? null : l.trim());
        u.setProfile(rs.getInt(8));
        v.add(u);
      }
    } catch (SQLException ex) {
      throw ex;
    }
    return v;
  }

  @Override
  public List<User> load() throws SQLException {
    return find(null);
  }

  public HashMap<String, HashMap> loadAuthorizations() {
    HashMap<String, HashMap> authorizations = new HashMap<>();

    String query = "SELECT idper, label, autorisation FROM " + T_ACCESS + " a JOIN " + T_MENU + " m ON a.idmenu = m.id";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        if (authorizations.get(rs.getString(2)) == null) {
          HashMap<Integer, Boolean> d = new HashMap<>();
          d.put(rs.getInt(1), rs.getBoolean(3));
          authorizations.put(rs.getString(2), d);
        } else {
          HashMap d = authorizations.get(rs.getString(2));
          d.put(rs.getInt(1), rs.getBoolean(3));
        }
      }
    } catch (SQLException e) {
      GemLogger.logException("DataCache.loadAuhorizations:" + query, e);
    }
    return authorizations;
  }
}
