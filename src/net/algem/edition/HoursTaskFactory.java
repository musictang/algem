/*
 * @(#) HoursTaskFactory.java Algem 2.11.5 25/01/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */
package net.algem.edition;

import net.algem.util.GemLogger;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.5
 * @since 2.10.0 03/06/2016
 */
public class HoursTaskFactory {

  public static HoursTaskExecutor getInstance() {
    try {
      Class c = Class.forName("net.algem.plugins.WorkingTimePlugin");
      return (HoursTaskExecutor) c.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      GemLogger.logException(ex);
    } catch (ClassNotFoundException ex) {
      GemLogger.log(ex.getMessage());
    }
    return null;
  }
}
