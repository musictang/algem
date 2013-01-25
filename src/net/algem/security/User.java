/*
 * @(#)User.java	2.6.d 06/11/12
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
package net.algem.security;

import net.algem.contact.Person;

/**
 * Algem user.
 * An user is defined by a default profile.
 * @see net.algem.security.UserIO
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.d
 */
public class User
        extends Person
{

  private String login;
  private String password;
  private int profile;

  public User() {
  }

  public User(Person p) {
    id = p.getId();
    name = p.getName();
    firstName = p.getFirstName();
    civility = p.getCivility();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Person p = (Person) obj;
    if (!super.equals(p)) {
      return false;
    }
    final User other = (User) obj;
    if ((this.login == null) ? (other.login != null) : !this.login.equals(other.login)) {
      return false;
    }
    if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password)) {
      return false;
    }
    if (this.profile != other.profile) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + (this.login != null ? this.login.hashCode() : 0);
    hash = 17 * hash + (this.password != null ? this.password.hashCode() : 0);
    hash = 17 * hash + this.profile;
    return hash;
  }

  @Override
  public String toString() {
    return super.toString() + " " + login;
  }

  public void setLogin(String s) {
    login = s;
  }

  public String getLogin() {
    return login;
  }

  public void setPassword(String s) {
    password = s;
  }

  public String getPassword() {
    return password;
  }

  public void setProfile(int i) {
    profile = i;
  }

  public int getProfile() {
    return profile;
  }
}
