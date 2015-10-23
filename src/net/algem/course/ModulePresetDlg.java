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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.config.Preset;
import net.algem.config.PresetCtl;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 20/10/2015
 */
public class ModulePresetDlg
  extends JDialog
  implements ActionListener, ListSelectionListener {

  private Component parent;
  private DataCache dataCache;
  private PresetCtl presetCtl;
  private JList moduleList;
  private ModuleService service;
  private JButton btValidation;
  private JButton btCancel;
  private boolean validation;

  public ModulePresetDlg(Component parent, DataCache cache) {
    this.dataCache = cache;
    service = new ModuleService(DataCache.getDataConnection());
    setModal(true);
  }

  public void initUI() {
    presetCtl = new PresetCtl();

    presetCtl.addActionListener(this);
    presetCtl.addSelectionListener(this);
    presetCtl.load(getSavedPresets());
    Component presetPanel = presetCtl.getView();
    presetPanel.setMinimumSize(new Dimension(200, 400));

    GemPanel footer = new GemPanel(new BorderLayout());
    URL url = getClass().getResource(FileUtil.DEFAULT_HELP_DIR + "/detail/module-preset.html");

    JEditorPane help = new JEditorPane();
    help.setContentType("text/html");
    try {
      help.setPage(url);
    } catch (IOException ex) {
      GemLogger.log(ex.getMessage());
    }
    help.setEditable(false);

    GemPanel cmdPanel = new GemPanel(new GridLayout(1, 2));
    cmdPanel.add(btCancel = new GemButton(GemCommand.CANCEL_CMD));
    cmdPanel.add(btValidation = new GemButton(GemCommand.VALIDATION_CMD));
    btValidation.addActionListener(this);
    btCancel.addActionListener(this);
    
    footer.add(help, BorderLayout.NORTH);
    footer.add(cmdPanel, BorderLayout.SOUTH);
    
    moduleList = new JList(getModules().toArray());
    JScrollPane rightScroll = new JScrollPane(moduleList);
    rightScroll.setMinimumSize(new Dimension(400, 400));
    setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, presetPanel, rightScroll);
    add(splitPane, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    setSize(new Dimension(650, 650));
    setLocationRelativeTo(parent);
//    setLocation(100,0);
    setVisible(true);
  }

  private List<Preset<Integer>> getSavedPresets() {
    List<Preset<Integer>> list = new ArrayList<>();
    try {
      list = service.findPresets();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return list;
  }

  private List<Module> getModules() {
    return dataCache.getList(Model.Module).getData();
  }
  
  public List<Module> getSelectedModules() {
    return moduleList.getSelectedValuesList();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    try {
      if (GemCommand.DELETE_CMD.equals(cmd)) {
        Object p = presetCtl.remove();
        if (p != null) {
          service.deletePreset(((Preset<Integer>) p).getId());
        }
      } else if (GemCommand.ADD_CMD.equals(cmd)) {
        List<Module> modules = moduleList.getSelectedValuesList();
        Preset<Integer> p = new DefaultPreset<>();
        Integer[] indices = new Integer[modules.size()];
        for (int i = 0; i < modules.size(); i++) {
          indices[i] = modules.get(i).getId();
        }
        String name = MessagePopup.input(this, BundleUtil.getLabel("Name.label"), GemCommand.ADD_CMD, modules.get(0).getTitle());
        if (name != null) {
          p.setName(name.isEmpty() ? modules.get(0).getTitle() : name);
          p.setValue(indices);
          service.addPreset(p);
          presetCtl.add(p);
        }
      } else if (GemCommand.RENAME_CMD.equals(cmd)) {
        Preset<Integer> p = presetCtl.rename();
        if (p != null) {
          service.renamePreset(p);
        }
      } else if (GemCommand.CANCEL_CMD.equals(cmd)) {
        validation = false;
        close();
      } else if (GemCommand.VALIDATION_CMD.equals(cmd)) {
        validation = true;
        close();
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    Preset<Integer> p = presetCtl.getSelected();
    if (p == null || e.getValueIsAdjusting()) {
      return;
    }
    Integer[] m = p.getValue();
    int[] indices = new int[m.length];
    int k = 0;
    moduleList.clearSelection();
    for (int i : m) {
      for (int j = 0; j < moduleList.getModel().getSize(); j++) {
        Module d = (Module) moduleList.getModel().getElementAt(j);
        if (d.getId() == i) {
          indices[k++] = j;
        }
      }
    }
    moduleList.setSelectedIndices(indices);
  }
  
  public boolean isValidated() {
    return validation;
  }
  
  private void close() {
    setVisible(false);
    dispose();
  }

}
