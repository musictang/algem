/*
 * @(#)RoomIO.java	2.8.q 09/12/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.Person;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.room.Room}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.q
 * @since 1.0a 07/07/1999
 */
public class RoomIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "salle";
  private static final String SEQUENCE = "idsalle";
  private static String NOTE_TABLE = "note";
  private static String EQUIP_TABLE = "sallequip";
  private DataConnection dc;
//  private String insertEquipStatement = "INSERT INTO " + EQUIP_TABLE + " VALUES(?,?,?)";
//  private String updateEquipStatement = "UPDATE " + EQUIP_TABLE + " SET libelle = ?, qte = ? WHERE idsalle = ? AND libelle = ?";

  public RoomIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(Room s) throws SQLException {
    int id = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + id
            + "','" + escape(s.getName())
            + "','" + escape(s.getFunction())
            + "'," + s.getSurface()
            + "," + s.getNPers()
            + "," + s.getEstab()
            + ",'" + (s.isActive() ? "t" : "f")
            + "'," + s.getRate().getId()
            + "," + s.getContact().getId()
            + "," + s.getPayer().getId()
            + ",'" + (s.isAvailable() ? "t" : "f") + "'"
            + ")";

    dc.executeUpdate(query);
    s.setId(id);
  }

  private void insert(Equipment e) throws SQLException {
    String query = "INSERT INTO " + EQUIP_TABLE + " VALUES("
            + "" + e.getRoom()
            + ",'" + escape(e.getLabel()) // 64 caractères max depuis version 2.0pc
            + "'," + e.getQuantity()
            + "," + e.getIdx()
            + ")";
    dc.executeUpdate(query);
  }

  /**
   *
   * @param old old room
   * @param nr new room
   * @throws SQLException
   */
  public void update(Room old, Room nr) throws SQLException {
    trans_update(old, nr);
  }

  public void updateEquipment(Room r) throws SQLException {

    deleteEquipment(r.getId());
    List<Equipment> ve = r.getEquipment();
    for (int i = 0; i < ve.size(); i++) {
      Equipment e = ve.get(i);
      e.setRoom(r.getId());
      e.setIdx((short) i);
      insert(e);
    }
  }

  /**
   *
   * @param old old room
   * @param n new room
   * @throws SQLException
   */
  private void trans_update(Room old, Room n) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "nom ='" + escape(n.getName())
            + "',fonction = '" + escape(n.getFunction())
            + "',surf = " + n.getSurface()
            + ",npers = " + n.getNPers()
            + ",etablissement =" + n.getEstab()
            + ",active = '" + (n.isActive() ? "t" : "f")           
            + "',idtarif =" + n.getRate().getId()
            + ",payeur = " + n.getPayer().getId()
            + ",public = '" + (n.isAvailable() ? "t" : "f") + "'"
            + " WHERE id = " + n.getId();

    dc.executeUpdate(query);

    new ContactIO(dc).update(old.getContact(), n.getContact());

  }

  /**
   * Deletes a room.
   * Equipment infos are also deleted but the contact is not.
   *
   * @param r
   * @throws RoomException if error sql
   */
  public void delete(Room r) throws RoomException {
    try {
      dc.setAutoCommit(false);
      String query = "DELETE FROM " + TABLE + " WHERE id = " + r.getId();
      dc.executeUpdate(query);
      deleteEquipment(r.getId());
      query = "DELETE FROM " + NOTE_TABLE + " WHERE idper = " + r.getContact().getId() + " AND ptype = " + Person.ROOM;
      dc.executeUpdate(query);
      dc.commit();
    } catch (SQLException e) {
      dc.rollback();
      throw new RoomException(MessageUtil.getMessage("delete.exception") + e.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Deletes equipment for room {@code r}.
   *
   * @param r room id
   * @throws SQLException
   */
  private void deleteEquipment(int r) throws SQLException {
    String query = "DELETE FROM " + EQUIP_TABLE + " WHERE idsalle = " + r;
    dc.executeUpdate(query);
  }

  public Room findId(String n) {
    return findId(new Integer(n));
  }

  public Room findId(int n) {
    return findId(new Integer(n));
  }

  public Room findId(Integer n) {

    String query = "WHERE id = " + n;
    Vector<Room> v = find(query);
    if (v != null && v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  /**
   * Gets the room where the course {@code courseId} is scheduled from {@code dateStart}.
   *
   * @param courseId
   * @param dateStart
   * @return a room
   */
  public Room getRoom(int courseId, String dateStart) throws SQLException {
    int s = 0;

    String query = "SELECT lieux FROM planning, action WHERE ptype = " + Schedule.COURSE
            + " AND jour >= '" + dateStart + "' AND planning.action = action.id AND action.cours = " + courseId + " LIMIT 1";
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      s = rs.getInt(1);
    }
    rs.close();

    return findId(s);
  }

  public Vector<Room> findAll() {
    String query = "SELECT * FROM " + TABLE + " ORDER BY nom";
    return findAll(query);
  }

  public Vector<Room> find(String where) {
    String query = "SELECT * FROM " + TABLE + " " + where;
    return findAll(query);
  }

  private Vector<Room> findAll(String query) {
    Vector<Room> v = new Vector<Room>();
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Room s = new Room();
        s.setId(rs.getInt(1));
        s.setName(unEscape(rs.getString(2).trim()));
        s.setFunction(unEscape(rs.getString(3).trim()));
        s.setSurface(rs.getInt(4));
        s.setNPers(rs.getInt(5));
        s.setEstab(rs.getInt(6));
        s.setActive(rs.getBoolean(7));              
        RoomRate rt = loadRate(rs.getInt(8));
        s.setRate(rt);
        Contact c = loadContact(rs.getInt(9));
        s.setContact(c);
        s.setPayer(loadPayer(rs.getInt(10)));
        s.setAvailable(rs.getBoolean(11));
        v.addElement(s);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  public Vector<Equipment> loadEquip(int idsalle) {
    Vector<Equipment> v = new Vector<Equipment>();
    String query = "SELECT * FROM " + EQUIP_TABLE + " WHERE idsalle = " + idsalle + " ORDER BY idx";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Equipment e = new Equipment();
        e.setRoom(rs.getInt(1));
        e.setLabel(unEscape(rs.getString(2).trim()));
        e.setQuantity(rs.getInt(3));
        e.setIdx(rs.getShort(4));

        v.addElement(e);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  private Contact loadContact(int idper) throws SQLException {
    
    Person p = (Person) DataCache.findId(idper, Model.Person);
    if (p != null) {
      return new Contact(p);
    }

    return null;
  }

  private RoomRate loadRate(int rate) throws SQLException {
    return (RoomRate) DataCache.findId(rate, Model.RoomRate);
  }

  private Person loadPayer(int payerId) throws SQLException {
    return (Person) DataCache.findId(payerId, Model.Person);
  }


  @Override
  public List<Room> load() {
    return findAll();
  }
}
