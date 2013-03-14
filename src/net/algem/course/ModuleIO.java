/*
 * @(#)ModuleIO.java 2.7.a 07/01/13
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.course.Module}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class ModuleIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "module";
  public static final String SEQUENCE = "idmodule";
  public static final int TITLE_MAX_LEN = 40;
  private DataConnection dc;

  public ModuleIO(DataConnection dc) {
    this.dc = dc;
  }
  
  public void insert(Module m) throws SQLException {

    int n = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + n
            + "','" + m.getCode()
            + "','" + escape(m.getTitle())
            + "'," + m.getBasePrice()
            + "," + m.getMonthReducRate()
            + "," + m.getQuarterReducRate()
            + ",0" //+m.getUnite()
            + ")";

    int rsn = dc.executeUpdate(query);
    m.setId(n);
  }

  public void update(Module m) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "titre = '" + escape(m.getTitle())
            + "',code = '" + m.getCode()
            + "',prix_base = " + m.getBasePrice()
            + ",taux_mensuel = " + m.getMonthReducRate()
            + ",taux_trim = " + m.getQuarterReducRate()
            + ",unite = 0" //+m.getUnite()
            + " WHERE id = " + m.getId();

    dc.executeUpdate(query);
  }

  public void delete(Module m) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + m.getId();
    dc.executeUpdate(query);
  }

  /**
   * Recherche d'un forfait par son id.
   *
   * @param n l'id du module
   * @return le premier forfait trouvé
   */
  public Module findId(int n) throws SQLException {
    String query = "WHERE id = " + n;
    Vector<Module> v = find(query);
    if (v != null && v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public Vector<Module> find(String where) throws SQLException {
    Vector<Module> v = new Vector<Module>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Module m = new Module();
      m.setId(rs.getInt(1));
      //System.out.println("id forfait :"+rs.getInt(1));//
      m.setCode(rs.getString(2).trim());
      m.setTitle(rs.getString(3));
      m.setBasePrice(rs.getDouble(4)); // prixinst
      m.setMonthReducRate(rs.getDouble(5)); // taux de réduction prélèvement mensuel
      m.setQuarterReducRate(rs.getDouble(6)); // taux de réduction prélèvement trimestriel
      
      m.setCourses(findCourses(m.getId()));
      
      v.addElement(m);
    }
    rs.close();
    return v;
  }
  
  private List<CourseModuleInfo> findCourses(int module) throws SQLException {
    List<CourseModuleInfo> courses = new ArrayList<CourseModuleInfo>();
    String query = "SELECT * FROM module_cours WHERE idmodule = " + module;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      CourseModuleInfo info = new CourseModuleInfo();
      info.setId(rs.getShort(1));
      info.setIdModule(module);
      info.setCode(rs.getInt(3));
      info.setTimeLength(rs.getInt(4));
      
      courses.add(info);
    }
    return courses;
  }

  @Override
  public List<Module> load() throws SQLException {
    return find("ORDER BY titre");
  }
}
