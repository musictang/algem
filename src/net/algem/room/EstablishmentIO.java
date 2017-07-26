/*
 * @(#)EstablishmentIO.java	2.15.0 26/07/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.algem.bank.RibIO;
import net.algem.contact.*;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.room.Establishment}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 */
public class EstablishmentIO
  extends TableIO {

  public static final String TABLE = "etablissement";

  public static Establishment findId(int n, DataConnection dc) throws SQLException {

    String where = " AND p.id = " + n;
    List<Establishment> v = find(where, dc);
    if (v.size() > 0) {
      return v.get(0);
    }
    return null;
  }

  public static List<Establishment> find(String where, DataConnection dc) throws SQLException {

    List<Establishment> estabs = new ArrayList<Establishment>();
    String query = "SELECT DISTINCT ON (p.nom) " + PersonIO.COLUMNS + ", e.actif " + PersonIO.POST_QUERY
      + " JOIN " + TABLE + " e ON (p.id = e.id)"
      + " WHERE p.ptype = " + Person.ESTABLISHMENT;
    query += where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Establishment e = new Establishment(PersonIO.getFromRS(rs));
      e.setActive(rs.getBoolean(10));
      e.setAddress(AddressIO.findId(e.getId(), dc));
      e.setTele(TeleIO.findId(e.getId(), dc));
      e.setEmail(EmailIO.find(e.getId(), dc));
      e.setSites(WebSiteIO.find(e.getId(), Person.ESTABLISHMENT, dc));

      estabs.add(e);
    }
    return estabs;
  }

  public static void insert(final Establishment e, final short type, DataConnection dc) throws SQLException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          trans_insert(e, type, conn);
          return null;
        }
      });
    } catch (Exception ex) {
      GemLogger.logException("transaction insert Etablissement", ex);
      throw new SQLException(ex.getMessage());
    }
  }

  public static void update(final Establishment e, final Establishment n, DataConnection dc) throws SQLException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          trans_update(e, n, conn);
          return null;
        }
      });
    } catch (Exception ex) {
      GemLogger.logException("transaction update Etablissement", ex);
      throw new SQLException(ex.getMessage());
    }

  }

  public static void updateStatus(int id, boolean active, int userId, DataConnection dc) throws SQLException {
    String query = "UPDATE etablissement SET actif = " + active + " WHERE id = " + id + " AND idper = " + userId;
    dc.executeUpdate(query);
  }

  public static void delete(Establishment e, DataConnection dc) throws EstablishmentException {

    String query = "SELECT etablissement FROM " + RoomIO.TABLE + " WHERE etablissement = " + e.getId();
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        throw new EstablishmentException(MessageUtil.getMessage("establishment.delete.exception"));
      }

      new PersonIO(dc).delete(e.getPerson());
      AddressIO.delete(e.getId(), dc);
      TeleIO.delete(e.getId(), dc);
      EmailIO.delete(e.getId(), dc);
      WebSiteIO.delete(e.getId(), Person.ESTABLISHMENT, dc);
      RibIO.delete(e.getId(), dc);
    } catch (SQLException sqe) {
      throw new EstablishmentException("SQL : " + sqe.getMessage());
    }
  }

  private static void trans_insert(Establishment e, short type, DataConnection dc) throws SQLException {
    int number = nextId(PersonIO.SEQUENCE, dc);
    Person p = e.getPerson();
    String query = "INSERT INTO " + PersonIO.TABLE + "  VALUES("
      + number
      + "," + type
      + ",'" + escape(e.getName())
      + "',''" // firstname
      + ",''" // gender
      + ",FALSE" // img rights
      + (p.getOrgName() == null || p.getOrgName().isEmpty() ? ",NULL" : ",'" + escape(p.getOrgName()) + "'")
      + ")";

    dc.executeUpdate(query);
    e.setId(number);

    query = "INSERT INTO etablissement SELECT " + e.getId() + ", idper, " + e.isActive() + " FROM login WHERE profil in (1,2,4)";
    dc.executeUpdate(query);

    Address a = e.getAddress();
    if (a != null && a.getAdr1().length() > 0) {
      a.setId(e.getId());
      AddressIO.insert(a, dc);
    }
    Vector<Telephone> v = e.getTele();
    for (int i = 0; v != null && i < v.size(); i++) {
      Telephone tel = v.elementAt(i);
      tel.setIdper(e.getId());
      if (tel.getNumber().length() > 0) {
        TeleIO.insert(tel, i, dc);
      }
    }
    Vector<Email> ve = e.getEmail();
    if (ve != null) {
      for (int j = 0; j < ve.size(); j++) {
        Email em = ve.elementAt(j);
        em.setIdper(e.getId());
        if (em.getEmail() != null && em.getEmail().trim().length() > 0) {
          EmailIO.insert(ve.elementAt(j), j, dc);
        }
      }
    }

    List<WebSite> sites = e.getSites();
    if (sites != null) {
      for (int j = 0; j < sites.size(); j++) {
        WebSite w = sites.get(j);
        w.setIdper(e.getId());
        w.setPtype(Person.ESTABLISHMENT);
        WebSiteIO.insert(w, j, dc);
      }
    }

  }

  private static void trans_update(Establishment e, Establishment n, DataConnection dc) throws SQLException {
    Person p = n.getPerson();
    PersonIO pio = (PersonIO) DataCache.getDao(Model.Person);
    pio.update(p);

    Address a = n.getAddress();
    if (e.getAddress() != null) {
      if (a != null && !a.equals(e.getAddress())) {
        AddressIO.update(a, dc);
      }
    } else if (a != null && a.getAdr1().length() > 0) {
      AddressIO.insert(a, dc);
    }

    Vector<Telephone> newtels = n.getTele();
    Vector<Telephone> oldtels = e.getTele();

    int i = 0;
    for (; newtels != null && i < newtels.size(); i++) {
      Telephone nt = newtels.elementAt(i);
      if (oldtels != null && i < oldtels.size()) {
        if (!nt.equals(oldtels.elementAt(i))) {
          nt.setIdper(e.getId());
          TeleIO.update(nt, i, dc);
        }
      } else {
        nt.setIdper(e.getId());
        TeleIO.insert(nt, i, dc);
      }
    }
    // si le nombre d'anciens numéros > nombre nouveaux numéros
    for (; oldtels != null && i < oldtels.size(); i++) {
      TeleIO.delete(e.getId(), i, dc);
    }
    i = 0;
    Vector<Email> oldmails = e.getEmail();
    Vector<Email> newmails = n.getEmail();
    for (; newmails != null && i < newmails.size(); i++) {
      Email ne = newmails.elementAt(i);
      if (oldmails != null && i < oldmails.size()) {
        if (!ne.equals(oldmails.elementAt(i))) {
          EmailIO.update(ne, i, dc);
        }
      } else {
        EmailIO.insert(ne, i, dc);
      }
    }

    for (; oldmails != null && i < oldmails.size(); i++) {
      EmailIO.delete(e.getId(), i, dc);
    }

    WebSiteIO.delete(e.getId(), Person.ESTABLISHMENT, dc);
    i = 0;
    List<WebSite> newsites = n.getSites();
    for (; newsites != null && i < newsites.size(); i++) {
      WebSite w = newsites.get(i);
      WebSiteIO.insert(w, i, dc);
    }

  }
}
