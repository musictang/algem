/*
 * @(#) TableRights.java Algem 2.13.2 03/05/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.security;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
 * @since 2.13.2 03/05/2017
 */
public class SQLRights {

  private String name;
  private boolean authRead;
  private boolean authInsert;
  private boolean authUpdate;
  private boolean authDelete;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAuthRead() {
    return authRead;
  }

  public void setAuthRead(boolean authRead) {
    this.authRead = authRead;
  }

  public boolean isAuthInsert() {
    return authInsert;
  }

  public void setAuthInsert(boolean authInsert) {
    this.authInsert = authInsert;
  }

  public boolean isAuthUpdate() {
    return authUpdate;
  }

  public void setAuthUpdate(boolean authUpdate) {
    this.authUpdate = authUpdate;
  }

  public boolean isAuthDelete() {
    return authDelete;
  }

  public void setAuthDelete(boolean authDelete) {
    this.authDelete = authDelete;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + Objects.hashCode(this.name);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SQLRights other = (SQLRights) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return true;
  }


}
