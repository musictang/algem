/*
 * @(#)CoursSchedulePrintDetail.java	2.17.0 15/05/19
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
package net.algem.planning;

import java.time.format.TextStyle;
import java.util.Locale;
import net.algem.course.Course;
import net.algem.util.model.GemModel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 2.17.0
 */
public class CourseSchedulePrintDetail
        extends CourseSchedule
        implements GemModel {

  private int dow;
  
  public CourseSchedulePrintDetail() {
  }

  public CourseSchedulePrintDetail(Schedule d) {
    super(d);
  }

    public int getDow() {
        return dow;
    }

    public void setDow(int dow) {
        this.dow = dow;
    }

    public String getDowLabel() {
        return java.time.DayOfWeek.of(dow).getDisplayName(TextStyle.FULL, Locale.FRANCE);
    }
  @Override
  public String toString() {
    return ((Course) activity).getTitle()+" / "+getDowLabel()+" "+start;
  }

}
