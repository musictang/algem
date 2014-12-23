/*
 * @(#)RehearsalCardCtrl.java 2.8.w 08/07/14
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

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for rehearsal cards.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.w
 */
public class RehearsalCardCtrl 
	extends CardCtrl
{

  private final DataConnection dc;
  private final GemDesktop desktop;
  private final RehearsalCardView view;
  private RehearsalCard card;

  public RehearsalCardCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dc = DataCache.getDataConnection();
    view = new RehearsalCardView();
    addCard("Fiche abonnement", view);
    select(0);
  }

  @Override
  public void select(int n) {
    btPrev.setText(GemCommand.DELETE_CMD);
    btPrev.setEnabled(true);
    btNext.setText(GemCommand.VALIDATE_CMD);
  }

	@Override
  public boolean next() {
    return false;
  }

	@Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
    }
    return true;
  }

	@Override
  public boolean prev() {
    return false;
  }

  @Override
  public boolean validation() {
    RehearsalCard edited = getCard();
    if (edited.getLabel() == null || edited.getLabel().trim().isEmpty()) {
      MessagePopup.information(this, MessageUtil.getMessage("label.mandatory.warning"));
      return false;
    }
    if (edited.getId() < 0) {   
      return create(edited);
    }
    if (card.strictlyEquals(edited)) {
      return true;
    }

    card = edited;
    return update(card);
  }

	@Override
  public boolean loadCard(Object o) {
    view.clear();
    if (o == null || !(o instanceof RehearsalCard)) {
      return false;
    }

    card = (RehearsalCard) o;
    view.setCard(card);

    return true;
  }

	@Override
  public boolean loadId(int id) {
    try {
      return loadCard(RehearsalCardIO.find(id, dc));
    } catch (SQLException ex) {
      GemLogger.logException("Carte abonnement found exception", ex);
      return loadCard(null);
    }
  }

  public RehearsalCard getCard() {
    return view.getCard();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    if (GemCommand.DELETE_CMD.equals(evt.getActionCommand())) {
      if (actionListener != null) {
        actionListener.actionPerformed(evt);
      }
    }
  }

  private boolean create(RehearsalCard edited) {
    try {
      RehearsalCardIO.insert(edited, dc);
      desktop.getDataCache().add(edited);
    } catch (SQLException e1) {
      GemLogger.logException("insertion carte abonnement", e1, this);
      return false;
    } 
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(getCard(), ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  private boolean update(RehearsalCard card) {
    try {
      int res = RehearsalCardIO.update(card, dc);
      desktop.getDataCache().update(card);
    } catch (Exception e1) {
      GemLogger.logException("update carte abonnement", e1, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, "CtrlUpdate"));
    }
    return true;
  }
}
