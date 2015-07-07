package net.algem.planning.fact.ui;

import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.fact.services.PlanningFactService;
import net.algem.room.Room;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SQLErrorDlg;
import net.algem.util.ui.Toast;

public class AbsenceToCatchUpCtrl implements AbsenceToCatchUpDialog.RoomSelectedListener {
    private final GemDesktop desktop;
    private final Schedule schedule;
    private final PlanningFactService planningFactService;

    public AbsenceToCatchUpCtrl(GemDesktop desktop, Schedule schedule) {
        this.desktop = desktop;
        this.schedule = schedule;
        this.planningFactService = desktop.getDataCache().getPlanningFactService();
    }

    public void run() {
        AbsenceToCatchUpDialog dialog = new AbsenceToCatchUpDialog(this, desktop.getDataCache());
        dialog.setLocationRelativeTo(desktop.getFrame());
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override
    public void onRoomSelected(Room room, String comment) {
        try {
            planningFactService.scheduleCatchUp(schedule, room, comment);
            DateFr date = schedule.getDate();
            desktop.postEvent(new ModifPlanEvent(this, date, date));
            Toast.showToast(desktop, "Le planning a été mis en rattrapage");
        } catch (Exception e) {
            SQLErrorDlg.displayException(desktop.getFrame(), "Erreur", e);
            GemLogger.logException(e);
        }
    }
}
