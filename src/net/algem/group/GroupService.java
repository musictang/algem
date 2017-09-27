/*
 * @(#)GroupService.java	2.15.2 27/09/17
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

package net.algem.group;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 2.8.p 08/11/13
 */
public interface GroupService {

  void create(Group g) throws SQLException;

  void create(int g, Musician m) throws SQLException;

  void delete(Group g) throws GroupException;

  Group find(int id) throws SQLException;

  List<Group> find(String where) throws SQLException;

  List<Group> findAll(String order) throws SQLException;

  List<Group> getGroups(int idper) throws SQLException;

  List<Musician> getMusicians(Group g) throws SQLException;

  List<Musician> getMusicians(int idper) throws SQLException;

  void update(Group old, Group g) throws SQLException;

  void update(int g, List<Musician> vm, List<Musician> om) throws GroupException;

}
