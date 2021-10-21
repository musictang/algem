/*
 * @(#)LibelSearchCtrl.java	2.9.4.13 15/10/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.CardLayout;
import java.awt.event.MouseEvent;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.ui.SearchCtrl;

/**
 * Search bank controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class LibelSearchCtrl
        extends SearchCtrl {

    public LibelSearchCtrl(DataConnection dc) {
        super(dc, "");
    }

    @Override
    public void init() {
        searchView = new LibelSearchView();
        searchView.addActionListener(this);

        list = new LibelListCtrl();
        list.addMouseListener(this);
        list.addActionListener(this);

        mask = new LibelCtrl(dc);
        mask.addActionListener(this);

        wCard.add("cherche", searchView);
        wCard.add("masque", mask.getContentPane());
        wCard.add("liste", list);

        ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
    }

    @Override
    public void search() {

        String query;
        String code;
        String libelle;

        if ((code = searchView.getField(0)) != null) {
            query = "WHERE lower(code) LIKE '%" + code.toLowerCase() + "%'";
        } else if ((libelle = searchView.getField(1)) != null) {
            query = "WHERE lower(libelle) LIKE '%" + libelle.toLowerCase() + "%'";
        } else {
            query = "";
        }

        List<Libel> v = LibelIO.find(query, dc);
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
    public void mouseClicked(MouseEvent e) {
        ((CardLayout) wCard.getLayout()).show(wCard, "masque");

        mask.loadCard(((LibelListCtrl) list).getSelected());
        //setDefaultCursor();
    }
}
