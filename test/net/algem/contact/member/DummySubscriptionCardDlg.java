/*
 * @(#)DummySubscriptionCardDlg.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2010 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Component;
import net.algem.util.DataCache;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * Bouchon de test pour les cartes d'abonnement.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class DummySubscriptionCardDlg
        extends PopupDlg
{

  private RehearsalPass card;

  public DummySubscriptionCardDlg(Component c, DataCache dc) {
    super(c, "Choix abonnement", true);
  }

  @Override
  public GemPanel getMask() {
    return null;
  }

  public void set(RehearsalPass c) {
    this.card = c;
  }

  public RehearsalPass get() {
    return card;
  }
}
