/*
 * @(#)CourseSearchCtrl.java	2.8.w 08/07/14
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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Course search controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class CourseSearchCtrl
        extends SearchCtrl
{

  private final GemDesktop desktop;

  public CourseSearchCtrl(GemDesktop desktop) {
    super(DataCache.getDataConnection(), null);
    this.desktop = desktop;
  }

	@Override
  public void init() {
    searchView = new CourseSearchView();
    ((CourseSearchView) searchView).setCode(desktop.getDataCache().getList(Model.CourseCode));
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

    String m;
    String query = "";
    int id = getId();
    if (id > 0) {
      query += "WHERE id = " + id;
    } // search by title
    else if (null != (m = searchView.getField(1))) {
      query += "WHERE titre ~* '" + m.toUpperCase() + "'";
    } // search by type
    else {
      m = searchView.getField(2);
      int c = Integer.parseInt(m);
      if (c > 0) {
        query += "WHERE code IN(SELECT id FROM module_type WHERE id = " + c + ")";
      } else if (null != (m = searchView.getField(3))) { // search by status
        if (m.equals("t")) {
          query += "WHERE collectif = '" + m + "'";
        }
      }
    }

    query += " ORDER BY titre";

    Vector<Course> v = null;
		try {
			v = ((CourseIO) DataCache.getDao(Model.Course)).find(query);
		} catch (SQLException ex) {
			GemLogger.logException(ex);
		}
    if (v == null || v.isEmpty()) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (GemCommand.CREATE_CMD.equals(evt.getActionCommand())) {
      mask.loadCard(new Course());
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
    } else {
      super.actionPerformed(evt);
    }
  }
}

