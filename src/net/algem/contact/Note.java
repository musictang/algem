/*
 * @(#)Note.java	2.9.4.14 04/01/16
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
package net.algem.contact;

import java.util.Objects;
import net.algem.util.model.GemModel;

/**
 * comment.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 */
public class Note
        implements GemModel {

    private static final long serialVersionUID = -2510125799305049998L;

    private int id;
    private int idper;
    private String text;
    private short ptype;

    public Note() {
    }

    public Note(String t) {
        text = t;
    }

    public Note(int _idper, String t, short ptype) {
        this(t);
        this.idper = _idper;
        this.ptype = ptype;
    }

    public Note(int id, int _idper, String t, short ptype) {
        this(t);
        this.id = id;
        this.idper = _idper;
        this.ptype = ptype;
    }

    @Override
    public String toString() {
        return id + " " + text + " " + ptype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Note n = (Note) o;
        return text.equals(n.text);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.idper;
        hash = 59 * hash + Objects.hashCode(this.text);
        hash = 59 * hash + this.ptype;
        return hash;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int i) {
        id = i;
    }

    public int getIdPer() {
        return idper;
    }

    public void setIdPer(int i) {
        idper = i;
    }

    public String getText() {
        return text;
    }

    public void setText(String l) {
        text = l;
    }

    public short getPtype() {
        return ptype;
    }

    public void setPtype(short ptype) {
        this.ptype = ptype;
    }

}
