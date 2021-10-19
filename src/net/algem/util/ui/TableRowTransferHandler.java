/*
 * @(#)TableRowTransferHandler.java 2.9.4.13 11/11/15
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.util.ui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import net.algem.util.GemLogger;

/**
 * Handles drag & drop row reordering.
 *
 * @version 2.9.4.13
 * @since 2.9.4.12 16/09/15
 * @see <a href="http://stackoverflow.com/questions/638807/how-do-i-drag-and-drop-a-row-in-a-jtable">how-do-i-drag-and-drop-a-row-in-a-jtable</a>
 *
 */
public class TableRowTransferHandler
        extends TransferHandler
{

  private final DataFlavor localObjectFlavor = new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index");
  private JTable table = null;

  public TableRowTransferHandler(JTable table) {
    this.table = table;
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    assert (c == table);
    return new DataHandler(table.getSelectedRow(), localObjectFlavor.getMimeType());
  }

  @Override
  public boolean canImport(TransferHandler.TransferSupport info) {
    boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
    return b;
  }

  @Override
  public int getSourceActions(JComponent c) {
    return TransferHandler.COPY_OR_MOVE;
  }

  @Override
  public boolean importData(TransferHandler.TransferSupport info) {
    JTable target = (JTable) info.getComponent();
    JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
    int index = dl.getRow();
    int max = table.getModel().getRowCount();
    if (index < 0 || index > max) {
      index = max;
    }
    target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    try {
      Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
      if (rowFrom != -1 && rowFrom != index) {
        ((Reorderable) table.getModel()).reorder(rowFrom, index);
        if (index > rowFrom) {
          index--;
        }
        target.getSelectionModel().addSelectionInterval(index, index);
        return true;
      }
    } catch (Exception e) {
        GemLogger.logException(e);
    }
    return false;
  }

  @Override
  protected void exportDone(JComponent c, Transferable t, int act) {
    if ((act == TransferHandler.MOVE) || (act == TransferHandler.NONE)) {
      table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  public interface Reorderable
  {

    public void reorder(int fromIndex, int toIndex);
  }

}
