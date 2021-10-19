/*
 * @(#)DayChoice.java	2.9.4.0 06/04/15
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
package net.algem.planning.day;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import net.algem.planning.DayOfWeek;
import net.algem.planning.PlanningService;
import net.algem.util.ui.GemChoice;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.0
 */
public class DayChoice
        extends GemChoice
{

  static final String[] dayLabel = new DateFormatSymbols().getWeekdays();

  /**
   * Days are numbered from 1 (sunday) to (saturday) 7.
   * dayLabel[0] is empty.
   */
  public DayChoice() {
     addItem(new DayOfWeek(Calendar.MONDAY, dayLabel[Calendar.MONDAY]));
     addItem(new DayOfWeek(Calendar.TUESDAY, dayLabel[Calendar.TUESDAY]));
     addItem(new DayOfWeek(Calendar.WEDNESDAY, dayLabel[Calendar.WEDNESDAY]));
     addItem(new DayOfWeek(Calendar.THURSDAY, dayLabel[Calendar.THURSDAY]));
     addItem(new DayOfWeek(Calendar.FRIDAY, dayLabel[Calendar.FRIDAY]));
     addItem(new DayOfWeek(Calendar.SATURDAY, dayLabel[Calendar.SATURDAY]));
     addItem(new DayOfWeek(Calendar.SUNDAY, dayLabel[Calendar.SUNDAY]));
  }

  @Override
  public int getKey() {
    return ((DayOfWeek) getSelectedItem()).getIndex();
  }

  @Override
  public void setKey(int k) {
    switch (k) {
      case Calendar.MONDAY:
        setSelectedIndex(0);
        break;
      case Calendar.TUESDAY:
        setSelectedIndex(1);
        break;
      case Calendar.WEDNESDAY:
        setSelectedIndex(2);
        break;
      case Calendar.THURSDAY:
        setSelectedIndex(3);
        break;
      case Calendar.FRIDAY:
        setSelectedIndex(4);
        break;
      case Calendar.SATURDAY:
        setSelectedIndex(5);
        break;
      case Calendar.SUNDAY:
        setSelectedIndex(6);
        break;
    }
  }
}
