/*
 * @(#)EmployeeIO.java 2.9.3 25/02/15
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

package net.algem.contact;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3
 * @since 2.8.m 02/09/13
 */
public class EmployeeIO
{
  public static final String TABLE = "salarie";
  public static final String TYPE_TABLE = "salarie_type";
  public static final String CAT_TABLE = "categorie_salarie";
  public static final String COLUMNS = "idper,insee,datenais,lieunais,guso,nationalite,sitfamiliale,enfants";
  
  private DataConnection dc;

  public EmployeeIO(DataConnection dc) {
    this.dc = dc;
  }
  
  public void insert(Employee e) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES(" + e.getIdPer()
            + ",'" + TableIO.escape(e.getNir())
            + "', " + ((e.getDateBirth() == null) ? null : "'" + e.getDateBirth() + "'")
            + ",'" + TableIO.escape(e.getPlaceBirth())
            + "','" + TableIO.escape(e.getGuso())
            + "','" + TableIO.escape(e.getNationality())
            + "', " + e.getMaritalStatus()
            + ", " + getBirthDateValues(e)
            + ")";

    dc.executeUpdate(query);
    updateType(e);

  }
  
  private String getBirthDateValues(Employee e) {
    if (e.getBirthDatesOfChildren() == null || e.getBirthDatesOfChildren().length == 0) {
      return "NULL";
    }
    StringBuilder sb = new StringBuilder("'{");
    for (Date d : e.getBirthDatesOfChildren()) {
      sb.append(new DateFr(d).toString()).append(',');
    }
    sb.replace(sb.length() - 1, sb.length(), "}'");
    return sb.toString();
  }
  
  public void update(Employee e) throws SQLException {
    String query = "UPDATE " + TABLE + " SET"
            + " insee = '" + TableIO.escape(e.getNir())
            + "', datenais = " + ((e.getDateBirth() == null) ? null : "'" + e.getDateBirth() + "'")
            + ", lieunais = '" + TableIO.escape(e.getPlaceBirth())
            + "', guso = '" + TableIO.escape(e.getGuso())
            + "', nationalite = '" + TableIO.escape(e.getNationality())
            + "', sitfamiliale = " + e.getMaritalStatus()
            + ", enfants = " + getBirthDateValues(e)
            + " WHERE idper = " + e.getIdPer();

    dc.executeUpdate(query);
    updateType(e);

  }
  
  private void updateType(Employee e) {
    List<Integer> types = e.getTypes();
    String query = null;
    try {
      if (types != null && types.size() > 0) {
        dc.setAutoCommit(false);
        query = "DELETE FROM " + TYPE_TABLE + " WHERE idper = " + e.getIdPer();
        dc.executeUpdate(query);
        for (int i = 0; i < types.size(); i++) {
          int t = types.get(i);
          query = "INSERT INTO " + TYPE_TABLE + " VALUES(" + e.getIdPer() + ", " + t + ", " + i + ")";
          dc.executeUpdate(query);
        }
      } else {
        query = "DELETE FROM " + TYPE_TABLE + " WHERE idper = " + e.getIdPer();
        dc.executeUpdate(query);
      }
      dc.commit();
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      dc.rollback();
    } finally {
      dc.setAutoCommit(true);
    }
  }
  
  public Employee findId(int idper) throws SQLException {
    String query = "SELECT * FROM "  + TABLE + " WHERE idper = " + idper;
    
    ResultSet rs = dc.executeQuery(query);
    Employee e = null;
    while (rs.next()) {
      e = new Employee(idper);
      String insee = TableIO.unEscape(rs.getString(2));
      e.setNir(insee == null ? null : insee.trim());
      Date d = rs.getDate(3);
      e.setDateBirth(d == null ? null : new DateFr(d));
      e.setPlaceBirth(TableIO.unEscape(rs.getString(4)));
      String guso = TableIO.unEscape(rs.getString(5));
      e.setGuso(guso == null ? null : guso.trim());
      e.setNationality(TableIO.unEscape(rs.getString(6)));
      e.setMaritalStatus(rs.getInt(7));
      Array dates = rs.getArray(8);
      if (dates != null) {
        e.setBirthDatesOfChildren((Date[]) rs.getArray(8).getArray());
      }
    }
    
    if (e != null) {
      query = "SELECT c.id FROM " + CAT_TABLE + " c, " + TYPE_TABLE + " t WHERE t.idper = " + idper + " AND t.idcat = c.id ORDER BY t.idx";
      rs = dc.executeQuery(query);
      List<Integer> types = new ArrayList<Integer>();
      while(rs.next()) {
        types.add(rs.getInt(1));
      }
      e.setTypes(types);
    }
    return e;
  }
 
  public void delete(int idper) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper;
    dc.executeUpdate(query);
  }

}
