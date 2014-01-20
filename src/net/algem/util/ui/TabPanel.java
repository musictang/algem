/*
 * @(#)TabPanel.java	2.8.r 17/01/14
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

import java.awt.Insets;
import java.awt.event.ActionListener;

/**
 * Generic tab panel.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 */
public class TabPanel
        extends javax.swing.JTabbedPane
{

  public final static Insets DEFAULT_INSETS = new Insets(2, 10, 2, 0);

  public TabPanel() {
  }

  public void addItem(java.awt.Component c, String title) {
    add(title, c);
  }

  /**
   * Adds a closeableTab component to the tab.
   * Closing action command is transmitted to listener.
   * @param index
   * @param listener
   */
  public void addCloseButton(int index, ActionListener listener) {
    setTabComponentAt(index, new CloseableTab(this, listener));
  }
  
  /**
   * Gets index of component by class name.
   * @param classname
   * @return
   */
  /*public int getIndexOfComponent(String classname) {
  Component [] components = getComponents();
  for(int i = 0 ; i < components.length ; i++) {
  if (classname.equals(components[i].getClass().getSimpleName())) {
  return i;
  }
  }
  return -1;
  }*/
}
