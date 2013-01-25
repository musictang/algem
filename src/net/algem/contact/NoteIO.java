/*
 * @(#)NoteIO.java	2.6.a 17/09/12
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
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.Note}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class NoteIO
        extends TableIO
{

  public static final String TABLE = "note";
  public static final String SEQUENCE = "idnote";

  public static void insert(Note n, DataConnection dc) throws SQLException {
    
    int number = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + number
            + "," + n.getIdPer()
            + ",'" + escape(n.getText())
            + "'," + n.getPtype()
            + ")";

    dc.executeUpdate(query);
    n.setId(number);
  }

  public static void update(Note n, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET texte = '" + escape(n.getText())
            + "' WHERE id = " + n.getId() + " AND ptype = " + n.getPtype();
    dc.executeUpdate(query);
  }

  public static void delete(Note n, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + n.getId() + " AND ptype = " + n.getPtype();
    dc.executeUpdate(query);
  }

  public static Note findId(int n, short type, DataConnection dc) throws NoteException {
    String query = "WHERE idper = " + n + " AND ptype = " + type;
    Vector<Note> v = find(query, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public static Vector<Note> find(String where, DataConnection dc) throws NoteException {

    Vector<Note> v = new Vector<Note>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Note n = new Note();
        n.setId(rs.getInt(1));
        n.setIdPer(rs.getInt(2));
        n.setText(unEscape(rs.getString(3)));
        n.setPtype(rs.getShort(4));
        v.addElement(n);
      }
      rs.close();

      return v;
    } catch (SQLException ex) {
      throw new NoteException(MessageUtil.getMessage("note.exception"));
    }
  }
}
