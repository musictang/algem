/*
 * @(#)AgeRangeIO.java 2.7.n 22/03/13
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
 */
package net.algem.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.n
 * @since 2.3.a
 */
public class AgeRangeIO 
  extends TableIO 
  implements Cacheable
{

  public final static String TABLE = "trancheage";
  public final static String SEQUENCE = "trancheage_id_seq";

  private DataConnection dc;
  
  public AgeRangeIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(AgeRange t) throws AgeRangeException {
    try {
      int numero = TableIO.nextId(SEQUENCE, dc);

      String query = "INSERT INTO " + TABLE + " VALUES("
              + "'" + numero
              + "','" + t.getAgemin()
              + "','" + t.getAgemax()
              + "','" + escape(t.getLabel().trim())
              + "','" + t.getCode()
              + "')";


      dc.executeUpdate(query);
      t.setId(numero);
    } catch (SQLException e) {
      throw new AgeRangeException(MessageUtil.getMessage("age.range.create.exception") + "\n" + e.getMessage(), e);
    }
  }

  public void update(AgeRange t) throws AgeRangeException {
    String query = "UPDATE " + TABLE + " SET"
            + " agemin = '" + t.getAgemin()
            + "', agemax = '" + t.getAgemax()
            + "', nom = '" + escape(t.getLabel().trim())
            + "', code = '" + t.getCode()
            + "'";
    query += " WHERE id = " + t.getId();
    try {
      dc.executeUpdate(query);
    } catch (SQLException e) {
      throw new AgeRangeException(MessageUtil.getMessage("age.range.update.exception") + "\n" + e.getMessage());
    }
  }

  public boolean delete(int id) throws AgeRangeException {
    if (id == 0) {
      return false;
    }
    try {
      String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
      dc.executeUpdate(query);
    } catch (SQLException e) {
      throw new AgeRangeException(MessageUtil.getMessage("age.range.delete.exception") + "\n" + e.getMessage(), e);
    }

    return true;
  }

  public AgeRange findId(int n) throws SQLException {
    String query = "WHERE id = " + n;
    Vector<AgeRange> v = find(query);
    if (v != null && v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public Vector<AgeRange> find(String where) throws SQLException {

    Vector<AgeRange> v = new Vector<AgeRange>();
    String query = "SELECT * FROM " + TABLE + " " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      AgeRange t = new AgeRange();
      t.setId(rs.getInt(1));
      t.setAgemin(rs.getInt(2));
      t.setAgemax(rs.getInt(3));
      t.setLabel(unEscape(rs.getString(4).trim()));
      t.setCode(rs.getString(5));

      v.addElement(t);
    }

    return v;
  }

  /**
   * 
   * @param ar
   * @return a range
   * @throws AgeRangeException 
   * @deprecated
   * It's preferable to not verify overlapping.
   * We might have for exemple 2 ranges : 10-12 et 8-15.
   */
  public AgeRange check(AgeRange ar) throws AgeRangeException {

    try {
      String query = "";
      if (ar.getId() != 0) {
        query += " WHERE id <> " + ar.getId();
      }
			for (Iterator<AgeRange> it = find(query).iterator(); it.hasNext();) {
				AgeRange t = it.next();
				if (ar.intersect(t)) {
					return t;
				}
			}
      return null;
    } catch (SQLException e) {
      throw new AgeRangeException(MessageUtil.getMessage("age.range.searchinter.exception") + "\n" + e.getMessage(), e);
    }
  }

  @Override
  public List<AgeRange> load() throws SQLException {
    return find("ORDER BY agemin");
  }
}
