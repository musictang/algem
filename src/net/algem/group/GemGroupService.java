/*
 * @(#)GemGroupService.java	2.8.p 08/11/13
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
package net.algem.group;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import net.algem.accounting.Account;
import net.algem.accounting.AccountPrefIO;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.accounting.OrderLineIO;
import net.algem.config.*;
import net.algem.contact.*;
import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.util.*;
import net.algem.util.model.Model;

/**
 * Service class for group operations.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 * @since 2.4.a 10/05/12
 */
public class GemGroupService
  implements DocService, GroupService
{

  public static final int MIN_ANNULATION = 72; //72 heures minimum before annulation TODO set config parameter
  private DataConnection dc;
  private GroupIO groupIO;
  private ActionIO actionIO;

  public GemGroupService(DataConnection dc) {
    this.dc = dc;
    groupIO = (GroupIO) DataCache.getDao(Model.Group);
    actionIO = (ActionIO) DataCache.getDao(Model.Action);
  }

  @Override
  public void create(Group g) throws SQLException {
    groupIO.insert(g);
    Vector<WebSite> sites = g.getSites();
    if (sites != null) {
      for (int i = 0; i < sites.size(); i++) {
        WebSite s = sites.elementAt(i);
        s.setIdper(g.getId());
        s.setPtype(Person.GROUP);
        WebSiteIO.insert(s, i, dc);
      }
    }

  }

  @Override
  public void create(int g, Musician m) throws SQLException {
    groupIO.insert(g, m);
  }

  @Override
  public void update(Group old, Group g) throws SQLException {
    groupIO.update(g);
    update(old.getSites(), g);
  }

  private void update(Vector<WebSite> oldsites, Group g) throws SQLException {
    new ContactIO(dc).updateSites(oldsites, g.getSites(), g.getId(), Person.GROUP);
  }

  @Override
  public void update(int g, Vector<Musician> vm, Vector<Musician> om) throws GroupException {
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate("DELETE FROM " + GroupIO.TABLE_DETAIL + " WHERE id = " + g);
      if (om != null) {
        for (Musician m : om) {
          InstrumentIO.delete(m.getId(), Instrument.MUSICIAN, dc);
        }
      }
      if (vm != null) {
        for (Musician m : vm) {
          create(g, m);
        }
      }
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.log(getClass().getName() + "#update " + ex.getMessage());
      throw new GroupException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  @Override
  public void delete(Group g) throws GroupException {
    String where = " WHERE idper = " + g.getId() + " AND ptype = " + Schedule.GROUP_SCHEDULE;

    try {
      Vector<ScheduleObject> vp = ScheduleIO.findObject(where, dc);
      if (vp != null && vp.size() > 0) {
        throw new GroupException(String.valueOf(vp.size()));
      }
    } catch (SQLException e) {
      throw new GroupException(e.getMessage());
    }
    try {
      dc.setAutoCommit(false);
      groupIO.delete(g);
      // instruments suppression
      if (g.getMusicians() != null) {
        for (Musician m : g.getMusicians()) {
          InstrumentIO.delete(m.getId(), Instrument.MUSICIAN, dc);
        }
      }
      // group detail suppression
      String query = "DELETE FROM " + GroupIO.TABLE_DETAIL + " WHERE id = " + g.getId();
      dc.executeUpdate(query);
      dc.commit();
    } catch (SQLException se) {
      dc.rollback();
      throw new GroupException(se.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }

  }

  @Override
  public Vector<Group> findAll(String order) throws SQLException {
    return groupIO.find(order);
  }

  @Override
  public Group find(int id) throws SQLException {
    return groupIO.findId(id);
  }

  @Override
  public Vector<Group> find(String where) throws SQLException {
    return groupIO.find(where);
  }

  Contact getContact(int idper) {
    return ContactIO.findId(idper, dc);
  }

  Note getNote(int idper) throws NoteException {
    return NoteIO.findId(idper, Person.GROUP, dc);
  }

  void create(Note n) throws SQLException {
    NoteIO.insert(n, dc);
  }

  void update(Note n) throws SQLException {
    NoteIO.update(n, dc);
  }

  @Override
  public Vector<Musician> getMusicians(Group g) throws SQLException {
    return groupIO.findMusicians(g);
  }

  @Override
  public Vector<Musician> getMusicians(int idper) throws SQLException {
    return groupIO.findMusicians(idper);
  }

  @Override
  public Vector<Group> getGroups(int idper) throws SQLException {
    return groupIO.find(idper);
  }

  Vector<WebSite> getSites(int id) throws SQLException {
    return WebSiteIO.find(id, Person.GROUP, dc);
  }

  Vector<Param> getCategoriesSite() {
    return ParamTableIO.find(Category.SITEWEB.getTable(), Category.SITEWEB.getCol(), dc);
  }

  Vector<Schedule> getRehearsalHisto(int g, DateFr start, DateFr end, boolean all) {
    String query = " WHERE p.ptype = " + Schedule.GROUP_SCHEDULE + " AND p.idper = " + g;
    if (!all) {
      query += " AND jour BETWEEN '" + start + "' AND '" + end + "'";
    }
    query += " ORDER BY jour,debut";
    return ScheduleIO.find(query, dc);
  }

  Vector<OrderLine> getSchedulePayment(Group g)  {
    int membershipAccount = 0;
    StringBuilder where = new StringBuilder("WHERE groupe = ");
    where.append(g.getId());
    List<Musician> lm = g.getMusicians();
    if (lm != null) {
      try {
        Preference p = AccountPrefIO.find(AccountPrefIO.MEMBER_KEY_PREF, dc);
        Account a = AccountPrefIO.getAccount(p, dc);
        membershipAccount = a.getId();
      } catch (SQLException ex) {
        GemLogger.log(ex.getMessage());
      }
      where.append("OR (adherent IN (");
      for (Musician m : lm) {
        where.append(m.getId()).append(",");
      }
      where.deleteCharAt(where.length()-1);
      where.append(") AND compte = ").append(membershipAccount);
      where.append(")");
    }

    return OrderLineIO.find(where.toString(), dc);
  }

  /**
   * Room occupation test.
   *
   * @param date
   * @param start start time
   * @param end end time
   * @param room room id
   * @return a number of schedules
   */
  int testRoomConflict(String date, String start, String end, int room) {
    String query = ConflictQueries.getRoomConflictSelection(date, start, end, room);
    return ScheduleIO.count(query, dc);
  }

  /**
   * Test group occupation
   *
   * @param date
   * @param start start time
   * @param end end time
   * @param g group id
   * @return a number of schedules
   */
  int testGroupConflict(String date, String start, String end, int g) {
    String query = ConflictQueries.getGroupConflictSelection(date, start, end, g);
    return ScheduleIO.count(query, dc);
  }

  /**
   * Saves a single rehearsal.
   *
   * @param date date of rehearsal
   * @param start start time
   * @param end end time
   * @param g group
   * @param rn room id
   * @throws GroupException if error SQL
   */
  void createRehearsal(DateFr date, Hour start, Hour end, Group g, int rn) throws GroupException {
    ScheduleDTO dto = new ScheduleDTO();

    dto.setDay(date.toString());
    dto.setStart(start.toString());
    dto.setEnd(end.toString());
    dto.setType(Schedule.GROUP_SCHEDULE);
    dto.setPersonId(g.getId());
    dto.setPlace(rn);
    dto.setNote(0);
    try {
      dc.setAutoCommit(false);
      Action a = new Action();
      actionIO.insert(a);
      dto.setAction(a.getId());
      ScheduleIO.insert(dto, dc);

      Room room = ((RoomIO) DataCache.getDao(Model.Room)).findId(rn);
      double amount = RehearsalUtil.calcSingleRehearsalAmount(start, end, room.getRate(), getNumberOfMusicians(g), dc);
      Person ref = ((PersonIO) DataCache.getDao(Model.Person)).findId(g.getIdref());
      // Echéance référent
      if (ref != null && ref.getId() > 0) {
        PersonFile dossier = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findId(ref.getId());
        OrderLine ol = AccountUtil.setGroupOrderLine(g.getId(), dossier, date, getAccount(AccountPrefIO.REHEARSAL_KEY_PREF), amount);
        String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey(), dc);
        ol.setSchool(Integer.parseInt(s));
        AccountUtil.createEntry(ol, dc);
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new GroupException(MessageUtil.getMessage("rehearsal.create.exception") + "\n" + sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  void createPassRehearsal(Vector<DateFr> dates, Hour start, Hour end, int g, int room) throws GroupException {

    try {
      dc.setAutoCommit(false);
      Action a = new Action();
      actionIO.insert(a);
      ScheduleDTO dto = new ScheduleDTO();
      dto.setType(Schedule.GROUP_SCHEDULE);
      dto.setPersonId(g);
      dto.setPlace(room);
      dto.setNote(0);
      dto.setStart(start.toString());
      dto.setEnd(end.toString());
      dto.setAction(a.getId());

      for (int i = 0; i < dates.size(); i++) {
        DateFr d = dates.elementAt(i);
        dto.setDay(d.toString());
        ScheduleIO.insert(dto, dc);
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new GroupException(MessageUtil.getMessage("rehearsal.create.exception") + "\n" + sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }

  }

  /**
   * Generates a list of dates for rehearsal between {@code startDate} and {@code endDate}.
   * The date of week is the same for each date.
   *
   * @param date date of week
   * @param startDate start date
   * @param endDate end date
   * @return a list of dates
   */
  Vector<DateFr> generateDates(int day, DateFr startDate, DateFr endDate) {
    Vector<DateFr> v = new Vector<DateFr>();

    Calendar debut = Calendar.getInstance(Locale.FRANCE);
    debut.setTime(startDate.getDate());
    Calendar fin = Calendar.getInstance(Locale.FRANCE);
    fin.setTime(endDate.getDate());

    while (!debut.after(fin)) {
      if (debut.get(Calendar.DAY_OF_WEEK) == day + 1) {
        v.addElement(new DateFr(debut.getTime()));
      }
      debut.add(Calendar.DATE, 1);
    }
    return v;
  }

  /**
   * Gets the number of musicians in the group.
   *
   * @return an integer
   */
  private int getNumberOfMusicians(Group g) {
    int nm = 1;// nombre de musiciens
    Vector<Musician> vm = null;
    try {
      vm = groupIO.findMusicians(g);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (vm != null && vm.size() > 1) {
      nm = vm.size();
    }
    return nm;
  }

  Vector<ScheduleTestConflict> testConflict(Vector<DateFr> dates, Hour start, Hour end, int g, int room) {

    Vector<ScheduleTestConflict> vc = new Vector<ScheduleTestConflict>();
    String startHour = start.toString();
    String endHour = end.toString();

    for (int i = 0; i < dates.size(); i++) {
      DateFr d = dates.elementAt(i);
      ScheduleTestConflict conflict = new ScheduleTestConflict(d, start, end);

      String query = ConflictQueries.getRoomConflictSelection(d.toString(), startHour, endHour, room);

      if (ScheduleIO.count(query, dc) > 0) {
        conflict.setRoomFree(false);
      }
      query = ConflictQueries.getGroupConflictSelection(d.toString(), startHour, endHour, g);

      if (ScheduleIO.count(query, dc) > 0) {
        conflict.setMemberFree(false);
      }
      vc.add(conflict);
    }

    return vc;
  }

  /**
   * Gets the preferred account and cost account for this {@code key}.
   *
   * @param key category key (ex. ADHÉSIONS)
   * @return a preference
   * @throws SQLException
   */
  private Preference getAccount(String key) throws SQLException {
    return AccountPrefIO.find(key, dc);
  }

  @Override
  public String getDocumentPath() {
    return FileUtil.getDocumentPath(ConfigKey.GROUPS_PATH, dc);
  }
}
