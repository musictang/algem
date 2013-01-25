/*
 * @(#)ConfigIO.java 2.6.a 01/08/2012
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
 * @version 2.6.a
 * @since 2.1.k
 */
public class ConfigIO
	extends TableIO {

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

		ResultSet rs = dc.executeQuery(query);
		while (rs.next()) {
			Config c = new Config(rs.getString(1).trim(), rs.getString(2).trim());
			cache.put(c.getKey(), c);
		}
		rs.close();
		return cache.isEmpty() ? null : cache;
	}

	public static Config findId(String key, DataConnection dc) throws SQLException {
      
		Config cfg = cache.get(key);
		if (cfg != null) {
			return cfg;
		}
		String query = getSelectQuery("WHERE " + KEY + " = '" + key + "'");
		ResultSet rs = dc.executeQuery(query);
		while (rs.next()) {
			cfg = new Config(rs.getString(1).trim(), rs.getString(2).trim());
			cache.put(cfg.getKey(), cfg);
		}
		rs.close();
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
			update(c, dc);
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
