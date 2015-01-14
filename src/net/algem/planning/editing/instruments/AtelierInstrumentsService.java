package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.contact.Person;
import net.algem.planning.Action;

import java.util.List;
import java.util.Map;

public interface AtelierInstrumentsService {
    static class PersonInstrumentRow {
        public final Person person;
        public Instrument instrument;

        public PersonInstrumentRow(Person person, Instrument instrument) {
            this.person = person;
            this.instrument = instrument;
        }
    }

    public List<PersonInstrumentRow> getInstrumentsAllocation(Action action);
    public void setInstrumentsAllocation(Action action, List<PersonInstrumentRow> rows);
    public List<Instrument> getAvailableInstruments(Person person);
}
