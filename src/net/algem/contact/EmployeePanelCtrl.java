/*
 * @(#)EmployeePanelCtrl.java	2.9.4.3 22/04/15
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
package net.algem.contact;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import net.algem.planning.PlanningService;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.ui.AbstractGemPanelCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.3
 * @since 2.8.v 29/05/14
 */
public class EmployeePanelCtrl
  extends AbstractGemPanelCtrl
{

  private List<EmployeePanel> panels;
  private final static int SPACING = 4;
  private List<Person> employees;

  public EmployeePanelCtrl(String label) {
    plus = new GemButton("+");
    plus.setMargin(new Insets(0, 4, 0, 4)); //reduction de la taille du bouton
    plus.addActionListener(this);
    plus.setToolTipText(GemCommand.ADD_CMD);
    GemPanel top = new GemPanel(new BorderLayout());
    top.add(new GemLabel(label), BorderLayout.WEST);
    top.add(plus, BorderLayout.EAST);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(top);
    add(Box.createVerticalStrut(SPACING));
    panels = new ArrayList<EmployeePanel>();
    addPanel();
  }


  @Override
  public void addPanel() {
    EmployeePanel p = new EmployeePanel(setEmployees(EmployeeType.TECHNICIAN));
    p.addActionListener(this);
    panels.add(p);
    add(panels.get(panels.size() - 1));
    add(Box.createVerticalStrut(SPACING));
  }

  public void addPanel(int id) {
    EmployeePanel p = new EmployeePanel(setEmployees(EmployeeType.TECHNICIAN));
    p.addActionListener(this);
    p.setId(id);
    panels.add(p);
    add(panels.get(panels.size() - 1));
    add(Box.createVerticalStrut(SPACING));
  }

  @Override
  public void removePanel(GemPanel panel) {
    panels.remove((EmployeePanel) panel);
    ((EmployeePanel) panel).removeActionListener(this);
    remove(panel);
    revalidate();
  }

  @Override
  public void clear() {
    for (int i = 1; i < panels.size(); i++) {
      EmployeePanel rp = panels.get(i);
      panels.remove(rp);
      remove(rp);
    }
//    panels.get(0).reset();
    revalidate();
  }

  private List<Person> setEmployees(Enum cat) {
    if (employees == null) {
      return new PlanningService(DataCache.getDataConnection()).getEmployees(cat);
    }
    return employees;
  }

  public int[] getEmployees() {
    int emps [] = new int[panels.size()];
    for (int i = 0; i < panels.size(); i++) {
      emps[i] = panels.get(i).getId();
    }
    return emps;
  }


}
