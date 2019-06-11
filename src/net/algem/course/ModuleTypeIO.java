/*
 * @(#)ModuleTypeIO.java	2.6.a 03/08/12
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
package net.algem.course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.course.ModuleType}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class ModuleTypeIO
        extends TableIO
{

  public static final String TABLE = "module_type";
  public static final String SEQUENCE = "idmoduletype";

  /**
   * 
   * @param m
   * @param dc
   * @throws SQLException 
   * @deprecated 
   */
  public static void insert(ModuleType m, DataConnection dc) throws SQLException {

    int numero = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + numero
            + "','" + m.getCode()
            + "'," + m.getLabel()
            + "')";

    dc.executeUpdate(query);
    m.setId(numero);
  }

  /**
   * Mise à jour du module_type.
   * @param m le module_tyep
   * @param dc dataCache
   * @throws SQLException
   * @deprecated 
   */
  public static void update(ModuleType m, DataConnection dc) throws SQLException {
   /* A priori, on ne devrait pouvoir updater que le libellé.
    Un changement de code implique de
    changer les codes de tous les cours associés à ce type. */
    String query = "UPDATE " + TABLE + " SET "
            + "code='" + m.getCode()
            + "',libelle='" + m.getLabel()
            + "'";
    query += " WHERE id=" + m.getId();

    dc.executeUpdate(query);
  }

  /**
   * 
   * @param m
   * @param dc
   * @throws SQLException
   * @deprecated 
   */
  public static void delete(ModuleType m, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id=" + m.getId();
    dc.executeUpdate(query);
  }

  /**
   * 
   * @param where
   * @param dc connection
   * @return a list of moduleType
   * @throws SQLException
   * @deprecated 
   */
  public static Vector<ModuleType> find(String where, DataConnection dc) throws SQLException {
    Vector<ModuleType> v = new Vector<ModuleType>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ModuleType m = new ModuleType();
      m.setId(rs.getInt(1));
      m.setCode(rs.getString(2).trim());
      m.setLabel(rs.getString(3));

      v.addElement(m);
    }
    rs.close();
    return v;
  }
}
