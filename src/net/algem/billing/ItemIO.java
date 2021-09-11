/*
 * @(#)ItemIO.java 2.14.0 07/06/17
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
package net.algem.billing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * Item persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
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
  public static final int MAX_LENGH = 128;
  private final DataConnection dc;

  public ItemIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(Item i) throws SQLException {

    int n = nextId(SEQUENCE, dc);
    String q = "INSERT INTO " + TABLE + " VALUES (?,?,?,?,?,?)";
    try (PreparedStatement ps = dc.prepareStatement(q)) {
      ps.setInt(1, n);
      ps.setString(2, i.getDesignation());
      ps.setDouble(3, i.getPrice());
      ps.setInt(4, i.getTax().getId());
      ps.setInt(5, i.getAccount());
      ps.setBoolean(6, i.isStandard());
      GemLogger.info(ps.toString());
      ps.executeUpdate();
      i.setId(n);
    }
  }

  public Item findId(int id) throws SQLException {
    Item n = null;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      n = new Item(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(5), rs.getBoolean(6));
      n.setTax(getVat(rs.getInt(4)));
    }
    return n;
  }

  public Vector<Item> find(String where) throws SQLException {

    Vector<Item> v = new Vector<Item>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Item a = new Item(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(5), rs.getBoolean(6));
      a.setTax(getVat(rs.getInt(4)));
      v.add(a);
    }
    return v;
  }

  public void update(Item i) throws SQLException {
    String q = "UPDATE " + TABLE + " SET designation = ?, prix_u = ?, id_tva = ?, compte = ?, standard = ? WHERE id = ?";
    try(PreparedStatement ps = dc.prepareStatement(q)) {
      ps.setString(1, i.getDesignation());
      ps.setDouble(2, i.getPrice());
      ps.setInt(3,i.getTax().getId());
      ps.setInt(4, i.getAccount());
      ps.setBoolean(5, i.isStandard());
      ps.setInt(6, i.getId());
      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }
  }

  public void delete(Item a) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + a.getId();
    dc.executeUpdate(query);
  }

  private Vat getVat(int taxId) throws SQLException {
    return (Vat) DataCache.findId(taxId, Model.Vat);
  }

  @Override
  public List<Item> load() throws SQLException {
    return find("");
  }
}
