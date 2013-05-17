/*
 * @(#)ItemIO.java 2.7.a 17/01/13
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
package net.algem.billing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.config.Param;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * Item persistence.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.3.a 22/12/11
 */
public class ItemIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "article";
  public static final String SEQUENCE = "article_id_seq";
  public static final String COLUMNS = "id, designation, prix_u, id_tva, compte, standard";
  public static final String TVA_TABLE = "tva";
  private DataConnection dc;

  public ItemIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(Item i) throws SQLException {

    int n = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES (" + n
            + ",'" + escape(i.getDesignation())
            + "'," + i.getPrice()
            + ",'" + i.getVat().getKey()
            + "','" + i.getAccount()
            + "', " + i.isStandard() + ")";
    dc.executeUpdate(query);
    i.setId(n);
  }

  public Item findId(int id) throws SQLException {
    Item n = null;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      n = new Item(rs.getInt(1), unEscape(rs.getString(2)), rs.getDouble(3), rs.getInt(5), rs.getBoolean(6));
      n.setVat(getVat(rs.getInt(4)));
    }
    return n;
  }

  public Vector<Item> find(String where) throws SQLException {

    Vector<Item> v = new Vector<Item>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Item a = new Item(rs.getInt(1), unEscape(rs.getString(2)), rs.getDouble(3), rs.getInt(5), rs.getBoolean(6));
      a.setVat(getVat(rs.getInt(4)));
      v.add(a);
    }
    return v;
  }

  public void update(Item i) throws SQLException {

    String query = "UPDATE " + TABLE + " SET designation = '" + escape(i.getDesignation()) + "'"
            + ", prix_u = " + i.getPrice()
            + ", id_tva = '" + i.getVat().getKey() + "'"
            + ", compte = '" + i.getAccount() + "'"
            + ", standard = " + i.isStandard()
            + " WHERE id = " + i.getId();
    dc.executeUpdate(query);
  }

  public void delete(Item a) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + a.getId();
    dc.executeUpdate(query);
  }

  private Param getVat(int idVat) throws SQLException {
//    return ParamTableIO.findBy(TVA_TABLE, " WHERE id = " + idVat, dc);
    return (Vat) DataCache.findId(idVat, Model.Vat);
  }

  @Override
  public List<Item> load() throws SQLException {
    return find("");
  }
}
