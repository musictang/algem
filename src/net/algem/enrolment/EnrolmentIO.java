/*
 * @(#)EnrolmentIO.java 2.6.a 17/09/12
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
package net.algem.enrolment;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.Enrolment}.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class EnrolmentIO
        extends TableIO
{

  public void trans_insert(Enrolment i, DataConnection dc) throws Exception {
    Order c = i.getOrder();
    OrderIO.insert(c, dc);

    /*
    if (cmm != null)
    for (int i=0; i < cmm.length;i++)
    cmm[i].insert(dc,c.id);
    if (cmc != null)
    for (int i=0; i < cmc.length;i++)
    cmc[i].insert(dc,c.id);
     */
  }

  public void insert(Enrolment i, DataConnection dc) throws Exception {
    dc.setAutoCommit(false);

    try {
      trans_insert(i, dc);
      dc.commit();
    } catch (Exception e1) {
      GemLogger.logException("transaction insert inscription", e1);
      dc.rollback();
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void update(Enrolment newval, DataConnection dc) throws Exception {
    dc.setAutoCommit(false);

    try {
      trans_update(newval, dc);
      dc.commit();
    } catch (Exception e1) {
      GemLogger.logException("transaction update inscription", e1);
      dc.rollback();
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void trans_update(Enrolment newval, DataConnection dc) throws Exception {
  }

  public void delete(DataCache dc) throws Exception {
  }

  public static Enrolment findId(int n, DataConnection dc) throws SQLException {
    String query = "WHERE id=" + n;
    Vector<Enrolment> v = find(query, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public static Vector<Enrolment> find(String where, DataConnection dc) throws SQLException {
    Vector<Enrolment> v = new Vector<Enrolment>();
    Vector<Order> pl = OrderIO.find(where, dc);
    if (pl.size() < 1) {
      return v;
    }
    Enumeration<Order> enu = pl.elements();
    while (enu.hasMoreElements()) {
      Order c = enu.nextElement();
      Enrolment i = new Enrolment(c);
      i.setModule(ModuleOrderIO.findId(i.getId(), dc));
      i.setCourseOrder(CourseOrderIO.findId(i.getId(), dc));
      v.addElement(i);
    }
    return v;
  }
}
