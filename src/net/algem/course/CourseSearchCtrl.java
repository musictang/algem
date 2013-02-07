/*
 * @(#)CourseSearchCtrl.java	2.7.f 07/02/13
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

import java.awt.CardLayout;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.f
 * @since 1.0a 07/07/1999
 */
public class CourseSearchCtrl
        extends SearchCtrl
{

  private GemDesktop desktop;
  
  public CourseSearchCtrl(GemDesktop desktop) {
    super(desktop.getDataCache().getDataConnection(), "Recherche d'un cours");
    this.desktop = desktop;
  }

	@Override
  public void init() {
    searchView = new CourseSearchView();
    searchView.addActionListener(this);

    list = new CourseListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new CourseCtrl(desktop);
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

	@Override
  public void search() {
    String query = "WHERE code != '"+Course.ATP_CODE+"'";
    String m;

    int id = getId();
    if (id > 0) {
      query += " AND id = " + id;
    } // recherche par title
    else if (null != (m = searchView.getField(1))) {
      query += " AND titre ~* '" + m.toUpperCase() + "'";
    } // recherche par type
    else if (null != (m = searchView.getField(2))) {
      query += " AND code ~* '" + m + "'";
    } else if (null != (m = searchView.getField(3))) {
      if (m.equals("t")) {
        query += " AND collectif = '" + m + "'";
      }
    }

    query += " ORDER BY titre";

    Vector<Course> v = null;
		try {
			v = ((CourseIO) DataCache.getDao(Model.Course)).find(query);
		} catch (SQLException ex) {
			GemLogger.logException(ex);
		}
    if (v.isEmpty()) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }
}

