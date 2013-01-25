/*
 * @(#)RoomFileView.java 2.6.a 24/09/12
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
package net.algem.room;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.UIManager;
import net.algem.accounting.OrderLineEditor;
import net.algem.accounting.OrderLineTableModel;
import net.algem.contact.*;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.module.FileView;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.TabPanel;

/**
 * Tab container for room file.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.j
 */
public class RoomFileView
        extends FileView
{

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

  public RoomFileView(GemDesktop _desktop, Room room, RoomService service) {
    super(_desktop, GemModule.ROOM_VIEW_KEY);
    this.service = service;

    if (room != null && room.getId() > 0) {
      setTitle(room.getName() + " " + room.getId());
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
      service.fillContact(room);
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
    Room s = editor.getRoom();
    if (!creation) {
      s.setContact(contactEditor.getContact());
      s.setEquipment(equipEditor.getData());
      if (s.getPayer() == null) {
        s.setPayer(new Person(s.getContact().getId()));
      }
    }
    return s;
  }

  public Vector<Equipment> getEquipment() {
    return equipEditor.getData();
  }

  public void completeTabs(Room s) {
    setRoom(s);
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
    orderLineEditor.setMemberId(room.getContact().getId());
    orderLineEditor.setPayerId(room.getPayer().getId());
    orderLineEditor.setLabel(room.getPayer().getNameFirstname());
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

  private void initContact() {
    contactEditor = new ContactFileEditor(desktop);
    contactEditor.setCodePostalCtrl(new CodePostalCtrl(dataCache.getDataConnection()));
    setContact();
    wTab.addItem(contactEditor, TABS[1]);
    wTab.addItem(equipEditor, TABS[2]);
    creation = false;
  }

  private void setRoom(Room s) {
    this.room = s;
    editor.set(s);
  }

  private void setEquipment(Vector<Equipment> ve) {
    Vector<Equipment> vt = new Vector<Equipment>();
    if (ve != null) {
      try {
        for (Equipment e : ve) {
          vt.add((Equipment) e.clone());
        }
      } catch (CloneNotSupportedException ex) {
        GemLogger.logException(ex);
      }
    }
    equipEditor.load(vt);
  }
}
