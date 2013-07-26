/*
 * @(#)MusicianListView.java	2.8.k 26/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.group;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.*;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Musicians list view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 1.0a 18/02/2004
 */
public class MusicianListView
        extends GemPanel
        implements ActionListener
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private int id;
  private MusicianTableModel musicians;
  private JTable table;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDel;
  private GemButton btMail;

  public MusicianListView(GemDesktop _desktop) {

    setLayout(new BorderLayout());
    desktop = _desktop;
    dataCache = desktop.getDataCache();

    musicians = new MusicianTableModel(dataCache);
    table = new JTable(musicians)
    {

      public void processMouseEvent(MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_CLICKED && evt.getClickCount() > 1) {
          viewPerson();
        } else {
          super.processMouseEvent(evt);
        }
      }
    };
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(120);
    cm.getColumn(2).setPreferredWidth(120);
    cm.getColumn(3).setPreferredWidth(120);

    JScrollPane pm = new JScrollPane(table);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 4));
    buttons.add(new GemLabel(BundleUtil.getLabel("Group.members.label")));

    buttons.add(btMail = new GemButton(BundleUtil.getLabel("Action.mail.label")));
    buttons.add(btDel = new GemButton(GemCommand.REMOVE_CMD));
    buttons.add(btModify = new GemButton(GemCommand.MODIFY_CMD));
    buttons.add(btAdd = new GemButton(GemCommand.ADD_CMD));
    btMail.addActionListener(this);
    btMail.setToolTipText(MessageUtil.getMessage("group.mailing.tip"));
    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btDel.addActionListener(this);

    GemBorderPanel pb1 = new GemBorderPanel();
    pb1.setLayout(new BorderLayout());
    pb1.add(buttons, BorderLayout.NORTH);
    pb1.add(pm, BorderLayout.CENTER);

    this.setLayout(new BorderLayout());
    add(pb1, BorderLayout.CENTER);

  }

  public void viewPerson() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return;
    }
    int n = table.convertRowIndexToModel(row);
    if (n < 0) {
      return;
    }

    Musician m = (Musician) musicians.getItem(n);
    // il est nécessaire de récupérer les adresses, tel et email éventuels du contact
    Contact c = ContactIO.findId(m.getId(), dataCache.getDataConnection());
    PersonFile pf = new PersonFile(c);
    try {
      ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(pf);
    } catch (SQLException ex) {
      GemLogger.logException("complete dossier musicien liste", ex);
    }
    PersonFileEditor editor = new PersonFileEditor(pf);
    desktop.addModule(editor);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (GemCommand.MAIL_CMD.equals(cmd)) {
      Vector<Musician> vm = musicians.getData();
      MailUtil mailUtil = new MailUtil(dataCache);
      String message = mailUtil.mailToGroupMembers(vm);
      if (message.length() > 0) {
        String info = MessageUtil.getMessage("group.members.without.email");
        new MessageDialog(desktop.getFrame(), BundleUtil.getLabel("Information.label"), false, info, message);
      }
    } else if (GemCommand.ADD_CMD.equals(cmd)) {
      MusicianDlg dlg = new MusicianDlg(this, "ajout musicien", desktop);
      dlg.setOperation(GemEvent.CREATION);
      dlg.show();
    } else {
        int n = getSelectedRow();
        if (n < 0) {
          return;
        }

        if (GemCommand.MODIFY_CMD.equals(cmd)) {
          Musician m = (Musician) musicians.getItem(n);
          MusicianDlg dlg = new MusicianDlg(this, "modif musicien", desktop);
          dlg.setPerson(m);
          dlg.setOperation(GemEvent.MODIFICATION);
          dlg.show();
        } else if (GemCommand.REMOVE_CMD.equals(cmd)) {
          musicians.deleteItem(n);
        }
    }
  }

  void setMusician(Musician m, int operation) {
    if (operation == GemEvent.CREATION) {
      musicians.addItem(m);
    } else if (operation == GemEvent.MODIFICATION) {
      int n = getSelectedRow();
      if (n < 0) {
        return;
      }
      musicians.modItem(n, m);
    } 

  }

  private int getSelectedRow() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return -1;
    }
    return table.convertRowIndexToModel(row); 
  }
  
  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
    //no.setText(String.valueOf(i));
  }

  public Vector<Musician> get() {
    return musicians.getData();
  }

  void addRow(Musician p) {
    musicians.addItem(p);
  }

  void deleteCurrent() {
    musicians.deleteItem(table.getSelectedRow());
  }

  void clear() {
    musicians.clear();
  }
}
