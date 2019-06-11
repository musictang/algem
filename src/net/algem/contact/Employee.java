/*
 * @(#)Employee.java 2.9.4.2 10/04/15
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

package net.algem.contact;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.algem.planning.DateFr;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.2
 * @since 2.8.m 04/09/13
 */
public class Employee {

  private int idper;
  private String nir;
  private DateFr dateBirth;
  private String placeBirth;
  private String guso;
  private String nationality;
  private List<Integer> types;
  private int maritalStatus;
  private Date[] children;

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

  public String getNir() {
    return nir;
  }

  public void setNir(String nir) {
    this.nir = nir;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public int getMaritalStatus() {
    return maritalStatus;
  }

  public void setMaritalStatus(int maritalStatus) {
    this.maritalStatus = maritalStatus;
  }

  public Date[] getBirthDatesOfChildren() {
    return children;
  }

  public void setBirthDatesOfChildren(Date[] dates) {
    this.children = dates;
  }

  public List<Integer> getTypes() {
    return types;
  }

  public void setTypes(List<Integer> types) {
    this.types = types;
  }

  @Override
  public String toString() {
    return idper + " " + nir;
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
    if ((this.nir == null) ? (other.nir != null) : !this.nir.equals(other.nir)) {
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
    if ((this.nationality == null) ? (other.nationality != null) : !this.nationality.equals(other.nationality)) {
      return false;
    }
    if (this.maritalStatus != other.maritalStatus) {
      return false;
    }
    if ((this.types == null) ? (other.types != null) : this.types.size() != other.types.size()) {
      return false;
    }
    for(int i = 0; i < types.size(); i++) {
      if (types.get(i) != other.types.get(i)) {
        return false;
      }
    }
    if (!Arrays.deepEquals(this.children, other.children)) {
      return false;
    }
    
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 31 * hash + this.idper;
    hash = 31 * hash + Objects.hashCode(this.nir);
    hash = 31 * hash + Objects.hashCode(this.dateBirth);
    hash = 31 * hash + Objects.hashCode(this.placeBirth);
    hash = 31 * hash + Objects.hashCode(this.guso);
    hash = 31 * hash + Objects.hashCode(this.nationality);
    hash = 31 * hash + Objects.hashCode(this.types);
    hash = 31 * hash + this.maritalStatus;
    hash = 31 * hash + Arrays.hashCode(this.children);
    return hash;
  }

  /**
   * Checks if there is no information about insee.
   * @return true if all infos are empty, false if there is at least one info not empty.
   */
  public boolean isEmpty() {
    return (nir == null || nir.isEmpty())
            && (dateBirth == null || dateBirth.toString().equals(DateFr.NULLDATE))
            && (placeBirth == null || placeBirth.isEmpty())
            && (guso == null || guso.isEmpty())
            && (nationality == null || nationality.isEmpty())
            && (maritalStatus <= 0)
            && (types == null || types.size() <= 0)
            && (children == null || children.length <= 0);
  }

}
