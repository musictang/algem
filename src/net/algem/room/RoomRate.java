/*
 * @(#)RoomRate.java	2.6.a 24/09/12
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
package net.algem.room;

import net.algem.util.model.GemModel;

/**
 * Room rate infos.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 05/10/10 2.1a
 */
public class RoomRate
        implements GemModel {

  private int id;
  private String label;
  
  /** Type of rate (hour, person, etc.). */
  private RoomRateEnum type;

  /** Offpeak rate. */
  private double nh;
  
  /** Peak rate. */
  private double ph;
  
  /** Max rate for an hour. */
  private double maxHourPrice;

  /** Fixed offpeak rate. */
  private double fixedNh;

  /** Fixed peak rate. */
  private double fixedPh;

  public double getFixedNh() {
    return fixedNh;
  }

  public void setFixedNh(double fixedNh) {
    this.fixedNh = fixedNh;
  }

  public double getFixedPh() {
    return fixedPh;
  }

  public void setFixedPh(double fixedPh) {
    this.fixedPh = fixedPh;
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

  public double getNh() {
    return nh;
  }

  public void setNh(double nhRate) {
    this.nh = nhRate;
  }

  public double getMax() {
    return maxHourPrice;
  }

  public void setMax(double max) {
    this.maxHourPrice = max;
  }

  public double getPh() {
    return ph;
  }

  public void setPh(double phRate) {
    this.ph = phRate;
  }

  public RoomRateEnum getType() {
    return type;
  }

  public void setType(RoomRateEnum type) {
    this.type = type;
  }

  /**
   * Strict comparison between 2 rates.
   * @param t
   * @return true if equals
   */
  public boolean equals(RoomRate t) {
    if (t == null) {
      return false;
    }

    if (this.id != t.id) {
      return false;
    }
    if ((this.label == null) ? (t.label != null) : !this.label.equals(t.label)) {
      return false;
    }
    if ((this.type == null) ? (t.type != null) : !this.type.equals(t.type)) {
      return false;
    }
    if (this.nh != t.nh) {
      return false;
    }
    if (this.ph != t.ph) {
      return false;
    }
    if (this.maxHourPrice != t.maxHourPrice) {
      return false;
    }
    if (this.fixedNh != t.fixedNh) {
      return false;
    }
    if (this.fixedPh != t.fixedPh) {
      return false;
    }
    return true;
  }

  /**
   * Used for collections.
   *
   * @param obj
   * @return true if equals
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RoomRate other = (RoomRate) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + this.id;
    return hash;
  }

  @Override
  public String toString() {
    return label;
  }
}
