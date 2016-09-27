/*
 * @(#)ConfigEditor.java 2.11.0 27/09/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

package net.algem.config;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * General config editor.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.1.k
 */
public class ConfigEditor
        extends GemPanel
        implements ActionListener, ListSelectionListener
{
  
  public static final String GLOBAL_CONFIG_KEY = "Menu.configuration";
  private ConfigOrganization orgPanel;
  private ConfigPlanning activityPanel;
  private ConfigPanel adminPanel;
  private ConfigPanel filePanel;
  private ConfigPanel accountingPanel;
  private Map<String, Config> confs;
  private DataConnection dc;
  private DataCache dataCache;
  private GemPanel btPanel;
  private GemButton btValidation;
  private GemButton btClose;
  private GemDesktop desktop;
  private JList sectionList;
  private JPanel confPanel;
  
  public ConfigEditor() {
  }
  
  public ConfigEditor(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    this.dc = DataCache.getDataConnection();
    load();
  }

  /**
   * Loading of the configuration panels.
   */
  private void load() {
    setLayout(new BorderLayout());
    confPanel = new JPanel(new CardLayout());
    confPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    try {
      confs = ConfigIO.find(null, dc);
      orgPanel = new ConfigOrganization(BundleUtil.getLabel("ConfEditor.organization.label"), confs);
      activityPanel = new ConfigPlanning(confs);
      adminPanel = new ConfigAdmin(BundleUtil.getLabel("ConfEditor.management.label"), confs);
      ((ConfigAdmin) adminPanel).init(dataCache);
      filePanel = new ConfigFile(BundleUtil.getLabel("ConfEditor.file.label"), confs);
      accountingPanel = new ConfigAccounting(BundleUtil.getLabel("ConfEditor.accounting.label"), confs);
      confPanel.add(orgPanel, "organization");
      confPanel.add(activityPanel, "schedule");
      confPanel.add(adminPanel, "management");
      confPanel.add(filePanel, "files");
      confPanel.add(accountingPanel, "accounting");
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    
    String[] sections = {
      BundleUtil.getLabel("ConfEditor.organization.label"),
      BundleUtil.getLabel("ConfEditor.schedule.label"),
      BundleUtil.getLabel("ConfEditor.management.label"),
      BundleUtil.getLabel("ConfEditor.file.label"),
      BundleUtil.getLabel("ConfEditor.accounting.label"),};
    sectionList = new JList(sections);
    sectionList.setSelectedIndex(0);
    sectionList.addListSelectionListener(this);
    
    JScrollPane scroll = new JScrollPane(confPanel);
    scroll.setMinimumSize(new Dimension(550, scroll.getPreferredSize().height));
    
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sectionList, scroll);
    splitPane.setDividerLocation(150);
    
    add(splitPane, BorderLayout.CENTER);
    btPanel = new GemPanel();
    btPanel.setLayout(new GridLayout(1, 1));
    btValidation = new GemButton(GemCommand.SAVE_CMD);
    btClose = new GemButton(GemCommand.CANCEL_CMD);
    btValidation.addActionListener(this);
    btClose.addActionListener(this);
    btPanel.add(btValidation);
    btPanel.add(btClose);
    
    add(btPanel, BorderLayout.SOUTH);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btValidation) {
      try {
        for (Config c : orgPanel.get()) {
          confs.put(c.getKey(), c);
        }
        for (Config c : activityPanel.get()) {
          confs.put(c.getKey(), c);
        }
        for (Config c : adminPanel.get()) {
          confs.put(c.getKey(), c);
        }
        for (Config c : filePanel.get()) {
          confs.put(c.getKey(), c);
        }
        for (Config c : accountingPanel.get()) {
          confs.put(c.getKey(), c);
        }
        ConfigIO.update(confs, dc);
        dataCache.setConfig();// mise à jour du dataCache
      } catch (SQLException ex) {
        GemLogger.logException(MessageUtil.getMessage("config.update.exception"), ex);
      }
    }
    close();
  }
  
  @Override
  public void valueChanged(ListSelectionEvent e) {
    JList list = (JList) e.getSource();
    CardLayout cl = (CardLayout) (confPanel.getLayout());
    switch (list.getSelectedIndex()) {
      case 0:
        cl.show(confPanel, "organization");
        break;
      case 1:
        cl.show(confPanel, "schedule");
        break;
      case 2:
        cl.show(confPanel, "management");
        break;
      case 3:
        cl.show(confPanel, "files");
        break;
      case 4:
        // panneau infos bancaires
        cl.show(confPanel, "accounting");
        break;
      default:
        cl.show(confPanel, "organization");
    }
  }
  
  private void close() {
    confs.clear();
    desktop.removeModule(GLOBAL_CONFIG_KEY);
  }
  
}
