/*
 * @(#)InvoiceItemElement.java 2.8.y 25/09/14
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
import net.algem.billing.InvoiceItem;
import net.algem.billing.InvoiceView;
import net.algem.util.ImageUtil;

/**
 * Invoice item element.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y
 * @since 2.3.a 23/02/12
 */
public class InvoiceItemElement
        extends DrawableElement
{

  public static final int TABLE_WIDTH = ImageUtil.toPoints(180);
  public static final int FIRST_COL_WIDTH = ImageUtil.toPoints(110);
  public static final int xColQty = InvoiceView.MARGIN + FIRST_COL_WIDTH;
  public static final int xColPrice = xColQty + 45;
  public static final int xColVAT = xColPrice + 60;
  public static final int xColHT = xColVAT + 30;
  
  private InvoiceItem af;

  protected int end;

  public InvoiceItemElement(int x, int y) {
    super(x, y);
    end = x + TABLE_WIDTH;
  }

  public InvoiceItemElement(int x, int y, InvoiceItem af) {
    this(x, y);
    this.af = af;
  }

  @Override
  public void draw(Graphics g) {
    setFont(g);
    String des = af.getItem().getDesignation();
    double qty = af.getQuantity();
    double price = af.getItem().getPrice();
    double vat = Double.parseDouble(af.getItem().getVat().getValue());
    double totalHt = af.getTotal(false);

    int topOffset = 15;

    g.drawString(des, x + 5, y + topOffset);
    rightAlign(g, qty, xColPrice, y + topOffset);
    rightAlign(g, price, xColVAT, y + topOffset);
    rightAlign(g, vat, xColHT, y + topOffset);
    rightAlign(g, totalHt, end, y + topOffset);

  }

  protected void rightAlign(Graphics g, double d, int x, int y) {
    String s = "";
    if (d != 0.0) { // on n'affiche pas les montants nuls
      s = String.format("%,.2f", d);
    }
    int stringLen = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
    int start = x - stringLen - 3;
    g.drawString(s, start, y);
  }

  @Override
  public void setFont(Graphics g) {
    g.setFont(serifSmall);
  }
  
  protected void center(Graphics g2d, String s, int width, int x, int y) {
    int stringLen = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
    int start = width / 2 - stringLen / 2;
    g2d.drawString(s, start + x, y);
  }
}
