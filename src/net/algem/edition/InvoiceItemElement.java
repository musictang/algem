/*
 * @(#)InvoiceItemElement.java 2.9.4.13 05/10/2015
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
package net.algem.edition;

import java.awt.Graphics;
import net.algem.billing.InvoiceItem;
import net.algem.billing.InvoiceView;
import net.algem.util.ImageUtil;

/**
 * Invoice item element.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.a 23/02/12
 */
public class InvoiceItemElement
        extends DrawableElement
{

  public static final int TABLE_WIDTH = ImageUtil.mmToPoints(180);
  public static final int FIRST_COL_WIDTH = ImageUtil.mmToPoints(110);
  public static final int xColQty = InvoiceView.MARGIN + FIRST_COL_WIDTH;
  public static final int xColPrice = xColQty + 45;
  public static final int xColVAT = xColPrice + 60;
  public static final int xColHT = xColVAT + 30;

  protected int end;
  private InvoiceItem af;
  private int offset;

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
    double vat = Double.parseDouble(af.getItem().getTax().getValue());
    double totalHt = af.getTotal(false);

    int topOffset = 15;

    StringBuilder sb = new StringBuilder();
    int w = 0;
    for (int i = 0; i < des.length(); i++) {
      w += g.getFontMetrics().charWidth(des.charAt(i));
      if (w > FIRST_COL_WIDTH) {
        // coupure de mots
        while (sb.length() > 1 && sb.charAt(sb.length() - 1) != ' ') {
          sb.deleteCharAt(sb.length() - 1);
          i--;
        }
        g.drawString(sb.toString(), x + 5, y + topOffset + offset);
        offset += g.getFontMetrics().getHeight();
        sb.delete(0, sb.length());
        w = 0;
      }
      sb.append(des.charAt(i));
    }
    g.drawString(sb.toString(), x + 5, y + topOffset + offset);

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
    g.setFont(SERIF_SMALL);
  }

  protected void center(Graphics g2d, String s, int width, int x, int y) {
    int stringLen = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
    int start = width / 2 - stringLen / 2;
    g2d.drawString(s, start + x, y);
  }

  public int getOffset() {
    return offset;
  }
}
