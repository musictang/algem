/*
 * @(#)EmployeePanel.java	2.8.v 24/06/14
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
package net.algem.contact;

import java.awt.BorderLayout;
import java.util.List;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.RemovablePanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 29/05/14
 */
public class EmployeePanel
  extends RemovablePanel {

  private GemChoice employee;

  public EmployeePanel() {
  }

  public EmployeePanel(List<Person> employees) {
    employee = new EmployeeSelector(employees);
    if (employees != null && employees.size() > 0) {
      employee.setSelectedIndex(0);
    }
    employee.setPreferredSize(CB_SIZE);
    removeBt.setPreferredSize(BT_SIZE);
    setLayout(new BorderLayout());
    add(employee, BorderLayout.WEST);
    add(removeBt, BorderLayout.EAST);
    setBorder(null);
  }

  public int getId() {
    return employee.getKey();
  }

  public void setId(int id) {
    employee.setKey(id);
  }
}
