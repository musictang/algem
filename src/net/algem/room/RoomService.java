/*
 * @(#)RoomService.java 2.7.a 26/11/12
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
package net.algem.room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.accounting.OrderLineIO;
import net.algem.accounting.OrderLineTableModel;
import net.algem.contact.*;
import net.algem.planning.ScheduleIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * Service class for room operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.2.b
 */
public class RoomService
{

  private DataConnection dc;
  private RoomIO roomIO;

  public RoomService(DataConnection dc) {
    this.dc = dc;
    roomIO = (RoomIO) DataCache.getDao(Model.Room);
  }

  /**
   * Création d'une salle. La création se fait en trois temps : création du
   * contact associé, du payeur puis celle de la salle.
   *
   * @param r la nouvelle salle
   * @throws SQLException
   */
  public void create(Room r) throws SQLException, RoomException {

    if (roomIO.find("WHERE nom = '" + TableIO.escape(r.getName()) + "'").isEmpty()) {
      try {
        dc.setAutoCommit(false);
        Person p = new Person();
        p.setName(r.getName());
        p.setType(Person.ROOM);
        p.setFirstName("");
        p.setCivility("");
        p.setImgRights(false);

        ((PersonIO) DataCache.getDao(Model.Person)).insert(p);
        r.setContact(new Contact(p));
        r.setPayer(new Person(r.getContact().getId()));
        r.setEquipment(new Vector<Equipment>());
        roomIO.insert(r);
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

  /**
   * Mise à jour d'une salle.
   *
   * @param o ancienne salle
   * @param n nouvelle salle
   * @throws SQLException
   */
  public void update(Room o, Room n) throws RoomException {
    try {
      dc.setAutoCommit(false);
      roomIO.update(o, n);

      Vector<Equipment> vo = o.getEquipment();
      Vector<Equipment> vn = n.getEquipment();

      // Aucun changement
      if (vo.equals(vn)) {
        return;
      }
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
   * Gets all order lines of the contact associated whith the room {@code r}.
   *
   * @param r room
   * @return a table model
   */
  public OrderLineTableModel getOrderLineTabelModel(Room r) {
    OrderLineTableModel t = null;

    Person p = r.getContact();
    if (p != null && p.getId() > 0) {
      t = new OrderLineTableModel();
      t.load(OrderLineIO.findByMemberOrPayer(p.getId(), p.getId(), dc));
    }
    return t;
  }

  /**
   * Room suppression.
   *
   * @param r room
   * @throws RoomDeleteException if error sql or unallowed suppression
   */
  public void delete(Room r) throws RoomException {
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
   * Gets equipment registered for the room {@code s}.
   *
   * @param r room
   * @return a list of equipments
   */
  public Vector<Equipment> getEquipment(Room r) {
    return roomIO.loadEquip(r.getId());
  }

  /**
   * Gets the person whose code is {@code id}.
   *
   * @param id payer id
   * @return a person
   */
  public static Person getPayer(int id) {
    return ((PersonIO) DataCache.getDao(Model.Person)).findId(id);
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
  public void fillContact(Room r) {
    ContactIO.complete(r.getContact(), dc);
  }
}
