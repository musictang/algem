/*
 * @(#)UserDao.java	1.0.0 11/02/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.algem.contact.PersonIO;
import net.algem.util.AbstractGemDao;
import org.apache.commons.codec.binary.Base64;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.0
 * @since 1.0.0 11/02/13
 */
@Repository
public class UserDao
  extends AbstractGemDao {

  public static final String TABLE = "login";

  public UserDao() {
  }

  public List<User> findAll() {
    String query = "SELECT * FROM login";
    return jdbcTemplate.query(query, new RowMapper<User>() {

      public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getFromRS(rs);
      }
    });
  }

  private User getFromRS(ResultSet rs) throws SQLException {
    User u = new User();
    u.setId(rs.getInt(1));
    u.setLogin(rs.getString(2));
    u.setProfile(getProfileFromId(rs.getShort(3)));
    u.setPass(getUserPass(rs.getString(4), rs.getString((5))));
    u.setName(rs.getString(6) + " " + rs.getString(7));

    return u;
  }

  private Profile getProfileFromId(int id) {
    switch(id) {

      case 1: return Profile.User;
      case 2: return Profile.Teacher;
      case 3: return Profile.Public;
      case 4: return Profile.Admin;
      case 10: return Profile.Visitor;
      default: return Profile.Visitor;
    }
  }

  /**
   * Gets the encrypted password.
   *
   * @param salt base64-encoded salt
   * @param pass base64-encoded pass
   * @return user pass info
   */
  private UserPass getUserPass(String pass, String salt) {

    byte[] b64pass = Base64.decodeBase64(pass);
    byte[] b64salt = Base64.decodeBase64(salt);

    return new UserPass(b64pass, b64salt);
  }

  public User find(String login) {
    int id = getIdFromLogin(login);
    String query = "SELECT l.*,p.prenom,p.nom,p.pseudo FROM login l INNER JOIN " + PersonIO.TABLE
      + " p ON (l.idper = p.id) WHERE l.idper = ? OR l.login = ?";
    return jdbcTemplate.queryForObject(query, new RowMapper<User>() {
      public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getFromRS(rs);
      }
    }, id, login);

  }

    private int getIdFromLogin(String login) {
      try {
        return Integer.parseInt(login);
      } catch(NumberFormatException nfe) {
        return -1;
      }
    }

  byte[] findAuthInfo(String login, String col) {
    int id = getIdFromLogin(login);
    String query = "SELECT " + col + " FROM " + TABLE + " WHERE idper = ? OR login = ?";
    String result = jdbcTemplate.queryForObject(query, String.class, new Object[]{id, login});
    return Base64.decodeBase64(result);
  }

  public User findById(int id) {
    String query = "SELECT * FROM login WHERE idper = ?";
    List<User> users = jdbcTemplate.query(query, new RowMapper<User>() {

      public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getFromRS(rs);
      }
    }, id);
    return users.isEmpty() ? null : users.get(0);
  }

  public List<Map<String, Boolean>> listMenuAccess(int userId) {
    String query = "SELECT m.label, a.autorisation FROM  menu2 m JOIN menuaccess a ON m.id = a.idmenu WHERE a.idper = ?";
    return jdbcTemplate.query(query, new RowMapper<Map<String, Boolean>>() {

      public Map<String, Boolean> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put(rs.getString(1), rs.getBoolean(2));
        return map;
      }
    }, userId);

  }
}
