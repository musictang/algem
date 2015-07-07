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

public class ActivitySupCtrl {
    private final GemDesktop desktop;
    private final Schedule schedule;
    private final PlanningFactService planningFactService;

    public ActivitySupCtrl(GemDesktop desktop, Schedule schedule) {
        this.desktop = desktop;
        this.schedule = schedule;
        this.planningFactService = desktop.getDataCache().getPlanningFactService();
    }

    public void run() {
        boolean confirmation = MessagePopup.confirm(desktop.getFrame(), "<html><b>Voulez vous marquer ce planning comme une activité supplémentaire ?</b>\n" +
                "Un événement de hausse d'activité sera enregistré.", "Confirmation");

        if (confirmation) {
            try {
                planningFactService.markAsAdditionalActivity(schedule, "");
                Toast.showToast(desktop, "Hausse d'activité enregistrée");
            } catch (Exception e) {
                SQLErrorDlg.displayException(desktop.getFrame(), "Erreur", e);
                GemLogger.logException(e);
            }
        }
    }
}
