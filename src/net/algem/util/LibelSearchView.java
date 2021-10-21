/*
 * @(#)LibelSearchView.java	2.9.4.13 15/10/15
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

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * Search bank dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class LibelSearchView
        extends SearchView {

    private GemField libelCode;
    private GemField libelLibelle;
    private GemPanel maskPanel;

    public LibelSearchView() {
    }

    @Override
    public GemPanel init() {
        libelCode = new GemField(30);
        libelCode.addActionListener(this);
        libelLibelle = new GemField(50);
        libelLibelle.addActionListener(this);

        btCreate.setEnabled(false);
        btCreate.setText(null);
        btErase = new GemButton(GemCommand.ERASE_CMD);
        btErase.addActionListener(this);

        maskPanel = new GemPanel();
        maskPanel.setLayout(new GridBagLayout());

        GridBagHelper gb = new GridBagHelper(maskPanel);
        gb.add(new GemLabel(BundleUtil.getLabel("Code.label")), 0, 0, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Libel.label")), 0, 1, 1, 1, GridBagHelper.WEST);

        gb.add(libelCode, 1, 0, 1, 1, GridBagHelper.WEST);
        gb.add(libelLibelle, 1, 1, 1, 1, GridBagHelper.WEST);

        return maskPanel;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (actionListener == null) {
            return;
        }
        if (evt.getSource() == libelCode
                || evt.getSource() == libelLibelle) {
            actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.SEARCH_CMD));
        } else {
            actionListener.actionPerformed(evt);
        }
    }

    @Override
    public String getField(int n) {
        String s = null;
        switch (n) {
            case 0:
                s = libelCode.getText();
                break;
            case 1:
                s = libelLibelle.getText();
                break;
        }
        if (s != null && s.length() > 0) {
            return s;
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        libelCode.setText("");
        libelLibelle.setText("");
    }
}
