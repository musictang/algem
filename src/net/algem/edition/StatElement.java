/*
 * @(#)StatElement.java 2.10.0 07/06/2016
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.edition;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 07/06/2016
 */
public class StatElement {

  private int key;
  private String label;
  private boolean active;

  public StatElement(int key, String label, boolean active) {
    this.key = key;
    this.label = label;
    this.active = active;
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 41 * hash + this.key;
    hash = 41 * hash + Objects.hashCode(this.label);
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
    final StatElement other = (StatElement) obj;
    if (this.key != other.key) {
      return false;
    }
    if (!Objects.equals(this.label, other.label)) {
      return false;
    }
    return true;
  }
  
}
