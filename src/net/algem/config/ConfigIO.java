/*
 * @(#)ConfigIO.java 2.9.1 12/11/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.1.k
 */
public class ConfigIO
        extends TableIO
{

  public final static String TABLE = "config";
  public final static String COLUMNS = "clef,valeur";

  private final static String KEY = "clef";
  private final static String VAL = "valeur";
  private static final Map<String, Config> cache = new HashMap<String, Config>();

  /**
   *
   * @param where
   * @param dc
   * @return a map
   * @throws SQLException
   */
  public static Map<String, Config> find(String where, DataConnection dc) throws SQLException {
    String query = getSelectQuery(where);
    HashMap<String, Config> confs = new HashMap<String, Config>();
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        String k = rs.getString(1).trim();
        String v = TableIO.unEscape(rs.getString(2).trim());
        Config c = new Config(k, v);
        confs.put(k, c);
        cache.put(k, new Config(k, v));
      }
    }
    return confs.isEmpty() ? null : confs;
  }

  public static Config findId(String key, DataConnection dc) throws SQLException {

    Config cfg = cache.get(key);
    if (cfg != null) {
      return cfg;
    }
    String query = getSelectQuery("WHERE " + KEY + " = '" + key + "'");
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        String k = rs.getString(1).trim();
        String v = TableIO.unEscape(rs.getString(2).trim());
        cfg = new Config(key, v);
        cache.put(k, new Config(key, v));
      }
    }
    return cfg;
  }

  /**
   *
   * @param config
   * @param dc
   * @throws SQLException
   */
  public static void update(Map<String, Config> config, DataConnection dc) throws SQLException {
    for (Config c : config.values()) {
      Config cached = cache.get(c.getKey());
      if (!c.equals(cached)) {
        update(c, dc);
        cache.put(c.getKey(), new Config(c.getKey(), c.getValue()));
      }
    }
  }

  /**
   *
   * @param conf
   * @param dc
   * @throws SQLException
   */
  private static void update(Config conf, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET " + VAL + " = '" + escape(conf.getValue()) + "' WHERE " + KEY + " = '" + conf.getKey() + "'";
    dc.executeUpdate(query);
  }

  /**
   *
   * @param where
   * @return a request string
   */
  private static String getSelectQuery(String where) {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE;
    if (where != null) {
      query += " " + where;
    }
    return query;
  }
}
