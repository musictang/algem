/*
 * @(#)RoomTimesIO.java	2.17.2 27/10/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.algem.planning.Hour;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;

/**
 * Daily times DAO.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.2
 * @since 2.8.w 16/07/14
 */
public class RoomTimesIO
{

  public static final String TABLE = "horaires";

  private DataConnection dc;

  public RoomTimesIO(DataConnection dc) {
    this.dc = dc;
  }

  public Hour getOpeningTime(int roomId, int dow) throws SQLException {
    String query = "SELECT ouverture FROM " + TABLE + " WHERE idsalle = " + roomId + " AND jour = " + dow;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      return new Hour(rs.getString(1));
    }
    return null;
  }

  public Hour getClosingTime(int roomId, int dow) throws SQLException {
    String query = "SELECT fermeture FROM " + TABLE + " WHERE idsalle = " + roomId + " AND jour = " + dow;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      return new Hour(rs.getString(1));
    }
    return null;
  }

  public DailyTimes[] find(int roomId) throws SQLException {
    String query = "SELECT jour, ouverture, fermeture FROM " + TABLE + " WHERE idsalle = " + roomId + " ORDER BY jour";
    ResultSet rs = dc.executeQuery(query);
    List<DailyTimes> times = new ArrayList<DailyTimes>();
    while (rs.next()) {
      DailyTimes dt = new DailyTimes(rs.getInt(1));
      dt.setOpening(new Hour(rs.getString(2)));
      dt.setClosing(new Hour(rs.getString(3)));
      times.add(dt);
    }
    DailyTimes[] timesArray = new DailyTimes[7];
    if (times.size() < 7) {
      for (int i = 0 ; i < 7 ; i++) {
        DailyTimes dt = new DailyTimes(i+1);
        dt.setOpening(new Hour("00:00"));
        dt.setClosing(new Hour("24:00"));
        timesArray[i] = dt;
      }
    } else {
      timesArray = times.toArray(timesArray);
    }
    return timesArray;
  }

  public void update(int roomId, DailyTimes[] times) throws SQLException {
    try {
      dc.setAutoCommit(false);
      String query = "DELETE FROM " + TABLE + " WHERE idsalle = " + roomId;
      dc.executeUpdate(query);
      for (DailyTimes dt : times) {
        query = "INSERT INTO " + TABLE + " VALUES(" + roomId + "," + dt.getDow() + ", '" + dt.getOpening() + "', '" + dt.getClosing() + "')";
        dc.executeUpdate(query);
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      GemLogger.log(sqe.getMessage());
      throw sqe;
    } finally {
      dc.setAutoCommit(true);
    }
  }

      public static HashMap<Integer, DailyTimes[]> loadDailyTimes(List rooms) {   //ERIC 2.17 27/03/2019
        HashMap<Integer, DailyTimes[]> roomsTimes = new HashMap<>();
        for (int i = 0; i < rooms.size(); i++) {
            Room r = (Room) rooms.get(i);
            DailyTimes[] timesArray = new DailyTimes[7];
            for (int j = 0; j < 7; j++) {
                DailyTimes dt = new DailyTimes(j + 1);
                dt.setOpening(new Hour("00:00"));
                dt.setClosing(new Hour("24:00"));
                timesArray[j] = dt;
            }
            roomsTimes.put(r.getId(), timesArray);
        }
        return roomsTimes;
    }

    public static HashMap<Integer, DailyTimes[]> findDailyTimes() {   //ERIC 2.17 27/03/2019
        String query = "SELECT idsalle, jour, ouverture, fermeture FROM horaires ORDER BY idsalle, jour";
        System.out.println("RoomIO.findDailyTimes query=" + query);
        HashMap<Integer, DailyTimes[]> roomsTimes = new HashMap<>();

        //TODO a faire
        //voir RoomService.findDailyTimes()

        System.out.println("RoomIO.findDailyTimes rooms size=" + roomsTimes.size());
        return roomsTimes;
    }


}
