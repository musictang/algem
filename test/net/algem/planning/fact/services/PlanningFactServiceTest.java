package net.algem.planning.fact.services;

import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.planning.Schedule;
import net.algem.room.Room;
import net.algem.util.DataConnection;

import java.util.Date;

import static org.mockito.Mockito.*;

public class PlanningFactServiceTest extends TestCase {

    private PlanningService planningService;
    private PlanningFactDAO planningFactDAO;
    private PlanningFactService planningFactService;
    private PlanningFactService.RoomFinder roomFinder;
    private Schedule schedule;
    private PlanningFact absence404Fact;
    private PlanningFact lowActivity404Fact;

    public void setUp() throws Exception {
        super.setUp();

        DataConnection dc = spy(TestProperties.getDataConnection());
        planningService = mock(PlanningService.class);
        planningFactDAO = mock(PlanningFactDAO.class);
        PlanningFactCreator planningFactCreator = mock(PlanningFactCreator.class);

        // Two rooms : 404 (normal) and 405 catchup
        roomFinder = mock(PlanningFactService.RoomFinder.class);
        Room room404 = new Room(404, "Salle 404");
        Room catchupRoom = new Room(405, "RATTRAPAGE");
        assert catchupRoom.isCatchingUp();
        when(roomFinder.findRoom(404)).thenReturn(room404);
        when(roomFinder.findRoom(405)).thenReturn(catchupRoom);

        //Schedule in room 404
        schedule = new Schedule();
        schedule.setId(1234);
        schedule.setIdPerson(3301);
        schedule.setIdRoom(404);
        schedule.setDate(new DateFr(1, 1, 2015));
        schedule.setStart(new Hour(8, 0));
        schedule.setEnd(new Hour(9, 30));


        //Expected facts
        absence404Fact = new PlanningFact(new Date(), PlanningFact.Type.ABSENCE, 1234, 3301, "commentaire", 90, 0, 0, "");
        when(planningFactCreator.createFactForPlanning(schedule, PlanningFact.Type.ABSENCE, "commentaire"))
                .thenReturn(absence404Fact);

        lowActivity404Fact = new PlanningFact(new Date(), PlanningFact.Type.ACTIVITE_BAISSE, 1234, 3301,
                "low activity", 90, 0, 0, "");
        when(planningFactCreator.createFactForPlanning(schedule, PlanningFact.Type.ACTIVITE_BAISSE, "low activity"))
                .thenReturn(lowActivity404Fact);

        planningFactService = new PlanningFactService(dc, planningService, planningFactDAO, planningFactCreator, roomFinder);
    }



    public void testScheduleCatchUp() throws Exception {
        //Given a schedule 1234, for the prof 3301, in room 404
        //When I put the schedule in catchup room 405
        planningFactService.scheduleCatchUp(schedule, roomFinder.findRoom(405), "commentaire");

        //Then:
        //  the room of the schedule is changed
        verify(planningService).changeRoom(1234, 405);
        //  an absence fact is created
        verify(planningFactDAO).insert(absence404Fact);
    }

    public void testScheduleCatchupAlreadyInCatchup() throws Exception {
        //Given a schedule 1234, for the prof 3301, already in room 405
        Schedule schedule = new Schedule();
        schedule.setId(1234);
        schedule.setIdPerson(3301);
        schedule.setIdRoom(405);
        schedule.setDate(new DateFr(1, 1, 2015));
        schedule.setStart(new Hour(8, 0));
        schedule.setEnd(new Hour(9, 30));

        //When I put the schedule in catchup room 405
        try {
            planningFactService.scheduleCatchUp(schedule, roomFinder.findRoom(405), "commentaire");
            assertFalse(true);
        } catch (IllegalArgumentException e) {
            //Then a IllegalArgumentException is thrown, because the schedule is already in catchup room
        }
    }

    public void testScheduleDeletedLowActivity() throws Exception {
        //Given a schedule 1234, for the prof 3301, in room 404
        //When I delete the schedule because of low activity
        planningFactService.scheduleDeletedLowActivity(schedule, "low activity");
        //Then:
        //  the schedule is deleted
        verify(planningService).deleteSchedule(schedule);
        //  a low activity fact is created
        verify(planningFactDAO).insert(lowActivity404Fact);
    }

    public void testReplanify() throws Exception {

    }
}