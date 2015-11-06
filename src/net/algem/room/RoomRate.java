/*
 * @(#)RoomRate.java	2.9.4.13 05/11/15
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

import net.algem.util.model.GemModel;

/**
 * Room rate infos.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 05/10/10 2.1a
 */
public class RoomRate
        implements GemModel {

  private static final long serialVersionUID = 6310410461945842796L;
  
  private int id;
  private String label;
  
  /** Type of rate (hour, person, etc.). */
  private RoomRateEnum type;

  /** Off-peak rate. */
  private double offpeakRate;
  
  /** Full rate. */
  private double fullRate;
  
  /** Max rate for an hour. */
  private double maxHourPrice;

  /** Fixed off-peak rate. */
  private double passOffPeakPrice;

  /** Pass full price. */
  private double passFullPrice;

  public double getPassOffPeakPrice() {
    return passOffPeakPrice;
  }

  public void setPassOffPeakPrice(double price) {
    this.passOffPeakPrice = price;
  }

  public double getPassFullPrice() {
    return passFullPrice;
  }

  public void setPassFullPrice(double price) {
    this.passFullPrice = price;
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

  public double getOffpeakRate() {
    return offpeakRate;
  }

  public void setOffPeakRate(double nhRate) {
    this.offpeakRate = nhRate;
  }

  public double getFullRate() {
    return fullRate;
  }

  public void setFullRate(double price) {
    this.fullRate = price;
  }
  
  public double getMax() {
    return maxHourPrice;
  }

  public void setMax(double max) {
    this.maxHourPrice = max;
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
    if (this.offpeakRate != t.offpeakRate) {
      return false;
    }
    if (this.fullRate != t.fullRate) {
      return false;
    }
    if (this.maxHourPrice != t.maxHourPrice) {
      return false;
    }
    if (this.passOffPeakPrice != t.passOffPeakPrice) {
      return false;
    }
    if (this.passFullPrice != t.passFullPrice) {
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
