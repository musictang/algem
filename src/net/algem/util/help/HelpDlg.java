/*
 * @(#)HelpDlg.java 2.8.w 10/09/14
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
package net.algem.util.help;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

/**
 * Help viewer.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 08/09/14
 */
public class HelpDlg
        extends AbstractHelpDlg
        implements ListSelectionListener
{

  private JList sectionList;
  private HtmlEditor editorPane;

  public HelpDlg(GemDesktop desktop, String title) {
    super(desktop.getFrame(), title);

    setSize(650, 600);
    double w = getSize().getWidth();
    setLocation(desktop.getFrame().getWidth() - (int) w, 100);
    setVisible(true);
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    JList list = (JList) e.getSource();
    URL helpURL = null;
    switch (list.getSelectedIndex()) {
      case 0:
        helpURL = getResource("introduction.html");
        break;
      case 1:
        helpURL = getResource("file.html");
        break;
      case 2:
        helpURL = getResource("windows.html");
        break;
      case 3:
        helpURL = getResource("catalog.html");
        break;
      case 4:
        helpURL = getResource("search.html");
        break;
      case 5:
        helpURL = getResource("schedule.html");
        break;
      case 6:
        helpURL = getResource("accounting.html");
        break;
      case 7:
        helpURL = getResource("configuration.html");
        break;
      case 8:
        helpURL = getResource("shortcuts.html");
        break;
    }
    if (helpURL != null) {
      try {
        editorPane.setPage(helpURL);
      } catch (IOException ex) {
        GemLogger.log(ex.getMessage());
      }
    } else {
      GemLogger.log("NULL URL");
    }
  }

  @Override
  protected Component getContent() {
    String[] sections = {
      BundleUtil.getLabel("Introduction.label"),
      BundleUtil.getLabel("Menu.file.label"),
      BundleUtil.getLabel("Menu.windows.label"),
      BundleUtil.getLabel("Menu.catalog.label"),
      BundleUtil.getLabel("Search.label"),
      BundleUtil.getLabel("Menu.schedule.label"),
      BundleUtil.getLabel("Menu.accounting.label"),
      BundleUtil.getLabel("Menu.configuration.label"),
      BundleUtil.getLabel("Shortcuts.label")
    };
    sectionList = new JList(sections);
    sectionList.setSelectedIndex(0);
    sectionList.addListSelectionListener(this);
    editorPane = new HtmlEditor("text/html", null);
    try {
      editorPane.setPage(getResource("introduction.html"));
    } catch (IOException ex) {
      GemLogger.log(ex.getMessage());
    }
    
    JScrollPane scroll = new JScrollPane(editorPane);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    //Provide minimum sizes for the two components in the split pane
    Dimension minimumSize = new Dimension(150, 200);
    sectionList.setMinimumSize(minimumSize);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sectionList, scroll);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(150);
    
    return splitPane;
  }

}
