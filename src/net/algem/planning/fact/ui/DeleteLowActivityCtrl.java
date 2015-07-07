package net.algem.planning.fact.ui;

import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.fact.services.PlanningFactService;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SQLErrorDlg;
import net.algem.util.ui.Toast;

public class DeleteLowActivityCtrl {
    private final GemDesktop desktop;
    private final Schedule schedule;
    private final PlanningFactService planningFactService;

    public DeleteLowActivityCtrl(GemDesktop desktop, Schedule schedule) {
        this.desktop = desktop;
        this.schedule = schedule;
        this.planningFactService = desktop.getDataCache().getPlanningFactService();
    }

    public void run() {
        boolean confirmation = MessagePopup.confirm(desktop.getFrame(), "<html><b>Voulez vous supprimer ce planning ?</b>\n" +
                "Un événement de baisse d'activité sera enregistré.", "Confirmation de suppression");

        if (confirmation) {
            try {
                planningFactService.scheduleDeletedLowActivity(schedule, "");
                DateFr date = schedule.getDate();
                desktop.postEvent(new ModifPlanEvent(this, date, date));
                Toast.showToast(desktop, "Planning supprimé");
            } catch (Exception e) {
                SQLErrorDlg.displayException(desktop.getFrame(), "Erreur", e);
                GemLogger.logException(e);
            }
        }
    }
}
