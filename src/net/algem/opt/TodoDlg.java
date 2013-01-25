/*
 * @(#)TodoDlg.java	2.6.a 25/09/12
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

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import net.algem.contact.Note;
import net.algem.contact.NoteIO;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.config.CategoryOccupChoice;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class TodoDlg
        implements ActionListener
{

  Frame parent;
  JDialog dlg;
  DataCache cache;
  int idafaire;
  int idper;
  Todo afaire;
  Person personne;
  GemBorderPanel fond;
  GemLabel titre;
  GemChoice categorie;
  JComboBox priorite;
  GemField texte;
  DateFrField echeance;
  GemTextArea note;
  GemButton valider;
  GemButton abandon;

  public TodoDlg(Component c, DataCache _dc, int _idper, int _idafaire) {
    cache = _dc;
    idper = _idper;
    idafaire = _idafaire;

    parent = PopupDlg.getTopFrame(c);
    if (parent == null) {
      return;
    }

    dlg = new JDialog(parent, true);

    valider = new GemButton("OK");
    valider.addActionListener(this);
    abandon = new GemButton(GemCommand.CANCEL_CMD);
    abandon.addActionListener(this);

    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 2));
    boutons.add(abandon);
    boutons.add(valider);

    titre = new GemLabel("A Faire pour:" + _idper);

    fond = new GemBorderPanel();
    fond.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(fond);

    //categorie = new GemChoice(cache.getDataConnection(),"Select id,nom from categorieafaire order by nom");
    categorie = new CategoryOccupChoice(cache.getOccupationalCat());
    priorite = new JComboBox();
    priorite.addItem("urgent");
    priorite.addItem("des que possible");
    priorite.addItem("a écheance");
    priorite.addItem("plus tard");
    texte = new GemField(50);
    echeance = new DateFrField(new DateFr());
    note = new GemTextArea();

    gb.add(new GemLabel("Catégorie"), 0, 0, 1, 1, gb.EAST);
    gb.add(new GemLabel("Priorité"), 0, 1, 1, 1, gb.EAST);
    gb.add(new GemLabel("Texte"), 0, 2, 1, 1, gb.EAST);
    gb.add(new GemLabel("Echéance"), 0, 3, 1, 1, gb.EAST);
    gb.add(new GemLabel("Note"), 0, 4, 1, 1, gb.EAST);

    gb.add(categorie, 1, 0, 1, 1, gb.WEST);
    gb.add(priorite, 1, 1, 1, 1, gb.WEST);
    gb.add(texte, 1, 2, 1, 1, gb.WEST);
    gb.add(echeance, 1, 3, 1, 1, gb.WEST);
    gb.add(note, 0, 5, 2, 1, gb.WEST);

    dlg.getContentPane().add("North", titre);
    dlg.getContentPane().add("Center", fond);
    dlg.getContentPane().add("South", boutons);

    dlg.pack();
    dlg.setLocation(100, 100);

    dlg.setVisible(true);
  }

  public TodoDlg(Component c, DataCache dc, int _idper) {
    this(c, dc, _idper, 0);
  }

  public boolean enregistre() {
    try {
      if (idafaire == 0) {
        afaire = new Todo(idper, texte.getText());
        afaire.setPriorite(priorite.getSelectedIndex());
        afaire.setCategorie(categorie.getSelectedIndex());
        afaire.setEcheance(echeance.get());
        String n = note.getText().trim();
        if (n.length() > 0) {
          Note note = new Note(n);
          NoteIO.insert(note, cache.getDataConnection());
          afaire.setNote(note.getId());
        }
        TodoIO.insert(afaire, cache.getDataConnection());
      } else {
        /*
         * String s = texte.getText(); { afaire.setText(s);
         * TodoIO.update(afaire, cache); }
         */
      }
    } catch (SQLException e) {
      GemLogger.logException("enregistrement afaire", e, fond);
      return false;
    }

    return true;

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals("OK")) {
      if (!enregistre()) {
        return;
      }
    }
    //dlg.setVisible(false);
    dlg.dispose();
  }
}
