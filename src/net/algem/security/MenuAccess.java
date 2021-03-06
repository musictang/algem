/*
 * @(#)MenuAccess.java 2.8.p 01/11/13
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

/**
 * Access authorization menu.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 * @since 2.6.a 13/09/12
 */
class MenuAccess {
  
  private int id;
  private String label;
  private boolean auth;

  MenuAccess(int id, String label, boolean auth) {
    this.id = id;
    this.label = label;
    this.auth = auth;
  }

  boolean isAuth() {
    return auth;
  }

  void setAuth(boolean auth) {
    this.auth = auth;
  }

  int getId() {
    return id;
  }

  String getLabel() {
    return label;
  }
 
  void setLabel(String menuLabel) {
    this.label = menuLabel;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MenuAccess other = (MenuAccess) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + this.id;
    return hash;
  }

}
