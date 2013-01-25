/*
 * @(#)Teacher.java	2.7.c 23/01/13
 *
 * Copyright (c) 2009 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact.teacher;

import java.util.List;
import net.algem.contact.Person;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.c
 */
public class Teacher
  extends Person
{

  private List<Integer> instruments;
  private String cert1 = "";
  private String cert2 = "";
  private String cert3 = "";
  private boolean active;

  public Teacher(int _id) {
    id = _id;
  }

  @Override
  public String toString() {
    //return String.valueOf(id); //+ instrument1 + "/" + instrument2;
    return firstName + " " + name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    
    final Teacher other = (Teacher) obj;
    if (this.id != other.id) {
      return false;
    }
    if ((this.cert1 == null) ? (other.cert1 != null) : !this.cert1.equals(other.cert1)) {
      return false;
    }
    if ((this.cert2 == null) ? (other.cert2 != null) : !this.cert2.equals(other.cert2)) {
      return false;
    }
    if ((this.cert3 == null) ? (other.cert3 != null) : !this.cert3.equals(other.cert3)) {
      return false;
    }
    if (this.active != other.active) {
      return false;
    }
    if (this.instruments != other.instruments && (this.instruments == null || !this.instruments.equals(other.instruments))) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;

    hash = 59 * hash + (this.cert1 != null ? this.cert1.hashCode() : 0);
    hash = 59 * hash + (this.cert2 != null ? this.cert2.hashCode() : 0);
    hash = 59 * hash + (this.cert3 != null ? this.cert3.hashCode() : 0);
    hash = 59 * hash + (this.active ? 1 : 0);
    hash = 59 * hash + (this.instruments != null ? this.instruments.hashCode() : 0);
    return hash;
  }

  public List<Integer> getInstruments() {
    return instruments;
  }

  public void setInstruments(List<Integer> instruments) {
    this.instruments = instruments;
  }

  public void setCertificate1(String s) {
    cert1 = s;
  }

  public String getCertificate1() {
    return cert1;
  }

  public void setCertificate2(String s) {
    cert2 = s;
  }
  
  public String getCertificate2() {
    return cert2;
  }

  public void setCertificate3(String s) {
    cert3 = s;
  }
  
  public String getCertificate3() {
    return cert3;
  }

  public void setActive(boolean s) {
    active = s;
  }

  public boolean isActive() {
    return active;
  }

  @Override
  public boolean isValid() {
    return true;
//    return instruments == null || instruments.isEmpty() ? true : 
//    return instruments.size() > 1;//Ca ne devrait pas arriver
  }
}
