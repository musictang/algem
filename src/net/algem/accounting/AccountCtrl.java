/*
 * @(#)AccountCtrl.java	2.9.4.11 21/07/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import net.algem.config.Param;
import net.algem.config.ParamTableCtrl;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;

/**
 * Management of accounts.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.11
 */
public class AccountCtrl
  extends ParamTableCtrl  {

  private AccountingService service;
  /**
   * Par défaut, les numéros de compte sont éditables et les comptes sont activables.
   * @param _desktop
   */
  public AccountCtrl(GemDesktop _desktop) {
    super(_desktop, "Comptes comptables", true, true);
    service = new AccountingService(dc);
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
   *
   * @see net.algem.ctrl.parametres.ParamTableCtrl#suppression
   */
  public void suppression(Param p) throws AccountDeleteException {
    if (p instanceof Account) {
      Account c = (Account) p;
      service.delete(c);
      desktop.getDataCache().remove(c);
      desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.ACCOUNT, c));
    }
  }

}
