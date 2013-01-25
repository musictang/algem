/*
 * @(#)TableSGBD.java	2.6.a 24/09/12
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
package net.algem.util.model;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TableSGBD
        implements java.io.Serializable
{

  private String name;
  private String owner;
  private int ntuples;
  private String index;
  private String type;
  private int natts;

  public TableSGBD() {
  }

  @Override
  public String toString() {
    return name + " " + owner + " " + ntuples;
  }

  public void setNTuples(int i) {
    ntuples = i;
  }

  public int getNTuples() {
    return ntuples;
  }

  public void setName(String s) {
    name = s;
  }

  public String getName() {
    return name;
  }

  public void setOwner(String s) {
    owner = s;
  }

  public String getOwner() {
    return owner;
  }

  public void setType(String s) {
    type = s;
  }

  public String getType() {
    return type;
  }

  public void setIndex(String s) {
    index = s;
  }

  public String getIndex() {
    return index;
  }

  public void setNAtts(int i) {
    natts = i;
  }

  public int getNAtts() {
    return natts;
  }
}
