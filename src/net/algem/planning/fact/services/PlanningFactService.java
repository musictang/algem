package net.algem.planning.fact.services;

import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.planning.Schedule;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.StringUtils;
import net.algem.util.model.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
     * @param schedule the schedule to move in catchup room
     * @param room the catchup room
     * @param commentaire user comment
     * @throws Exception
     */
    public void scheduleCatchUp(final Schedule schedule, final Room room, final String commentaire) throws Exception {
        if (!room.isCatchingUp()) throw new IllegalArgumentException("Room " + room + " is not for catching up");
        Room currentRoom = (Room) DataCache.findId(schedule.getIdRoom(), Model.Room);
        if (currentRoom != null && currentRoom.isCatchingUp())
            throw new IllegalArgumentException("Schedule " + schedule + " is already catching up");

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

    /**
     * Delete a schedule, and save a ACTIVITE_BAISSE fact for this event.
     * @param schedule the schedule to delete
     * @param commentaire user comment
     * @throws Exception
     */
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

    public boolean isInAbsence(Schedule schedule) throws SQLException {
        Room room = (Room) DataCache.findId(schedule.getIdRoom(), Model.Room);
        return room != null && room.isCatchingUp();
    }

    /**
     * Replanify a schedule, according to the replanification command.
     * Absence, catchup and replacement facts, will be automatically created from the command,
     * according to the <a href="https://trello.com/c/3WmPBMdy">specification</a>
     *
     * @param cmd a command that describes which part
     * @param comment user
     * @throws Exception
     */
    public void replanify(final ReplanifyCommand cmd, final String comment) throws Exception {
        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                checkRoom(cmd);
                List<PlanningFact> facts = createReplanifyFacts(cmd, comment);
                for (PlanningFact fact : facts) {
                    planningFactDAO.insert(fact);
                }
                executeReplanifyUpdate(cmd);
                return null;
            }
        });
    }

    private List<PlanningFact> createReplanifyFacts(ReplanifyCommand cmd, String comment) throws SQLException {
        List<PlanningFact> facts = new ArrayList<>();
        Schedule schedule = cmd.getSchedule();
        boolean scheduleInAbsence = isInAbsence(schedule);

        if (cmd.getProfId().isPresent()) {
            if (!scheduleInAbsence) {
                facts.add(factCreator.createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, comment));
            }
            facts.add(factCreator.createFactForPlanning(schedule, cmd.getProfId().get(), PlanningFact.Type.REMPLACEMENT, comment));
        }
        else if (cmd.getDate().isPresent()) {
            if (!scheduleInAbsence) {
                facts.add(factCreator.createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, comment));
            }
            facts.add(factCreator.createFactForPlanning(schedule, PlanningFact.Type.RATTRAPAGE, comment));
        }
        return facts;
    }


    private void checkRoom(ReplanifyCommand cmd) throws SQLException {
        for (int salle : cmd.getRoomId()) {
            Room room = (Room) DataCache.findId(salle, Model.Room);
            if (room != null && room.isCatchingUp()) {
                throw new IllegalArgumentException("Cannot replanify to a catching up room");
            }
        }
    }

    private void executeReplanifyUpdate(ReplanifyCommand cmd) throws SQLException {
        List<String> updates = new ArrayList<>();
        for (int prof : cmd.getProfId()) {
            updates.add("prof = " + prof);
        }
        for (DateFr dateFr : cmd.getDate()) {
            updates.add("date = " + dateFr);
        }
        for (Hour hour : cmd.getStartHour()) {
            int durationMinutes = cmd.getSchedule().getStart().getLength(cmd.getSchedule().getEnd());
            updates.add("debut = " + hour);
            Hour endHour = new Hour(hour);
            endHour.incMinute(durationMinutes);
            updates.add("fin = " + endHour);
        }
        for (int salle : cmd.getRoomId()) {
            updates.add("lieux = " + salle);
        }

        String setPart = StringUtils.join(updates, ", ");
        dc.executeQuery("UPDATE planning SET " + setPart + " WHERE id = " + cmd.getSchedule().getId());
    }
}
