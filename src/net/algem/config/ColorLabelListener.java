/*
 * @(#)ColorLabelListener.java	2.6.a 25/09/12
 *
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.*
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
 */
package net.algem.config;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import net.algem.util.MessageUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
class ColorLabelListener
        extends MouseAdapter
{

  private String title = MessageUtil.getMessage("prefs.modify.text.color.tip");
  private JLabel jl;

  @Override
  public void mouseClicked(MouseEvent e) {
    jl = (JLabel) e.getSource();
    Color c = JColorChooser.showDialog(jl, title, jl.getForeground());
    jl.setForeground(c);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    jl = (JLabel) e.getSource();
    jl.setBorder(BorderFactory.createLineBorder(Color.darkGray, 2));
  }

  @Override
  public void mouseExited(MouseEvent e) {
    jl.setBorder(null);
  }
}
