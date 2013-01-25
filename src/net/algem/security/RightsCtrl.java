/*
 * @(#)RightsCtrl.java	2.6.a 03/10/12
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

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.GemLogger;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 01/08/2012
 */
public class RightsCtrl
        extends CardCtrl
{

  private RightsCardView cv;
  private RightsMenuView mv;
  private User user;
  private UserService service;

  public RightsCtrl(UserService service) {
    this.service = service;
    mv = new RightsMenuView(service);
    cv = new RightsCardView(service);

    addCard("Menus", mv);
    addCard("Droits", cv);
    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        break;
    }
    return true;
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
    switch (step) {
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {
    try {
      user.setLogin(mv.getLogin());
      user.setProfile(mv.getProfile());
      service.update(user);
      //TODO update cache

    } catch (SQLException e1) {
      GemLogger.logException("Update user", e1, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  public void clear() {
    mv.clear();
    cv.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof User)) {
      return false;
    }

    user = (User) o;
    try {
      mv.load(user);
      cv.load(user);

      select(0);
    } catch (Exception e) {
      GemLogger.logException("lecture ficher droits", e, this);
      return false;
    }
    return true;
  }

  @Override
  public boolean loadId(int id) {
    return loadCard(service.findId(id));
  }
}
