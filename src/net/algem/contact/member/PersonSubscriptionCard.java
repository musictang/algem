/*
 * @(#)PersonSubscriptionCard.java 2.9.2 19/12/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;

/**
 * Subscription card for single rehearsals.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class PersonSubscriptionCard
{

  private int id;
  private int idper;
  private int rehearsalCardId;
  private DateFr purchaseDate;
  private int rest;
  private List<PersonalCardSession> sessions = new ArrayList<PersonalCardSession>();

  public PersonSubscriptionCard() {
  }

  public PersonSubscriptionCard(int idper, int cardId, DateFr date, int r) {
    this.idper = idper;
    this.rehearsalCardId = cardId;
    this.purchaseDate = date;
    this.rest = r;
  }

  public PersonSubscriptionCard(int id) {
    this.idper = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public DateFr getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(DateFr date) {
    this.purchaseDate = date;
  }

  public int getRehearsalCardId() {
    return rehearsalCardId;
  }

  public void setRehearsalCardId(int cardId) {
    this.rehearsalCardId = cardId;
  }

  public int getIdper() {
    return idper;
  }

  public void setIdper(int idper) {
    this.idper = idper;
  }

  public int getRest() {
    return rest;
  }

  public void setRest(int r) {
    this.rest = r;
  }

  List<PersonalCardSession> getSessions() {
    return sessions;
  }

  void setSessions(List<PersonalCardSession> sessions) {
    this.sessions = sessions;
  }

  /**
   * Adds the session corresponding to {@code scheduleId} to this card.
   * @param scheduleId
   */
  public void addSession(int scheduleId) {
    if (scheduleId > 0) {
      PersonalCardSession s = new PersonalCardSession();
      s.setScheduleId(scheduleId);
      sessions.add(s);
      }
  }

  /**
   * Adds a session to this card.
   * @param s single session
   */
  void addSession(PersonalCardSession s) {
    sessions.add(s);
  }

  /**
   * Une nouvelle carte d'abonnement ne comporte pas de date d'achat.
   * @return vrai si la date d'achat est nulle
   */
  public boolean isNewCard() {
    return getPurchaseDate() == null;
  }

  /**
   * Decrements the number of remainder hours.
   * @param duration in minutes
   */
  public void dec(int duration) {
    /*if (getLength > restant) {
    restant = 0;
    }
    else {
    restant -= getLength;
    }*/
    rest -= duration;
  }

  /**
   * Increments the number of remainder minutes.
   * @param duration in minutes
   */
  public void inc(int duration) {
    rest += duration;
  }

  /**
   * Gets the remainders hours in card.
   * @return a duration as string
   */
  public String displayRestTime() {
    Hour h = new Hour(getRest());
    return h.toSimpleString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PersonSubscriptionCard other = (PersonSubscriptionCard) obj;
    if (this.idper != other.idper) {
      return false;
    }
    if (this.rehearsalCardId != other.rehearsalCardId) {
      return false;
    }
    if (this.purchaseDate != other.purchaseDate && (this.purchaseDate == null || !this.purchaseDate.equals(other.purchaseDate))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 61 * hash + this.idper;
    hash = 61 * hash + this.rehearsalCardId;
    hash = 61 * hash + (this.purchaseDate != null ? this.purchaseDate.hashCode() : 0);
    return hash;
  }
}
