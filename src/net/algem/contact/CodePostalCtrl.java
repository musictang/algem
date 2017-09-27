/*
 * @(#)CodePostalCtrl.java	2.15.2 27/09/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * Search controller for a city from its postal code.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 */
public class CodePostalCtrl
        extends KeyAdapter
        implements ActionListener, FocusListener
{

  private DataConnection dc;
  private GemField zipCode;
  private GemField city;
  private SelectCityDlg selectCityDlg;

  public CodePostalCtrl(DataConnection dc) {
    this.dc = dc;
  }

  public void setFields(GemField _city, GemField _zip) {
    city = _city;
    zipCode = _zip;
  }

  public void findCity(GemField cdp) {
    List<City> v = CityIO.findCity(cdp.getText(), dc);
    int size = v.size();
    if (size > 0) {
      if (size == 1) {
        city.setText(v.get(0).getCity());
      } else {
        selectCityDlg = new SelectCityDlg(PopupDlg.getTopFrame(city), true);
        selectCityDlg.loadResult(v);
        selectCityDlg.initUI();
        City c = selectCityDlg.getCity();
        if (c != null) {
          city.setText(c.getCity());
        }
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int length = zipCode.getText().length();
    if (length == 5) {
      findCity(zipCode);
    }
  }

  @Override
  public void focusGained(FocusEvent evt) {
  }

  @Override
  public void focusLost(FocusEvent evt) {
    if (city != null && city.getText().length() == 0) {
      findCity((GemField) evt.getSource());
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    findCity((GemField) evt.getSource());
  }

  class SelectCityDlg
          extends JDialog
          implements ActionListener
  {

    private CityTableModel model;
    private JTable table;
    private GemButton btClose;
    private Frame parent;
    private City city;

    SelectCityDlg(Frame owner, boolean modal) {
      super(owner, BundleUtil.getLabel("City.label"), modal);
      this.parent = owner;
      model = new CityTableModel();
      table = new JTable(model);
    }

    void initUI() {
      table.getColumnModel().getColumn(0).setPreferredWidth(80);
      table.getColumnModel().getColumn(1).setPreferredWidth(240);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent event) {
          if (event.getValueIsAdjusting()) {
            return;
          }
          setCity();
          setVisible(false);
        }
      });
      GemPanel buttons = new GemPanel(new GridLayout(1, 2));
      JScrollPane scroll = new JScrollPane(table);
      add(scroll, BorderLayout.CENTER);
      btClose = new GemButton(GemCommand.CLOSE_CMD);
      btClose.addActionListener(this);

      buttons.add(btClose);
      add(buttons, BorderLayout.SOUTH);
      setSize(GemModule.XS_SIZE);
      setLocationRelativeTo(parent);
      setVisible(true);
    }

    void setCity() {
      int row = table.getSelectedRow();
      if (row >= 0) {
        city = (City) model.getItem(row);
      }
    }

    City getCity() {
      return city;
    }

    public void loadResult(List<City> result) {
      for (City c : result) {
        model.addItem(c);
      }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      /*Object src = e.getSource();
      if (src == btOk) {
        setCity();
      } else {
        city = null;
      }*/
      setVisible(false);
    }
  }

}
