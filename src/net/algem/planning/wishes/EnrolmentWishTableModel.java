/*
 * @(#)EnrolmentWishTableModel.java	2.17.0 16/03/19
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
 * @since 2.17.0 16/03/19
 */
public class EnrolmentWishTableModel extends AbstractTableModel {

    public final static int COLUMN_DATE = 0;
    public final static int COLUMN_PREFERENCE = 1;
    public final static int COLUMN_SELECTED = 2;
    public final static int COLUMN_COURSE = 3;
    public final static int COLUMN_DAY = 4;
    public final static int COLUMN_HOUR = 5;
    public final static int COLUMN_DURATION = 6;
    public final static int COLUMN_TEACHER = 7;
    public final static int COLUMN_NOTE = 8;
    public final static int COLUMN_MAILDATE = 9;
    public final static int COLUMN_COUNT = 10;
    
    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private List<EnrolmentWish> datas = new ArrayList<>();
    
    public EnrolmentWishTableModel(List datas) {
        this.datas = datas;
    }

    /**
     * Loads a list of enrolment whishes.
     *
     * @param lines
     */
    public void load(List<EnrolmentWish> lines) {
        datas = lines;
        fireTableDataChanged();
    }

    public void clear() {
        datas.clear();

        fireTableDataChanged();
    }

    public List<EnrolmentWish> getData() {
        return datas;
    }

    public void addElement(Object p) {
        datas.add((EnrolmentWish) p);
        fireTableRowsInserted(datas.size() - 1, datas.size() - 1);
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COLUMN_DATE :
                return String.class;
            case COLUMN_PREFERENCE :
                return Integer.class;
            case COLUMN_SELECTED :
                return Boolean.class;
            case COLUMN_COURSE :
                return String.class;
            case COLUMN_DAY :
                return String.class;
            case COLUMN_HOUR :
                return String.class;
            case COLUMN_DURATION :
                return String.class;
            case COLUMN_TEACHER :
                return String.class;
            case COLUMN_NOTE :
                return String.class;
            case COLUMN_MAILDATE :
                return String.class;
                
            default:
                GemLogger.log("Error:EnrolmentWishTableModel.getColumnClass invalid column:" + col);
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
            case COLUMN_DATE : //TODO ERIC  BundleUtil.getLabel("Payer.label");
                return BundleUtil.getLabel("Date.label");
            case COLUMN_PREFERENCE :
                return BundleUtil.getLabel("Enrolment.wish.label");
            case COLUMN_SELECTED :
                return BundleUtil.getLabel("Enrolment.wish.selected.label");
            case COLUMN_COURSE :
                return BundleUtil.getLabel("Course.label");
            case COLUMN_DAY :
                return BundleUtil.getLabel("Day.label");
            case COLUMN_HOUR :
                return BundleUtil.getLabel("Hour.label");
            case COLUMN_DURATION :
                return BundleUtil.getLabel("Duration.label");
            case COLUMN_TEACHER :
                return BundleUtil.getLabel("Profile.teacher.label");
            case COLUMN_NOTE :
                return BundleUtil.getLabel("Notes.label");
            case COLUMN_MAILDATE :
                return BundleUtil.getLabel("Enrolment.wish.mail.confirm.date.label");
            default:
                GemLogger.log("Error:EnrolmentWishTableModel#getColumnName colonne " + col);
        }
        return "Erreur";
    }

    @Override
    public int getRowCount() {
        return (datas == null) ? 0 : datas.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        EnrolmentWish wish = datas.get(row);
        switch (col) {
            case COLUMN_DATE : 
                    return wish.getCreationDate().format(dateFormatter);
            case COLUMN_PREFERENCE :
                return wish.getPreference();
            case COLUMN_SELECTED :
                return wish.isSelected();
            case COLUMN_COURSE :
                return wish.getCourseLabel();
            case COLUMN_DAY :
                return wish.getDayLabel();
            case COLUMN_HOUR :
                return wish.getHour().toString();
            case COLUMN_DURATION :
                return wish.getDuration().toString();
            case COLUMN_TEACHER :
                return wish.getTeacherLabel();
            case COLUMN_NOTE :
                return wish.getNote();
            case COLUMN_MAILDATE :
                if (wish.getDateMailConfirm() != null) {
                    return wish.getDateMailConfirm().format(timestampFormatter);
                } else {
                    return "";
                }
            default:
                GemLogger.log("Error:EnrolmentWishTableModel.getValueAt colonne " + col);
        }
        return "erreur";
    }

    @Override
    public void setValueAt(Object o, int row, int col) {
        EnrolmentWish wish = (EnrolmentWish) datas.get(row);
        switch (col) {
            case COLUMN_DATE : 
                //wish.setCreateDate((LocalDate)o);    //TODO ERIC wish.setCreateDate();
                break;
            case COLUMN_PREFERENCE :
                wish.setPreference((short)Integer.parseInt((String)o));
                break;
            case COLUMN_SELECTED :
                wish.setSelected((Boolean)o);
                break;
            case COLUMN_COURSE :
                wish.setCourseLabel((String)o);
                break;
            case COLUMN_DAY :
                wish.setDayLabel((String)o);
                break;
            case COLUMN_HOUR :
                wish.setHour(new Hour((String)o));
                break;
            case COLUMN_DURATION :
                wish.setDuration(new Hour((String)o));
                break;
            case COLUMN_TEACHER :
                wish.setTeacherLabel((String)o);
                break;
            case COLUMN_NOTE :
                wish.setNote((String)o);
                break;
            case COLUMN_MAILDATE :
                wish.setDateMailConfirm((LocalDateTime)o);
                break;
            default:
                GemLogger.log("Error:EnrolmentWishTableModel.getValueAt colonne " + col);
        }

        fireTableChanged(new TableModelEvent(this, row, row, col));
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

}
