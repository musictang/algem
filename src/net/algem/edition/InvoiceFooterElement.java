/*
 * @(#)InvoiceFooterElement.java 2.8.y 25/09/14
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import java.util.Map;
import net.algem.billing.Quote;
import net.algem.util.BundleUtil;

/**
 * Invoice footer element.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y
 * @since 2.3.a 23/02/12
 */
public class InvoiceFooterElement
        extends InvoiceItemElement
{

  private Quote q;

  public InvoiceFooterElement(int x, int y, Quote q) {
    super(x, y);
    this.q = q;
  }

  @Override
  public void draw(Graphics g) {

    int xoffset = x;
    int sep1 = x + FIRST_COL_WIDTH;
//    int sep2 = sep1 + 100;
    int sep2 = xColVAT;
    int acompteOffset = q.hasDownPayment() ? 40 : 0;

    g.drawString(BundleUtil.getLabel("Invoice.item.vat.label").toUpperCase(), x + 5, y + 30);
    g.drawString(BundleUtil.getLabel("Total.label").toUpperCase(), sep1 + 5, y + 30);
    g.drawRect(x, y + 40, TABLE_WIDTH, 40 + acompteOffset);

    g.drawLine(x, y + 60, x + TABLE_WIDTH, y + 60);
    g.drawLine(sep1, y + 40, sep1, y + 80 + acompteOffset);
    g.drawLine(sep2, y + 40, sep2, y + 80 + acompteOffset);

    g.drawString(BundleUtil.getLabel("Invoice.et.label"), sep1 + 5, y + 52);
    g.drawString(BundleUtil.getLabel("Invoice.ati.label"), sep2 + 5, y + 52);

    for (Map.Entry<String, Double> entry : q.getTotalVAT().entrySet()) {
      if (entry.getValue() > 0.0) {
        rightAlign(g, Double.parseDouble(entry.getKey()), xoffset + 40, y + 50); // valeur tva
        rightAlign(g, entry.getValue(), xoffset + 40, y + 70); // montant total pour cette tva
        xoffset += 40;
      }
    }
    rightAlign(g, q.getTotalET(), sep2, y + 72);
    rightAlign(g, (q.getTotalATI()), end, y + 72);

    if (q.hasDownPayment()) {
      g.drawString(BundleUtil.getLabel("Invoice.down.payment.label"), sep1 + 5, y + 92);
      rightAlign(g, -(q.getDownPayment()), end, y + 92);
      g.drawString(BundleUtil.getLabel("Invoice.net.label"), sep1 + 5, y + 112);
      rightAlign(g, (q.netToPay()), end, y + 112);
    }
  }
}
