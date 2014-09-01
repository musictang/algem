/*
 * @(#)ModuleOrderIO.java	2.8.w 23/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.8.a
 */
public class ModuleOrderIO
        extends TableIO
{

  public static final String TABLE = "commande_module";
  public static final String SEQUENCE = "commande_module_id_seq";

  public static void insert(ModuleOrder c, DataConnection dc) throws SQLException {
    int next = nextId(SEQUENCE, dc);
    
    String query = "INSERT INTO " + TABLE + " VALUES("
            + next
            + ",'" + c.getIdOrder()
            + "','" + c.getModule()
            //+"','"+c.getPrice()
            + "','" + (int) (c.getPrice() * 100)
            + "','" + c.getStart()
            + "','" + c.getEnd()
            + "','" + c.getModeOfPayment()
            + "','" + c.getNOrderLines()
            + "','" + c.getPayment().getName()
            + "')";
    dc.executeUpdate(query);
    c.setId(next);
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
            + "',paiement = '" + c.getPayment().getName()
            + "' WHERE id = " + c.getId();

    dc.executeUpdate(query);
  }

  public static void deleteByOrder(int order, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idcmd = " + order;
    dc.executeUpdate(query);
  }
  
  public static void delete(int moduleOrder, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + moduleOrder;
    dc.executeUpdate(query);
  }
  
  public static ModuleOrder findId(int id, DataConnection dc) throws SQLException {
    ModuleOrder mo = null;
    String query = "SELECT FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      mo = getFromRs(rs);
    }
    return mo;
  }

  public static Vector<ModuleOrder> findByIdOrder(int n, DataConnection dc) throws SQLException {
    String query = " AND cm.idcmd = " + n;
    return find(query, dc);
  }

  public static Vector<ModuleOrder> find(String where, DataConnection dc) throws SQLException {
    Vector<ModuleOrder> v = new Vector<ModuleOrder>();
    String query = "SELECT cm.id, cm.idcmd, cm.module, cm.prix, cm.debut, cm.fin, cm.reglement, cm.necheance, cm.paiement, m.titre"
            + " FROM " + TABLE + " cm, " + ModuleIO.TABLE + " m"
            + " WHERE cm.module = m.id " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ModuleOrder c = getFromRs(rs);
      v.addElement(c);
    }
    rs.close();
    return v;
  }
  
  private static ModuleOrder getFromRs(ResultSet rs) throws SQLException {

      ModuleOrder m = new ModuleOrder();
      m.setId(rs.getInt(1));
      m.setIdOrder(rs.getInt(2));
      m.setModule(rs.getInt(3));
      m.setPrice(rs.getInt(4));
      m.setStart(new DateFr(rs.getString(5)));
      m.setEnd(new DateFr(rs.getString(6)));
      m.setModeOfPayment(rs.getString(7));
      m.setNOrderLines(rs.getInt(8));
      m.setPayment(getFrequencyByName(rs.getString(9)));
      m.setTitle(rs.getString(10));
      
      return m;
  }
  
  public static PayFrequency getFrequencyByName(String f) {
    if (PayFrequency.MONTH.getName().equals(f)) {
      return PayFrequency.MONTH;
    } else if (PayFrequency.QUARTER.getName().equals(f)) {
      return PayFrequency.QUARTER;
    } else if (PayFrequency.SEMESTER.getName().equals(f)) {
      return PayFrequency.SEMESTER;
    } else if (PayFrequency.YEAR.getName().equals(f)) {
      return PayFrequency.YEAR;
    }
    return PayFrequency.QUARTER;//backward-compatible
  }
}
