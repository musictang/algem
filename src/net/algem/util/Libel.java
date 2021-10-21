/*
 * @(#)Libel.java	2.9.4.13 05/11/15
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

import java.util.Objects;

/**
 * Libel object model. A bank is a person of type
 * {@link net.algem.contact.Person#BANK}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Libel
        implements java.io.Serializable {

    private static final long serialVersionUID = 626018583577705700L;

    private int id;

    private String code;
    private String libelle;

    public Libel() {
    }

    public Libel(String _code, String _libelle) {
        code = _code;
        libelle = _libelle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Libel b = (Libel) o;
        return (code.equals(b.code)
                && libelle.equals(b.libelle));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.code);
        hash = 67 * hash + Objects.hashCode(this.libelle);
        return hash;
    }

    @Override
    public String toString() {
        return code + " " + libelle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String s) {
        code = s;
    }

    public String getCode() {
        return code;
    }

    public void setLibelle(String s) {
        libelle = s;
    }

    public String getLibelle() {
        return libelle;
    }

    public boolean isValid() {
        return code.length() > 5 && libelle.length() > 2;
    }

}
