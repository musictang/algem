/*
 * @(#)Equipment.java	2.14.0 13/06/17
 * 
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.util.Objects;

/**
 * 
 * Room equipment.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
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
  private String fixedAssetNumber;
  private boolean visible;

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

  public Equipment(int quantity, String label) {
    this.quantity = quantity;
    this.label = label;
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

  public String getFixedAssetNumber() {
    return fixedAssetNumber;
  }

  public void setFixedAssetNumber(String fixedAssetNumber) {
    this.fixedAssetNumber = fixedAssetNumber;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 13 * hash + this.room;
    hash = 13 * hash + this.quantity;
    hash = 13 * hash + Objects.hashCode(this.label);
    hash = 13 * hash + Objects.hashCode(this.fixedAssetNumber);
    hash = 13 * hash + (this.visible ? 1 : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
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
    if (this.visible != other.visible) {
      return false;
    }
    if (!Objects.equals(this.label, other.label)) {
      return false;
    }
    if (!Objects.equals(this.fixedAssetNumber, other.fixedAssetNumber)) {
      return false;
    }
    return true;
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
