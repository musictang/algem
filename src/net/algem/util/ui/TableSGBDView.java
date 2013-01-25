/*
 * @(#)TableSGBDView.java	2.6.a 06/08/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.ui;

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableSGBD;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TableSGBDView
        extends GemPanel
        implements ItemListener
{

  private DataConnection dc;
  private JComboBox bases;
  private SGBDTableModel tables;
  private JTable tableOfTables;
  private FieldTableModel fieldTableModel;
  private JTable fieldTable;

  public TableSGBDView(DataConnection _dc) {
    dc = _dc;
    bases = loadBase();

    tables = new SGBDTableModel();
    tableOfTables = new JTable(tables);
    tableOfTables.setAutoCreateRowSorter(true);
    tableOfTables.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int n = tableOfTables.getSelectedRow();
        TableSGBD v = (TableSGBD) tables.getItem(tableOfTables.convertRowIndexToModel(n));
        loadAttrib(v.getName());
      }
    });


    TableColumnModel cm = tableOfTables.getColumnModel();
    cm.getColumn(0).setPreferredWidth(100);
    cm.getColumn(1).setPreferredWidth(60);
    cm.getColumn(2).setPreferredWidth(60);
    cm.getColumn(3).setPreferredWidth(60);
    cm.getColumn(4).setPreferredWidth(60);
    cm.getColumn(5).setPreferredWidth(60);

    JScrollPane pmt = new JScrollPane(tableOfTables);
    /*
     * Column[] hdrTables = new Column[6]; hdrTables[0] = new
     * Column("Table",80); hdrTables[1] = new Column("Proprio",60); hdrTables[2]
     * = new Column("Nb tuples",50); hdrTables[3] = new Column("index",50);
     * hdrTables[4] = new Column("type",50); hdrTables[5] = new Column("Nb
     * Atts",50); tables = new Tableau(hdrTables,300,100);
     */

    fieldTableModel = new FieldTableModel();
    fieldTable = new JTable(fieldTableModel);
    fieldTable.setAutoCreateRowSorter(true);

    cm = fieldTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(200);
    cm.getColumn(1).setPreferredWidth(100);
    cm.getColumn(2).setPreferredWidth(60);

    JScrollPane pmc = new JScrollPane(fieldTable);

    /*
     * Column[] hdrAttrib = new Column[3]; hdrAttrib[0] = new Column("Nom",200);
     * hdrAttrib[1] = new Column("Type",50); hdrAttrib[2] = new
     * Column("Longueur",50); attributs = new Tableau(hdrAttrib,300,100);
     */

    bases.addItemListener(this);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(bases, 0, 0, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(pmt, 0, 1, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(pmc, 0, 2, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);

    loadBase();
    loadTable();
  }

  public JComboBox loadBase() {
    JComboBox base = new JComboBox();
    String query = "SELECT datname as base,usename as proprio from pg_database,pg_user where datdba = usesysid";

    try {
      ResultSet rs = dc.executeQuery(query);

      String[] ligne = new String[2];
      while (rs.next()) {
        base.addItem(rs.getString(1) + " (" + rs.getString(2) + ")");
      }
    } catch (Exception e) {
      GemLogger.logException(query, e);
    }

    return base;
  }

  public boolean loadTable() {
    String query = "SELECT relname,usename,reltuples,relhasindex,relkind,relnatts from pg_class,pg_user where relowner = usesysid AND relname !~ '^pg' AND relname !~ '^Inv' ORDER BY relname";
    tables.clear();

    try {
      ResultSet rs = dc.executeQuery(query);

      while (rs.next()) {
        TableSGBD t = new TableSGBD();
        t.setName(rs.getString(1));
        t.setOwner(rs.getString(2));
        t.setNTuples(rs.getInt(3));
        if (rs.getString(4).equals("t")) {
          t.setIndex("oui");
        } else {
          t.setIndex("non");
        }

        if (rs.getString(5).equals("r")) {
          t.setType("relation");
        } else if (rs.getString(5).equals("i")) {
          t.setType("relation");
        } else if (rs.getString(5).equals("S")) {
          t.setType("séquence");
        }
        t.setNAtts(rs.getInt(6));
        tables.addItem(t);
      }
    } catch (Exception e) {
      GemLogger.logException(query, e);
      return false;
    }
    return true;
  }

  public boolean loadAttrib(String relation) {
    if (relation == null) {
      return false;
    }

    String query = "SELECT a.attname,t.typname,a.attlen from pg_class c, pg_attribute a, pg_type t WHERE c.relname='" + relation.trim() + "' and a.attnum > 0 AND a.attrelid = c.oid AND a.atttypid = t.oid";
    fieldTableModel.clear();

    try {
      ResultSet rs = dc.executeQuery(query);

      while (rs.next()) {
        SGBDField c = new SGBDField();
        c.setName(rs.getString(1));
        c.setType(rs.getString(2));
        c.setLength(rs.getInt(3));
        fieldTableModel.addItem(c);
      }
    } catch (Exception e) {
      GemLogger.logException(query, e);
      return false;
    }

    return true;
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getSource() == bases) {
//			String[] col = (String[])evt.getItemSelectable().getSelectedObjects();
//			String base = col[0];

      String base = (String) bases.getSelectedItem();
      int i = base.indexOf(' ');
      base = base.substring(0, i);

      int port = dc.getDbport();
      String host = dc.getDbhost();
      try {
        dc.connect(host, port, base);
        loadTable();
      } catch (Exception e) {
        GemLogger.logException("connexion", e, this);
      }
    }
  }
}
