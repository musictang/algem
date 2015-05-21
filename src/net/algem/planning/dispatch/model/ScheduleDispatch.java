package net.algem.planning.dispatch.model;

import net.algem.contact.Person;
import net.algem.planning.Schedule;

import java.util.*;

public class ScheduleDispatch {
    private final List<Schedule> schedules;
    private final List<Person> persons;
    private final Map<Integer, Set<Integer>> dispatch;
    private final List<ScheduleDispatchEvent> events;

    public ScheduleDispatch(List<Schedule> schedules, List<Person> persons, Map<Integer, Set<Integer>> dispatch) {
        this.schedules = schedules;
        this.persons = persons;
        this.dispatch = new HashMap<>(dispatch);
        this.events = new LinkedList<>();
    }

    public void subscribe(Person person, Schedule schedule) {
        Set<Integer> schedulesId = dispatch.get(person.getId());
        if (schedulesId != null) {
            if (schedulesId.add(schedule.getId())) {
                events.add(new ScheduleDispatchEvent(SubscribeAction.SUBSCRIBE, person, schedule));
            }
        }
    }

    public void unsubscribe(Person person, Schedule schedule) {
        Set<Integer> schedulesId = dispatch.get(person.getId());
        if (schedulesId != null) {
            if (schedulesId.remove(schedule.getId())) {
                events.add(new ScheduleDispatchEvent(SubscribeAction.UNSUBSCRIBE, person, schedule));
            }
        }
    }

    public void subscribe(Person person, SubscriptionPattern pattern) {
        int index = schedules.indexOf(pattern.from);
        if (index != -1) {
            for (int i = index; i < schedules.size(); i++) {
                if ((i - index) % pattern.sessionFrequency == 0) {
                    Schedule schedule = schedules.get(i);
                    switch (pattern.action) {
                        case SUBSCRIBE:
                            subscribe(person, schedule);
                            break;
                        case UNSUBSCRIBE:
                            unsubscribe(person, schedule);
                            break;
                    }
                }
            }
        }
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public boolean isSubscribed(int personId, int scheduleId) {
        Set<Integer> schedulesId = dispatch.get(personId);
        return schedulesId != null && schedulesId.contains(scheduleId);
    }

    public boolean isSubscribed(Person person, Schedule schedule) {
        return isSubscribed(person.getId(), schedule.getId());
    }

    List<ScheduleDispatchEvent> getEvents() {
        return events;
    }

    Map<Integer, Set<Integer>> getDispatchMap() {
        return new HashMap<>(dispatch);
    }

    public static class SubscriptionPattern {
        private final SubscribeAction action;
        private final Schedule from;
        private final int sessionFrequency;

        public SubscriptionPattern(SubscribeAction action, Schedule from, int sessionFrequency) {
            this.action = action;
            this.from = from;
            this.sessionFrequency = sessionFrequency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SubscriptionPattern that = (SubscriptionPattern) o;

            if (sessionFrequency != that.sessionFrequency) return false;
            if (action != that.action) return false;
            return !(from != null ? !from.equals(that.from) : that.from != null);

        }

        @Override
        public int hashCode() {
            int result = action != null ? action.hashCode() : 0;
            result = 31 * result + (from != null ? from.hashCode() : 0);
            result = 31 * result + sessionFrequency;
            return result;
        }
    }

    @Override
    public String toString() {
        return "ScheduleDispatch{" +
                "schedules=" + schedules +
                ", persons=" + persons +
                ", dispatch=" + dispatch +
                '}';
    }
}
