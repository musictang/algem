/*
 * @(#)MenuHelp.java 2.6.a 12/10/2012
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

package net.algem.util.menu;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import net.algem.util.About;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.HtmlViewer;
import net.algem.util.ui.MessagePopup;

/**
 * Help menu.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 14/05/2003
 */
public class MenuHelp extends GemMenu
{

  private HtmlViewer viewer = null;
  private JMenuItem miAbout;
  private JMenuItem miCurrent;
  private JMenuItem miIndex;
  private JMenuItem miSearch;

  public MenuHelp(GemDesktop _desktop) {
    super(BundleUtil.getLabel("Menu.help.label"), _desktop);

    //add(miIndex = new JMenuItem(BundleUtil.getLabel("Menu.help.index.label"), 'i'));
    //add(miSearch = new JMenuItem(BundleUtil.getLabel("Action.search.label"), 'r'));
    //add(miCurrent = new JMenuItem(BundleUtil.getLabel("Menu.help.current.label"), 'c'));
    //addSeparator();
    add(miAbout = new JMenuItem(BundleUtil.getLabel("About.label"), 'p'));

    setListener(this);

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Object source = evt.getSource();

    desktop.setWaitCursor();

    if (source == miAbout) {
//      MessagePopup.information(this,"Algem, version : "+ dataCache.getVersion());
      JDialog about = new About(desktop, "Algem [version " + desktop.getDataCache().getVersion() + "]");
    } /*else if (source == miIndex) {
      showHelp("index.html");
    } else if (source == miSearch) {
    } else if (source == miCurrent) {
    }*/
    desktop.setDefaultCursor();
  }

  void showHelp(String url) {
    String urlPrefix = BundleUtil.getLabel("Url.doc.location");
    if (viewer == null || !viewer.isVisible()) {
      viewer = new HtmlViewer(urlPrefix + url);
      viewer.setVisible(true);
    } else {
      try {
        viewer.linkActivated(new URL(urlPrefix + url));
      } catch (Exception e) {
      }
    }

  }
}
