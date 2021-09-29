/*
 * @(#)MemberListTab.java	2.6.a 18/09/12
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.PersonFileListCtrl;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.ListCtrl;

/**
 * Linked members tab in payer file.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MemberListTab
        extends FileTab
{

  private ListCtrl _list;
  boolean loaded;

  public MemberListTab(GemDesktop _desktop, ListCtrl list) {
    super(_desktop);
    setLayout(new BorderLayout());
    _list = list;
    _list.addMouseListener(new MouseAdapter()
    {

      @Override
      public void mouseClicked(MouseEvent e) {
        int id = _list.getSelectedID();
        if (id > 0) {
          PersonFileEditor m = desktop.getPersonFileEditor(id);
          if (m == null) {
            PersonFile pf = ((PersonFileListCtrl) _list).getPersonFile();
              PersonFileEditor editor = new PersonFileEditor(pf);
              desktop.addModule(editor);
              //setGroupeContact();
          } else {
            desktop.setSelectedModule(m);
          }
        }
      }
    });
    add(_list, BorderLayout.NORTH);
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    loaded = _list != null;
  }
}
