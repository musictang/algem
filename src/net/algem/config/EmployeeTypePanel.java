/*
 * @(#)EmployeeTypePanel.java	2.8.v 28/05/14
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.RemovablePanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 28/05/14
 */
class EmployeeTypePanel
        extends RemovablePanel
{

  private GemChoice type; 
  private static final Dimension CB_SIZE = new Dimension(250, 24);
  private static final Dimension BT_SIZE = new Dimension(24, 24);

  public EmployeeTypePanel() {
  }

  public EmployeeTypePanel(GemList<GemParam> types) {
    type = new EmployeeTypeSelector(types);
    type.setPreferredSize(CB_SIZE);
    removeBt.setPreferredSize(BT_SIZE);
    setLayout(new BorderLayout());
    add(type, BorderLayout.WEST);
    add(removeBt, BorderLayout.EAST);
    setBorder(null);
  }

  int getType() {
    return type.getKey();
  }

  void setType(int t) {
    type.setKey(t);
  }
}
