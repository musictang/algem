/*
 * @(#)CourseModuleView.java	2.8.a 14/03/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import net.algem.config.GemParam;
import net.algem.config.GemParamChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.model.Model;
import net.algem.util.ui.ButtonRemove;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoiceModel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 13/03/2013
 */
public class CourseModuleView
  extends GemPanel
  implements ActionListener
{
  
  private GemParamChoice code;
  private GemButton plus;
  private GemPanel rowsPanel;
  
  public CourseModuleView(DataCache dataCache) {

    setLayout(new BorderLayout());

    GemPanel header = new GemPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
    code = new GemParamChoice(new GemChoiceModel(dataCache.getList(Model.CourseCode)));

    header.add(code);
    header.add(Box.createHorizontalStrut(20));
    plus = new GemButton("+");
    plus.addActionListener(this); 
    plus.setMargin(new Insets(0, 5, 0, 5)); //reduction de la taille du bouton
    header.add(plus);
  
    add(header, BorderLayout.NORTH);
    rowsPanel = new GemPanel();
    rowsPanel.setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Course.label")));
    rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
    add(rowsPanel, BorderLayout.CENTER);
  }

  protected void addRow() {
    CourseModuleInfo info = new CourseModuleInfo();
    GemParam cc = (GemParam) code.getSelectedItem();
    info.setCode(cc);
    info.setTimeLength(0);
    CourseModulePanel p = new CourseModulePanel(info, this);
    rowsPanel.add(p);
    revalidate();
  }
  
   protected void addRow(CourseModuleInfo info) {
    CourseModulePanel p = new CourseModulePanel(info, this);
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
    code.setSelectedIndex(0);
    rowsPanel.removeAll();
    revalidate();
  }
}
