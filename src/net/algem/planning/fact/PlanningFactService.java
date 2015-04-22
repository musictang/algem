package net.algem.planning.fact;

import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.GemModel;
import net.algem.util.model.Model;

import java.sql.SQLException;
import java.util.Date;

public class PlanningFactService {
    private final DataConnection dc;
    private final PlanningService planningService;

    public PlanningFactService(DataConnection dc, PlanningService planningService) {
        this.dc = dc;
        this.planningService = planningService;
    }

    public void scheduleCatchUp(final Schedule schedule, final Room room, final String commentaire) throws Exception {
        if (!room.isCatchingUp()) throw new IllegalArgumentException("Room " + room + " is not for catching up");

        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                PlanningFact planningFact = createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, commentaire);
                planningService.changeRoom(schedule.getId(), room.getId());
                PlanningFactIO.insert(planningFact, conn);
                return null;
            }
        });
    }

    public void scheduleDeletedLowActivity(final Schedule schedule, final String commentaire) throws Exception {
        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                PlanningFact planningFact = createFactForPlanning(schedule, PlanningFact.Type.ACTIVITE_BAISSE, commentaire);
                planningService.deleteSchedule(schedule);
                PlanningFactIO.insert(planningFact, conn);
                return null;
            }
        });
    }


    private PlanningFact createFactForPlanning(Schedule schedule, PlanningFact.Type type, String commentaire) throws SQLException {
        Action action = (Action) DataCache.findId(schedule.getId(), Model.Action);
        if (action == null) {
            throw new IllegalArgumentException("Schedule " + schedule + " has no action");
        }
        return new PlanningFact(
                new Date(),
                type,
                schedule.getId(),
                schedule.getIdPerson(),
                commentaire,
                schedule.getStart().getLength(schedule.getEnd()),
                action.getStatus().getId(),
                action.getLevel().getId()
        );
    }
}
