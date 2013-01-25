/*
 * @(#)InvoiceItemCtrl.java 2.6.a 01/08/2012
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
 */
package net.algem.billing;

import java.sql.SQLException;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.event.CancelEvent;
import net.algem.util.event.GemEvent;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a 15/02/12
 */
public class InvoiceItemCtrl
        extends ItemCtrl
{

  private InvoiceItem af;

  public InvoiceItemCtrl(DataConnection dc, BillingService service) {

    this.service = service;
    view = new InvoiceItemView(service);
    addCard("", view);
    btPrev.setEnabled(false);

    select(0);
  }

  @Override
  public boolean loadCard(Object o) {

    view.clear();

    if (o == null || !(o instanceof InvoiceItem)) {
      return false;
    }

    af = (InvoiceItem) o;
    ((InvoiceItemView) view).set(af);

    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      Item a = service.getItem(id);
      af = new InvoiceItem(a);
      return loadCard(af);
    } catch (SQLException ex) {
      MessagePopup.warning(view, MessageUtil.getMessage("facturation.item.not.found.exception"));
      return loadCard(null);
    }
  }

  @Override
  public boolean validation() {
    if (gemListener == null) {
      return false;
    }
    af = ((InvoiceItemView) view).getInvoiceItem();
    gemListener.postEvent(new InvoiceItemCreateEvent(af, GemEvent.CREATION, GemEvent.INVOICE_ITEM));
    return cancel();

  }

  @Override
  public boolean cancel() {
    if (gemListener == null) {
      return false;
    }
    gemListener.postEvent(new CancelEvent());
    return true;
  }
}
