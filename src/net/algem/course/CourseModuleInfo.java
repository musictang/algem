/*
 * @(#)CourseModuleInfo.java	2.9.4.13 03/11/15
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

package net.algem.course;

import net.algem.config.GemParam;
import net.algem.planning.DateFr;
import net.algem.util.model.GemModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.8.a 13/03/2013
 */
public class CourseModuleInfo
  implements GemModel
{
  /** Min length in minutes. */
  public static final int MIN_LENGTH = 10;

  /** Max length in hours. */
  public static final int MAX_LENGTH = 24;
  
  private static final long serialVersionUID = -8268780716470396857L;

  private int id;
  private int idModule;
  private int timeLength;
  private GemParam code;
  private DateFr date;

  public CourseModuleInfo() {
  }

  public int getIdCode() {
    return code == null ? 0  : code.getId();
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public int getIdModule() {
    return idModule;
  }

  public void setIdModule(int idModule) {
    this.idModule = idModule;
  }

  public int getTimeLength() {
    return timeLength;
  }

  public void setTimeLength(int timeLength) {
    this.timeLength = timeLength;
  }

  public boolean hasValidLength() {
    if (code == null) {
      return false;
    }
    if (code.getId() != CourseCodeType.ATP.getId()
      &&  code.getId() != CourseCodeType.STG.getId()
      && timeLength < MIN_LENGTH) {
      return false;
    }
    if (timeLength > (MAX_LENGTH * 60)) {
      return false;
    }
    return true;
  }

  public GemParam getCode() {
    return code;
  }

  public void setCode(GemParam code) {
    this.code = code;
  }

  public DateFr getDate() {
    return date;
  }

  public void setDate(DateFr date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return code + " : " + timeLength;
  }

}
