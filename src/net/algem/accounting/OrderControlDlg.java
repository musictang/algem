/*
* @(#)OrderControlDlg.java	2.17.1 20/10/19
*
* Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Global view of orderlines.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1d 20/10/2019
 */
public class OrderControlDlg
        extends GemPanel
        implements ActionListener, TableModelListener {

    private Frame parent;
    private final DataConnection dc;
    private final DataCache dataCache;
    private OrderControlTableModel tableModel;
    private DateFrField dateStart;
    private DateFrField dateEnd;
    private OrderControlTableView tableView;
    private GemButton btLoad;
    private GemButton btPrint;
    private GemPanel buttons;
    private final NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);

    /**
     *
     * @param desktop GemDesktop instance
     * @param tableModel
     */
    public OrderControlDlg(GemDesktop desktop, OrderControlTableModel tableModel) {
        parent = desktop.getFrame();
        this.dataCache = desktop.getDataCache();
        this.dc = DataCache.getDataConnection();
        this.tableModel = tableModel;
    }

    public void init() {

        tableView = new OrderControlTableView(tableModel, this);

        btPrint = new GemButton(BundleUtil.getLabel("Action.print.label"));
        btPrint.addActionListener(this);

        dateStart = new DateFrField(dataCache.getStartOfPeriod());
        dateEnd = new DateFrField(dataCache.getEndOfPeriod());
        btLoad = new GemButton(BundleUtil.getLabel("Action.load.label"));
        btLoad.setPreferredSize(new Dimension(btLoad.getPreferredSize().width, dateStart.getPreferredSize().height));
        btLoad.addActionListener(this);

        GemPanel header = new GemPanel();

        header.add(new JLabel(BundleUtil.getLabel("Date.From.label")));
        header.add(dateStart);
        header.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
        header.add(dateEnd);
        header.add(btLoad);

        buttons = new GemPanel(new GridLayout(1, 4));
        buttons.add(btPrint);

        GemPanel top = new GemPanel(new BorderLayout());
        top.add(header, BorderLayout.SOUTH);

        GemPanel bottom = new GemPanel(new BorderLayout());

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(tableView, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    @Override
    public void tableChanged(TableModelEvent evt) {
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();

        if (src == btPrint) {
            try {
                // printing of all orderlines, regardless of selection
                AccountUtil.print(tableView.getTable());
            } catch (PrinterException ex) {
                System.err.format("Cannot print %s%n", ex.getMessage());
            }
        } else if (src == btLoad) {
            load();
        }
    }

    public void load() {
        tableModel.clear();
        DateFr debut = dateStart.getDateFr();
        DateFr fin = dateEnd.getDateFr();
        nf.setMinimumFractionDigits(2);

        String query = "select payeur, adherent, commande, montant from echeancier2"
                + " WHERE echeance >= '" + debut + "' AND echeance <= '" + fin + "'"
                + " order by payeur, commande";
        try {
            ResultSet rs = dc.executeQuery(query);
            int curpay = 0;
            int cpt = 0;
            int montant = 0;

            while (rs.next()) {
                int payeur = rs.getInt(1);
                if (payeur != curpay) {
                    if (curpay == 0) {
                        curpay = payeur;
                        cpt = 1;
                        montant = rs.getInt(4);
                    } else {
                        addOrder(curpay, cpt, montant);
                        curpay = payeur;
                        cpt = 1;
                        montant = rs.getInt(4);
                    }
                } else {
                    cpt++;
                    montant += rs.getInt(4);
                }
            }
            addOrder(curpay, cpt, montant);

            //System.out.println("payeur "+curpay+" cpt="+cpt);
        } catch (Exception ex) {
            GemLogger.logException("EnrolmentWishService.getCurrentEnrolmentForStudent", ex);
        }
    }

    private void addOrder(int payer, int cpt, int montant) {
        if (montant != 0) {
            double mnt = (double) montant / 100;
            String[] line = new String[4];
            line[0] = String.valueOf(payer);
            try {
                Person p = (Person) DataCache.findId(payer, Model.Person);
                line[1] = p.getFirstnameName();
            } catch (Exception e) {
                line[1] = "inconnu";
            }
            line[2] = String.valueOf(cpt);
            line[3] = nf.format(mnt);
            tableModel.addElement(line);
        }

    }

    /**
     * Loading of the orderlines of current month.
     */
    private void loadCurrentMonth() {

        DateFr b = new DateFr(new Date());
        b.setDay(1);
        dateStart.set(b);
        b.incMonth(1);
        b.decDay(1);
        dateEnd.set(b);
        load();
    }

}
