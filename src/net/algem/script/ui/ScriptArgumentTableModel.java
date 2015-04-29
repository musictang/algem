package net.algem.script.ui;

import net.algem.script.common.ScriptArgument;
import net.algem.script.execution.models.ScriptUserArguments;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ScriptArgumentTableModel extends AbstractTableModel {

    private final List<ScriptArgument> arguments;
    private final Map<String, Object> values;

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

    public static class MyCellEditorFactory implements JTableX.CellEditorFactory {
        private final List<ScriptArgument> arguments;

        public MyCellEditorFactory(List<ScriptArgument> arguments) {
            this.arguments = arguments;
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
                    NumberFormat longFormat = NumberFormat.getIntegerInstance();
                    NumberFormatter numberFormatter = new NumberFormatter(longFormat);
                    numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
                    numberFormatter.setAllowsInvalid(false); //this is the key!!
                    numberFormatter.setMinimum(0l); //Optional
                    return new DefaultCellEditor(new JFormattedTextField(numberFormatter)) {
                        @Override
                        public Object getCellEditorValue() {
                            Object cellEditorValue = super.getCellEditorValue();
                            if (cellEditorValue != null && cellEditorValue instanceof String) {
                                if (((String) cellEditorValue).isEmpty()) {
                                    return null;
                                }
                                return Integer.parseInt((String) cellEditorValue);
                            }
                            return cellEditorValue;
                        }
                    };
                case FLOAT:
                    break;
                case BOOL:
                    return new DefaultCellEditor(new JCheckBox());
                case DATE:
                    return new DatePickerCellEditor(new SimpleDateFormat("dd/MM/yyyy"));
            }
            return editor;
        }
    }

    public static class MyCellRenderer implements TableCellRenderer {
        private final List<ScriptArgument> arguments;

        public MyCellRenderer(List<ScriptArgument> arguments) {
            this.arguments = arguments;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ScriptArgument scriptArgument = arguments.get(row);
            switch (scriptArgument.getType()) {
                case TEXT:
                case INT:
                case FLOAT:
                    return new JLabel(value != null ?value.toString() : "");
                case BOOL:
                    return new JCheckBox("", value != null && (boolean) value);
                case DATE:
                    if (value != null) {
                        String dateText = new SimpleDateFormat("dd/MM/yyyy").format((Date) value);
                        return new JLabel(dateText);
                    } else {
                        return new JLabel("");
                    }
            }
            return null;
        }
    }
}
