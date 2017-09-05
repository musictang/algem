/*
 * @(#)TestProperties.java 2.9.4.14 15/12/15
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

package net.algem;

import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;

/**
 * Default values for database connection.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.5.a 28/06/12
 */
public class TestProperties
{
   /** Test database. */
  public final static String BASE = "mustang";
  /** SGBD server. */
  public final static String HOST = "localhost";
	/** Test database port. */
  public final static int PORT = 5442;
  /** User with profile 4. */
  public final static String ADMIN = System.getProperty("user.name");
   /** User with profile 1. */
  public final static String USER = "nobody"; // a modifier suivant le contexte
	 /** Pass. */
  public final static String PASS = "Pigfy!"; // a modifier suivant le contexte
  /** JDBC driver. */
  public final static String DRIVER = "org.postgresql.Driver";

  public static DataCache getDataCache(DataConnection dc) throws Exception {
    return DataCache.getInstance(dc, USER);
  }

  public static DataConnection getDataConnection() {
    DataConnection dc = new DataConnection(HOST, PORT, BASE, PASS);
    try {
      dc.connect();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return dc;
  }

  /**
   * List system properties. For info only.
   */
//  @Ignore
  public void listSystemProperties() {
    java.util.Properties props = System.getProperties();
    props.list(System.out);
  }

}
