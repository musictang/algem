/*
 * @(#)TeacherFollowUpEditor.java	2.11.5 25/01/17
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.contact.teacher;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import net.algem.contact.PersonFile;
import net.algem.course.Course;
import net.algem.course.CourseTeacherTableModel;
import net.algem.enrolment.FollowUp;
import net.algem.planning.*;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MultiLineTableCellRenderer;

/**
 * Follow-up editor for teacher.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.5
 */
public class TeacherFollowUpEditor
        extends FileTab
        implements ActionListener
{

  private boolean loaded;
  private PersonFile pFile;
  private DateFrField dateStart;
  private DateFrField dateEnd;
  private GemButton btModify;
  private GemButton btLoad;
  private final GemButton btPrint;
  private CourseTeacherTableModel courseTableModel;
  private JTable table;
  private JTable printTable;
  private JDialog printDlg;
  private TeacherService service;
  private GemLabel totalTime;

  public TeacherFollowUpEditor(GemDesktop desktop, PersonFile dossier) {
    super(desktop);

    service = new TeacherService(planningService, dc);
    pFile = dossier;

    courseTableModel = new CourseTeacherTableModel();
    table = new JTable(courseTableModel);
    table.setAutoCreateRowSorter(true);
    table.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (table.getSelectedRow() < 0) {
          return;
        }
        int n = table.convertRowIndexToModel(table.getSelectedRow());
        if (e.getClickCount() == 2) {
          try {
            modification(n);
          } catch (SQLException sqe) {
            GemLogger.log(sqe.getMessage());
          }
        }
      }
    });

    final ListSelectionModel listSelectionModel = table.getSelectionModel();
    listSelectionModel.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
          return;
        }
        int total = 0;
        if (!listSelectionModel.isSelectionEmpty()) {
          int rows[] = table.getSelectedRows();
          for (int i = 0; i < rows.length; i++) {
            int rowIndex = table.convertRowIndexToModel(rows[i]);
            String member = (String) table.getModel().getValueAt(rowIndex, 4);
            // do not include teacher breaks
            if (BundleUtil.getLabel("Teacher.break.label").toUpperCase().equals(member)) {
              continue;
            }
            Hour start = new Hour((String) table.getModel().getValueAt(rowIndex, 1));
            Hour end = new Hour((String) table.getModel().getValueAt(rowIndex, 2));
            total += start.getLength(end);
          }
        }
        totalTime.setText(Hour.format(total));
      }
    });

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);
    cm.getColumn(1).setPreferredWidth(15);
    cm.getColumn(2).setPreferredWidth(15);
    cm.getColumn(3).setPreferredWidth(120);
    cm.getColumn(4).setPreferredWidth(150);
    cm.getColumn(5).setPreferredWidth(15);
    cm.getColumn(6).setPreferredWidth(15);
    cm.getColumn(7).setPreferredWidth(250);

    initPrintTable();
    initPrintDialog();
    JScrollPane pm = new JScrollPane(table);

    btLoad = new GemButton(GemCommand.LOAD_CMD);
    btLoad.addActionListener(this);
    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btPrint.addActionListener(this);
    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());

    GemPanel datePanel = new GemPanel();
    totalTime = new GemLabel();
    datePanel.add(new GemLabel(BundleUtil.getLabel("Total.label") + " :"));
    datePanel.add(totalTime);
    datePanel.add(new JLabel(BundleUtil.getLabel("Date.From.label").toLowerCase()));
    datePanel.add(dateStart);
    datePanel.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    datePanel.add(dateEnd);
    datePanel.add(btLoad);
    datePanel.add(btPrint);

    btModify = new GemButton(BundleUtil.getLabel("View.modify.label"));
    btModify.addActionListener(this);

    GemPanel footer = new GemPanel(new BorderLayout());
    footer.add(datePanel, BorderLayout.NORTH);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btModify);
    footer.add(buttons, BorderLayout.SOUTH);

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(footer, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    loaded = true;
    courseTableModel.clear();
    int min = 0;
    try {
      Vector<ScheduleRangeObject> v = service.getSchedule(pFile.getId(), dateStart.toString(), dateEnd.toString());
      for (int i = 0; i < v.size(); i++) {
        ScheduleRangeObject r = v.elementAt(i);
        CourseSchedule pc = new CourseSchedule(r);
        Course c = r.getCourse();
        pc.setCourse(c);
        if (!c.isCollective() || v.size() == 1) {
          pc.setMember(r.getMember());
        }
        if (pc.getCourse().isCollective()) {
          Schedule s = planningService.getScheduleByRange(pc.getId());
          if (s != null) {
            pc.setId(s.getId());
          }
          FollowUp n = planningService.getCollectiveFollowUpByRange(r.getId());
          if (n != null) {
            pc.setNote(n.getId());
            pc.setFollowUp(n);
          } else {
            pc.setNote(0);
          }
        } else {
          pc.setFollowUp(planningService.getFollowUp(r.getNote()));
        }
        courseTableModel.addItem(pc);
        Hour hd = pc.getStart();
        Hour hf = pc.getEnd();
        min += hd.getLength(hf);
      }
      totalTime.setText(Hour.format(min));

    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#load " + ex.getMessage());
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    Object src = evt.getSource();
    if (src == btLoad) {
      desktop.setWaitCursor();
      load();
      desktop.setDefaultCursor();
      return;
    } else if (src == btPrint) {
      print();
      return;
    } else if (table.getSelectedRow() < 0) {
      return;
    }

    int n = table.convertRowIndexToModel(table.getSelectedRow());

    if (src == btModify) {
      try {
        modification(n);
      } catch (SQLException e) {
        GemLogger.log(e.getMessage());
      }
    }
  }

  void modification(int n) throws SQLException {

    CourseSchedule p = (CourseSchedule) courseTableModel.getItem(n);
    Course c = p.getCourse();
    if (c == null) {
      return;
    }
    CommonFollowUpDlg dlg = new CommonFollowUpDlg(desktop, planningService, p, p.getCourse().toString(), c.isCollective());
    dlg.entry();
    if (!dlg.isValidation()) {
      return;
    }
    p.setFollowUp(dlg.getFollowUp());
    courseTableModel.modItem(n, p);
    dlg.exit();
  }

  void insertion(int n) throws SQLException {
    clear();
  }

  private void initPrintTable() {
    printTable = new JTable(courseTableModel);
    TableColumnModel cm = printTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(10);
    cm.getColumn(2).setPreferredWidth(10);
    cm.getColumn(3).setPreferredWidth(80);
    cm.getColumn(4).setPreferredWidth(100);
    cm.getColumn(5).setPreferredWidth(10);
    cm.getColumn(6).setPreferredWidth(10);
    cm.getColumn(7).setPreferredWidth(300);
    printTable.getColumnModel().getColumn(7).setCellRenderer(new MultiLineTableCellRenderer());
    printTable.setRowHeight(30);
  }

  private void initPrintDialog() {
    JScrollPane sc = new JScrollPane(printTable);
    GemPanel p = new GemPanel(new BorderLayout());
    p.add(sc, BorderLayout.CENTER);
    printDlg = new JDialog();
    printDlg.add(p);
    printDlg.setSize(GemModule.XXL_SIZE);
  }

  private void print() {
    desktop.setWaitCursor();
    printDlg.setVisible(true);
    try {
      String sb = pFile.getContact().getFirstnameName() + " " + dateStart.toString() + "/" + dateEnd.toString();
      MessageFormat header = new MessageFormat(sb);
      MessageFormat footer = new MessageFormat("Page {0}");
      PrintRequestAttributeSet prs = new HashPrintRequestAttributeSet();
      prs.add(MediaSizeName.ISO_A4);
      prs.add(Sides.TWO_SIDED_LONG_EDGE);
      prs.add(OrientationRequested.PORTRAIT);
      prs.add(new JobName(BundleUtil.getLabel("Follow.up.label") + "-" + pFile.getId(), Locale.getDefault()));
      //210 x 297 mm | 8.3 x 11.7 in 1 inch = 25.4 mm

      MediaPrintableArea printableArea = new MediaPrintableArea(10f, 10f, 190f, 277f, MediaPrintableArea.MM);
      prs.add(printableArea);
      printTable.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, prs, true);
    } catch (PrinterException ex) {
      GemLogger.logException(ex);
    } finally {
      printDlg.setVisible(false);
      desktop.setDefaultCursor();
    }
  }

  void clear() {
//		liste.clear();
  }
}
