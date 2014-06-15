/*
 * @(#)StudioScheduleView.java	2.8.v 13/06/14
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

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import net.algem.config.*;
import net.algem.contact.EmployeePanelCtrl;
import net.algem.group.Group;
import net.algem.group.GroupChoice;
import net.algem.room.RoomActiveChoiceModel;
import net.algem.room.RoomChoice;
import net.algem.room.RoomPanelCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Studio scheduling view.
 * This view is used to select one or several rooms at different times and for different technicians
 * when scheduling studio sessions.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 21/05/14
 */
class StudioScheduleView
  extends GemPanel
{

   private GroupChoice group;
   private DateTimeCtrl dateTimeCtrl;
   private RoomPanelCtrl roomPanelCtrl;
   private GemChoice category;
   private EmployeePanelCtrl employeePanelCtrl;
   private RoomChoice studio;
   private int defStudio;
   private JCheckBox onlyStudio;

  public StudioScheduleView(DataCache dataCache) {
    onlyStudio = new JCheckBox("Réservation studio seul");
    onlyStudio.setBorder(null);
    onlyStudio.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        roomPanelCtrl.setEnabled(!((JCheckBox) e.getItem()).isSelected());
      }

    });

    group = new GroupChoice(new Vector<Group>(dataCache.getList(Model.Group).getData()));
    dateTimeCtrl = new DateTimeCtrl();
    roomPanelCtrl = new RoomPanelCtrl(dataCache);

    GemPanel separator = new GemPanel();
    separator.add(Box.createVerticalStrut(10));
    separator.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
    category = new GemParamChoice(new GemParamModel(dataCache.getList(Model.StudioType)));
    category.setKey(0);
    employeePanelCtrl = new EmployeePanelCtrl(dataCache, BundleUtil.getLabel("Technician.label"));
    studio = new RoomChoice(new RoomActiveChoiceModel(dataCache.getList(Model.Room), true));
    defStudio = Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_STUDIO.getKey(), dataCache.getDataConnection()));
    studio.setKey(defStudio);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(onlyStudio, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Group.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(group, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(roomPanelCtrl, 0, 3, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(dateTimeCtrl, 0, 4, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(separator, 0, 5, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Category.label")), 0, 6, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(category, 0, 7, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(employeePanelCtrl, 0, 8, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Studio.label")), 0, 9, 1, 1, GridBagHelper.WEST);
    gb.add(studio, 0, 10, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);

  }

  boolean isStudioOnly() {
    return onlyStudio.isSelected();
  }

  int getGroup() {
    return group.getKey();
  }

  List<GemDateTime> getDates() {
    return dateTimeCtrl.getRanges();
  }

  int [] getRooms() {
    if (isStudioOnly()) {
      return null;
    }
    return roomPanelCtrl.getRooms();
  }

  GemParam getCategory() {
    return (GemParam) category.getSelectedItem();
  }

  int getStudio() {
    return studio.getKey();
  }

  int [] getEmployees() {
    return employeePanelCtrl.getEmployees();
  }

  void clear() {
     group.setSelectedIndex(0);
     studio.setKey(defStudio);
     dateTimeCtrl.clear();
     roomPanelCtrl.clear();
     employeePanelCtrl.clear();
  }

}
