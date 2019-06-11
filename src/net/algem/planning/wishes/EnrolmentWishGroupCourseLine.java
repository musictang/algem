/*
 * @(#)EnrolmentWishGroupCourseLine.java	2.17.0 15/05/19
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
import net.algem.contact.Person;
import net.algem.planning.DateFr;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 15/05/19
 */
public class EnrolmentWishGroupCourseLine {

    private static final long serialVersionUID = 1L;
    
    private EnrolmentWish wish;

    private Person student;   // colonne 1
    private boolean selected;   // colonne 2
    private DateFr birth;    // colonne 3
    private String instrument; // colonne 4
    private int practice; // colonne 5 

    public EnrolmentWishGroupCourseLine(Person student) {
        this.student = student;
    }

    public Person getStudent() {
        return student;
    }

    public void setStudent(Person student) {
        this.student = student;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public DateFr getBirthDate() {
        return birth;
    }

    public void setBirthDate(DateFr birth) {
        this.birth = birth;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public int getPractice() {
        return practice;
    }

    public void setPractice(int practice) {
        this.practice = practice;
    }

    public EnrolmentWish getWish() {
        return wish;
    }

    public void setWish(EnrolmentWish wish) {
        this.wish = wish;
    }


}
