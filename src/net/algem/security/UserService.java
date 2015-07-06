/*
 * @(#)UserService.java	2.9.4.9 06/07/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.util.postit.Postit;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.9
 * @since 2.8.p 30/10/13
 */
public interface UserService
{

  /**
   * Authenticates a user by his login and pass.
   *
   * @param login login name
   * @param pass clear text password
   * @return true if pass encryption equals stored encrypted pass
   */
  public boolean authenticate(String login, String pass);

  /**
   * Authenticates a user with some {@code pass}.
   *
   * @param user user instance
   * @param pass clear text password
   * @return true if clearPass encryption equals stored encrypted pass
   */
  public boolean authenticate(User user, String pass);

  public boolean authorize(String menu2, User user);

  public void create(User u) throws SQLException;

  public User findId(int id);

  public User find(String login);

  public List<User> findAll(String where);

  public void update(User user) throws SQLException;

  public boolean update(User nu, final User old) throws SQLException, UserException;
  
  public void delete(int userId) throws UserException;

  public List<User> getRegisteredUsers();

  public void create(Postit p) throws SQLException;

  public void update(Postit p) throws SQLException;

  public void delete(Postit p) throws SQLException;

  public Vector<Postit> getPostits(int idUser, int read);

  public void updateTableRights(String table, String col, Object value, int userId);

  public Vector getTableRights(int userId);
}
