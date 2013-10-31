/*
 * @(#)UserPass 2.8.p 30/10/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

package net.algem.security;

import java.util.Arrays;

/**
 * User password info.
 * Encrypted password is associated with its salt.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 * @since 2.8.p 30/10/13
 */
class UserPass 
{
	private byte[] pass;
	private byte[] key;

	public UserPass(byte[] pass, byte[] key) {
		this.pass = pass;
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UserPass other = (UserPass) obj;
		if (!Arrays.equals(this.pass, other.pass)) {
			return false;
		}
		if (!Arrays.equals(this.key, other.key)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Arrays.hashCode(this.pass);
		hash = 29 * hash + Arrays.hashCode(this.key);
		return hash;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public byte[] getPass() {
		return pass;
	}

	public void setPass(byte[] pass) {
		this.pass = pass;
	}
	
}
