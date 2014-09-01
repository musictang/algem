/*
 * @(#)CityCtrl.java	2.8.w 08/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Association controller between cities and postal codes.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 02/09/2001
 */
public class CityCtrl
        extends GemPanel
        implements ActionListener
{

  private CodePostalField cdp;
  private GemField city;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDelete;
  private GemButton btClose;
  private CityTableModel cityTableModel;
  private JTable cityTable;
  private static final String entry_error = MessageUtil.getMessage("entry.error");
  private final DataConnection dc;
  private final GemDesktop desktop;

  public CityCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dc = DataCache.getDataConnection();

    cityTableModel = new CityTableModel();
    cityTable = new JTable(cityTableModel);
    cityTable.setAutoCreateRowSorter(true);
    cityTable.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e) {
        int n = cityTable.convertRowIndexToModel(cityTable.getSelectedRow());
        City v = (City) cityTableModel.getItem(n);
        cdp.setText(v.getCdp());
        city.setText(v.getCity());
      }
    });


    TableColumnModel cm = cityTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);

    JScrollPane pm = new JScrollPane(cityTable);

    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btClose = new GemButton(GemCommand.CLOSE_CMD);
    
    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 4));
    boutons.add(btAdd);
    boutons.add(btModify);
    boutons.add(btDelete);
    boutons.add(btClose);

    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btDelete.addActionListener(this);
    btClose.addActionListener(this);

    cdp = new CodePostalField();
    city = new GemField(30);
    GemPanel p1 = new GemPanel();
    p1.add(cdp);
    p1.add(city);

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(p1, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(boutons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    load();
  }

  public void load() {
    Vector v = CityIO.find("", dc);
    for (int i = 0; i < v.size(); i++) {
      cityTableModel.addItem((City) v.elementAt(i));
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.CLOSE_CMD)) {
      close();
    }
    else if (cmd.equals(GemCommand.ADD_CMD)) {
      try {
        insertion();
        //clear();
      } catch (SQLException e) {
        GemLogger.logException("insertion ville", e, this);
      }
    }
    else {
      int n = cityTable.getSelectedRow();
      if (n < 0) {
        return;
      }// N <=0
      n = cityTable.convertRowIndexToModel(n);
      
      if (cmd.equals(GemCommand.MODIFY_CMD)) {
        try {
          modification(n);
        } catch (SQLException e) {
          GemLogger.logException("modification ville", e, this);
        }
      } else if (cmd.equals(GemCommand.DELETE_CMD)) {
        try {
          suppression(n);
          //clear();
        } catch (SQLException e) {
          GemLogger.logException("suppresion ville", e, this);
        }
      }
    }
  }

  void modification(int n) throws SQLException {
    String sc = cdp.getText().trim();
    String sv = city.getText().trim();
    if (sc.length() == 0 || sv.length() == 0) {
      new ErrorDlg(this, entry_error);
      return;
    }
    City v = new City(sc, sv);
    CityIO.update(v, dc);

    cityTableModel.modItem(n, v);
    clear();
  }

  void insertion() throws SQLException {
    String sc = cdp.getText().trim();
    String sv = city.getText().trim();
    if (sc.length() == 0 || sv.length() == 0) {
      new ErrorDlg(this, entry_error);
      return;
    }
    City v = new City(sc, sv);
    CityIO.insert(v, dc);

    cityTableModel.addItem(v);
    clear();
  }

  void suppression(int n) throws SQLException {
    String sc = cdp.getText().trim();
    String sv = city.getText().trim();
    if (sc.length() == 0 || sv.length() == 0) {
      new ErrorDlg(this, entry_error);
      return;
    }
    City v = new City(sc, sv);
    CityIO.delete(v, dc);

    cityTableModel.deleteItem(n);
    clear();
  }

  void clear() {
    cdp.setText("");
    city.setText("");
//		liste.clear();
  }

  private void close() {
    desktop.removeModule(GemModule.CITY_KEY);
  }
}
