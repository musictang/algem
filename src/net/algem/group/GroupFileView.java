/*
 * @(#)GroupFileView.java 2.8.w 27/08/14
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
package net.algem.group;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Vector;
import net.algem.util.BundleUtil;
import net.algem.util.module.DefaultGemView;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.TabPanel;

/**
 * Group view tab container.
 * View is divided in 4 tabs (group, musicians, history, schedule payment).
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class GroupFileView
        extends DefaultGemView
{

  private Group group;
  private GemGroupService service;
  private TabPanel tabPanel;
  private GroupEditor groupEditor;
  private MusicianEditor musiciansEditor;
  private GroupRehearsalHistoView rehearsalHistoView;
  private ActionListener listener;

  public GroupFileView(GemDesktop _desktop, GemGroupService service, Group group) {
    super(_desktop, GemModule.GROUP_VIEW_KEY);
    this.service = service;
    if (group != null && group.getId() > 0) {
      setTitle(group.getName() + " " + group.getId());
    } else {
      setTitle(BundleUtil.getLabel("New.label"));
    }
    this.group = group;
  }

  public void init(Vector<Musician> vm) {

    setLayout(new BorderLayout());

    tabPanel = new TabPanel();
    groupEditor = new GroupEditor(desktop, service, group);
    tabPanel.addItem(groupEditor, BundleUtil.getLabel("Group.label"));
    musiciansEditor = new MusicianEditor(desktop, vm);
    musiciansEditor.load();
    tabPanel.addItem(musiciansEditor, BundleUtil.getLabel("Group.members.label"));

    rehearsalHistoView = new GroupRehearsalHistoView(desktop, service, group.getId());
    rehearsalHistoView.load();
    tabPanel.addItem(rehearsalHistoView, BundleUtil.getLabel("Rehearsal.tab.label"));
    tabPanel.setSelectedIndex(0);
    add(tabPanel, BorderLayout.CENTER);

    setSize(GemModule.XXL_SIZE);
  }
  
  @Override
  public void addActionListener(ActionListener listener) {
    this.listener = listener;
  }
  
  void addTab(FileTab tab, String label) {
    tabPanel.addItem(tab, label);
    
    tabPanel.setSelectedComponent(tab);
    tabPanel.addCloseButton(tabPanel.getSelectedIndex(),listener);
    
  }
  
   /**
   * Gets the component which type is {@code clazz}.
   * @param <T>
   * @param clazz class type
   */
  <T> Object getTab(final Class<T> clazz) {
    Component [] tabs = tabPanel.getComponents();
    for(Component c : tabs) {
      if (c.getClass() == clazz) {
        return c;
      }
    }
    return null;
  }
  
  /**
   * Removes the tab which type is {@code clazz}.
   * @param <T>
   * @param clazz class type
   */
  <T> void remove(final Class<T> clazz) {
    Component [] tabs = tabPanel.getComponents();
    for(Component c : tabs) {
      if (c.getClass() == clazz) {
        tabPanel.remove(c);
        break;
      }
    }   
  }
  
  @Override
  public void setSelectedTab(int index) {
    tabPanel.setSelectedIndex(index);
  }

  public void refreshId(int id) {
    groupEditor.setId(id);
  }

  public Group getGroup() {
    return groupEditor.getGroup();
  }

  /**
   * Gets the list of musicians (members of the group).
   *
   * @return a list of musicians or null
   */
  public Vector<Musician> getMusicians() {
    if (musiciansEditor.hasChanged()) {
      Vector<Musician> lm = musiciansEditor.getMusicians();
      musiciansEditor.load();
      return lm;
    }
    return null;
  }

  @Override
  public void close() {
    groupEditor.dispose();
    dispose();  
  }
}
