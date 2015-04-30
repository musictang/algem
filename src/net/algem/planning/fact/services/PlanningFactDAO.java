package net.algem.planning.fact.services;

import net.algem.planning.DateFr;
import net.algem.util.DBUtil;
import net.algem.util.DataConnection;
import net.algem.util.Option;
import net.algem.util.StringUtils;
import net.algem.util.model.TableIO;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        String query = String.format("INSERT INTO planning_fact VALUES (DEFAULT, '%s', '%s', %d, %d, '%s', '%s', %d, %d, '%s')",
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

    public void update(PlanningFact fact) throws Exception {
        if (fact.getId() == -1) throw new IllegalArgumentException("Cannot save transient fact " + fact);
        String query = String.format("UPDATE planning_fact " +
                        "SET date = '%s', type = '%s', planning = %d, prof =  %d, commentaire = '%s', duree = '%s', statut = %d, niveau = %d, planning_desc = '%s' " +
                        "WHERE id = " + fact.getId(),
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

    public void delete(long id) throws Exception {
        dataConnection.executeUpdate("DELETE FROM planning_fact WHERE id = " + id);
    }

    public static class Query {
        public final Option<Integer> idPlanning;
        public final Option<Integer> idProf;
        public final Option<DateFr> start;
        public final Option<DateFr> end;

        public Query(Option<Integer> idPlanning, Option<Integer> idProf, Option<DateFr> start, Option<DateFr> end) {
            this.idPlanning = idPlanning;
            this.idProf = idProf;
            this.start = start;
            this.end = end;
        }
    }

    public ResultSet find(Query q) throws SQLException {
        List<String> criteria = new ArrayList<>();
        for (Integer id : q.idPlanning) {
            criteria.add("planning = " + id);
        }
        for (Integer id : q.idProf) {
            criteria.add("prof = " + id);
        }

        for (DateFr dateFr : q.start) {
            criteria.add("date >= '" + dateFr  + "'");
        }
        for (DateFr dateFr : q.end) {
            DateFr endDate = new DateFr(dateFr);
            endDate.incDay(1);
            criteria.add("date < '" + endDate + "'");
        }
        String whereClause = criteria.size() > 0 ? StringUtils.join(criteria, " AND ") : "1 = 1";
        String query = "SELECT * FROM " + TABLE + " WHERE " + whereClause + " ORDER BY date ASC";

        return dataConnection.executeQuery(query);
    }

    private static int intervalToMinutes(PGInterval interval) {
        return interval.getHours() * 60 + interval.getMinutes();
    }

    public List<PlanningFact> findAsList(Query q) throws SQLException {
        return DBUtil.resultSetAsList(find(q), new DBUtil.Mapper<PlanningFact>() {
            @Override
            public PlanningFact toObj(ResultSet rs) throws SQLException {
                return new PlanningFact(
                        rs.getLong(1),
                        rs.getTimestamp(2),
                        PlanningFact.getType(rs.getString(3)),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getString(6),
                        intervalToMinutes((PGInterval) rs.getObject(7)),
                        rs.getInt(8),
                        rs.getInt(9),
                        rs.getString(10)
                );
            }
        });
    }
}
