/*
 * @(#) VatIO.java Algem 2.14.0 08/06/2017
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
 */
package net.algem.billing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.accounting.Account;
import net.algem.accounting.AccountIO;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.14.0 07/06/2017
 */
public class VatIO
  extends TableIO
  implements Cacheable
{

  private static final String TABLE = "tva";
  private static final String SEQUENCE = "tva_id_seq";
  DataConnection dc;

  public VatIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(Vat tax) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES(?,?,?)";

    int id = nextId(SEQUENCE, dc);

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, nextId(SEQUENCE, dc));
      ps.setFloat(2, Float.parseFloat(tax.getKey()));
      if (tax.getAccount() != null) {
        ps.setInt(3, tax.getAccount().getId());
      } else {
        ps.setNull(3, java.sql.Types.INTEGER);
      }

      ps.executeUpdate();
      tax.setId(id);
    }

  }

  public void update(Vat tax) throws SQLException {
    if (tax.getId() > 1) {
      String query = "UPDATE " + TABLE + " SET pourcentage = ?,compte=? WHERE id = ?";
      try (PreparedStatement ps = dc.prepareStatement(query)) {
        ps.setFloat(1, tax.getRate());
        if (tax.getAccount() != null) {
          ps.setInt(2, tax.getAccount().getId());
        } else {
          ps.setNull(2, java.sql.Types.INTEGER);
        }
        ps.setInt(3, tax.getId());

        ps.executeUpdate();
      }
    }
  }

  public void delete(int id) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  @Override
  public List<Vat> load() throws SQLException {
    String query = "SELECT id,pourcentage,compte FROM " + TABLE + " ORDER BY pourcentage";
    List<Vat> taxes = new ArrayList<>();
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          taxes.add(getResultFromRS(rs));
        }
      }
    }

    return taxes;
  }

  public Vat findId(int id) throws SQLException {
    String query = "SELECT id,pourcentage,compte FROM " + TABLE + " WHERE id = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          return getResultFromRS(rs);
        }
      }
    }
    return new Vat(1,"0.0",null);
  }

  private Vat getResultFromRS(ResultSet rs) throws SQLException {
    Vat t = new Vat();
    t.setId(rs.getInt(1));
    t.setKey(rs.getString(2));
    Account a = AccountIO.find(rs.getInt(3), false, dc);
    t.setAccount(a);

    return t;
  }

}
