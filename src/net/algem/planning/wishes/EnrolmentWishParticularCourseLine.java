/*
 * @(#)EnrolmentWishParticularCourseLine.java	2.17.0 16/03/19
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

import java.time.LocalDateTime;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */
public class EnrolmentWishParticularCourseLine {

    private static final long serialVersionUID = 1L;

    private Hour hour;   // colonne 0
    private EnrolmentCurrent current;   // colonne 1
    private EnrolmentSelected selected;   // colonne 2
    private LocalDateTime mailDate;    // colonne 3
    private EnrolmentWish sameAsCurrent; // colonne 4
    private EnrolmentWish[] choice; // colonne 5 à 7

    public EnrolmentWishParticularCourseLine(Hour hour) {
        this.hour = hour;
        this.current = new EnrolmentCurrent();
        this.selected = new EnrolmentSelected();
        this.sameAsCurrent = new EnrolmentWish();
        this.choice = new EnrolmentWish[3];
        for (int i = 0; i <= 2; i++) 
            choice[i] = new EnrolmentWish();
    }

    public EnrolmentWishParticularCourseLine(Hour hour, EnrolmentCurrent current) {
        this(hour);
        this.current = current;
    }

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour heure) {
        this.hour = hour;
    }

    public EnrolmentCurrent getCurrent() {
        return current;
    }

    public void setCurrent(EnrolmentCurrent current) {
        this.current = current;
    }

    public EnrolmentSelected getSelected() {
        return selected;
    }

    public void setSelected(EnrolmentSelected selected) {
        this.selected = selected;
    }

    public LocalDateTime getMailDate() {
        return mailDate;
    }

    public void setMailDate(LocalDateTime mailDate) {
        this.mailDate = mailDate;
    }

    public EnrolmentWish getSameAsCurrent() {
        return sameAsCurrent;
    }

    public void setSameAsCurrent(EnrolmentWish sameAsCurrent) {
        this.sameAsCurrent = sameAsCurrent;
    }

    public EnrolmentWish getChoice(int n) {
        return choice[n];
    }

    public void setChoice(int n, EnrolmentWish choice) {
        this.choice[n] = choice;
    }
}
