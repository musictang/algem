/*
 * @(#)ModuleOrderIO.java	2.6.a 17/09/12
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
package net.algem.enrolment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.course.ModuleIO;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.ModuleOrder}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ModuleOrderIO
        extends TableIO
{

  public static final String TABLE = "commande_module";

  public static void insert(ModuleOrder c, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + c.getId()
            + "','" + c.getModule()
            //+"','"+c.getPrice()
            + "','" + (int) (c.getPrice() * 100)
            + "','" + c.getStart()
            + "','" + c.getEnd()
            + "','" + c.getModeOfPayment()
            + "','" + c.getNOrderLines()
            + "','" + c.getPayment()
            + "')";
    dc.executeUpdate(query);
  }

  public static void update(ModuleOrder c, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "module = '" + c.getModule()
            //+"',prix='"+c.getPrice()
            //+"',prix='"+(int) (c.getPrice() * 100)//probleme si le prix n'a pas changé
            + "',debut = '" + c.getStart()
            + "',fin = '" + c.getEnd()
            + "',reglement = '" + c.getModeOfPayment()
            + "',necheance = '" + c.getNOrderLines()
            + "',paiement = '" + c.getPayment()
            + "'"
            + " WHERE oid = " + c.getOID();

    dc.executeUpdate(query);
  }

  public static void delete(int cmd, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idcmd=" + cmd;
    dc.executeUpdate(query);
  }

  public static Vector<ModuleOrder> findId(int n, DataConnection dc) throws SQLException {
    String query = " AND cm.idcmd=" + n;
    return find(query, dc);
  }

  public static Vector<ModuleOrder> find(String where, DataConnection dc) throws SQLException {
    Vector<ModuleOrder> v = new Vector<ModuleOrder>();
    String query = "SELECT cm.oid, cm.idcmd, cm.module, cm.prix, cm.debut, cm.fin, cm.reglement, cm.necheance, cm.paiement"
            + ", m.titre"
            + " FROM " + TABLE + " cm, " + ModuleIO.TABLE + "  m"
            + " WHERE cm.module = m.id " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ModuleOrder c = new ModuleOrder();
      c.setOID(rs.getInt(1));
      c.setId(rs.getInt(2));
      c.setModule(rs.getInt(3));
      c.setPrice(rs.getInt(4));
      c.setStart(new DateFr(rs.getString(5)));
      c.setEnd(new DateFr(rs.getString(6)));
      c.setModeOfPayment(rs.getString(7));
      c.setNOrderLines(rs.getInt(8));
      c.setPayment(rs.getString(9));
      c.setTitle(rs.getString(10));

      v.addElement(c);
    }
    rs.close();
    return v;
  }
}
