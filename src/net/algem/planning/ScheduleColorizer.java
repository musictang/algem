/*
 * @(#)ScheduleColorizer.java 2.9.4.13 02/11/2015
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.planning;

import java.awt.Color;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 02/11/2015
 */
public interface ScheduleColorizer {

  /**
   * Gets the default background color.
   *
   * @param p schedule
   * @return a color
   */
  Color getDefaultScheduleColor(ScheduleObject p);

  /**
   * Gets the background color.
   *
   * @param p schedule
   * @return a color
   */
  Color getScheduleColor(ScheduleObject p);

  /**
   * Gets the foreground color.
   *
   * @param p schedule
   * @return a color
   */
  Color getTextColor(ScheduleObject p);

}
