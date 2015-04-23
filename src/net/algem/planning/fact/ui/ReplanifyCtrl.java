package net.algem.planning.fact.ui;

import net.algem.contact.teacher.Teacher;
import net.algem.planning.Schedule;
import net.algem.planning.fact.services.PlanningFact;
import net.algem.planning.fact.services.PlanningFactService;
import net.algem.planning.fact.services.ReplanifyCommand;
import net.algem.util.DataCache;
import net.algem.util.StringUtils;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                parts.add(sb.toString());
            }
            return StringUtils.join(parts, "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onReplanifyCommandSelected(ReplanifyCommand cmd) {
        //TODO implement this
    }
}
