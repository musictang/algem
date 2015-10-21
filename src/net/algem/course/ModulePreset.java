/*
 * @(#)ModulePreset.java 2.9.4.13 20/10/2015
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
public class ModulePreset 
  extends Preset {
  
  private int id;
  private String label;
  private int[] selection;

  public ModulePreset(int id, String label, int[] selection) {
    this.id = id;
    this.label = label;
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

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public int[] getValue() {
    return selection;
  }

  @Override
  public void setValue(Object preset) {
    this.selection = (int[]) preset;
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
    final ModulePreset other = (ModulePreset) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return label;
  }

  
}
