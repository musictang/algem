package net.algem.planning.dispatch.model;

import junit.framework.TestCase;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.planning.dispatch.model.ScheduleDispatch.SubscriptionPattern;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ScheduleDispatchTest extends TestCase {

    private List<Schedule> schedules;
    private List<Person> persons;
    private ScheduleDispatch dispatch;

    public void setUp() throws Exception {
        super.setUp();
        schedules = new ArrayList<>();

        Schedule s = new Schedule();
        s.setId(1);
        s.setDate(new DateFr(1, 6, 2015));
        s.setStart(new Hour(8)); s.setEnd(new Hour(9));
        schedules.add(s);

        s = new Schedule();
        s.setId(2);
        s.setDate(new DateFr(8, 6, 2015));
        s.setStart(new Hour(8)); s.setEnd(new Hour(9));
        schedules.add(s);

        s = new Schedule();
        s.setId(3);
        s.setDate(new DateFr(15, 6, 2015));
        s.setStart(new Hour(8)); s.setEnd(new Hour(9));
        schedules.add(s);

        s = new Schedule();
        s.setId(4);
        s.setDate(new DateFr(22, 6, 2015));
        s.setStart(new Hour(8)); s.setEnd(new Hour(9));
        schedules.add(s);

        persons = new ArrayList<>();

        Person p = new Person();
        p.setId(1);
        persons.add(p);

        p = new Person();
        p.setId(2);
        persons.add(p);

        Map<Integer, Set<Integer>> dispatchMap = new HashMap<>();
        dispatchMap.put(1, new HashSet<Integer>());
        dispatchMap.put(2, new HashSet<Integer>());

        dispatch = new ScheduleDispatch(schedules, persons, dispatchMap);
    }

    public void testSubscribe() throws Exception {
        // Given these 3 subscription actions
        dispatch.subscribe(persons.get(0), schedules.get(0));
        dispatch.subscribe(persons.get(0), schedules.get(0));
        dispatch.subscribe(persons.get(1), schedules.get(1));

        // When I retrieve the list of events
        // I should retrieve these 2 subscription events
        assertEquals(asList(
                new ScheduleDispatchEvent(SubscribeAction.SUBSCRIBE, persons.get(0), schedules.get(0)),
                new ScheduleDispatchEvent(SubscribeAction.SUBSCRIBE, persons.get(1), schedules.get(1))
        ), dispatch.getEvents());

        // When I retrieve the dispatch map
        // I should retrieve the 2 subscriptions

        Map<Integer, Set<Integer>> expected = new HashMap<>();
        expected.put(1, new HashSet<>(singletonList(1)));
        expected.put(2, new HashSet<>(singletonList(2)));
        assertEquals(expected, dispatch.getDispatchMap());
    }

    public void testSubscribeWithPattern() throws Exception {
        // Given these subscriptions patterns
        dispatch.subscribe(persons.get(0), new SubscriptionPattern(SubscribeAction.SUBSCRIBE, schedules.get(0), 2));
        dispatch.subscribe(persons.get(1), new SubscriptionPattern(SubscribeAction.SUBSCRIBE, schedules.get(1), 2));

        // When I retrieve the dispatch map
        // Subscription should be created accordingly
        Map<Integer, Set<Integer>> expected = new HashMap<>();
        expected.put(1, new HashSet<>(asList(1, 3)));
        expected.put(2, new HashSet<>(asList(2, 4)));
        assertEquals(expected, dispatch.getDispatchMap());
    }
}