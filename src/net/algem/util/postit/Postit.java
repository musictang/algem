/*
 * @(#)Postit.java	2.9.4.13 05/11/15
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
package net.algem.util.postit;

import net.algem.planning.DateFr;

/**
 * Postit.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Postit
        implements java.io.Serializable
{

  private static final long serialVersionUID = -4295824851261772842L;
  
  private int id;
  
  /** Type : notes/urgent. */
  private int type;

  private int issuer;
  
  /** Receiver : private / public. */
  private int receiver;

  private DateFr day;
  
  /** Term. */
  private DateFr term;
  
  private String text;

  @Override
  public String toString() {
    return id + " " + day + " " + receiver + " " + text;
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public int getType() {
    return type;
  }

  public void setType(int i) {
    type = i;
  }

  public int getReceiver() {
    return receiver;
  }

  public void setReceiver(int i) {
    receiver = i;
  }

  public int getIssuer() {
    return issuer;
  }

  public void setIssuer(int i) {
    issuer = i;
  }

  public DateFr getDay() {
    return day;
  }

  public void setDay(DateFr d) {
    day = d;
  }

  public DateFr getTerm() {
    return term;
  }

  public void setTerm(DateFr d) {
    term = d;
  }

  public String getText() {
    return text;
  }

  public void setText(String t) {
    text = t;
  }
  
}
