/*
 * @(#)HistoMemberRentalView.java 2.17.1 29/08/2019
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
package net.algem.rental;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1
 */
public class HistoMemberRentalView
        extends FileTabDialog {

    private RentalHistoTableModel rentalHistoTableModel;
    private JTable table;
    protected int idper;
    protected ActionListener listener;
    private boolean loaded = false;

    /**
     * Inits rehearsal history.
     *
     * @param desktop
     * @param listener optional
     * @param id contact or group id
     */
    public HistoMemberRentalView(GemDesktop desktop, ActionListener listener, int id) {
        super(desktop);
        this.listener = listener;
        this.idper = id;

        rentalHistoTableModel = new RentalHistoTableModel();
        table = new JTable(rentalHistoTableModel);
        table.setAutoCreateRowSorter(false);

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(200);
        cm.getColumn(1).setPreferredWidth(80);
        cm.getColumn(2).setPreferredWidth(80);
        cm.getColumn(3).setPreferredWidth(300);

        JScrollPane sp = new JScrollPane(table);
        JPanel mainPanel = new GemPanel(new BorderLayout());
        mainPanel.add(sp, BorderLayout.CENTER);

        btValidation.setText(BundleUtil.getLabel("Rental.return.label"));
        btValidation.setToolTipText(BundleUtil.getLabel("Rental.return.tip"));
        btCancel.setText(GemCommand.CLOSE_CMD);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

    }

    @Override
    public void validation() {
        clear();
        load();
    }

    @Override
    public void cancel() {
        System.out.println("HistoMemberRentalView cancel");
        clear();
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoLocation.Abandon"));
        }
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Loads the rentals.
     *
     */
    @Override
    public void load() {
        int min = 0;
        List<RentalOperation> l = getRentals();

        if (l != null && l.size() > 0) {
            loaded = true;
        }
        for (RentalOperation o : l) {
            rentalHistoTableModel.addItem(o);
        }
    }

    public void clear() {
        if (rentalHistoTableModel.getRowCount() > 0) {
            rentalHistoTableModel.clear();
        }
    }

    public void reload() {
        clear();
        load();
    }
    
    public RentalOperation getSelected() {
        int idx = table.getSelectedRow();
        if (idx < 0) return null;
        return rentalHistoTableModel.getItem(idx);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btValidation) {
            int idx = table.getSelectedRow();
            if (idx < 0) return;
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoLocation.Return"));
            }
        } else {
           super.actionPerformed(evt); 
        }
    }

    /**
     * Retrieves the rentals.
     *
     * @return a list of schedules
     */
    public List<RentalOperation> getRentals() {

        String query = " WHERE adherent=" + idper;
        query += " ORDER BY debut DESC";

        return RentalOperationIO.find(query, DataCache.getDataConnection());

    }
}
