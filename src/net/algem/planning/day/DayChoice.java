/*
 * @(#)DayChoice.java	2.8.j 12/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import net.algem.planning.PlanningService;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.j
 */
public class DayChoice
        extends javax.swing.JComboBox
{

  static final String[] dayLabel = PlanningService.WEEK_DAYS;

  /**
   * Days are numbered from 1 (sunday) to (saturday) 7.
   * dayLabel[0] is empty.
   */
  public DayChoice() {
    addItem(dayLabel[2]); // du lundi
    addItem(dayLabel[3]);
    addItem(dayLabel[4]);
    addItem(dayLabel[5]);
    addItem(dayLabel[6]);
    addItem(dayLabel[7]);
    addItem(dayLabel[1]); // au dimanche
  }

//  /**
//   * Gets day of week.
//   * @return an integer
//   */
//  public int getDay() {
//    int i = getSelectedIndex();
//    if (i == 6) {
//      return 0;
//    }
//    return i + 1; // si lundi retourne 1, si dimanche retourne 0
//  }


//  public void setDay(int i) {
//    setSelectedIndex(i - 1);
//  }

  @Override
  public int getSelectedIndex() {
    int i = super.getSelectedIndex();
    if (i == 6) {
      return 0;
    }
    return i + 1; // si lundi retourne 1, si dimanche retourne 0
  }
  
  @Override
  public void setSelectedIndex(int i) {
    if (i == 0) {
      super.setSelectedIndex(6);
    } else {
      super.setSelectedIndex(i-1);
    }
  }
}
