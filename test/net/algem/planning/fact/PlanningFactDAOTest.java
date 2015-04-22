package net.algem.planning.fact;

import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.planning.fact.services.PlanningFact;
import net.algem.planning.fact.services.PlanningFactDAO;
import net.algem.util.DataConnection;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanningFactDAOTest extends TestCase {

    private DataConnection dc;
    private PlanningFactDAO planningFactDAO;


    @Override
    protected void setUp() throws Exception {
        dc = TestProperties.getDataConnection();
        planningFactDAO = new PlanningFactDAO(dc);
        dc.executeUpdate("INSERT INTO prof VALUES (3301, NULL, NULL, NULL, TRUE)");
    }

    @Override
    protected void tearDown() throws Exception {
        dc.executeUpdate("DELETE FROM prof");
        dc.executeUpdate("DELETE FROM planning_fact");

    }

    public void testInsert() throws Exception {

        //Given a planning fact
        Date factDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("01/04/2015 12:34");
        PlanningFact fact = new PlanningFact(
                factDate,
                PlanningFact.Type.ABSENCE,
                45,
                3301,
                "test'commentaire",
                90, 1, 2
        );

        //When I insert it in the PlanningFactDAO
        planningFactDAO.insert(fact);

        //Then I can retrieve the same data in the DB
        ResultSet resultSet = dc.executeQuery("SELECT * from planning_fact");
        resultSet.next();
        assertEquals(factDate, resultSet.getTimestamp(1));
        assertEquals("absence", resultSet.getString(2));
        assertEquals(45, resultSet.getInt(3));
        assertEquals(3301, resultSet.getInt(4));
        assertEquals("test'commentaire", resultSet.getString(5));
        assertEquals(new PGInterval(0, 0, 0, 1, 30, 0), resultSet.getObject(6));
        assertEquals(1, resultSet.getInt(7));
        assertEquals(2, resultSet.getInt(8));
    }
}