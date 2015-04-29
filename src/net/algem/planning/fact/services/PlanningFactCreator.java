package net.algem.planning.fact.services;

import net.algem.planning.Action;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Factory class, to help create a planning fact, based on a planning
 */
public class PlanningFactCreator {
    public PlanningFact createFactForPlanning(Schedule schedule, int idProf, Date factDate, PlanningFact.Type type, String commentaire) throws SQLException {
        Action action = (Action) DataCache.findId(schedule.getIdAction(), Model.Action);
        if (action == null) {
            throw new IllegalArgumentException("Schedule " + schedule + " has no action");
        }
        return new PlanningFact(
                factDate,
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
        return createFactForPlanning(schedule, schedule.getIdPerson(), dateForSchedule(schedule), type, commentaire);
    }

    public static Date dateForSchedule(DateFr dateFr, Hour hour) {
        GregorianCalendar cal = new GregorianCalendar();
        Date date;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(dateFr.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hour.getHour());
        cal.add(Calendar.MINUTE, hour.getMinute());
        return cal.getTime();
    }

    public static Date dateForSchedule(Schedule schedule) {
        return dateForSchedule(schedule.getDate(), schedule.getStart());
    }

}
