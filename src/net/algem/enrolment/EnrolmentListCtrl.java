/*
 * @(#)EnrolmentListCtrl.java 2.9.1 12/12/14
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
package net.algem.enrolment;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.PersonIO;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.security.Profile;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 1.0a 07/07/1999
 */
public class EnrolmentListCtrl
        extends GemPanel
        implements ActionListener
{

  private DataCache dataCache;
  private GemDesktop desktop;
  private OrderTableModel orderTableModel;
  private JTable enrolmentTable;
  private ActionListener actionListener;
  private EnrolmentService service;

  public EnrolmentListCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    service = new EnrolmentService(dataCache);

    orderTableModel = new OrderTableModel();

    enrolmentTable = new JTable(orderTableModel);
    enrolmentTable.setAutoCreateRowSorter(true);
    enrolmentTable.getTableHeader().setToolTipText(MessageUtil.getMessage("click.column.to.sort.tip"));

    TableColumnModel cm = enrolmentTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(70);
    cm.getColumn(2).setPreferredWidth(40);
    cm.getColumn(3).setPreferredWidth(150);
    cm.getColumn(4).setPreferredWidth(100);
    cm.getColumn(5).setPreferredWidth(50);

    JScrollPane pm = new JScrollPane(enrolmentTable);

    GemButton btModidy = new GemButton(BundleUtil.getLabel("View.modify.label"));
    GemButton btDelete = new GemButton(GemCommand.DELETE_CMD);
    GemButton btClose = new GemButton(GemCommand.CLOSE_CMD);

    btModidy.addActionListener(this);
    btClose.addActionListener(this);

    if (Profile.ADMIN.getId() == dataCache.getUser().getProfile()) {
      btDelete.addActionListener(this);
    } else {
      btDelete.setEnabled(false);
    }

    GemPanel buttons = new GemPanel(new GridLayout(1, 3));
    buttons.add(btModidy);
    buttons.add(btDelete);
    buttons.add(btClose);

    setLayout(new BorderLayout());
    add(pm, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    load();
  }

  public void load() {
    List<MemberOrder> v = service.getOrders();
    for (int i = 0; i < v.size(); i++) {
      orderTableModel.addItem(v.get(i));
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
    int row = enrolmentTable.getSelectedRow();
    if (row < 0) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
      return;
    }
    int n = enrolmentTable.convertRowIndexToModel(row);
    if (n < 0) {
      return;
    }

    String cmd = evt.getActionCommand();
    if (cmd.equals(BundleUtil.getLabel("View.modify.label"))) {
      try {
        modification(n);
      } catch (SQLException e) {
        GemLogger.logException("modification instrument", e, this);
      }
    } else if (cmd.equals(GemCommand.DELETE_CMD)) {
      try {
        suppression(n);
        clear();
      } catch (SQLException e) {
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

    MemberOrder mo = (MemberOrder) orderTableModel.getItem(n);
    PersonFileEditor editor = desktop.getModuleFileEditor(mo.getMember());
    if (editor != null) {
      desktop.setSelectedModule(editor);
    } else {
      PersonFile dossier = service.getMemberFile(mo.getMember());
      if (dossier == null) {
        MessagePopup.warning(this, MessageUtil.getMessage("load.fiche.error", dossier.getId()));
        GemLogger.log("Error PersonFileSearchCtrl.createModule ID NOT FOUND:" + dossier.getId());
      } else {
        editor = new PersonFileEditor(dossier);
        desktop.addModule(editor);
        editor.getPersonView().setSelectedTab(2);//supposed always to be index 2
      }
    }

    setCursor(Cursor.getDefaultCursor());
  }

  private void insertion(int n) throws SQLException {
    //inscriptions.addItem(v);
  }

  private void suppression(int n) throws SQLException {
    try {
      MemberOrder cmd = (MemberOrder) orderTableModel.getItem(n);
      Person p = ((PersonIO) DataCache.getDao(Model.Person)).findById(cmd.getMember());
      if (!MessagePopup.confirm(this,
              MessageUtil.getMessage("enrolment.delete.confirmation", new Object[]{cmd.getId(), p.getFirstnameName()}),
              "Suppression inscription")) {
        return;
      }
      service.delete(cmd);
      orderTableModel.deleteItem(n);
      desktop.postEvent(new EnrolmentEvent(this, GemEvent.SUPPRESSION, cmd.getMember()));
      desktop.postEvent(new ModifPlanEvent(this, cmd.getCreation(), dataCache.getEndOfYear()));//XXX dlg.getStart/Fin
    } catch (Exception e) {
      GemLogger.logException("Insertion inscription", e, this);
    }
  }

  private void clear() {
//		liste.clear();
  }
}
