/*
 * @(#)CourseIO.java	2.15.12 12/10/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import java.util.Vector;
import net.algem.planning.ActionIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.course.Course}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.15.12
 */
public class CourseIO
  extends TableIO
  implements Cacheable {

  public static final String TABLE = "cours";
  public static final String ALIAS = "c";
  public static final String COLUMNS = "c.id, c.titre, c.libelle, c.nplaces, c.niveau, c.collectif, c.code, c.ecole, c.actif";
  public static final String SEQUENCE = "idcours";

  private static final String FIND_BY_ID_QUERY = "SELECT DISTINCT " + COLUMNS + " FROM " + TABLE + " " + ALIAS + " WHERE c.id = ?";
  private DataConnection dc;

  public CourseIO(DataConnection _dc) {
    dc = _dc;
  }

  public void insert(Course c) throws SQLException {

    int n = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, n);
      ps.setString(2, c.getTitle().toUpperCase());
      ps.setString(3, c.getLabel());
      ps.setShort(4, (short) 0);
      ps.setShort(5, c.getLevel());
      ps.setBoolean(6, c.isCollective());
      ps.setInt(7, c.getCode());
      ps.setInt(8, c.getSchool());
      ps.setBoolean(9, c.isActive());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }

    c.setId(n);
  }

  public void update(Course c) throws SQLException {
    String query = "UPDATE " + TABLE
      + " SET titre=?,libelle=?,niveau=?,collectif=?,code=?,ecole=?,actif=?"
      + " WHERE id = ?";
    
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setString(1, c.getTitle().toUpperCase());
      ps.setString(2, c.getLabel());
      ps.setShort(3, c.getLevel());
      ps.setBoolean(4, c.isCollective());
      ps.setInt(5, c.getCode());
      ps.setInt(6, c.getSchool());
      ps.setBoolean(7, c.isActive());
      ps.setInt(8, c.getId());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }

  }

  public void delete(Course c) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + c.getId();
    dc.executeUpdate(query);
  }

  public Course findId(int n) throws SQLException {
    try (PreparedStatement ps = dc.prepareStatement(FIND_BY_ID_QUERY)) {
      ps.setInt(1, n);
      try (ResultSet rs = ps.executeQuery()){
        while (rs.next()) {
          return getCourseFromRS(rs);
        }
      }
    }

    return null;
  }

  public Course findIdByAction(int idAction) throws SQLException {
    return findId(ActionIO.getCourse(idAction, dc));
  }

  public Vector<Course> find(String where) throws SQLException {
    Vector<Course> v = new Vector<Course>();

    String query = "SELECT DISTINCT " + COLUMNS + " FROM " + TABLE + " " + ALIAS + " " + where;
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        v.addElement(getCourseFromRS(rs));
      }
    }

    return v;
  }

  private Course getCourseFromRS(ResultSet rs) throws SQLException {
    Course c = new Course();
    c.setId(rs.getInt(1));
    c.setTitle(rs.getString(2).trim());
    c.setLabel(rs.getString(3).trim());
    //c.setNSessions(rs.getShort(4));
//    c.setNPlaces(rs.getShort(4));
    c.setLevel(rs.getShort(5));
    c.setCollective(rs.getBoolean(6));
    c.setCode(rs.getInt(7));
    c.setSchool(rs.getShort(8));
    c.setActive(rs.getBoolean(9));

    return c;
  }

  @Override
  public List<Course> load() throws SQLException {
    String where = "WHERE c.code <> " + CourseCodeType.ATP.getId() + " ORDER BY c.titre";
    return find(where);
  }

  public List<Course> load(String where) throws SQLException {
    return find(where);
  }

}
