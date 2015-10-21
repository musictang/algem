/*
 * @(#)ModulePresetDlg.java 2.9.4.0 20/10/2015
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.course;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.config.Preset;
import net.algem.config.PresetView;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 20/10/2015
 */
public class ModulePresetDlg 
  extends JDialog
  implements ListSelectionListener
{

  private GemDesktop desktop;
  private PresetView presetView;
  JList moduleList;
  
  public ModulePresetDlg(GemDesktop desktop) {
    this.desktop = desktop;
  }
  
  public void initUI() {
    presetView = new PresetView(getDefaultPresets());
    presetView.addActionListener();
    presetView.setMinimumSize(new Dimension(200,400));
    presetView.addSelectionListener(this);
//    GemPanel rightPane = new GemPanel();
//    rightPane.setMinimumSize(new Dimension(400,400));
//    
    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    buttons.add(new GemButton(GemCommand.CANCEL_CMD));
    buttons.add(new GemButton(GemCommand.VALIDATION_CMD));
    
    JList moduleList = new JList(getModules().toArray());
    JScrollPane scroll2 = new JScrollPane(moduleList);
    scroll2.setMinimumSize(new Dimension(400,400));
    add(buttons, BorderLayout.SOUTH);
    setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, presetView, scroll2);
    add(splitPane, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.DEFAULT_SIZE);
    setVisible(true);
  }
  
  private List<Preset> getDefaultPresets() {
    List<Preset> list = new ArrayList<>();
    list.add(new ModulePreset(1, "Formation enfants", new int[]{41,44,80}));
    list.add(new ModulePreset(2, "Zik loisir", new int[]{84,85,86,87}));
    return list;
  }
  
  private List<Module> getModules() {
    return desktop.getDataCache().getList(Model.Module).getData();
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    Preset p = presetView.getSelected(e.getFirstIndex());
    int [] s = ((ModulePreset) p).getValue();
    for (int i : s) {
      for (int j = 0 ; j < moduleList.getModel().getSize(); j++) { 
        Preset el = (Preset) moduleList.getModel().getElementAt(j);
        if (el.getId() == i) {
          moduleList.setSelectedIndex(j);
        }
      }
    }
  }
  
}
