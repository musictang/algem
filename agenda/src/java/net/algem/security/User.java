/*
 * @(#)User.java	1.0.0 11/02/13
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

import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.0
 * @since 1.0.0 11/02/13
 */
public class User {

	public static final int VISITOR = 1;
	public static final int ADMIN = 2;
//	@Pattern(regexp="^[a-zA-Z0-9]+$", message="Username must be alphanumeric with no spaces")
	private int id;
	private String login;
	private String password;
	private short profile;
	private String name;

	/**
	 * Gets the profile's value.
	 *
	 * @return the value of profile
	 */
	public short getProfile() {
		return profile;
	}

	/**
	 * Sets the profile's value.
	 *
	 * @param profile new value of profile
	 */
	public void setProfile(short profile) {
		this.profile = profile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the name's value.
	 *
	 * @return the value of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name's value.
	 *
	 * @param name new value of name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User {" + "login=" + login + ", password = " + password + ", profile=" + profile + '}';
	}
}
