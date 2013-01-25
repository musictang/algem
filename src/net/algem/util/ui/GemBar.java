/*
 * @(#)Icon.java	2.6.a 31/07/12
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

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ImageIcon;
import net.algem.util.ImageUtil;

/**
 * Icon bar.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class GemBar extends GemBorderPanel
{

  Vector<GemButton> buttons;

  public GemBar() {
    setLayout(new FlowLayout(FlowLayout.LEFT));
    buttons = new Vector<GemButton>();
  }

  public GemButton addIcon(String file, String label) {
    return addIcon(file, label, null);
  }

  public GemButton addIcon(String file, String label, String tip) {
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
    if (tip != null) {
      bt.setToolTipText(tip);
    }
    buttons.addElement(bt);
    add(bt);
    return bt;
  }

  public void addIcon(ActionListener l, String file, String label, String tip) {
    addIcon(file, label, tip).addActionListener(l);
  }

  /**
   * Removes a button from the bar.
   * @param label
   */
  public void removeIcon(String label) {
//    System.out.println("removing icon " + label);
    for (Iterator it = buttons.iterator(); it.hasNext();) {
      GemButton bt = (GemButton) it.next();
      if (bt.getActionCommand().equals(label)) {
        buttons.remove(bt);
        remove(bt);
        validate();
        break;
      }
    }
  }

  /**
   * Enables the button with actionCommand {@code action}.
   * @param action
   */
  public void enableButton(String action) {
    for (Iterator it = buttons.iterator(); it.hasNext();) {
      GemButton bt = (GemButton) it.next();
      if (bt.getActionCommand().equals(action)) {
        bt.setEnabled(true);
        break;
      }
    }
  }

  public void addButton(String letter, String label) {
    GemButton bt = new GemButton(letter);
    bt.setActionCommand(label);
    buttons.addElement(bt);
    add(bt);
  }

  public void addButtonListener(ActionListener l)
  {
    Enumeration e = buttons.elements();
    while (e.hasMoreElements()) {
      GemButton bt = (GemButton) e.nextElement();
      bt.addActionListener(l);
    }
  }

}
