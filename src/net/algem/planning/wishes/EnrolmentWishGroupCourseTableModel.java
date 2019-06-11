/*
 * @(#)EnrolmentWishGroupCourseTableModel.java	2.17.0 15/05/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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

package net.algem.planning.wishes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.ui.GemButton;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 15/05/19
 */
public class EnrolmentWishGroupCourseTableModel extends AbstractTableModel {

    public final static int COLUMN_NUMBER = 0;
    public final static int COLUMN_STUDENT = 1;
    public final static int COLUMN_SELECTED = 2;
    public final static int COLUMN_AGE = 3;
    public final static int COLUMN_INSTRUMENT = 4;
    public final static int COLUMN_PRACTICE = 5;
    public final static int COLUMN_DELETE = 6;
    public final static int COLUMN_COUNT = 7;
    
    private List<EnrolmentWishGroupCourseLine> dataLines = new ArrayList<>();
    
    public EnrolmentWishGroupCourseTableModel(List datas) {
        DefaultTableModel m;
        this.dataLines = datas;
    }

    /**
     * Loads a list of enrolment whishes.
     *
     * @param lines
     */
    public void load(List<EnrolmentWishGroupCourseLine> lines) {
        dataLines = lines;
        fireTableDataChanged();
    }

    public void clear() {
        dataLines.clear();

        fireTableDataChanged();
    }

    public List<EnrolmentWishGroupCourseLine> getData() {
        return dataLines;
    }

    public void addElement(Object p) {
        dataLines.add((EnrolmentWishGroupCourseLine) p);
        fireTableRowsInserted(dataLines.size() - 1, dataLines.size() - 1);
    }

    public void removeRow(int row) {
        dataLines.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COLUMN_NUMBER :
                return Integer.class;
            case COLUMN_STUDENT:
                return Person.class;
            case COLUMN_SELECTED:
                return Boolean.class;
            case COLUMN_AGE:
                return String.class;
            case COLUMN_INSTRUMENT:
                return String.class;
            case COLUMN_PRACTICE:
                return Integer.class;
            case COLUMN_DELETE:
                return GemButton.class;
            default:
                GemLogger.log("Error:EnrolmentWishGroupCourseTableModel.getColumnClass invalid column:" + col);
        }
        return Object.class;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case COLUMN_NUMBER:
                return "";
            case COLUMN_STUDENT:
                return BundleUtil.getLabel("Member.label");
            case COLUMN_SELECTED:
                return BundleUtil.getLabel("Enrolment.wish.option.label");
            case COLUMN_AGE:
                return BundleUtil.getLabel("Member.age.label");
            case COLUMN_INSTRUMENT:
                return BundleUtil.getLabel("Instrument.label");
            case COLUMN_PRACTICE:
                return BundleUtil.getLabel("Practical.experience.label");
            case COLUMN_DELETE:
                return "";
            default:
                GemLogger.log("Error:EnrolmentWishGroupCourseTableModel#getColumnName colonne " + col);
        }
        return "Erreur";
    }

    @Override
    public int getRowCount() {
        return (dataLines == null) ? 0 : dataLines.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        EnrolmentWishGroupCourseLine line = dataLines.get(row);
        switch (col) {
            case COLUMN_NUMBER:
                return row+1;
            case COLUMN_STUDENT:
                return line.getStudent() != null ? line.getStudent().getFirstnameName() : "";
            case COLUMN_SELECTED:
                return line.isSelected();
            case COLUMN_AGE:
                return ageFromBirth(line.getBirthDate());
            case COLUMN_INSTRUMENT:
                return line.getInstrument();
            case COLUMN_PRACTICE:
                return line.getPractice();
            case COLUMN_DELETE:
                return BundleUtil.getLabel("Action.suppress.label");
            default:
                GemLogger.log("Error:EnrolmentWishGroupCourseTableModel.getValueAt colonne " + col);
        }
        return "erreur";
    }

    @Override
    public void setValueAt(Object o, int row, int col) {
        EnrolmentWishGroupCourseLine line = dataLines.get(row);
        switch (col) {
            case COLUMN_SELECTED :
                line.setSelected((Boolean)o);
                break;
            default:
                GemLogger.log("Error:EnrolmentWishGroupSourseTableModel.getValueAt colonne " + col);
        }

        fireTableChanged(new TableModelEvent(this, row, row, col));
    }

@Override
    public boolean isCellEditable(int row, int col) {
        return col == COLUMN_DELETE || col == COLUMN_SELECTED;
    }
    
    public boolean isSelected(int row) {
        EnrolmentWishGroupCourseLine line = dataLines.get(row);

        return line != null ? line.isSelected() : false;
    }

    public EnrolmentWish getWish(int row) {
        EnrolmentWishGroupCourseLine line = dataLines.get(row);
        
        return line != null ? line.getWish() : null;
    }
    
    public static String ageFromBirth(DateFr birth) {
    if (birth == null) return "";
    
    LocalDate now = LocalDate.now();
    LocalDate birthDate = LocalDate.of(birth.getYear(), birth.getMonth(), birth.getDay());
    int year = birthDate.until(now).getYears();
    int month = birthDate.until(now).getMonths();
    
    return year+" ans "+month+" mois";
}
/*     
    public void refreshStudent(int studentId) {
        for (int i = 0; i < dataLines.size(); i++) {
            EnrolmentWishGroupCourseLine line = dataLines.get(i);
            if ((line.getSameAsCurrent().getStudentId() == studentId)
                    || (line.getChoice(0).getStudentId() == studentId)
                    || (line.getChoice(1).getStudentId() == studentId) 
                    || (line.getChoice(2).getStudentId() == studentId)) {
                fireTableChanged(new TableModelEvent(this, i, i));
            }
        }
        
    }
*/
}
