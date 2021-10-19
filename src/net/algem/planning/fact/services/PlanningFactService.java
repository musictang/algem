package net.algem.planning.fact.services;

import net.algem.planning.*;
import net.algem.planning.fact.services.PlanningFactDAO.Query;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.Option;
import net.algem.util.StringUtils;
import net.algem.util.model.Model;

import java.sql.SQLException;
import java.util.*;

/**
 * Service class, to perform auditable operation on planning (schedule) instances.
 * Each operation will modify the state of the planning, and stores one or serveral facts according to the operation
 */
public class PlanningFactService {
    private final DataConnection dc;
    private final PlanningService planningService;
    private final PlanningFactDAO planningFactDAO;
    private final PlanningFactCreator factCreator;
    private final RoomFinder roomFinder;
    private final ScheduleUpdater scheduleUpdater;
    private final SimpleConflictService conflictService;

    public PlanningFactService(DataConnection dc, PlanningService planningService, PlanningFactDAO planningFactDAO, PlanningFactCreator factCreator, RoomFinder roomFinder, ScheduleUpdater scheduleUpdater, SimpleConflictService conflictService) {
        this.dc = dc;
        this.planningService = planningService;
        this.planningFactDAO = planningFactDAO;
        this.factCreator = factCreator;
        this.roomFinder = roomFinder;
        this.scheduleUpdater = scheduleUpdater;
        this.conflictService = conflictService;
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
        Room currentRoom = roomFinder.findRoom(schedule.getIdRoom());
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
        Room room = roomFinder.findRoom(schedule.getIdRoom());
        return room != null && room.isCatchingUp();
    }

    /**
     * Replanify a schedule, according to the replanification command.
     * Absence, catchup and replacement facts, will be automatically created from the command,
     * according to the <a href="https://trello.com/c/3WmPBMdy">specification</a>
     *
     * @param cmd a command that describes which part of the schedule should be updated
     * @param comment user comment
     * @throws Exception
     */
    public void replanify(final ReplanifyCommand cmd, final String comment) throws Exception {
        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                checkRoom(cmd);
                checkConflict(cmd);
                List<PlanningFact> facts = createReplanifyFacts(cmd, comment);
                for (PlanningFact fact : facts) {
                    planningFactDAO.insert(fact);
                }
                executeReplanifyUpdate(cmd);
                return null;
            }
        });
    }


    public static class ConflictException extends Exception {
        private final Schedule schedule;

        public ConflictException(Schedule schedule) {
            super("Conflit avec " + schedule);
            this.schedule = schedule;
        }

        public Schedule getSchedule() {
            return schedule;
        }
    }

    /**
     * Checks potential conflict for a replanification
     * @param cmd the replanification command
     * @throws ConflictException if there is a conflict
     */
    public void checkConflict(ReplanifyCommand cmd) throws ConflictException {
        Schedule s = cmd.getSchedule();

        Hour startHour = cmd.getStartHour().getOrElse(s.getStart());
        int durationMinutes = cmd.getSchedule().getStart().getLength(cmd.getSchedule().getEnd());
        Hour endHour = new Hour(startHour);
        endHour.incMinute(durationMinutes);

        Option<Schedule> maybeConflict = conflictService.testConflict(s,
                cmd.getProfId().getOrElse(s.getIdPerson()),
                cmd.getRoomId().getOrElse(s.getIdRoom()),
                cmd.getDate().getOrElse(s.getDate()),
                startHour, endHour);

        //noinspection LoopStatementThatDoesntLoop
        for (Schedule schedule : maybeConflict) {
            if (schedule.isValid())
                throw new ConflictException(schedule);
        }

    }

    /**
     * Create facts (not persisted) based on a replanification command,
     * according to the <a href="https://trello.com/c/3WmPBMdy">specification</a>
     * @param cmd a command that describes which part of the schedule should be updated
     * @param comment user comment
     * @return planning facts corresponding to a replanification
     * @throws SQLException
     */
    public List<PlanningFact> createReplanifyFacts(ReplanifyCommand cmd, String comment) throws SQLException {
        List<PlanningFact> facts = new ArrayList<>();
        Schedule schedule = cmd.getSchedule();
        boolean scheduleInAbsence = isInAbsence(schedule);

        Date replanificationDate = getReplanificationDate(cmd, schedule);
        if (cmd.getProfId().isPresent()) {
            if (!scheduleInAbsence) {
                facts.add(factCreator.createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, comment));
            }
            facts.add(factCreator.createFactForPlanning(schedule, cmd.getProfId().get(), replanificationDate, PlanningFact.Type.REMPLACEMENT, comment));
        }
        else if (cmd.getDate().isPresent()) {
            if (!scheduleInAbsence) {
                facts.add(factCreator.createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, comment));
            }
            facts.add(factCreator.createFactForPlanning(schedule, schedule.getIdPerson(), replanificationDate, PlanningFact.Type.RATTRAPAGE, comment));
        } else if (cmd.getRoomId().isPresent() && scheduleInAbsence) {
            facts.add(factCreator.createFactForPlanning(schedule, schedule.getIdPerson(), replanificationDate, PlanningFact.Type.RATTRAPAGE, comment));
        }
        return facts;
    }

    private Date getReplanificationDate(ReplanifyCommand cmd, Schedule schedule) {
        return PlanningFactCreator.dateForSchedule(cmd.getDate().getOrElse(schedule.getDate()),
                cmd.getStartHour().getOrElse(schedule.getStart()));
    }


    public void checkRoom(ReplanifyCommand cmd) throws SQLException {
        int salle = cmd.getRoomId().getOrElse(cmd.getSchedule().getIdRoom());
        Room room = roomFinder.findRoom(salle);
        if (room != null && room.isCatchingUp()) {
            throw new IllegalArgumentException("Cannot replanify to a catching up room");
        }
    }

    private void executeReplanifyUpdate(ReplanifyCommand cmd) throws SQLException {
        Map<String, Object> updates = new LinkedHashMap<>();
        for (int prof : cmd.getProfId()) {
            updates.put("idper", prof);
        }
        for (DateFr dateFr : cmd.getDate()) {
            updates.put("jour", dateFr);
        }
        for (Hour hour : cmd.getStartHour()) {
            int durationMinutes = cmd.getSchedule().getStart().getLength(cmd.getSchedule().getEnd());
            updates.put("debut", hour);
            Hour endHour = new Hour(hour);
            endHour.incMinute(durationMinutes);
            updates.put("fin", endHour);
        }
        for (int salle : cmd.getRoomId()) {
            updates.put("lieux", salle);
        }

        scheduleUpdater.updateSchedule(cmd.getSchedule(), updates);
    }

    public static class ActivitySupAlreadyExistingException extends Exception {
        public ActivitySupAlreadyExistingException() {
            super("Une activité supplémentaire existe déjà pour ce planning et ce professeur");
        }
    }

    public void markAsAdditionalActivity(final Schedule schedule, final String comment) throws Exception {
        dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
            @Override
            public Void run(DataConnection conn) throws Exception {
                checkActivitySupExisting(schedule);
                PlanningFact fact = factCreator.createFactForPlanning(schedule, PlanningFact.Type.ACTIVITE_SUP, comment);
                planningFactDAO.insert(fact);
                return null;
            }
        });
    }

    public void checkActivitySupExisting(Schedule schedule) throws SQLException, ActivitySupAlreadyExistingException {
        List<PlanningFact> facts = planningFactDAO.findAsList(new Query(Option.of(schedule.getId()), Option.of(schedule.getIdPerson()),
                Option.<DateFr>none(), Option.<DateFr>none(), Option.of(PlanningFact.Type.ACTIVITE_SUP)));

        if (facts.size() > 0) {
            throw new ActivitySupAlreadyExistingException();
        }
    }

    public static class RoomFinder {
        public Room findRoom(int id) throws SQLException {
            return (Room) DataCache.findId(id, Model.Room);
        }
    }

    public static class ScheduleUpdater {
        private final DataConnection dc;

        public ScheduleUpdater(DataConnection dc) {
            this.dc = dc;
        }

        private String quoteIfNecessary(Object obj) {
            if (obj instanceof DateFr || obj instanceof Hour) {
                return "'" + obj + "'";
            }
            return obj.toString();
        }

        public void updateSchedule(Schedule schedule, Map<String, Object> updates) throws SQLException {
            List<String> parts = new ArrayList<>();
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                parts.add(entry.getKey() + " = " + quoteIfNecessary(entry.getValue()));
            }
            String setPart = StringUtils.join(parts, ", ");
            dc.executeUpdate("UPDATE planning SET " + setPart + " WHERE id = " + schedule.getId());

            if (updates.containsKey("debut")) {
                int offset = schedule.getStart().getLength((Hour) updates.get("debut"));
                String query = "UPDATE " + ScheduleRangeIO.TABLE
                        + " SET debut = debut + INTERVAL '" + offset + " min'"
                        + ", fin = (CASE WHEN fin + INTERVAL '" + offset + " min' = '00:00:00' THEN '24:00:00' ELSE fin + INTERVAL '" + offset + " min' END)"
                        + " WHERE idplanning = " + schedule.getId();

                dc.executeUpdate(query);
            }
        }


    }
}
