/*
 * @(#)StatisticsFactory.java	2.10.0 07/06/2016
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
package net.algem.edition;

import java.util.logging.Level;
import net.algem.util.GemLogger;

/**
 * Factory for statistics plugin class.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.6.a 11/10/12
 */
public class StatisticsFactory
{

  /**
   * Gets the right stats' instance for the organization.
   * The class StatisticsPlugin contains specific requests and methods for the organization.
   * StatisticsPlugin must extends {@link net.algem.edition.Statistics }
   * and must be located under {@code net.algem.plugins path}.
   * A jar file (generally named Statistics.jar) must be present in classpath.
   * Generally, this jar is placed under lib directory on algem install's path.
   * @return a statistics instance
   * @see net.algem.edition.Statistics
   * @see net.algem.edition.StatisticsDefault
   */
  public static Statistics getInstance() {
    try {
      Class c = Class.forName("net.algem.plugins.StatisticsPlugin");
      return (Statistics) c.newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
      GemLogger.log(Level.SEVERE, ex.getMessage());
    }
    return null;
  }

}
