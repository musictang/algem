/*
 * @(#)ItemCtrl.java 2.6.a 01/08/2012
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

package net.algem.billing;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Item mask edition.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a 03/02/12
 */
public class ItemCtrl
        extends CardCtrl
{

  protected BillingService service;
  protected ItemView view;
  private Item old;

  public ItemCtrl() {
  }

  public ItemCtrl(BillingService service) {

    this.service = service;
    view = new ItemView(service);
    addCard("", view);
    btPrev.setText(GemCommand.DELETE_CMD);

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

    if (MessagePopup.confirm(this, MessageUtil.getMessage("invoice.item.delete.confirmation"),
            BundleUtil.getLabel("Warning.label"))) {
      try {
        Item a = view.get();
        service.delete(a);
        gemListener.postEvent(new ItemDeleteEvent(this, a));
        return cancel();
      } catch (SQLException ex) {
        service.log(MessageUtil.getMessage("invoice.item.delete.exception"), ex);
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean validation() {

    Item a = view.get();

    if (a.equals(old)) {
      MessagePopup.warning(view, MessageUtil.getMessage("no.update.info"));
      return false;
    }

    if (a.getDesignation().isEmpty()) {
      MessagePopup.warning(view, MessageUtil.getMessage("invoice.item.empty.designation"));
      return false;
    }

    if (a.getId() == 0) {
      try {
        service.create(a);
        old = a;
        view.set(a);
        //return true;// COMMENTÉ, on sort de la fenêtre de création
      } catch (SQLException ex) {
        MessagePopup.warning(view, MessageUtil.getMessage("invoice.item.create.exception")+"\n"+ex.getMessage());
        return false;
      }
    }
    try {
      service.update(a);
      old = a;
      gemListener.postEvent(new ItemUpdateEvent(this, a));
    } catch (SQLException ex) {
      MessagePopup.warning(view, MessageUtil.getMessage("invoice.item.update.exception")+"\n"+ex.getMessage());
      return false;
    }
    return cancel();
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(service.getItem(id));
    } catch (SQLException ex) {
      MessagePopup.warning(view, MessageUtil.getMessage("invoice.item.not.found.exception"));
      return loadCard(null);
    }
  }

  @Override
  public boolean loadCard(Object o) {

    view.clear();

    if (o == null || !(o instanceof Item)) {
      return false;
    }
    Item a = (Item) o;
    view.set(a);
    old = (Item) a;

    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
