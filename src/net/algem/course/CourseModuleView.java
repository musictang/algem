/*
 * @(#)CourseModuleView.java	2.9.3.2 12/03/15
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.algem.course;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.model.GemList;
import net.algem.util.ui.ButtonRemove;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.2
 * @since 2.8.a 13/03/2013
 */
public class CourseModuleView
  extends GemPanel
  implements ActionListener
{

  private GemButton plus;
  private GemPanel rowsPanel;
  private GemList<CourseCode> codeList;
  
  public CourseModuleView(GemList<CourseCode> codeList) {

    this.codeList = codeList;
    
    setLayout(new BorderLayout());
    
    rowsPanel = new GemPanel();
    rowsPanel.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Course.label")));
    rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
    
    GemPanel footer = new GemPanel();
    footer.setLayout(new BorderLayout());
    footer.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

    plus = new GemButton(BundleUtil.getLabel("Course.add.label"));
    plus.addActionListener(this); 
    footer.add(plus);

    add(rowsPanel, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
  }

  protected void addRow() {
    CourseModuleInfo info = new CourseModuleInfo();
    info.setTimeLength(0);
    CourseModulePanel p = new CourseModulePanel(info, codeList, this);
    rowsPanel.add(p);
    revalidate();
  }
  
   protected void addRow(CourseModuleInfo info) {
    CourseModulePanel p = new CourseModulePanel(info, codeList, this);
    rowsPanel.add(p); 
  }
  
  void deleteRow(Component o) {
    rowsPanel.remove(o);
    revalidate();
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == plus) {
      addRow();
    } else if (e.getActionCommand().equals(GemCommand.REMOVE_CMD)) {
      deleteRow(((ButtonRemove) e.getSource()).getContainer());
    }
  }
  
  public void set(Module m) {
    if (m.getCourses() == null) {
      return;
    }
    for (CourseModuleInfo info : m.getCourses()) {
      addRow(info);
    }
    revalidate();
  }
  
  public List<CourseModuleInfo> get() {
    List<CourseModuleInfo> courses = new ArrayList<CourseModuleInfo>();
    Component rows [] = rowsPanel.getComponents();
    for (int i = 0 ; i < rows.length; i++) {
      CourseModuleInfo cmi = ((CourseModulePanel) rows[i]).get();
      cmi.setId(i);
      courses.add(cmi);
    }
    return courses;
  }
  
  public void clear() {
    rowsPanel.removeAll();
    revalidate();
  }
}
