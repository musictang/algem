/*
 * @(#)TitleElement.java 2.13.3 17/05/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.3
 * @since 2.1.n
 */
class TitleElement extends DrawableElement
{

  private String title;
  private double centerPos;
  private int h = 16;
  private int angle = 10;

  public TitleElement(String title, int x, int y) {
    super(x, y);
    this.title = title;
  }

  public void setCenter(double c) {
    this.centerPos = c;
  }

  @Override
  protected void draw(Graphics g) {
    g.setFont(SANS_LARGE);
    FontMetrics fm = g.getFontMetrics();
    int w = fm.stringWidth(title);
    int margin = fm.charWidth('x');
    w = w + (2*margin);
    x = (int) (centerPos - (w / 2f));
    g.drawRoundRect(x, y, w, h, angle, angle);
    g.drawString(title, x + margin, y + (h * 3 / 4)); // 3/4 de la hauteur
  }

}
