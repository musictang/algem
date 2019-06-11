/*
 * @(#)CDromCtrl.java	2.8.w 08/07/14
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
package net.algem.opt;

import java.awt.CardLayout;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.ui.SearchCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @deprecated 
 */
public class CDromCtrl
        extends SearchCtrl {
	
	private final DataCache dataCache;

  public CDromCtrl(DataCache dc) {
    super(DataCache.getDataConnection(), "Consultation/modification d'un CDROM");
		this.dataCache = dc;
  }

  @Override
  public void init() {
    searchView = new CDromSearchView();
    searchView.addActionListener(this);

    list = new CDromListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new CDromCardCtrl(dataCache);
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String query;
    String nom;
    int id = getId();
    if (id > 0) {
      query = "where id=" + id;
    } else if ((nom = searchView.getField(1)) != null) {
      query = "where nom ~ '" + nom.toUpperCase() + "'";
    } else {
      query = "";
    }

    Vector v = CDromIO.find(query, dc);
    if (v.isEmpty()) {
      setStatus("Aucun enregistrement trouvé");
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard((CDrom) v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }
}
