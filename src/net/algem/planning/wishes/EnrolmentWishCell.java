/*
 * @(#)EnrolmentWishCell.java	2.17.0 16/03/19
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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */
public class EnrolmentWishCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private EnrolmentWishPanel renderer;
    private EnrolmentWishPanel editor;

    public EnrolmentWishCell(ActionListener listener) {
        renderer = new EnrolmentWishPanel(listener);
        editor = new EnrolmentWishPanel(listener);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderer.setCurrent((EnrolmentSelected) value, isSelected ? table.getSelectionBackground() : table.getBackground());

        return renderer;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editor.setCurrent((EnrolmentSelected) value, isSelected ? table.getSelectionBackground() : table.getBackground());

        return editor;
    }
    
    @Override
    public Object getCellEditorValue() {
        return editor.getCurrent();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true; 
    }
}
