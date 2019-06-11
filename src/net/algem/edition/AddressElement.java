/*
 * @(#)AddressElement.java 2.13.3 17/05/17
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
import net.algem.contact.Address;
import net.algem.util.TextUtil;

/**
 * Address element component.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.3
 * @since 2.1.n 22/07/2011
 */
public class AddressElement extends DrawableElement
{

  private Address address;

  public AddressElement(Address a, int x, int y) {
    super(x, y);
    address = a;
  }

  @Override
  public void draw(Graphics g) {
    setFont(g);
    FontMetrics fm = g.getFontMetrics();
    int lineH = fm.getHeight();
    if (address != null) {
      String adr1 = TextUtil.crop(address.getAdr1(),g, MemberCardEditor.COURSE_LIST_X);
      g.drawString(adr1, x, y);
      String adr2 = TextUtil.crop(address.getAdr2(),g, MemberCardEditor.COURSE_LIST_X);
      g.drawString(adr2, x, y + lineH);
      String city = TextUtil.crop(address.getCdp() + " " + address.getCity(),g, MemberCardEditor.COURSE_LIST_X);
      g.drawString(city, x, y + (2 * lineH));
    }
  }

  @Override
  public void setFont(Graphics g) {
    g.setFont(SERIF_SMALL);
  }

}
