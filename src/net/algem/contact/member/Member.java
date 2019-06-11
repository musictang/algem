/*
 * @(#)Member.java	2.17.0 05/06/2019
 *                      2.15.0 30/07/2017
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
package net.algem.contact.member;

import java.util.List;
import net.algem.config.CategoryOccup;
import net.algem.planning.DateFr;
import net.algem.util.model.GemModel;

/**
 * Member object model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0 lien payer+family
 */
public class Member
        implements GemModel, Cloneable
{

  private static final long serialVersionUID = 217;

  private int id;
  private String occupation;
  private DateFr birth;
  private int payer;
  private int family;
  private int membershipCount;
  private int practice;
  private int level;
  private List<Integer> instruments;
  private String insurance;
  private String insuranceRef;

  public Member(int _id) {
    id = _id;
  }

  public void clear() {
    instruments.clear();
    occupation = "aucun";
    birth = new DateFr(DateFr.NULLDATE);
    payer = 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Member other = (Member) obj;
    if (this.id != other.id) {
      return false;
    }
    if ((this.occupation == null) ? (other.occupation != null) : !this.occupation.equals(other.occupation)) {
      return false;
    }
    if (this.birth != other.birth && (this.birth == null || !this.birth.equals(other.birth))) {
      return false;
    }
    if (this.payer != other.payer) {
      return false;
    }
    if (this.family != other.family) {
      return false;
    }
    if (this.membershipCount != other.membershipCount) {
      return false;
    }
    if (this.practice != other.practice) {
      return false;
    }
    if (this.level != other.level) {
      return false;
    }
    if (this.instruments != other.instruments && (this.instruments == null || !this.instruments.equals(other.instruments))) {
      return false;
    }
    if ((this.insurance == null) ? (other.insurance != null) : !this.insurance.equals(other.insurance)) {
      return false;
    }
    if ((this.insuranceRef == null) ? (other.insuranceRef != null) : !this.insuranceRef.equals(other.insuranceRef)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + (this.occupation != null ? this.occupation.hashCode() : 0);
    hash = 47 * hash + (this.birth != null ? this.birth.hashCode() : 0);
    hash = 47 * hash + this.payer;
    hash = 47 * hash + this.family;
    hash = 47 * hash + this.membershipCount;
    hash = 47 * hash + this.practice;
    hash = 47 * hash + this.level;
    hash = 47 * hash + (this.instruments != null ? this.instruments.hashCode() : 0);
    hash = 47 * hash + (this.insurance != null ? this.insurance.hashCode() : 0);
    hash = 47 * hash + (this.insuranceRef != null ? this.insuranceRef.hashCode() : 0);
    return hash;
  }

  @Override
  public void setId(int _id) {
    id = _id;
  }

  @Override
  public int getId() {
    return id;
  }

  public void setOccupation(CategoryOccup c) {
    occupation = c.getLabel();
  }

  public void setOccupation(String i) {
    occupation = i;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setBirth(DateFr d) {
    birth = d;
  }

  public DateFr getBirth() {
    return birth;
  }

  public List<Integer> getInstruments() {
    return instruments;
  }

  public void setInstruments(List<Integer> instruments) {
    this.instruments = instruments;
  }

  public int getFirstInstrument() {
    return instruments == null || instruments.isEmpty() ? 0 : instruments.get(0);
  }

  public void setPayer(int p) {
    payer = p;
  }

  public int getPayer() {
    return payer;
  }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

  public void setMembershipCount(int n) {
    membershipCount = n;
  }

  public int getMembershipCount() {
    return membershipCount;
  }

  public void setLevel(int p) {
    level = p;
  }

  public int getLevel() {
    return level;
  }

  public void setPractice(int p) {
    practice = p;
  }

  public int getPractice() {
    return practice;
  }

  public String getInsurance() {
    return insurance;
  }

  public void setInsurance(String insurance) {
    this.insurance = insurance;
  }

  public String getInsuranceRef() {
    return insuranceRef;
  }

  public void setInsuranceRef(String insuranceRef) {
    this.insuranceRef = insuranceRef;
  }

  public boolean isValid() {
    return true;
    // && instrument1.length() > 1;
  }

  @Override
  public String toString() {
    return id + " " + birth;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
