package net.algem.planning.fact.services;

import net.algem.planning.Action;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

import java.sql.SQLException;
import java.util.Date;

/**
 * Factory class, to help create a planning fact, based on a planning
 */
public class PlanningFactCreator {
    public PlanningFact createFactForPlanning(Schedule schedule, int idProf, PlanningFact.Type type, String commentaire) throws SQLException {
        Action action = (Action) DataCache.findId(schedule.getIdAction(), Model.Action);
        if (action == null) {
            throw new IllegalArgumentException("Schedule " + schedule + " has no action");
        }
        return new PlanningFact(
                new Date(),
                type,
                schedule.getId(),
                idProf,
                commentaire,
                schedule.getStart().getLength(schedule.getEnd()),
                action.getStatus().getId(),
                action.getLevel().getId(),
                schedule.toString());
    }

    public PlanningFact createFactForPlanning(Schedule schedule, PlanningFact.Type type, String commentaire) throws SQLException {
        return createFactForPlanning(schedule, schedule.getIdPerson(), type, commentaire);
    }
}
