/*
 * @(#)ExtendeModuleOrderListCtrl.java	2.9.4.13 11/11/15
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
package net.algem.enrolment;

import net.algem.util.model.AsyncLoader;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.Box;
import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.TextUtil;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 * Controller used to print and display the list of module orders.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.2.1 16/02/15
 */
public class ExtendeModuleOrderListCtrl
  extends GemPanel
  implements ActionListener {

  private static final int SORT_COL_INDEX = 4;
  private GemDesktop desktop;
  private JTableModel<ExtendedModuleOrder> tableModel;
  private final JTable table;
  private EnrolmentService service;
  private final DateRangePanel datePanel;
  private GemLabel status;

  private String[] columnToolTips = {
    BundleUtil.getLabel("Extended.module.list.date.tip"),
    BundleUtil.getLabel("First.name.label"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("Nickname.label"),
    BundleUtil.getLabel("Module.label"),
    BundleUtil.getLabel("Extended.module.list.total.time.tip"),
    BundleUtil.getLabel("Extended.module.list.remaining.time.tip"),
    BundleUtil.getLabel("Extended.module.list.total.amount.tip"),
    BundleUtil.getLabel("Deferred.income.tip"),};

  public ExtendeModuleOrderListCtrl(GemDesktop desktop, EnrolmentService service, JTableModel<ExtendedModuleOrder> tableModel) {
    this.desktop = desktop;
    this.service = service;
    this.tableModel = tableModel;

    table = new JTable(tableModel) {
      //Implements table header tool tips.
      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {

          @Override
          public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            return columnToolTips[realIndex];
          }
        };
      }
    };
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        int[] rows = table.getSelectedRows();
        if (rows.length <= 0) {
          status.setText(null);
        } else {
          status.setText(String.valueOf(rows.length));
        }
      }
    });

    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
    table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
    table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
    table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
    table.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(20);
    cm.getColumn(1).setPreferredWidth(60);
    cm.getColumn(2).setPreferredWidth(60);
    cm.getColumn(3).setPreferredWidth(60);
    cm.getColumn(4).setPreferredWidth(120);
    cm.getColumn(5).setPreferredWidth(10);
    cm.getColumn(6).setPreferredWidth(10);
    cm.getColumn(7).setPreferredWidth(15);
    cm.getColumn(8).setPreferredWidth(15);
    JScrollPane scroll = new JScrollPane(table);

    GemButton btPrint = new GemButton(GemCommand.EXPORT_CMD);
    GemButton btClose = new GemButton(GemCommand.CLOSE_CMD);

    btPrint.addActionListener(this);
    btClose.addActionListener(this);

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    buttons.add(btPrint);
    buttons.add(btClose);

    GemBorderPanel mainPanel = new GemBorderPanel(new BorderLayout());
    mainPanel.add(scroll, BorderLayout.CENTER);

    GemPanel bottom = new GemPanel(new FlowLayout(FlowLayout.LEFT));
    bottom.add(new GemLabel(BundleUtil.getLabel("Orders.label") + " : "));
    bottom.add(status = new GemLabel());
    bottom.add(Box.createHorizontalStrut(150));
    bottom.add(datePanel = new DateRangePanel(desktop.getDataCache().getStartOfYear(), desktop.getDataCache().getEndOfYear()));
    GemButton btLoad = new GemButton(GemCommand.LOAD_CMD);
    btLoad.addActionListener(this);
    bottom.add(btLoad);
    mainPanel.add(bottom, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

  }

  /**
   * Feeds the model with the current loaded list.
   *
   * @param list the list of current orders
   */
  void load(List<ExtendedModuleOrder> list) {
    tableModel.clear();
    for (ExtendedModuleOrder m : list) {
      tableModel.addItem(m);
    }
    status.setText(String.valueOf(tableModel.getData().size()));
    sortByColIndex(SORT_COL_INDEX, SortOrder.ASCENDING);
  }

  /**
   * Loads the list of orders created between {@code start} and {@code start} dates.
   *
   * @param start start date
   * @param end end date
   */
  public void load(final Date start, final Date end) {
    AsyncLoader moduleOrderLoader = new ModuleOrderLoader(ExtendeModuleOrderListCtrl.this, service, start, end);
    moduleOrderLoader.load();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (GemCommand.CLOSE_CMD.equals(cmd)) {
            if (desktop.getDataCache().getUser().getDesktop() == 1) {
                desktop.removeModule("Modules.ordered");
            }
    } else if (GemCommand.EXPORT_CMD.equals(cmd)) {
      print();
    } else if (GemCommand.LOAD_CMD.equals(cmd)) {
      load(datePanel.getStart(), datePanel.getEnd());
    }
  }

  /**
   * Saves the selected rows to CSV.
   */
  private void print() {
    String filename = "pca_" + datePanel.toString() + ".csv";
    File f = FileUtil.getSaveFile(this, "csv", "Documents csv (tableur)", filename);
    if (f == null) {
      return;
    }
    try (PrintWriter out = new PrintWriter(f, "UTF-16LE")) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < tableModel.getColumnCount(); i++) {
        sb.append(tableModel.getColumnName(i)).append(';');
      }
      sb.delete(sb.length() - 1, sb.length() - 1);
      sb.append(TextUtil.LINE_SEPARATOR);
      int[] rows = table.getSelectedRows();
      if (rows.length == 0) {
        rows = new int[tableModel.getRowCount()];
        for (int i = 0; i < rows.length; i++) {
          rows[i] = i;
        }
      }
      for (int i = 0; i < rows.length; i++) {
        int idx = table.convertRowIndexToModel(rows[i]);
        sb.append(tableModel.getValueAt(idx, 0)).append(';');
        sb.append(tableModel.getValueAt(idx, 1)).append(';');
        sb.append(tableModel.getValueAt(idx, 2)).append(';');
        sb.append(tableModel.getValueAt(idx, 3)).append(';');
        sb.append(tableModel.getValueAt(idx, 4)).append(';');
        sb.append(tableModel.getValueAt(idx, 5)).append(';');
        sb.append(tableModel.getValueAt(idx, 6)).append(';');
        sb.append(tableModel.getValueAt(idx, 7)).append(';');
        sb.append(tableModel.getValueAt(idx, 8)).append(TextUtil.LINE_SEPARATOR);
      }
      out.println(sb.toString());
    } catch (IOException ex) {
      GemLogger.log(ex.getMessage());
    } finally {
      try {
        if (f.length() > 0) {
          new DesktopOpenHandler().open(f.getAbsolutePath());
        }
      } catch (DesktopHandlerException ex) {
        GemLogger.log(ex.getMessage());
      }
    }
  }

  /**
   * Automatic sorting by column index.
   *
   * @param col index of the column to sort
   */
  void sortByColIndex(int col, SortOrder order) {
    DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
    List<RowSorter.SortKey> sortkeys = new ArrayList<>();
    sortkeys.add(new RowSorter.SortKey(col, order));
    sorter.setSortKeys(sortkeys);
  }

}
