/*
 * @(#)ExtendeModuleOrderListCtrl.java	2.9.2.1 16/02/15
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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.TextUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 * Controller used to print and display the list of module orders.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2.1
 * @since 2.9.2.1 16/02/15
 */
public class ExtendeModuleOrderListCtrl
 extends GemPanel
        implements ActionListener
{

  private GemDesktop desktop;
  private JTableModel<ExtendedModuleOrder> tableModel;
  private final JTable table;
  private EnrolmentService service;
  private final DateRangePanel datePanel;

  private String[] columnToolTips = {
    BundleUtil.getLabel("Extended.module.list.person.id.tip"),
    BundleUtil.getLabel("First.name.label"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("Nickname.label"),
    BundleUtil.getLabel("Module.label"),
    BundleUtil.getLabel("Extended.module.list.total.time.tip"),
    BundleUtil.getLabel("Extended.module.list.remaining.time.tip"),
    BundleUtil.getLabel("Extended.module.list.total.amount.tip"),
    BundleUtil.getLabel("Deferred.income.tip"),
  };

  public  ExtendeModuleOrderListCtrl(GemDesktop desktop, EnrolmentService service, JTableModel<ExtendedModuleOrder> tableModel) {
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

    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment( JLabel.RIGHT );
    table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
    table.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(10);
    cm.getColumn(1).setPreferredWidth(60);
    cm.getColumn(2).setPreferredWidth(60);
    cm.getColumn(3).setPreferredWidth(60);
    cm.getColumn(4).setPreferredWidth(120);
    cm.getColumn(5).setPreferredWidth(10);
    cm.getColumn(6).setPreferredWidth(10);
    cm.getColumn(7).setPreferredWidth(15);
    cm.getColumn(8).setPreferredWidth(15);
    JScrollPane scroll = new JScrollPane(table);

    GemButton btPrint = new GemButton(GemCommand.PRINT_CMD);
    GemButton btClose = new GemButton(GemCommand.CLOSE_CMD);

    btPrint.addActionListener(this);
    btClose.addActionListener(this);

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    buttons.add(btPrint);
    buttons.add(btClose);

    GemBorderPanel mainPanel = new GemBorderPanel(new BorderLayout());
    mainPanel.add(scroll, BorderLayout.CENTER);
    
    GemPanel bottom = new GemPanel();
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
   * @param list the list of current orders
   */
  public void load(List<ExtendedModuleOrder> list) {
    for (ExtendedModuleOrder m : list) {
      tableModel.addItem(m);
    }
    sortByColIndex(2, SortOrder.ASCENDING);
  }
  
  /**
   * Loads the list of orders created between {@code start} and {@code start} dates.
   * @param start start date
   * @param end end date
   */
  private void load(final Date start, final Date end) {
     SwingUtilities.invokeLater(new Runnable()
     {
      @Override
      public void run() {
        desktop.setWaitCursor();
        List<ExtendedModuleOrder> current = new ArrayList<>();
        try {
          current = service.getExtendedModuleList(start, end); 
        } catch (SQLException ex) {
          GemLogger.log(ex.getMessage());
        } finally {
          tableModel.clear();
          load(current);
          desktop.setDefaultCursor();
        }
      }
    });

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (GemCommand.CLOSE_CMD.equals(cmd)) {
      desktop.removeModule("Modules.ordered");
    } else if(GemCommand.PRINT_CMD.equals(cmd)) {
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
    try (PrintWriter out = new PrintWriter(new FileWriter(FileUtil.getSaveFile(this, "csv", "Documents csv (tableur)", filename)))) {
      StringBuilder sb = new StringBuilder("\ufeff");
      for (int i = 0; i < tableModel.getColumnCount(); i++) {
        sb.append(tableModel.getColumnName(i)).append(';');
      }
      sb.delete(sb.length() -1,  sb.length() -1);
      sb.append(TextUtil.LINE_SEPARATOR);
      int [] rows = table.getSelectedRows();
      if (rows.length == 0) {
        rows = new int [tableModel.getRowCount()];
        for(int i = 0; i < rows.length; i++) {
          rows[i] = i;
        }
      }
      for (int i = 0 ; i < rows.length; i++) {
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
    }
  }
  
  /**
   * Automatic sorting by column index.
   * @param col index of the column to sort
   */
  private void sortByColIndex(int col, SortOrder order) {
    DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
    List<RowSorter.SortKey> sortkeys = new ArrayList<>();
    sortkeys.add(new RowSorter.SortKey(col, order));
    sorter.setSortKeys(sortkeys);
  }
  

}
