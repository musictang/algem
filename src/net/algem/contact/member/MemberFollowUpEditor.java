/*
 * @(#)MemberFollowUpEditor.java	2.10.0 16/05/16
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
 *
 */
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.PersonFile;
import net.algem.course.Course;
import net.algem.course.Module;
import net.algem.planning.DateRange;
import net.algem.planning.DateRangePanel;
import net.algem.planning.FollowUpDlg;
import net.algem.planning.Hour;
import net.algem.planning.PlanningException;
import net.algem.planning.ScheduleRangeObject;
import net.algem.planning.ScheduleRangeTableModel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Follow up list controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 */
public class MemberFollowUpEditor
        extends FileTab
        implements ActionListener {

  private boolean loaded;
  private PersonFile personFile;
  private final GemButton btModify;
  private final GemButton btDelete;
  private final GemButton btLoad;
  private final GemButton btPrint;
  private ScheduleRangeTableModel tableModel;
  private JTable table;
  private JTable printTable;
  private final MemberService memberService;
  private DateRangePanel dates;
  private GemLabel totalTime;
  private JDialog printDlg;

  public MemberFollowUpEditor(GemDesktop desktop, PersonFile pf) {
    super(desktop);
    memberService = new MemberService(DataCache.getDataConnection());
    personFile = pf;

    tableModel = new ScheduleRangeTableModel(dataCache);
    table = new JTable(tableModel);
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
          } catch (PlanningException pe) {
            GemLogger.log(pe.getMessage());
          }
        }
      }
    });

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(15);
    cm.getColumn(2).setPreferredWidth(15);
    cm.getColumn(3).setPreferredWidth(100);
    cm.getColumn(4).setPreferredWidth(40);
    cm.getColumn(5).setPreferredWidth(60);
    cm.getColumn(6).setPreferredWidth(160);
    cm.getColumn(7).setPreferredWidth(150);

    JScrollPane scroll = new JScrollPane(table);
    initPrintTable();
    initPrintDialog();

    btModify = new GemButton(GemCommand.VIEW_EDIT_CMD);// consulter/modifier
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btLoad = new GemButton(GemCommand.LOAD_CMD);
    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btModify.addActionListener(this);
    btDelete.addActionListener(this);
    btLoad.addActionListener(this);
    btPrint.addActionListener(this);

    GemPanel datesPanel = new GemPanel();
    dates = new DateRangePanel();
    dates.setStart(dataCache.getStartOfYear());
    dates.setEnd(new Date());// now by default
    totalTime = new GemLabel();
    GemPanel timePanel = new GemPanel();

    datesPanel.add(new GemLabel(BundleUtil.getLabel("Total.label") + " :"));
    datesPanel.add(totalTime);
    datesPanel.add(dates);
    datesPanel.add(btLoad);
    datesPanel.add(btPrint);

    GemPanel pDates = new GemPanel(new BorderLayout());
    pDates.add(datesPanel, BorderLayout.CENTER);
    pDates.add(timePanel, BorderLayout.SOUTH);

    GemPanel mainPanel = new GemPanel(new BorderLayout());
    mainPanel.add(scroll, BorderLayout.CENTER);
    mainPanel.add(pDates, BorderLayout.SOUTH);

    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    buttons.add(btModify);
    buttons.add(btDelete);

    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    desktop.setWaitCursor();
    clear();
    Vector<ScheduleRangeObject> v = null;
    try {
      v = memberService.findFollowUp(personFile.getId(), new DateRange(dates.getStartFr(), dates.getEndFr()));
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    int min = 0;
    if (v != null) {
      for (int i = 0; i < v.size(); i++) {
        ScheduleRangeObject r = v.elementAt(i);
        Hour hd = r.getStart();
        Hour hf = r.getEnd();
        min += hd.getLength(hf);
        tableModel.addItem(r);
      }
      totalTime.setText(Hour.format(min));
    }
    loaded = true;
    desktop.setDefaultCursor();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    Object src = evt.getSource();
    if(src == btLoad || src == dates) {
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
      } catch (PlanningException pe) {
        GemLogger.log(pe.getMessage());
      }
    } else if (src == btDelete) {
      try {
        suppression(n);
      } catch (SQLException e) {
        GemLogger.logException("suppression suivi pédagogique", e, this);
      }
    }
  }

  void modification(int n) throws PlanningException, SQLException {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    ScheduleRangeObject sro = (ScheduleRangeObject) tableModel.getItem(n);
    Course c = sro.getCourse();

    int col = table.getSelectedColumn();
    FollowUpDlg dlg = new FollowUpDlg(desktop, sro, c.getTitle(), (col == 7));
    try {
      dlg.entry();
      if (!dlg.isValidation()) {
        return;
      }
      if (col != 7) {
        planningService.updateFollowUp(sro, dlg.getText());
        sro.setNoteValue(dlg.getText());
        tableModel.modItem(n, sro);
      }
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  void insertion(int n) throws SQLException {
    //plages.addItem(v);
  }

  void suppression(int n) throws SQLException {

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    ScheduleRangeObject p = (ScheduleRangeObject) tableModel.getItem(n);
    try {
      dc.setAutoCommit(false);
      planningService.deleteFollowUp(p);
      p.setNote(0);
      p.setNoteValue(null);
      tableModel.modItem(n, p);
      dc.commit();
    } catch (SQLException e1) {
      GemLogger.logException("transaction update", e1);
      dc.rollback();
    } finally {
      dc.setAutoCommit(true);
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

  }

  private void clear() {
    if (tableModel.getRowCount() > 0) {
      tableModel.clear();
    }
    totalTime.setText(null);
  }

  private void initPrintTable() {

    printTable = new JTable(tableModel);

    TableColumnModel cm = printTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(10);
    cm.getColumn(2).setPreferredWidth(10);
    cm.getColumn(3).setPreferredWidth(100);
    cm.getColumn(4).setPreferredWidth(80);
    cm.getColumn(5).setPreferredWidth(100);
    cm.getColumn(6).setPreferredWidth(140);
    cm.getColumn(7).setPreferredWidth(110);
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
    printDlg.setVisible(true);
    try {
      List<Module> modules = memberService.findModuleOrders(personFile.getId(), dates.getStart(), dates.getEnd());
      StringBuilder sb = new StringBuilder(personFile.getContact().getFirstnameName());

      if (modules != null && !modules.isEmpty()) {
        sb.append(" : ");
        sb.append(modules.get(0).getTitle());
        if (modules.size() > 1) {
          sb.append("..."); // do not display next modules
        }
      }

      MessageFormat header = new MessageFormat(sb.toString());
      MessageFormat footer = new MessageFormat("Page {0}");
      PrintRequestAttributeSet prs = new HashPrintRequestAttributeSet();
      prs.add(MediaSizeName.ISO_A4);
      prs.add(Sides.TWO_SIDED_SHORT_EDGE);
      prs.add(OrientationRequested.PORTRAIT);
      //210 x 297 mm | 8.3 x 11.7 in 1 inch = 25.4 mm

      MediaPrintableArea printableArea =
        new MediaPrintableArea(10f, 10f, 190f, 277f, MediaPrintableArea.MM);
      prs.add(printableArea);
      printTable.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, prs, true);
    } catch (PrinterException ex) {
      GemLogger.logException(ex);
    } finally {
      printDlg.setVisible(false);
    }
  }
}
