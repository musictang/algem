/*
 * @(#)ConflictListView.java	2.9.4.0 06/04/15
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.ImageUtil;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;


/**
 * List of conflicts.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 */
public class ConflictListView
        extends GemPanel
{

  private ConflictTableModel tableModel;
  private JTable table;
  private GemLabel status;

  public ConflictListView() {

    tableModel = new ConflictTableModel();
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(40);
    cm.getColumn(2).setPreferredWidth(40);
    cm.getColumn(3).setPreferredWidth(30);
    cm.getColumn(4).setPreferredWidth(400);
//    JCheckBox checkBx = new JCheckBox(ImageUtil.createImageIcon(ImageUtil.CONFLICT_ICON));
//    checkBx.setSelectedIcon(ImageUtil.createImageIcon(ImageUtil.NO_CONFLICT_ICON));
    cm.getColumn(3).setCellRenderer(new CustomTrafficLightCellRenderer());
//    cm.getColumn(3).setCellEditor(new CustomTrafficLightEditor());
//    cm.getColumn(3).setCellEditor(new DefaultCellEditor(new TrafficLightCheckBox(ImageUtil.createImageIcon(ImageUtil.NO_CONFLICT_ICON))));
//    new JCheckBox()

    JScrollPane pm = new JScrollPane(table);

    status = new GemLabel();
    setLayout(new BorderLayout());
    add(pm, BorderLayout.CENTER);
    add(status, BorderLayout.SOUTH);
  }

  public void clear() {
    tableModel.clear();
    status.setText("");
  }

  public void addConflict(ScheduleTestConflict p) {
    tableModel.addItem(p);

  }

  public void setStatus(String s) {
    status.setText(tableModel.getRowCount() + " "
      + (s == null ? BundleUtil.getLabel("Conflicts.label").toLowerCase() : s)
    );
  }
//  public class TrafficLightCheckBox extends JCheckBox  {
//
////        private ImageIcon sad;
////        private ImageIcon happy;
//       private final ImageIcon iconOK = ImageUtil.createImageIcon(ImageUtil.NO_CONFLICT_ICON);
//    private final ImageIcon iconERR = ImageUtil.createImageIcon(ImageUtil.CONFLICT_ICON);
//
//    public TrafficLightCheckBox() {
//    }
//
////    public TrafficLightCheckBox(Icon icon) {
////      super(icon);
////    }
//
//
//  }
  public class CustomTrafficLightCellRenderer extends JCheckBox implements TableCellRenderer {
private final ImageIcon iconOK = ImageUtil.createImageIcon(ImageUtil.NO_CONFLICT_ICON);
    private final ImageIcon iconERR = ImageUtil.createImageIcon(ImageUtil.CONFLICT_ICON);

    public CustomTrafficLightCellRenderer() {
//      setIcon(iconOK);
    }

    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

//        if (value instanceof Boolean) {
            boolean selected = (boolean) value;
            setSelected(selected);
            setIcon(selected ? iconOK: iconERR);
//        }
        return this;
    }

}
//  public class CustomTrafficLightEditor extends AbstractCellEditor implements TableCellEditor {
//
//    private TrafficLightCheckBox editor;
//private final ImageIcon iconOK = ImageUtil.createImageIcon(ImageUtil.NO_CONFLICT_ICON);
//    private final ImageIcon iconERR = ImageUtil.createImageIcon(ImageUtil.CONFLICT_ICON);
//    public CustomTrafficLightEditor() {
//        editor = new TrafficLightCheckBox();
//    }
//
//    @Override
//    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//        if (value instanceof Boolean) {
//            //boolean selected = (boolean) value;
//            editor.setSelected((boolean) value);
//            editor.setIcon((boolean) value ? iconOK: iconERR);
//        }
//        return editor;
//    }
//
//    @Override
//    public Object getCellEditorValue() {
//        return editor.isSelected();
//    }
//
//}

  }
