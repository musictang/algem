/*
 * @(#)CourseIO.java	2.8.t 15/04/14
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
package net.algem.course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.planning.ActionIO;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.course.Course}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.t
 */
public class CourseIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "cours";
  public static final String ALIAS = "c";
  public static final String COLUMNS = "c.id, c.titre, c.libelle, c.nplaces, c.niveau, c.collectif, c.code, c.ecole, c.actif";
  public static final String SEQUENCE = "idcours";
  private DataConnection dc;

  public CourseIO(DataConnection _dc) {
    dc = _dc;
  }

  public void insert(Course c) throws SQLException {

    int n = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + n
            + ",'" + escape(c.getTitle().toUpperCase())
            + "','" + escape(c.getLabel())
            //+"','0"	//+c.getNSessions()
            + "','" + c.getNPlaces()
            + "','" + c.getLevel()
            + "','" + (c.isCollective() ? "t" : "f")
            + "'," + c.getCode()
            + "," + c.getSchool()
            + ",'" + (c.isActive() ? "t" : "f") //ajout 1.1d
            + "')";

    dc.executeUpdate(query);
    c.setId(n);
  }

  public void update(Course c) throws SQLException {

    String query = "UPDATE " + TABLE + " SET "
            + "titre = '" + escape(c.getTitle().toUpperCase())
            + "',libelle = '" + escape(c.getLabel())
            + "',nplaces = '" + c.getNPlaces()
            + "',niveau = '" + c.getLevel()
            + "',collectif = '" + (c.isCollective() ? "t" : "f")
            + "',code = " + c.getCode()
            + ",ecole = " + c.getSchool()
            + ",actif = '" + (c.isActive() ? "t" : "f") //ajout 1.1d
            + "' WHERE id = " + c.getId();

    dc.executeUpdate(query);
  }

  public void delete(Course c) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + c.getId();
    dc.executeUpdate(query);
  }

  public Course findId(String n) throws SQLException {
    return findId(new Integer(n));
  }

  public Course findId(int n) throws SQLException {
    return findId(new Integer(n));
  }

  public Course findId(Integer n) throws SQLException {

    String query = "WHERE c.id = " + n;

    Vector<Course> v = find(query);
    if (v != null && v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public Course findIdByAction(int idAction) throws SQLException {
    return findId(ActionIO.getCourse(idAction, dc));
  }

  public Vector<Course> find(String where) throws SQLException {
    Vector<Course> v = new Vector<Course>();

    String query = "SELECT DISTINCT " + COLUMNS + " FROM " + TABLE + " " + ALIAS + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Course c = new Course();
      c.setId(rs.getInt(1));
      c.setTitle(unEscape(rs.getString(2).trim()));
      c.setLabel(unEscape(rs.getString(3).trim()));
      //c.setNSessions(rs.getShort(4));
      c.setNPlaces(rs.getShort(4));
      c.setLevel(rs.getShort(5));
      c.setCollective(rs.getBoolean(6));
      c.setCode(rs.getInt(7));
      c.setSchool(rs.getShort(8));
      c.setActive(rs.getBoolean(9));// ajout 1.1d
      v.addElement(c);
    }
    rs.close();

    return v;
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
