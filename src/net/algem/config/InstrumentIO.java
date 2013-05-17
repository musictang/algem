/*
 * @(#)InstrumentIO.java	2.8.a 15/03/13
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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Io methods for class {@link net.algem.config.Instrument}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 */
public class InstrumentIO
        extends TableIO {

  public static final String TABLE = "instrument";
  public static final String PERSON_INSTRUMENT_TABLE = "person_instrument";
  
  public static final String SEQUENCE = "idinstrument";


  public static void insert(Instrument i, DataConnection dc) throws SQLException {
    int id = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + id
            + "','" + i.getName()
            + "')";

    dc.executeUpdate(query);
    i.setId(id);
  }

  public static void update(Instrument i, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET nom = '" + i.getName() + "' WHERE id = " + i.getId();
    dc.executeUpdate(query);
  }

  public static void delete(Instrument i, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + i.getId();
    dc.executeUpdate(query);
  }

  public static Instrument findId(String n, DataConnection dc) throws SQLException {
    String query = "WHERE id = " + n;
    Vector<Instrument> v = find(query, dc);
    if (v.size() > 0) {
      return (Instrument) v.elementAt(0);
    }
    return null;
  }

  public static Vector<Instrument> find(String where, DataConnection dc) throws SQLException {
    Vector<Instrument> v = new Vector<Instrument>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Instrument i = new Instrument();
      i.setId(rs.getInt(1));
      i.setName(rs.getString(2).trim());
      v.addElement(i);
    }
    return v;
  }
  
  public static List<Integer> find(int idper, int ptype, DataConnection dc) throws SQLException {
    List<Integer> li = new ArrayList<Integer>();
    String query = "SELECT instrument FROM " + PERSON_INSTRUMENT_TABLE + " WHERE idper = " + idper + " AND ptype = " + ptype;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      li.add(rs.getInt(1));
    }
    rs.close();
    return li.isEmpty() ? null : li;
  }
  
  public static Hashtable<Integer, List<Integer>> load(DataConnection dc) throws SQLException {
    
    Hashtable<Integer, List<Integer>> h = new Hashtable<Integer, List<Integer>>();
    String query = "SELECT idper,array_agg(instrument) FROM " + PERSON_INSTRUMENT_TABLE + " WHERE ptype = " + Instrument.TEACHER + " GROUP BY idper";
    ResultSet rs = dc.executeQuery(query);
    
    while (rs.next()) {  
      Array a = rs.getArray(2);
      Integer [] ins = (Integer[])a.getArray();
      h.put(rs.getInt(1), Arrays.asList(ins));
    }
    return h;
  }
  
  public static void insert(List<Integer> instruments, int idper, int ptype, DataConnection dc) throws SQLException {
    if (instruments == null) {
      return;
    }
    for(int i = 0 ; i < instruments.size(); i++) {
      String query = "INSERT INTO " + PERSON_INSTRUMENT_TABLE + " VALUES(DEFAULT," + i + "," + idper + "," + instruments.get(i) + "," + ptype + ")";
      dc.executeUpdate(query);
    }
  }
  
  public static void delete(int idper, int ptype, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + PERSON_INSTRUMENT_TABLE + " WHERE idper = " + idper + " AND ptype = " + ptype;
    dc.executeUpdate(query);
  }
  
}
