/*
 * @(#)GemMenu.java	2.8.t 16/05/14
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
package net.algem.util.menu;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.module.GemDesktop;


/**
 * Gem menu utility.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 1.0a 07/07/1999
 */
public abstract class GemMenu
        extends JMenu
        implements ActionListener
{

  protected DataCache dataCache;
  protected GemDesktop desktop;

  public GemMenu(String libelle, GemDesktop _desktop) {
    super(libelle);
    desktop = _desktop;
    dataCache = desktop.getDataCache();
  }

  public void setListener(JMenu menu) {
    JMenuItem item;

    for (int i = 0; i < menu.getItemCount(); i++) {
      item = menu.getItem(i);
      if (item instanceof JMenu) {
        setListener((JMenu) item);
      } else if (item instanceof JMenuItem) {
        item.addActionListener(this);
      }
    }
  }
  
  /**
   * Enables or disables an item if {@code key} is authorized for current user.
   * 
   * @param item
   * @param key
   * @return a menu item
   */
  JMenuItem getItem(JMenuItem item, String key) {
    if (!dataCache.authorize(key)) {
      item.setEnabled(false);
    }
    return item;
  }

  /**
   * Gets a new JMenuItem the label depending on key {@code id} in properties file.
   * 
   * @param id
   * @return a jMenuItem
   */
  JMenuItem getMenuItem(String id) {
    JMenuItem m = new JMenuItem(BundleUtil.getLabel(id + ".label"));
    m.setMnemonic(BundleUtil.getLabel(id + ".mnemo").charAt(0));
    m.getAccessibleContext().setAccessibleDescription(BundleUtil.getLabel(id + ".info"));
    m.setActionCommand(id);
    m.addActionListener(this);

    return m;
  }

  public JMenu createJMenu(String nom) {
    JMenu m = new JMenu(BundleUtil.getLabel(nom + ".label"));
    m.setMnemonic(BundleUtil.getLabel(nom + ".mnemo").charAt(0));

    return m;
  }
}
