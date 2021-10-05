/*
 * @(#)MajoView.java	1.0a 07/07/1999
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
package net.algem.opt;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class MajoView
        extends GemPanel
{

  private MajoTableModel majos;
  private JTable table;
  private String id = "0";
  private GemField mode;
  private GemNumericField pcent;
  private GemButton ajouter;
  private GemButton modifier;
  private GemButton supprimer;

  public MajoView(String titre) {

    mode = new GemField(30);
    pcent = new GemNumericField(6);
    GemPanel p1 = new GemPanel();
    p1.add(mode);
    p1.add(pcent);

    majos = new MajoTableModel();
    table = new JTable(majos);
    table.setAutoCreateRowSorter(true);
    table.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        Majoration m = getSelected();
        setId(String.valueOf(m.getId()));
        setMode(m.getMode());
        setPCent(String.valueOf(m.getPCent()));
      }
    });


    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);

    JScrollPane pm = new JScrollPane(table);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    ajouter = new GemButton(GemCommand.ADD_CMD);
    modifier = new GemButton(GemCommand.MODIFY_CMD);
    supprimer = new GemButton(GemCommand.DELETE_CMD);
    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 3));
    boutons.add(ajouter);
    boutons.add(modifier);
    boutons.add(supprimer);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(p1, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(boutons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
  }

  @Override
  public synchronized void addMouseListener(MouseListener l) {
    table.addMouseListener(l);
  }

  public void addActionListener(ActionListener l) {
    ajouter.addActionListener(l);
    modifier.addActionListener(l);
    supprimer.addActionListener(l);
  }

  public String getId() {
    return id;
  }

  public void setId(String i) {
    id = i;
  }

  public String getMode() {
    return mode.getText();
  }

  public void setMode(String m) {
    mode.setText(m);
  }

  public String getPCent() {
    return pcent.getText();
  }

  public void setPCent(String p) {
    pcent.setText(p);
  }

  public Majoration getSelected() {
    return (Majoration) majos.getItem(table.convertRowIndexToModel(table.getSelectedRow()));
  }

  public Majoration getMajo() {
    Majoration m = new Majoration();
    m = new Majoration(Integer.parseInt(id), mode.getText(), Integer.parseInt(pcent.getText()));
    return m;
  }

  public void modRow(Majoration m) {
    majos.modItem(table.convertRowIndexToModel(table.getSelectedRow()), m);
  }

  public void addRow(Majoration m) {
    majos.addItem(m);
  }

  public void deleteCurrent() {
    majos.deleteItem(table.convertRowIndexToModel(table.getSelectedRow()));
  }
}
