/*
 * @(#)ConfigPlanning.java 2.8.w 27/08/14
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

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class ConfigPlanning
  extends ConfigPanel
{

  private DateRangePanel yearPanel;
  private DateRangePanel periodPanel;
  private HourField offPeakTime;
  private HourField startTime;
  private JCheckBox rangeNames;
  private Config c1,c2,c3,c4,c5,c6,c7;

  public ConfigPlanning(String title, Map<String, Config> confs) {
    super(title, confs);
    init();
  }

  private void init() {
    c1 = confs.get(ConfigKey.BEGINNING_YEAR.getKey());
    c2 = confs.get(ConfigKey.END_YEAR.getKey());
    c3 = confs.get(ConfigKey.BEGINNING_PERIOD.getKey());
    c4 = confs.get(ConfigKey.END_PERIOD.getKey());
    c5 = confs.get(ConfigKey.OFFPEAK_HOUR.getKey());
    c6 = confs.get(ConfigKey.START_TIME.getKey());
    c7 = confs.get(ConfigKey.SCHEDULE_RANGE_NAMES.getKey());

    Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    yearPanel = new DateRangePanel(new DateFr(c1.getValue()),new DateFr(c2.getValue()), border);
		yearPanel.setToolTipText(ConfigKey.BEGINNING_YEAR.getLabel());
    periodPanel = new DateRangePanel(new DateFr(c3.getValue()),new DateFr(c4.getValue()), border);
		periodPanel.setToolTipText(ConfigKey.BEGINNING_PERIOD.getLabel());

    offPeakTime = new HourField(c5.getValue());
		offPeakTime.setToolTipText(ConfigKey.OFFPEAK_HOUR.getLabel());
    startTime = new HourField(c6 == null ? "00:00" : c6.getValue());
		startTime.setToolTipText(BundleUtil.getLabel("ConfEditor.start.time.tip"));

    content = new GemPanel();
    content.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(content);
		gb.insets = GridBagHelper.SMALL_INSETS;

		GemLabel yearLabel = new GemLabel(BundleUtil.getLabel("School.year.label"));
		yearLabel.setToolTipText(ConfigKey.BEGINNING_YEAR.getLabel());
    gb.add(yearLabel,0,0,1,1,GridBagHelper.WEST);
    gb.add(yearPanel,1,0,1,1,GridBagHelper.WEST);
    
		GemLabel periodLabel = new GemLabel(BundleUtil.getLabel("Period.label"));
		periodLabel.setToolTipText(ConfigKey.BEGINNING_PERIOD.getLabel());
    gb.add(periodLabel,0,1,1,1,GridBagHelper.WEST);
    gb.add(periodPanel,1,1,1,1,GridBagHelper.WEST);
    
    GemLabel hourLabel = new GemLabel(BundleUtil.getLabel("Room.rate.peak.label"));
		hourLabel.setToolTipText(ConfigKey.OFFPEAK_HOUR.getLabel());
    
    GemPanel opPanel = new GemPanel(new BorderLayout());
    opPanel.add(offPeakTime, BorderLayout.WEST);

    gb.add(hourLabel,0,2,1,1,GridBagHelper.WEST);
    gb.add(opPanel,1,2,1,1,GridBagHelper.WEST);
    
    GemPanel stPanel = new GemPanel(new BorderLayout());
    stPanel.add(startTime, BorderLayout.WEST);
    GemLabel startTimeLabel = new GemLabel(ConfigKey.START_TIME.getLabel());
    startTimeLabel.setToolTipText(BundleUtil.getLabel("ConfEditor.start.time.tip"));
    gb.add(startTimeLabel,0,3,1,1,GridBagHelper.WEST);
    gb.add(startTime,1,3,1,1,GridBagHelper.WEST);
    
    rangeNames = new JCheckBox(ConfigKey.SCHEDULE_RANGE_NAMES.getLabel());
    rangeNames.setSelected(c7.getValue().equals("t"));
    
    gb.add(rangeNames, 0, 4, 2, 1);
    add(content);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(yearPanel.getStartFr().toString());
    c2.setValue(yearPanel.getEndFr().toString());
    c3.setValue(periodPanel.getStartFr().toString());
    c4.setValue(periodPanel.getEndFr().toString());
    Hour h = offPeakTime.get();
    Hour limit = new Hour("23:00");
    c5.setValue(h.after(limit) ? limit.toString() : h.toString());
    h = startTime.get();
    c6.setValue(h.after(limit) ? limit.toString() : h.toString());

    c7.setValue(rangeNames.isSelected() ? "t" : "f");
    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    conf.add(c6);
    conf.add(c7);
    
    return conf;
  }


}
