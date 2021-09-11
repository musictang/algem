/*
 * @(#)NoteIO.java	2.9.4.13 12/10/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.Note}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class NoteIO
        extends TableIO
{

  public static final String TABLE = "note";
  public static final String SEQUENCE = "idnote";
  private static final PreparedStatement notePS = 
          DataCache.getDataConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE idper = ? AND ptype = ? LIMIT 1");

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

  /**
   * 
   * @param idper person id
   * @param type note type
   * @param dc data connection
   * @return a note
   * @throws NoteException
   */
  public static Note findId(int idper, short type, DataConnection dc) throws NoteException {
    try {
      notePS.setInt(1, idper);
      notePS.setShort(2, type);
      ResultSet rs = notePS.executeQuery();
      while (rs.next()) {
        return getResultFromRS(rs);
      }
    } catch (SQLException ex) {
      throw new NoteException(MessageUtil.getMessage("note.exception"));
    }
    return null;
  }

  public static Vector<Note> find(String where, DataConnection dc) throws NoteException {

    Vector<Note> v = new Vector<Note>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        v.addElement(getResultFromRS(rs));
      }
      rs.close();

      return v;
    } catch (SQLException ex) {
      throw new NoteException(MessageUtil.getMessage("note.exception"));
    }
  }
  
  private static Note getResultFromRS(ResultSet rs) throws SQLException {
    Note n = new Note();
    n.setId(rs.getInt(1));
    n.setIdPer(rs.getInt(2));
    n.setText(rs.getString(3));
    n.setPtype(rs.getShort(4));
    return n;
  }
  
}
