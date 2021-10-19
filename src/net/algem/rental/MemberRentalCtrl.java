/*
 * @(#)MemberRentalCtrl.java	2.17.1 29/08/2013
 *                              2.15.8 26/03/18
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
import javax.swing.JOptionPane;
import net.algem.contact.PersonFile;
import net.algem.planning.*;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.MessagePopup;

/**
 * Single rehearsal controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 28/09/2019
 */
public class MemberRentalCtrl
        extends FileTabDialog {

    private PersonFile personFile;
    private MemberRentalView view;
    private ActionListener actionListener;
    private RentalService rentalService;

    public MemberRentalCtrl(GemDesktop desktop) {
        super(desktop);
        rentalService = new RentalService(dc);
    }

    public MemberRentalCtrl(GemDesktop desktop, ActionListener listener, PersonFile dossier) {
        this(desktop);
        personFile = dossier;
        actionListener = listener;

//        view = new MemberRentalView(dataCache.getList(Model.RentableObject));
        view = new MemberRentalView(rentalService.findAvailableRentable());
        view.set(personFile.getContact());

        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    public void clear() {
        view.clear();
    }

    @Override
    public void load() {
        view.set(personFile.getContact());
    }

    @Override
    public boolean isLoaded() {
        return personFile != null;
    }

    @Override
    public void cancel() {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentLocation.Abandon"));
    }

    @Override
    public void validation() {

        if (!isEntryValid(view.getDate())) {
            return;
        }
        try {
            if (!save()) {
                return;
            }
            JOptionPane.showMessageDialog(this,
                    MessageUtil.getMessage("rental.create.info"),
                    MessageUtil.getMessage("rental.member.entry"),
                    JOptionPane.INFORMATION_MESSAGE);
            actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentLocation.Validation"));
        } catch (RentException ex) {
            GemLogger.logException(MessageUtil.getMessage("rental.create.exception"), ex, this);
        }
    }

    private boolean isEntryValid(DateFr date) {

        String dateError = MessageUtil.getMessage("date.entry.error");//date incorrecte
        String entryError = MessageUtil.getMessage("entry.error");

        if (date.bufferEquals(DateFr.NULLDATE)) {
            MessagePopup.error(view, dateError, entryError);
            return false;
        }
        if (date.before(dataCache.getStartOfPeriod())
                || date.after(dataCache.getEndOfPeriod())) {
            MessagePopup.error(view, MessageUtil.getMessage("date.out.of.period"), entryError);
            return false;
        }

        return true;
    }

    private boolean save() throws RentException {

        RentalOperation r = new RentalOperation();
        r.setRentableObjectId(view.getRentableId());
        r.setStartDate(view.getDate());
        r.setMemberId(personFile.getId());
        try {
            r.setAmount(Integer.parseInt(view.getAmount())*100);
        } catch (Exception e) {
            return false;
        }
        r.setDescription(view.getDescription());
        try {
            rentalService.saveRental(r);
            rentalService.saveRentalOrderLine(view.getRentable(), personFile, view.getDate(), r.getAmount()/100f);
        } catch (Exception ex) {
            throw new RentException(ex.getMessage());
        }
        return true;
    }

}
