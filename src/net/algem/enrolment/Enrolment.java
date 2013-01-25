/*
 * @(#)Enrolment.java	2.6.a 17/09/12
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

import java.util.Vector;

/**
 * Enrolment object model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class Enrolment
        implements java.io.Serializable
{

  private Order c;
  private Vector<ModuleOrder> cmm;
  private Vector<CourseOrder> cmc;

  public Enrolment() {
  }

  public Enrolment(Order cmd) {
    c = cmd;
  }

  public Order getOrder() {
    return c;
  }

  public void setOrder(Order v) {
    c = v;
  }

  public Vector<ModuleOrder> getModule() {
    return cmm;
  }

  public void setModule(Vector<ModuleOrder> v) {
    cmm = v;
  }

  public Vector<CourseOrder> getCourseOrder() {
    return cmc;
  }

  public void setCourseOrder(Vector<CourseOrder> v) {
    cmc = v;
  }

  public int getId() {
    return c.getId();
  }

  public int getMember() {
    return c.getMember();
  }

  public int getPayer() {
    return c.getPayer();
  }

  public boolean isValid() {
    return true;
  }
  
  public boolean equals(Enrolment aa) {
    return (aa != null && c.equals(aa.c));
  }

  @Override
  public String toString() {
    return c.toString();
  }

}
