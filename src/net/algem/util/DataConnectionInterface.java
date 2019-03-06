/*
 * @(#) DataConnectionInterface.java Algem 2.15.11 27/11/2018
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

package net.algem.util;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import net.algem.util.DataConnection.SQLRunnable;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.11
 * @since 2.15.11 27/11/2018
 */
public interface DataConnectionInterface {

  void close();

  void commit() throws SQLException;

  /**
   *
   * @param h host
   * @param p port
   * @param b base
   * @return true if connected
   * @throws SQLException
   */
  boolean connect(String h, int p, String b) throws SQLException;

  boolean connect() throws SQLException;

  Array createArray(String type, Object[] o) throws SQLException;

  /**
   * Gets a resultset.
   *
   * @param query
   * @return a resultSet
   * @throws SQLException
   */
  ResultSet executeQuery(String query) throws SQLException;

  ResultSet executeQuery(String query, int resultSetType, int resultSetConcurrency) throws SQLException;

  int executeUpdate(String query) throws SQLException;

  Properties getConnectionProperties();

  String getDbhost();

  String getDbname();

  int getDbport();

  String getUrl();

  boolean isConnected();

  PreparedStatement prepareStatement(String query);

  void rollback();

  void setAutoCommit(boolean b);

  void setCacert(boolean cacert);

  void setSsl(boolean ssl);

  <T> T withTransaction(SQLRunnable<T> block) throws Exception;

}
