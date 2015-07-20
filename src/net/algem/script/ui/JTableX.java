package net.algem.script.ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.Vector;

public class JTableX extends JTable {

    public JTableX() {
    }

    public JTableX(TableModel dm) {
        super(dm);
    }

    public JTableX(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public JTableX(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public JTableX(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public JTableX(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public JTableX(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    private CellEditorFactory cellEditorFactory;

    public CellEditorFactory getCellEditorFactory() {
        return cellEditorFactory;
    }

    public void setCellEditorFactory(CellEditorFactory cellEditorFactory) {
        this.cellEditorFactory = cellEditorFactory;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (cellEditorFactory != null) {
            return cellEditorFactory.getCellEditor(row, column);
        }
        return super.getCellEditor(row, column);//TODO
    }

    public interface CellEditorFactory {
        TableCellEditor getCellEditor(int row, int column);
    }
}
