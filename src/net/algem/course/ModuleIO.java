/*
 * @(#)ModuleIO.java 2.12.0 14/03/17
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
package net.algem.course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.algem.config.GemParam;
import net.algem.config.Preset;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.course.Module}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class ModuleIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "module";
  public static final String SEQUENCE = "idmodule";
  public static final int TITLE_MAX_LEN = 64;
  private static final String PRESET_SELECTION_TABLE = "module_preset";
  private static final String PRESET_SELECTION_SEQUENCE = "module_preset_id_seq";
  private DataConnection dc;

  public ModuleIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Inserts a new training module.
   * A new module is enabled by default.
   * @param m the module to insert
   * @throws SQLException
   */
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
    transInsert(m);
  }

  private void transInsert(Module m) throws SQLException {
    for (CourseModuleInfo cm : m.getCourses()) {
      cm.setIdModule(m.getId());
      String query = "INSERT INTO module_cours VALUES("
              + cm.getId()
              + ", " + cm.getIdModule()
              + ", " + cm.getCode().getId()
              + ", " + cm.getTimeLength()
              + ")";
      dc.executeUpdate(query);
    }
  }

  public void update(Module m) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "titre = '" + escape(m.getTitle())
            + "',code = '" + m.getCode()
            + "',prix_base = " + m.getBasePrice()
            + ",taux_mensuel = " + m.getMonthReducRate()
            + ",taux_trim = " + m.getQuarterReducRate()
            + ",unite = 0"
            + ",actif = " + m.isActive()//+m.getUnite()
            + " WHERE id = " + m.getId();

    dc.executeUpdate(query);
    query = "DELETE FROM module_cours WHERE idmodule = " + m.getId();
    dc.executeUpdate(query);
    transInsert(m);
  }

  public void delete(Module m) throws SQLException {
    //TODO verifier la non utilisation du module dans les commandes cours
    String query = "DELETE FROM " + TABLE + " WHERE id = " + m.getId();
    dc.executeUpdate(query);
    query = "DELETE FROM module_cours WHERE idmodule = " + m.getId();
    dc.executeUpdate(query);
  }

  /**
   * Finds a module by its id.
   *
   * @param n module id
   * @return the first module found
   * @throws java.sql.SQLException
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
    String query = "SELECT " + TABLE + ".* FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Module m = new Module();
      m.setId(rs.getInt(1));
      m.setCode(rs.getString(2).trim());
      m.setTitle(rs.getString(3));
      m.setBasePrice(rs.getDouble(4)); // prixinst
      m.setMonthReducRate(rs.getDouble(5)); // taux de réduction prélèvement mensuel
      m.setQuarterReducRate(rs.getDouble(6)); // taux de réduction prélèvement trimestriel
      m.setActive(rs.getBoolean(8));

      m.setCourses(findCourses(m.getId()));

      v.addElement(m);
    }
    rs.close();
    return v;
  }

  private List<CourseModuleInfo> findCourses(int module) throws SQLException {
//    String query = "SELECT * FROM module_cours WHERE idmodule = " + module;
    String query = "SELECT id,code,duree FROM module_cours WHERE idmodule = ?";
    PreparedStatement ps = dc.prepareStatement(query);
    ps.setInt(1, module);
//    ResultSet rs = dc.executeQuery(query);
    ResultSet rs = ps.executeQuery();
    List<CourseModuleInfo> courses = new ArrayList<CourseModuleInfo>();
    while (rs.next()) {
      CourseModuleInfo info = new CourseModuleInfo();
      info.setId(rs.getShort(1));
      info.setIdModule(module);
      info.setCode((GemParam) DataCache.findId(rs.getInt(2), Model.CourseCode));
      info.setTimeLength(rs.getInt(3));

      courses.add(info);
    }
    return courses;
  }

  /**
   * Find all associations between modules and course code.
   * @param code course code
   * @return the number of associations found
   * @throws SQLException
   */
  public int haveCode(int code) throws SQLException {
    String query = "SELECT count(*) FROM module_cours WHERE code = " + code;
    int count = 0;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      count = rs.getInt(1);
    }
    return count;
  }

  @Override
  public List<Module> load() throws SQLException {
    return find("ORDER BY code,titre");
  }

  public List<Preset<Integer>> loadPresets() throws SQLException {
    String key = "modules";
    String query = "SELECT id,nom,"+ key + " FROM module_preset";
    ResultSet rs = dc.executeQuery(query);
    List<Preset<Integer>> presets = new ArrayList<>();
    while(rs.next()) {
      Preset<Integer> p = new DefaultPreset<>();
      p.setId(rs.getInt(1));
      p.setName(rs.getString(2));
      Integer[] modules = (Integer[]) rs.getArray(3).getArray();
      p.setValue(modules);
      presets.add(p);
    }
    return presets;
  }

  public void addPreset(Preset<Integer> p) throws SQLException {
    Integer[] modules = p.getValue();
    int idx = nextId(PRESET_SELECTION_SEQUENCE, dc);
    String query = "INSERT into " + PRESET_SELECTION_TABLE + " VALUES(" + idx + ",E'"+ TableIO.escape(p.getName()) + "','" + dc.createArray("integer", modules) + "')";
    p.setId(idx);
    dc.executeUpdate(query);
  }

  public void updatePreset(Preset<Integer> p) throws SQLException {
    String query = "UPDATE " + PRESET_SELECTION_TABLE + " SET nom = E'" + TableIO.escape(p.getName()) + "' WHERE id = " + p.getId();
    dc.executeUpdate(query);
  }

  public void deletePreset(int p) throws SQLException {
    String query = "DELETE FROM " + PRESET_SELECTION_TABLE + " WHERE id = " + p;
    dc.executeUpdate(query);
  }

}
