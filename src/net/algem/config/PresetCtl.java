/*
 * @(#)PresetCtl.java 2.9.4.13 21/10/2015
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
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
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

  public <T extends Preset> void load(List<T> presets) {
    for (T p : presets) {
      model.addElement(p);
    }
  }

  public void addActionListener(ActionListener listener) {
    ((PresetPanel) view).addActionListener(listener);
  }

  public void addSelectionListener(ListSelectionListener listener) {
    list.getSelectionModel().addListSelectionListener(listener);
  }

  public <T extends Preset> void add(T preset) {
    model.add(model.getSize(), preset);
  }

  public Preset<Integer> rename() {
    Preset<Integer> p = (Preset<Integer>) list.getSelectedValue();
    String s = MessagePopup.input(view, MessageUtil.getMessage("dialog.rename"),GemCommand.RENAME_CMD, p.getName());
    if (s != null && s.length() > 0) {
      p.setName(s);
      return p;
    }
    return null;
  }

  public Object remove() {
    int idx = list.getSelectedIndex();
    if (idx >= 0) {
      return model.remove(idx);
    }
    return null;
  }

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
