/*
 * @(#)StopCourseView.java	2.6.a 21/09/12
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
package net.algem.planning.editing;

import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GridBagHelper;

/**
 * View for stopping course.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 27/09/2001
 */
public class StopCourseView
        extends JPanel
{

  private DateFrField dateStart;
  private JLabel course;

  public StopCourseView(String c) {
    setLayout(new java.awt.GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    dateStart = new DateFrField(new Date());
    this.course = new JLabel(c);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new JLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel(BundleUtil.getLabel("From.label")), 0, 1, 1, 1, GridBagHelper.EAST);

    gb.add(course, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dateStart, 1, 1, 1, 1, GridBagHelper.WEST);

  }

  public DateFr getDateStart() {
    return dateStart.getDateFr();
  }
}
