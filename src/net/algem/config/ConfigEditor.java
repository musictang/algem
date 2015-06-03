/*
 * @(#)ConfigEditor.java 2.9.4.6 02/06/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemScrollPane;

/**
 * General config editor.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 2.1.k
 */
public class ConfigEditor
  extends GemPanel implements ActionListener {

  public static final String GLOBAL_CONFIG_KEY = "Menu.configuration";
  private ConfigOrganization orgPanel;
  private ConfigPlanning activityPanel;
  private ConfigPanel adminPanel;
  private ConfigPanel filePanel;
  private ConfigPanel ribPanel;
  private Map<String,Config> confs;
  private DataConnection dc;
  private DataCache dataCache;
  private GemPanel btPanel;
  private GemButton btValidation;
  private GemButton btClose;
  private GemDesktop desktop;

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
    GemPanel content = new GemPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

    try {
      confs = ConfigIO.find(null, dc);

      orgPanel = new ConfigOrganization(BundleUtil.getLabel("ConfEditor.organization.label"), confs);
      content.add(orgPanel);
      activityPanel = new ConfigPlanning(BundleUtil.getLabel("ConfEditor.schedule.label"), confs);
      content.add(activityPanel);
      adminPanel = new ConfigAdmin(BundleUtil.getLabel("ConfEditor.management.label"), confs);
      ((ConfigAdmin) adminPanel).init(dataCache);
      content.add(adminPanel);
      filePanel = new ConfigFile(BundleUtil.getLabel("ConfEditor.file.label"), confs);
      content.add(filePanel);
      // panneau infos bancaires
      ribPanel = new ConfigCreditor(BundleUtil.getLabel("ConfEditor.accounting.label"), confs);
      content.add(ribPanel);
    } catch (SQLException ex) {
        GemLogger.logException(ex);
    }
    JScrollPane sp = new GemScrollPane(content);
    sp.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    add(sp,BorderLayout.CENTER);
    btPanel = new GemPanel();
    btPanel.setLayout(new GridLayout(1,1));
    btValidation = new GemButton(GemCommand.SAVE_CMD);
    btClose = new GemButton(GemCommand.CANCEL_CMD);
    btValidation.addActionListener(this);
    btClose.addActionListener(this);
    btPanel.add(btValidation);
    btPanel.add(btClose);
    add(btPanel, BorderLayout.SOUTH);
    setSize(GemModule.DEFAULT_SIZE);
    //pack();
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
        for (Config c : ribPanel.get()) {
          confs.put(c.getKey(), c);
        }
        ConfigIO.update(confs, dc);
        dataCache.setConfig();// mise à jour du dataCache
      } catch (SQLException ex) {
          GemLogger.logException(MessageUtil.getMessage("config.update.exception"),ex);
      }
    }
    close();
  }

   private void close() {
     confs.clear();
     desktop.removeModule(GLOBAL_CONFIG_KEY);
   }

}

