/*
 * @(#)Equipment.java	2.9.4.13 05/11/15
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

/**
 * 
 * Room equipment.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 10/09/2001
 */
public class Equipment
        implements java.io.Serializable, Cloneable
{

  private static final long serialVersionUID = 8423669965600138221L;
  
  private int room;
  private int quantity;
  private String label;
  private short idx;

  public Equipment() {
  }

  /**
   * Creates an equipment.
   * @param label description
   * @param quantity
   * @param room room id
   */
  public Equipment(String label, int quantity, int room) {
    this.label = label;
    this.quantity = quantity;
    this.room = room;
  }

  public Equipment(int quantite, String libelle) {
    this.quantity = quantite;
    this.label = libelle;
  }

  public int getRoom() {
    return room;
  }

  public void setRoom(int i) {
    room = i;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int i) {
    quantity = i;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String l) {
    label = l;
  }
  
  public short getIdx() {
    return idx;
  }

  public void setIdx(short idx) {
    this.idx = idx;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Equipment other = (Equipment) obj;
    if (this.room != other.room) {
      return false;
    }
    if (this.quantity != other.quantity) {
      return false;
    }
    if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 19 * hash + this.room;
    hash = 19 * hash + this.quantity;
    hash = 19 * hash + (this.label != null ? this.label.hashCode() : 0);
    return hash;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String toString() {
    return quantity+" "+label+" ("+room+")";
  }

}
