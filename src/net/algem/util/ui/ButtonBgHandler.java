/*
 * @(#)ButtonBgHandler.java 2.9.6 16/03/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.util.ui;

import javax.swing.JButton;
import javax.swing.UIManager;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.6
 * @since 2.9.4.13 10/11/2015
 */
public class ButtonBgHandler
{

  private static final String LAF_NAME = UIManager.getLookAndFeel().getName();

  private ButtonBgHandler() {
  }

  public static void decore(JButton bt) {
    switch (LAF_NAME) {
      case "Metal":
      case "JGoodies Plastic":
      case "JGoodies Plastic 3D":
        bt.setOpaque(true);
        break;
      case "Windows":
      case "Windows Classic":
      case "GTK+":
      case  "GTK look and feel":
        //windows L&F workaround
        bt.setContentAreaFilled(false);
        bt.setOpaque(true);
        break;
    }
  }

  public static void reset(JButton bt) {
    if (bt != null) {
      bt.setBackground(null);
      bt.setContentAreaFilled(true);
      bt.setOpaque(false);
    }
  }
}
