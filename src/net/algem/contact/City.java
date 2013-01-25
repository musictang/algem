/*
 * @(#)City.java	2.6.a 17/09/12
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
package net.algem.contact;

/**
 * City.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class City
        implements java.io.Serializable
{

  private String cdp;
  private String city;

  public City() {
  }

  public City(String c, String v) {
    cdp = c;
    city = v;
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

  public boolean equals(City v) {
    return (v != null
            && cdp.equals(v.cdp)
            && city.equals(v.city));
  }

  @Override
  public String toString() {
    return cdp + " " + city;
  }
}
