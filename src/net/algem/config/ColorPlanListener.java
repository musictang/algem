/*
 * @(#)ColorPlanListener.java	2.6.a 25/09/12
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
package net.algem.config;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import net.algem.util.MessageUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ColorPlanListener
        extends MouseAdapter
{

  private String title = MessageUtil.getMessage("prefs.modify.bg.color.tip");
  private JPanel p;
  private Cursor hand = new Cursor(Cursor.HAND_CURSOR);

  @Override
  public void mouseClicked(MouseEvent e) {
    p = (JPanel) e.getSource();
    Color c = JColorChooser.showDialog(p, title, p.getBackground());
    if (c != null) {
      p.setBackground(c);
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    p = (JPanel) e.getSource();
    p.setCursor(hand);
    //p.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.WHITE, Color.GRAY));
  }

  @Override
  public void mouseExited(MouseEvent e) {
    p = (JPanel) e.getSource();
    p.setCursor(Cursor.getDefaultCursor());
    //p.setBorder(null);
  }
}
