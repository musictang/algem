/*
 * @(#)FollowUp.java	2.11.0 16/09/16
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import net.algem.config.FollowUpStatus;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.11.0 16/09/16
 */
public class FollowUp 
        implements java.io.Serializable {

  private int id;
  private int scheduleId;
  private String content;
  private String note;
  private short status;
  private boolean collective;

  public FollowUp() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(int scheduleId) {
    this.scheduleId = scheduleId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public short getStatus() {
    return status;
  }

  public void setStatus(short status) {
    this.status = status;
  }

  public boolean isAbsent() {
    return FollowUpStatus.ABS.getId() == status;
  }

  public boolean isExcused() {
    return FollowUpStatus.EXC.getId() == status;
  }

  public boolean isCollective() {
    return collective;
  }

  public void setCollective(boolean collective) {
    this.collective = collective;
  }

  @Override
  public String toString() {
    //String st = (status > 0) ? getStatusFromResult(status).name() : "";
    //String n = (note != null && note.length() > 0) ? note + " " : "";
    return content;
  }


  public FollowUpStatus getStatusFromResult() {
    switch(status) {
      case 1:
        return FollowUpStatus.ABS;
      case 2:
        return FollowUpStatus.EXC;
      default:
        return FollowUpStatus.PRE;
    }
  }

}
