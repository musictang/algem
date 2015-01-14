package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.Person;
import net.algem.planning.Action;
import net.algem.util.DataConnection;

import java.util.List;

public class AtelierInstrumentsServiceImpl implements AtelierInstrumentsService {
    private final DataConnection dc;

    public AtelierInstrumentsServiceImpl(DataConnection dc) {
        this.dc = dc;
    }

    private List<Instrument> getInstrumentsForPerson(Person person) {
        return null; //TODO;
    }

    private List<Person> getPersonsForAction(Action action) {
        return null; //TODO
    }

    @Override
    public List<PersonInstrumentRow> getInstrumentsAllocation(Action action) {
        List<Person> persons = getPersonsForAction(action);
        for (Person person : persons) {
        }

        return null;
    }

    @Override
    public void setInstrumentsAllocation(Action action, List<PersonInstrumentRow> rows) {

    }

    @Override
    public List<Instrument> getAvailableInstruments(Person person) {
        return null;
    }
}
