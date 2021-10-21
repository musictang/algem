/*
 * @(#)LibelView.java	2.9.4.13 15/10/15
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
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Libel view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.13
 */
public class LibelView
        extends GemBorderPanel {

    private GemField code;
    private JTextArea libelle;

    public LibelView() {
        code = new GemField(30);
        code.setEditable(false);
        libelle = new JTextArea(3, 50);
        libelle.setLineWrap(true);
        libelle.setWrapStyleWord(true);

        this.setLayout(new GridBagLayout());
        GridBagHelper gb = new GridBagHelper(this);

        gb.add(new GemLabel(BundleUtil.getLabel("Libel.code.label")), 0, 0, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Libel.libelle.label")), 0, 1, 1, 1, GridBagHelper.WEST);
        gb.add(code, 1, 0, 3, 1, GridBagHelper.WEST);
        gb.add(libelle, 1, 1, 3, 1, GridBagHelper.WEST);
    }

    public void setCode(String s) {
        code.setText(s);
    }

    public void setLibelLibelle(String s) {
        libelle.setText(s);
    }

    public String getLibelLibelle() {
        return libelle.getText();
    }

    public String getLibelCode() {
        return code.getText();
    }

    public void setLibel(Libel b) {
        code.setText(b.getCode());
        libelle.setText(b.getLibelle());
    }

    public Libel getLibel() {
        Libel b = new Libel(getLibelCode(), getLibelLibelle());
        return b;
    }

    public void clear() {
        code.setText("");
        libelle.setText("");
    }
}
