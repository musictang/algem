/*
 * @(#)EnrolmentCurrent.java	2.17.0 16/03/19
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

import net.algem.contact.Person;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */

public class EnrolmentCurrent {

    private static final long serialVersionUID = 1L;
    private Hour duration;
    private String libelle;
    private Person student;
    private boolean collectif;

    public EnrolmentCurrent() {
    }
    
    public EnrolmentCurrent(Hour duration) {
        this.duration = duration;
    }

    public EnrolmentCurrent(Hour duration, String libelle, boolean collectif) {
        this(duration);
        this.libelle = libelle;
        this.collectif = collectif;
    }

    public Hour getDuration() {
        return duration;
    }

    public void setDuration(Hour duration) {
        this.duration = duration;
    }

    public Person getStudent() {
        return student;
    }

    public void setStudent(Person student) {
        this.student = student;
    }

    public String getLibelle() {
        if (collectif) {
            return libelle;
        } else if (student != null) {
            return student.toString();
        }
        return "";
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public boolean isCollectif() {
        return collectif;
    }

    public void setCollectif(boolean collectif) {
        this.collectif = collectif;
    }

    @Override
    public String toString() {
        return "EnrolmentCurrent{" + "duration=" + duration + ", libelle=" + libelle +", student=" + student + ", collectif=" + collectif + '}';
    }

}
