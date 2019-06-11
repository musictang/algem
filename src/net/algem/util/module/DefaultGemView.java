/*
 * @(#)DefaultGemView.java 2.8.w 27/08/14
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
package net.algem.util.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ImageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.GemCloseVetoException;

/**
 * Basic algem internal frame.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 27/08/14
 */
public class DefaultGemView 
  extends JInternalFrame 
  implements GemView
  
{

  protected GemDesktop desktop;
  protected DataCache dataCache;
  protected ImageIcon icon;
  private String label;

  /**
   *
   * @param desktop
   * @param label title
   */
  public DefaultGemView(GemDesktop desktop, String label) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();

    this.label = label;
    setTitle(BundleUtil.getLabel(label + ".label") + " ");

    setResizable(true);
    setMaximizable(true);
    setIconifiable(true);
    setClosable(true);
    icon = ImageUtil.createImageIcon(BundleUtil.getLabel(label + ".icon"));
    if (icon != null) {
      setFrameIcon(icon);
    }

  }

  public DefaultGemView(GemDesktop desktop) {
    this(desktop, "GemVue");
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setDesktop(GemDesktop desktop) {
    this.desktop = desktop;
  }

  public GemDesktop getDesktop() {
    return desktop;
  }

  public JMenuItem getMenuItem(ActionListener l, String id) {
    JMenuItem m = new JMenuItem(BundleUtil.getLabel(id + ".label"));
    m.setMnemonic(BundleUtil.getLabel(id + ".mnemo").charAt(0));
    m.getAccessibleContext().setAccessibleDescription(BundleUtil.getLabel(id + ".info"));
    m.setActionCommand(id);
    m.addActionListener(l);

    return m;
  }

  public JMenu createJMenu(String nom) {
    JMenu m = new JMenu(BundleUtil.getLabel(nom + ".label"));
    m.setMnemonic(BundleUtil.getLabel(nom + ".mnemo").charAt(0));

    return m;
  }

  @Override
  public void postEvent(GemEvent evt) { }
  
  @Override
  public void actionPerformed(ActionEvent e) { }
  
  @Override
  public void addActionListener(ActionListener l) { }
   
  @Override
  public void removeActionListener(ActionListener l) { }

  @Override
  public void print() { }
  
  @Override
  public void setSelectedTab(int tabIndex) { }

  @Override
  public void close() throws GemCloseVetoException { }
}
