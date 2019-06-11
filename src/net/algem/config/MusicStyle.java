/*
 * @(#)MusicStyle.java	2.9.4.13 03/11/15
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
package net.algem.config;

import net.algem.util.model.GemModel;

/**
 * Musical style.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class MusicStyle
        implements GemModel
{

  private static final long serialVersionUID = -6985183064680912108L;
  
  private int id;
  private String label;

  public MusicStyle() {
  }

  public MusicStyle(int i) {
    id = i;
    label = "Autre";
  }

  public MusicStyle(int i, String l) {
    id = i;
    label = l;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String l) {
    label = l;
  }

  @Override
  public String toString() {
    return label;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MusicStyle other = (MusicStyle) obj;
    return this.id == other.id;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + this.id;
    return hash;
  }
 
}
