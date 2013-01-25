/*
 * @(#)GemParam.java 2.7.a 09/01/13
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

package net.algem.config;

import net.algem.util.model.GemModel;

/**
 * General parameter implementing GemModel interface.
 * A param is a couple of key,value.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.5.a 05/07/12

 */
public class GemParam 
  extends Param
  implements GemModel
 {

  protected int id;
  public final static String NONE = "-";
  
  public GemParam() {
    
  }

  public GemParam(Param p) {
    try {
      this.id = Integer.parseInt(p.getKey());
    } catch(NumberFormatException ne) {
      this.id = 0;
    }
    this.key = p.getKey();
    this.value = p.getValue();
  }
  
  public GemParam(int id) {
    this.id = id;
  }
  
  public GemParam(int id, String code, String label) {
    this.id = id;
    this.key = code;
    this.value = label;
  }
  
  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }
  
  public String getCode() {
    return super.getKey();
  }
  
  public void setCode(String code) {
    super.setKey(code);
  }
  
  public String getLabel() {
    return super.getValue();
  }
  
  public void setLabel(String label) {
    super.setValue(label);
  }
  
   @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Param o) {
    return (o != null
            && key.equals(o.key)
            && value.equals(o.value));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Param)) {
      return false;
    }
    final Param other = (Param) obj;
    if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
      return false;
    }
    if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 83 * hash + this.id;
    return hash;
  }
  
  
  public boolean isUndefined() {
    return key == null || NONE.equals(key);
  }

}
