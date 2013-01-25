/*
 * @(#)Note.java	2.6.a 17/09/12
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
 * comment.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class Note
        implements java.io.Serializable
{

  private int id;
  private int idper;
  private String text;
  private short ptype;

  public Note() {
  }


  public Note(String t) {
    text = t;
  }

  public Note(int _idper, String t, short ptype) {
    this(t);
    this.idper = _idper;
    this.ptype = ptype;
  }

  public Note(int id, int _idper, String t, short ptype) {
    this(t);
    this.id = id;
    this.idper = _idper;
    this.ptype = ptype;
  }

  @Override
  public String toString() {
    return id + " " + text + " "+ptype;
  }

  public boolean equals(Note n) {
    return text.equals(n.text);
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public int getIdPer() {
    return idper;
  }

  public void setIdPer(int i) {
    idper = i;
  }

  public String getText() {
    return text;
  }

  public void setText(String l) {
    text = l;
  }

  public short getPtype() {
    return ptype;
  }

  public void setPtype(short ptype) {
    this.ptype = ptype;
  }

}
