/*
 * @(#)TodoListCtrl.java	2.8.w 08/07/14
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
package net.algem.opt;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataCache;
import net.algem.config.CategoryOccupChoice;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @deprecated
 */
public class TodoListCtrl
        extends GemPanel
        implements ActionListener, ItemListener
{

  DataCache cache;
  int idper;
  TodoTableModel afaire;
  JTable tableAFaire;
  GemChoice categorie;
  GemButton modifier;
  GemButton creer;
  GemButton supprimer;

  public TodoListCtrl(DataCache _dc, int _idper) {

    cache = _dc;
    idper = _idper;

    setLayout(new BorderLayout());

    afaire = new TodoTableModel();
    tableAFaire = new JTable(afaire);
    tableAFaire.setAutoCreateRowSorter(true);

    TableColumnModel cm = tableAFaire.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);
    cm.getColumn(1).setPreferredWidth(250);
    cm.getColumn(2).setPreferredWidth(50);
    cm.getColumn(3).setPreferredWidth(30);

    JScrollPane pm = new JScrollPane(tableAFaire);

    GemPanel haut = new GemPanel();
    haut.add(new GemLabel("Catégorie"));
    //categorie = new GemChoice(cache.getDataConnection(),"Select id,nom from categorieafaire order by nom");
    categorie = new CategoryOccupChoice(cache.getOccupationalCat());
    categorie.addItemListener(this);
    haut.add(categorie);

    modifier = new GemButton("Modification");
    modifier.addActionListener(this);
    creer = new GemButton("Creation");
    creer.addActionListener(this);
    supprimer = new GemButton("Suppression");
    supprimer.addActionListener(this);

    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 3));
    boutons.add(modifier);
    boutons.add(creer);
    boutons.add(supprimer);

    add("North", haut);
    add("Center", pm);
    add("South", boutons);
  }

  public TodoListCtrl(DataCache _dc) {
    this(_dc, _dc.getUser().getId());
  }

  public void itemStateChanged(ItemEvent evt) {
    if (evt.getSource() == categorie) {
      int cat = categorie.getSelectedIndex();
      load(cat);
    }
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == modifier) {
    } else if (evt.getSource() == creer) {
    } else if (evt.getSource() == supprimer) {
    }
  }

  public void addActionListener(ActionListener l) {
    //retour.addActionListener(l);
  }

  public void addBlock(Vector block) {
    try {
      Enumeration e = block.elements();
      while (e.hasMoreElements()) {
        addRow(e.nextElement());
      }
    } catch (Exception e) {
      //GemLogger.logException("listeCtrl:",e);
      return;
    }
  }

  public void load(int idcat) {
    afaire.clear();
    Vector liste = TodoIO.find("where idper=" + idper + " and categorie=" + idcat, DataCache.getDataConnection());
    addBlock(liste);
  }

  public int nblignes() {
    return afaire.getRowCount();
  }

  public void clear() {
    afaire.clear();
  }

  public void addRow(Object _ligne) {
    Todo a = (Todo) _ligne;
    afaire.addItem(a);
  }
}
