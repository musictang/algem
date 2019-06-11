/*
 * @(#)RoomRateIO.java	2.9.4.13 05/10/2015
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class RoomRateIO
        implements Cacheable
{

  private static final String TABLE = "tarifsalle";
  private final static String COLUMNS = "id, libelle, type, hc, hp, plafond, forfaithc, forfaithp";
  private final static String SEQUENCE = "tarifsalle_id_seq";
  private DataConnection dc;

  public RoomRateIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(RoomRate t) throws SQLException {

    int nextid = TableIO.nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES(" + nextid
            + ",'" + t.getLabel()
            + "','" + t.getType().name()
            + "'," + t.getOffpeakRate()
            + "," + t.getFullRate()
            + "," + t.getMax()
            + "," + t.getPassOffPeakPrice()
            + "," + t.getPassFullPrice()
            + ")";
    dc.executeUpdate(query);
    t.setId(nextid);
  }

  public void update(RoomRate t) throws SQLException {
    String query = "UPDATE " + TABLE + " SET libelle = '" + t.getLabel()
            + "',  type = '" + t.getType().name()
            + "', hc = " + t.getOffpeakRate()
            + ", hp = " + t.getFullRate()
            + ", plafond = " + t.getMax()
            + ", forfaithc = " + t.getPassOffPeakPrice()
            + ", forfaithp = " + t.getPassFullPrice()
            + " WHERE id = " + t.getId();
    dc.executeUpdate(query);
  }

  public boolean delete(int id) {
    if (id <= 1) {
      return false;
    }
    try {
      dc.setAutoCommit(false);
      // déréférencement en premier
      String query = "UPDATE " + RoomIO.TABLE + " SET idtarif = 1 WHERE idtarif = " + id;
      dc.executeUpdate(query);
      // puis suppression
      query = "DELETE FROM " + TABLE + " WHERE id = " + id;
      dc.executeUpdate(query);
      dc.commit();
    } catch (SQLException ex) {
      GemLogger.logException("Suppression tarif salle", ex);
      dc.rollback();
      return false;
    } finally {
      dc.setAutoCommit(true);
    }
    return true;
  }

  public RoomRate findId(int id) throws SQLException {
    RoomRate t = null;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      t = getRateFromRS(rs);
    }
    rs.close();
    return t;
  }

  public Vector<RoomRate> find(String where) throws SQLException {

    Vector<RoomRate> v = new Vector<RoomRate>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      v.addElement(getRateFromRS(rs));
    }
    rs.close();

    return v;

  }

  private RoomRate getRateFromRS(ResultSet rs) throws SQLException {
    RoomRate t = new RoomRate();

    t.setId(rs.getInt(1));
    t.setLabel(rs.getString(2).trim());
    //t.setType(rs.getString(3));
    t.setType(Enum.valueOf(RoomRateEnum.class, rs.getString(3)));
    t.setOffPeakRate(rs.getDouble(4));
    t.setFullRate(rs.getDouble(5));
    t.setMax(rs.getDouble(6));
    t.setPassOffPeakPrice(rs.getDouble(7));
    t.setPassFullPrice(rs.getDouble(8));

    return t;
  }

  @Override
  public List<RoomRate> load() throws SQLException {
    return find(" ORDER BY hp");
  }
}
