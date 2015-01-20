package net.algem.planning.editing.instruments;

import net.algem.util.DataConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

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
                "select distinct p.id, p.nom, p.prenom from personne p\n" +
                "join commande c on c.adh = p.id\n" +
                "join commande_cours cc on cc.idcmd = c.id\n" +
                "join action a on cc.idaction = a.id\n" +
                "where a.id=%d\n" +
                "and cc.datedebut < '%s'\n" +
                "and cc.datefin > '%s'\n" +
                "order by p.nom, p.prenom", idAction, dateString, dateString
        );

        List<Integer> result = new ArrayList<>();
        ResultSet resultSet = dc.executeQuery(query);
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }

    public List<Integer> getInstrumentIdsForPerson(int idPerson) throws SQLException {
        String query = format("select instrument from person_instrument where idper=%d order by idx", idPerson);
        List<Integer> result = new ArrayList<>();
        ResultSet resultSet = dc.executeQuery(query);
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }
}
