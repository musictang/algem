package net.algem.planning.fact;

import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.util.DataConnection;

/**
 * Service class, to perform auditable operation on planning (schedule) instances.
 * Each operation will modify the state of the planning, and stores one or serveral facts according to the operation
 */
public class PlanningFactService {
    private final DataConnection dc;
    private final PlanningService planningService;
    private final PlanningFactDAO planningFactDAO;
    private final PlanningFactCreator factCreator;

    public PlanningFactService(DataConnection dc, PlanningService planningService, PlanningFactDAO planningFactDAO, PlanningFactCreator factCreator) {
        this.dc = dc;
        this.planningService = planningService;
        this.planningFactDAO = planningFactDAO;
        this.factCreator = factCreator;
    }

    /**
     * Move a schedule in a catch up room, and save an ABSENCE for the related prof.
     *
     * @param schedule the schedule to
     * @param room the catchup room
     * @param commentaire user comment
     * @throws Exception
     */
    public void scheduleCatchUp(final Schedule schedule, final Room room, final String commentaire) throws Exception {
        if (!room.isCatchingUp()) throw new IllegalArgumentException("Room " + room + " is not for catching up");

        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                PlanningFact planningFact = factCreator.createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, commentaire);
                planningService.changeRoom(schedule.getId(), room.getId());
                planningFactDAO.insert(planningFact);
                return null;
            }
        });
    }

    public void scheduleDeletedLowActivity(final Schedule schedule, final String commentaire) throws Exception {
        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                PlanningFact planningFact = factCreator.createFactForPlanning(schedule, PlanningFact.Type.ACTIVITE_BAISSE, commentaire);
                planningService.deleteSchedule(schedule);
                planningFactDAO.insert(planningFact);
                return null;
            }
        });
    }
}
