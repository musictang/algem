/*
 * @(#)HistoInvoice 2.9.4.7 15/06/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes All Rights Reserved.
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.planning.DateRange;
import net.algem.planning.DateRangePanel;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Controller for a list of invoices.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.7
 * @since 2.3.a 14/02/12
 */
public class HistoInvoice
        extends FileTabDialog
        implements GemEventListener {

    protected InvoiceListCtrl invoiceListCtrl;
    protected CardLayout layout;
    protected ActionListener actionListener;
    protected static final String card0 = "histo";
    protected static final String card1 = "invoice";
    protected Quote current;
    protected BillingService service;
    protected DateRangePanel rangePanel;
    protected int idper;

    public <Q extends Quote> HistoInvoice(final GemDesktop desktop, BillingService service) throws SQLException {
        super(desktop);
        this.service = service;
        btValidation.setText(GemCommand.VIEW_EDIT_CMD);
        btCancel.setText(GemCommand.CLOSE_CMD);

        setLayout(new CardLayout());
        GemPanel datePanel = new GemPanel();
        datePanel.add(new GemLabel(BundleUtil.getLabel("Period.label")));
        rangePanel = new DateRangePanel();
        DateRange range = service.getFinancialYear();
        rangePanel.setStart(range.getStart());
        rangePanel.setEnd(range.getEnd());
        datePanel.add(rangePanel);
        GemButton loadBt = new GemButton(BundleUtil.getLabel("Action.load.label"));
        loadBt.addActionListener(this);
        datePanel.add(loadBt);
        GemPanel histo = new GemPanel(new BorderLayout());
        setListCtrl();
        histo.add(datePanel, BorderLayout.NORTH);
        histo.add(invoiceListCtrl, BorderLayout.CENTER);
        histo.add(buttons, BorderLayout.SOUTH);
        add(histo, card0);
    }

    @Override
    public boolean isLoaded() {
        return invoiceListCtrl != null && invoiceListCtrl.getData() != null && invoiceListCtrl.getData().size() > 0;
    }

    @Override
    public void load() {
        desktop.addGemEventListener(this);
    }

    @Override
    public <T extends Object> void load(Collection<T> c) {
        if (invoiceListCtrl != null && c != null) {
            invoiceListCtrl.loadResult(new ArrayList<T>(c));
        }
        if (isLoaded()) {
            rangePanel.setStart(invoiceListCtrl.getElementAt(0).getDate());
        }
    }

    public void setIdper(int idper) {
        this.idper = idper;
    }

    protected void setListCtrl() {
        load();
        invoiceListCtrl = new InvoiceListCtrl(false, new BasicBillingService(dataCache));
        invoiceListCtrl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int id = ((InvoiceListCtrl) invoiceListCtrl).getIdContact();
                    if (id > 0) {
                        PersonFileEditor m = desktop.getModuleFileEditor(id);
                        if (m == null) {
                            desktop.setWaitCursor();
                            PersonFile pf = DataCache.getPersonFile(id);
                            if (pf == null) {
                                GemLogger.log("Error PersonFileSearchCtrl.createModule ID NOT FOUND:" + id);
                            } else {
                                PersonFileEditor editor = new PersonFileEditor(pf);
                                desktop.addModule(editor);
                                desktop.setDefaultCursor();
                            }
                        } else {
                            desktop.setSelectedModule(m);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void validation() {

        int n = 0;
        try {
            n = invoiceListCtrl.getSelectedIndex();
        } catch (IndexOutOfBoundsException ix) {
            MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
            return;
        }
        Invoice inv = (Invoice) invoiceListCtrl.getElementAt(n);
        current = inv;
        InvoiceEditor editor = new InvoiceEditor(desktop, new BasicBillingService(dataCache), inv);
        editor.addActionListener(this);
        editor.addGemEventListener(this);
        editor.load();
        add(editor, card1);
        layout = (CardLayout) getLayout();
        layout.show(this, card1);

    }

    @Override
    public void cancel() {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoFacture.Abandon"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        String cmd = e.getActionCommand();
        if (cmd.equals(BundleUtil.getLabel("Action.load.label"))) {
            ProgressMonitor monitor = new ProgressMonitor(this, BundleUtil.getLabel("Loading.label"), "", 1, 100);
            monitor.setProgress(1);
            monitor.setMillisToDecideToPopup(10);
            DateRange range = new DateRange(rangePanel.getStartFr(), rangePanel.getEndFr());
            SwingWorker<Void, String> task = new InvoiceLoader(this, service, range, idper, monitor);
            task.addPropertyChangeListener(new ProgressMonitorHandler(monitor, task));
            task.execute();
        } else if (cmd.equals("CtrlAbandonFacture")) {
            layout.show(this, card0);
        }
    }

    public void addActionListener(ActionListener l) {
        this.actionListener = l;
    }

    @Override
    public void postEvent(GemEvent evt) {
        if (evt instanceof InvoiceEvent) {
            Invoice iv = ((InvoiceEvent) evt).getInvoice();
            if (idper > 0 && !(iv.getPayer() == idper || iv.getMember() == idper)) {
                return;
            }
            if (evt.getOperation() == GemEvent.MODIFICATION) {
                invoiceListCtrl.reload(iv);
            } else if (evt.getOperation() == GemEvent.CREATION) {
                invoiceListCtrl.addRow(iv);
            }
        }
    }
}
