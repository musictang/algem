
/*
 * @(#)PresetView.java 2.9.4.0 20/10/2015
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

package net.algem.config;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionListener;
import net.algem.course.ModulePreset;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 20/10/2015
 */
public class PresetView 
extends GemPanel
implements ActionListener
{

  private JList presetList;
  DefaultListModel model;
  private JButton btDel;
  private JButton btAdd;
  ListSelectionListener listener;
  
  
  public <T extends Preset> PresetView(List<T> presets) {
    GemLabel title = new GemLabel("Présélections");
    model = new DefaultListModel();
    for(Preset p : presets) {
      model.addElement(p);
    }
    this.presetList = new JList(model);
    
    setLayout(new BorderLayout());
    add(title, BorderLayout.NORTH);
    JScrollPane scroll = new JScrollPane(presetList);
    add(scroll, BorderLayout.CENTER);
    
    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    btDel = new GemButton(GemCommand.DELETE_CMD);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    buttons.add(btDel);
    buttons.add(btAdd);
    
    add(buttons, BorderLayout.SOUTH);
    
  }
  
  public void addActionListener() {
    btDel.addActionListener(this);
    btAdd.addActionListener(this);
  }
  
  public void addSelectionListener(ListSelectionListener listener) {
    this.listener = listener;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btDel) {
      model.remove(presetList.getSelectedIndex());
    } else if (src == btAdd) {
      model.add(model.getSize(), new ModulePreset(4, "Ajout test", new int[]{1,2,3,4}));
    }
  }
  
  public Preset getSelected(int index) {
    return (Preset) model.get(index);
  }
  

}
