/*
 * @(#)PersonFileGroupView.java 2.9.7 03/05/16
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import net.algem.contact.PersonFileGroupListCtrl;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.ListCtrl;

/**
 * Groups tab in person file.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.7
 */
public class PersonFileGroupView
        extends FileTab
{

  private final ListCtrl groupList;
  private boolean loaded;

  public PersonFileGroupView(GemDesktop _desktop, ListCtrl listCtrl) {
    super(_desktop);
    setLayout(new BorderLayout());
    groupList = listCtrl;
    groupList.addMouseListener(new MouseAdapter()
    {

      @Override
      public void mouseClicked(MouseEvent e) {
        desktop.setWaitCursor();
        Group g = ((PersonFileGroupListCtrl) groupList).getGroup();
        if (g != null && g.getId() > 0) {
          loadModule(g);
        }
        desktop.setDefaultCursor();
      }
    });
    add(groupList, BorderLayout.NORTH);
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    loaded = groupList != null;
  }

  private void loadModule(Group g) {
    GroupFileEditor m = desktop.getGroupFileEditor(g.getId());
    if (m == null) {
      m = new GroupFileEditor(g);
      desktop.addModule(m);
    } else {
      desktop.setSelectedModule(m);
    }
  }
}
