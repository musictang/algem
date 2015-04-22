package net.algem.planning.fact.services;

import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.util.Option;

import java.util.Iterator;

public class ReplanifyCommand {

    private final Schedule schedule;
    private final Option<Integer> profId;
    private final Option<Integer> roomId;
    private final Option<DateFr> date;
    private final Option<Hour> startHour;

    public ReplanifyCommand(Schedule schedule, Option<Integer> profId, Option<Integer> roomId, Option<DateFr> date, Option<Hour> startHour) {
        this.schedule = schedule;
        this.profId = profId;
        this.roomId = roomId;
        this.date = date;
        this.startHour = startHour;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Option<Integer> getProfId() {
        return profId;
    }

    public Option<Integer> getRoomId() {
        return roomId;
    }

    public Option<DateFr> getDate() {
        return date;
    }

    public Option<Hour> getStartHour() {
        return startHour;
    }
}

