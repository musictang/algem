/*
 * @(#)ConfigIO.java 1.0.5 14/09/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.algem.util.AbstractGemDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.5
 * @since 1.0.5
 */
@Repository
public class ConfigIO
  extends AbstractGemDao

{

  public final static String TABLE = "config";
  public final static String COLUMNS = "clef,valeur";

  private final static String KEY = "clef";
  private final static String VAL = "valeur";


  public Config findId(String key) {

    String query = "SELECT clef,valeur FROM " + TABLE + " WHERE " + KEY + " = ?";
    return jdbcTemplate.queryForObject(query, new  RowMapper<Config>()
    {
      @Override
      public Config mapRow(ResultSet rs, int rowNum) throws SQLException {
        Config c = new Config(rs.getString(1), rs.getString(2));
        return c;
      }
    }, key);
  }

}
