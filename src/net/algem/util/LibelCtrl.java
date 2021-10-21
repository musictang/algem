/*
 * @(#)LibelCtrl.java	2.6.a 14/09/12
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
package net.algem.util;

import java.awt.event.ActionEvent;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 */
public class LibelCtrl
        extends CardCtrl {

    private DataConnection dc;
    private LibelView libelView;
    private Libel libel;

    public LibelCtrl(DataConnection dc) {

        this.dc = dc;
        libelView = new LibelView();

        addCard("fiche libelle", libelView);
        select(0);
    }

    @Override
    public boolean next() {
        switch (step) {
            default:
                select(step + 1);
                break;
        }
        return true;
    }

    @Override
    public boolean cancel() {
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
        }
        return true;
    }

    @Override
    public boolean prev() {
        switch (step) {
            default:
                select(step - 1);
                break;
        }
        return true;
    }

    @Override
    public boolean validation() {
        if (libel == null) {
            return false;
        }

        if (libel.equals(getLibel())) {
            return true;
        }

        libel = getLibel();

        try {
            LibelIO.update(libel, dc);
        } catch (Exception e1) {
            GemLogger.logException("update libelle", e1, contentPane);
            return false;
        }
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
        }
        return true;
    }

    public void clear() {
        libelView.clear();
    }

    @Override
    public boolean loadCard(Object o) {
        clear();
        if (o == null || !(o instanceof Libel)) {
            return false;
        }

        libel = (Libel) o;
        libelView.setLibel(libel);

        return true;
    }

    @Override
    public boolean loadId(int id) {
        return loadCard(LibelIO.findCode(String.valueOf(id), dc));
    }

    public Libel getLibel() {
        return libelView.getLibel();
    }
}
