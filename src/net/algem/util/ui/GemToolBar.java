/*
 * @(#)GemToolBar.java 2.6.f 12/11/12
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
package net.algem.util.ui;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import net.algem.util.ImageUtil;

/**
 * Generic tool bar.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.f
 * @since 2.1.j 10/06/11
 */
public class GemToolBar
        extends JToolBar
{

  private List<GemButton> buttons = new ArrayList<GemButton>();

  public GemToolBar() {
  }

  public GemToolBar(boolean floatable) {
    setFloatable(floatable);
  }

  public GemToolBar(int orientation, boolean floatable) {
    super(orientation);
    setFloatable(floatable);
  }

  public GemButton addIcon(String file, String label) {
    return addIcon(file, label, null);
  }

  public void addIcon(ActionListener l, String file, String label, String tooltip) {
    addIcon(file, label, tooltip).addActionListener(l);
  }
  
  public GemButton addIcon(String file, String label, String tooltip) {
    GemButton bt;

    String path = ImageUtil.IMAGE_PATH + file;
    java.net.URL s = getClass().getResource(path);
    if (file != null && file.length() > 1 && s != null) {
      ImageIcon img = new ImageIcon(s);
      bt = new GemButton(img);
    } else {
      bt = new GemButton(label);
    }

    bt.setActionCommand(label);
    if (tooltip != null) {
      bt.setToolTipText(tooltip);
    }
    buttons.add(bt);
      add(bt);
    return bt;
  }

  /**
   * Removes a button from bar.
   * 
   * @param label
   */
  public void removeIcon(String label) {
    for (Iterator<GemButton> it = buttons.iterator(); it.hasNext();) {
      GemButton bt = it.next();
      if (bt.getActionCommand().equals(label)) {
        buttons.remove(bt);
        remove(bt);
        revalidate();// et pas validate() comme auparavant
        break;
      }
    }
  }

  /**
   * Enables or disables the button with actionCommand {@code action}.
   * @param b activation
   * @param action
   */
  public void setEnabled(boolean b, String action) {
    for (Iterator it = buttons.iterator(); it.hasNext();) {
      GemButton bt = (GemButton) it.next();
      if (bt.getActionCommand().equals(action)) {
        bt.setEnabled(b);
        break;
      }
    }
  }

  public void addButton(String text, String label) {
    GemButton bt = new GemButton(text);
    bt.setActionCommand(label);
    buttons.add(bt);
    add(bt);
  }

  public void addButtonListener(ActionListener l) {
    for (GemButton bt : buttons) {
      bt.addActionListener(l);
    }
  }
}
