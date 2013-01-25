/*
 * @(#)ConfigAdmin.java 2.7.a 03/12/12
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import net.algem.room.EstabChoice;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Panel for config and administrative tasks.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.1.k
 */
public class ConfigAdmin
        extends ConfigPanel
{

  private Config c1, c2, c3, c4, c5;
  private JCheckBox jc1, jc2, jc3;
  private ParamChoice ecoleChoix;
  private EstabChoice etabChoix;

  public ConfigAdmin(String title, Map<String, Config> cm) {
    super(title, cm);
  }

  public void init(DataCache dataCache) {

    c1 = confs.get(ConfigKey.TEACHER_MANAGEMENT.getKey());
    c2 = confs.get(ConfigKey.COURSE_MANAGEMENT.getKey());
    c3 = confs.get(ConfigKey.WORKSHOP_MANAGEMENT.getKey());
    c4 = confs.get(ConfigKey.DEFAULT_SCHOOL.getKey());
    c5 = confs.get(ConfigKey.DEFAULT_ESTABLISHMENT.getKey());

    content = new GemPanel();

    jc1 = new JCheckBox(ConfigKey.TEACHER_MANAGEMENT.getLabel());
    jc2 = new JCheckBox(ConfigKey.COURSE_MANAGEMENT.getLabel());
    jc3 = new JCheckBox(ConfigKey.WORKSHOP_MANAGEMENT.getLabel());

    ecoleChoix = new ParamChoice(ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dataCache.getDataConnection()));
    ecoleChoix.setValue(c4.getValue());

    etabChoix = new EstabChoice(dataCache.getList(Model.Establishment));
    etabChoix.setKey(Integer.parseInt(c5.getValue()));

    jc1.setSelected(isSelected(c1.getValue()));
    jc2.setSelected(isSelected(c2.getValue()));
    jc3.setSelected(isSelected(c3.getValue()));

    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.add(jc1);
    content.add(jc2);
    content.add(jc3);
    
    GemPanel p = new GemPanel();
    p.add(new GemLabel(ConfigKey.DEFAULT_SCHOOL.getLabel()));
    p.add(ecoleChoix);
    p.add(Box.createHorizontalGlue());
    p.add(new GemLabel(ConfigKey.DEFAULT_ESTABLISHMENT.getLabel()));
    p.add(etabChoix);
    content.add(p);

    add(content);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(getValue(jc1));
    c2.setValue(getValue(jc2));
    c3.setValue(getValue(jc3));
    c4.setValue(((Param)ecoleChoix.getSelectedItem()).getValue());
    c5.setValue(String.valueOf(etabChoix.getKey()));

    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);

    return conf;
  }

  private boolean isSelected(String conf) {
    return conf.startsWith("t");
  }

  private String getValue(JCheckBox box) {
    return box.isSelected() ? "t" : "f";
  }
}
