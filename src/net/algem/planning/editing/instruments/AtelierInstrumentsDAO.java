/*
 * @(#)AtelierInstrumentsDAO.java 2.9.3 23/02/15
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

import net.algem.util.DataConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import net.algem.contact.PersonIO;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.OrderIO;
import net.algem.planning.ActionIO;

/**
 * 
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3
 * @since 2.9.2
 */
public class AtelierInstrumentsDAO {

    public static final String TABLE = "atelier_instruments";
    public static final String COLUMNS = "idaction, idper, idinstru";
    private final DataConnection dc;

    public AtelierInstrumentsDAO(DataConnection dc) {
        this.dc = dc;
    }

    public AtelierInstrument find(int idAction, int idPerson) throws SQLException {
        String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE idaction = " + idAction + " AND idper = " + idPerson;
        ResultSet resultSet = dc.executeQuery(query);
        if (resultSet.next()) {
            return new AtelierInstrument(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3));
        } else return null;
    }

    public void save(AtelierInstrument a) throws SQLException {
        AtelierInstrument existing = find(a.getIdAction(), a.getIdPerson());
        if (existing != null) {
            delete(a.getIdAction(), a.getIdPerson());
        }
        dc.executeUpdate(format("INSERT INTO %s VALUES (%d, %d, %d)",
                TABLE, a.getIdAction(), a.getIdPerson(), a.getIdInstrument()));
    }

    public void delete(int idAction, int idPerson) throws SQLException {
        dc.executeUpdate(format("DELETE FROM %s WHERE idaction = %d AND idper = %d", TABLE, idAction, idPerson));
    }

    public void delete(AtelierInstrument atelierInstrument) throws SQLException {
        delete(atelierInstrument.getIdAction(), atelierInstrument.getIdPerson());
    }




    public List<Integer> getInstrumentIdsForPerson(int idPerson) throws SQLException {
        String query = format("SELECT DISTINCT ON(instrument) instrument,idx FROM person_instrument WHERE idper = %d ORDER BY instrument,idx", idPerson);
        List<Integer> result = new ArrayList<>();
        ResultSet resultSet = dc.executeQuery(query);
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }
}
