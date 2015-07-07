package net.algem.script.ui;

import net.algem.script.execution.models.ScriptResult;

import javax.swing.table.AbstractTableModel;

public class ScriptResultTableModel extends AbstractTableModel {
    private final ScriptResult scriptResult;

    public ScriptResultTableModel(ScriptResult scriptResult) {
        this.scriptResult = scriptResult;
    }

    @Override
    public int getRowCount() {
        return scriptResult.getRows().size();
    }

    @Override
    public int getColumnCount() {
        return scriptResult.getHeader().size();
    }

    @Override
    public String getColumnName(int column) {
        return scriptResult.getHeader().get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return scriptResult.getRows().get(rowIndex).get(columnIndex);
    }
}
