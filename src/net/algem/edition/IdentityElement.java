/*
 * @(#)IdentityElement.java 2.13.3 17/05/17
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
import static net.algem.edition.MemberCardEditor.COURSE_LIST_X;
import net.algem.util.TextUtil;

/**
 * Identity element.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.3
 * @since 2.1.n 22/07/2011
 */
public class IdentityElement
        extends DrawableElement
{

  private Contact contact;

  public IdentityElement(Contact c, int x, int y) {
    super(x, y);
    contact = c;
  }

  @Override
  public void draw(Graphics g) {
    g.setFont(SERIF_MED);
    String ident = "";
    if (contact != null) {
      ident = TextUtil.crop(contact.toString(), g, COURSE_LIST_X);

      String org = contact.getOrganization();
      if (org != null && !org.isEmpty()) {
        g.drawString(org, x, y - 10);
      }
    }
    g.drawString(ident, x, y);
  }

}
