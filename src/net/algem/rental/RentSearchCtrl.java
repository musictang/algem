/*
 * @(#)RentSearchCtrl.java	2.17.0t 29/08/19
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

import net.algem.course.*;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * rent search controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0t
 * @since 2.17.0t 29/08/2019
 */
public class RentSearchCtrl
        extends SearchCtrl {

    private final GemDesktop desktop;

    public RentSearchCtrl(GemDesktop desktop) {
        super(DataCache.getDataConnection(), null);
        this.desktop = desktop;
    }

    @Override
    public void init() {
        searchView = new RentSearchView();
        searchView.addActionListener(this);

        list = new RentListCtrl();
        list.addMouseListener(this);
        list.addActionListener(this);

        mask = new RentCtrl(desktop);
        mask.addActionListener(this);

        wCard.add("cherche", searchView);
        wCard.add("masque", mask.getContentPane());
        wCard.add("liste", list);

        ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
    }

    @Override
    public void search() {

        String m;
        String query = "";
        int id = getId();
        if (id > 0) {
            query += "WHERE id = " + id;
        } // search by title
        else if (null != (m = searchView.getField(1))) {
            query += "WHERE type ~* '" + m.toUpperCase() + "'";
        } // search by type
        else if (null != (m = searchView.getField(2))) {
            m = searchView.getField(2);
            query += "WHERE marque ~* '" + m.toUpperCase() + "'";
      } else if (null != (m = searchView.getField(3))) { // search by status
        if (m.equals("t")) {
          query += "WHERE actif = '" + m + "'";
        }
        }

        query += " ORDER BY type, marque";

        List<RentableObject> v = null;
        //try {
            v = ((RentableObjectIO) DataCache.getDao(Model.RentableObject)).find(query);
        //} catch (SQLException ex) {
        //    GemLogger.logException(ex);
        //}
        if (v.isEmpty()) {
            setStatus(EMPTY_LIST);
        } else if (v.size() == 1) {
            ((CardLayout) wCard.getLayout()).show(wCard, "masque");
            mask.loadCard(v.get(0));
        } else {
            ((CardLayout) wCard.getLayout()).show(wCard, "liste");
            list.loadResult(v);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GemCommand.CREATE_CMD.equals(evt.getActionCommand())) {
            mask.loadCard(new Course());
            ((CardLayout) wCard.getLayout()).show(wCard, "masque");
        } else {
            super.actionPerformed(evt);
        }
    }
}
