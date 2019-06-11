/*
 * @(#)WishItemElement.java 2.17.0 20/03/19
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
import net.algem.planning.wishes.EnrolmentWish;
import net.algem.planning.wishes.Hour;
import net.algem.util.ImageUtil;

/**
 * Wish item element.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 20/03/19
 */
public class WishItemElement
        extends DrawableElement
{
  public static final int MARGIN = 50;
  public static final int TABLE_WIDTH = ImageUtil.mmToPoints(180);
  public static final int FIRST_COL_WIDTH = ImageUtil.mmToPoints(15); //préférence
  public static final int xColCourse = MARGIN + FIRST_COL_WIDTH;
  public static final int xColDay = xColCourse + 140;
  public static final int xColHour = xColDay + 50;
  public static final int xColDuration = xColHour + 40;
  public static final int xColTeacher = xColDuration + 40;
  
  protected int end;
  private EnrolmentWish wish;
  private int offset;

  public WishItemElement(int x, int y) {
    super(x, y);
    end = x + TABLE_WIDTH;
  }

  public WishItemElement(int x, int y, EnrolmentWish wish) {
    this(x, y);
    this.wish = wish;
  }

  @Override
  public void draw(Graphics g) {
    setFont(g);
    String preference = String.valueOf(wish.getPreference());
    String course = wish.getCourseLabel();
    String day = wish.getDayLabel();
    Hour hour = wish.getHour();
    Hour duration = wish.getDuration();
    String teacher = wish.getTeacherLabel();

    int topOffset = 15;

    g.drawString(preference, x + 5, y + topOffset);
    g.drawString(course, xColCourse+2, y + topOffset);
    g.drawString(day, xColDay+2, y + topOffset);
    g.drawString(hour.toString(), xColHour+2, y + topOffset);
    g.drawString(duration.toString(), xColDuration+2, y + topOffset);
    g.drawString(teacher, xColTeacher+2, y + topOffset);

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
