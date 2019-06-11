package net.algem.planning.dispatch.model;

import net.algem.contact.Person;
import net.algem.planning.Schedule;

import java.util.List;

public class ScheduleDispatchEvent {
    private final SubscribeAction action;
    private final Person person;
    private final Schedule schedule;

    public ScheduleDispatchEvent(SubscribeAction action, Person person, Schedule schedule) {
        this.action = action;
        this.person = person;
        this.schedule = schedule;
    }

    public SubscribeAction getAction() {
        return action;
    }

    public Person getPerson() {
        return person;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleDispatchEvent that = (ScheduleDispatchEvent) o;

        if (action != that.action) return false;
        if (person != null ? !person.equals(that.person) : that.person != null) return false;
        return !(schedule != null ? !schedule.equals(that.schedule) : that.schedule != null);

    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (person != null ? person.hashCode() : 0);
        result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScheduleDispatchEvent{" +
                "action=" + action +
                ", person=" + person +
                ", schedule=" + schedule +
                '}';
    }

    public static List<ScheduleDispatchEvent> compress(List<ScheduleDispatchEvent> events) {
        //TODO implement compression algorithm
        return events;
    }
}
