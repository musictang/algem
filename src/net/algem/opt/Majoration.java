/*
 * @(#)Majoration.java	1.0a 07/07/1999
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
package net.algem.opt;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class Majoration
        implements java.io.Serializable
{

  int id;
  String mode;
  int pcent;

  public Majoration() {
  }

  public Majoration(int i, String m, int p) {
    id = i;
    mode = m;
    pcent = p;
  }

  @Override
  public String toString() {
    return mode + " " + pcent + "%";
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public int getPCent() {
    return pcent;
  }

  public void setPCent(int p) {
    pcent = p;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String m) {
    mode = m;
  }
}
