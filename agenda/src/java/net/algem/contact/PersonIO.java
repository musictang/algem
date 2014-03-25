/*
 * @(#)PersonIO.java	1.0.2 28/01/14
 *
 * Copyright (c) 2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.algem.util.AbstractGemDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.2
 * @since 1.0.0 11/02/13
 */
@Repository
public class PersonIO
  extends AbstractGemDao {

  public static final String TABLE = "personne";

  public List<Person> findEstablishments(String where) {
    String query = "SELECT id, nom FROM " + TABLE + " WHERE ptype = " + Person.ESTABLISHMENT;
    if (where != null && !where.isEmpty()) {
      query += where;
    }
    return jdbcTemplate.query(query, new RowMapper<Person>() {

      @Override
      public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person p = new Person();
        p.setId(rs.getInt(1));
        p.setName(rs.getString(2));
        return p;
      }
    });
  }
}
