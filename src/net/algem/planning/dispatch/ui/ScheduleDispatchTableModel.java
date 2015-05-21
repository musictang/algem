package net.algem.planning.dispatch.ui;

import net.algem.contact.Person;
import net.algem.planning.Schedule;
import net.algem.planning.dispatch.model.ScheduleDispatch;

import javax.swing.table.AbstractTableModel;

public class ScheduleDispatchTableModel extends AbstractTableModel {
    private final ScheduleDispatch scheduleDispatch;

    public ScheduleDispatchTableModel(ScheduleDispatch scheduleDispatch) {
        this.scheduleDispatch = scheduleDispatch;
    }

    @Override
    public int getRowCount() {
        return scheduleDispatch.getPersons().size();
    }

    @Override
    public int getColumnCount() {
        return scheduleDispatch.getSchedules().size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Boolean.class;
    }

    @Override
    public String getColumnName(int column) {
        return scheduleDispatch.getSchedules().get(column).getDate().toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return scheduleDispatch.isSubscribed(scheduleDispatch.getPersons().get(rowIndex),
                scheduleDispatch.getSchedules().get(columnIndex));
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);//TODO
        Person person = scheduleDispatch.getPersons().get(rowIndex);
        Schedule schedule = scheduleDispatch.getSchedules().get(columnIndex);
        boolean isSubscribed = scheduleDispatch.isSubscribed(person, schedule);

        if (!isSubscribed) {
            scheduleDispatch.subscribe(person, schedule);
        } else {
            scheduleDispatch.unsubscribe(person, schedule);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
