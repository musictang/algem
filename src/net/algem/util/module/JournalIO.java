/*
 * @(#)JournalIO.java	3.0.0 14/10/2021
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
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
package net.algem.util.module;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import net.algem.security.User;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for table journal
 *
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 */
public class JournalIO
	extends TableIO {

	private static final String TABLE = "journal";
	private static final String SEQUENCE = "journal_id_seq";

        public final static int LOGIN = 1;
        public final static int ERROR = 2;
//        private final static PreparedStatement stmt = DataCache.getDataConnection().prepareStatement("INSERT INTO " + TABLE +" (logdate, type, login, message) VALUES (?,?,?,?,?)");

	public static void log(int type, String message) {
            if (DataCache.getInitializedInstance() == null)
                return;
            User u = DataCache.getInitializedInstance().getUser();
            log(type, u != null ? u.toString() : "unlogged", message);
        }
        
	public static void log(int type, String user, String message) {
            if (!DataCache.isDataConnected())
                return;
            DataConnection dc = DataCache.getDataConnection();
            try {
		//int id = nextId(SEQUENCE, dc);
                PreparedStatement stmt = dc.prepareStatement("INSERT INTO " + TABLE +" (logdate, type, login, message) VALUES (?,?,?,?)");
//                PreparedStatement stmt = dc.prepareStatement("INSERT INTO " + TABLE +" (id, logdate, type, login, message) VALUES (?,?,?,?,?)");
                //stmt.setInt(1, id);
                stmt.setObject(1, LocalDateTime.now());
                stmt.setInt(2, type);
                stmt.setString(3, user);
                stmt.setString(4, message);
                stmt.executeUpdate();
            } catch (Exception e) {
                System.err.println("JournalIO.log error");
            }
	}

}
