/*
 * @(#)Param.java	2.8.a 01/04/13
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
package net.algem.config;

import net.algem.util.model.GemModel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.a
 * @since 1.0a 07/07/1999
 */
public class Param
        implements GemModel
{

  protected int id;
  protected String key;
  protected String value;

  public Param() {
  }

  public Param(String _key, String _value) {
    key = _key;
    value = _value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String _key) {
    key = _key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String _value) {
    value = _value;
  }

  @Override
  public String toString() {
    return value;
  }

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
    hash = 37 * hash + (this.key != null ? this.key.hashCode() : 0);
    hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
    return hash;
  }

  @Override
  public int getId() {
    int i = 0;
    try {
      i = Integer.parseInt(key);
    } catch(NumberFormatException ex) {
       
    }
    return i;
  }

  @Override
  public void setId(int id) {
    setKey(String.valueOf(id));
  }
  
}
