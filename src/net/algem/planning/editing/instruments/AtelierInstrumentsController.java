package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.contact.Person;
import net.algem.planning.Action;
import net.algem.planning.ReloadDetailEvent;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;


public class AtelierInstrumentsController implements AtelierInstrumentsDialog.Callback {
    private final GemDesktop desktop;
    private final Action courseAction;
    private final AtelierInstrumentsService service;

    public AtelierInstrumentsController(GemDesktop desktop, Action courseAction) {
        this.desktop = desktop;
        this.courseAction = courseAction;
        this.service = DataCache.getInitializedInstance().getAtelierInstrumentsService();
    }

    public void run() {
        AtelierInstrumentsDialog dialog = new AtelierInstrumentsDialog(service, this);
        dialog.setLocationRelativeTo(desktop.getFrame());
        dialog.pack();
        try {
            dialog.setData(service.getInstrumentsAllocation(courseAction));
        } catch (Exception e) {
            GemLogger.logException(e);
        }
        dialog.setVisible(true);
    }

    @Override
    public void onOkSelected(List<AtelierInstrumentsService.PersonInstrumentRow> rows) {
        try {
            service.setInstrumentsAllocation(courseAction, rows);
            desktop.postEvent(new ReloadDetailEvent(this));
        } catch (Exception e) {
            GemLogger.logException(e);
        }
    }
}
