package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.contact.Person;
import net.algem.planning.Action;
import net.algem.util.module.GemDesktop;

import java.util.Arrays;
import java.util.List;


public class AtelierInstrumentsController {
    private final GemDesktop desktop;
    private final Action courseAction;
    private final AtelierInstrumentsService service;

    public AtelierInstrumentsController(GemDesktop desktop, Action courseAction) {
        this.desktop = desktop;
        this.courseAction = courseAction;
        this.service = new AtelierInstrumentsService() {
            @Override
            public List<PersonInstrumentRow> getInstrumentsAllocation(Action action) {
                return Arrays.asList(
                        new PersonInstrumentRow(
                                new Person(12, "nom", "prenom", "m"),
                                new Instrument(45, "Guitare")
                        ),
                        new PersonInstrumentRow(
                                new Person(12, "nom 2", "prenom 2", "m"),
                                new Instrument(56, "Basse")
                        ),
                        new PersonInstrumentRow(
                                new Person(12, "nom 2", "prenom 2", "m"),
                                null
                        )
                );
            }

            @Override
            public void setInstrumentsAllocation(Action action, List<PersonInstrumentRow> rows) {

            }

            @Override
            public List<Instrument> getAvailableInstruments(Person person) {
                return Arrays.asList(
                        new Instrument(45, "Guitare"),
                        new Instrument(56, "Basse")
                );
            }
        };
    }

    public void run() {
        AtelierInstrumentsDialog dialog = new AtelierInstrumentsDialog(service);
        dialog.setLocationRelativeTo(desktop.getFrame());
        dialog.pack();
        dialog.setData(service.getInstrumentsAllocation(courseAction));
        dialog.setVisible(true);
    }
}
