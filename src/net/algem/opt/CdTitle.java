/*
 * @(#)CdTitle.java	2.9.4.13 05/11/15
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
package net.algem.opt;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class CdTitle
        implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int cd;
    private int number;
    private String title;
    private String performer;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CdTitle c = (CdTitle) o;
        return (cd == c.cd
                && title.equals(c.title)
                && performer.equals(c.performer));
    }

    @Override
    public String toString() {
        return number + " " + title + " " + performer;
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getTitre() {
        return title;
    }

    public void setPerformer(String s) {
        performer = s;
    }

    public String getPerformer() {
        return performer;
    }

    public void setCd(int i) {
        cd = i;
    }

    public int getCd() {
        return cd;
    }

    public void setNumber(int i) {
        number = i;
    }

    public int getNumber() {
        return number;
    }
}
