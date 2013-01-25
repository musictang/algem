/*
 * @(#)Musician.java	2.7.a 22/11/12
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
package net.algem.group;

import net.algem.contact.Person;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class Musician
        extends Person
{

  private int instrument;
  private Group group;

  public Musician() {
  }

  public Musician(int id, int instrument) {
    this.id = id;
    this.instrument = instrument;
  }

  public Musician(Person p) {
    id = p.getId();
    name = p.getName();
    firstName = p.getFirstName();
    civility = p.getCivility();
    type = p.getType();
    imgRights = p.getImgRights();
  }

  public void setInstrument(int i) {
    instrument = i;
  }

  public int getInstrument() {
    return instrument;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Musician other = (Musician) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.instrument != other.instrument) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + this.id;
    hash = 59 * hash + this.instrument;
    return hash;
  }

  @Override
  public String toString() {
    return super.toString() + " " + instrument;
  }
}
