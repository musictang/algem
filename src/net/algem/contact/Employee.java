/*
 * @(#)Employee.java 2.8.m 06/09/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

package net.algem.contact;

import net.algem.planning.DateFr;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.8.m 04/09/13
 */
public class Employee {
  
  private int idper;
  private String insee;
  private DateFr dateBirth;
  private String placeBirth;
  private String guso;

  public Employee(int idper) {
    this.idper = idper;
  }

  public String getPlaceBirth() {
    return placeBirth;
  }

  public void setPlaceBirth(String cityBirth) {
    this.placeBirth = cityBirth;
  }

  public DateFr getDateBirth() {
    return dateBirth;
  }

  public void setDateBirth(DateFr dateBirth) {
    this.dateBirth = dateBirth;
  }

  public String getGuso() {
    return guso;
  }

  public void setGuso(String guso) {
    this.guso = guso;
  }

  public int getIdPer() {
    return idper;
  }

  public void setIdPer(int id) {
    this.idper = id;
  }

  public String getInsee() {
    return insee;
  }

  public void setInsee(String insee) {
    this.insee = insee;
  }

  @Override
  public String toString() {
    return idper + " " + insee;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Employee other = (Employee) obj;
    if (this.idper != other.idper) {
      return false;
    }
    if ((this.insee == null) ? (other.insee != null) : !this.insee.equals(other.insee)) {
      return false;
    }
    if ((this.dateBirth == null) ? (other.dateBirth != null) : !this.dateBirth.equals(other.dateBirth)) {
      return false;
    }
    if ((this.placeBirth == null) ? (other.placeBirth != null) : !this.placeBirth.equals(other.placeBirth)) {
      return false;
    }
    if ((this.guso == null) ? (other.guso != null) : !this.guso.equals(other.guso)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + this.idper;
    hash = 59 * hash + (this.insee != null ? this.insee.hashCode() : 0);
    hash = 59 * hash + (this.dateBirth != null ? this.dateBirth.hashCode() : 0);
    hash = 59 * hash + (this.placeBirth != null ? this.placeBirth.hashCode() : 0);
    hash = 59 * hash + (this.guso != null ? this.guso.hashCode() : 0);
    return hash;
  }
  
  /**
   * Checks if there is no information about insee.
   * @return true if all infos are empty, false if there is at least one info not empty.
   */
  public boolean isEmpty() {
    return (insee == null || insee.isEmpty())
            && (dateBirth == null || dateBirth.toString().equals(DateFr.NULLDATE))
            && (placeBirth == null || placeBirth.isEmpty())
            && (guso == null || guso.isEmpty());
  }
  
}
