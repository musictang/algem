/*
 * @(#)ModuleType.java	2.9.4.13 05/11/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.course;

/**
 * Module type.
 * There is a relation between module type and course.
 * A module type must exists for a specific course.
 * The course code is linked to module type code.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class ModuleType
        implements java.io.Serializable
{

  private static final long serialVersionUID = -5041491730098885876L;
  
  private int id;
  private String code;
  private String label;

  public ModuleType() {
  }

  public ModuleType(int i) {
    id = i;
    code = "";
  }

  @Override
  public String toString() {
    return code;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ModuleType other = (ModuleType) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 67 * hash + this.id;
    return hash;
  }

  public void setId(int i) {
    id = i;
  }

  public int getId() {
    return id;
  }

  public void setLabel(String s) {
    label = s;
  }

  public String getLabel() {
    return label;
  }

  public void setCode(String c) {
    code = c;
  }

  public String getCode() {
    return code;
  }
  
}
