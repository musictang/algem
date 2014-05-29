/*
 * @(#)StudioScheduleView.java	2.8.v 29/05/14
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

package net.algem.planning;

import java.awt.GridBagLayout;
import java.util.List;
import java.util.Vector;
import net.algem.contact.EmployeePanelCtrl;
import net.algem.group.Group;
import net.algem.group.GroupChoice;
import net.algem.room.RoomPanelCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Studio scheduling view.
 * This view is used to select one or several rooms at different times and for different technicians.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 21/05/14
 */
public class StudioScheduleView
  extends GemPanel
{
   private GroupChoice group;
   private DateTimeCtrl dateTimeCtrl;
   private RoomPanelCtrl roomPanelCtrl;
   private EmployeePanelCtrl employeePanelCtrl;

  public StudioScheduleView(DataCache dataCache) {
    group = new GroupChoice(new Vector<Group>(dataCache.getList(Model.Group).getData()));
    dateTimeCtrl = new DateTimeCtrl();
    roomPanelCtrl = new RoomPanelCtrl(dataCache);
    employeePanelCtrl = new EmployeePanelCtrl(dataCache, BundleUtil.getLabel("Technician.label"));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Group.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(group, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(roomPanelCtrl, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(dateTimeCtrl, 0, 3, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(employeePanelCtrl, 0, 4, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);

  }

  int getGroup() {
    return group.getKey();
  }

  List<GemDateTime> getDates() {
    return dateTimeCtrl.getRanges();
  }

  int [] getRooms() {
    return roomPanelCtrl.getRooms();
  }

  int [] getEmployees() {
    return employeePanelCtrl.getEmployees();
  }

  void clear() {
     group.setSelectedIndex(0);
     dateTimeCtrl.clear();
     roomPanelCtrl.clear();
     employeePanelCtrl.clear();
  }

}
