/*
 * @(#)Module.java	 2.6.a 05/10/12
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
package net.algem.course;

import java.util.List;
import net.algem.util.model.GemModel;

/**
 * A module is a combination of courses.
 * It is a specific course agencement with a price and rates for month or
 * quarter reductions.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.a
 */
public class Module
        implements GemModel {

  private int id;
  private String code;
  private String title;
  private double basePrice;
  private double monthReducRate;
  private double quarterReducRate;
  private List<CourseModuleInfo> courses;

  public Module() {
  }

  public Module(int i) {
    id = i;
    code = "";
    title = "";
  }

  public Module(String s) {
    id = 0;
    code = s;
    title = "";
  }

  @Override
  public String toString() {
    return title + " [" + code + "]";//
  }

  public boolean equals(Module f) {
    return (f != null
            && id == f.id);
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  @Override
  public int getId() {
    return id;
  }

  public void setTitle(String s) {
    title = s;
  }

  public String getTitle() {
    return title;
  }

  public void setCode(String c) {
    code = c;
  }

  public String getCode() {
    return code;
  }

  /**
   * De type professionnel.
   * @return vrai si le code du cours débute par "P"
   */
  public boolean isProfessional() {
    return code.startsWith("P");
  }

  /**
   * Specifies if this module is of leisure type.
   * @return true if code starts with "L"
   */
  public boolean isLeisure() {
    return code.startsWith("L");
  }

  /**
   * With rehearsal.
   * @return true if code ends with "R"
   */
  public boolean withRehearsal() {
    return code.endsWith("R");
  }

  /** 
   * With group accompaniment.
   * @return true if code ends with "A"
   */
  public boolean withGroupAccompaniment() {
    return code.endsWith("A");
  }

  /**
   * With single workshop (discovery workshop)
   * 
   * @return true if character 8 of code = "1"
   */
  public boolean withSelectiveWorkshop() {
    return code.substring(7, 8).equals("1");
  }

  /**
   * With musical formation.
   * @return true if character 9 of code =  "1"
   */
  public boolean withMusicalFormation() {
    return code.substring(8, 9).equals("1");
  }

  /**
   * Course instrument duration.
   * @return a duration in minutes
   */
  public int getInstrumentDuration() {
    int d = 0;
    try {
      d = Integer.parseInt(code.substring(1, 4));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    return d;
  }

  /**
   * Workshop duration.
   * @return a duration in minutes
   */
  public int getWorkshopDuration() {
    int duree = 0;
    try {
      duree = Integer.parseInt(code.substring(4, 7));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    return duree;
  }

  /**
   * Base price.
   * This price is for a month.
   *
   * @param p
   */
  public void setBasePrice(double p) {
    basePrice = p;
  }

  /**
   *
   * @return a double
   */
  public double getBasePrice() {
    return basePrice;
  }

  /**
   * Sets the reduction rate for month debiting.
   *
   * @param p a rate in percent
   */
  public void setMonthReducRate(double p) {
    monthReducRate = p;
  }

  /**
   * Gets the reduction rate for month debiting.
   *
   * @return a rate in percent
   */
  public double getMonthReducRate() {
    return monthReducRate;
  }

  /**
   * Sets the reduction rate for quarter debiting.
   *
   * @param p a rate in percent
   */
  public void setQuarterReducRate(double p) {
    quarterReducRate = p;
  }

  /**
   * Gets the reduction rate for quarter debiting.
   *
   * @return a rate in percent
   */
  public double getQuarterReducRate() {
    return quarterReducRate;
  }

  public List<CourseModuleInfo> getCourses() {
    return courses;
  }

  public void setCourses(List<CourseModuleInfo> courses) {
    this.courses = courses;
  }
  
  

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Module f = (Module) o;
    return getId() == f.getId();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + (this.code != null ? this.code.hashCode() : 0);
    return hash;
  }
}
