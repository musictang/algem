/*
 * @(#)RoomFileEditor.java 2.8.m 11/09/13
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
package net.algem.room;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import net.algem.accounting.OrderLineEditor;
import net.algem.accounting.OrderLineTableModel;
import net.algem.billing.*;
import net.algem.contact.*;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.module.FileEditor;
import net.algem.util.module.FileView;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.1.b
 */
public class RoomFileEditor
        extends FileEditor
{

  private Room room;
  private GemToolBar mainToolbar;
  private GemToolBar closeToolbar;
  private GemButton btSave;
  private GemButton btClose;
  private GemButton btNote;
  private GemButton btOrderLine;
  private GemButton btPayer;
  private JMenuBar mBar;
  private JMenuItem miHistoInvoice;
  private JMenuItem miHistoQuote;
  private RoomFileView roomView;
  private RoomService service;
  private List<Equipment> oldEquip = new Vector<Equipment>();
  private int oldPayerId;
  private PersonFile payerFile;
  private Date date;

  public RoomFileEditor(Room r, String key) {
    super(key);
    this.room = r;    
  }

  /**
   *
   * @return room id converted to string
   */
  @Override
  public String getSID() {
    return String.valueOf(room.getId());
  }

  @Override
  public void init() {
    super.init();
    desktop.addGemEventListener(this);
    service = new RoomService(dataCache.getDataConnection());
    loadEquipment();
    backup();
    view = roomView = new RoomFileView(desktop, room, service);
    roomView.init();
    roomView.addActionListener(this);

    // MENUS
    mBar = new JMenuBar();
    JMenu mFile = new JMenu(BundleUtil.getLabel("Menu.file.label"));

    mFile.add(getMenuItem("Room.suppression"));
    JMenu mOptions = new JMenu(BundleUtil.getLabel("Menu.options.label"));
    mOptions.add(miHistoInvoice = getMenuItem("Invoice.history"));
    mOptions.add(miHistoQuote = getMenuItem("Quotation.history"));

    mBar.add(mFile);
    mBar.add(mOptions);

    view.setJMenuBar(mBar);

    // BARRE D'OUTILS
    mainToolbar = new GemToolBar(false);

    btNote = mainToolbar.addIcon(
            BundleUtil.getLabel("Note.icon"),
            GemCommand.NOTE_CMD,
            BundleUtil.getLabel("Note.icon.tip"));
    btOrderLine = mainToolbar.addIcon(
            BundleUtil.getLabel("Member.schedule.payment.icon"),
            GemCommand.ECHEANCIER_CMD,
            BundleUtil.getLabel("Member.schedule.payment.tip"));
    btNote.addActionListener(this);
    btOrderLine.addActionListener(this);

    closeToolbar = new GemToolBar(false);
    //closeToolbar.setAlignmentX(JToolBar.RIGHT_ALIGNMENT);
    GemBorderPanel toolbar = new GemBorderPanel();
    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
    toolbar.add(mainToolbar);
    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(closeToolbar);

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

    btPayer = mainToolbar.addIcon(
            BundleUtil.getLabel("Member.payer.icon"),
            "PayerLink",
            BundleUtil.getLabel("Member.payer.tip"));
    btPayer.addActionListener(this);
    if (hasOtherPayer()) {
      addPayerFile(room.getPayer().getId());
    } else {
      btPayer.setEnabled(false);
    }

    roomView.add(toolbar, BorderLayout.NORTH);

  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    String arg = evt.getActionCommand();
    Object src = evt.getSource();
    if (GemCommand.NOTE_CMD.equals(arg)) {
      NoteDlg nd = new NoteDlg(desktop);
      nd.loadNote(roomView.getNote(), room.getContact());
      nd.show();
      roomView.setNote(nd.getNote());
    } else if (GemCommand.ECHEANCIER_CMD.equals(arg)) {
      loadOrderLine();
      btOrderLine.setEnabled(false);
    } else if (GemCommand.CLOSE_CMD.equals(arg)) {
      close();
    } else if (GemCommand.SAVE_CMD.equals(arg)) {
      save(roomView.getRoom());
    } else if ("Room.suppression".equals(arg)) {
      delete();
    } else if ("PayerLink".equals(arg)) {
      PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(room.getPayer().getId());
      if (m == null) {
        PersonFileEditor editor = new PersonFileEditor(payerFile);
        desktop.addModule(editor);
      } else {
        desktop.setSelectedModule(m);
      }
    } else if ("Invoice.history".equals(arg)) {
      int payer = getPayer();
      if (payer <= 0) {
        return;
      }
      HistoInvoice hf = addHistoInvoice(payer, room.getContact().getId());
      hf.addActionListener(this);
      roomView.addTab(hf, FileView.HISTO_INVOICE_TAB_TITLE);
      miHistoInvoice.setEnabled(false);
    } else if ("Quotation.history".equals(arg)) {
      int payeur = getPayer();
      if (payeur <= 0) {
        return;
      }
      HistoQuote hd = getHistoQuotation(payeur, room.getContact().getId());
      hd.addActionListener(this);
      roomView.addTab(hd, FileView.HISTO_ESTIMATE_TAB_TITLE);
      miHistoQuote.setEnabled(false);
    } else if ("HistoFacture.Abandon".equals(arg)) {
      roomView.removeTab((HistoInvoice) src);
      miHistoInvoice.setEnabled(true);
    } else if (FileView.INVOICE_TAB_TITLE.equals(arg)) {
      InvoiceEditor ef = addInvoice(src, room);
      roomView.addTab(ef, FileView.INVOICE_TAB_TITLE);
    } else if ("CtrlAbandonFacture".equals(arg)) {
      roomView.removeTab((InvoiceEditor) src);
    } else if ("HistoDevis.Abandon".equals(arg)) {
      roomView.removeTab((HistoQuote) src);
      miHistoQuote.setEnabled(true);
    } else if (FileView.ESTIMATE_TAB_TITLE.equals(arg)) {
      QuoteEditor ed = addQuotation(src, room);
      roomView.addTab(ed, FileView.ESTIMATE_TAB_TITLE);
    } else if ("CtrlAbandonDevis".equals(arg)) {
      roomView.removeTab((QuoteEditor) src);
    } else if (CloseableTab.CLOSE_CMD.equals(arg)) {// fermeture de l'onglet par le bouton de fermeture
      if (getClassName(OrderLineEditor.class).equals(src)) {
        btOrderLine.setEnabled(true);
      } else if (getClassName(HistoInvoice.class).equals(src)) {
        miHistoInvoice.setEnabled(true);
      } else if (getClassName(HistoQuote.class).equals(src)) {
        miHistoQuote.setEnabled(true);
      }
    }

  }

  private int getPayer() {
    if (room.getPayer() == null || room.getContact() == null) {
      return -1;
    }
    return room.getPayer().getId() > 0 ? room.getPayer().getId() : room.getContact().getId();
  }

  @Override
  /**
   * @see net.algem.module.GemModule#postEvent
   */
  public void postEvent(GemEvent evt) {
    if (evt instanceof InvoiceEvent) {
      Invoice iv = ((InvoiceEvent) evt).getInvoice();
      if (room != null && room.getPayer() != null && room.getPayer().getId() == iv.getPayer()) {
        OrderLineEditor ole = roomView.getOrderLineEditor();
        if (ole != null) {
          ole.postEvent(evt);
        }
      }
    }

  }

  @Override
  public void close() {
    Room r = roomView.getRoom();
    if (hasChanged(r)) {
      if (MessagePopup.confirm(roomView,
              MessageUtil.getMessage("room.update.confirmation", room.getName()),
              MessageUtil.getMessage("closing.label"))) {
        updateRoom(r);
      } 
    }
    closeModule();
  }

  public void setDate(Date d) {
    this.date = d;
  }

  /**
   * Checks the modifications in views.
   *
   * @return true if modification
   */
  private boolean hasChanged(Room r) {

    return !(r.isEqualOf(room))
            || !(r.getEquipment().equals(oldEquip))
            || oldPayerId != r.getPayer().getId();
  }

  /**
   * Enregistrement d'une salle après modifications éventuelles.
   */
  private void save(Room r) {
    if (hasChanged(r)) {
      updateRoom(r);
      if (hasOtherPayer()) {
        addPayerFile(r.getPayer().getId());
      } else if (payerFile != null) {
        payerFile = null;
        btPayer.setEnabled(false);
      }
    }

  }

  /**
   * Creates or update the current room.
   */
  private void updateRoom(Room r) {
    try {

      if (r.getId() == 0) {
        service.create(r);
        roomView.completeTabs(r);
        dataCache.add(r);
        desktop.postEvent(new RoomCreateEvent(this, r));
      } else {
        service.update(room, r);
        dataCache.update(r);
        desktop.postEvent(new RoomUpdateEvent(this, r, date));
      }
      this.room = r;
      backup();
    } catch (RoomException c) {
      MessagePopup.warning(null, c.getMessage());
    } catch (SQLException e) {
      MessagePopup.warning(roomView, MessageUtil.getMessage("update.exception.info") + e.getMessage());
      GemLogger.logException(e);
    }

  }
  
  private void backup() {

    if (room.getEquipment() != null) {
      oldEquip.clear();
      for (Equipment e : room.getEquipment()) {       
        oldEquip.add(new Equipment(e.getLabel(), e.getQuantity(), e.getRoom()));
      }
    }
    if (room.getPayer() != null) {
      oldPayerId = room.getPayer().getId();
    }
  }

  private void delete() {
    try {
      if (MessagePopup.confirm(roomView, MessageUtil.getMessage("room.delete.confirmation"))) {
        service.delete(room);
        dataCache.remove(room);
        closeModule();
      }
    } catch (RoomException ex) {
      MessagePopup.warning(view, ex.getMessage());
    }
  }

  /**
   * Loads order lines for the current room.
   */
  private void loadOrderLine() {
    OrderLineTableModel t = service.getOrderLineTabelModel(room);
    if (t != null) {
      roomView.addOrderLine(t);
    }
  }

  private void loadEquipment() {
    Vector<Equipment> ve = service.getEquipment(room);
    room.setEquipment(ve);
  }

  private void closeModule() {
    try {
      view.close();
      desktop.removeGemEventListener(this);
      desktop.removeModule(this);
    } catch (GemCloseVetoException ex) {
      System.err.println(ex.getMessage());
    }
  }

  /**
   *
   * @param id payer id
   */
  private void addPayerFile(int id) {
    if (payerFile != null && payerFile.getId() == id) {
      return;
    }
    Contact c = ContactIO.findId(id, dataCache.getDataConnection());
    if (c != null) {
      payerFile = new PersonFile(c);
      try {
        ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(payerFile);
        btPayer.setEnabled(true);
      } catch (SQLException ex) {
        GemLogger.logException(MessageUtil.getMessage("record.completion.exception"), ex);
      }
    }

  }

  private boolean hasOtherPayer() {
    return room.getPayer() != null && room.getContact() != null
            && room.getPayer().getId() != room.getContact().getId();
  }
}
