/*
 * @(#)RoomFileEditor.java 2.14.0 13/06/17
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
package net.algem.room;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
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
import net.algem.util.module.FileTabView;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.1.b
 */
public class RoomFileEditor
        extends FileEditor {

    private static final String ROOM_DOSSIER_KEY = "ModuleSalle";
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
    private JMenuItem miSchedule;
    private JMenuItem miTimes;
    private RoomFileView roomView;
    private RoomService service;
    private List<Equipment> oldEquip = new ArrayList<>();
    private int oldPayerId;
    private int oldContactId;
    private PersonFile payerFile;
    private Date date;

    public RoomFileEditor(Room r) {
//    super(ROOM_DOSSIER_KEY);
        super(r.getName() != null ? "Fiche: " + r.getName() : ROOM_DOSSIER_KEY);
        this.room = r;
    }

    /**
     * Gets room's id as string.
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
        service = new RoomService(DataCache.getDataConnection());
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
        mOptions.add(miSchedule = getMenuItem("Menu.month.schedule"));
        mOptions.addSeparator();
        mOptions.add(miHistoInvoice = getMenuItem("Invoice.history"));
        mOptions.add(miHistoQuote = getMenuItem("Quotation.history"));
        miTimes = new JMenuItem(BundleUtil.getLabel("Times.label"));
        miTimes.setToolTipText(BundleUtil.getLabel("Room.times.tip"));
        miTimes.addActionListener(this);
        mOptions.add(miTimes);
        mOptions.addSeparator();
        JMenuItem miContact = getMenuItem("Contact.change");
        miContact.setEnabled(dataCache.authorize("Contact.modification.auth"));
        mOptions.add(miContact);

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
            PersonFileEditor m = desktop.getModuleFileEditor(room.getPayer().getId());
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
            HistoInvoice hb = addHistoInvoice(payer);
            if (hb != null) {
                hb.addActionListener(this);
                roomView.addTab(hb, FileTabView.HISTO_INVOICE_TAB_TITLE);
                miHistoInvoice.setEnabled(false);
            } else {
                MessagePopup.information(view, MessageUtil.getMessage("no.invoice.recorded"));
            }
        } else if ("Quotation.history".equals(arg)) {
            int payer = getPayer();
            if (payer <= 0) {
                return;
            }
            HistoQuote hq = getHistoQuotation(payer);
            if (hq != null) {
                hq.addActionListener(this);
                roomView.addTab(hq, FileTabView.HISTO_ESTIMATE_TAB_TITLE);
                miHistoQuote.setEnabled(false);
            } else {
                MessagePopup.information(view, MessageUtil.getMessage("no.quote.recorded"));
            }
        } else if ("HistoFacture.Abandon".equals(arg)) {
            roomView.removeTab((HistoInvoice) src);
            miHistoInvoice.setEnabled(true);
        } else if (FileTabView.INVOICE_TAB_TITLE.equals(arg)) {
            InvoiceEditor ef = addInvoice(src, room);
            roomView.addTab(ef, FileTabView.INVOICE_TAB_TITLE);
        } else if ("CtrlAbandonFacture".equals(arg)) {
            roomView.removeTab((InvoiceEditor) src);
        } else if ("HistoDevis.Abandon".equals(arg)) {
            roomView.removeTab((HistoQuote) src);
            miHistoQuote.setEnabled(true);
        } else if (FileTabView.ESTIMATE_TAB_TITLE.equals(arg)) {
            QuoteEditor ed = addQuotation(src, room);
            roomView.addTab(ed, FileTabView.ESTIMATE_TAB_TITLE);
        } else if ("CtrlAbandonDevis".equals(arg)) {
            roomView.removeTab((QuoteEditor) src);
        } else if ("Menu.month.schedule".equals(arg)) {
            RoomScheduleCtrl dlg = new RoomScheduleCtrl(desktop, room.getId());
            roomView.addTab(dlg, BundleUtil.getLabel("Menu.month.schedule.label"));
            miSchedule.setEnabled(false);
        } else if (BundleUtil.getLabel("Times.label").equals(arg)) {
            DailyTimesEditor dtEditor = new DailyTimesEditor(desktop, room.getId(), service);
            dtEditor.load();
            dtEditor.addActionListener(this);
            roomView.addTab(dtEditor, arg);
            miTimes.setEnabled(false);
        } else if ("CancelEditingTimes".equals(arg)) {
            roomView.removeTab((DailyTimesEditor) src);
            miTimes.setEnabled(true);
        } else if ("Contact.change".equals(arg)) {
//      System.out.println("modifier contact");
            PersonFileSearchCtrl contactBrowser = new PersonFileSearchCtrl(desktop, BundleUtil.getLabel("Contact.browser.label"), this);
            contactBrowser.init();
            desktop.addPanel("Contact", contactBrowser, GemModule.S_SIZE);
        } else if (CloseableTab.CLOSE_CMD.equals(arg)) {// fermeture de l'onglet par le bouton de fermeture
            if (getClassName(OrderLineEditor.class).equals(src)) {
                btOrderLine.setEnabled(true);
            } else if (getClassName(HistoInvoice.class).equals(src)) {
                miHistoInvoice.setEnabled(true);
            } else if (getClassName(HistoQuote.class).equals(src)) {
                miHistoQuote.setEnabled(true);
            } else if (getClassName(RoomScheduleCtrl.class).equals(src)) {
                miSchedule.setEnabled(true);
            } else if (getClassName(DailyTimesEditor.class).equals(src)) {
                miTimes.setEnabled(true);
            }
        }

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
        } else if (evt instanceof ContactSelectEvent) {
            Contact c = ((ContactSelectEvent) evt).getContact();
            room.setContact(c);
//      System.out.println(c + "selected");
            roomView.setContact(c);
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
            } else {
                try {
                    room.setContact(new Contact((Person) DataCache.findId(oldContactId, Model.Person)));
                    room.setPayer((Person) DataCache.findId(oldPayerId, Model.Person));
                } catch (SQLException ex) {
                    GemLogger.log(Level.SEVERE, ex.getMessage());
                }

            }
        }
        closeModule();
    }

    public void setDate(Date d) {
        this.date = d;
    }

    private int getPayer() {
        if (room.getPayer() == null || room.getContact() == null) {
            return -1;
        }
        return room.getPayer().getId() > 0 ? room.getPayer().getId() : room.getContact().getId();
    }

    /**
     * Checks the modifications in views.
     *
     * @return true if modification
     */
    private boolean hasChanged(Room r) {

        return !(r.isEqualOf(room))
                || !(r.getEquipment().equals(oldEquip))
                || oldPayerId != r.getPayer().getId()
                || oldContactId != r.getContact().getId();
    }

    /**
     * Enregistrement d'une salle après modifications éventuelles.
     */
    private void save(Room r) {
        if (!dataCache.authorize("Room.creation.auth")) {
            MessagePopup.warning(view, MessageUtil.getMessage("room.creation.authorization.warning"));
            return;
        }
        if (hasChanged(r)) {
            String msg = MessageUtil.getMessage("update.warning");
            if (MessagePopup.confirm(roomView, msg, "Création nouvelle salle")) {
                updateRoom(r);
                if (hasOtherPayer()) {
                    addPayerFile(r.getPayer().getId());
                } else if (payerFile != null) {
                    payerFile = null;
                    btPayer.setEnabled(false);
                }
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
                desktop.postEvent(new RoomEvent(this, GemEvent.CREATION, r));
            } else {
                service.update(room, r);
                dataCache.update(r);
                desktop.postEvent(new RoomEvent(this, GemEvent.MODIFICATION, r, date));
            }
            this.room = r;
            backup();
        } catch (RoomException c) {
            MessagePopup.warning(null, c.getMessage());
        }
    }

    private void backup() {

        if (room.getEquipment() != null) {
            oldEquip.clear();
            for (Equipment e : room.getEquipment()) {
                Equipment o = new Equipment(e.getLabel(), e.getQuantity(), e.getRoom());
                o.setFixedAssetNumber(e.getFixedAssetNumber());
                o.setVisible(e.isVisible());
                oldEquip.add(o);
            }
        }
        if (room.getPayer() != null) {
            oldPayerId = room.getPayer().getId();
        }
        if (room.getContact() != null) {
            oldContactId = room.getContact().getId();
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
        List<Equipment> ve = service.getEquipment(room);
        room.setEquipment(ve);
    }

    private void closeModule() {
        try {
            view.close();
            desktop.removeGemEventListener(this);
            desktop.removeModule(this);
        } catch (GemCloseVetoException ex) {
            GemLogger.log(Level.WARNING, ex.getMessage());
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
        Contact c = ContactIO.findId(id, DataCache.getDataConnection());
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
