/*
 * @(#)AgeRange.java 2.9.4.13 05/11/15
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
 */

package net.algem.config;

import net.algem.util.model.GemModel;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.a
 */

public class AgeRange
  implements GemModel
{
  private static final long serialVersionUID = 7996967257343256662L;
  private int id; 
  private int agemin;
  private int agemax;
  private String label;
  private String code;

  public AgeRange() {
  }

  public AgeRange(int id, String code) {
    this.id = id;
    this.code = code;
  }

  @Override
  public String toString() {
    return label;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AgeRange other = (AgeRange) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + this.id;
    return hash;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  public String getLabel() {
    return label;
  }
  
  public void setLabel(String l) {
    label = l;
  }

  public void setAgemin(int min) {
      agemin = min;
  }

  public int getAgemin() {
    return agemin;
  }

  public void setAgemax(int max) {
      agemax = max;
  }

  public int getAgemax() {
    return agemax;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
  
  public boolean isUndefined() {
    return GemParam.NONE.equals(code); 
  }
  
  /**
   * 
   * @param t
   * @return true if overlapping
   * @deprecated 
   */
  public boolean intersect(AgeRange t) {
      return ((this.agemin <= t.getAgemin() && this.agemax > t.getAgemin())
            || (this.agemin < t.getAgemax() && this.agemax >= t.getAgemax()));
  }
  
}
