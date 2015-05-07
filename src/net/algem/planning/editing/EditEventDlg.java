/*
 * @(#)EditEventDlg.java 2.9.4.4 06/05/15
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.4
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

  public EditEventDlg(GemDesktop desktop, ScheduleRangeObject range, Schedule schedule, PlanningService service) {
    super(desktop.getFrame());
    setTitle(BundleUtil.getLabel("Diary.modification.label"));
    this.desktop = desktop;
    this.range = range;
    this.schedule = schedule;
    this.service = service;
    setLayout(new BorderLayout());
    GemPanel p = new GemPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;
    timePanel = new HourRangePanel(range.getStart(), range.getEnd());
    note = new JTextArea(5, 20);
    note.setBorder(new JTextField().getBorder());
    note.setLineWrap(true);
    note.setWrapStyleWord(true);
    note.setText(range.getNote1());
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(timePanel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Heading.label")), 0, 1, 1, 1, GridBagHelper.NORTHWEST);
    gb.add(note, 1, 1, 1, 1, GridBagHelper.WEST);

    add(p, BorderLayout.CENTER);
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));

    GemButton btValid = new GemButton(GemCommand.OK_CMD);
    btValid.addActionListener(this);
    GemButton btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemButton btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);

    buttons.add(btDelete);
    buttons.add(btValid);
    buttons.add(btCancel);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.XS_SIZE);
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
    try {
      if (GemCommand.OK_CMD.equals(cmd)) {
        if (logErrors() == null || logErrors().isEmpty()) {
          if (timePanel.getStart().getLength(timePanel.getEnd()) > minTime
            || MessagePopup.confirm(this, MessageUtil.getMessage("invalid.range.length.confirmation"))) {
            range.setStart(timePanel.getStart());
            range.setEnd(timePanel.getEnd());
            service.updateAdministrativeEvent(range, note.getText().trim());
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

}
