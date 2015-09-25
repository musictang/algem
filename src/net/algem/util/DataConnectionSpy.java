/*
 * @(#)DataConnectionSpy.java	2.9.4.12 24/09/15
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
package net.algem.util;

/**
 *
 * @author eric
 * @since 2.9.4.12
 */
public class DataConnectionSpy
        extends DataConnection
{

  public DataConnectionSpy(String host, int port, String dbname, String dbpass) {
    super(host, port, dbname, dbpass);

    try {
      Class.forName("net.sf.log4jdbc.DriverSpy");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @Override
  public String getUrl() {
    return "jdbc:log4jdbc:postgresql" + "://" + getDbhost() + ":" + getDbport() + "/" + getDbname();
  }
}
