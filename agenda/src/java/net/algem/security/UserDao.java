/*
 * @(#)UserDao.java	1.0.0 11/02/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.algem.util.AbstractGemDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.0
 * @since 1.0.0 11/02/13
 */
@Repository
public class UserDao extends AbstractGemDao {

	public UserDao() {
	}

	public List<User> findAll() {
		String query = "SELECT * FROM login";
		return jdbcTemplate.query(query, new RowMapper<User>() {

			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User u = new User();
				u.setId(rs.getInt(1));
				u.setLogin(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setProfile(rs.getShort(4));
				return u;
			}
		});
	}

	public User find(String login) {
		String query = "SELECT * FROM login WHERE login = ?";
		return jdbcTemplate.queryForObject(query, new RowMapper<User>() {

			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User u = new User();

				u.setId(rs.getInt(1));
				u.setLogin(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setProfile(rs.getShort(4));

				return u;
			}
		}, login);

	}

	public User findById(int id) {
		String query = "SELECT * FROM login WHERE idper = ?";
		List<User> users = jdbcTemplate.query(query, new RowMapper<User>() {

			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User u = new User();

				u.setId(rs.getInt(1));
				u.setLogin(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setProfile(rs.getShort(4));

				return u;
			}
		}, id);
		return users.isEmpty() ? null : users.get(0);
	}

	public List<Map<String, Boolean>> listMenuAccess(int userId) {
		String query = "SELECT m.label, a.autorisation FROM  menu2 m JOIN menuaccess a ON m.id = a.idmenu WHERE a.idper = ?";
		return jdbcTemplate.query(query, new RowMapper<Map<String, Boolean>>() {

			public Map<String, Boolean> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Boolean> map = new HashMap<String, Boolean>();
				map.put(rs.getString(1), rs.getBoolean(2));
				return map;
			}
		}, userId);

	}
}
