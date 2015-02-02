/*
 * @(#)AtelierInstrumentsServiceImpl.java 2.9.2 02/02/15
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

import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.planning.Action;
import net.algem.util.DataConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.9.2
 */
public class AtelierInstrumentsServiceImpl
        implements AtelierInstrumentsService
{

  private final DataConnection dc;
  private final AtelierInstrumentsDAO atelierInstrumentsDAO;
  private final PersonIO personIO;

  public AtelierInstrumentsServiceImpl(DataConnection dc, AtelierInstrumentsDAO atelierInstrumentsDAO, PersonIO personIO) {
    this.dc = dc;
    this.atelierInstrumentsDAO = atelierInstrumentsDAO;
    this.personIO = personIO;
  }

  private List<Person> getPersonsForAction(Action action) throws Exception {
    List<Integer> ids = atelierInstrumentsDAO.getPersonsIdsForAction(action.getId());
    List<Person> result = new ArrayList<>(ids.size());
    for (Integer id : ids) {
      result.add(personIO.findId(id));
    }
    return result;
  }

  private static boolean isEmptyAllocation(List<PersonInstrumentRow> rows) {
    for (PersonInstrumentRow row : rows) {
      if (row.instrument != null) {
        return false;
      }
    }
    return true;
  }

  private Instrument getFirstInstrument(Person person) throws SQLException {
    for (Instrument instrument : getAvailableInstruments(person)) {
      return instrument;
    }
    return null;
  }

  @Override
  public List<PersonInstrumentRow> getInstrumentsAllocation(final Action action) throws Exception {
    return dc.withTransaction(new DataConnection.SQLRunnable<List<PersonInstrumentRow>>()
    {
      @Override
      public List<PersonInstrumentRow> run(DataConnection conn) throws Exception {
        List<Person> persons = getPersonsForAction(action);
        List<PersonInstrumentRow> rows = new ArrayList<>(persons.size());
        for (Person person : persons) {
          PersonInstrumentRow row;
          AtelierInstrument atelierInstrument = atelierInstrumentsDAO.find(action.getId(), person.getId());
          if (atelierInstrument != null) {
            //Instrument instrument = InstrumentIO.findId(atelierInstrument.getIdInstrument(), dc);
            Instrument instrument = (Instrument) DataCache.findId(atelierInstrument.getIdInstrument(), Model.Instrument);
            row = new PersonInstrumentRow(person, instrument);
          } else {
            row = new PersonInstrumentRow(person, null);
          }
          rows.add(row);
        }

        //In case of an empty allocation (usually the first time the allocation is fetched), try
        //to populate with primary instruments
        if (isEmptyAllocation(rows)) {
          for (PersonInstrumentRow row : rows) {
            row.instrument = getFirstInstrument(row.person);
          }
        }
        return rows;
      }
    });

  }

  @Override
  public void setInstrumentsAllocation(final Action action, final List<PersonInstrumentRow> rows) throws Exception {
    dc.withTransaction(new DataConnection.SQLRunnable<Void>()
    {
      @Override
      public Void run(DataConnection conn) throws Exception {
        for (PersonInstrumentRow row : rows) {
          if (row.instrument != null) {
            atelierInstrumentsDAO.save(new AtelierInstrument(action.getId(), row.person.getId(), row.instrument.getId()));
          } else {
            atelierInstrumentsDAO.delete(action.getId(), row.person.getId());
          }
        }
        return null;
      }
    });

  }

  @Override
  public List<Instrument> getAvailableInstruments(Person person) throws SQLException {
    List<Integer> ids = atelierInstrumentsDAO.getInstrumentIdsForPerson(person.getId());
    List<Instrument> instruments = new ArrayList<>(ids.size());
    for (Integer id : ids) {
      //instruments.add(InstrumentIO.findId(id, dc));
      instruments.add((Instrument) DataCache.findId(id, Model.Instrument));
    }
    return instruments;
  }

  @Override
  public Instrument getAllocatedInstrument(Action action, Person person) throws Exception {
    return getAllocatedInstrument(action.getId(), person.getId());
  }

  @Override
  public Instrument getAllocatedInstrument(int actionId, int personId) throws Exception {
    AtelierInstrument atelierInstrument = atelierInstrumentsDAO.find(actionId, personId);
    if (atelierInstrument != null) {
      //return InstrumentIO.findId(atelierInstrument.getIdInstrument(), dc);
      return (Instrument) DataCache.findId(atelierInstrument.getIdInstrument(), Model.Instrument);
    } else {
      List<Integer> ids = atelierInstrumentsDAO.getInstrumentIdsForPerson(personId);
      if (ids.size() > 0) {
        //return InstrumentIO.findId(ids.get(0), dc);
        return (Instrument) DataCache.findId(ids.get(0), Model.Instrument);
      }
    }
    return null;
  }
}
