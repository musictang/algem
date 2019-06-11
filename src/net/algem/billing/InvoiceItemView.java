/*
 * @(#)InvoiceItemView.java	2.6.a 01/08/2012
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

import net.algem.util.BundleUtil;
import net.algem.util.ui.GemDecimalField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a 14/02/12
 */
public class InvoiceItemView
        extends ItemView {

  private GemDecimalField qty;
  private InvoiceItem invoiceItem = new InvoiceItem();

  public InvoiceItemView(BillingService service) {
    super(service);

    qty = new GemDecimalField();
    qty.setColumns(4);

    qty.setValue(0.0d); // quantité 0 par défaut

    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.item.quantity.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(qty, 1, 5, 3, 1, GridBagHelper.WEST);

  }

  /**
   * Gets the updated invoice item.
   *
   * @return an invoice item
   */
  public InvoiceItem getInvoiceItem() {

    Item a = super.get();

    invoiceItem.setItem(a);
    invoiceItem.setQuantity(((Number) qty.getValue()).floatValue());

    return invoiceItem;
  }

  /**
   * Sets the detail of an invoice item {@code it}.
   *
   * @param it the item
   */
  public void set(InvoiceItem it) {

    Item a = it.getItem();
    super.set(a);
    qty.setValue(it.getQuantity());

    if (a != null && a.isStandard()) {
      protect();
    }
    this.invoiceItem = it;

  }

  @Override
  public void clear() {
    super.clear();
    qty.setValue(0.0d);
  }

  /**
   * Protects non editable fields.
   */
  private void protect() {
    //designation.setEditable(false);
    //prix.setEditable(false);
    account.setEnabled(false);
    vat.setEnabled(false);
    //standard.setEnabled(false);
  }
}
