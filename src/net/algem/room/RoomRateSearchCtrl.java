/*
 * @(#)RoomRateSearchCtrl.java	2.9.4.13 07/10/15
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
package net.algem.room;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Search controller for room rates.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.1a
 */
public class RoomRateSearchCtrl
        extends SearchCtrl
{

  private final DataCache dataCache;
  private final GemDesktop desktop;

  public RoomRateSearchCtrl(GemDesktop desktop) {
    super(DataCache.getDataConnection(), "Consultation/modification des tarifs salles");
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
  }

  @Override
  public void init() {
    searchView = new RoomRateSearchView();
    searchView.addActionListener(this);

    list = new RoomRateListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new RoomRateCardCtrl(desktop);
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {
    String query = null;
    String label = null;

    String type = searchView.getField(0);
    if (type != null) {
      query = "WHERE type ~* '" + type + "' ORDER BY hp";
    } else if ((label = searchView.getField(1)) != null) {
      query = "WHERE libelle ~* '" + label + "' ORDER BY hp";
    } else {
      query = "ORDER BY hp";
    }

    Vector<RoomRate> v;
    try {
      v = ((RoomRateIO) DataCache.getDao(Model.RoomRate)).find(query);
//      v = new Vector<RoomRate>(dataCache.getList(Model.RoomRate).getData());
    } catch (SQLException ex) {
      GemLogger.logException("Tarif salle found exception", ex);
      v = null;
    }
    if (v == null || v.isEmpty()) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v); // remplissage de la liste des tarifs dans la table
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    if ("CtrlValider".equals(evt.getActionCommand())) {
      list.loadResult(((GemList<RoomRate>) dataCache.getList(Model.RoomRate)).getData());//XXX recharge la list de TOUS les tarifs !
    } else if (GemCommand.CREATE_CMD.equals(evt.getActionCommand())) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(null);
    }
  }
}
