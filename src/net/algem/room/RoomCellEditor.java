
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.room;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 18/03/2015
 */
public class RoomCellEditor
extends AbstractCellEditor
        implements TableCellEditor
{

  private JComponent component;

  public RoomCellEditor(DataCache dataCache) {
    component = new RoomChoice(new RoomActiveChoiceModel(dataCache.getList(Model.Room), true));
  }

  @Override
  public Object getCellEditorValue() {
    return ((RoomChoice) component).getKey();
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    ((RoomChoice) component).setKey((int) value);
    return ((RoomChoice) component);
  }

}
