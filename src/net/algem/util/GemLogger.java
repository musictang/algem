/*
 * @(#)GemLogger.java	2.6.a 03/08/12
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
package net.algem.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.algem.util.ui.SQLErrorDlg;

/**
 * Utility class for logging.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 30/07/2012
 */
public class GemLogger {
  
	public static final Logger LOGGER = Logger.getLogger("net.algem");
	private static final int MAX_SIZE = 50000000;// en octects (50M)

    public GemLogger() {
    }

	public static void set(String path) throws IOException {
		LOGGER.setLevel(Level.ALL);
		Handler fh = new FileHandler(path, MAX_SIZE, 1, true);
		fh.setFormatter(new GemLogFormatter());
		LOGGER.addHandler(fh);
	}

	public static void log(Level level, String msg) {
		LOGGER.log(level, msg);
	}

	public static void log(String msg) {
		LOGGER.warning(msg); 
	}
 
    public static void log(String sourceClass, String sourceMethod, String msg) {
		LOGGER.logp(Level.SEVERE, sourceClass, sourceMethod, msg);
	}

	public static void log(Level level, String sourceClass, String sourceMethod, String msg) {
		LOGGER.logp(level, sourceClass, sourceMethod, msg);
	}

	public static void log(String sourceClass, String sourceMethod, Throwable thrown) {
		LOGGER.throwing(sourceClass, sourceMethod, thrown);
	}

	public static void logException(Exception e) {
		logException(null, e);
	}

	public static void logException(String msg, Exception e) {
		logException(msg, e, null);
	}

	public static void logException(String msg, Exception e, java.awt.Component parent) {
        LOGGER.log(Level.SEVERE, msg, e);
		if (e instanceof SQLException) {
			/*log(null, null, e);
			SQLException sqle = (SQLException) e;
			while (sqle != null) {
				LOGGER.log(Level.SEVERE, "  Error code: {0}", sqle.getErrorCode());
				LOGGER.log(Level.SEVERE, "  SQLState  : {0}", sqle.getSQLState());
				LOGGER.log(Level.SEVERE, "  Message   : {0}", sqle.getMessage());

				sqle = sqle.getNextException();
			}*/
			if (parent != null) {
				new SQLErrorDlg(parent, e, msg);
			}
		} /*else {
            if (e.getMessage() != null) {
			log(e.getMessage());
            }
		}*/

	}
	
	public static void info(String msg) {
		LOGGER.log(Level.INFO, msg);
	}
}
