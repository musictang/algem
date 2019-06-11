/*
 * @(#)GemScrollPane.java	2.8.v 13/06/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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


package net.algem.util.ui;

import java.awt.Component;
import javax.swing.JScrollPane;

/**
 * Subclass of JScrollPane to set the auto-increment of vertical and/or horizontal bars.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 13/06/14
 */
public class GemScrollPane
  extends JScrollPane
{

  private static final int VERTICAL_INCREMENT = 20;
  private static final int HORIZONTAL_INCREMENT = 20;

  public GemScrollPane() {
    setIncrement(HORIZONTAL_INCREMENT, VERTICAL_INCREMENT);
  }

  public GemScrollPane(Component view) {
    super(view);
    setIncrement(HORIZONTAL_INCREMENT, VERTICAL_INCREMENT);
  }

  public GemScrollPane(int vsbPolicy, int hsbPolicy) {
    super(vsbPolicy, hsbPolicy);
    setIncrement(HORIZONTAL_INCREMENT, VERTICAL_INCREMENT);
  }

  public GemScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    super(view, vsbPolicy, hsbPolicy);
    setIncrement(HORIZONTAL_INCREMENT, VERTICAL_INCREMENT);
  }

  private void setIncrement(int h, int v) {
    getHorizontalScrollBar().setUnitIncrement(h);
    getVerticalScrollBar().setUnitIncrement(v);
  }

}
