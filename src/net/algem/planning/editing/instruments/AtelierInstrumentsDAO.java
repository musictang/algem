package net.algem.planning.editing.instruments;

import net.algem.util.DataConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.String.format;

class AtelierInstrumentsDAO {
    private final DataConnection dc;

    public static final String TABLE = "atelier_instruments";
    public static final String COLUMNS = "idaction, idpers, idinstru";

    AtelierInstrumentsDAO(DataConnection dc) {
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
}
