/*
 * @(#)RehearsalCardSearchCtrl.java 2.8.w 08/07/14
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
package net.algem.contact.member;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Rehearsal card search controller.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.w
 * @since 2.1ab 17/11/2010
 */
public class RehearsalCardSearchCtrl
        extends SearchCtrl
{
  private final GemDesktop desktop;

  public RehearsalCardSearchCtrl(GemDesktop _desktop) {
    super(DataCache.getDataConnection(), "Consultation/modification des cartes d'abonnement");
    this.desktop = _desktop;
  }

  @Override
  public void init() {
    searchView = new RehearsalCardSearchView();
    searchView.addActionListener(this);

    list = new RehearsalCardListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new RehearsalCardCtrl(desktop);
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String query = "";
    String nbSessions;
    String minDuration;

    String label = searchView.getField(0);

    if (label != null) {
      query += "WHERE libelle ~* '" + label + "'";
    } else if ((nbSessions = searchView.getField(1)) != null) {
      query += "WHERE nbseances = " + Integer.parseInt(nbSessions);
    } else if ((minDuration = searchView.getField(2)) != null) {
      query += "WHERE dureemin = " + Integer.parseInt(minDuration);
    }

    query += " ORDER BY libelle";
    System.out.println(query);
    Vector<RehearsalCard> v;
    try {
      v = RehearsalCardIO.findAll(query, dc);
    } catch (SQLException ex) {
      GemLogger.logException("Carte abonnement found exception", ex);
      v = null;
    }
    if (v == null || v.isEmpty()) {
      setStatus(MessageUtil.getMessage("search.empty.list.status"));
    } else if (v.size() == 1) {
      ((RehearsalCardCtrl) mask).loadCard(v.elementAt(0));
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
    } else {
      list.loadResult(v);
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    String cmd = evt.getActionCommand();

    if ("CtrlUpdate".equals(cmd)) {
      if (list.nbLines() > 0) {
        list.updateRow(evt.getSource());
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      } else {
        ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
      }
    } else if (GemCommand.CREATE_CMD.equals(cmd)) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      list.clear();
      mask.loadCard(new RehearsalCard(-1));
    } else if (GemCommand.DELETE_CMD.equals(cmd)) {
      RehearsalCard c = ((RehearsalCardCtrl) mask).getCard();
      if (delete(c) && list.nbLines() > 0) {
        list.deleteRow(c);
        c = null;
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      } else {
        ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      }
    } 

  }

  private boolean delete(RehearsalCard card) {
    try {
      if (RehearsalCardIO.delete(card.getId(), dc)) {
        desktop.getDataCache().remove(card);
      }
    } catch (SQLException ex) {
      return false;
    }

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }
}
