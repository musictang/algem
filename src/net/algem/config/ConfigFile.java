/*
 * @(#)ConfigFile.java 2.1.k 07/07/11
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

package net.algem.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.ui.FilePanel;
import net.algem.util.ui.GemPanel;

/**
 * Default paths config.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 2.1.k
 */
public class ConfigFile
  extends ConfigPanel
{
  private FilePanel logFilePanel;
  private FilePanel exportFilePanel;
  private Config c1, c2;

  public ConfigFile(String title, Map<String, Config> cm) {
    super(title, cm);
    init();
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(FileUtil.escapeBackSlashes(logFilePanel.getText()));
    c2.setValue(FileUtil.escapeBackSlashes(exportFilePanel.getText()));

    conf.add(c1);
    conf.add(c2);

    return conf;
  }

  private void init() {
    c1 = confs.get(ConfigKey.LOG_PATH.getKey());
    c2 = confs.get(ConfigKey.EXPORT_PATH.getKey());
    
    content = new GemPanel();
    content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));

    logFilePanel = new FilePanel(BundleUtil.getLabel("ConfEditor.log.path.label"),c1.getValue());
    logFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.log.path.tip"));
    exportFilePanel = new FilePanel(BundleUtil.getLabel("ConfEditor.export.path.label"),c2.getValue());
    exportFilePanel.setToolTipText(BundleUtil.getLabel("ConfEditor.export.path.tip"));

    content.add(logFilePanel);
    content.add(exportFilePanel);
    
    add(content);
  }

}
