/*
 * @(#)Room.java	1.0.1 06/03/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.room;

//import net.algem.contact.Person;

/**
 * Room object model.
 * A room is a person of type {@link net.algem.contact.Person#ROOM}.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.1
 * @since 1.0.1 06/03/13
 * 
 */
public class Room
{

  private int id;

  private String name;
  private int estab;
  private boolean active;
  
  /** Public access and available for rehearsals. */
  private boolean available;

  public Room() {
    this(0, "aucune");
  }

  public Room(int n) {
    this(n, "?????");
  }

  public Room(String n) {
    this(0, n);
  }

  public Room(int i, String n) {
    id = i;
    name = n;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Room other = (Room) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 61 * hash + this.id;
    return hash;
  }

  public void setId(int i) {
    id = i;
  }

  public int getId() {
    return id;
  }

  public void setName(String s) {
    name = s;
  }

  public String getName() {
    return name;
  }

  public void setEstab(int i) {
    estab = i;
  }

  public int getEstab() {
    return estab;
  }

  public void setActive(boolean n) {
    active = n;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  @Override
  public String toString() {
    return name;
  }
  
  /**
   * Checks if this room is used to catching up with lessons when a teacher is absent.
   * 
   * @return true if catching up
   */
  public boolean isCatchingUp() {
    // TODO test should not apply to room's name
    String regex = "(?iu).*(RATTRAP|CATCHING).*";
    return name.matches(regex);
  }

}
