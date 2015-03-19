
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.planning;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.algem.planning.day.DayChoice;
import net.algem.room.Room;
import net.algem.room.RoomActiveChoiceModel;
import net.algem.room.RoomCellEditor;
import net.algem.room.RoomChoice;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.HourCellEditor;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 18/03/2015
 */
public class AdministrativeTableView
  extends GemPanel
{
  
  static final String [] WEEKDAYS = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
  private final JTableModel tableModel;
  
  public AdministrativeTableView(DataCache dataCache) {   
    tableModel = new AdministrativeActionTableModel();
    JTable table = new JTable(tableModel);
    HourCellEditor cellEditor = new HourCellEditor();
    table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new DayChoice()));
    table.getColumnModel().getColumn(1).setCellEditor(cellEditor);
    table.getColumnModel().getColumn(2).setCellEditor(cellEditor);
//    table.getColumnModel().getColumn(3).setCellEditor(new RoomCellEditor(dataCache));
    table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new RoomChoice(new RoomActiveChoiceModel(dataCache.getList(Model.Room), true))));
    JScrollPane scroll = new JScrollPane(table);
    setLayout(new BorderLayout());
    add(scroll, BorderLayout.CENTER);
  }
  
  public void load() throws SQLException {
    
    for (int i = 1; i < WEEKDAYS.length ; i++) {
//      System.out.println(WEEKDAYS[i]);
      ActionTableModel a = new ActionTableModel();
      a.setDay(WEEKDAYS[i]);
      a.setStart(new Hour("00:00"));
      a.setEnd(new Hour("00:00"));
      a.setRoom((Room) DataCache.findId(0, Model.Room));
      tableModel.addItem(a);
    }
  }

}
