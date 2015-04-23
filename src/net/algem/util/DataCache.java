/*
 * @(#)DataCache.java	2.9.4.0 06/04/15
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
package net.algem.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JMenuItem;
import net.algem.Algem.GemBoot;
import net.algem.accounting.*;
import net.algem.billing.Item;
import net.algem.billing.ItemIO;
import net.algem.billing.Vat;
import net.algem.config.*;
import net.algem.contact.EmployeeTypeIO;
import net.algem.contact.Person;
import net.algem.contact.PersonFileIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberIO;
import net.algem.contact.member.RehearsalPass;
import net.algem.contact.member.RehearsalPassIO;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherComparator;
import net.algem.contact.teacher.TeacherEvent;
import net.algem.contact.teacher.TeacherIO;
import net.algem.course.*;
import net.algem.group.*;
import net.algem.planning.*;
import net.algem.planning.day.DaySchedule;
import net.algem.planning.editing.instruments.AtelierInstrumentsDAO;
import net.algem.planning.editing.instruments.AtelierInstrumentsService;
import net.algem.planning.editing.instruments.AtelierInstrumentsServiceImpl;
import net.algem.planning.fact.services.PlanningFactCreator;
import net.algem.planning.fact.services.PlanningFactDAO;
import net.algem.planning.fact.services.PlanningFactService;
import net.algem.planning.month.MonthSchedule;
import net.algem.room.*;
import net.algem.security.DefaultUserService;
import net.algem.security.User;
import net.algem.security.UserIO;
import net.algem.security.UserService;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Cacheable;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.model.Model;

/**
 * Cache and various utilities.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 1.0b 03/09/2001
 */
public class DataCache
{

  private static volatile DataCache INSTANCE;
  private static MemberIO MEMBER_IO;
  private static PersonIO PERSON_IO;
  private static PersonFileIO PERSON_FILE_IO;
  private static TeacherIO TEACHER_IO;
  private static RoomIO ROOM_IO;
  private static RoomRateIO ROOM_RATE_IO;
  private static GroupIO GROUP_IO;
  private static CourseIO COURSE_IO;
  private static ModuleIO MODULE_IO;
  private static MusicStyleIO MUSIC_STYLE_IO;
  private static AgeRangeIO AGE_RANGE_IO;
  private static LevelIO LEVEL_IO;
  private static StatusIO STATUS_IO;
  private static CourseCodeIO COURSE_CODE_IO;
  private static ActionIO ACTION_IO;
  private static UserIO USER_IO;
  private static ItemIO ITEM_IO;
  private static EmployeeTypeIO EMPLOYEE_TYPE_IO;
  private static StudioTypeIO STUDIO_TYPE_IO;
  private static GemParamIO MARITAL_STATUS_IO;

  // Cache data
  private static Hashtable<Integer, List<Integer>> TEACHER_INSTRUMENT_CACHE = new Hashtable<Integer, List<Integer>>();
  private static Hashtable<String, Param> COST_ACCOUNT_CACHE = new Hashtable<String, Param>();
  private static Hashtable<Integer, User> USER_CACHE = new Hashtable<Integer, User>();
  private static Hashtable<Integer, Person> PERSON_CACHE = new Hashtable<Integer, Person>();
  private static Hashtable<Integer, Member> MEMBER_CACHE = new Hashtable<Integer, Member>();
  private static Hashtable<Integer, Action> ACTION_CACHE = new Hashtable<Integer, Action>();
  private static Hashtable<Integer, Item> ITEM_CACHE = new Hashtable<Integer, Item>();
  private static Hashtable<Integer, OrderLine> ORDER_LINE_CACHE = new Hashtable<Integer, OrderLine>();
  private static Hashtable<Integer, RehearsalPass> PASS_CARD = new Hashtable<Integer, RehearsalPass>();

  private static GemList<Course> COURSE_LIST;
  private static GemList<Course> WORKSHOP_LIST;
  private static GemList<Teacher> TEACHER_LIST;
  private static GemList<Group> GROUP_LIST;
  private static GemList<Room> ROOM_LIST;
  private static GemList<RoomRate> ROOM_RATE_LIST;
  private static GemList<Module> MODULE_LIST;
  private static GemList<AgeRange> AGE_RANGE_LIST;
  private static GemList<GemParam> LEVEL_LIST;
  private static GemList<GemParam> STATUS_LIST;
  private static GemList<MusicStyle> STYLE_LIST;
  private static GemList<Establishment> ESTAB_LIST;
  private static GemList<Param> SCHOOL_LIST;
  private static GemList<Account> ACCOUNT_LIST;
  private static GemList<Vat> VAT_LIST;
  private static GemList<GemParam> COURSE_CODE_LIST;
  private static GemList<GemParam> EMPLOYEE_TYPE_LIST;
  private static GemList<GemParam> STUDIO_TYPE_LIST;
  private static GemList<GemParam> MARITAL_STATUS_LIST;

  private static Vector<Instrument> instruments;//TODO manage list
  private Vector<CategoryOccup> occupCat;
  private Vector<Param> vacancyCat;
  private Vector<Param> webSiteCat;

  private boolean cacheInit = false;
  private User user;
  private DateFr startOfPeriod;
  private DateFr startOfYear;
  private DateFr endOfYear;
  private DateFr endOfPeriod;
  private PreparedStatement loadMonthStmt;
  private PreparedStatement loadMonthRangeStmt;
  private PreparedStatement loadDayStmt;
  private PreparedStatement loadDayRangeStmt;

  private MonthSchedule monthSchedule;
  private DaySchedule daySchedule;
  private Thread monthThread;

  private static DataConnection dc;
  private UserService userService;

  private AtelierInstrumentsService atelierInstrumentsService;
  private PlanningFactService planningFactService;

  private DataCache() {

  }

  /**
   * Private constructor.
   *
   * @param dc
   * @param login
   */
  private DataCache(DataConnection dc, String login) {

    this.dc = dc;
    PERSON_IO = new PersonIO(dc);
    MEMBER_IO = new MemberIO(dc);
    TEACHER_IO = new TeacherIO(dc);
    PERSON_FILE_IO = new PersonFileIO(this);
    COURSE_IO = new CourseIO(dc);
    ROOM_RATE_IO = new RoomRateIO(dc);
    ROOM_IO = new RoomIO(dc);
    GROUP_IO = new GroupIO(dc);
    MODULE_IO = new ModuleIO(dc);
    MUSIC_STYLE_IO = new MusicStyleIO(dc);
    AGE_RANGE_IO = new AgeRangeIO(dc);
    LEVEL_IO = new LevelIO(dc);
    STATUS_IO = new StatusIO(dc);
    ACTION_IO = new ActionIO(dc);
    USER_IO = new UserIO(dc);
    ITEM_IO = new ItemIO(dc);
    COURSE_CODE_IO = new CourseCodeIO(dc);
    EMPLOYEE_TYPE_IO = new EmployeeTypeIO(dc);
    STUDIO_TYPE_IO = new StudioTypeIO(dc);
    MARITAL_STATUS_IO = new MaritalStatusIO(dc);

    loadMonthStmt = dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p WHERE jour >= ? AND jour <= ? ORDER BY p.jour,p.debut");
    loadMonthRangeStmt = dc.prepareStatement(ScheduleRangeIO.getMonthRangeStmt());
    loadDayStmt = dc.prepareStatement("SELECT " + ScheduleIO.COLUMNS + " FROM planning p WHERE jour = ? ORDER BY p.debut");//ORDER BY p.action,p.debut
    loadDayRangeStmt = dc.prepareStatement(ScheduleRangeIO.getDayRangeStmt());

    userService = new DefaultUserService(this);
    user = ((DefaultUserService) userService).find(login);

    monthSchedule = new MonthSchedule();
    daySchedule = new DaySchedule();

    atelierInstrumentsService = new AtelierInstrumentsServiceImpl(dc, new AtelierInstrumentsDAO(dc), PERSON_IO);
    planningFactService = new PlanningFactService(dc, new PlanningService(dc), new PlanningFactDAO(dc),
            new PlanningFactCreator(), new PlanningFactService.RoomFinder(),
            new PlanningFactService.ScheduleUpdater(dc));
  }

  /**
   * Singleton access.
   *
   * @param dc
   * @param user
   * @return une instance de DataCache
   */
  public static DataCache getInstance(DataConnection dc, String user) {
    if (INSTANCE == null) {
      synchronized (DataCache.class) {
        if (INSTANCE == null) {
          INSTANCE = new DataCache(dc, user);
        }
      }
    }
    return INSTANCE;
  }

  public static DataCache getInitializedInstance() {
    return INSTANCE;
  }

  /**
   * Gets the connection.
   *
   * @return a connection
   */
  public static DataConnection getDataConnection() {
    return dc;
  }

   /**
   * Generic getter for GemList instances.
   * @param model enumeration model
   * @return an instance of GemList
   */
  public GemList getList(Model model) {
    switch(model) {
      case Room :
        return ROOM_LIST;
      case Establishment:
        return ESTAB_LIST;
      case Teacher :
        return TEACHER_LIST;
      case Group :
        return GROUP_LIST;
      case Course:
        return COURSE_LIST;
      case Workshop:
        return WORKSHOP_LIST;
      case Module:
        return MODULE_LIST;
      case RoomRate:
        return ROOM_RATE_LIST;
      case MusicStyle:
        return STYLE_LIST;
      case AgeRange:
        return AGE_RANGE_LIST;
      case Status:
        return STATUS_LIST;
      case Level:
        return LEVEL_LIST;
      case Account:
        return ACCOUNT_LIST;
      case Vat:
        return VAT_LIST;
      case CourseCode:
        return COURSE_CODE_LIST;
      case School:
        return SCHOOL_LIST;
      case EmployeeType:
        return EMPLOYEE_TYPE_LIST;
      case StudioType:
        return STUDIO_TYPE_LIST;
      case MaritalStatus:
        return MARITAL_STATUS_LIST;
      default: return null;
    }

  }

  /**
   * Gets the correct DAO instance for model {@literal m}.
   * @param m model type
   * @return an instance of {@link net.algem.util.model.Cacheable }
   */
  public static Cacheable getDao(Model m) {
    switch(m) {
      case Room :
        return ROOM_IO;
      case RoomRate :
        return ROOM_RATE_IO;
      case Teacher :
        return TEACHER_IO;
      case Member:
        return MEMBER_IO;
      case Group :
        return GROUP_IO;
      case Course:
        return COURSE_IO;
      case Module:
        return MODULE_IO;
      case MusicStyle:
        return MUSIC_STYLE_IO;
      case Person:
        return PERSON_IO;
      case PersonFile:
        return PERSON_FILE_IO;
      case AgeRange:
        return AGE_RANGE_IO;
      case Action:
        return ACTION_IO;
      case Level:
        return LEVEL_IO;
      case Status:
        return STATUS_IO;
      case CourseCode:
        return COURSE_CODE_IO;
      case User:
        return USER_IO;
      default: return null;
    }
  }

  /**
   * Gets a model instance.
   * @param id
   * @param m model enumeration
   * @return an instance of {@link net.algem.util.model.GemModel }
   * @throws SQLException
   */
  public static GemModel findId(int id, Model m) throws SQLException {
    switch (m) {
      case Account:
        Account account = (Account) ACCOUNT_LIST.getItem(id);
        if (account == null) {
          account = AccountIO.find(id, dc);
          ACCOUNT_LIST.addElement(account);
        }
        return  account;
      case Action:
        Action a = ACTION_CACHE.get(id);
        if (a == null) {
          a = ACTION_IO.findId(id);
          if (a != null) {
            ACTION_CACHE.put(id, a);
          }
        }
        return a;
      case AgeRange:
        AgeRange ar = (AgeRange) AGE_RANGE_LIST.getItem(id);
        return ar != null ? ar : AGE_RANGE_IO.findId(id);
      case Course:
        Course c = (Course) COURSE_LIST.getItem(id);
        return c != null ? c : COURSE_IO.findId(id);
      case Establishment:
        Establishment e = (Establishment) ESTAB_LIST.getItem(id);
        return e != null ? e : new Establishment("NOID:" + id);
      case Group:
        Group g = (Group) GROUP_LIST.getItem(id);
        return g != null ? g : GROUP_IO.findId(id);
      case Item:
        Item it = (Item) ITEM_CACHE.get(id);
        return it != null ? it : ITEM_IO.findId(id);
      case Level:
        GemParam l = (GemParam) LEVEL_LIST.getItem(id);
        return l != null ? l : LEVEL_IO.find(id);
      case Member:
        Member mb = (Member) MEMBER_CACHE.get(id);
        return mb != null ? mb : MEMBER_IO.findId(id);
      case Module:
        Module mod = (Module) MODULE_LIST.getItem(id);
        return mod != null ? mod : MODULE_IO.findId(id);
      case MusicStyle:
        MusicStyle ms = (MusicStyle) STYLE_LIST.getItem(id);
        return ms != null ? ms : MUSIC_STYLE_IO.findId(id);
      case Person:
        Person pi = PERSON_CACHE.get(id);
        if (pi == null) {
          pi = PERSON_IO.findId(id);
          if (pi != null) {
            PERSON_CACHE.put(id, pi);
          }
        }
        return pi;
      case OrderLine:
        return (OrderLine) ORDER_LINE_CACHE.get(id);
      case PersonFile:
        return PERSON_FILE_IO.findId(id);
      case Room:
        Room r = (Room) ROOM_LIST.getItem(id);
        return r != null ? r : ROOM_IO.findId(id);
      case RoomRate:
        RoomRate rr = (RoomRate) ROOM_RATE_LIST.getItem(id);
        return rr != null ? rr : ROOM_RATE_IO.findId(id);
      case Status:
        GemParam s = (GemParam) STATUS_LIST.getItem(id);
        return s != null ? s : STATUS_IO.find(id);
      case Teacher:
        Teacher t = (Teacher) TEACHER_LIST.getItem(id);
        return t != null ? t : TEACHER_IO.findId(id);
      case User:
        User u = USER_CACHE.get(id);
        return u != null ? u : USER_IO.findId(id);
      case Vat:
        return (Vat) VAT_LIST.getItem(id);
      case CourseCode:
        GemParam cc = (GemParam) COURSE_CODE_LIST.getItem(id);
        return cc != null ? cc : COURSE_CODE_IO.find(id);
      case School:
        Param school = (Param) SCHOOL_LIST.getItem(id);
        return school != null ? school : new Param("0", "");
      case Instrument:
        for (Instrument i : instruments) {
          if (i.getId() == id) {
            return i;
          }
        }
        return null;
      case StudioType:
        GemParam stype = (GemParam) STUDIO_TYPE_LIST.getItem(id);
        return stype != null ? stype : STUDIO_TYPE_IO.find(id);
      case PassCard:
        RehearsalPass pc = PASS_CARD.get(id);
        if (pc == null) {
          pc = RehearsalPassIO.find(id, dc);
          PASS_CARD.put(pc.getId(), pc);
        }
      return pc;
      default:
        return null;
    }
  }

  public static Param getCostAccount(String code) {
    return code == null ? null : COST_ACCOUNT_CACHE.get(code);
  }

  /**
   * Adds a new element to the list in dataCache.
   * @param <T>
   * @param m model
   */
  public <T extends GemModel> void add(T m) {
    if (m instanceof Room) {
      ROOM_LIST.addElement((Room) m);
      Collections.sort(ROOM_LIST.getData(), new RoomComparator());
    } else if (m instanceof Course) {
      Course course = (Course)m;
      if (course.isATP()) {
        WORKSHOP_LIST.addElement(course);
        Collections.sort(WORKSHOP_LIST.getData(), new CourseComparator());
      } else {
        COURSE_LIST.addElement(course);
        Collections.sort(COURSE_LIST.getData(), new CourseComparator());
      }
    } else if (m instanceof Teacher) {
      Teacher t = (Teacher) m;
      TEACHER_LIST.addElement(t);
      Collections.sort(TEACHER_LIST.getData(), new TeacherComparator(ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey())));
      if(t.getInstruments() != null) {
        TEACHER_INSTRUMENT_CACHE.put(t.getId(), t.getInstruments());
      }
    } else if (m instanceof Member) {
      MEMBER_CACHE.put(m.getId(), (Member) m);
    } else if (m instanceof Person) {
      PERSON_CACHE.put(m.getId(), (Person) m);
    } else if (m instanceof Group) {
      GROUP_LIST.addElement((Group) m);
      Collections.sort(GROUP_LIST.getData(), new GroupComparator());
    } else if (m instanceof Module) {
      MODULE_LIST.addElement((Module) m);
      Collections.sort(MODULE_LIST.getData(), new ModuleComparator());
    } else if (m instanceof MusicStyle) {
      STYLE_LIST.addElement((MusicStyle) m);
      Collections.sort(STYLE_LIST.getData(), new MusicStyleComparator());
    } else if (m instanceof RoomRate) {
      ROOM_RATE_LIST.addElement((RoomRate) m);
      Collections.sort(ROOM_RATE_LIST.getData(), new RoomRateComparator());
    } else if (m instanceof Establishment) {
      ESTAB_LIST.addElement((Establishment) m);
    } else if (m instanceof AgeRange) {
      AGE_RANGE_LIST.addElement((AgeRange) m);
      Collections.sort(AGE_RANGE_LIST.getData(), new AgeRangeComparator());
    } else if (m instanceof Level) {
      LEVEL_LIST.addElement((Level) m);
    } else if (m instanceof Status) {
      STATUS_LIST.addElement((Status) m);
    } else if (m instanceof Account) {
      ACCOUNT_LIST.addElement((Account) m);
    } else if (m instanceof CostAccount) {
      COST_ACCOUNT_CACHE.put(((CostAccount) m).getKey(), (CostAccount) m);
    } else if (m instanceof User) {
      USER_CACHE.put(m.getId(), (User) m);
    } else if (m instanceof Vat) {
      VAT_LIST.addElement((Vat) m);
    } else if (m instanceof CourseCode) {
      COURSE_CODE_LIST.addElement((CourseCode) m);
    } else if (m instanceof RehearsalPass) {
      PASS_CARD.put(m.getId(), (RehearsalPass) m);
    } else if (m instanceof MaritalStatus) {
      MARITAL_STATUS_LIST.addElement((MaritalStatus) m);
    }
  }

  /**
   * Updates an element in the list in dataCache.
   * @param m
   */
  public void update(GemModel m) {
    if (m instanceof Room) {
      ROOM_LIST.update((Room) m, new RoomComparator());
      Person c = ((Room) m).getContact();
      PERSON_CACHE.put(c.getId(), c);
    } else if (m instanceof Establishment) {
      ESTAB_LIST.update((Establishment) m, null);
    } else if (m instanceof Course) {
      Course course = (Course)m;
      if (course.isATP()) {
        WORKSHOP_LIST.update(course, new CourseComparator());
      } else {
        COURSE_LIST.update(course, new CourseComparator());
      }
    } else if (m instanceof Teacher) {
      Teacher t = (Teacher) m;
      TEACHER_LIST.update(t, new TeacherComparator(ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey())));
      if(t.getInstruments() != null) {
        TEACHER_INSTRUMENT_CACHE.put(t.getId(), t.getInstruments());
      }
    } else if (m instanceof Member) {
      MEMBER_CACHE.put(m.getId(), (Member) m);
    } else if (m instanceof Person) {
      PERSON_CACHE.put(m.getId(), (Person) m);
      Teacher t = (Teacher) TEACHER_LIST.getItem(m.getId());
      if (t != null) {
        t.setFirstName(((Person) m).getFirstName());
        t.setName(((Person) m).getName());
        TEACHER_LIST.update(t, new TeacherComparator(ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey())));
      }
    } else if (m instanceof Group) {
      GROUP_LIST.update((Group) m, new GroupComparator());
    } else if (m instanceof Module) {
      MODULE_LIST.update((Module) m, new ModuleComparator());
    } else if (m instanceof MusicStyle) {
      STYLE_LIST.update((MusicStyle) m, new MusicStyleComparator());
    } else if (m instanceof RoomRate) {
      ROOM_RATE_LIST.update((RoomRate) m, new RoomRateComparator());
    } else if (m instanceof AgeRange) {
      AGE_RANGE_LIST.update((AgeRange) m, new AgeRangeComparator());
    } else if (m instanceof Level) {
      LEVEL_LIST.update((Level) m, null);
    } else if (m instanceof Status) {
      STATUS_LIST.update((Status) m, null);
    } else if (m instanceof Account) {
      ACCOUNT_LIST.update((Account) m, null);
    } else if (m instanceof CostAccount) {
      COST_ACCOUNT_CACHE.put(((CostAccount)m).getKey(), (CostAccount) m);
    } else if (m instanceof User) {
      USER_CACHE.put(m.getId(), (User) m);
    } else if (m instanceof Action) {
      ACTION_CACHE.put(m.getId(), (Action) m);
    } else if (m instanceof Vat) {
      VAT_LIST.update((Vat) m, null);
    } else if (m instanceof CourseCode) {
      COURSE_CODE_LIST.update((CourseCode) m, null);
    } else if (m instanceof Item) {
      ITEM_CACHE.put(m.getId(), (Item) m);
    } else if (m instanceof RehearsalPass) {
      PASS_CARD.put(m.getId(), (RehearsalPass) m);
    } else if (m instanceof MaritalStatus) {
      MARITAL_STATUS_LIST.update((MaritalStatus) m, null);
    }

  }

  /**
   * Removes an element from the list in dataCache.
   * @param m
   */
  public void remove(GemModel m) {
    if (m instanceof Room) {
      ROOM_LIST.removeElement((Room) m);
    } else if (m instanceof Establishment) {
      ESTAB_LIST.removeElement((Establishment) m);
    } else if (m instanceof Course) {
      Course course = (Course)m;
      if (course.isATP()) {
        WORKSHOP_LIST.removeElement(course);
      } else {
        COURSE_LIST.removeElement(course);
      }
    } else if (m instanceof Teacher) {
      TEACHER_INSTRUMENT_CACHE.remove(m.getId());
      TEACHER_LIST.removeElement((Teacher) m);
    } else if (m instanceof Person) {
      PERSON_CACHE.remove(m.getId());
    } else if (m instanceof Group) {
      GROUP_LIST.removeElement((Group) m);
    } else if (m instanceof Module) {
      MODULE_LIST.removeElement((Module) m);
    } else if (m instanceof MusicStyle) {
      STYLE_LIST.removeElement((MusicStyle) m);
    } else if (m instanceof RoomRate) {
      ROOM_RATE_LIST.removeElement((RoomRate) m);
    } else if (m instanceof AgeRange) {
      AGE_RANGE_LIST.removeElement((AgeRange) m);
    } else if (m instanceof Level) {
      LEVEL_LIST.removeElement((Level) m);
    } else if (m instanceof Status) {
      STATUS_LIST.removeElement((Status) m);
    } else if (m instanceof Account) {
      ACCOUNT_LIST.removeElement((Account) m);
    } else if (m instanceof CostAccount) {
      COST_ACCOUNT_CACHE.remove(((CostAccount) m).getKey());
    } else if (m instanceof User) {
      USER_CACHE.remove(m.getId());
    } else if (m instanceof Vat) {
      VAT_LIST.removeElement((Vat) m);
    } else if (m instanceof CourseCode) {
      COURSE_CODE_LIST.removeElement((CourseCode) m);
    } else if (m instanceof RehearsalPass) {
      PASS_CARD.remove(m.getId());
    } else if (m instanceof MaritalStatus) {
      MARITAL_STATUS_LIST.removeElement((MaritalStatus) m);
    }

  }

  public void remoteEvent(GemEvent _evt) {
    System.out.println("DataCache.remoteEvent:" + _evt);
    switch (_evt.getType()) {
      case GemEvent.COURSE:
        Course c = ((CourseEvent) _evt).getCourse();
        if (_evt.getOperation() == GemEvent.CREATION) {
          add(c);
        } else if (_evt.getOperation() == GemEvent.MODIFICATION) {
          update(c);
        } else if (_evt.getOperation() == GemEvent.SUPPRESSION) {
          remove(c);
        }
        break;
      case GemEvent.CONTACT:
        sync(_evt, (Person) _evt.getObject());
        break;
      case GemEvent.MEMBER:
        sync(_evt, (Member) _evt.getObject());
        break;
      case GemEvent.TEACHER:
        Teacher t = ((TeacherEvent) _evt).getTeacher();
        if (_evt.getOperation() == GemEvent.CREATION) {
          add(t);
        } else if (_evt.getOperation() == GemEvent.MODIFICATION) {
          update(t);
        }
        break;

      case GemEvent.ROOM:
        if (_evt.getOperation() == GemEvent.CREATION) {
          add(((RoomCreateEvent) _evt).getRoom());
        } else if (_evt.getOperation() == GemEvent.MODIFICATION) {
          update(((RoomUpdateEvent) _evt).getRoom());
        } else if (_evt.getOperation() == GemEvent.SUPPRESSION) {
          remove(((RoomDeleteEvent) _evt).getRoom());
        }
        break;
      case GemEvent.ROOM_RATE:
        sync(_evt, (RoomRate) _evt.getObject());
        break;
      case GemEvent.ESTABLISHMENT:
        sync(_evt, (Establishment) _evt.getObject());
        break;

      case GemEvent.GROUP:
        if (_evt.getOperation() == GemEvent.CREATION) {
          add(((GroupCreateEvent) _evt).getGroup());
        } else if (_evt.getOperation() == GemEvent.MODIFICATION) {
            update(((GroupUpdateEvent) _evt).getGroup());
        } else if (_evt.getOperation() == GemEvent.SUPPRESSION) {
          remove(((GroupDeleteEvent) _evt).getGroup());
        }
        break;

      case GemEvent.MUSIC_STYLE:
        sync(_evt, ((MusicStyleEvent) _evt).getMusicStyle());
        break;

      case GemEvent.MODULE:
        sync(_evt, ((ModuleEvent) _evt).getModule());
        break;

      case GemEvent.AGE_RANGE:
        sync(_evt, (AgeRange) _evt.getObject());
        break;

      case GemEvent.STATUS:
        sync(_evt, (Status) _evt.getObject());
        break;

     case GemEvent.LEVEL:
        sync(_evt, (Level) _evt.getObject());
        break;

     case GemEvent.COURSE_CODE:
        sync(_evt, (CourseCode) _evt.getObject());
        break;

     case GemEvent.USER:
        sync(_evt, (User) _evt.getObject());
        break;

     case GemEvent.ACCOUNT:
        sync(_evt, (Account) _evt.getObject());
        break;
     case GemEvent.VAT:
       sync(_evt, (Vat) _evt.getObject());
       break;
    }
  }

  private <T extends GemModel> void sync(GemEvent evt, T obj) {
    if (evt.getOperation() == GemEvent.CREATION) {
      add(obj);
    } else if (evt.getOperation() == GemEvent.MODIFICATION) {
      update(obj);
    } else if (evt.getOperation() == GemEvent.SUPPRESSION) {
      remove(obj);
    }
  }

  /**
   * Initial loading.
   * @param frame (optional) to display messages
   */
  public void load(GemBoot frame) {
    try {

      showMessage(frame, BundleUtil.getLabel("Loading.label") + "...");
      setConfig();

      ROOM_RATE_LIST = new GemList<RoomRate>(ROOM_RATE_IO.load());
      loadRoomContactCache();
      Vector<Param> schools = ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.COLUMN_KEY, dc);
      SCHOOL_LIST = new GemList<Param>(schools);
      ESTAB_LIST = new GemList<Establishment>(EstablishmentIO.find(" ORDER BY p.nom", dc));

      showMessage(frame, BundleUtil.getLabel("Room.label"));
      ROOM_LIST = new GemList<Room>(ROOM_IO.load());

      showMessage(frame, BundleUtil.getLabel("Course.label"));
      COURSE_LIST = new GemList<Course>(COURSE_IO.load());

      showMessage(frame, BundleUtil.getLabel("Workshop.reading.label"));
      WORKSHOP_LIST = new GemList<Course>(COURSE_IO.load("WHERE c.code = '" + CourseCodeType.ATP.getId() + "' AND titre !~* 'DEFIN' ORDER BY c.titre"));

      showMessage(frame, BundleUtil.getLabel("Instruments.label"));
      instruments = InstrumentIO.find("ORDER BY nom", dc);

      showMessage(frame, BundleUtil.getLabel("Teachers.label"));
      loadTeacherInstrumentCache();
      TEACHER_LIST = new GemList<Teacher>(TEACHER_IO.load());

      showMessage(frame, BundleUtil.getLabel("Modules.label"));
      // important : before module
      COURSE_CODE_LIST = new GemList<GemParam>(COURSE_CODE_IO.load());
      MODULE_LIST = new GemList<Module>(MODULE_IO.load());

      occupCat = CategoryOccupIO.find("ORDER BY nom", dc);
      vacancyCat = ParamTableIO.find(Category.VACANCY.getTable(), Category.VACANCY.getCol(), dc);
      webSiteCat = ParamTableIO.find(Category.SITEWEB.getTable(), Category.SITEWEB.getCol(), dc);

      showMessage(frame, BundleUtil.getLabel("Scheduling.label"));
      loadScheduleCache();

      showMessage(frame, BundleUtil.getLabel("Menu.style.label"));
      STYLE_LIST = new GemList<MusicStyle>(MUSIC_STYLE_IO.load());

      showMessage(frame, BundleUtil.getLabel("Groups.label"));
      GROUP_LIST = new GemList<Group>(GROUP_IO.load());

      showMessage(frame, BundleUtil.getLabel("Accounting.label"));
      loadAccountingCache();

//      showMessage(frame, BundleUtil.getLabel("Billing.label"));
//      loadBillingCache();

      for(User u : USER_IO.load()) {
        USER_CACHE.put(u.getId(), u);
      }

      EMPLOYEE_TYPE_LIST = new GemList<GemParam>(EMPLOYEE_TYPE_IO.load());
      MARITAL_STATUS_LIST = new GemList<>(MARITAL_STATUS_IO.find());

      STUDIO_TYPE_LIST = new GemList<GemParam>(STUDIO_TYPE_IO.load());
      PASS_CARD = new Hashtable<Integer,RehearsalPass>();
      for (RehearsalPass c : RehearsalPassIO.findAll("ORDER BY id", dc)) {
        PASS_CARD.put(c.getId(), c);
      }

    } catch (SQLException ex) {
      String m = MessageUtil.getMessage("cache.loading.exception");
      GemLogger.logException(m, ex);
    } finally {
      showMessage(frame, MessageUtil.getMessage("cache.loading.completed"));
      cacheInit = true;
    }
  }

  private void loadRoomContactCache() {
    String query = "SELECT " + PersonIO.COLUMNS + " FROM " + PersonIO.TABLE + " p, " + RoomIO.TABLE + " r "
            + "WHERE r.idper = p.id OR r.payeur = p.id ORDER BY nom";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Person p = PersonIO.getFromRS(rs);
        PERSON_CACHE.put(p.getId(), p);
      }
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
  }

  private void loadTeacherInstrumentCache() {
    try {
      TEACHER_INSTRUMENT_CACHE = InstrumentIO.load(dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  private void loadScheduleCache() throws SQLException {
    LEVEL_LIST = new GemList<GemParam>(LEVEL_IO.load());
    STATUS_LIST = new GemList<GemParam>(STATUS_IO.load());
    AGE_RANGE_LIST = new GemList<AgeRange>(AGE_RANGE_IO.load());
    for(Action a : ACTION_IO.load()) {
      ACTION_CACHE.put(a.getId(), a);
    }
  }

  public static List<OrderLine> findOrderLines(String invoiceId) {
    List<OrderLine> lo = new ArrayList<OrderLine>();
    for (OrderLine ol : ORDER_LINE_CACHE.values()) {
      if (ol.getInvoice().equals(invoiceId)) {
        lo.add(ol);
      }
    }
    if (lo.isEmpty()) {
      String query = "WHERE facture = '" + invoiceId + "'";
      lo =  OrderLineIO.find(query, dc);
      for(OrderLine ol : lo) {
        ORDER_LINE_CACHE.put(ol.getId(), ol);
      }
    }
    return lo;
  }

  private void loadAccountingCache() throws SQLException {
    ACCOUNT_LIST = new GemList<Account>(AccountIO.load(dc));

    Vector<Param> vca = ParamTableIO.find(CostAccountCtrl.tableName, CostAccountCtrl.columnKey,null, dc);
    for(Param p : vca) {
      COST_ACCOUNT_CACHE.put(p.getKey(), p);
    }
    List<Param> lp = ParamTableIO.find(ItemIO.TVA_TABLE, "id", dc);
    List<Vat> lv = new ArrayList<Vat>();
    for (Param p : lp) {
      lv.add(new Vat(p));
    }
    VAT_LIST = new GemList<Vat>(lv);

  }

  private void loadBillingCache() throws SQLException {
    for (Person p : PERSON_IO.load()) {
      PERSON_CACHE.put(p.getId(), p);
    }
    for (Item it : ITEM_IO.load()) {
      ITEM_CACHE.put(it.getId(), it);
    }
    for (OrderLine ol : OrderLineIO.getBillingOrderLines(dc)) {
      ORDER_LINE_CACHE.put(ol.getId(), ol);
    }

  }

  private void showMessage(GemBoot frame, String msg) {
    if (frame != null) {
      frame.setMessage(msg);
    }
  }

  public UserService getUserService() {
    return userService;
  }

  public AtelierInstrumentsService getAtelierInstrumentsService() {
    return atelierInstrumentsService;
  }

  public User getUser() {
    return user;
  }

  /**
   * Beginning date of school year.
   *
   * @return a date
   */
  public DateFr getStartOfYear() {
    return startOfYear;
  }

  /**
   * End date of school year.
   *
   * @return a date
   */
  public DateFr getEndOfYear() {
    return endOfYear;
  }

  /**
   * Beginning date of period.
   *
   * @return a date
   */
  public DateFr getStartOfPeriod() {
    return startOfPeriod;
  }

  /**
   * End date of period.
   *
   * @return a date
   */
  public DateFr getEndOfPeriod() {
    return endOfPeriod;
  }

  public DaySchedule getDaySchedule() {
    return daySchedule;
  }

  public MonthSchedule getMonthSchedule() {
    return monthSchedule;
  }

  public void setMonthSchedule(java.util.Date startDate, java.util.Date endDate) {

    if (monthThread != null) {
      monthThread.interrupt();
      try {
        monthThread.join();
      } catch (InterruptedException ignore) {
      }
    }
    final DateFr start = new DateFr(startDate);
    final DateFr end = new DateFr(endDate);

    monthThread = new Thread(new Runnable()
    {

      public void run() {
        try {
          loadMonthStmt.setDate(1, new java.sql.Date(start.getTime()));
          loadMonthStmt.setDate(2, new java.sql.Date(end.getTime()));
          Vector<ScheduleObject> vpl = ScheduleIO.getLoadRS(loadMonthStmt, dc);

          //dump("planningmois.ser",vpl);
          if (Thread.interrupted()) {
            return;
          }
          monthSchedule.setSchedule(start.getDate(), end.getDate(), vpl);

          loadMonthRangeStmt.setDate(1, new java.sql.Date(start.getTime()));
          loadMonthRangeStmt.setDate(2, new java.sql.Date(end.getTime()));
          Vector<ScheduleRangeObject> vpg = ScheduleRangeIO.getLoadRS(loadMonthRangeStmt, dc);
          //dump("plagemois.ser",vpg);
          if (Thread.interrupted()) {
            return;
          }
          monthSchedule.setScheduleRange(start.getDate(), end.getDate(), vpg);

        } catch (SQLException e) {
          GemLogger.logException(e);
        }
        monthThread = null;
      }
    });
    monthThread.start();

  }

  public void setDaySchedule(java.util.Date date) {
    try {
      loadDayStmt.setDate(1, new java.sql.Date(date.getTime()));
      loadDayRangeStmt.setDate(1, new java.sql.Date(date.getTime()));
      daySchedule.setDay(date,
              ScheduleIO.getLoadRS(loadDayStmt, dc),
              ScheduleRangeIO.getLoadRS(loadDayRangeStmt, dc));
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  public Vector<CategoryOccup> getOccupationalCat() {
    return occupCat;
  }

  public Vector<Param> getVacancyCat() {
    return vacancyCat;
  }

  public Vector<Param> getWebSiteCat() {
    return webSiteCat;
  }

  public Vector<Instrument> getInstruments() {
    return instruments;
  }

  public static List<Integer> getTeacherInstruments(int idper) {
    return TEACHER_INSTRUMENT_CACHE.get(idper);
  }

  /**
   * Gets the name of an instrument from its {@literal id }.
   * @param id
   * @return a string, possibly empty
   */
  public String getInstrumentName(int id) {
    for (Instrument i : instruments) {
      if (i.getId() == id) {
        return i.getName();
      }
    }
    return "";
  }

  /**
   * Gets a menu item.
   * If full is true, various parameters are also attached to the item (info, tooltip, mnemonic).
   *
   * @param menu prefix key in properties files
   * @param full with parameters
   * @return un jMenuItem
   */
  public JMenuItem getMenu2(String menu, boolean full) {
    String label = menu;
    label = BundleUtil.getLabel(menu + ".label");

    JMenuItem m = new JMenuItem(label);
    if (full) {
      m.setMnemonic(BundleUtil.getLabel(menu + ".mnemo").charAt(0));
      m.getAccessibleContext().setAccessibleDescription(BundleUtil.getLabel(menu + ".info"));
      m.setToolTipText(BundleUtil.getLabel(menu + ".tip"));
    }

    m.setEnabled(authorize(menu + ".auth"));
    return m;
  }

  public JMenuItem getMenu2(String menu) {
    return getMenu2(menu, false);
  }

  public boolean authorize(String menu2) {
    return userService.authorize(menu2, user);
  }

  public String getVersion() {
    String v = "inconnue";
    String query = "SELECT version FROM version";
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        v = rs.getString(1).trim();
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return v;
  }

  /**
   * Checks the rights for a table in database.
   * @param table
   * @param operation
   * @return true if access authorized
   * @deprecated
   */
  public boolean checkAccess(String table, String operation) {
    boolean ret = false;
    String query = "SELECT " + operation + " FROM droits WHERE idper=" + user.getId() + " AND nomtable = '" + table + "'";
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        ret = rs.getBoolean(1);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return ret;
  }

  /**
   * General configuration.
   */
  public void setConfig() {
    try {
      setDates();
    } catch (ConfigException ex) {
      GemLogger.log(ex.getMessage());
    }

  }

  /**
   * Date configuration for planning and enrolment.
   *
   * @throws ConfigException
   * @see net.algem.bdio.ConfigIO#findId
   */
  private void setDates() throws ConfigException {
    String s = ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey());
    if (s != null) {
      startOfYear = new DateFr(s);
    } else {
      throw new ConfigException(BundleUtil.getLabel("ConfEditor.date.start.exception"));
    }

    s = ConfigUtil.getConf(ConfigKey.BEGINNING_PERIOD.getKey());
    if (s != null) {
      startOfPeriod = new DateFr(s);
    } else {
      throw new ConfigException(BundleUtil.getLabel("ConfEditor.date.start.period.exception"));
    }

    s = ConfigUtil.getConf(ConfigKey.END_YEAR.getKey());
    if (s != null) {
      endOfYear = new DateFr(s);
    } else {
      throw new ConfigException(BundleUtil.getLabel("ConfEditor.date.end.exception"));
    }

    s = ConfigUtil.getConf(ConfigKey.END_PERIOD.getKey());
    if (s != null) {
      endOfPeriod = new DateFr(s);
    } else {
      throw new ConfigException(BundleUtil.getLabel("ConfEditor.date.end.period.exception"));
    }
  }

  <T extends Object> void dump(String p, Vector<T> v) {
    try {
      FileOutputStream fic = new FileOutputStream(p);
      ObjectOutputStream out = new ObjectOutputStream(fic);
      out.writeObject(v);
      fic.close();
    } catch (IOException e) {
      GemLogger.log("serializ err :" + e);
    }
  }

  public PlanningFactService getPlanningFactService() {
    return planningFactService;
  }
}
