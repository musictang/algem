
/*
 * @(#)IdElement.java 2.6.a 17/09/12
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
 * Id element component.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class IdElement 
        extends DrawableElement
{

  private int id;

  public IdElement(int id, int x, int y) {
    super(x, y);
    this.id = id;
  }

  @Override
  protected void draw(Graphics g) {
    g.drawString(getId(),x,y);
  }

  private String getId() {
    return BundleUtil.getLabel("Member.card.id.label")+" "+String.valueOf(id);
  }

}
