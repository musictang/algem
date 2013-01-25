/*
 * @(#)SubscriptionCardEditor.java 2.7.a 14/01/13
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEvent;
import net.algem.contact.PersonFileListener;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;

/**
 * Subscription card editor.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class SubscriptionCardEditor
        extends FileTabDialog
{

  private PersonSubscriptionCard card;
  private PersonSubscriptionCardView cardView;
  private ActionListener listener;
  private PersonFileListener pfListener;
  private PersonFile dp;
  private boolean newCard;
  private MemberService service;

  public SubscriptionCardEditor(GemDesktop desktop, ActionListener listener, PersonFile dp) throws SQLException {
    super(desktop);
    this.dp = dp;
    this.card = dp.getSubscriptionCard();
    this.listener = listener;
    service = new MemberService(dc);
    desktop.getDataCache().getList(Model.Account);
    cardView = new PersonSubscriptionCardView(service.getPassList(), desktop.getDataCache().getList(Model.Account));
    load();

    setLayout(new BorderLayout());
    add(cardView, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  public void setCard(PersonSubscriptionCard card) {
    this.card = card;
    load();
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  @Override
  public void load() {
    newCard = (null == card || card.isNewCard());
    cardView.set(card);
  }

  @Override
  public void validation() {
    card = cardView.get();
    cardView.clear();
    try {
      if (newCard) {
        service.create(card, dp);
      } else {
        service.update(card);
      }
    } catch (SQLException ex) {
      GemLogger.logException("carte abo personne édition", ex);
    }
    pfListener.contentsChanged(new PersonFileEvent(card, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED));
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "EditionCarteAbo.Validation"));

  }

  @Override
  public void cancel() {
    cardView.clear();
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "EditionCarteAbo.Abandon"));
  }

  public void addPersonFileListener(PersonFileListener dpl) {
    this.pfListener = dpl;
  }
}
