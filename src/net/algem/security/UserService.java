/*
 * @(#)UserService.java	2.13.2 03/05/17
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
package net.algem.security;

import java.sql.SQLException;
import java.util.List;
import net.algem.util.postit.Postit;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
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
  boolean authenticate(String login, String pass);

  /**
   * Authenticates a user with some {@code pass}.
   *
   * @param user user instance
   * @param pass clear text password
   * @return true if clearPass encryption equals stored encrypted pass
   */
  boolean authenticate(User user, String pass);

  boolean authorize(String menu2, User user);

  void create(User u) throws UserException;

  User findId(int id);

  User find(String login);

  List<User> findAll(String where);

  void update(User user) throws UserException;

  boolean update(User nu, final User old) throws UserException;

  void delete(User user) throws UserException;

  List<User> getPostitUserList();

  void create(Postit p) throws SQLException;

  void update(Postit p) throws SQLException;

  void delete(Postit p) throws SQLException;

  List<Postit> getPostits(int idUser, int read);

  Postit getBookingAlert();

  void updateTableRights(String table, String col, boolean value, int userId);

  List<SQLRights> getTableRights(int userId);
}
