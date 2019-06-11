/*
 * @(#)EditEventDlg.java 2.15.2 27/09/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
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
import net.algem.planning.PlanningException;
import net.algem.planning.PlanningService;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
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
 * @version 2.15.2
 * @since 2.9.4.0 31/03/2015
 */
public class EditEventDlg
        extends JDialog
        implements ActionListener
{

  private GemDesktop desktop;
  private ScheduleRangeObject range;
  private PlanningService service;
  private HourRangePanel timePanel;
  private JTextArea note;
  private Schedule schedule;
  private GemChoice employee;
  private JList<Person> eList;
  private GemButton btAdd;
  private DefaultListModel model;

  public EditEventDlg(GemDesktop desktop, ScheduleRangeObject range, Schedule schedule, PlanningService service) {
    super(desktop.getFrame());
    setTitle(BundleUtil.getLabel("Diary.modification.label"));
    this.desktop = desktop;
    this.range = range;
    this.schedule = schedule;
    this.service = service;
    setLayout(new BorderLayout());
    GemPanel p = new GemPanel(new GridBagLayout());
    p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagHelper gb = new GridBagHelper(p);
    timePanel = new HourRangePanel(range.getStart(), range.getEnd());
    note = new JTextArea(5, 20);
    note.setBorder(new JTextField().getBorder());
    note.setLineWrap(true);
    note.setWrapStyleWord(true);
    note.setPreferredSize(new Dimension(250, 50));
    note.setText(range.getFollowUp() == null ? null : range.getFollowUp().getContent());

    gb.add(new GemLabel(BundleUtil.getLabel("Time.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(timePanel, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Heading.label")), 0, 2, 1, 1, GridBagHelper.NORTHWEST);
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
    try {
      for (Person att : service.getAttendees(schedule.getId(), schedule.getIdPerson(), new HourRange(range.getStart(),range.getEnd()))) {
        model.addElement(att);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    eList = new JList<>(model);
    eList.setToolTipText(MessageUtil.getMessage("attendee.list.tip"));

    eList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          eList.setSelectedIndex(eList.locationToIndex(e.getPoint()));

          JPopupMenu menu = new JPopupMenu();
          JMenuItem itemRemove = new JMenuItem(GemCommand.DELETE_CMD);
          itemRemove.addActionListener(new ActionListener() {
            @Override
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

    add(p, BorderLayout.CENTER);
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));


    GemButton btValid = new GemButton(GemCommand.VALIDATE_CMD);
    btValid.addActionListener(this);
    GemButton btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemButton btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);

    buttons.add(btDelete);
    buttons.add(btValid);
    buttons.add(btCancel);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.S_SIZE);
    pack();
    setLocationRelativeTo(desktop.getFrame());
    setVisible(true);
  }

  private String logErrors() {
    StringBuilder sb = new StringBuilder();
    if (timePanel.getEnd().le(timePanel.getStart())) {
      sb.append(MessageUtil.getMessage("hour.range.error"));
    }
    if (!(timePanel.getStart().between(schedule.getStart(), schedule.getEnd())
      && timePanel.getEnd().between(schedule.getStart(), schedule.getEnd()))) {
       sb.append('\n').append(MessageUtil.getMessage("invalid.range.warning"));
     }
     return sb.toString();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    int minTime = 15;
    HourRange oldTimeRange = new HourRange(range.getStart(), range.getEnd());
    try {
      if (GemCommand.VALIDATE_CMD.equals(cmd)) {
        if (logErrors() == null || logErrors().isEmpty()) {
          if (timePanel.getStart().getLength(timePanel.getEnd()) > minTime
            || MessagePopup.confirm(this, MessageUtil.getMessage("invalid.range.length.confirmation"))) {
            range.setStart(timePanel.getStart());
            range.setEnd(timePanel.getEnd());
            range.setMemberId(schedule.getIdPerson());
            FollowUp up = new FollowUp();
            up.setContent(note.getText().trim());
            service.updateAdministrativeEvent(range, oldTimeRange, up, getAttendees());
            desktop.postEvent(new ModifPlanEvent(this, range.getDate(), range.getDate()));
          }
        } else {
          MessagePopup.warning(this, logErrors());
        }
      } else if (GemCommand.DELETE_CMD.equals(cmd)) {
        service.deleteAdministrativeEvent(range);
        desktop.postEvent(new ModifPlanEvent(this, range.getDate(), range.getDate()));
      }
    } catch (PlanningException ex) {
      GemLogger.log(ex.getMessage());
    }
    dispose();
  }

  private void addAttendee() {
    Person a = (Person) employee.getSelectedItem();

    if (a == null || a.getId() <= 0) {return;}
    if (!model.contains(a) && a.getId() != schedule.getIdPerson()) {
      model.addElement(a);
    } else {
      MessagePopup.warning(this, MessageUtil.getMessage("attendee.already.present.warning"));
    }
  }

  private void removeAttendee() {
    int index = eList.getSelectedIndex();
    model.remove(index);
  }

  private List<Person> getAttendees() {
    return Collections.list(model.elements());
  }

}
