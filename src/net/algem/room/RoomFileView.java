/*
 * @(#)RoomFileView.java 2.9.6 18/03/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.UIManager;
import net.algem.accounting.OrderLineEditor;
import net.algem.accounting.OrderLineTableModel;
import net.algem.contact.*;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.module.FileTabView;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.TabPanel;

/**
 * Tab container for room file.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.6
 * @since 2.1.j
 */
public class RoomFileView
        extends FileTabView
{

  private static final String ROOM_VIEW_KEY = "Room";
  private Room room;
  private RoomEditor editor;
  private RoomEquipEditor equipEditor;
  private ContactFileEditor contactEditor;
  private OrderLineEditor orderLineEditor;
  private static final String[] TABS = {
    BundleUtil.getLabel("Room.tab.label"),
    BundleUtil.getLabel("Room.contact.tab.label"),
    BundleUtil.getLabel("Room.equipment.tab.label")
  };
  private Note note;
  private boolean creation;
  private RoomService service;

  public RoomFileView(GemDesktop desktop, Room room, RoomService service) {
    super(desktop, ROOM_VIEW_KEY);
    this.service = service;

    if (room != null && room.getId() > 0) {
      setTitle(BundleUtil.getLabel("Room.label")+ " " + room.getName() + " " + room.getId());
    } else {
      setTitle("");
    }
    this.room = room;
  }

  public void init() {

    setLayout(new BorderLayout());
    // modification des espacements par défaut de la partie haute de l'onglet.
    UIManager.put("TabbedPane.tabInsets", TabPanel.DEFAULT_INSETS);

    wTab = new TabPanel();
    editor = new RoomEditor(desktop, room);
    equipEditor = new RoomEquipEditor(desktop);
    setEquipment(room.getEquipment());
    wTab.addItem(editor, TABS[0]);
    try {
      note = service.getNote(room);
    } catch (NoteException ex) {
      GemLogger.logException(ex);
    }
    setNote(note);

    if (room.getId() > 0) {
      service.fillContact(room.getContact());
      initContact();
    } else {
      creation = true;
    }
    wTab.setSelectedIndex(0);
    add(wTab, BorderLayout.CENTER);

    setSize(GemModule.XXL_SIZE);
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note n) {
    if (n != null) {
      note = n;
      editor.setNote(note);
    }
  }

  public Room getRoom() {
    Room r = editor.getRoom();
    if (!creation || contactEditor != null) {
      r.setContact(contactEditor.getContact());
      r.setEquipment(equipEditor.getData());
      if (r.getPayer() == null) {
        r.setPayer(new Person(r.getContact().getId()));
      }
    }
    return r;
  }

  public void completeTabs(Room r) {
    setRoom(r);
    initContact();
    wTab.setSelectedIndex(1);
  }

  @Override
  public void addActionListener(ActionListener l) {
    listener = l;
  }

  public void addOrderLine(OrderLineTableModel t) {

    assert room.getContact() != null;

    orderLineEditor = new OrderLineEditor(desktop, t);
    orderLineEditor.init();
    orderLineEditor.setMemberId(room.getContact().getId());
    int payerId = room.getPayer() != null ? room.getPayer().getId() : 0;
    orderLineEditor.setPayerId(payerId);
    String label = String.valueOf(payerId);
    if (room.getPayer() != null) {
      if (room.getPayer().getNameFirstname() != null && room.getPayer().getNameFirstname().trim().length() > 0) {
        label = room.getPayer().getNameFirstname();
      } else if (room.getPayer().getOrgName() != null && room.getPayer().getOrgName().length() > 0) {
        label = room.getPayer().getOrgName();
      }
    }
    orderLineEditor.setLabel(label);
    orderLineEditor.addActionListener(listener);
    wTab.addItem(orderLineEditor, BundleUtil.getLabel("Person.schedule.payment.tab.label"));
    wTab.setSelectedComponent(orderLineEditor);
    wTab.addCloseButton(wTab.getSelectedIndex(), listener);
  }

  public OrderLineEditor getOrderLineEditor() {
    return orderLineEditor;
  }

  private void setContact() {
    if (room != null && room.getContact() != null) {
      contactEditor.set(room.getContact());
    }
  }

  void setContact(Contact c) {
    if (room != null && c != null) {
      room.setContact(c);
      if (contactEditor == null) {
        loadContact();
      } else {
        contactEditor.set(c);
      }
    }
  }

  private void initContact() {
    if (contactEditor == null) {
      loadContact();
    }
    creation = false;
  }
  
  private void loadContact() {
    contactEditor = new ContactFileEditor(desktop);
    contactEditor.setCodePostalCtrl(new CodePostalCtrl(DataCache.getDataConnection()));
    setContact();
    wTab.addItem(contactEditor, TABS[1]);
    wTab.addItem(equipEditor, TABS[2]);
  }

  private void setRoom(Room r) {
    this.room = r;
    editor.set(r);
  }

  private void setEquipment(Vector<Equipment> ve) {
    equipEditor.load(ve);
  }
}
