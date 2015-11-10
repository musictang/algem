/*
 * @(#)PresetCtl.java 2.9.4.13 09/11/15
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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.PresetPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 20/10/2015
 */
public class PresetCtl {

  private JList list;
  private DefaultListModel model;
  private JPanel view;

  public PresetCtl() {
    this.model = new DefaultListModel();
    this.list = new JList(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    view = new PresetPanel(list);
  }

  /**
   * Feeds the model with a preset list.
   * @param <T>
   * @param presets 
   */
  public <T extends Preset> void load(List<T> presets) {
    for (T p : presets) {
      model.addElement(p);
    }
  }

  public void addActionListener(ActionListener listener) {
    ((PresetPanel) view).addActionListener(listener);
  }

  public void addMouseListener(MouseListener listener) {
    list.addMouseListener(listener);
  }

  /**
   * Adds a preset in model.
   * @param <T>
   * @param preset 
   */
  public <T extends Preset> void add(T preset) {
    model.add(model.getSize(), preset);
  }

  /**
   * Rename the selected preset.
   * @return the renamed preset or null if the operation was not successful.
   */
  public Preset<Integer> rename() {
    Preset<Integer> p = (Preset<Integer>) list.getSelectedValue();
    String s = MessagePopup.input(view, MessageUtil.getMessage("dialog.rename"),GemCommand.RENAME_CMD, p.getName());
    if (s != null && s.length() > 0) {
      p.setName(s);
      return p;
    }
    return null;
  }

  /**
   * Removes a preset from model.
   * @return the object removed or null if object was not found
   */
  public Object remove() {
    int idx = list.getSelectedIndex();
    if (idx >= 0) {
      return model.remove(idx);
    }
    return null;
  }

  /**
   * Gets the selected preset in the list.
   * @return a preset or null if none has been found
   */
  public Preset getSelected() {
    int idx = list.getSelectedIndex();
    if (idx < 0) {
      return null;
    }
    return (Preset) model.get(idx);
  }

  public Component getView() {
    return view;
  }

}
