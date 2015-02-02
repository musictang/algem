/*
 * @(#)AtelierInstrument.java 2.9.2 02/02/15
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

package net.algem.planning.editing.instruments;

/**
 * 
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @version 2.9.2
 * @since 2.9.2
 */
class AtelierInstrument {
    private int idAction;
    private int idPerson;
    private int idInstrument;

    public AtelierInstrument(int idAction, int idPerson, int idInstrument) {
        this.idAction = idAction;
        this.idPerson = idPerson;
        this.idInstrument = idInstrument;
    }

    public int getIdAction() {
        return idAction;
    }

    public void setIdAction(int idAction) {
        this.idAction = idAction;
    }

    public int getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    public int getIdInstrument() {
        return idInstrument;
    }

    public void setIdInstrument(int idInstrument) {
        this.idInstrument = idInstrument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtelierInstrument that = (AtelierInstrument) o;

        if (idAction != that.idAction) return false;
        if (idInstrument != that.idInstrument) return false;
        if (idPerson != that.idPerson) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idAction;
        result = 31 * result + idPerson;
        result = 31 * result + idInstrument;
        return result;
    }

    @Override
    public String toString() {
        return "AtelierInstrument{" +
                "idAction=" + idAction +
                ", idPerson=" + idPerson +
                ", idInstrument=" + idInstrument +
                '}';
    }
}
