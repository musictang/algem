/*
 * @(#)EstablishmentIO.java	2.9.4.6 03/06/15
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
package net.algem.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
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
 * @version 2.9.4.6
 */
public class EstablishmentIO
        extends TableIO
{

  public static void trans_insert(Establishment e, short type, DataConnection dc) throws SQLException {
    int number = nextId(PersonIO.SEQUENCE, dc);

    Person p = e.getPerson();
    String query = "INSERT INTO " + PersonIO.TABLE + "  VALUES("
            + number
            + "," + type
            + ",'" + escape(p.getName().toUpperCase())
            + "','" + escape(p.getFirstName())
            + "','" + p.getGender()
            + "', FALSE"
            + ", '" + escape(p.getOrganization())
            + "')";

    dc.executeUpdate(query);
    p.setId(number);

    Address a = e.getAddress();
    if (a != null && a.getAdr1().length() > 0) {
      a.setId(p.getId());
      AddressIO.insert(a, dc);
    }
    Vector<Telephone> v = e.getTele();
    for (int i = 0; v != null && i < v.size(); i++) {
      Telephone tel = v.elementAt(i);
      tel.setIdper(p.getId());
      if (tel.getNumber().length() > 0) {
        TeleIO.insert(tel, i, dc);
      }
    }
    Vector<Email> ve = e.getEmail();
    if (ve != null) {
      for (int j = 0; j < ve.size(); j++) {
        Email em = ve.elementAt(j);
        em.setIdper(p.getId());
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

  public static void insert(Establishment e, short type, DataConnection dc) throws SQLException {
    try {
      dc.setAutoCommit(false);
      trans_insert(e, type, dc);
      dc.commit();
    } catch (SQLException e1) {
      dc.rollback();
      GemLogger.logException("transaction insert Etablissement", e1);
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public static void update(Establishment e, Establishment n, DataConnection dc) throws SQLException {

    try {
      dc.setAutoCommit(false);
      trans_update(e, n, dc);
      dc.commit();
    } catch (SQLException e1) {
      GemLogger.logException("transaction update Etablissement", e1);
      dc.rollback();
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public static void trans_update(Establishment e, Establishment n, DataConnection dc) throws SQLException {
    Person p = n.getPerson();
    PersonIO pio = (PersonIO) DataCache.getDao(Model.Person);
    pio.update(p);

    Address a = n.getAddress();
    if (e.getAddress() != null) {
      if (a != null && !a.equals(e.getAddress())) {
        AddressIO.update(a, dc);
      }
    } else {
      if (a != null && a.getAdr1().length() > 0) {
        AddressIO.insert(a, dc);
      }
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
    for(; newsites != null && i < newsites.size(); i++) {
      WebSite w = newsites.get(i);
      WebSiteIO.insert(w, i, dc);
    }

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

  public static Establishment findId(int n, DataConnection dc) throws SQLException {

    String where = " AND id = " + n;
    Vector<Establishment> v = find(where, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public static Vector<Establishment> find(String where, DataConnection dc) throws SQLException {

    Vector<Establishment> v = new Vector<Establishment>();
    String query = "SELECT " + PersonIO.COLUMNS + " FROM " + PersonIO.TABLE + " p WHERE p.ptype = " + Person.ESTABLISHMENT;
    query += where;

    ResultSet rs = dc.executeQuery(query);
    Vector<Person> pl = new Vector<Person>();
    while (rs.next()) {
      pl.addElement(PersonIO.getFromRS(rs));
    }

    if (pl.size() < 1) {
      return v;
    }
    Enumeration<Person> enu = pl.elements();
    while (enu.hasMoreElements()) {
      Person p = enu.nextElement();
      Establishment e = new Establishment(p);

      e.setAddress(AddressIO.findId(p.getId(), dc));
      e.setTele(TeleIO.findId(p.getId(), dc));
      e.setEmail(EmailIO.find(p.getId(), dc));
      e.setSites(WebSiteIO.find(p.getId(), Person.ESTABLISHMENT, dc));

      v.addElement(e);
    }

    return v;
  }
}
