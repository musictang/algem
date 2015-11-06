/*
 * @(#)Action.java	2.9.4.13 05/11/15
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
package net.algem.planning;

import java.util.List;
import net.algem.config.AgeRange;
import net.algem.config.GemParam;
import net.algem.contact.Note;
import net.algem.util.model.GemModel;

/**
 * An action ensures the grouping of several schedule objects.
 * Each schedule of the group has the same action id. It facilitates the
 * modification and the suppression of schedules and the recording of members
 * on planning.
 * An action is composed also of specific parameters (status, level, age range, etc.).
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class Action
        implements GemModel {

  private static final long serialVersionUID = -2440481206020812924L;
  
  private int id;
  private int courseId;
  private short nsessions;
  private DateFr dateStart;
  private DateFr dateEnd;
  private Hour hourStart;
  private Hour hourEnd;
  private int idper;
  private int roomId;
  private short day;
  private int vacancy;
  private int color = 0;
  private boolean resetDefaultColor;
  private int memberId;
  private Periodicity periodicity;
  private Note note;

  /** Length in minutes. */
  private int length;

  private GemParam level;

  private GemParam status;

  /** Number of places. */
  private short places;

  private AgeRange tage;
  private List<DateFr> dates;

  public Action() {
  }

  public Action(Action a) {
    this.courseId = a.getCourse();
    this.dateStart = a.getDateStart();
    this.dateEnd = a.getDateEnd();
    this.idper = a.getIdper();
    this.roomId = a.getRoom();
    this.day = (short) a.getDay();
    this.nsessions = a.getNSessions();
    this.vacancy = a.getVacancy();
    this.periodicity = a.getPeriodicity();
    this.places = a.getPlaces();
  }

  public Action(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public int getCourse() {
    return courseId;
  }

  public void setCourse(int i) {
    courseId = i;
  }

  public short getNSessions() {
    return nsessions;
  }

  public void setNSessions(short n) {
    nsessions = n;
  }

  public int getIdper() {
    return idper;
  }

  public void setIdper(int i) {
    idper = i;
  }

  public int getRoom() {
    return roomId;
  }

  public void setRoom(int i) {
    roomId = i;
  }

  public Hour getHourStart() {
    return hourStart;
  }

  public void setHourStart(Hour h) {
    hourStart = h;
  }

  public Hour getHourEnd() {
    return hourEnd;
  }

  public void setHourEnd(Hour h) {
    hourEnd = h;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public DateFr getDateStart() {
    return dateStart;
  }

  public void setDateStart(DateFr d) {
    dateStart = d;
  }

  public DateFr getDateEnd() {
    return dateEnd;
  }

  public void setDateEnd(DateFr d) {
    dateEnd = d;
  }

  public int getDay() {
    return (int) day;
  }

  public void setDay(int j) {
    day = (short) j;
  }

  public int getVacancy() {
    return vacancy;
  }

  public void setVacancy(int vacance) {
    this.vacancy = vacance;
  }

  public Periodicity getPeriodicity() {
    return periodicity;
  }

  public void setPeriodicity(Periodicity p) {
    this.periodicity = p;
  }
  
  public GemParam getLevel() {
    return level;
  }

  public void setLevel(GemParam level) {
    this.level = level;
  }

  public GemParam getStatus() {
    return status;
  }

  public void setStatus(GemParam status) {
    this.status = status;
  }

  public short getPlaces() {
    return places;
  }

  public void setPlaces(short places) {
    this.places = places;
  }

  public AgeRange getAgeRange() {
    return tage;
  }

  public void setAgeRange(AgeRange tage) {
    this.tage = tage;
  }

  public int getColor() {
    return color;
  }

  /**
   * Black color offset.
   * Color 0 represents null color or no change and must not be used to set pure black.
   * @param color 
   */
  public void setColor(int color) {
    this.color = color == 0 ? -1 : color;
  }
  
  /**
   * Switchs to null color.
   */
  public void resetColor() {
    resetDefaultColor = true;
  }
  
  boolean hasResetDefaultColor() {
    return resetDefaultColor;
  }
  
  public List<DateFr> getDates() {
    return dates;
  }

  public void setDates(List<DateFr> dates) {
    this.dates = dates;
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  @Override
  public String toString() {
    return id + " " + hourStart + " " + hourEnd + " " + idper + " " + memberId + " " + roomId;
  }

  public String getCodeLabel() {
    StringBuilder c = new StringBuilder();
    String s = status.getId() == 0 ? null : " " + status.getLabel();
    String n = level.getId() == 0 ? null : " " + level.getLabel();
    String t = tage.getId() == 0 ? null : " " + tage.getLabel();
    if (s != null) {
      c.append(s);
    }
    if (n != null) {
      c.append(n);
    }
    if (t != null) {
      c.append(t);
    }
    return c.toString();
  }
  
}
