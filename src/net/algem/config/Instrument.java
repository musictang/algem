/*
 * @(#)Instrument.java	2.7.a 30/11/12
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
package net.algem.config;

import net.algem.util.model.GemModel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class Instrument
        implements GemModel
{

  public static final int MEMBER = 1;
  public static final int TEACHER = 2;
  public static final int MUSICIAN = 3;
  private int id;
  private String name;

  public Instrument() {
  }

  public Instrument(int i) {
    id = i;
    name = "";
  }

  public Instrument(int i, String l) {
    id = i;
    name = l;
  }

  @Override
  public String toString() {
//    return id + " " + name;
    return name;
  }

  public boolean equals(Instrument i) {
    return (i != null && id == i.id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Instrument other = (Instrument) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }


  @Override
  public int hashCode() {
    int hash = 3;
    hash = 11 * hash + this.id;
    return hash;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

}
