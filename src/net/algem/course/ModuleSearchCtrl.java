/*
 * @(#)ModuleSearchCtrl.java	2.10.0 13/05/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.SearchCtrl;

/**
 * Search controller for modules.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 1.0a 07/07/1999
 */
public class ModuleSearchCtrl
        extends SearchCtrl {

  protected GemDesktop desktop;

  public ModuleSearchCtrl(GemDesktop d) {
    super(DataCache.getDataConnection(), null);
    this.desktop = d;
  }

  @Override
  public void init() {
    searchView = new ModuleSearchView();
    searchView.addActionListener(this);

    list = new ModuleListeCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = getCard();
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  protected CardCtrl getCard() {
    return new ModuleCtrl(desktop);
  }

  @Override
  public void search() {

    String query = "";
    String code = null;
    String nom = null;

    int id = getId();
    if (id > 0) {
      query = "WHERE id = " + id;
    } 
    else if (null != (code = searchView.getField(1))) {
      query = "WHERE code ~* '" + code + "'";
    } else if ((nom = searchView.getField(2)) != null) {
      query = "WHERE titre ~* '" + nom + "'";
    }

    query += " ORDER BY code,titre";
    Vector<Module> v = null;
    try {
      v = ((ModuleIO) DataCache.getDao(Model.Module)).find(query);
    } catch (SQLException sqe) {
      GemLogger.log(getClass().getName() + "#search " + sqe.getMessage());
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
      mask.loadCard(new Module());
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
    } else {
      super.actionPerformed(evt);
    }
  }
}
