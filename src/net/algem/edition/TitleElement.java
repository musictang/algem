/*
 * @(#)TitleElement.java 2.6.a 17/09/12
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

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.n
 */
class TitleElement extends DrawableElement
{

  static int TITLE_WIDTH = 300;
  private String titre;

  private int h = 16;
  private int angle = 10;

  public TitleElement(String titre, int x, int y) {
    super(x, y);
    this.titre = titre;
  }

  @Override
  protected void draw(Graphics g) {
    g.setFont(sansLarge);
    g.drawRoundRect(x, y, TITLE_WIDTH, h, angle, angle);
    FontMetrics fm = g.getFontMetrics();
    int margin = getX(fm.charWidth('x'));
    //centrage du texte à l'intérieur du rectangle
    // seulement dans le cas d'un rectangle conteneur haut
    //g.drawString(titre, x + margin, y + (h / 2) + (fm.getHeight() / 2));
    g.drawString(titre, x + margin, y + (h * 3 / 4)); // 3/4 de la hauteur
  }

  /**
   * Position x du texte à l'intérieur du rectangle.
   * @param charWidth
   * @return une position relative
   */
  private int getX(int charWidth) {
    int stringLength = titre.length() * charWidth;
    return (TITLE_WIDTH / 2) - (stringLength / 2);
  }
}
