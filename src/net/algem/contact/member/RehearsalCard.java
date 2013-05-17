/*
 * @(#)RehearsalCard.java 2.8.c 14/05/13
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
package net.algem.contact.member;

import net.algem.util.model.GemModel;

/**
 * Subscription pass option for rehearsals.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.c
 */
public class RehearsalCard
  implements GemModel
{

  private int id;
  private String label;
  private float amount;
  
  /** Sessions number. */
  private int nbSessions;
  
  /** Length in minutes for a session. */
  private int length;
  
  public final static int NB_SESSIONS_DEFAULT = 10;
  public final static int MIN_LENGTH_DEFAULT = 30;

  public RehearsalCard() {
  }

  /**
   * Creates a new default card.
   *
   * @param id
   */
  public RehearsalCard(int id) {
    this(id, "", 0.0F, NB_SESSIONS_DEFAULT, MIN_LENGTH_DEFAULT);
  }

  public RehearsalCard(String label, float amount, int sessions, int length) {
    this.label = label;
    this.amount = amount;
    this.nbSessions = sessions;
    this.length = length;
  }

  public RehearsalCard(int id, String label, float amount, int sessions, int length) {
    this.id = id;
    this.label = label;
    this.amount = amount;
    this.nbSessions = sessions;
    this.length = length;
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

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public int getSessionsNumber() {
    return nbSessions;
  }

  public void setSessionsNumber(int n) {
    this.nbSessions = n;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int d) {
    this.length = d;
  }

  public boolean strictlyEquals(RehearsalCard other) {
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
    if (this.nbSessions != other.nbSessions) {
      return false;
    }
    if (this.length != other.length) {
      return false;
    }
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RehearsalCard other = (RehearsalCard) obj;
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

  public int getTotalLength() {
    return getSessionsNumber() * getLength();
  }
}
