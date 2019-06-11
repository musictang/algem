/*
 * @(#)InvoiceHeaderElement.java 2.8.y 25/09/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.8.y
 * @since 2.3.a 23/02/12
 */
public class InvoiceHeaderElement
        extends InvoiceItemElement
{

  private String[] cols = {BundleUtil.getLabel("Invoice.item.description.label"),
    BundleUtil.getLabel("Invoice.item.quantity.label"),
                           BundleUtil.getLabel("Invoice.item.price.label"),
                           BundleUtil.getLabel("Invoice.item.vat.label"),                           
                           BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Invoice.et.label")};

  public InvoiceHeaderElement(int x, int y) {
    super(x, y);
  }

  @Override
  public void draw(Graphics g) {
    int top = 15;
    int margin = 5;
    g.drawString(cols[0], x + margin, y + top);
//    g.drawString(cols[1], xColQty + 2, y + top);
    center(g,cols[1], (xColPrice - xColQty), xColQty, y + top);
//    g.drawString(cols[2], xColPrice + margin, y + top);
    center(g, cols[2], (xColVAT - xColPrice), xColPrice, y + top);
//    g.drawString(cols[3], xColVAT + margin, y + top);
    center(g, cols[3], (xColHT - xColVAT), xColVAT, y + top);
//    g.drawString(cols[4], xColHT + margin, y + top);
    center(g, cols[4], (end - xColHT), xColHT, y + top);
    
  }
  
  
}
