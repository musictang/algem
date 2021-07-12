/*
 * @(#)EnrolmentCurrentPanel.java	2.17.0 16/03/19
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
package net.algem.planning.wishes;

import java.awt.BorderLayout;
import java.awt.Color;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */

public class EnrolmentCurrentPanel extends GemPanel {

    HourField duration;
    GemLabel libelle;

    public EnrolmentCurrentPanel() {
        setLayout(new BorderLayout(5, 0));
        duration = new HourField();
        libelle = new GemLabel();
        add(duration, BorderLayout.WEST);
        add(libelle, BorderLayout.CENTER);
    }

    public void setCurrent(EnrolmentCurrent current, Color color) {
        if (current != null) {
            duration.set(current.getDuration());
            libelle.setText(current.getLibelle());

            setBackground(current.isCollectif() ? Color.GRAY : color);
            duration.setBackground(color);
        }
    }

    public EnrolmentCurrent getCurrent() {
        EnrolmentCurrent current = new EnrolmentCurrent();
        current.setDuration(duration.get());
        current.setLibelle(libelle.getText());

        return current;
    }
}
