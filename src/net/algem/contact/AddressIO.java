/*
 * @(#)AddressIO.java	2.13.0 29/03/2017
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
 *
 */
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.Address}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 */
public class AddressIO
        extends TableIO {

  public static final String TABLE = "adresse";
  public static final int ADR1_LIMIT = 50;

  public static void insert(Address a, DataConnection dc) throws SQLException {
    assert(a != null && a.getAdr1() != null);
    String query = "INSERT INTO " + TABLE + " VALUES("
            + a.getId()
            + ",'" + escape(a.getAdr1())
            + "','" + (a.getAdr2() == null ? "" : escape(a.getAdr2()))
            + "','" + (a.getCdp() == null ? "" : escape(a.getCdp()))
            + "','" + (a.getCity() == null ? "" : escape(a.getCity().toUpperCase()))
            + "','" + (a.isArchive() ? "t" : "f")
            + "')";

    dc.executeUpdate(query);
    if (a.getCdp() != null && a.getCity() != null) {
      if (CityIO.findCity(a.getCdp(), dc) == null) {
        City v = new City(a.getCdp(), a.getCity().toUpperCase());
        CityIO.insert(v, dc);
      }
    }
  }

  public static void update(Address a, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "adr1='" + escape(a.getAdr1())
            + "',adr2='" + (a.getAdr2() == null ? "" : escape(a.getAdr2()))
            + "',cdp='" + (a.getCdp() == null ? "" : escape(a.getCdp()))
            + "',ville='" + (a.getCity() == null ? "" : escape(a.getCity().toUpperCase()))
            + "',archive='" + (a.isArchive() ? "t" : "f")
            + "'";
    query += " WHERE idper=" + a.getId();

    dc.executeUpdate(query);
  }

  public static void delete(int idper, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper=" + idper;
    dc.executeUpdate(query);
  }

  public static void delete(Address a, DataConnection dc) throws SQLException {
    delete(a.getId(), dc);
  }

  public static Vector<Address> findId(int id, DataConnection dc) throws SQLException {
    String query = "WHERE idper = " + id;
    return find(query, dc);
  }

  public static Vector<Address> findId(String id, DataConnection dc) throws SQLException {
    String query = "WHERE idper = " + id;
    return find(query, dc);
  }

  public static Vector<Address> find(String where, DataConnection dc) throws SQLException {
    Vector<Address> v = new Vector<Address>();
    String query = "SELECT oid,idper,adr1,adr2,cdp,ville,archive FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    for (int i = 0; rs.next(); i++) {
      Address a = new Address();
      a.setOID(rs.getInt(1));
      a.setId(rs.getInt(2));
      String adr1 = rs.getString(3);
      a.setAdr1(adr1 != null ? adr1.trim() : null);
      String adr2 = rs.getString(4);
      a.setAdr2(adr2 != null ? adr2.trim() : null);
      String cdp = rs.getString(5);
      a.setCdp(cdp != null ? cdp.trim() : null);
      String ville = rs.getString(6);
      a.setCity(ville != null ? ville.trim() : null);
      a.setArchive(rs.getBoolean(7));
      v.addElement(a);
    }
    rs.close();
    return v;
  }
}
