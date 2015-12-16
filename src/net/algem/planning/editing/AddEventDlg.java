/*
 * @(#)AddEventDlg.java 2.9.4.14 15/12/15
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
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.algem.planning.HourRange;
import net.algem.planning.HourRangePanel;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.0 26/03/2015
 */
public class AddEventDlg
  extends ModifPlanDlg
{

  private HourRangePanel timePanel;
  private JTextArea note;
  private ScheduleObject plan;

  public AddEventDlg(GemDesktop desktop, ScheduleObject plan) {
    super(desktop.getFrame());
    this.plan = plan;
    dlg = new JDialog(desktop.getFrame(), true);
    dlg.setLayout(new BorderLayout());
    GemPanel p = new GemPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;
    timePanel = new HourRangePanel(plan.getStart(),plan.getEnd());
    note = new JTextArea(5, 20);
    note.setBorder(new JTextField().getBorder());
    note.setLineWrap(true);
    note.setWrapStyleWord(true);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(timePanel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Heading.label")), 0, 1, 1, 1, GridBagHelper.NORTHWEST);
    gb.add(note, 1, 1, 1, 1, GridBagHelper.WEST);
    addContent(p, "Schedule.add.event.label");
    dlg.setSize(GemModule.XS_SIZE);
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

  String getNote() {
    return note.getText().trim();
  }

}
