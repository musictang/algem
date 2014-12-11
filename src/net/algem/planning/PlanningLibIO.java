/*
 * @(#)PlanningLibIO.java	2.9.1 08/12/14
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
package net.algem.planning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Planning lib view selection.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 1.0a 07/07/1999
 * @see net.algem.planning.PlanningLib
 */
public class PlanningLibIO
        extends TableIO
{

  private final static String VIEW = "planningvue";
  /**
   * View is saved in database.
   * Any modification in source code must be reflected in the definition of the
   * view in database.
   * SELECT pl.id, pl.jour, pl.debut, pl.fin, pl.action, p.id AS profid, p.prenom AS prenomprof, p.nom AS nomprof, s.id AS salleid, s.nom AS salle, c.id AS coursid, c.titre AS cours, c.ecole
   *  FROM planning pl, personne p, salle s, cours c, action a
   *  WHERE pl.ptype IN (1,5,6) AND pl.action = a.id AND a.cours = c.id AND pl.lieux = s.id AND pl.idper = p.id;
   * -- and (select count(id) from plage where idplanning = pl.id) > 0 -- empty schedules not included
   *
   * @param dc dataCache
   * @param where
   * @return a list of planninglib
   */
  public static Vector<PlanningLib> find(String where, DataConnection dc) throws SQLException {
    Vector<PlanningLib> v = new Vector<PlanningLib>();
    String query = "SELECT id, jour, debut, fin, action, coursid, cours, profid, prenomprof, nomprof, salleid, salle FROM " + VIEW + " " + where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      PlanningLib p = new PlanningLib();
      p.setID(rs.getInt(1));
      p.setDay(new DateFr(rs.getString(2)));
      p.setStart(new Hour(rs.getString(3)));
      p.setEnd(new Hour(rs.getString(4)));
      p.setAction(rs.getInt(5));
      p.setCourseId(rs.getInt(6));
      p.setCourse(rs.getString(7));// titre du cours
      p.setTeacherId(rs.getInt(8));
      p.setTeacher(rs.getString(9) + " " + rs.getString(10)); // prenom et nom prof
      p.setRoomId(rs.getInt(11));
      p.setRoom(rs.getString(12));// nom de la salle

      v.addElement(p);
    }
    rs.close();

    return v;
  }
}
