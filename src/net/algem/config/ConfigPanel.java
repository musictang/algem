/*
 * @(#)ConfigPanel.java 2.11.0 27/09/16
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

import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 */
public abstract class ConfigPanel
  extends GemBorderPanel
 {

  protected Map<String, Config> confs;
  protected GemPanel content;

  public ConfigPanel(String title) {
    super(BorderFactory.createTitledBorder(title));
  }
  
  public ConfigPanel(String title, Map<String, Config> cm) {
    this(title);
    this.confs = cm;
  }

  public ConfigPanel(Border border, Map<String, Config> cm) {
    super(border);
    this.confs = cm;
  }
  
  public abstract List<Config> get();

}
