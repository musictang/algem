/*
 * @(#)SubstituteTeacher.java	2.7.a 03/12/12
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
package net.algem.contact.teacher;

import net.algem.contact.Person;
import net.algem.course.Course;

/**
 * Substitute teacher.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class SubstituteTeacher
        implements java.io.Serializable
{

  private int establishment;
  private Course course;
  private Person teacher;
  private Person substitute;
  private String days;
  private boolean _favorite;

  //public static String[] JOURS_SEMAINE = {"lundi","mardi","mercredi","jeudi","vendredi","samedi","dimanche"};
  public SubstituteTeacher() {
  }

  public SubstituteTeacher(int estab, Course c, Person teacher, Person substitute) {
    establishment = estab;
    course = c;
    this.teacher = teacher;
    this.substitute = substitute;
  }

  public SubstituteTeacher(int estab, Course c, Person teacher, Person substitute, String days, boolean favorite) {
    this(estab, c, teacher, substitute);
    this.days = days;
    _favorite = favorite;
  }

  /**
   * @return the _etablissement
   */
  public int getEstablishment() {
    return establishment;
  }

  /**
   * @param e the _etablissement to set
   */
  public void setEstablishment(int e) {
    this.establishment = e;
  }

  /**
   * @return a person
   */
  public Person getTeacher() {
    return teacher;
  }

  /**
   * @param t the teacher to set
   */
  public void setTeacher(Person t) {
    this.teacher = t;
  }

  public Person getSubstitute() {
    return substitute;
  }

  public void setSubstitute(Person subs) {
    this.substitute = subs;
  }

  /**
   * @return the _idcours
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @param courseId the courseId to set
   */
  public void setCourse(Course courseId) {
    this.course = courseId;
  }

  public String getDays() {
    return days;
  }

  /**
   * @param days the days to set
   */
  public void setDays(String days) {
    this.days = days;
  }

  /**
   * Convert a string in boolean array.
   * Days are listed from monday to sunday.
   *
   * @return a boolean array
   */
  public boolean[] daysToArray() {
    if (days == null) {
      setDays("0000000");
    }
    boolean[] db = new boolean[7];
    for (int i = 0; i < days.length(); i++) {
      db[i] = (days.charAt(i) == '0') ? false : true;
    }
    return db;
  }

  public boolean isFavorite() {
    return _favorite;
  }

  public void setFavorite(boolean favorite) {
    _favorite = favorite;
  }

  @Override
  public boolean equals(Object o) {
    if (o.getClass() != getClass() || o == null) {
      return false;
    }
    SubstituteTeacher other = (SubstituteTeacher) o;
    return other.getEstablishment() == getEstablishment()
            && other.getCourse().getId() == getCourse().getId()
            && other.getTeacher().getId() == getTeacher().getId()
            && other.getSubstitute().getId() == getSubstitute().getId()
            && other.getDays().equals(getDays());
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + this.establishment;
    hash = 79 * hash + (this.course != null ? this.course.hashCode() : 0);
    hash = 79 * hash + (this.teacher != null ? this.teacher.hashCode() : 0);
    hash = 79 * hash + (this.substitute != null ? this.substitute.hashCode() : 0);
    hash = 79 * hash + (this.days != null ? this.days.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return getSubstitute().toString() + (isFavorite() ? "*" : "");
  }
}
