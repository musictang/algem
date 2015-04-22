package net.algem.planning.fact;

import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanningFactIO extends TableIO {
    public final static String TABLE = "planning_fact";

    private static String minutesToPGInterval(int minutes) {
        return minutes + " minutes";
    }

    private static String toTimeStamp(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static void insert(PlanningFact fact, DataConnection dc) throws Exception {
        String query = String.format("INSERT INTO planning_fact VALUES ('%s', '%s', %d, %d, '%s', '%s', %d, %d)",
                toTimeStamp(fact.getDate()),
                fact.getType().toDBType(),
                fact.getPlanning(),
                fact.getProf(),
                fact.getCommentaire(),
                minutesToPGInterval(fact.getDureeMinutes()),
                fact.getStatut(),
                fact.getNiveau()
        );
        dc.executeQuery(query);
    }
}
