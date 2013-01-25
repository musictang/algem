/*
 * @(#)IdentityElement.java 2.6.a 17/09/12
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
import net.algem.contact.Contact;

/**
 * Identity element.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.n 22/07/2011
 */
public class IdentityElement extends DrawableElement
{

  private Contact contact;

  public IdentityElement(Contact c, int x, int y) {
    super(x, y);
    contact = c;
  }

  @Override
  public void draw(Graphics g) {
    g.setFont(serifMed);
    if (contact != null) { 
      String p = (contact.getFirstName() == null || contact.getFirstName().isEmpty()) ? "" : contact.getFirstName() + " ";
      String n = contact.getName() == null ? "" : contact.getName();
      g.drawString(p + n, x, y);
    }
  }
}
