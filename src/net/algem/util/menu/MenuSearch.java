/*
 * @(#)MenuSearch.java 2.9.4.6 02/06/15
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
package net.algem.util.menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import net.algem.Algem;
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
 * @version 2.9.4.6
 * @since 1.0a 07/07/1999
 */
public class MenuSearch
        extends GemMenu
{

  public static final String CONTACT_BROWSER_KEY = "Contact.browser";
  
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
      if (desktop.hasModule(CONTACT_BROWSER_KEY)) {
          desktop.showModule(CONTACT_BROWSER_KEY);
      } else {
        contact = new PersonFileSearchCtrl(desktop, null);
        contact.addActionListener(this);
        contact.init();
        desktop.addPanel(CONTACT_BROWSER_KEY, contact);
        if (Algem.isFeatureEnabled("cc-mdl")) {
            desktop.getSelectedModule().setSize(new Dimension(420, 460));
        } else {
            desktop.getSelectedModule().setSize(GemModule.S_SIZE);
        }
      }
    } else if (src == miGroupBrowse) {
      if (desktop.hasModule(GroupSearchCtrl.GROUP_BROWSER_KEY)) {
          desktop.showModule(GroupSearchCtrl.GROUP_BROWSER_KEY);
      } else {
        group = new GroupSearchCtrl(desktop);
        group.addActionListener(this);
        group.init();
        desktop.addPanel(GroupSearchCtrl.GROUP_BROWSER_KEY, group, GemModule.S_SIZE);
      }
    } else if (src == miRoomBrowse) {
      if (desktop.hasModule(RoomSearchCtrl.ROOM_BROWSER_KEY)) {
          desktop.showModule(RoomSearchCtrl.ROOM_BROWSER_KEY);
      } else {
        RoomSearchCtrl sb = new RoomSearchCtrl(desktop);
        sb.addActionListener(this);
        sb.init();
        desktop.addPanel(RoomSearchCtrl.ROOM_BROWSER_KEY, sb, GemModule.S_SIZE);
      }
    } else if (arg.equals(GemCommand.CANCEL_CMD)) {
      desktop.removeCurrentModule();
    }
    desktop.setDefaultCursor();
  }

}
