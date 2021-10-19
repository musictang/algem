package net.algem.planning.fact.ui;

import net.algem.contact.teacher.Teacher;
import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.fact.services.PlanningFact;
import net.algem.planning.fact.services.PlanningFactService;
import net.algem.planning.fact.services.ReplanifyCommand;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.Option;
import net.algem.util.StringUtils;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SQLErrorDlg;
import net.algem.util.ui.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.GemLogger;

public class ReplanifyCtrl implements ReplanifyDialog.ControllerCallbacks {
    private final GemDesktop desktop;
    private final Schedule schedule;
    private final PlanningFactService planningFactService;

    public ReplanifyCtrl(GemDesktop desktop, Schedule schedule) {
        this.desktop = desktop;
        this.schedule = schedule;
        this.planningFactService = desktop.getDataCache().getPlanningFactService();
    }

    public void run() {
        ReplanifyDialog dialog = new ReplanifyDialog(this, schedule);
        dialog.setLocationRelativeTo(desktop.getFrame());
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override
    public Option<String> validateCommand(ReplanifyCommand cmd) {
        try {
            planningFactService.checkRoom(cmd);
        } catch (Exception e) {
            return Option.of("Veuillez choisir une nouvelle salle");
        }
        try {
            planningFactService.checkConflict(cmd);
        } catch (PlanningFactService.ConflictException e) {
            return Option.of("Conflit avec un planning existant :\n" + reprSchedule(e.getSchedule()));
        }
        return Option.none();
    }

    @Override
    public String getMessageForReplanifyCommand(ReplanifyCommand cmd) {
        try {
            List<PlanningFact> replanifyFacts = planningFactService.createReplanifyFacts(cmd, "");
            List<String> parts = new ArrayList<>();
            for (PlanningFact replanifyFact : replanifyFacts) {
                StringBuilder sb = new StringBuilder();
                switch (replanifyFact.getType()) {
                    case ABSENCE:
                        sb.append("Une absence sera historisé pour");
                        break;
                    case RATTRAPAGE:
                        sb.append("Un rattrapage sera historisé pour");
                        break;
                    case REMPLACEMENT:
                        sb.append("Un remplacement sera historisé pour");
                        break;
                }
                Teacher teacher = (Teacher) DataCache.findId(replanifyFact.getProf(), Model.Teacher);
                sb.append(" ");
                sb.append(teacher);
                String date = new SimpleDateFormat("dd/MM/yyyy").format(replanifyFact.getDate());
                sb.append(" (");
                sb.append(date);
                sb.append(")");
                parts.add(sb.toString());
            }
            return StringUtils.join(parts, "\n");
        } catch (SQLException e) {
            GemLogger.logException(e);
        }
        return "";
    }

    @Override
    public boolean onReplanifyCommandSelected(ReplanifyCommand cmd, String comment) {

        try {
            planningFactService.replanify(cmd, comment);
            Toast.showToast(desktop, "La replanification a bien été effectuée");
            DateFr date = schedule.getDate();
            desktop.postEvent(new ModifPlanEvent(this, date, cmd.getDate().getOrElse(date)));
            return true;
        } catch (Exception e) {
            SQLErrorDlg.displayException(desktop.getFrame(), "Erreur durant la replanification", e);
        }
        return false;
    }


    private static String reprSchedule(Schedule s) {
        try {
            Room room = (Room) DataCache.findId(s.getIdRoom(), Model.Room);
            Teacher teacher = (Teacher) DataCache.findId(s.getIdPerson(), Model.Teacher);
            return String.format("Salle %s / %s / %s - %s", room, teacher, s.getStart(), s.getEnd());
        } catch (SQLException e) {
            GemLogger.logException(e);
            return null;
        }
    }
}
