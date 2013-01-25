/*
 * @(#)EnrolmentListCtrl.java 2.7.a 26/11/12
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
package net.algem.enrolment;

import java.awt.AWTEventMulticaster;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.PersonIO;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.security.UserIO;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class EnrolmentListCtrl
        extends GemPanel
        implements ActionListener
{

  private DataCache dataCache;
  private GemDesktop desktop;
  private GemButton btModidy;
  private GemButton btDelete;
  private GemButton btClose;
  private OrderTableModel enrolmentTableModel;
  private JTable enrolmentTable;
  private ActionListener actionListener;
  private EnrolmentService service;

  public EnrolmentListCtrl(GemDesktop _desktop) {
    desktop = _desktop;
    dataCache = desktop.getDataCache();
    service = new EnrolmentService(dataCache);

    enrolmentTableModel = new OrderTableModel();

    enrolmentTable = new JTable(enrolmentTableModel);
    enrolmentTable.setAutoCreateRowSorter(true);
    enrolmentTable.getTableHeader().setToolTipText("Cliquez sur une colonne pour trier)");

    TableColumnModel cm = enrolmentTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(70);
    cm.getColumn(2).setPreferredWidth(40);
    cm.getColumn(3).setPreferredWidth(150);
    cm.getColumn(4).setPreferredWidth(100);
    cm.getColumn(5).setPreferredWidth(50);

    JScrollPane pm = new JScrollPane(enrolmentTable);

    btModidy = new GemButton(BundleUtil.getLabel("View.modify.label"));
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btClose = new GemButton(GemCommand.CLOSE_CMD);

    btModidy.addActionListener(this);
    btClose.addActionListener(this);

    if (dataCache.getUser().getProfile() == UserIO.PROFIL_ADMIN) {
      btDelete.addActionListener(this);
    } else {
      btDelete.setEnabled(false);
    }

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 3, 2, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(btModidy, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(btDelete, 1, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(btClose, 2, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    load();
  }

  public void load() {
    Vector<MemberOrder> v = service.getOrders();
    for (int i = 0; i < v.size(); i++) {
      enrolmentTableModel.addItem(v.elementAt(i));
    }
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.CLOSE_CMD)) {
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
      }
      return;
    }
    int n = enrolmentTable.convertRowIndexToModel(enrolmentTable.getSelectedRow());
    if (n < 0) {
      return;
    }

    String cmd = evt.getActionCommand();
    if (cmd.equals(BundleUtil.getLabel("View.modify.label"))) {
      try {
        modification(n);
      } catch (Exception e) {
        GemLogger.logException("modification instrument", e, this);
      }
    } else if (cmd.equals(GemCommand.DELETE_CMD)) {
      try {
        suppression(n);
        clear();
      } catch (Exception e) {
        GemLogger.logException("suppresion instrument", e, this);
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  private void modification(int n) throws SQLException {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    MemberOrder mo = (MemberOrder) enrolmentTableModel.getItem(n);
    PersonFileEditor editor = ((GemDesktopCtrl) desktop).getPersonFileEditor(mo.getMember());
    if (editor != null) {
      desktop.setSelectedModule(editor);
    } else {
      PersonFile dossier = service.getMemberFile(mo.getMember());
      editor = new PersonFileEditor(dossier);
      desktop.addModule(editor);
    }

    setCursor(Cursor.getDefaultCursor());
  }

  private void insertion(int n) throws SQLException {
    //inscriptions.addItem(v);
  }

  private void suppression(int n) throws SQLException {
    try {
      MemberOrder cmd = (MemberOrder) enrolmentTableModel.getItem(n);
//      PersonFile adh = DataCache.getPersonFileIO().findId(cmd.getMember());
      Person p = ((PersonIO) DataCache.getDao(Model.Person)).findId(cmd.getMember());
      if (!MessagePopup.confirm(this,
              MessageUtil.getMessage("enrolment.delete.confirmation", new Object[]{cmd.getId(), p.getFirstnameName()}),
              "Suppression inscription")) {
        return;
      }
      service.delete(cmd);
      enrolmentTableModel.deleteItem(n);
      desktop.postEvent(new EnrolmentDeleteEvent(this, cmd));
      desktop.postEvent(new ModifPlanEvent(this, cmd.getCreation(), dataCache.getEndOfYear()));//XXX dlg.getStart/Fin
    } catch (Exception e) {
      GemLogger.logException("Insertion inscription", e, this);
    }
  }

  private void clear() {
//		liste.clear();
  }
}
