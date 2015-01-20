package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.Person;
import net.algem.planning.Action;
import net.algem.util.DataConnection;

import java.util.ArrayList;
import java.util.List;

public class AtelierInstrumentsServiceImpl implements AtelierInstrumentsService {
    private final DataConnection dc;
    private final AtelierInstrumentsDAO atelierInstrumentsDAO;
    private final InstrumentIO instrumentIO;

    public AtelierInstrumentsServiceImpl(DataConnection dc, AtelierInstrumentsDAO atelierInstrumentsDAO, InstrumentIO instrumentIO) {
        this.dc = dc;
        this.atelierInstrumentsDAO = atelierInstrumentsDAO;
        this.instrumentIO = instrumentIO;
    }

    private List<Instrument> getInstrumentsForPerson(Person person) {
        return null; //TODO;
    }

    private List<Person> getPersonsForAction(Action action) {
        return null; //TODO
    }

    @Override
    public List<PersonInstrumentRow> getInstrumentsAllocation(Action action) throws Exception {
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
        return rows;
    }

    @Override
    public void setInstrumentsAllocation(Action action, List<PersonInstrumentRow> rows) {

    }

    @Override
    public List<Instrument> getAvailableInstruments(Person person) {
        return null;
    }
}
