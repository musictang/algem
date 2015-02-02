package net.algem.planning.editing.instruments;

import net.algem.util.DataConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import net.algem.contact.PersonIO;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.OrderIO;
import net.algem.planning.ActionIO;

public class AtelierInstrumentsDAO {
    private final DataConnection dc;

    public static final String TABLE = "atelier_instruments";
    public static final String COLUMNS = "idaction, idpers, idinstru";

    public AtelierInstrumentsDAO(DataConnection dc) {
        this.dc = dc;
    }

    public AtelierInstrument find(int idAction, int idPerson) throws SQLException {
        String query = "select " + COLUMNS + " from " + TABLE + " where idaction = " + idAction + " and idpers = " + idPerson;
        ResultSet resultSet = dc.executeQuery(query);
        if (resultSet.next()) {
            return new AtelierInstrument(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3));
        } else return null;
    }

    public void save(AtelierInstrument a) throws SQLException {

        AtelierInstrument existing = find(a.getIdAction(), a.getIdPerson());
        if (existing != null) {
            delete(a.getIdAction(), a.getIdPerson());
        }
        dc.executeUpdate(format("insert into %s values (%d, %d, %d)",
                TABLE, a.getIdAction(), a.getIdPerson(), a.getIdInstrument()));
    }

    public void delete(int idAction, int idPerson) throws SQLException {
        dc.executeUpdate(format("delete from %s where idaction=%d and idpers=%d", TABLE, idAction, idPerson));
    }

    public void delete(AtelierInstrument atelierInstrument) throws SQLException {
        delete(atelierInstrument.getIdAction(), atelierInstrument.getIdPerson());
    }


    public List<Integer> getPersonsIdsForAction(int idAction) throws SQLException {
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String query = format(
                "SELECT DISTINCT p.id, p.nom, p.prenom FROM " + PersonIO.TABLE + " p\n" +
                "JOIN " + OrderIO.TABLE + " c ON c.adh = p.id\n" +
                "JOIN " + CourseOrderIO.TABLE + " cc ON cc.idcmd = c.id\n" +
                "JOIN " + ActionIO.TABLE + " a ON cc.idaction = a.id\n" +
                "WHERE a.id=%d\n" +
                "AND cc.datedebut < '%s'\n" +
                "AND cc.datefin > '%s'\n" +
                "ORDER BY p.nom, p.prenom", idAction, dateString, dateString
        );

        List<Integer> result = new ArrayList<>();
        ResultSet resultSet = dc.executeQuery(query);
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }

    public List<Integer> getInstrumentIdsForPerson(int idPerson) throws SQLException {
        String query = format("SELECT instrument FROM person_instrument WHERE idper=%d ORDER BY idx", idPerson);
        List<Integer> result = new ArrayList<>();
        ResultSet resultSet = dc.executeQuery(query);
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }
}
