/*
 * @(#)FileTabView.java	2.9.1 12/12/14
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

import java.awt.event.ActionListener;
import net.algem.util.BundleUtil;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.TabPanel;

/**
 * Base class used to display dossiers.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.3.c 21/03/12
 */
public class FileTabView
        extends DefaultGemView
{

  public static final String HISTO_INVOICE_TAB_TITLE = BundleUtil.getLabel("Invoice.tab.label");
  public static final String INVOICE_TAB_TITLE = BundleUtil.getLabel("Invoice.label");
  public static final String ESTIMATE_TAB_TITLE = BundleUtil.getLabel("Quotation.label");
  public static final String HISTO_ESTIMATE_TAB_TITLE = BundleUtil.getLabel("Menu.quotation.history.label");

  protected TabPanel wTab;
  protected ActionListener listener;

  public FileTabView(GemDesktop desktop) {
    super(desktop);
  }

  public FileTabView(GemDesktop desktop, String label) {
    super(desktop, label);
  }

  /**
   * Removes a tab.
   * @param tab
   */
  public void removeTab(FileTab tab) {
    wTab.remove(tab);
    wTab.setSelectedIndex(0);
  }

  public void addTab(FileTab tab) {
    wTab.setSelectedComponent(tab);
    wTab.addCloseButton(wTab.getSelectedIndex(), listener);
  }

  /**
   * Adds a tab with closing button.
   * Tab is automatically selected after adding.
   * @param tab
   * @param label tab title
   */
  public void addTab(FileTab tab, String label) {
    wTab.addItem(tab, label);
    wTab.setSelectedComponent(tab);
    wTab.addCloseButton(wTab.getSelectedIndex(), listener);
  }

  /**
   * Adds a tab and selects the tab if <code>selectionFlag</code> is true.
   * @param tab
   * @param label tab title
   * @param selectionFlag if true, tab is selected
   */
  protected void addTab(FileTab tab, String label, boolean selectionFlag) {
    wTab.addItem(tab, label);
    if (selectionFlag) {
      wTab.setSelectedComponent(tab);
    }
    wTab.addCloseButton(wTab.indexOfTab(label), listener);
  }


  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @Override
  public void setSelectedTab(int idx) {
    wTab.setSelectedIndex(idx);
  }

}
