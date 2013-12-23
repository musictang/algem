/*
 * @(#)ConfigUtil.java 2.8.p 07/11/13
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

package net.algem.config;

import java.sql.SQLException;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 */
public class ConfigUtil {

  public static String getConf(String key, DataConnection dc) {
    try {
      Config c = ConfigIO.findId(key, dc);
      if (c != null) {
        return c.getValue();
      }
    } catch (SQLException ex) {
        GemLogger.logException(MessageUtil.getMessage("config.load.exception", key), ex);
    }
    return null;
  }

  public static String getPath(ConfigKey key, DataConnection dc) {
    return getConf(key.getKey(), dc);
  }
  
  public static String getExportPath(DataConnection dc) {
    return getConf(ConfigKey.EXPORT_PATH.getKey(), dc);
  }
	
	public static String getStartOfPeriod(DataConnection dc) {
    return getConf(ConfigKey.BEGINNING_PERIOD.getKey(), dc);
  }
	
	public static String getEndOfPeriod(DataConnection dc) {
    return getConf(ConfigKey.END_PERIOD.getKey(), dc);
  }
	
	public static String getStartOfYear(DataConnection dc) {
    return getConf(ConfigKey.BEGINNING_YEAR.getKey(), dc);
  }
	
	public static String getEndOfYear(DataConnection dc) {
    return getConf(ConfigKey.END_YEAR.getKey(), dc);
  }
}
