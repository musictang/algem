/*
 * @(#)MenuSearch.java 2.6.a 12/10/12
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
package net.algem.util.menu;

import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import net.algem.contact.PersonFileSearchCtrl;
import net.algem.group.GroupSearchCtrl;
import net.algem.room.RoomSearchCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;

/**
 * Search menu.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class MenuSearch
        extends GemMenu
{

  private PersonFileSearchCtrl contact;
  private GroupSearchCtrl group;
  private JMenuItem miGroupBrowse;
  private JMenuItem miSearchPerson;
  private JMenuItem miRoomBrowse;

  public MenuSearch(GemDesktop _desktop) {
    super(BundleUtil.getLabel("Search.label"), _desktop);
    miSearchPerson = add(BundleUtil.getLabel("Contact.label"));
    miGroupBrowse = add(BundleUtil.getLabel("Group.label"));
    miRoomBrowse = new JMenuItem(BundleUtil.getLabel("Room.label"));
    miRoomBrowse.addActionListener(this);
    add(miRoomBrowse);
    setListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Object src = evt.getSource();

    desktop.setWaitCursor();

    if (src == miSearchPerson) {
      if (!desktop.hasModule(GemModule.CONTACT_BROWSER_KEY)) {
        contact = new PersonFileSearchCtrl(desktop, null);
        contact.addActionListener(this);
        contact.init();
        desktop.addPanel(GemModule.CONTACT_BROWSER_KEY, contact);
        desktop.getSelectedModule().setSize(GemModule.S_SIZE);
      }
    } else if (src == miGroupBrowse) {
      if (!desktop.hasModule(GemModule.GROUP_BROWSER_KEY)) {
        group = new GroupSearchCtrl(desktop);
        group.addActionListener(this);
        group.init();
        desktop.addPanel(GemModule.GROUP_BROWSER_KEY, group, GemModule.S_SIZE);
      }
    } else if (src == miRoomBrowse) {
      if (!desktop.hasModule(GemModule.ROOM_BROWSER_KEY)) {
        RoomSearchCtrl sb = new RoomSearchCtrl(desktop);
        sb.addActionListener(this);
        sb.init();
        desktop.addPanel(GemModule.ROOM_BROWSER_KEY, sb, GemModule.S_SIZE);
      }
    } else if (arg.equals(GemCommand.CANCEL_CMD)) {
      desktop.removeCurrentModule();
    }
    desktop.setDefaultCursor();
  }

}
