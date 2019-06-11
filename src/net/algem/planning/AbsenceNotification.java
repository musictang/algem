/*
 * @(#)AbsenceNotification.java	2.8.x.2 18/09/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

/**
 * Absence notification handler.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.x.2
 * @since 2.8.w 01/09/14
 */
public class AbsenceNotification {

  private boolean absence;
  private boolean replacement;
  private String note;
  
  public AbsenceNotification() {
  }

  public boolean isAbsenceMemo() {
    return absence;
  }

  public void setAbsence(boolean absence) {
    this.absence = absence;
  }

  public boolean isReplacement() {
    return replacement;
  }

  public void setReplacement(boolean replacement) {
    this.replacement = replacement;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
  
}
