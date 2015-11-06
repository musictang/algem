/*
 * @(#)Telephone.java 2.9.4.13 05/11/15
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

/**
 * Telephone model.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Telephone
        implements java.io.Serializable, Cloneable
{
  public static int DEFAULT_TYPE = 1;
  private static final long serialVersionUID = -1173024853654392333L;
  
  private int idper;
  private int idx;
  private String number;
  private int typetel;

  public Telephone() {
  }

  /**
   *
   * @param t int number type
   * @param n String number
   */
  public Telephone(int t, String n) {
    typetel = t;
    number = n;
  }

  /**
   * Two Telephones instances equal if number and type are equal.
   * @param t Telephone
   * @return vrai si égal
   */
  @Override
  public boolean equals(Object t) {
    if (t instanceof Telephone) {
      return equals((Telephone) t);
    }
    return false;
  }

  public boolean equals(Telephone t) {
    return number.equals(t.number) && typetel == t.typetel;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + (this.number != null ? this.number.hashCode() : 0);
    hash = 89 * hash + this.typetel;
    return hash;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String toString() {
    return idper + " " + typetel + " " + number;
  }

  public void setIdper(int i) {
    idper = i;
  }

  public int getIdper() {
    return idper;
  }

  public void setIdx(int i) {
    idx = i;
  }

  public int getIdx() {
    return idx;
  }

  public void setNumber(String s) {
    number = s;
  }

  public String getNumber() {
    return number;
  }

  public void setTypeTel(int t) {
    typetel = t;
  }

  public int getTypeTel() {
    return typetel;
  }
  
}
