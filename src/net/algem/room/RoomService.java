/*
 * @(#)RoomService.java 2.9.6 18/03/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.accounting.OrderLineIO;
import net.algem.accounting.OrderLineTableModel;
import net.algem.contact.*;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanificationUtil;
import net.algem.planning.ScheduleIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import net.algem.util.ui.MessagePopup;

/**
 * Service class for room operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.6
 * @since 2.2.b
 */
public class RoomService
{

  private DataConnection dc;
  private RoomIO roomIO;
  private RoomTimesIO roomTimesIO;

  public RoomService(DataConnection dc) {
    this.dc = dc;
    roomIO = (RoomIO) DataCache.getDao(Model.Room);
    roomTimesIO = new RoomTimesIO(dc);
  }

  /**
   * Création d'une salle. La création se fait en trois temps : création du
   * contact associé, du payeur puis celle de la salle.
   *
   * @param r la nouvelle salle
   * @throws RoomException
   */
  void create(Room r) throws RoomException {

    if (roomIO.find("WHERE nom = '" + TableIO.escape(r.getName()) + "'").isEmpty()) {
      try {
        dc.setAutoCommit(false);
        if (r.getContact() == null) {
          Person p = createDefaultContact(r.getName());
          ((PersonIO) DataCache.getDao(Model.Person)).insert(p);
          r.setContact(new Contact(p));
        } 
        r.setPayer(new Person(r.getContact().getId()));
        roomIO.insert(r);
        if (r.getEquipment() == null) {
          r.setEquipment(new Vector<Equipment>());
        } else {
          roomIO.updateEquipment(r);
        }
        dc.commit();
      } catch (SQLException sq) {
        dc.rollback();
        throw new RoomException(MessageUtil.getMessage("create.exception") + sq.getMessage());
      } finally {
        dc.setAutoCommit(true);
      }

    } else {
      String message = MessageUtil.getMessage("create.exception") + MessageUtil.getMessage("existing.room.warning");
      throw new RoomException(message);
    }
  }
  
  private Person createDefaultContact(String name) {
    Person p = new Person();
    p.setName(name);
    p.setType(Person.ROOM);
    p.setFirstName("");
    p.setGender("");
    p.setImgRights(false);
    return p;
  }

  /**
   * Updates a room.
   *
   * @param o old room
   * @param n new room
   * @throws RoomException
   */
  void update(Room o, Room n) throws RoomException {
    try {
      dc.setAutoCommit(false);
      roomIO.update(o, n);

      /*Vector<Equipment> vo = o.getEquipment();
      Vector<Equipment> vn = n.getEquipment();

      // Aucun changement
      if (vo.equals(vn)) {
        return;
      }*/
      roomIO.updateEquipment(n);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new RoomException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Gets all order lines of the contact associated whith the room {@literal r}.
   *
   * @param r room
   * @return a table model
   */
  OrderLineTableModel getOrderLineTabelModel(Room r) {
    OrderLineTableModel t = null;

    int contactId = r.getContact() != null ? r.getContact().getId() : 0;
    int payeurId = r.getPayer() != null ? r.getPayer().getId() : contactId;
    if (contactId > 0 || payeurId > 0) {
      t = new OrderLineTableModel();
      t.load(OrderLineIO.findByMemberOrPayer(contactId, payeurId, dc));
    }
    return t;
  }

  /**
   * Room suppression.
   *
   * @param r room
   * @throws RoomException if error sql or unallowed suppression
   */
  void delete(Room r) throws RoomException {
    try {
      //recherche de plannings existants
      //TODO visualiser les plannings existants en cas d'occupation
      PreparedStatement ps = dc.prepareStatement(ScheduleIO.BUSY_ROOM_STMT);
      ps.setInt(1, r.getId());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        int n = rs.getInt(1);
        if (n > 0) {
          throw new RoomException(MessageUtil.getMessage("room.delete.planning.exception", n));
        }
      }
      roomIO.delete(r);
    } catch (SQLException ex) {
      throw new RoomException(MessageUtil.getMessage("delete.exception") + ex.getMessage());
    }

  }

  /**
   * Gets equipment registered for the room {@literal s}.
   *
   * @param r room instance
   * @return a list of equipments
   */
  Vector<Equipment> getEquipment(Room r) {
    if (r.getId() == 0) {
      return new Vector<Equipment>();
    }
    return roomIO.loadEquip(r.getId());
  }

  /**
   * Gets the person whose code is {@literal id}.
   *
   * @param id payer id
   * @return a person
   */
  public static Person getPayer(int id) {
    return ((PersonIO) DataCache.getDao(Model.Person)).findById(id);
  }

  public Note getNote(Room r) throws NoteException {
    return NoteIO.findId(r.getContact().getId(), r.getContact().getType(), dc);
  }

  /**
   * Completes infos for room contact.
   *
   * @param r
   * @since 2.6.b
   */
  void fillContact(Contact c) {
    if (c != null) {
      ContactIO.complete(c, dc);
    }
  }

  public DailyTimes[] findDailyTimes(int roomId) {
    try {
      return roomTimesIO.find(roomId);
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      return getDefaultDailyTimes();
    }
  }

  /**
   * Default opening times.
   * @return an array of daily times
   */
  private DailyTimes[] getDefaultDailyTimes() {
    DailyTimes[] timesArray = new DailyTimes[7];

    for (int i = 0 ; i < 7 ; i++) {
      DailyTimes dt = new DailyTimes(i+1);
      dt.setOpening(new Hour("00:00"));
      dt.setClosing(new Hour("24:00"));
      timesArray[i] = dt;
    }
    return timesArray;
  }

  void updateTimes(int roomId, DailyTimes [] times) {
    roomTimesIO.update(roomId, times);
  }

  public static boolean isClosed(int room, DateFr date, Hour hStart, Hour hEnd) {
    Hour closed = PlanificationUtil.isRoomClosed(room, date, hStart);
    if (new Hour().equals(closed)) {
      return MessagePopup.confirm(null, MessageUtil.getMessage("room.closed.warning"));
    }

    if (closed != null) {
      if (!MessagePopup.confirm(null, MessageUtil.getMessage("opening.room.warning", closed))) {
        return false;
      }
    }
    closed = PlanificationUtil.isRoomClosed(room, date, hEnd);
    if (closed != null) {
      if (!MessagePopup.confirm(null, MessageUtil.getMessage("closing.room.warning", closed))) {
        return false;
      }
    }
    return true;
  }
}
