/*
 * @(#)DayChoice.java	2.6.a 21/09/12
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
package net.algem.planning.day;

import net.algem.planning.PlanningService;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 */
public class DayChoice
        extends javax.swing.JComboBox
{

  static final String[] dayLabel = PlanningService.WEEK_DAYS;

  /**
   * Les jours sont numérotés de 1 (dimanche) à (samedi) 7 pour la Locale.FRANCE.
   * dayLabel[0] est vide.
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

  public int getDay() {
    int i = super.getSelectedIndex();
    if (i == 6) {
      return 0;
    }
    return i + 1; // si lundi retourne 1, si dimanche retourne 7
  }

  public void setDay(int i) {
    super.setSelectedIndex(i - 1);
  }
}
