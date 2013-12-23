/*
 * @(#)GroupFileView.java 2.7.k 04/03/13
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
package net.algem.group;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.module.GemView;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.TabPanel;

/**
 * View group editor.
 * View is divided in 4 tabs (group, musicians, history, schedule payment).
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.k
 */
public class GroupFileView
        extends GemView
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
