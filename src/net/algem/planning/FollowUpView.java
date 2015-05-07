/*
 * @(#)FollowUpView.java	2.9.4.4 06/05/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemTextArea;
import net.algem.util.ui.GridBagHelper;


/**
 * View for followup editing.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.4
 */
public class FollowUpView
        extends GemBorderPanel
{

  private JLabel course;
  private GemTextArea textArea;

  public FollowUpView(String courseName, DateFr date, Hour start, Hour end) {
    course = new JLabel(courseName);
    JLabel detail = new JLabel(date + " " + start + "-" + end);

    textArea = new GemTextArea(3, 30);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(course, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(detail, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(textArea, 0, 2, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
  }

  public FollowUpView(DateFr date, Hour start, Hour end) {
    this("", date,start, end);
  }

  public void set(String courseName, String txt) {
    course.setText(courseName);
    textArea.setText(txt);
  }

  public void setText(String txt) {
    textArea.setText(txt);
  }

  public String getText() {
    return textArea.getText();
  }

  public void clear() {
    course.setText("");
    textArea.clear();
  }
}
