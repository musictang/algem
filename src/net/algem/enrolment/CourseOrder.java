/*
 * @(#)CourseOrder.java	2.6.a 17/09/12
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
package net.algem.enrolment;

import net.algem.planning.DateFr;
import net.algem.planning.Hour;

/**
 * Course order entity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CourseOrder
        implements java.io.Serializable
{

  private int id;
  private int idcmd;
  private int module;
  private int action;
  private DateFr startDate;
  private DateFr endDate;
  private Hour start;
  private Hour end;
  private String title;
  private String code;
  private int day;
  private int room;

  public boolean equals(CourseOrder d) {
    return (d != null
            && idcmd == d.idcmd
            && module == d.module
            && action == d.action
            && start.equals(d.start)
            && end.equals(d.end));
  }

  @Override
  public String toString() {
    return idcmd + " " + module + " " + action + " " + start + " " + end + " " + startDate + " " + endDate;
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  /**
   * Récupère le numéro de commande.
   * 
   * @return un entier 
   */
  public int getIdOrder() {
    return idcmd;
  }

  public void setIdOrder(int i) {
    idcmd = i;
  }

  public int getModule() {
    return module;
  }

  public void setModule(int i) {
    module = i;
  }

  public void setAction(int a) {
    action = a;
  }

  public int getAction() {
    return action;
  }

  public void setStart(Hour h) {
    start = h;
  }

  public Hour getStart() {
    return start;
  }

  public void setEnd(Hour h) {
    end = h;
  }

  public Hour getEnd() {
    return end;
  }

  public void setDateStart(DateFr h) {
    startDate = h;
  }

  public DateFr getDateStart() {
    return startDate;
  }

  public void setDateEnd(DateFr h) {
    endDate = h;
  }

  public DateFr getDateEnd() {
    return endDate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String t) {
    title = t;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String c) {
    code = c;
  }

  public int getDay() {
    return (int) day;
  }

  public void setDay(int j) {
    day = (short) j;
  }

  /**
   * @return an integer
   * @since 1.1da bug inscription
   */
  public int getRoom() {
    return room;
  }

  /**
   * 
   * @param _room
   * @since 1.1da bug inscription
   */
  public void setRoom(int _room) {
    room = _room;
  }
}
