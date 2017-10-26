/*
 * @(#)DataConnection.java	2.15.4 18/10/17
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
package net.algem.util;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Utility class for database connection.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.4
 * @since 2.6.a 01/08/2012
 */
public class DataConnection
{

  public static final int DEFAULT_DB_PORT = 5432;

  static final String DEFAULT_DB_USER = "nobody";

  /**
   * Default database pass.
   * Users must change it in Postgre database (ex. ALTER USER nobody ENCRYPTED PASSWORD 'mypass').
   * Set auth-method to password or md5 in pg_hba.conf
   */
  private static final String DEFAULT_DB_PASS = "Pigfy!"; // PigG8fy!
  private static final String DEFAULT_DB_NAME = "algem";
  private static final String DEFAULT_HOST = "localhost";
  private final String DEFAULT_DRIVER_NAME = "org.postgresql.Driver";
  private static final String DRIVER_URL = "jdbc:postgresql";
  private Connection cnx;
  private String dbhost;
  private int dbport;
  private String dbname;
  private boolean connected;
  private boolean debugSQL;
  private boolean ssl = false;
  private boolean cacert = false;
  private String dbpass;

  public DataConnection(String host, int port, String dbname, String dbpass) {
    this.dbhost = (host == null || host.isEmpty()) ? DEFAULT_HOST : host;
    this.dbport = port == 0 ? DEFAULT_DB_PORT : port;
    this.dbname = (dbname == null || dbname.isEmpty()) ? DEFAULT_DB_NAME : dbname;
    this.dbpass = (dbpass == null || dbpass.isEmpty()) ? DEFAULT_DB_PASS : dbpass;
  }

  /**
   * Creates an instance with default host, port and base.
   */
  public DataConnection() {
    this(DEFAULT_HOST, DEFAULT_DB_PORT, DEFAULT_DB_NAME, DEFAULT_DB_PASS);
  }

  /**
   * Creates an instance with default port, base and password.
   * @param host
   */
  public DataConnection(String host) {
    this(host, DEFAULT_DB_PORT, DEFAULT_DB_NAME, DEFAULT_DB_PASS);
  }

  /**
   * Creates an instance with default port.
   * @param host
   * @param dbname
   */
  public DataConnection(String host, String dbname) {
    this(host, DEFAULT_DB_PORT, dbname, DEFAULT_DB_PASS);
  }

  boolean isSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  boolean isCacert() {
    return cacert;
  }

  public void setCacert(boolean cacert) {
    this.cacert = cacert;
  }

  /**
   *
   * @param h host
   * @param p port
   * @param b base
   * @return true if connected
   * @throws SQLException
   */
  public boolean connect(String h, int p, String b) throws SQLException {
    dbhost = h;
    dbport = p;
    dbname = b;

    return connect();
  }

  public boolean connect() throws SQLException {
    if (cnx != null) {
      close();
    }
    cnx = DriverManager.getConnection(getUrl(), getConnectionProperties());
    connected = true;

    return connected;
  }

  public Properties getConnectionProperties() {
    Properties props = new Properties();
    props.setProperty("user", DEFAULT_DB_USER);
    props.setProperty("password", dbpass);
    if (ssl) {
      props.setProperty("ssl", "true");
      // by default, jdbc requests certificat
      // to install certificate on client :
      // server.crt.der is on postgresql cluster
      // keytool -keystore /path/to/java/lib/security/cacerts -alias <myalias> -import -file /path/to/server.crt.der
      if (!cacert) { // for demo usage
        props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
      }
    }
    return props;
  }

  public String getUrl() {
    return DRIVER_URL + "://" + dbhost + ":" + dbport + "/" + dbname;
  }

  public String getDbhost() {
    return dbhost;
  }

  public String getDbname() {
    return dbname;
  }

  public int getDbport() {
    return dbport;
  }

  public boolean isConnected() {
    try {
      return cnx != null && !cnx.isClosed() && connected;
    } catch (SQLException ex) {
      GemLogger.logException("connection error ", ex);
    }
    return false;
  }

  public PreparedStatement prepareStatement(String query) {
    PreparedStatement pstmt = null;
    try {
      pstmt = cnx.prepareStatement(query);
    } catch (Exception other) {
      GemLogger.logException("Exception preparedStatement", other);
      try {
        connect();
        pstmt = cnx.prepareStatement(query);
      } catch (SQLException sqe) {
        GemLogger.logException("Reconnection error ", sqe);
      }
    }
    return pstmt;
  }

  Statement createStatement() {
    Statement stmt = null;
    try {
      stmt = cnx.createStatement();
    } catch (Exception other) {
      GemLogger.logException("Exception createStatement", other);
      try {
        connect();
        stmt = cnx.createStatement();
      } catch (SQLException sqe) {
        GemLogger.logException("Reconnection error ", sqe);
      }
    }
    return stmt;
  }

  private Statement createStatement(int resultSetType, int resultSetConcurrency) {
    Statement stmt = null;
    try {
      stmt = cnx.createStatement(resultSetType, resultSetConcurrency);
    } catch (Exception other) {
      GemLogger.logException("Exception createStatement", other);
      try {
        connect();
        stmt = cnx.createStatement(resultSetType, resultSetConcurrency);
      } catch (SQLException sqe) {
        GemLogger.logException("Reconnection error ", sqe);
      }
    }
    return stmt;
  }

  /**
   * Gets a resultset.
   *
   * @param query
   * @return a resultSet
   * @throws SQLException
   */
  public ResultSet executeQuery(String query) throws SQLException {
    if (debugSQL) {
      GemLogger.log(Level.INFO, query);
    }
    Statement stmt = createStatement();
    return stmt.executeQuery(query);
  }

  public ResultSet executeQuery(String query, int resultSetType, int resultSetConcurrency) throws SQLException {
    if (debugSQL) {
      GemLogger.info(query);
    }
    Statement stmt = createStatement(resultSetType, resultSetConcurrency);
    return stmt.executeQuery(query);
  }

  public int executeUpdate(String query) throws SQLException {
    int rc = 0; // row count
    GemLogger.info(query);

    try (Statement stmt = createStatement()) {
      rc = stmt.executeUpdate(query);
    }

    return rc;
  }

  public void commit() throws SQLException {
    if (!cnx.getAutoCommit()) {
      cnx.commit();
      GemLogger.log(Level.INFO, "commit");
    }
  }

  public void rollback() {
    try {
      cnx.rollback();
      GemLogger.log("rollback");
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  public void setAutoCommit(boolean b) {
    try {
      cnx.setAutoCommit(b);
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  public void close() {
    if (cnx != null) {
      try {
        cnx.close();
      } catch (SQLException e) {
      }

      cnx = null;
      connected = false;
    }
  }

  public interface SQLRunnable<T> {
    public T run(DataConnection conn) throws Exception;
  }

  public static abstract class SQLProc implements SQLRunnable<Void> {
    protected abstract void run() throws Exception;

    @Override
    public Void run(DataConnection conn) throws Exception {
      run();
      return null;
    }
  }

  public <T> T withTransaction(SQLRunnable<T> block) throws Exception {
    boolean inTransaction = !cnx.getAutoCommit();
    if (inTransaction) {
      return block.run(this);
    } else {
      try {
        setAutoCommit(false);
        return block.run(this);
      } catch (Exception e) {
        rollback();
        throw e;
      } finally {
        setAutoCommit(true);
      }
    }
  }

  public Array createArray(String type, Object[] o) throws SQLException {
    if (cnx != null) {
      return cnx.createArrayOf(type, o);
    }
    return null;
  }
}
