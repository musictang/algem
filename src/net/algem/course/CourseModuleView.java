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
import javax.swing.JComboBox;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.ButtonRemove;
import net.algem.util.ui.GemButton;
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
//  extends InfoView
{

  protected List<Component> rows;
  private JComboBox type;
  private GemButton plus;
  private GemPanel rowsPanel;
  
  public CourseModuleView() {
   
    rows = new ArrayList<Component>();
    setLayout(new BorderLayout());

    GemPanel header = new GemPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
    type = new JComboBox();
    
    header.add(type);
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

//  @Override
  protected void addRow() {
    CourseModuleInfo info = new CourseModuleInfo();
    info.setCode(0);
    info.setTimeLength(0);
    CourseModulePanel p = new CourseModulePanel(info, this);
//    rows.add(p);
    rowsPanel.add(p);
    revalidate();
  }
  
   protected void addRow(CourseModuleInfo info) {
    
    CourseModulePanel p = new CourseModulePanel(info, this);
//    rows.add(p);
    rowsPanel.add(p);
    
  }
  
  void deleteRow(Component o) {
//    rows.remove(o);
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
    for (CourseModuleInfo info : m.getCourses()) {
      addRow(info);
    }
    revalidate();
  }
  
  public void get() {
    
  }
  
  public void clear() {
    type.setSelectedIndex(0);
    rowsPanel.removeAll();
    revalidate();
  }
}
