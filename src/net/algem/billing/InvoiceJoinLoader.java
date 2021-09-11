/*
 * @(#)InvoiceJoinLoader 2.9.17 07/06/2019
 *                      from InvoiceLoader 2.9.4.13 09/11/15
 *
 * Copyright 1999-2019 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Cursor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.accounting.Account;
import net.algem.accounting.OrderLine;
import net.algem.config.Param;
import net.algem.planning.DateFr;
import net.algem.planning.DateRange;
import net.algem.security.User;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 2.17.0 07/06/2019
 */
public class InvoiceJoinLoader
        extends SwingWorker<Void, String> {

    private final HistoInvoice histo;
    private final BillingService service;
    private final ProgressMonitor monitor;
    private final DateRange range;
    private final int idper;
    private List<Invoice> invoices = new ArrayList<Invoice>();

    public InvoiceJoinLoader(HistoInvoice histo, BillingService service, DateRange range, int idper, ProgressMonitor monitor) {
        this.service = service;
        this.histo = histo;
        this.monitor = monitor;
        this.range = range;
        this.idper = idper;
    }

    @Override
    protected Void doInBackground() throws Exception {

        try {
        histo.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String where = " WHERE date_emission BETWEEN '" + range.getStart() + "' AND '" + range.getEnd() + "'";
        if (idper > 0) {
            where += " AND (debiteur = " + idper + " OR adherent = " + idper + ")";
        }
        int k = 1;
        int size = InvoiceIO.countInvoice(where);
        //System.out.println("InvoiceJoinLoader count=" + size);
        ResultSet rs = InvoiceIO.invoiceJoinSelect(where);
        String currentInvoice = null;
        int currentOrder = 0;
        Invoice inv = null;
        OrderLine ol = null;
        while (rs.next()) {
            if (currentInvoice == null || !currentInvoice.equals(rs.getString(1))) {
                currentInvoice = rs.getString(1);
                currentOrder = rs.getInt(2);
                inv = new Invoice(currentInvoice);
                inv.setDate(new DateFr(rs.getString(6)));
                inv.setEstablishment(rs.getInt(7));
                inv.setIssuer(rs.getInt(8));
                inv.setUser((User) DataCache.findId(rs.getInt(8), Model.User));
                inv.setPayer(rs.getInt(9));
                inv.setDescription(rs.getString(10));
                inv.setReference(rs.getString(11));
                inv.setMember(rs.getInt(12));
                inv.setDownPayment(rs.getDouble(13));

                ol = getOrderLineFromRS(rs);
                inv.addOrderLine(ol);

                inv.addItem(getItemFromRS(rs, ol));

                //System.out.println("InvoiceJoinLoader add invoice="+inv);
                invoices.add(inv);

                int p = k * 100 / size;
                setProgress(p);
                String m = k++ + " " + BundleUtil.getLabel("Invoice.tab.label").toLowerCase() + "  " + BundleUtil.getLabel("On.label") + " " + size;
                publish(m);

            } else {
                if (currentOrder == rs.getInt(2)) {
                    //System.out.println("InvoiceJoinLoader add item");
                    inv.addItem(getItemFromRS(rs, ol));
                } else {
                    currentOrder = rs.getInt(2);
                    ol = getOrderLineFromRS(rs);
                    //System.out.println("InvoiceJoinLoader add orderline="+ol);
                    inv.addOrderLine(ol);
                    inv.addItem(getItemFromRS(rs, ol));
                }
            }
        }
        } catch (Exception ex) {
            System.out.println("InvoiceJoinLoader EX="+ex);
            ex.printStackTrace();
        }
        return null;
    }

    private InvoiceItem getItemFromRS(ResultSet rs, OrderLine ol) throws SQLException {
        int itemId = rs.getInt(3);
        int qty = rs.getInt(4);
        Item item = null;
        if (itemId > 0) {
            item = (Item) DataCache.findId(itemId, Model.Item);
        }
        if (item == null || itemId == 0) {
            item = new Item(ol, qty);
        }
        InvoiceItem iitem = new InvoiceItem(rs.getString(1), item, qty, ol);

        return iitem;
    }

    private OrderLine getOrderLineFromRS(ResultSet rs) throws SQLException {
        OrderLine ol = new OrderLine();
        ol.setId(rs.getInt(14));
        ol.setDate(new DateFr(rs.getString(15)));
        ol.setPayer(rs.getInt(16));
        ol.setMember(rs.getInt(17));
        ol.setOrder(rs.getInt(18));
        ol.setLabel(rs.getString(20));
        ol.setModeOfPayment(rs.getString(19).trim());
        ol.setAmount(rs.getInt(21));
        ol.setDocument(rs.getString(22));
        ol.setSchool(rs.getInt(23));
        Account c = (Account) DataCache.findId(rs.getInt(24), Model.Account);
        ol.setAccount(c);
        ol.setPaid(rs.getBoolean(25));
        ol.setTransfered(rs.getBoolean(26));
        ol.setCurrency(rs.getString(27).trim());

        String code = rs.getString(28);
        Param p = DataCache.getCostAccount(code);
        Account a = null;
        if (p != null) {
            a = new Account(p);
            a.setLabel(p.getValue());
        } else {
            a = new Account(code);
            a.setLabel(code);
        }

        ol.setCostAccount(a);
        ol.setInvoice(rs.getString(29));

        ol.setGroup(rs.getInt(30));
        ol.setTax(rs.getFloat(31));

        return ol;
    }

    @Override

    public void process(List<String> data) {
        for (String n : data) {
            monitor.setNote(n);
        }
    }

    @Override
    public void done() {
        super.done();
        //System.out.println("InvoiceJoinLoader done size="+invoices.size());
        histo.load(invoices);
        histo.setCursor(Cursor.getDefaultCursor());
    }
}
