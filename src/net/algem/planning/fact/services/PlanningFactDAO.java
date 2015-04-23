package net.algem.planning.fact.services;

import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanningFactDAO extends TableIO {
    public final static String TABLE = "planning_fact";

    private final DataConnection dataConnection;

    public PlanningFactDAO(DataConnection dataConnection) {
        this.dataConnection = dataConnection;
    }

    private static String minutesToPGInterval(int minutes) {
        return minutes + " minutes";
    }

    private static String toTimeStamp(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public void insert(PlanningFact fact) throws Exception {
        String query = String.format("INSERT INTO planning_fact VALUES ('%s', '%s', %d, %d, '%s', '%s', %d, %d, '%s')",
                toTimeStamp(fact.getDate()),
                fact.getType().toDBType(),
                fact.getPlanning(),
                fact.getProf(),
                escape(fact.getCommentaire()),
                minutesToPGInterval(fact.getDureeMinutes()),
                fact.getStatut(),
                fact.getNiveau(),
                escape(fact.getPlanningDescription())
        );
        dataConnection.executeUpdate(query);
    }
}
