/*
 * @(#)WishHeaderElement.java 2.17.0 20/03/19
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.edition;

import java.awt.Graphics;
import net.algem.util.BundleUtil;

/**
 * Invoice header element.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 20/03/19
 */
public class WishHeaderElement
        extends WishItemElement {

    private String[] ocols = {BundleUtil.getLabel("Invoice.item.description.label"),
        BundleUtil.getLabel("Invoice.item.quantity.label"),
        BundleUtil.getLabel("Invoice.item.price.label"),
        BundleUtil.getLabel("Invoice.item.vat.label"),
        BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Invoice.et.label")};
    
    //FIXME TODO ERIC 2.17
    private String[] cols = {"Choix", "Discipline","Jour","Heure","Durée","Professeur"};

    public WishHeaderElement(int x, int y) {
        super(x, y);
    }

    @Override
    public void draw(Graphics g) {
        int top = 15;
        int margin = 5;
        g.drawString(cols[0], x + margin, y + top);
        g.drawString(cols[1], xColCourse + 2, y + top);
        g.drawString(cols[2], xColDay + 2, y + top);
        g.drawString(cols[3], xColHour + 2, y + top);
        g.drawString(cols[4], xColDuration + 2, y + top);
        g.drawString(cols[5], xColTeacher + 2, y + top);

//        center(g, cols[1], (xColDay - xColCourse), xColCourse, y + top);

    }

}
