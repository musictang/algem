/*
 * @(#)Room.java	2.9.4.13 15/10/15
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
package net.algem.room;

import java.util.Vector;
import net.algem.contact.Contact;
import net.algem.contact.Person;
import net.algem.util.model.GemModel;

/**
 * Room object model.
 * A room is a person of type {@link net.algem.contact.Person#ROOM}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 02/09/2001
 * 
 */
public class Room
        implements Cloneable, GemModel
{

  private int id;

  /** Contact referent. */
  private Contact contact;

  /** Room rate. */
  private RoomRate rate;

  private String name;
  private String function;
  private int surface;
  private int npers;
  private int estab;
  private boolean active;
  
  /** Public access and available for rehearsals. */
  private boolean available;

  private Vector<Equipment> equipment;
  
  /** Payer. */
  private Person payer;

  public Room() {
    this(0, "aucune");
  }

  public Room(int n) {
    this(n, "");
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

  public boolean isEqualOf(Object obj) {
    if (!equals(obj)) {
      return false;
    }
    final Room other = (Room) obj;
    if (this.contact != other.contact && (this.contact == null || !this.contact.equals(other.contact))) {
      return false;
    }
    if (this.rate != other.rate && (this.rate == null || !this.rate.equals(other.rate))) {
      return false;
    }
    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
      return false;
    }
    if ((this.function == null) ? (other.function != null) : !this.function.equals(other.function)) {
      return false;
    }
    if (this.surface != other.surface) {
      return false;
    }
    if (this.npers != other.npers) {
      return false;
    }
    if (this.estab != other.estab) {
      return false;
    }
    if (this.active != other.active) {
      return false;
    }
    if (this.available != other.available) {
      return false;
    }
    /*if ((this.payer == null) ? (other.payer != null) : this.payer.getId() != other.payer.getId()) {
      return false;
    }*/
    return true;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  @Override
  public int getId() {
    return id;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public RoomRate getRate() {
    return rate;
  }

  public void setRate(RoomRate rate) {
    this.rate = rate;
  }

  public void setName(String s) {
    name = s;
  }

  public String getName() {
    return name;
  }

  public void setFunction(String s) {
    function = s;
  }

  public String getFunction() {
    return function;
  }

  public void setSurface(int i) {
    surface = i;
  }

  public int getSurface() {
    return surface;
  }

  public void setEstab(int i) {
    estab = i;
  }

  public int getEstab() {
    return estab;
  }

  public void setNPers(int i) {
    npers = i;
  }

  public int getNPers() {
    return npers;
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

  public Person getPayer() {
    return payer;
  }

  public void setPayer(Person p) {
    this.payer = p;
  }

  @Override
  public String toString() {
    return name;
  }

  public Vector<Equipment> getEquipment() {
    return equipment;
  }

  public void setEquipment(Vector<Equipment> equipment) {
    this.equipment = equipment;
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

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /** Debug. */
  private boolean out(int n) {
//    System.out.println("!Room.equals "+n);
    return false;
  }
}
