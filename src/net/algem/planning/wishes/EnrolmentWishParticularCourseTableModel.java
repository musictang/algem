/*
 * @(#)EnrolmentWishParticularCourseTableModel.java	2.17.0 13/03/19
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 13/03/19
 */
public class EnrolmentWishParticularCourseTableModel extends AbstractTableModel {

    public static final int COLUMN_HOUR = 0;
    public static final int COLUMN_CURRENT = 1;
    public static final int COLUMN_SELECTED = 2;
    public static final int COLUMN_MAILDATE = 3;
    public static final int COLUMN_SAMEASCURRENT = 4;
    public static final int COLUMN_CHOICE1 = 5;
    public static final int COLUMN_CHOICE2 = 6;
    public static final int COLUMN_CHOICE3 = 7;
    public static final int COLUMN_COUNT = 8;
    
    public static final int MINUTES_PER_ROW = 5;
    private static final String DATE_FORMATTER= "dd-MM-yyyy HH:mm";
    
    private List<EnrolmentWishParticularCourseLine> dataLines = new ArrayList<>();
    
    public EnrolmentWishParticularCourseTableModel(List datas) {
        this.dataLines = datas;
    }

    /**
     * Loads a list of enrolment whishes.
     *
     * @param lines
     */
    public void load(List<EnrolmentWishParticularCourseLine> lines) {
        dataLines = lines;
        fireTableDataChanged();
    }

    public void clear() {
        dataLines.clear();

        fireTableDataChanged();
    }

    public List<EnrolmentWishParticularCourseLine> getData() {
        return dataLines;
    }

    public void addElement(Object p) {
        dataLines.add((EnrolmentWishParticularCourseLine) p);
        fireTableRowsInserted(dataLines.size() - 1, dataLines.size() - 1);
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COLUMN_HOUR :
                return Hour.class;
            case COLUMN_CURRENT:
                return EnrolmentCurrent.class;
            case COLUMN_SELECTED:
                return EnrolmentSelected.class;
            case COLUMN_MAILDATE:
                return String.class;
            case COLUMN_SAMEASCURRENT:
            case COLUMN_CHOICE1:
            case COLUMN_CHOICE2:
            case COLUMN_CHOICE3:
                return EnrolmentWish.class;
            default:
                GemLogger.log("Error:EnrolmentWishParticularCourseTableModel.getColumnClass invalid column:" + col);
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
            case COLUMN_HOUR:
                return BundleUtil.getLabel("Hour.label");
            case COLUMN_CURRENT:
                return BundleUtil.getLabel("Enrolment.wish.current.label");
            case COLUMN_SELECTED:
                return BundleUtil.getLabel("Enrolment.wish.nextyear.label");
            case COLUMN_MAILDATE:
                return BundleUtil.getLabel("Enrolment.wish.mail.confirm.date.label");
            case COLUMN_SAMEASCURRENT:
                return BundleUtil.getLabel("Enrolment.wish.liste1.label");
            case COLUMN_CHOICE1:
                return BundleUtil.getLabel("Enrolment.wish.liste2.label");
            case COLUMN_CHOICE2:
                return BundleUtil.getLabel("Enrolment.wish.liste3.label");
            case COLUMN_CHOICE3:
                return BundleUtil.getLabel("Enrolment.wish.liste4.label");
            default:
                GemLogger.log("Error:EnrolmentWishParticularCourseTableModel#getColumnName colonne " + col);
        }
        return "Erreur";
    }

    @Override
    public int getRowCount() {
        return (dataLines == null) ? 0 : dataLines.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        EnrolmentWishParticularCourseLine line = dataLines.get(row);
        switch (col) {
            case COLUMN_HOUR:
                return line.getHour();
            case COLUMN_CURRENT:
                return line.getCurrent();
            case COLUMN_SELECTED:
                return line.getSelected();
            case COLUMN_MAILDATE:
                if (line.getMailDate() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                    return line.getMailDate().format(formatter);
                } else {
                    return "";
                }
            case COLUMN_SAMEASCURRENT:
                return line.getSameAsCurrent();
            case COLUMN_CHOICE1:
                return line.getChoice(0);
            case COLUMN_CHOICE2:
                return line.getChoice(1);
            case COLUMN_CHOICE3:
                return line.getChoice(2);
            default:
                GemLogger.log("Error:EnrolmentWishParticularCourseTableModel.getValueAt colonne " + col);
        }
        return "erreur";
    }

    @Override
    public void setValueAt(Object o, int row, int col) {
        EnrolmentWishParticularCourseLine ligne = (EnrolmentWishParticularCourseLine) dataLines.get(row);
        switch (col) {
            case COLUMN_HOUR:
                ligne.setHour((Hour) o);
                break;
            case COLUMN_CURRENT:
                ligne.setCurrent((EnrolmentCurrent) o);
                break;
            case COLUMN_SELECTED:
                ligne.setSelected((EnrolmentSelected)o);
                break;
            case COLUMN_MAILDATE:
                ligne.setMailDate((LocalDateTime)o);
                break;
            case COLUMN_SAMEASCURRENT:
                ligne.setSameAsCurrent((EnrolmentWish) o);
                break;
            case COLUMN_CHOICE1:
                ligne.setChoice(0, (EnrolmentWish) o);
                break;
            case COLUMN_CHOICE2:
                ligne.setChoice(1, (EnrolmentWish) o);
                break;
            case COLUMN_CHOICE3:
                ligne.setChoice(2, (EnrolmentWish) o);
                break;
            default:
                GemLogger.log("Error:EnrolmentWishParticularCourseTableModel.getValueAt colonne " + col);
        }

        fireTableChanged(new TableModelEvent(this, row, row, col));
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col >= COLUMN_SELECTED;
    }

    public int getRowFromHour(Hour hour, int minutesPerRow) {
        Hour depart = (Hour)getValueAt(0,0);
        int nbMinutes = hour.toMinutes()-depart.toMinutes();
        return (nbMinutes / minutesPerRow);    
    }
            
    public void unCheckColumn(int line, int col) {
        EnrolmentWishParticularCourseLine ligne = (EnrolmentWishParticularCourseLine) dataLines.get(line);
        switch (col) {
            case COLUMN_SAMEASCURRENT:
                ligne.getSameAsCurrent().setSelected(false);
                break;
            case COLUMN_CHOICE1:
                ligne.getChoice(0).setSelected(false);
                break;
            case COLUMN_CHOICE2:
                ligne.getChoice(1).setSelected(false);
                break;
            case COLUMN_CHOICE3:
                ligne.getChoice(2).setSelected(false);
                break;
            default:
                GemLogger.log("Error:EnrolmentWishParticularCourseTableModel unCheck unknow colum " + col);
        }
        fireTableChanged(new TableModelEvent(this, line, line));
    }

    public void unCheckAll(int line) {
        EnrolmentWishParticularCourseLine ligne = (EnrolmentWishParticularCourseLine) dataLines.get(line);
        ligne.getSameAsCurrent().setSelected(false);
        ligne.getChoice(0).setSelected(false);
        ligne.getChoice(1).setSelected(false);
        ligne.getChoice(2).setSelected(false);
        fireTableChanged(new TableModelEvent(this, line, line));
    }

    public int getChecked(int line) {
        EnrolmentWishParticularCourseLine ligne = (EnrolmentWishParticularCourseLine) dataLines.get(line);
        if (ligne.getSameAsCurrent().isSelected()) {
            return COLUMN_SAMEASCURRENT;
        }
        if (ligne.getChoice(0).isSelected()) {
            return COLUMN_CHOICE1;
        }
        if (ligne.getChoice(1).isSelected()) {
            return COLUMN_CHOICE2;
        }
        if (ligne.getChoice(2).isSelected()) {
            return COLUMN_CHOICE3;
        }

        return -1;
    }

     
    public void refreshStudent(int studentId) {
        for (int i = 0; i < dataLines.size(); i++) {
            EnrolmentWishParticularCourseLine line = dataLines.get(i);
            if ((line.getSameAsCurrent().getStudentId() == studentId)
                    || (line.getChoice(0).getStudentId() == studentId)
                    || (line.getChoice(1).getStudentId() == studentId) 
                    || (line.getChoice(2).getStudentId() == studentId)) {
                fireTableChanged(new TableModelEvent(this, i, i));
            }
        }
        
    }
}
