/*
 * @(#)GemGroupService.java	2.15.2 27/09/17
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
package net.algem.group;

import java.sql.SQLException;
import java.util.ArrayList;
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
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 2.4.a 10/05/12
 */
public class GemGroupService
  implements DocService, GroupService {

  public static final int BOOKING_CANCEL_DELAY = 72; // default cancel delay if config is not set
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
  public void update(final int g, final List<Musician> vm, final List<Musician> om) throws GroupException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
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
          return null;
        }
      });

    } catch (Exception ex) {
      GemLogger.log(getClass().getName() + "#update " + ex.getMessage());
      throw new GroupException(ex.getMessage());
    }
  }

  @Override
  public void delete(final Group g) throws GroupException {
    String where = " WHERE idper = " + g.getId() + " AND ptype = " + Schedule.GROUP;

    try {
      Vector<ScheduleObject> vp = ScheduleIO.findObject(where, dc);
      if (vp != null && vp.size() > 0) {
        throw new GroupException(String.valueOf(vp.size()));
      }
    } catch (SQLException e) {
      throw new GroupException(e.getMessage());
    }
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
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
          return null;
        }
      });

    } catch (Exception se) {
      GemLogger.log(getClass().getName() + "#update " + se.getMessage());
      throw new GroupException(se.getMessage());
    }

  }

  @Override
  public List<Group> findAll(String order) throws SQLException {
    return groupIO.find(order);
  }

  @Override
  public Group find(int id) throws SQLException {
    return groupIO.findId(id);
  }

  @Override
  public List<Group> find(String where) throws SQLException {
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
  public List<Musician> getMusicians(Group g) throws SQLException {
    return groupIO.findMusicians(g);
  }

  @Override
  public List<Musician> getMusicians(int idper) throws SQLException {
    return groupIO.findMusicians(idper);
  }

  @Override
  public List<Group> getGroups(int idper) throws SQLException {
    return groupIO.find(idper);
  }

  Vector<WebSite> getSites(int id) throws SQLException {
    return WebSiteIO.find(id, Person.GROUP, dc);
  }

  Vector<Param> getCategoriesSite() {
    return ParamTableIO.find(Category.SITEWEB.getTable(), Category.SITEWEB.getCol(), dc);
  }

  Vector<Schedule> getRehearsalHisto(int g, DateFr start, DateFr end) {
    String query = " WHERE p.ptype = " + Schedule.GROUP + " AND p.idper = " + g;
    if (start != null && end != null) {
      query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'";
    }
    query += " ORDER BY p.jour DESC, p.debut";
    return ScheduleIO.find(query, dc);
  }

  /**
   * Gets the payment schedules of the group {@literal g}.
   *
   * @param g group instance
   * @return a list of order lines
   */
  Vector<OrderLine> getSchedulePayment(Group g) {
    int membershipAccount = 0;
    StringBuilder where = new StringBuilder("WHERE (groupe > 0 AND groupe = ");
    where.append(g.getId()).append(')');
    List<Musician> lm = g.getMusicians();
    if (lm != null) {
      try {
        Preference p = AccountPrefIO.find(AccountPrefIO.MEMBERSHIP, dc);
        Account a = AccountPrefIO.getAccount(p, dc);
        membershipAccount = a.getId();
      } catch (SQLException ex) {
        GemLogger.log(ex.getMessage());
      }
      where.append(" OR (adherent IN (");
      for (Musician m : lm) {
        where.append(m.getId()).append(",");
      }
      where.deleteCharAt(where.length() - 1);
      where.append(") AND compte = ").append(membershipAccount);
      where.append(")");
    }

    return OrderLineIO.find(where.toString(), dc);
  }

  /**
   * Gets the payment schedules of the persons in the group {@literal g}.
   *
   * @param g group instance
   * @return a list of order lines
   */
  Vector<OrderLine> getMemberSchedulePayment(Group g) {
    List<Musician> lm = g.getMusicians();
    if (lm == null) {
      return new Vector<OrderLine>();
    }
    StringBuilder where = new StringBuilder("WHERE adherent IN (");
    for (Musician m : lm) {
      where.append(m.getId()).append(",");
    }
    where.deleteCharAt(where.length() - 1);
    where.append(")");
    return OrderLineIO.find(where.toString(), dc);
  }

  /**
   * Changes the group number of the order lines with the selected {@literal oids}.
   *
   * @param oids the list of lines to change
   * @param g group number
   * @throws SQLException
   */
  public void updateOrderLine(int oids[], int g) throws SQLException {
    OrderLineIO.setGroup(oids, g, dc);
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
   * Test group occupation.
   *
   * @param date selected date
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
    ScheduleObject dto = new GroupRehearsalSchedule();

    dto.setDate(date);
    dto.setStart(start);
    dto.setEnd(end);
    dto.setType(Schedule.GROUP);
    dto.setIdPerson(g.getId());
    dto.setIdRoom(rn);
    dto.setNote(0);
    try {
      dc.setAutoCommit(false);
      Action a = new Action();
      actionIO.insert(a);
      dto.setIdAction(a.getId());
      ScheduleIO.insert(dto, dc);

      Room room = ((RoomIO) DataCache.getDao(Model.Room)).findId(rn);
      double amount = RehearsalUtil.calcSingleRehearsalAmount(start, end, room.getRate(), getNumberOfMusicians(g), dc);
      Person ref = ((PersonIO) DataCache.getDao(Model.Person)).findById(g.getIdref());
      // Echéance référent
      if (ref != null && ref.getId() > 0) {
        PersonFile dossier = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findId(ref.getId());
        OrderLine ol = AccountUtil.setGroupOrderLine(g.getId(), dossier, date, getAccount(AccountPrefIO.REHEARSAL), amount);
        String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
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

  public void order(Schedule schedule, Group g) throws GroupException {
    Room room = ((RoomIO) DataCache.getDao(Model.Room)).findId(schedule.getIdRoom());
    double amount = RehearsalUtil.calcSingleRehearsalAmount(schedule.getStart(), schedule.getEnd(), room.getRate(), getNumberOfMusicians(g), dc);
    Person ref = ((PersonIO) DataCache.getDao(Model.Person)).findById(g.getIdref());
    // Echéance référent
    if (ref != null && ref.getId() > 0) {
      try {
        PersonFile dossier = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findId(ref.getId());
        OrderLine ol = AccountUtil.setGroupOrderLine(g.getId(), dossier, schedule.getDate(), getAccount(AccountPrefIO.REHEARSAL), amount);
        String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
        ol.setSchool(Integer.parseInt(s));
        AccountUtil.createEntry(ol, dc);
      } catch (SQLException ex) {
        throw new GroupException(ex.getMessage());
      }
    }
  }

  void createPassRehearsal(List<DateFr> dates, Hour start, Hour end, int g, int room) throws GroupException {

    try {
      dc.setAutoCommit(false);
      Action a = new Action();
      actionIO.insert(a);
      ScheduleObject dto = new GroupRehearsalSchedule();
      dto.setType(Schedule.GROUP);
      dto.setIdPerson(g);
      dto.setIdRoom(room);
      dto.setNote(0);
      dto.setStart(start);
      dto.setEnd(end);
      dto.setIdAction(a.getId());

      for (int i = 0; i < dates.size(); i++) {
        DateFr d = dates.get(i);
        dto.setDate(d);
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
   * Generates a list of dates of rehearsal between {@literal startDate} and {@literal endDate}.
   * The day of week is the same for each date.
   *
   * @param date date of week
   * @param startDate start date
   * @param endDate end date
   * @return a list of dates
   */
  Vector<DateFr> generateDates(int day, DateFr startDate, DateFr endDate) {
    Vector<DateFr> v = new Vector<DateFr>();

    Calendar start = Calendar.getInstance(Locale.FRANCE);
    start.setTime(startDate.getDate());
    Calendar end = Calendar.getInstance(Locale.FRANCE);
    end.setTime(endDate.getDate());

    while (!start.after(end)) {
      if (start.get(Calendar.DAY_OF_WEEK) == day) {
        v.addElement(new DateFr(start.getTime()));
      }
      start.add(Calendar.DATE, 1);
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
    List<Musician> vm = null;
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

  List<ScheduleTestConflict> testConflict(List<DateFr> dates, Hour start, Hour end, int g, int room) {

    List<ScheduleTestConflict> vc = new ArrayList<ScheduleTestConflict>();
    String startHour = start.toString();
    String endHour = end.toString();

    for (int i = 0; i < dates.size(); i++) {
      DateFr d = dates.get(i);
      ScheduleTestConflict conflict = new ScheduleTestConflict(d, start, end);

      String query = ConflictQueries.getRoomConflictSelection(d.toString(), startHour, endHour, room);
      if (ScheduleIO.count(query, dc) > 0) {
        conflict.setRoomFree(false);
        conflict.setActive(false);
      }

      query = ConflictQueries.getGroupConflictSelection(d.toString(), startHour, endHour, g);
      if (ScheduleIO.count(query, dc) > 0) {
        conflict.setMemberFree(false);
        conflict.setActive(false);
      }
      vc.add(conflict);
    }

    return vc;
  }

  /**
   * Gets the preferred account and cost account corresponding to this {@literal key}.
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
