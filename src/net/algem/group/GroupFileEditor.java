/*
 * @(#)GroupFileEditor.java 2.9.4.8 23/06/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.*;
import net.algem.accounting.GroupOrderLineEditor;
import net.algem.accounting.OrderLineTableModel;
import net.algem.contact.Contact;
import net.algem.contact.NoteException;
import net.algem.contact.WebSite;
import net.algem.planning.RehearsalEvent;
import net.algem.planning.Schedule;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Group file main editor.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.8
 */
public class GroupFileEditor
        extends GemModule
{

  private static final String GROUP_DOSSIER_KEY = "ModuleGroupe";
  private Group group, oldGroup;
  private Vector<Musician> musicians;
  private GroupFileView groupFileTabView;
  private GroupPassCreateCtrl passCreateCtrl;
  private GroupRehearsalCreateCtrl rehearsalCtrl;
  private GemToolBar mainToolbar;
  private GemToolBar closeToolbar;
  private GemButton btSave;
  private GemButton btClose;
  private GemButton btNote;
  private GemButton btSchedulePayment;
  private GemButton btRehearsal;
  private JMenuBar mBar;
  private JMenu mFile, mOptions;
  private JMenuItem miSuppression, miRehearsal, miPass;
  private JCheckBoxMenuItem miMemberPayments;
  private GemGroupService service;
  private Schedule plan;

  public GroupFileEditor() {
    super(BundleUtil.getLabel("New.group.label"));
  }

  public GroupFileEditor(Group g, Schedule plan) {
    this(g);
    this.plan = plan;
  }

  public GroupFileEditor(Group g) {
    super(GROUP_DOSSIER_KEY);
    this.group = g;
    if (g != null) {
      label += " " + g.getName();
    }
  }

  /**
   * Gets the id of the group.
   * @return the string value of the id
   */
  @Override
  public String getSID() {
    return String.valueOf(group.getId());
  }

  /**
   * Gets the id of the group.
   * @return the integer value of the id
   */
  public int getId() {
    return group.getId();
  }

  @Override
  public void init() {
    service = new GemGroupService(DataCache.getDataConnection());
    try {
      Contact ref = service.getContact(group.getIdref());
      Contact man = service.getContact(group.getIdman());
      Contact tour = service.getContact(group.getIdbook());
      group.setContact(ref, man, tour);
      group.setNote(service.getNote(group.getId()));
      Vector<WebSite> sites = service.getSites(group.getId());
      group.setSites(sites);
      oldGroup = group;
      view = groupFileTabView = new GroupFileView(desktop, service, group);
      view.addActionListener(this);
      Vector<Musician> vm = service.getMusicians(group);
      oldGroup.setMusicians(vm);
      groupFileTabView.init(vm);
    } catch(NoteException ne) {
      GemLogger.log(getClass().getName(), "init", ne);
    } catch (SQLException ex) {// NoteException ex) {
      GemLogger.log(getClass().getName(), "init", ex);
    } 

    mBar = new JMenuBar();
    mFile = new JMenu(BundleUtil.getLabel("Menu.file.label"));
    miSuppression = getMenuItem("Action.suppress");

    mFile.add(miSuppression);

    mOptions = new JMenu(BundleUtil.getLabel("Menu.options.label"));
    miPass = getMenuItem("Rehearsal.pass");
    miMemberPayments = new JCheckBoxMenuItem(BundleUtil.getLabel("Action.members.schedule.payment.label"));
    miMemberPayments.addActionListener(this);
    mOptions.add(miPass);
    mOptions.add(miMemberPayments);

    mBar.add(mFile);
    mBar.add(mOptions);

    view.setJMenuBar(mBar);

    mainToolbar = new GemToolBar(false);

    btNote = mainToolbar.addIcon(BundleUtil.getLabel("Member.note.icon"), "Note", BundleUtil.getLabel("Group.note.tip"));
    btNote.addActionListener(this);
    btSchedulePayment = mainToolbar.addIcon(
            BundleUtil.getLabel("Member.schedule.payment.icon"),
            "Member.schedule.payment",
            BundleUtil.getLabel("Member.schedule.payment.tip"));
    btSchedulePayment.addActionListener(this);
    btRehearsal = mainToolbar.addIcon(BundleUtil.getLabel("Rehearsal.icon"), "Rehearsal", BundleUtil.getLabel("Rehearsal.tip"));
    btRehearsal.addActionListener(this);

    closeToolbar = new GemToolBar(false);

    GemBorderPanel toolbar = new GemBorderPanel();
    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
    toolbar.add(mainToolbar);
    JPanel right = new JPanel(new BorderLayout());
    right.add(Box.createHorizontalGlue(), BorderLayout.WEST);
    right.add(closeToolbar, BorderLayout.EAST);
    toolbar.add(right);

    btSave = closeToolbar.addIcon(
            BundleUtil.getLabel("Contact.save.icon"),
            GemCommand.SAVE_CMD,
            BundleUtil.getLabel("Save.tip"));
    btClose = closeToolbar.addIcon(
            BundleUtil.getLabel("Contact.close.icon"),
            GemCommand.CLOSE_CMD,
            BundleUtil.getLabel("Close.tip"));

    btSave.addActionListener(this);
    btClose.addActionListener(this);
    
    groupFileTabView.add(toolbar, BorderLayout.NORTH);

    loadPaymentSchedule();
    groupFileTabView.setSelectedTab(0);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Object src = evt.getSource();

    // On sauve au préalable l'éventuel nouveau groupe avant d'executer les actions des menus.
    if (group.getId() == 0 && !arg.equals(GemCommand.SAVE_CMD) && !arg.equals(GemCommand.CLOSE_CMD)) {
      // TODO
    } else if (src == miPass) {
      passCreateCtrl = new GroupPassCreateCtrl(desktop, group);
      passCreateCtrl.addActionListener(this);
      desktop.addPanel("Group.pass", passCreateCtrl);
      desktop.getSelectedModule().setSize(GemModule.M_SIZE);
    } else if ("Rehearsal".equals(arg)) {
      rehearsalCtrl = new GroupRehearsalCreateCtrl(desktop, group);
      rehearsalCtrl.addActionListener(this);
      desktop.addPanel("Group.rehearsal", rehearsalCtrl, GemModule.S_SIZE);
      rehearsalCtrl.init();
      rehearsalCtrl.addGemEventListener(this);
    } else if (GemCommand.NOTE_CMD.equals(arg)) {
      GroupNoteDlg nd = new GroupNoteDlg(desktop, service);
      nd.loadNote(group);
      nd.show();
    } else if (GemCommand.CLOSE_CMD.equals(arg)) {
      try {
        if (hasChanged()) {
          if (MessagePopup.confirm(groupFileTabView,
                  MessageUtil.getMessage("band.update.confirmation", group.getName()),
                  MessageUtil.getMessage("closing.label"))) {
            save();
          }
        }
        super.close();
      } catch (GemCloseVetoException i) {
      }
    } else if ("Member.schedule.payment".equals(arg)) {
        loadPaymentSchedule();
    } else if (src == miMemberPayments) {
      groupFileTabView.remove(GroupOrderLineEditor.class);
      closeTab(GroupOrderLineEditor.class);
      loadPaymentSchedule();
    }
    else if (CloseableTab.CLOSE_CMD.equals(arg)) {
      closeTab(src);
    } // clic sur le bouton Enregistrer
    else if (GemCommand.SAVE_CMD.equals(arg)) {
      if (hasChanged()) {
        save();
      }
    } else if ("Action.suppress".equals(arg)) {
      deleteGroup();
    }
  }

  private void loadPaymentSchedule() {
    OrderLineTableModel tableModel = new OrderLineTableModel();
    if (miMemberPayments.isSelected()) {
      tableModel.load(service.getMemberSchedulePayment(group));
    } else {
      tableModel.load(service.getSchedulePayment(group));
    }
    GroupOrderLineEditor orderLineEditor = new GroupOrderLineEditor(desktop, tableModel, service);
    orderLineEditor.init();

    groupFileTabView.addTab(orderLineEditor, BundleUtil.getLabel("Person.schedule.payment.tab.label"));
    btSchedulePayment.setEnabled(false);
  }

  private void closeTab(Object source) {
    String classname = null;
    if (source instanceof String) {
      classname = (String) source;
    } else {
      return;
    }

    if (classname.equals(GroupOrderLineEditor.class.getSimpleName())) {
       btSchedulePayment.setEnabled(true);
    }
  }

  /**
   * Closing icon management.
   * Overrides
   * <code>net.algem.module.GemModule</code>
   */
  @Override
  public void close() {
    if (hasChanged()) {
      if (MessagePopup.confirm(groupFileTabView,
              MessageUtil.getMessage("band.update.confirmation", group.getName()),
              MessageUtil.getMessage("closing.label"))) {
        save();
      }
    }
    try {
      view.close();
    } catch (GemCloseVetoException ex) {
    }

    desktop.removeGemEventListener(this);
    desktop.removeModule(this);
  }

  private void create() throws SQLException {
    service.create(group);
    if (musicians != null) {
      for (Musician m : musicians) {
        service.create(group.getId(), m);
      }
    }
    oldGroup = group;
    groupFileTabView.refreshId(group.getId());
  }

  /**
   * Updates group.
   */
  private void update() throws GroupException, SQLException {

    service.update(oldGroup, group);
    if (musicians != null) {
      service.update(group.getId(), musicians, oldGroup.getMusicians());
      group.setMusicians(musicians);
    }
    oldGroup = group;
  }

  /**
   * Saves the group.
   * Only if modification. Idem for musicians.
   */
  private void save() {

    if (service == null) {
      service = new GemGroupService(DataCache.getDataConnection());
    }
    try {
      if (group.getId() == 0) {
        create();
        dataCache.add(group);
        desktop.postEvent(new GroupCreateEvent(this, group));
      } else {
        update();
        dataCache.update(group);
        desktop.postEvent(new GroupUpdateEvent(this, group));
        if (plan != null) {
          desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
        }
      }

    } catch (GroupException ex) {
      MessagePopup.error(view, ex.getMessage());
      GemLogger.logException(ex);
    } catch (SQLException sqe) {
      MessagePopup.error(view, sqe.getMessage());
      GemLogger.logException(sqe);
    }

  }

  /**
   * Checks if group has been modified.
   *
   * @return true if modified
   */
  private boolean hasChanged() {
    group = groupFileTabView.getGroup();
    musicians = groupFileTabView.getMusicians();

    return !oldGroup.equiv(group)
            || musicians != null
            || !Contact.sitesEqual(group.getSites(), oldGroup.getSites());
  }

  /**
   * Suppress a group.
   * A group may be deleted only if there is no rehearsal for this group in planning.
   */
  private void deleteGroup() {
    if (service == null) {
      service = new GemGroupService(DataCache.getDataConnection());
    }
    try {
      if (dataCache.authorize("Group.suppression.auth")) {
        service.delete(group);
        dataCache.remove(group);
        desktop.postEvent(new GroupDeleteEvent(this, group));
        super.close();
      } else {
        MessagePopup.warning(view, "Suppression non autorisée");
      }
    } catch (GroupException ex) {
      MessagePopup.warning(view, MessageUtil.getMessage("band.delete.info", ex.getMessage()));
    } catch (GemCloseVetoException i) {
      GemLogger.logException(i);
    }
  }
  
  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof RehearsalEvent) {
      groupFileTabView.remove(GroupOrderLineEditor.class);
      closeTab(GroupOrderLineEditor.class);
      loadPaymentSchedule();
    }
  }
}
