/*
 * @(#)GemView.java	2.6.a 25/09/12
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

package net.algem.util.module;

import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ImageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.GemCloseVetoException;

/**
 * Module view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/2002
 */
//XXX à mettre en interface
// GemVueMustang implements GemView
public class GemView extends JInternalFrame
        implements GemEventListener
{

  protected GemDesktop desktop;
  protected DataCache dataCache;
  protected ImageIcon icon;
  private String label;

  /**
   *
   * @param _desktop
   * @param _label titre label
   */
  public GemView(GemDesktop _desktop, String _label) {
    desktop = _desktop;
    dataCache = desktop.getDataCache();

    label = _label;
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

  public GemView(GemDesktop _desktop) {
    this(_desktop, "GemVue");
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String _label) {
    label = _label;
  }

  public void setDesktop(GemDesktop _desktop) {
    desktop = _desktop;
  }

  public GemDesktop getDesktop() {
    return desktop;
  }

  // à redéfinir dans les modules
  @Override
  public void postEvent(GemEvent evt) {
  }

  // à redéfinir dans les modules
  public void addActionListener(ActionListener l) {
  }

  // à redéfinir dans les modules
  public void print() {
  }

  // à redéfinir dans les modules
  public void close() throws GemCloseVetoException {
  }

  public void removeActionListener(ActionListener l) {
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
  //FIXME EG 08/2008
  // Redéfinie dans TableauJourVue
  public void setSelectedTab(int tabIndex) {
  }
}
