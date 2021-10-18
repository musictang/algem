/*
 * @(#)MusicStyleIO.java	2.7.a 07/01/13
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.algem.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.config.MusicStyle}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class MusicStyleIO
        extends TableIO
        implements Cacheable {

    public static final String DEFAULT_STYLE = "Autre";
    public static final String TABLE = "stylemus";
    public static final String SEQUENCE = "stylemus_id_seq";
    private DataConnection dc;

    public MusicStyleIO(DataConnection dc) {
        this.dc = dc;
    }

    public void insert(MusicStyle ms) throws SQLException {
        int n = nextId(SEQUENCE, dc);
        String query = "INSERT INTO " + TABLE + " VALUES("
                + n
                + ",'" + ms.getLabel()
                + "')";

        dc.executeUpdate(query);
        ms.setId(n);
    }

    public void update(MusicStyle ms) throws SQLException {
        String query = "UPDATE " + TABLE + " SET libelle = '" + ms.getLabel() + "' WHERE id = " + ms.getId();
        dc.executeUpdate(query);
    }

    public void delete(MusicStyle ms) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE id = " + ms.getId();
        dc.executeUpdate(query);
    }

    public MusicStyle findId(String l) throws SQLException {
        String query = "WHERE libelle = '" + l + "'";
        List<MusicStyle> v = find(query);
        if (v.size() > 0) {
            return v.get(0);
        }
        return null;
    }

    public MusicStyle findId(int id) throws SQLException {
        String query = "WHERE id = " + id;
        List<MusicStyle> v = find(query);
        if (v.size() > 0) {
            return v.get(0);
        }
        return null;
    }

    public List<MusicStyle> find(String where) throws SQLException {
        List<MusicStyle> v = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE + " " + where;
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                MusicStyle style = new MusicStyle();
                style.setId(rs.getInt(1));
                style.setLabel(rs.getString(2).trim());
                v.add(style);
            }
        }
        return v;
    }

    @Override
    public List<MusicStyle> load() throws SQLException {
        return find("ORDER BY libelle");
    }
}
