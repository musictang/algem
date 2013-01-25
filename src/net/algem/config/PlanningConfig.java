/*
 * @(#)PlanningConfig.java 2.6.a 20/09/12
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

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.planning.HourField;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PlanningConfig
  extends ConfigPanel
{

  private DateRangePanel yearPanel;
  private DateRangePanel periodPanel;
  private HourField offPeakHour;
  private Config c1,c2,c3,c4,c5;



  public PlanningConfig(String title, Map<String, Config> confs) {
    super(title, confs);
    init();
  }

  private void init() {
    c1 = confs.get(ConfigKey.BEGINNING_YEAR.getKey());
    c2 = confs.get(ConfigKey.END_YEAR.getKey());
    c3 = confs.get(ConfigKey.BEGINNING_PERIOD.getKey());
    c4 = confs.get(ConfigKey.END_PERIOD.getKey());
    c5 = confs.get(ConfigKey.OFFPEAK_HOUR.getKey());

    Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    yearPanel = new DateRangePanel(new DateFr(c1.getValue()),new DateFr(c2.getValue()), border);
    periodPanel = new DateRangePanel(new DateFr(c3.getValue()),new DateFr(c4.getValue()), border);

    offPeakHour = new HourField(c5.getValue());

    content = new GemPanel();


    content.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(content);
    gb.add(new GemLabel(ConfigKey.BEGINNING_YEAR.getLabel()),0,0,1,1,GridBagHelper.EAST);
    gb.add(yearPanel,1,0,1,1,GridBagHelper.WEST);
    
    gb.add(new GemLabel(ConfigKey.BEGINNING_PERIOD.getLabel()),0,1,1,1,GridBagHelper.EAST);
    gb.add(periodPanel,1,1,1,1,GridBagHelper.WEST);
    
    GemLabel h = new GemLabel(ConfigKey.OFFPEAK_HOUR.getLabel());
    GemBorderPanel ph = new GemBorderPanel(BorderFactory.createEmptyBorder());
    ph.add(offPeakHour);
    gb.add(h,0,2,1,1,GridBagHelper.EAST);
    gb.add(ph,1,2,1,1,GridBagHelper.WEST);

    add(content);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(yearPanel.getStartFr().toString());
    c2.setValue(yearPanel.getEndFr().toString());
    c3.setValue(periodPanel.getStartFr().toString());
    c4.setValue(periodPanel.getEndFr().toString());
    c5.setValue(offPeakHour.get().toString());

    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    return conf;
  }


}
