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

	public static void log(int type, String message) {
            log(type, DataCache.getInitializedInstance().getUser().toString(), message);
        }
        
	public static void log(int type, String user, String message) {
            System.out.println("JournalIO.log:"+message);
            DataConnection dc = DataCache.getDataConnection();
            try {
		int id = nextId(SEQUENCE, dc);
                PreparedStatement st = dc.prepareStatement("INSERT INTO " + TABLE +" (id, logdate, type, login, message) VALUES (?,?,?,?,?)");
                st.setInt(1, id);
                st.setObject(2, LocalDateTime.now());
                st.setInt(3, type);
                st.setString(4, user);
                st.setString(5, message);
                st.executeUpdate();
            } catch (Exception e) {
                GemLogger.log("JournalIO.log error");
            }
	}

}
