/*
 * @(#)ConfigFile.java 2.11.0 28/09/16
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import net.algem.util.BundleUtil;
import net.algem.util.ui.FilePanel;
import net.algem.util.ui.GemPanel;

/**
 * Default paths config.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.1.k
 */
public class ConfigFile
  extends ConfigPanel
{
  private FilePanel logFilePanel;
  private FilePanel exportFilePanel;
  private FilePanel photosFilePanel;
  private FilePanel groupsFilePanel;
  private FilePanel employeesFilePanel;
  private FilePanel scriptFilePanel;
  private FilePanel invoiceFooterPanel;
  
  private Config c1, c2, c3, c4, c5, c6, c7;

  public ConfigFile(String title, Map<String, Config> cm) {
    super(title, cm);
    init();
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(logFilePanel.getText());
    c2.setValue(exportFilePanel.getText());
    c3.setValue(photosFilePanel.getText());
    c4.setValue(groupsFilePanel.getText());
    c5.setValue(employeesFilePanel.getText());
    c6.setValue(invoiceFooterPanel.getText());
    c7.setValue(scriptFilePanel.getText());

    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    conf.add(c6);
    conf.add(c7);

    return conf;
  }

  private void init() {
    c1 = confs.get(ConfigKey.LOG_PATH.getKey());
    c2 = confs.get(ConfigKey.EXPORT_PATH.getKey());
    c3 = confs.get(ConfigKey.PHOTOS_PATH.getKey());
    c4 = confs.get(ConfigKey.GROUPS_PATH.getKey());
    c5 = confs.get(ConfigKey.EMPLOYEES_PATH.getKey());
    c6 = confs.get(ConfigKey.INVOICE_FOOTER.getKey());
    c7 = confs.get(ConfigKey.SCRIPTS_PATH.getKey());

    content = new GemPanel();
    content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));

    logFilePanel = new FilePanel(ConfigKey.LOG_PATH.getLabel(),c1.getValue());
    logFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.log.path.tip"));
    exportFilePanel = new FilePanel(ConfigKey.EXPORT_PATH.getLabel(),c2.getValue());
    exportFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.export.path.tip"));
    photosFilePanel = new FilePanel(ConfigKey.PHOTOS_PATH.getLabel(),c3.getValue());
    photosFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.photos.path.tip"));
    groupsFilePanel = new FilePanel(ConfigKey.GROUPS_PATH.getLabel(),c4.getValue());
    groupsFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.groups.path.tip"));
    employeesFilePanel = new FilePanel(ConfigKey.EMPLOYEES_PATH.getLabel(),c5.getValue());
    employeesFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.employees.path.tip"));
    scriptFilePanel = new FilePanel(ConfigKey.SCRIPTS_PATH.getLabel(),c7.getValue());
    scriptFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.scripts.path.tip"));
    invoiceFooterPanel = new FilePanel(ConfigKey.INVOICE_FOOTER.getLabel(),c6.getValue(), false);
    invoiceFooterPanel.setToolTipText(BundleUtil.getLabel("ConfEditor.invoice.footer.tip"));

    content.add(logFilePanel);
		content.add(Box.createVerticalStrut(4));
    content.add(exportFilePanel);
		content.add(Box.createVerticalStrut(4));
    content.add(photosFilePanel);
		content.add(Box.createVerticalStrut(4));
    content.add(groupsFilePanel);
		content.add(Box.createVerticalStrut(4));
    content.add(employeesFilePanel);
    content.add(Box.createVerticalStrut(4));
    content.add(scriptFilePanel);
		content.add(Box.createVerticalStrut(4));
    content.add(invoiceFooterPanel);

    add(content);
  }

}
