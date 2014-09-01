/*
 * @(#)ConfigAdmin.java 2.8.w 23/07/14
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
package net.algem.config;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.border.Border;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.enrolment.PayFrequency;
import net.algem.room.EstabChoice;
import net.algem.room.RoomActiveChoiceModel;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Panel for config and administrative tasks.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.1.k
 */
public class ConfigAdmin
        extends ConfigPanel
{

  private Config c1, c2, c3, c4, c5, c6, c7;
  private JCheckBox jc1, jc2, jc3;
  private ParamChoice school;
  private EstabChoice estab;
  private RoomChoice studio;
  private JComboBox rateFrequency;

  public ConfigAdmin(String title, Map<String, Config> cm) {
    super(title, cm);
  }

  public void init(DataCache dataCache) {

    c1 = confs.get(ConfigKey.TEACHER_MANAGEMENT.getKey());
    c2 = confs.get(ConfigKey.COURSE_MANAGEMENT.getKey());
//    c3 = confs.get(ConfigKey.WORKSHOP_MANAGEMENT.getKey());
    c4 = confs.get(ConfigKey.DEFAULT_SCHOOL.getKey());
    c5 = confs.get(ConfigKey.DEFAULT_ESTABLISHMENT.getKey());
    c6 = confs.get(ConfigKey.DEFAULT_STUDIO.getKey());
    c7 = confs.get(ConfigKey.BASIC_RATE_FREQUENCY.getKey());

    content = new GemPanel();
    
    Border checkBorder = BorderFactory.createEmptyBorder(0,0,5,5);
    jc1 = new JCheckBox(ConfigKey.TEACHER_MANAGEMENT.getLabel());
    jc1.setBorder(checkBorder);
    jc2 = new JCheckBox(ConfigKey.COURSE_MANAGEMENT.getLabel());
    jc2.setBorder(checkBorder);
//    jc3 = new JCheckBox(ConfigKey.WORKSHOP_MANAGEMENT.getLabel());

    school = new ParamChoice(dataCache.getList(Model.School).getData());
    school.setKey(Integer.parseInt(c4.getValue()));

    estab = new EstabChoice(dataCache.getList(Model.Establishment));
    estab.setKey(Integer.parseInt(c5.getValue()));

    studio = new RoomChoice(new RoomActiveChoiceModel(dataCache.getList(Model.Room), true));
    studio.setKey(Integer.parseInt(c6.getValue()));

    jc1.setSelected(isSelected(c1.getValue()));
    jc2.setSelected(isSelected(c2.getValue()));
//    jc3.setSelected(isSelected(c3.getValue()));
    
    rateFrequency = new JComboBox(new Enum[]{PayFrequency.MONTH, PayFrequency.QUARTER, PayFrequency.YEAR});
    String frequencyTip = BundleUtil.getLabel("ConfEditor.basic.rate.frequency.tip");
    rateFrequency.setToolTipText(frequencyTip);
    String frequency = ConfigUtil.getConf(ConfigKey.BASIC_RATE_FREQUENCY.getKey());
    rateFrequency.setSelectedItem(ModuleOrderIO.getFrequencyByName(frequency));

    Box box1 = Box.createHorizontalBox();
    box1.add(jc1);
    box1.add(Box.createHorizontalGlue());
    Box box2 = Box.createHorizontalBox();
    box2.add(jc2);
    box2.add(Box.createHorizontalGlue());

    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.add(box1);
    content.add(box2);

    GemPanel defs = new GemPanel(new GridLayout(4,2,0,5));

    defs.add(new GemLabel(ConfigKey.DEFAULT_SCHOOL.getLabel()));
    defs.add(school);

    defs.add(new GemLabel(ConfigKey.DEFAULT_ESTABLISHMENT.getLabel()));
    defs.add(estab);

    defs.add(new GemLabel(ConfigKey.DEFAULT_STUDIO.getLabel()));
    defs.add(studio);
    GemLabel frequencyLabel = new GemLabel(ConfigKey.BASIC_RATE_FREQUENCY.getLabel());
    frequencyLabel.setToolTipText(frequencyTip);
    defs.add(frequencyLabel);
    defs.add(rateFrequency);

    content.add(defs);
    add(content);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(getValue(jc1));
    c2.setValue(getValue(jc2));
//    c3.setValue(getValue(jc3));
    c4.setValue(String.valueOf(school.getKey()));
    c5.setValue(String.valueOf(estab.getKey()));
    c6.setValue(String.valueOf(studio.getKey()));
    c7.setValue(((PayFrequency) rateFrequency.getSelectedItem()).getName());

    conf.add(c1);
    conf.add(c2);
//    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    conf.add(c6);
    conf.add(c7);

    return conf;
  }

  private boolean isSelected(String conf) {
    return conf.startsWith("t");
  }

  private String getValue(JCheckBox box) {
    return box.isSelected() ? "t" : "f";
  }

}
