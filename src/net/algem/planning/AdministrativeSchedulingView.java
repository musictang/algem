/*
 * @(#) AdministrativeSchedulingView.java Algem 2.11.0 02/03/2017
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
 */

package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import net.algem.config.ParamChoice;
import net.algem.contact.EmployeeSelector;
import net.algem.contact.EmployeeType;
import net.algem.room.EstabChoice;
import net.algem.room.Room;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.11.0 02/03/2017
 */
public class AdministrativeSchedulingView extends GemPanel {
  
  private GemDesktop desktop;
  private DataCache dataCache;
  private DateRangePanel datePanel;
  private AdministrativeTableView tableView;
  private GemChoice employee;
  private PlanningService service;
  private GemList<Room> roomList;
  private final EstabChoice estab;
  private ParamChoice vacancy;

  public AdministrativeSchedulingView(GemDesktop desktop, PlanningService service) {
    GemPanel mainPanel = new GemPanel();
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    this.service = service;
//    service = new PlanningService(DataCache.getDataConnection());

    estab = new EstabChoice(dataCache.getList(Model.Establishment));
    estab.setSelectedIndex(0);
    roomList = dataCache.getList(Model.Room);
    datePanel = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfYear());
    tableView = new AdministrativeTableView(roomList, estab.getKey());
    tableView.load();
    estab.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          tableView.setEstab(roomList, estab.getKey());
        }
      }
    });
    employee = new EmployeeSelector(service.getEmployees(EmployeeType.ADMINISTRATOR));
    vacancy = new ParamChoice(dataCache.getVacancyCat());
    vacancy.setPreferredSize(employee.getPreferredSize());

    JTextField helpLabel = new JTextField();
    helpLabel.setText(MessageUtil.getMessage("administrative.schedule.ctrl.help"));
    helpLabel.setEditable(false);
    helpLabel.setBorder(BorderFactory.createCompoundBorder(helpLabel.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    mainPanel.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(mainPanel);
    gb.add(new GemLabel(BundleUtil.getLabel("Establishment.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Employee.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Menu.holidays.label")), 0, 4, 1, 1, GridBagHelper.WEST);

    gb.add(estab, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(tableView, 1, 2, 1, 1, GridBagHelper.BOTH, GridBagHelper.WEST);
    gb.add(employee, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(vacancy, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(helpLabel, 0, 5, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    add(mainPanel, BorderLayout.CENTER);
  }

  
}
