/*
 * @(#)ConfigUtil.java 2.8.w 17/07/14
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

package net.algem.config;

import java.sql.SQLException;
import net.algem.room.DailyTimes;
import net.algem.room.RoomService;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class ConfigUtil {

  private static final DataConnection dc = DataCache.getDataConnection();

  public static String getConf(String key) {
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

  public static String getPath(ConfigKey key) {
    return getConf(key.getKey());
  }

  public static String getExportPath() {
    return getConf(ConfigKey.EXPORT_PATH.getKey());
  }

	public static String getStartOfPeriod() {
    return getConf(ConfigKey.BEGINNING_PERIOD.getKey());
  }

	public static String getEndOfPeriod() {
    return getConf(ConfigKey.END_PERIOD.getKey());
  }

	public static String getStartOfYear() {
    return getConf(ConfigKey.BEGINNING_YEAR.getKey());
  }

	public static String getEndOfYear() {
    return getConf(ConfigKey.END_YEAR.getKey());
  }

  public static DailyTimes [] getTimes(int roomId){
    return new RoomService(dc).findDailyTimes(roomId);
  }
}
