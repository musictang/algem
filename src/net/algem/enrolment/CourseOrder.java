/*
 * @(#)CourseOrder.java	2.9.4.13 05/11/15
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
package net.algem.enrolment;

import net.algem.course.CourseModuleInfo;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;

/**
 * Course order entity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class CourseOrder
        implements java.io.Serializable
{

  private static final long serialVersionUID = -4012019492419193410L;
  
  private int id;
  private int idcmd;
  private int moduleOrder;
  private int action;
  private DateFr startDate;
  private DateFr endDate;
  private Hour start;
  private Hour end;
  private String title;
  private int code;
  private int day;
  private int room;
  private int estab;
  private CourseModuleInfo courseModuleInfo;
  private int module;

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CourseOrder other = (CourseOrder) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.idcmd != other.idcmd) {
      return false;
    }
    if (this.moduleOrder != other.moduleOrder) {
      return false;
    }
    if (this.action != other.action) {
      return false;
    }
    if (this.start != other.start && (this.start == null || !this.start.equals(other.start))) {
      return false;
    }
    if (this.end != other.end && (this.end == null || !this.end.equals(other.end))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + this.id;
    hash = 59 * hash + this.idcmd;
    hash = 59 * hash + this.moduleOrder;
    hash = 59 * hash + this.action;
    hash = 59 * hash + (this.start != null ? this.start.hashCode() : 0);
    hash = 59 * hash + (this.end != null ? this.end.hashCode() : 0);
    return hash;
  }

//  public boolean equals(CourseOrder d) {
//    return (d != null
//            && idcmd == d.idcmd
//            && moduleOrder == d.moduleOrder
//            && action == d.action
//            && start.equals(d.start)
//            && end.equals(d.end));
//  }

  @Override
  public String toString() {
    return idcmd + " " + moduleOrder + " " + action + " " + start + " " + end + " " + startDate + " " + endDate;
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

  public int getModuleOrder() {
    return moduleOrder;
  }

  public void setModuleOrder(int i) {
    moduleOrder = i;
  }

  public int getModule() {
    return module;
  }

  public void setModule(int module) {
    this.module = module;
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
  
  public int getTimeLength() {
    return start.getLength(end);
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

  public int getCode() {
    return code;
  }

  public void setCode(int c) {
    code = c;
  }

  public int getDay() {
    return (int) day;
  }

  public void setDay(int j) {
    day = (short) j;
  }

  public int getEstab() {
    return estab;
  }

  public void setEstab(int estab) {
    this.estab = estab;
  }

  public CourseModuleInfo getCourseModuleInfo() {
    return courseModuleInfo;
  }

  public void setCourseModuleInfo(CourseModuleInfo moduleInfo) {
    this.courseModuleInfo = moduleInfo;
    if (moduleInfo != null) {
      this.code = moduleInfo.getIdCode();
    }
  }

}
