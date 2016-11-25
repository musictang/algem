/*
 * @(#)AddEventDlg.java 2.11.3 25/11/16
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.algem.contact.EmployeeSelector;
import net.algem.contact.EmployeeType;
import net.algem.contact.Person;
import net.algem.enrolment.FollowUp;
import net.algem.planning.HourRange;
import net.algem.planning.HourRangePanel;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 * @since 2.9.4.0 26/03/2015
 */
public class AddEventDlg
  extends ModifPlanDlg {

  private HourRangePanel timePanel;
  private JTextArea note;
  private ScheduleObject plan;
  private GemChoice employee;
  private JList<Person> eList;
  private GemButton btAdd;
  private DefaultListModel model;

  public AddEventDlg(GemDesktop desktop, ScheduleObject plan, PlanningService service) {
    super(desktop.getFrame());
    this.plan = plan;
    dlg = new JDialog(desktop.getFrame(), true);
    dlg.setLayout(new BorderLayout());
    GemPanel content = new GemPanel(new GridBagLayout());
    content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagHelper gb = new GridBagHelper(content);
    gb.add(new GemLabel(BundleUtil.getLabel("Time.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    timePanel = new HourRangePanel(plan.getStart(), plan.getEnd());
    gb.add(timePanel, 0, 1, 1, 1, GridBagHelper.WEST);

    note = new JTextArea(5, 20);
    note.setBorder(new JTextField().getBorder());
    note.setLineWrap(true);
    note.setWrapStyleWord(true);
    note.setPreferredSize(new Dimension(250, 50));
    gb.add(new GemLabel(BundleUtil.getLabel("Heading.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(note, 0, 3, 2, 1, GridBagHelper.WEST);

    employee = new EmployeeSelector(service.getEmployees(EmployeeType.ADMINISTRATOR));
    btAdd = new GemButton("+");
    btAdd.setToolTipText(GemCommand.ADD_CMD);
    btAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addAttendee();
      }
    });
    GemPanel selectorPanel = new GemPanel();
    selectorPanel.add(employee);
    selectorPanel.add(btAdd);

    model = new DefaultListModel();
    eList = new JList<>(model);
    eList.setToolTipText(MessageUtil.getMessage("attendee.list.tip"));

    eList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          eList.setSelectedIndex(eList.locationToIndex(e.getPoint()));

          JPopupMenu menu = new JPopupMenu();
          JMenuItem itemRemove = new JMenuItem(GemCommand.DELETE_CMD);
          itemRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              removeAttendee();
            }
          });
          menu.add(itemRemove);
          menu.show(eList, e.getPoint().x, e.getPoint().y);
        }
      }
    });

    JScrollPane scrollList = new JScrollPane(eList);
    scrollList.setPreferredSize(new Dimension(250, 100));

    gb.add(new GemLabel(BundleUtil.getLabel("Attendees.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(selectorPanel, 0, 5, 2, 1, GridBagHelper.WEST);
    gb.add(scrollList, 0, 6, 2, 1, GridBagHelper.WEST);

    addContent(content, "Schedule.add.event.label");
    dlg.setSize(GemModule.S_SIZE);
    dlg.pack();
  }

  @Override
  public boolean isEntryValid() {
    validation = timePanel.getStart().between(plan.getStart(), plan.getEnd());
    return validation;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  HourRange getRange() {
    return new HourRange(timePanel.getStart(), timePanel.getEnd());
  }

  FollowUp getNote() {
    FollowUp up = new FollowUp();
    up.setContent(note.getText().trim());
    return up;
  }

  private void addAttendee() {
    Person a = (Person) employee.getSelectedItem();
    if (a == null || a.getId() <= 0) {return;}
    if (!model.contains(a) && a.getId() != plan.getIdPerson()) {
      model.addElement(a);
    } else {
      MessagePopup.warning(dlg, MessageUtil.getMessage("attendee.already.present.warning"));
    }
  }

  private void removeAttendee() {
    int index = eList.getSelectedIndex();
    model.remove(index);
  }

  List<Person> getAttendees() {
    return Collections.list(model.elements());
  }

}
