/*
 * @(#)Address.java	2.13.0 22/03/2017
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
package net.algem.contact;

import java.util.Objects;

/**
 * Address of contact.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 1.0a 07/07/1999
 */
public class Address
        implements java.io.Serializable, Cloneable
{
  private static final long serialVersionUID = -4271706486538263344L;

  private int oid;
  private int idper;
  private int type;
  private String adr1;
  private String adr2;
  private String cdp;
  private String city;
  private boolean archive;

  public Address() {
  }

  /**
   * Creates an address for contact {@code i}.
   * @param i id contact
   * @param t type
   * @param a1 address 1
   * @param a2 address 2
   * @param cp postal code
   * @param v city
   */
  public Address(int i, int t, String a1, String a2, String cp, String v) {
    idper = i;
    type = t;
    adr1 = a1;
    adr2 = a2;
    cdp = cp;
    city = v;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Address other = (Address) obj;
    if (this.archive != other.archive) {
      return false;
    }
    if (!Objects.equals(this.adr1, other.adr1)) {
      return false;
    }
    if (!Objects.equals(this.adr2, other.adr2)) {
      return false;
    }
    if (!Objects.equals(this.cdp, other.cdp)) {
      return false;
    }
    if (!Objects.equals(this.city, other.city)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + (this.adr1 != null ? this.adr1.hashCode() : 0);
    hash = 79 * hash + (this.adr2 != null ? this.adr2.hashCode() : 0);
    hash = 79 * hash + (this.cdp != null ? this.cdp.hashCode() : 0);
    hash = 79 * hash + (this.city != null ? this.city.hashCode() : 0);
    hash = 79 * hash + (this.archive ? 1 : 0);
    return hash;
  }

  @Override
  public String toString() {
//    return adr1 + " " + adr2 + " " + cdp + " " + city;
    StringBuilder address = new StringBuilder();
    address.append(getAdr1() == null || getAdr1().isEmpty() ? "" : getAdr1());
    if (getAdr2() != null && !getAdr2().isEmpty()) {
      address.append(' ').append(getAdr2());
    }
    if (getCdp() != null && !getCdp().isEmpty()) {
      address.append(' ').append(getCdp());
    }
    if (getCity() != null && !getCity().isEmpty()) {
      address.append(' ').append(getCity());
    }

    return address.toString();
  }

  public void setId(int i) {
    idper = i;
  }

  public int getId() {
    return idper;
  }

  public void setOID(int i) {
    oid = i;
  }

  public int getIOD() {
    return oid;
  }

  public void setAdr1(String s) {
    adr1 = s;
  }

  public String getAdr1() {
    return adr1;
  }

  public void setAdr2(String s) {
    adr2 = s;
  }

  public String getAdr2() {
    return adr2;
  }

  public void setCdp(String s) {
    cdp = s;
  }

  public String getCdp() {
    return cdp;
  }

  public void setCity(String s) {
    city = s;
  }

  public String getCity() {
    return city;
  }

  public void setArchive(boolean _archive) {
    archive = _archive;
  }

  public boolean isArchive() {
    return archive;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
