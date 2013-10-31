/*
 * @(#)UserIO.java 2.8.p 30/10/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.security.User}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.p
 * @since 1.0a 07/07/1999
 */
public class UserIO
        extends TableIO
        implements Cacheable
{

  public static int PROFIL_BASIC = 0; // BMS
  public static int PROFIL_USER = 1; // MUSTANG
  public static int PROFIL_PROF = 2; // PROF
  public static int PROFIL_PUBLIC = 3; // PUBLIC
  public static int PROFIL_ADMIN = 4; // ADMIN
  public static String[] PROFIL_NAMES = {"Basique", "Utilisateur", "Professeur", "Public", "Administrateur"};
  static final String TABLE = "login";
  
  private DataConnection dc;

  public UserIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(User u) throws SQLException {
		
		/*String query = "INSERT INTO " + TABLE + " (idper,login,profil,pass,clef) VALUES("
			+ u.getId()
			+ ",'" + u.getLogin()
			//+ "','" + u.getPassword()
			+ "'," + u.getProfile()
			+ "," + u.getPassInfo().getPassInfo()
			+ "," + u.getPassInfo().getKey()
			+ ")";*/
		String query = "INSERT INTO " + TABLE + " (idper,login,profil,pass,clef) VALUES(?,?,?,?,?)";
		PreparedStatement statement = dc.prepareStatement(query);
		statement.setInt(1, u.getId());
		statement.setString(2, u.getLogin());
		statement.setInt(3, u.getProfile());
		statement.setBytes(4, u.getPassInfo().getPass());
		statement.setBytes(5, u.getPassInfo().getKey());
		statement.executeUpdate();
		statement.close();
//		dc.executeUpdate(query);
	}

  public void update(User u) throws SQLException {
    /*String query = "UPDATE " + TABLE + " SET"
            + " login = '" + u.getLogin()
            //+ "',password = '" + u.getPassword()
            + "',profil = " + u.getProfile();*/
		String query = "UPDATE " + TABLE + " SET login = ?, profil = ?";
		UserPass pass = u.getPassInfo();
		if (pass != null) {
			if (pass.getPass() != null && pass.getKey() != null) {
				query += ",pass = ?,clef = ?";
			}
		}
    query += " WHERE idper = " + u.getId();

		PreparedStatement statement = dc.prepareStatement(query);
		statement.setString(1, u.getLogin());
		statement.setInt(2, u.getProfile());
		statement.setBytes(3, pass == null ? null : u.getPassInfo().getPass());
		statement.setBytes(4, pass == null ? null : u.getPassInfo().getKey());
		statement.executeUpdate();
		statement.close();
//    dc.executeUpdate(query);
  }

  public void delete(User u) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + u.getId();
    dc.executeUpdate(query);
  }

  public void initRights(User u) {
    String query = "SELECT relname FROM pg_class WHERE relkind = 'r' AND relname !~ '^pg' AND relname !~ '^sql_' AND relname !~ '^Inv'";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        String table = rs.getString(1);
        String query2 = "INSERT INTO droits VALUES(" + u.getId() + ",'" + table + "','t','f','f','f')";
        dc.executeUpdate(query2);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
  }

  public void initMenus(User u) {
    String query = "SELECT id,auth FROM menu2,menuprofil WHERE id=idmenu AND profil=" + u.getProfile();
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        int id = rs.getInt(1);
        boolean b = rs.getBoolean(2);
        String query2 = "INSERT INTO menuaccess VALUES(" + u.getId() + "," + id + "," + (b ? "'t'" : "'f'") + ")";
        dc.executeUpdate(query2);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
  }

  public User findId(int n) throws SQLException {
    String query = "WHERE idper = " + n;
    List<User> v = find(query);
//    if (v.size() > 0) {
//      return v.get(0);
//    }
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
    List<User> v = new Vector<User>();
    String query = "SELECT p.id,p.ptype,p.nom,p.prenom,p.civilite,u.login,u.profil,u.pass,u.clef FROM personne p, " + TABLE + " u ";
    if (where != null) {
      query += where + " AND p.id = u.idper";
    } else {
      query += " WHERE p.id = u.idper";
    }
    query += " ORDER BY p.nom, p.prenom";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      User u = new User();
      u.setId(rs.getInt(1));
      u.setType(rs.getShort(2));
      u.setName(rs.getString(3).trim());
      u.setFirstName(rs.getString(4).trim());
      u.setGender(rs.getString(5).trim());
      u.setLogin(rs.getString(6).trim());
      //u.setPassword(rs.getString(7).trim());
      u.setProfile(rs.getInt(7));
			u.setPassInfo(new UserPass(rs.getBytes(8), rs.getBytes(9)));

      v.add(u);
    }
    rs.close();
    return v;
  }

  @Override
  public List<User> load() throws SQLException {
    return find(null);
  }
  
}
