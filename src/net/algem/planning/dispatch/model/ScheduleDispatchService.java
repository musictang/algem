package net.algem.planning.dispatch.model;

import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.planning.*;
import net.algem.util.DataConnection;

import java.util.*;

public class ScheduleDispatchService {
    private final DataConnection dc;
    private final PersonIO personIO;

    public ScheduleDispatchService(DataConnection dc, PersonIO personIO) {
        this.dc = dc;
        this.personIO = personIO;
    }

    public ScheduleDispatch loadScheduleDispatch(Action action) throws Exception {
        List<Schedule> schedules = ScheduleIO.find("WHERE action = " + action.getId() + " ORDER BY jour", dc);
        List<Person> persons = personIO.getPersonsForAction(action);
        Map<Integer, Set<Integer>> dispatchMap = new HashMap<>();

        for (Person person : persons) {
            dispatchMap.put(person.getId(), new HashSet<Integer>());
        }

        for (Schedule schedule : schedules) {
            for (Person person : persons) {
                List<ScheduleRange> ranges = ScheduleRangeIO.find("pg WHERE pg.idplanning = " + schedule.getId()
                                                                     + " AND pg.adherent = " + person.getId(), dc);
                if (ranges != null && ranges.size() > 0) {
                    dispatchMap.get(person.getId()).add(schedule.getId());
                }
            }
        }

        return new ScheduleDispatch(schedules, persons, dispatchMap);
    }

    public void saveScheduleDispatch(Action action, final ScheduleDispatch dispatch) throws Exception {
        dc.withTransaction(new DataConnection.SQLProc() {
            @Override
            protected void run() throws Exception {
                for (ScheduleDispatchEvent event : ScheduleDispatchEvent.compress(dispatch.getEvents())) {
                    Schedule schedule = event.getSchedule();
                    int personId = event.getPerson().getId();
                    if (event.getAction() == SubscribeAction.SUBSCRIBE) {
                        ScheduleRange range = new ScheduleRange(schedule);
                        range.setDay(schedule.getDate());
                        range.setStart(schedule.getStart());
                        range.setEnd(schedule.getEnd());
                        range.setMemberId(personId);
                        ScheduleRangeIO.insert(range, dc);
                    } else {
                        ScheduleRangeIO.delete("idplanning = " + schedule.getId() + " AND adherent = " + personId, dc);
                    }
                }
            }
        });


    }
}
