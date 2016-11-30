/*
 * @(#)PlanificationUtil.java	2.11.3 30/11/16
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

import java.util.ArrayList;
import java.util.List;
import net.algem.config.ConfigUtil;
import net.algem.room.DailyTimes;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 * @since 2.8.v 29/05/14
 */
public class PlanificationUtil
{

  static boolean hasOverlapping(List<GemDateTime> orig) {
    List<GemDateTime> dup = new ArrayList<GemDateTime>(orig);
    for (int i = 0; i < orig.size(); i++) {
      DateFr d = orig.get(i).getDate();
      HourRange h = orig.get(i).getTimeRange();
      for(int j = 0; j < dup.size(); j++) {
        if (j != i && dup.get(j).getDate().equals(d)) {
          if (dup.get(j).getTimeRange().overlap(h.getStart(), h.getEnd())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Checks if room is closed at date {@code date} or closed at time {@code h}.
   * @param room room id
   * @param date start date
   * @param h time
   * @return null if room is free, null time if room is closed
   * or opening/closing time if selected time before/after opening/closing time
   */
  public static Hour isRoomClosed(int room, DateFr date, Hour h) {
    DailyTimes [] times = ConfigUtil.getTimes(room);
    int dow = date.getDayOfWeek();

    Hour first = times[dow -1].getOpening();
    Hour last = times[dow -1].getClosing();
    if (first.equals(last)) {
      return new Hour();
    }
    if (h.before(first)) {
      return first;
    } else if (h.after(last)) {
      return last;
    } else {
      return null;
    }
  }

}
