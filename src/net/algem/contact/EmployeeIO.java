/*
 * @(#)EmployeeIO.java 2.8.n 03/10/13
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

package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.8.m 02/09/13
 */
public class EmployeeIO
{
  public static final String TABLE = "salarie";
  public static final String COLUMNS = "idper,insee,datenais,lieunais,guso,nationalite";
  
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
            + "')";
    
    dc.executeUpdate(query);
    
  }
  
  public void update(Employee e) throws SQLException {
    String query = "UPDATE " + TABLE + " SET"
            + " insee = '" + TableIO.escape(e.getNir())
            + "', datenais = " + ((e.getDateBirth() == null) ? null : "'" + e.getDateBirth() + "'")
            + ", lieunais = '" + TableIO.escape(e.getPlaceBirth())
            + "', guso = '" + TableIO.escape(e.getGuso())
            + "', nationalite = '" + TableIO.escape(e.getNationality())
            + "' WHERE idper = " + e.getIdPer();

    dc.executeUpdate(query);
    
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
    }
    return e;
  }
 
  public void delete(int idper) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper;
    dc.executeUpdate(query);
  }

}
