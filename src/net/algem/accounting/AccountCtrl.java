/*
 * @(#)AccountCtrl.java	2.7.a 11/01/13
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

package net.algem.accounting;

import java.sql.SQLException;
import java.util.Vector;
import net.algem.config.Param;
import net.algem.config.ParamTableCtrl;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;

/**
 * Management of accounts.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class AccountCtrl
  extends ParamTableCtrl  {

  /**
   * Par défaut, les numéros de compte sont éditables et les comptes sont activables.
   * @param _desktop
   */
  public AccountCtrl(GemDesktop _desktop) {
    super(_desktop, "Comptes comptables", true, true);
  }

  @Override
  public void setView(boolean activable) {
    table = new AccountTableView(title, new AccountTableModel());
    mask = new AccountView();
  }
  
  @Override
  public void load() {
    try {
      load(AccountIO.find(false, dc).elements());
    } catch (SQLException ex) {

    }
  }

  @Override
  public void modification(Param current, Param p) throws SQLException {
    if (p instanceof Account) {
      Account c = (Account)p;
      AccountIO.update(c, dc);
      desktop.getDataCache().update(c);
      desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.ACCOUNT, c));
    }
  }

  @Override
  /**
   * @see net.algem.ctrl.parametres.ParamTableCtrl#insertion
   */
  public void insertion(Param p) throws SQLException {
    if (p instanceof Account) {
      Account c = (Account)p;
      AccountIO.insert(c, dc);
      desktop.getDataCache().add(c);
      desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.ACCOUNT, c));
    }
  }

  @Override
  /**
   * Account suppression.
   * It is not advisable to suppress an account referenced by any orderline.
   * @see net.algem.ctrl.parametres.ParamTableCtrl#suppression
   */
  public void suppression(Param p) throws SQLException, AccountDeleteException {
    if (p instanceof Account) {
      Account c = (Account) p;
      String where = "WHERE " + OrderLineIO.ACCOUNT_COLUMN + " = '"+c.getId()+"'";
      Vector<OrderLine> e = OrderLineIO.find(where, 1, dc);
      if (e != null && e.size() > 0) {
        throw new AccountDeleteException();
      }
      else {
        AccountIO.delete(c, dc);
        desktop.getDataCache().remove(c);
        desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.ACCOUNT, c));
      }
    }
  }

}
