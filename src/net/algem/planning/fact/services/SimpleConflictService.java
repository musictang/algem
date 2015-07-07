package net.algem.planning.fact.services;

import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.DataConnection;
import net.algem.util.Option;

import java.util.Vector;

public class SimpleConflictService {

    private final DataConnection dc;

    public SimpleConflictService(DataConnection dc) {
        this.dc = dc;
    }

    /**
     * Check conflict with existing plannings, for a replanification
     * @param s the schedule, unmodified
     * @param newProf the new teacher
     * @param newRoom the new room
     * @param newDate the new date
     * @param startHour the new start hour
     * @param endHour the new end hour
     * @return the conflicting schedule if there is a conflict.
     */
    public Option<Schedule> testConflict(Schedule s, int newProf, int newRoom, DateFr newDate, Hour startHour, Hour endHour) {
        Vector<Schedule> schedules = ScheduleIO.find("WHERE jour = '" + newDate + "' AND lieux = " + newRoom + " AND id !=" + s.getId(), dc);
        for (Schedule schedule : schedules) {
            if (overlaps(startHour, endHour, schedule.getStart(), schedule.getEnd())) {
                return Option.of(schedule);
            }
        }

        schedules = ScheduleIO.find("WHERE jour = '" + newDate + "' AND idper = " + newProf + " AND id !=" + s.getId(), dc);
        for (Schedule schedule : schedules) {
            if (overlaps(startHour, endHour, schedule.getStart(), schedule.getEnd())) {
                return Option.of(schedule);
            }
        }

        return Option.none();
    }

    private boolean overlaps(Schedule schedule1, Schedule schedule2) {
        Hour s1 = schedule1.getStart();
        Hour e1 = schedule1.getEnd();
        Hour s2 = schedule2.getStart();
        Hour e2 = schedule2.getEnd();
        return overlaps(s1, e1, s2, e2);
    }

    private boolean overlaps(Hour s1, Hour e1, Hour s2, Hour e2) {
        return s2.before(e1) && s1.before(e2);
    }
}
