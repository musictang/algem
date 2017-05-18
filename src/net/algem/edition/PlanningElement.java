/*
 * @(#)PlanningElement.java 2.13.3 18/05/17
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

import java.awt.Graphics;
import net.algem.util.TextUtil;

/**
 * Schedule info element.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.3
 * @since 2.6.a
 */
class PlanningElement extends DrawableElement
{

  private PlanningInfo info;

  public PlanningElement(PlanningInfo info, int x, int y) {
    super(x, y);
    this.info = info;
  }

  @Override
  protected void draw(Graphics g) {
    g.setFont(SERIF_XSMALL);
    String course = TextUtil.crop(info.getCourse(), g, 160);
    String teacher = TextUtil.crop(info.getTeacher(), g, 160);
    g.drawString(course, x, y);
    g.drawString(teacher, x + 160, y);
    g.drawString(info.getDay(), x + 320, y);
    String time = info.getStart() + "-" + info.getEnd();
    g.drawString(time, x + 365, y);
//    g.drawString(info.getEnd(), x + 395, y);
  }
}
