package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.planning.Action;
import net.algem.util.DataConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AtelierInstrumentsServiceImpl implements AtelierInstrumentsService {
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
            if (row.instrument != null) return false;
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
        return dc.withTransaction(new DataConnection.SQLRunnable<List<PersonInstrumentRow>>() {
            @Override
            public List<PersonInstrumentRow> run(DataConnection conn) throws Exception {
                List<Person> persons = getPersonsForAction(action);
                List<PersonInstrumentRow> rows = new ArrayList<>(persons.size());
                for (Person person : persons) {
                    PersonInstrumentRow row;
                    AtelierInstrument atelierInstrument = atelierInstrumentsDAO.find(action.getId(), person.getId());
                    if (atelierInstrument != null) {
                        Instrument instrument = InstrumentIO.findId(atelierInstrument.getIdInstrument(), dc);
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
        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
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
            instruments.add(InstrumentIO.findId(id, dc));
        }
        return instruments;
    }
}
