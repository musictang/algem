/*
 * @(#)NoteDlg.java	2.15.3 10/10/17
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.3
 */
public class NoteDlg
        implements ActionListener
{

  protected JDialog dlg;
  protected DataConnection dc;
  protected int idnote;
  protected int idper;
  protected short ptype;
  protected Note note;
  protected Person person;
  protected GemBorderPanel body;
  protected GemLabel title;
  protected GemTextArea text;
  protected GemButton btValidation;
  protected GemButton btCancel;
  protected ActionListener listener;

  public NoteDlg(Frame f) {
    dlg = new JDialog(f, BundleUtil.getLabel("Note.label"),true);
    setDisplay();
  }

  private void setDisplay() {
    btValidation = new GemButton(GemCommand.VALIDATE_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 2));
    boutons.add(btValidation);
    boutons.add(btCancel);

    body = new GemBorderPanel();
    body.setLayout(new BorderLayout());
    title = new GemLabel();

    text = new GemTextArea();
    text.setRows(5);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    DefaultCaret caret = (DefaultCaret)text.getCaret();
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    JScrollPane jspane = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    body.add(jspane, BorderLayout.CENTER);

    Container c = dlg.getContentPane();
    c.setLayout(new BorderLayout());
    c.add(title, BorderLayout.NORTH);
    c.add(body, BorderLayout.CENTER);
    c.add(boutons, BorderLayout.SOUTH);
    dlg.setPreferredSize(new Dimension(250,160));
    dlg.pack();
    dlg.setLocation(100, 100);
  }

  public NoteDlg(Component c, DataConnection dc) {
    this.dc = dc;
    Frame parent = PopupDlg.getTopFrame(c);
    if (parent == null) {
      return;
    }
    dlg = new JDialog(parent, true);
    setDisplay();
  }

  public NoteDlg(GemDesktop desktop) {
    this(desktop.getFrame());
    this.dc = DataCache.getDataConnection();
  }

  public void show() {
    dlg.setVisible(true);
  }

  public void loadNote(Note n, Person p) {

    if (p != null) {
      this.idper = p.getId();
      this.ptype = p.getType();
    }
    note = n;
    if (note != null) {
      title.setText("Modification note " + note.getId() + " (fiche : " + note.getIdPer() + ")");
      text.setText(note.getText());
    } else {
      title.setText("Création note fiche:" + idper);
    }
  }

  public boolean save() {
    try {
      String s = text.getText().trim();
      if (note == null) {
        //pas d'insertion si text null
        if (idper > 0 && s.length() > 0) {
          note = new Note(idper, text.getText(), ptype);
          NoteIO.insert(note, dc);
          //personne.setNote(note.getId());
          //cache.getPersonIO().update(person);
          if (listener != null) {
            listener.actionPerformed(new ActionEvent(note, ActionEvent.ACTION_PERFORMED, "NouvelleNote"));
          }
        }
      } else {
        if (!s.equals(note.getText())) {
          note.setText(s);
          NoteIO.update(note, dc);
        }
      }
    } catch (Exception e) {
      GemLogger.logException("enregistrement note", e, body);
      return false;
    }
    return true;

  }

  public Note getNote() {
    return note;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.VALIDATE_CMD)) {
      if (!save()) {
        return;
      }
    }
    dlg.setVisible(false);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
