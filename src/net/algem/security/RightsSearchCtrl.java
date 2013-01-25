/*
 * @(#)RightsSearchCtrl.java 2.6.a 03/10/12
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
package net.algem.security;

import java.awt.CardLayout;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.ui.SearchCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class RightsSearchCtrl
        extends SearchCtrl {

  private UserService service;
  
  public RightsSearchCtrl(DataCache dataCache) {
    super(dataCache.getDataConnection(), "Consultation/modification des logins");
    this.service = dataCache.getUserService();
  }

  @Override
  public void init() {
    searchView = new RightsSearchView();
    searchView.addActionListener(this);

    list = new RightsListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new RightsCtrl(service);
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String query;
    String name;
    String login;
    int id = getId();
    if (id > 0) {
      query = "WHERE idper = " + id;
    } else if ((name = searchView.getField(1)) != null) {
      query = "WHERE nom ~* '" + name + "'";
    } else if ((login = searchView.getField(2)) != null) {
      query = "WHERE login ~* '" + login + "'";
    } else {
      query = null;
    }

    Vector<User> v = service.findAll(query);
    if (v == null || v.isEmpty()) {
      setStatus(MessageUtil.getMessage("search.empty.list.status"));
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }
}
