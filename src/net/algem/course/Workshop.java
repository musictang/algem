/*
 * @(#)Workshop.java	2.9.4.13 05/11/15
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

import java.util.Objects;
import net.algem.util.BundleUtil;

/**
 * Workshop entity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Workshop
        implements java.io.Serializable
{

  private static final long serialVersionUID = 9030117032647031173L;
  
  private int id;
  private String name;
  private int teacherId;

  public Workshop() {
  }

  public Workshop(int n) {
    this.id = n;
    this.name = BundleUtil.getLabel("None.label");
  }

  public Workshop(String s) {
    this.id = 0;
    this.name = s;
  }

  public Workshop(int i, String s) {
    this.id = i;
    this.name = s;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Workshop a = (Workshop) o;
      
    return (id == a.id);
  }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + this.teacherId;
        return hash;
    }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public String getName() {
    return name;
  }

  public void setName(String l) {
    name = l;
  }

  public int getTeacher() {
    return teacherId;
  }

  public void setTeacher(int i) {
    teacherId = i;
  }
  
}
