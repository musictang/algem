/*
 * @(#)RehearsalPass.java 2.9.4.13 03/11/15
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
package net.algem.contact.member;

import net.algem.util.model.GemModel;

/**
 * Subscription pass option for rehearsals.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class RehearsalPass
  implements GemModel
{

  public final static int MIN_DEFAULT = 30; // see DEFAULT value in database
  private static final long serialVersionUID = -6338921697633719320L;
  
  private int id;
  private String label;
  private float amount;
   
  /** Minimal time of a session. */
  private int min;
  
  /** Total time in minutes. */
  private int totalTime;

  public RehearsalPass() {
  }

  /**
   * Creates a new default card.
   *
   * @param id
   */
  public RehearsalPass(int id) {
    this(id, "", 0.0F, MIN_DEFAULT);
  }

  public RehearsalPass(String label, float amount, int total) {
    this.label = label;
    this.amount = amount;
    this.min = MIN_DEFAULT;
    this.totalTime = 60;
  }

  public RehearsalPass(int id, String label, float amount, int total) {
    this.id = id;
    this.label = label;
    this.amount = amount;
    this.min = MIN_DEFAULT;
    this.totalTime = 60;
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

  void setLabel(String label) {
    this.label = label;
  }

  public float getAmount() {
    return amount;
  }

  void setAmount(float amount) {
    this.amount = amount;
  }

  public int getMin() {
    return min;
  }

  void setMin(int d) {
    this.min = d;
  }

  public boolean strictlyEquals(RehearsalPass other) {
    if (other == null) {
      return false;
    }

    if (this.id != other.id) {
      return false;
    }
    if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
      return false;
    }
    if (this.amount != other.amount) {
      return false;
    }
    if (this.min != other.min) {
      return false;
    }
    return this.totalTime == other.totalTime;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RehearsalPass other = (RehearsalPass) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + this.id;
    return hash;
  }

  @Override
  public String toString() {
    return label;
  }

  void setTotalTime(int min) {
    this.totalTime = min;
  }
  
  public int getTotalTime() {
    return totalTime;
  }
  
}
