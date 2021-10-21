/*
 * @(#)ScriptArgumentTableModel.java 2.10.0 23/05/16
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
package net.algem.script.ui;

import net.algem.script.common.ScriptArgument;
import net.algem.script.execution.models.ScriptUserArguments;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import net.algem.contact.teacher.Teacher;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import static net.algem.script.common.ArgType.TEACHER_LIST;

/**
 * @author Alexandre Delattre
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.9.4.9
 */
public class ScriptArgumentTableModel
        extends AbstractTableModel
{

  private final List<ScriptArgument> arguments;
  private final Map<String, Object> values;
  private static final NumberFormat longFormat = NumberFormat.getIntegerInstance();

  public ScriptArgumentTableModel(List<ScriptArgument> arguments) {
    this.arguments = arguments;
    values = new HashMap<>();
  }

  @Override
  public int getRowCount() {
    return arguments.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 1;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    ScriptArgument scriptArgument = arguments.get(rowIndex);
    if (columnIndex == 0) {
      return scriptArgument.getLabel();
    } else {
      return values.get(scriptArgument.getName());
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    ScriptArgument scriptArgument = arguments.get(rowIndex);
    values.put(scriptArgument.getName(), aValue);
    super.setValueAt(aValue, rowIndex, columnIndex);//TODO
  }

  public ScriptUserArguments getUserArguments() {
    return new ScriptUserArguments(values);
  }

  public static class MyCellEditorFactory
          implements JTableX.CellEditorFactory
  {

    private final List<ScriptArgument> arguments;
    private final DataCache dataCache;

    public MyCellEditorFactory(List<ScriptArgument> arguments, DataCache dataCache) {
      this.arguments = arguments;
      this.dataCache = dataCache;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
      ScriptArgument scriptArgument = arguments.get(row);
      TableCellEditor editor = new DefaultCellEditor(new JTextField());
      switch (scriptArgument.getType()) {
        case TEXT:
          editor = new DefaultCellEditor(new JTextField());
          break;
        case INT:
          NumberFormatter numberFormatter = new NumberFormatter(longFormat);
          numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
          numberFormatter.setAllowsInvalid(false); //this is the key!!
          numberFormatter.setMinimum(0l); //Optional
          return new DefaultCellEditor(new JFormattedTextField(numberFormatter))
          {
            @Override
            public Object getCellEditorValue() {
              Object cellEditorValue = super.getCellEditorValue();
              if (cellEditorValue != null && cellEditorValue instanceof String) {
                if (((String) cellEditorValue).isEmpty()) {
                  return null;
                }
                try {
                  return longFormat.parse((String) cellEditorValue).intValue();
                  // return Integer.parseInt((String) cellEditorValue);
                } catch (ParseException ex) {
                  GemLogger.log(ex.getMessage());
                }
              }
              return cellEditorValue;
            }
          };
        case FLOAT:
          break;
        case BOOL:
          return new DefaultCellEditor(new JCheckBox());
        case DATE:
          return new DatePickerCellEditor(new SimpleDateFormat("dd-MM-yyyy"));
        case TEACHER_LIST:
          List<Teacher> teachers = dataCache.getList(Model.Teacher).getData();
          return new DefaultCellEditor(new JComboBox(teachers.toArray()));
      }
      return editor;
    }
  }

  public static class MyCellRenderer
          implements TableCellRenderer
  {

    private final List<ScriptArgument> arguments;
    private final DataCache dataCache;

    public MyCellRenderer(List<ScriptArgument> arguments, DataCache dataCache) {
      this.arguments = arguments;
      this.dataCache = dataCache;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      ScriptArgument scriptArgument = arguments.get(row);
      switch (scriptArgument.getType()) {
        case TEXT:
        case FLOAT:
          return new JLabel(value != null ? value.toString() : "");
        case INT:
          return new JLabel(value != null ? longFormat.format(value) : "");
        case BOOL:
          return new JCheckBox("", value != null && (boolean) value);
        case DATE:
          if (value != null) {
            String dateText = new SimpleDateFormat("dd-MM-yyyy").format((Date) value);
            return new JLabel(dateText);
          } else {
            return new JLabel("");
          }
        case TEACHER_LIST:
          return new JLabel(value != null ? value.toString() : "");
      }
      return null;
    }
  }

}
