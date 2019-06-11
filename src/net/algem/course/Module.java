/*
 * @(#)Module.java	 2.13.1 17/04/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.13.1
 */
public class Module
        implements GemModel {

  private static final long serialVersionUID = 2788857764816449695L;

  private int id;
  private String code;
  private String title;
  private double basePrice;
  private double monthReducRate;
  private double quarterReducRate;
  private double yearReducRate;
  private List<CourseModuleInfo> courses;
  private boolean active;

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
    return "[" + code + "] " + title;
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
   * Specifies if this module is of type "professional".
   * @return true if code starts with "P"
   */
  public boolean isProfessional() {
    return code.startsWith("P");
  }

  /**
   * Specifies if this module is of type "leisure".
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
   * With group support.
   * @return true if code ends with "A"
   */
  public boolean withGroupAccompaniment() {
    return code.endsWith("A");
  }

  /**
   * Base price.
   * By default, This price is set for a quarter.
   *
   * @param p
   */
  public void setBasePrice(double p) {
    basePrice = p;
  }

  /**
   * Gets the base price.
   * @return a double
   */
  public double getBasePrice() {
    return basePrice;
  }

  /**
   * Sets the reduction rate for month debiting.
   * Reduction is applied only if the mode of payment is direct debit.
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
   * Reduction is applied only if the mode of payment is direct debit.
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

  /**
   * Gets the reduction rate for year debiting.
   *
   * @return a rate in percent
   */
  public double getYearReducRate() {
    return yearReducRate;
  }

  public void setYearReducRate(double yearReducRate) {
    this.yearReducRate = yearReducRate;
  }

  public List<CourseModuleInfo> getCourses() {
    return courses;
  }

  public void setCourses(List<CourseModuleInfo> courses) {
    this.courses = courses;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Module m = (Module) o;
    return getId() == m.getId();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + (this.code != null ? this.code.hashCode() : 0);
    return hash;
  }

}
