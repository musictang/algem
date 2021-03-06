/*
 * @(#)DataCache.java	2.17.3 01/11/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Color;
import javax.swing.JMenuItem;
import net.algem.Algem;
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
import net.algem.planning.Action;
import net.algem.planning.*;
import net.algem.planning.day.DaySchedule;
import net.algem.planning.dispatch.model.ScheduleDispatchService;
import net.algem.planning.editing.instruments.AtelierInstrumentsDAO;
import net.algem.planning.editing.instruments.AtelierInstrumentsService;
import net.algem.planning.editing.instruments.AtelierInstrumentsServiceImpl;
import net.algem.planning.fact.services.PlanningFactCreator;
import net.algem.planning.fact.services.PlanningFactDAO;
import net.algem.planning.fact.services.PlanningFactService;
import net.algem.planning.fact.services.SimpleConflictService;
import net.algem.planning.month.MonthSchedule;
import net.algem.room.*;
import net.algem.script.directory.ScriptDirectoryService;
import net.algem.script.directory.ScriptDirectoryServiceImpl;
import net.algem.script.directory.ScriptManifestParserImpl;
import net.algem.script.execution.ScriptExecutorService;
import net.algem.script.execution.ScriptExecutorServiceImpl;
import net.algem.script.execution.ScriptExportService;
import net.algem.script.execution.ScriptExportServiceImpl;
import net.algem.security.DefaultUserService;
import net.algem.security.User;
import net.algem.security.UserIO;
import net.algem.security.UserService;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Cacheable;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.algem.billing.VatIO;
import net.algem.contact.Note;
import net.algem.rental.RentableObject;
import net.algem.rental.RentableObjectIO;
import net.algem.util.ui.MessagePopup;

/**
 * Cache and various utilities.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.3
 * @since 1.0b 03/09/2001
 */
public class DataCache {

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
    private static RentableObjectIO RENTABLE_IO;

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

    public static int PERSON_CACHE_MIN_SIZE = 675; // ERIC 26/03/2019 à calculer suivant le nombre access'élèves ~675/800 pour polynotes
    private static Map<Integer, DailyTimes[]> roomsTimes = new HashMap<>(); //ERIC 2.17 27/03/2019
    private static Map<String, HashMap> authorizations = new HashMap<>(); //ERIC 2.17 30/03/2019
    private static Map<Integer, List> moduleCourses = new HashMap<>(); //ERIC 2.17 30/03/2019

    /**
     * Cached action memos. Key = action id, value = Note instance.
     */
    public static HashMap<Integer, Note> ACTION_MEMO_CACHE = new HashMap<Integer, Note>();
    /**
     * Cached action colors. Key = action id, value = action color.
     */
    public static Map<Integer, Integer> ACTION_COLOR_CACHE = new HashMap<Integer, Integer>();

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
    private static GemList<RentableObject> RENTABLE_LIST;

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
    /*private PreparedStatement loadDayStmt;
  private PreparedStatement loadDayRangeStmt;*/

    private MonthSchedule monthSchedule;
    private DaySchedule daySchedule;
    private Thread monthThread;

    private static DataConnection DATA_CONNECTION;
    private UserService userService;

    private AtelierInstrumentsService atelierInstrumentsService;
    private PlanningFactService planningFactService;
    private ScriptDirectoryService scriptDirectoryService;
    private ScriptExecutorService scriptExecutorService;
    private ScriptExportService scriptExportService;
    private PlanningFactDAO planningFactDAO;
    private ScheduleDispatchService scheduleDispatchService;

    private String loadDayQuery = "SELECT " + ScheduleIO.COLUMNS
            + " FROM " + ScheduleIO.TABLE + " p JOIN " + RoomIO.TABLE + " s ON (p.lieux = s.id) JOIN " + EstablishmentIO.TABLE + " e ON (s.etablissement = e.id)"
            + " WHERE p.jour = ? AND e.actif = TRUE AND e.idper = ? ORDER BY p.debut";

    private DataCache() {

    }

    /**
     * Private constructor.
     *
     * @param dc
     * @param login
     */
    private DataCache(DataConnection dc, String login) {

        this.DATA_CONNECTION = dc;
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
        RENTABLE_IO = new RentableObjectIO(dc);

        String loadMonthQuery = "SELECT " + ScheduleIO.COLUMNS
                + " FROM planning p JOIN salle s ON (p.lieux = s.id) JOIN etablissement e ON (s.etablissement = e.id)"
                + " WHERE p.jour >= ? AND p.jour <= ?"
                + " AND e.actif = TRUE AND e.idper = ? ORDER BY p.jour,p.debut";
//    loadMonthQuery += "0".equals(estabActivationType) ? " AND e.actif = TRUE ORDER BY p.jour,p.debut" : " ORDER BY p.jour,p.debut";
        loadMonthStmt = dc.prepareStatement(loadMonthQuery);
        loadMonthRangeStmt = dc.prepareStatement(ScheduleRangeIO.getMonthRangeStmt());

        /*String loadDayQuery = "SELECT " + ScheduleIO.COLUMNS
            + " FROM planning p JOIN salle s ON (p.lieux = s.id) JOIN etablissement e ON (s.etablissement = e.id)"
            + " WHERE p.jour = ?"
            + " AND e.actif = TRUE AND e.idper = ? ORDER BY p.debut";

    loadDayStmt = dc.prepareStatement(loadDayQuery);//ORDER BY p.action,p.debut
    loadDayRangeStmt = dc.prepareStatement(ScheduleRangeIO.getDayRangeStmt());*/

        userService = new DefaultUserService(this);

        monthSchedule = new MonthSchedule();
        daySchedule = new DaySchedule();

        atelierInstrumentsService = new AtelierInstrumentsServiceImpl(dc, new AtelierInstrumentsDAO(dc), PERSON_IO);
        planningFactDAO = new PlanningFactDAO(dc);
        planningFactService = new PlanningFactService(dc, new PlanningService(dc), planningFactDAO,
                new PlanningFactCreator(), new PlanningFactService.RoomFinder(),
                new PlanningFactService.ScheduleUpdater(dc), new SimpleConflictService(dc));

        File scriptsPath = Algem.getScriptsPath();
        if (scriptsPath != null) {
            System.out.println(scriptsPath);
            scriptDirectoryService = new ScriptDirectoryServiceImpl(scriptsPath, new IOUtil.FileReaderHelper(), new ScriptManifestParserImpl());
            scriptExecutorService = new ScriptExecutorServiceImpl(dc);
            scriptExportService = new ScriptExportServiceImpl();
        }

        scheduleDispatchService = new ScheduleDispatchService(dc, PERSON_IO);
    }

    public void setUser(String login) {
        user = ((DefaultUserService) userService).find(login);
    }

    /**
     * Singleton access.
     *
     * @param dc
     * @param user
     * @return a cache instance
     * @see   <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">"Double-Checked Locking Broken" Declaration</a>
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
        if (DATA_CONNECTION == null) {
            GemLogger.log(java.util.logging.Level.SEVERE, "NULL DATA CONNECTION");
            MessagePopup.error(null, MessageUtil.getMessage("no.active.connection"));
            System.exit(1);
        }
        return DATA_CONNECTION;
    }

    /**
     * Generic getter for GemList instances.
     * @param model enumeration model
     * @return an instance of GemList
     */
    public GemList getList(Model model) {
        switch (model) {
            case Room:
                return ROOM_LIST;
            case Establishment:
                return ESTAB_LIST;
            case Teacher:
                return TEACHER_LIST;
            case Group:
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
            case RentableObject:
                return RENTABLE_LIST;
            default:
                return null;
        }

    }

    /**
     * Gets the correct DAO instance for model {@literal m}.
     * @param m model type
     * @return an instance of {@link net.algem.util.model.Cacheable }
     */
    public static Cacheable getDao(Model m) {
        switch (m) {
            case Room:
                return ROOM_IO;
            case RoomRate:
                return ROOM_RATE_IO;
            case Teacher:
                return TEACHER_IO;
            case Member:
                return MEMBER_IO;
            case Group:
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
            case RentableObject:
                return RENTABLE_IO;
            default:
                return null;
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
                    account = AccountIO.find(id, DATA_CONNECTION);
                    ACCOUNT_LIST.addElement(account);
                }
                return account;
            case Action:
                Action a = ACTION_CACHE.get(id);
                if (a == null) {
                    a = ACTION_IO.findId(id);
                    if (a != null) {
                        ACTION_CACHE.put(id, a);
                    }
                }
                return a;
            case ActionMemo:
                // all memos are cached
                return ACTION_MEMO_CACHE.get(id);
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
                    pi = PERSON_IO.findById(id);
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
                Vat vat = (Vat) VAT_LIST.getItem(id);
                return vat != null ? vat : new VatIO(DATA_CONNECTION).findId(id);
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
                    pc = RehearsalPassIO.find(id, DATA_CONNECTION);
                    PASS_CARD.put(pc.getId(), pc);
                }
                return pc;
            case RentableObject:
                RentableObject o = (RentableObject) RENTABLE_LIST.getItem(id);
                return o != null ? o : RENTABLE_IO.findId(id);
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
            Course course = (Course) m;
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
            if (t.getInstruments() != null) {
                TEACHER_INSTRUMENT_CACHE.put(t.getId(), t.getInstruments());
            }
        } else if (m instanceof Member) {
            MEMBER_CACHE.put(m.getId(), (Member) m);
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
            Collections.sort(ESTAB_LIST.getData(), new EstablishmentComparator());
            ROOM_LIST = new GemList<Room>(ROOM_IO.load(user.getId()));
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
        } else if (m instanceof Person) { //!important after subclasses
            PERSON_CACHE.put(m.getId(), (Person) m);
        } else if (m instanceof Vat) {
            VAT_LIST.addElement((Vat) m);
        } else if (m instanceof CourseCode) {
            COURSE_CODE_LIST.addElement((CourseCode) m);
        } else if (m instanceof RehearsalPass) {
            PASS_CARD.put(m.getId(), (RehearsalPass) m);
        } else if (m instanceof MaritalStatus) {
            MARITAL_STATUS_LIST.addElement((MaritalStatus) m);
        } else if (m instanceof RentableObject) {
            RENTABLE_LIST.addElement((RentableObject) m);
            //Collections.sort(RENTABLE_LIST.getData(), new RentableComparator());
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
            Course course = (Course) m;
            if (course.isATP()) {
                WORKSHOP_LIST.update(course, new CourseComparator());
            } else {
                COURSE_LIST.update(course, new CourseComparator());
            }
        } else if (m instanceof Teacher) {
            Teacher t = (Teacher) m;
            TEACHER_LIST.update(t, new TeacherComparator(ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey())));
            if (t.getInstruments() != null) {
                TEACHER_INSTRUMENT_CACHE.put(t.getId(), t.getInstruments());
            }
        } else if (m instanceof Member) {
            MEMBER_CACHE.put(m.getId(), (Member) m);
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
            COST_ACCOUNT_CACHE.put(((CostAccount) m).getKey(), (CostAccount) m);
        } else if (m instanceof User) {
            USER_CACHE.put(m.getId(), (User) m);
        } else if (m instanceof Person) {
            Person p = (Person) m;
            PERSON_CACHE.put(m.getId(), p);
            Teacher t = (Teacher) TEACHER_LIST.getItem(m.getId());
            if (t != null) {
                t.setFirstName(p.getFirstName());
                t.setName(p.getName());
                TEACHER_LIST.update(t, new TeacherComparator(ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey())));
            }
        } else if (m instanceof Action) {
            Action a = (Action) m;
            ACTION_CACHE.put(m.getId(), a);
            Note n = a.getNote();
            if (n != null) {
                if (n.getIdPer() == 0) {
                    ACTION_MEMO_CACHE.remove(a.getId());
                } else {
                    ACTION_MEMO_CACHE.put(a.getId(), n);
                }
            }
            if (a.getColor() != 0) {
                ACTION_COLOR_CACHE.put(a.getId(), a.getColor());
            } else {
                ACTION_COLOR_CACHE.put(a.getId(), 0); // stay in cache but with color 0
            }
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
        } else if (m instanceof RentableObject) {
            RENTABLE_LIST.update((RentableObject) m, null); // new RentableObjectComparator()
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
            ROOM_LIST = new GemList<Room>(ROOM_IO.load(user.getId()));
        } else if (m instanceof Course) {
            Course course = (Course) m;
            if (course.isATP()) {
                WORKSHOP_LIST.removeElement(course);
            } else {
                COURSE_LIST.removeElement(course);
            }
        } else if (m instanceof Teacher) {
            TEACHER_INSTRUMENT_CACHE.remove(m.getId());
            TEACHER_LIST.removeElement((Teacher) m);
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
        } else if (m instanceof Person) {
            PERSON_CACHE.remove(m.getId());
        } else if (m instanceof Vat) {
            VAT_LIST.removeElement((Vat) m);
        } else if (m instanceof CourseCode) {
            COURSE_CODE_LIST.removeElement((CourseCode) m);
        } else if (m instanceof RehearsalPass) {
            PASS_CARD.remove(m.getId());
        } else if (m instanceof MaritalStatus) {
            MARITAL_STATUS_LIST.removeElement((MaritalStatus) m);
        } else if (m instanceof RentableObject) {
            RENTABLE_LIST.removeElement((RentableObject) m);
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
            showMessage(frame, BundleUtil.getLabel("Loading.label"));
            setConfig();

            ROOM_RATE_LIST = new GemList<RoomRate>(ROOM_RATE_IO.load());
            loadRoomContactCache();
            Vector<Param> schools = ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.COLUMN_KEY, DATA_CONNECTION);
            SCHOOL_LIST = new GemList<Param>(schools);
            ESTAB_LIST = new GemList<Establishment>(EstablishmentIO.find(" AND e.actif = TRUE AND e.idper = " + user.getId() + " ORDER BY p.nom", DATA_CONNECTION));

            showMessage(frame, BundleUtil.getLabel("Room.label"));
            ROOM_LIST = new GemList<Room>(ROOM_IO.load(user.getId()));

            showMessage(frame, BundleUtil.getLabel("Course.label"));
            COURSE_LIST = new GemList<Course>(COURSE_IO.load());

            showMessage(frame, BundleUtil.getLabel("Workshop.reading.label"));
            WORKSHOP_LIST = new GemList<Course>(COURSE_IO.load("WHERE c.code = '" + CourseCodeType.ATP.getId() + "' AND titre !~* 'DEFIN' ORDER BY c.titre"));

            showMessage(frame, BundleUtil.getLabel("Instruments.label"));
            instruments = InstrumentIO.find("ORDER BY nom", DATA_CONNECTION);

            showMessage(frame, BundleUtil.getLabel("Teachers.label"));
            loadTeacherInstrumentCache();
            TEACHER_LIST = new GemList<Teacher>(TEACHER_IO.load());

            showMessage(frame, BundleUtil.getLabel("Modules.label"));
            // important : before module
            COURSE_CODE_LIST = new GemList<GemParam>(COURSE_CODE_IO.load());
            //loadModuleCourses(); //ERIC 11-06-2019 en attente de gérer les mises à jour
            MODULE_LIST = new GemList<Module>(MODULE_IO.load());

            showMessage(frame, BundleUtil.getLabel("Rentable.label"));
            RENTABLE_LIST = new GemList<RentableObject>(RENTABLE_IO.load());

            occupCat = CategoryOccupIO.find("ORDER BY nom", DATA_CONNECTION);
            vacancyCat = ParamTableIO.find(Category.VACANCY.getTable(), Category.VACANCY.getCol(), DATA_CONNECTION);
            webSiteCat = ParamTableIO.find(Category.SITEWEB.getTable(), Category.SITEWEB.getCol(), DATA_CONNECTION);

            showMessage(frame, BundleUtil.getLabel("Scheduling.label"));
            loadScheduleCache();

            showMessage(frame, BundleUtil.getLabel("Menu.style.label"));
            STYLE_LIST = new GemList<MusicStyle>(MUSIC_STYLE_IO.load());

            showMessage(frame, BundleUtil.getLabel("Groups.label"));
            GROUP_LIST = new GemList<Group>(GROUP_IO.load());

            showMessage(frame, BundleUtil.getLabel("Accounting.label"));
            loadAccountingCache();

//      showMessage(frame, BundleUtil.getLabel("Billing.label"));
//      loadBillingCache(); //FIXME ERIC voir findOrderLines() invoiceloader invoceio.find +findorderlines

            for (User u : USER_IO.load()) {
                USER_CACHE.put(u.getId(), u);
            }

            EMPLOYEE_TYPE_LIST = new GemList<GemParam>(EMPLOYEE_TYPE_IO.load());
            MARITAL_STATUS_LIST = new GemList<>(MARITAL_STATUS_IO.find());

            STUDIO_TYPE_LIST = new GemList<GemParam>(STUDIO_TYPE_IO.load());
            PASS_CARD = new Hashtable<Integer, RehearsalPass>();
            for (RehearsalPass c : RehearsalPassIO.findAll("ORDER BY id", DATA_CONNECTION)) {
                PASS_CARD.put(c.getId(), c);
            }
            loadDailyTimesCache(); //ERIC 27/03/2019
            loadAuthorizationsCache(); //ERIC 27/03/2019
        } catch (SQLException ex) {
            String m = MessageUtil.getMessage("cache.loading.exception");
            GemLogger.logException(m, ex);
        } finally {
            showMessage(frame, MessageUtil.getMessage("cache.loading.completed"));
            cacheInit = true;
        }
    }

    private void loadRoomContactCache() {
//    String query = "SELECT " + PersonIO.COLUMNS + " FROM " + PersonIO.TABLE + " p, " + RoomIO.TABLE + " r "
//            + "WHERE r.idper = p.id OR r.payeur = p.id ORDER BY nom";
        String query = PersonIO.PRE_QUERY + " JOIN " + RoomIO.TABLE + " r ON (p.id = r.idper OR p.id = r.payeur) ORDER BY p.nom";
        try {
            ResultSet rs = DATA_CONNECTION.executeQuery(query);
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
            TEACHER_INSTRUMENT_CACHE = InstrumentIO.load(DATA_CONNECTION);
        } catch (SQLException ex) {
            GemLogger.logException(ex);
        }
    }

    private void loadScheduleCache() throws SQLException {
        LEVEL_LIST = new GemList<GemParam>(LEVEL_IO.load());
        STATUS_LIST = new GemList<GemParam>(STATUS_IO.load());
        AGE_RANGE_LIST = new GemList<AgeRange>(AGE_RANGE_IO.load());
        for (Action a : ACTION_IO.load(getStartOfYear().getDate(), getEndOfYear().getDate())) {
            ACTION_CACHE.put(a.getId(), a);
        }
        ACTION_COLOR_CACHE = ACTION_IO.loadColors(getStartOfYear().getDate(), getEndOfYear().getDate());
        ACTION_MEMO_CACHE = ACTION_IO.loadMemos();
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
            lo = OrderLineIO.find(query, DATA_CONNECTION);
            for (OrderLine ol : lo) {
                ORDER_LINE_CACHE.put(ol.getId(), ol);
            }
        }
        return lo;
    }

    private void loadAccountingCache() throws SQLException {
        ACCOUNT_LIST = new GemList<Account>(AccountIO.load(DATA_CONNECTION));

        Vector<Param> vca = ParamTableIO.find(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, null, DATA_CONNECTION);
        for (Param p : vca) {
            COST_ACCOUNT_CACHE.put(p.getKey(), p);
        }
        List<Vat> taxes = new VatIO(DATA_CONNECTION).load();
        VAT_LIST = new GemList<Vat>(taxes);

    }

    /**
     *
     * @throws SQLException
     * @deprecated
     *
     */
    private void loadBillingCache() throws SQLException {
        //for (Person p : PERSON_IO.load()) {
        //    PERSON_CACHE.put(p.getId(), p);
        //}
        for (Item it : ITEM_IO.load()) {
            ITEM_CACHE.put(it.getId(), it);
        }
//        for (OrderLine ol : OrderLineIO.getBillingOrderLines(DATA_CONNECTION)) {
//            ORDER_LINE_CACHE.put(ol.getId(), ol);
//        }

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

    public ScriptDirectoryService getScriptDirectoryService() {
        return scriptDirectoryService;
    }

    public ScriptExecutorService getScriptExecutorService() {
        return scriptExecutorService;
    }

    public ScriptExportService getScriptExportService() {
        return scriptExportService;
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

    /**
     * Label of school year. ex 2018-2019
     *
     * @return a string
     */
    public String getSchoolYearLabel() {    //ERIC 26/03/2019
        return startOfYear.getYear()+"-"+endOfYear.getYear();

    }

    /**
     * Label of next school year. ex 2019-2020
     *
     * @return a string
     */
    public String getSchoolNextYearLabel() {     //ERIC 26/03/2019
        return (startOfYear.getYear()+1)+"-"+(endOfYear.getYear()+1);

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
                    loadMonthStmt.setInt(3, user.getId());
                    Vector<ScheduleObject> vpl = ScheduleIO.getLoadRS(loadMonthStmt, DATA_CONNECTION);

                    //dump("planningmois.ser",vpl);
                    if (Thread.interrupted()) {
                        return;
                    }
                    monthSchedule.setSchedule(start.getDate(), end.getDate(), vpl);

                    loadMonthRangeStmt.setDate(1, new java.sql.Date(start.getTime()));
                    loadMonthRangeStmt.setDate(2, new java.sql.Date(end.getTime()));
                    Vector<ScheduleRangeObject> vpg = ScheduleRangeIO.getLoadRS(loadMonthRangeStmt, DATA_CONNECTION);
                    //ERIC 2.17
                     System.out.println("setMonthSchedule PersonCacheSize=" + DataCache.PERSON_CACHE.size());
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

        try (PreparedStatement loadDayStmt = DATA_CONNECTION.prepareStatement(loadDayQuery);
                PreparedStatement loadDayRangeStmt = DATA_CONNECTION.prepareStatement(ScheduleRangeIO.getDayRangeStmt())) {
            loadDayStmt.setDate(1, new java.sql.Date(date.getTime()));
            loadDayStmt.setInt(2, user.getId());
            loadDayRangeStmt.setDate(1, new java.sql.Date(date.getTime()));
            daySchedule.setDay(date,
                    ScheduleIO.getLoadRS(loadDayStmt, DATA_CONNECTION),
                    ScheduleRangeIO.getLoadRS(loadDayRangeStmt, DATA_CONNECTION));
        } catch (SQLException e) {
            GemLogger.logException(e);
        }
        //ERIC 2.17
        System.out.println("setDaySchedule PersonCacheSize=" + DataCache.PERSON_CACHE.size());
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
     * Retrieves the cached color for this action {@code id}.
     * @param id action id
     * @return a color or null if no color was found
     */
    public static Color getActionColor(int id) {

        Integer c = ACTION_COLOR_CACHE.get(id);
        if (c == null) {
            c = ACTION_IO.getColor(id);
            ACTION_COLOR_CACHE.put(id, c); // cache also 0-color actions to avoid subsequent requests
        }
        return c == 0 ? null : new Color(c);
    }


    /**
   * Gets a menu item.
   * If full is true, various parameters are also attached to the item (info, tooltip, mnemonic).
     *
     * @param menu prefix key in properties files
     * @param full with parameters
     * @return a jMenuItem
     */
    public JMenuItem getMenu2(String menu, boolean full) {
        String label = BundleUtil.getLabel(menu + ".label");

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

    /**
     *
     * @param menu2 menu key
     * @return true if menu is authorized for current user
     * @see net.algem.security.DefaultUserService#authorize(java.lang.String, net.algem.security.User)
     */
    public boolean authorize(String menu2) {
        Map<Integer, Boolean> access = authorizations.get(menu2);
        if (access == null) return true;
        return Optional.ofNullable(access.get(getUser().getId())).orElse(true); // authorize by default
    }

    public String getVersion() {
        String v = "inconnue";
        String query = "SELECT version FROM version";
        try (ResultSet rs = DATA_CONNECTION.executeQuery(query)) {
            if (rs.next()) {
                v = rs.getString(1).trim();
            }
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
            ResultSet rs = DATA_CONNECTION.executeQuery(query);
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
     * @see net.algem.config.ConfigIO#findId
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

    public PlanningFactDAO getPlanningFactDAO() {
        return planningFactDAO;
    }

    public ScheduleDispatchService getScheduleDispatchService() {
        return scheduleDispatchService;
    }

    //ERIC 27/03/2019
    public static void addToPersonCache(Person p) {  //ERIC preload cache personne
        PERSON_CACHE.put(p.getId(), p);
    }

    public static boolean personCacheLoaded() {
        return PERSON_CACHE.size() > PERSON_CACHE_MIN_SIZE;
    }

    public static boolean existId(int id, Model m) {
        switch (m) {
            case Person:
                return PERSON_CACHE.get(id) != null;
            default:
                return false;
        }
    }

    //ERIC 30-03-2019 //TODO gérer les mises à jour du cache
    public static List<CourseModuleInfo> getModuleCourse(int module) {
        return moduleCourses.get(module);
    }

    private static void loadModuleCourses() {
        moduleCourses = MODULE_IO.loadModuleCourses();
    }

    public static Map<Integer, DailyTimes[]> getDailyTimes() {
        return roomsTimes;
    }

    //FIX ME avec RoomService.findDailyTimes(id) + ConfigUtil.findDailyTimes()
    public static DailyTimes[] getDailyTimes(int id) {
        return roomsTimes.get(id);
    }

    private static void loadDailyTimesCache() {
        roomsTimes = RoomTimesIO.loadDailyTimes(ROOM_LIST.getData()); // valeurs par défaut
    }

    private static void loadAuthorizationsCache() {
        authorizations = USER_IO.loadAuthorizations();
    }

}
//ERIC END

