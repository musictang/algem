/*
 * @(#)InvoiceHeaderElement.java 2.6.a 17/09/12
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

package net.algem.edition;

import java.awt.Graphics;
import net.algem.util.BundleUtil;

/**
 * Invoice header element.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a 23/02/12
 */
public class InvoiceHeaderElement
        extends InvoiceItemElement
{

  private String[] cols = {BundleUtil.getLabel("Invoice.item.description.label"),
                           BundleUtil.getLabel("Invoice.item.price.label"),
                           BundleUtil.getLabel("Invoice.item.vat.label"),
                           BundleUtil.getLabel("Invoice.item.quantity.label"),
                           BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Invoice.et.label")};

  public InvoiceHeaderElement(int x, int y) {
    super(x, y);
  }

  @Override
  public void draw(Graphics g) {

    g.drawString(cols[0], x + 5, y + 15);
    g.drawString(cols[1], xColPrix + 5, y + 15);
    g.drawString(cols[2], xColTva + 5, y + 15);
    g.drawString(cols[3], xColQte + 5, y + 15);
    g.drawString(cols[4], xColHT + 5, y + 15);

  }
}
