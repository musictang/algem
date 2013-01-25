/*
 * @(#)DrawableElement.java 2.3.e 04/04/12
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

import java.awt.Font;
import java.awt.Graphics;

/**
 * Generic drawable element.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.3.e
 * @since 2.3.e
 */
public abstract class DrawableElement {
  
  protected int x;
  protected int y;

  protected Font sansLarge = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
  protected Font serifMed = new Font(Font.SERIF, Font.PLAIN, 10);
  protected Font serifSmall = new Font(Font.SERIF, Font.PLAIN, 9);
  protected Font sansSmall = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
  protected Font sansXSmall = new Font(Font.SANS_SERIF, Font.PLAIN, 6);

  public DrawableElement(int x, int y) {
    this.x = x;
    this.y = y;
  }

  abstract protected void draw(Graphics g);


  protected void setFont(Graphics g) {
    // NON IMPLEMENTE
  }

}
