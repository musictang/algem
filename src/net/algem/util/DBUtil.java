package net.algem.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
    public interface Mapper<T> {
        T toObj(ResultSet rs) throws SQLException;
    }

    public static <T> List<T> resultSetAsList(ResultSet rs, Mapper<T> mapper) throws SQLException {
        List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.toObj(rs));
        }
        rs.close();
        return results;
    }
}
