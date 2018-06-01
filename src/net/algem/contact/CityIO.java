/*
 * @(#)CityIO.java	2.15.8 21/03/18
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
 *
 */
package net.algem.contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * DAO methods for class {@link net.algem.contact.City}
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 */
public class CityIO
  extends TableIO {

  public final static String TABLE = "ville";

  public static void insert(City v, DataConnection dc) throws SQLException {

    String query = "INSERT INTO " + TABLE + " VALUES(?,?)";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, v.getCdp());
      ps.setString(2, v.getCity().toUpperCase());
      ps.executeUpdate();
    }
  }

  public static void update(City v, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET nom = ? WHERE cdp = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, v.getCity());
      ps.setString(2, v.getCdp());
      ps.executeUpdate();
    }
  }

  public static void delete(City v, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE cdp = ? AND nom = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, v.getCdp());
      ps.setString(2, v.getCity());
      ps.executeUpdate();
    }
  }

  public static List<City> findCity(String cdp, DataConnection dc) {
    String query = "WHERE cdp = '" + cdp + "'";
    return find(query, dc);
  }

  public static List<City> find(String where, DataConnection dc) {
    List<City> cities = new ArrayList<>();
    String query = "SELECT cdp, nom FROM " + TABLE + " " + where + " ORDER BY cdp";

    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        City n = new City();
        n.setCdp(rs.getString(1));
        n.setCity(unEscape(rs.getString(2).trim()));

        cities.add(n);
      }
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return cities;
  }
}
