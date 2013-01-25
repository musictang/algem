/*
 * @(#)UserService.java	2.7.a 09/01/13
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
package net.algem.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.postit.Postit;
import net.algem.util.postit.PostitIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.6.a 06/08/2012
 */
public class UserService
{

  private DataConnection dc;
  private DataCache dataCache;
  private UserIO dao;

  public UserService(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = dataCache.getDataConnection();
    dao = (UserIO) DataCache.getDao(Model.User);
  }

  public Vector<User> findAll(String where) {
    Vector<User> v = new Vector<User>();
    try {
      v = dao.find(where);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return v;
  }

  public User find(String login) {
    try {
      return dao.findLogin(login);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }

  public User findId(int id) {
    try {
      return (User) DataCache.findId(id, Model.User);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }

  public void update(User user) throws SQLException {
    dao.update(user);
    dataCache.update(user);
  }

  Vector<MenuAccess> getMenuAccess(int userId) {
    Vector<MenuAccess> mv = new Vector<MenuAccess>();
    String query = "SELECT idmenu,label,autorisation FROM menu2, menuaccess WHERE menu2.id = menuaccess.idmenu AND idper = " + userId + " ORDER BY idmenu";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        String label = BundleUtil.getLabel(rs.getString(2));
        MenuAccess m = new MenuAccess(rs.getInt(1), label, rs.getBoolean(3));
        mv.addElement(m);
      }
      rs.close();
    } catch (SQLException ex) {
      GemLogger.logException(query, ex);
    }
    return mv;
  }

  void updateAccess(MenuAccess m, String col, int userId) {
    try {
      String query = "UPDATE menuaccess SET " + col + " = '" + (m.isAuth() ? "t" : "f")
              + "' WHERE idper = " + userId + " AND idmenu = " + m.getId();
      dc.executeUpdate(query);
    } catch (SQLException ex) {
      GemLogger.logException("Update menuaccess", ex);
    }
  }

  Vector getTableRights(int userId) {
    Vector vr = new Vector();
    String query = "SELECT nomtable,lecture,insertion,modification,suppression FROM droits WHERE idper=" + userId + " ORDER BY nomtable";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Vector v = new Vector();
        for (int i = 1; i <= 5; i++) {
          v.addElement(rs.getObject(i));
        }
        vr.addElement(v);
      }
      rs.close();
    } catch (SQLException ex) {
      GemLogger.logException(query, ex);
    }
    return vr;
  }

  void updateTableRights(String table, String col, Object value, int userId) {
    String query = "UPDATE droits SET " + col + " = " + (((Boolean) value).booleanValue() ? "1" : "0")
            + " WHERE idper = " + userId + " AND nomtable = '" + table + "'";
    try {
      dc.executeUpdate(query);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  public Vector<User> getRegisteredUsers() {
    Vector<User> v = new Vector<User>();
    try {
      v = dao.find(null);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return v;
  }

  public void create(Postit p) throws SQLException {
    PostitIO.insert(p, dc);
  }

  public void update(Postit p) throws SQLException {
    PostitIO.update(p, dc);
  }

  public void delete(Postit p) throws SQLException {
    PostitIO.delete(p, dc);
  }

  public Vector<Postit> getPostits(int idUser, int read) {
    String where = "WHERE (dest = 0 OR dest = " + idUser + ") AND id > " + read;
    return PostitIO.find(where, dc);
  }

  public boolean authorize(String menu2, User user) {
    String query = "SELECT autorisation FROM menuaccess a, menu2 m"
            + " WHERE m.label = '" + menu2 + "'"
            + " AND a.idper = " + user.getId()
            + " AND a.idmenu = m.id";
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        return (rs.getBoolean(1));
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return true; // autorisé par défaut
  }
}
