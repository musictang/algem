/*
 * @(#)DefaultPreset.java 2.9.4.13 03/11/15
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */

package net.algem.course;

import net.algem.config.Preset;

/**
 * Module preset selection.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 20/10/2015
 */
public class DefaultPreset<T>
  implements Preset<T> {

  private static final long serialVersionUID = -6388535049138750981L;
  
  private int id;
  private String name;
  private T[] selection;

  public DefaultPreset() {
  }

  public DefaultPreset(int id, String name,  T[] selection) {
    this.id = id;
    this.name = name;
    this.selection = selection;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public T[] getValue() {
    return selection;
  }

  @Override
  public void setValue(T[] preset) {
    this.selection = preset;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + this.id;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DefaultPreset other = (DefaultPreset) obj;
    return this.id == other.id;
  }

  @Override
  public String toString() {
    return name;
  }

}
