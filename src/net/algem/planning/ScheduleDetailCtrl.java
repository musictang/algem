/*
 * @(#)ScheduleDetailCtrl.java 2.15.2 27/09/17
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.*;

import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;

import net.algem.config.Instrument;
import net.algem.contact.*;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberService;
import net.algem.course.Course;
import net.algem.course.CourseCtrl;
import net.algem.group.GemGroupService;
import net.algem.group.Group;
import net.algem.group.GroupFileEditor;
import net.algem.group.Musician;
import net.algem.planning.editing.BreakSuppressionDlg;
import net.algem.planning.editing.EditEventDlg;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.PlanModifCtrl;
import net.algem.planning.editing.instruments.AtelierInstrumentsService;
import net.algem.room.Room;
import net.algem.room.RoomFileEditor;
import net.algem.util.*;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.model.Model;
import net.algem.util.module.DefaultGemView;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.*;

/**
 * Access schedule infos and modifications.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 1.0a 07/07/1999
 */
public class ScheduleDetailCtrl
  implements ActionListener {

  private static PersonScheduleComparator psComparator = new PersonScheduleComparator(ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey()));
  private static boolean nameFirst = ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey()).equals("n");
  private static PersonComparator personComparator = new PersonComparator();
  private static MailUtil MAIL_UTIL;

  private GemDesktop desktop;
  private DataCache dataCache;
  private DataConnection dc;
  /**
   * Contains modification buttons.
   */
  private PlanModifCtrl modifCtrl;
  private JDialog frame;
  private GemPanel panel;
  private GemBorderPanel headPanel;
  private GemBorderPanel listPanel;
  private GemBorderPanel menuPanel;
  private Schedule schedule;
  private String title;
  private GemMenuButton btClose, btWrite, btGroupWrite;
  private ScheduleDetailEvent detailEvent;
  private DesktopMailHandler mailHandler;
  private PlanningService scheduleService;
  private GemGroupService groupService;
  private MemberService memberService;
  /**
   * Presence indicator of the modification buttons.
   */
  private boolean allMenus;
  private AtelierInstrumentsService atelierInstrumentsService;

  public ScheduleDetailCtrl(GemDesktop desktop, PlanModifCtrl pmCtrl, boolean all) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    dc = DataCache.getDataConnection();
    scheduleService = new PlanningService(dc);
    groupService = new GemGroupService(dc);
    memberService = new MemberService(dc);
    atelierInstrumentsService = dataCache.getAtelierInstrumentsService();
    MAIL_UTIL = new MailUtil(dataCache, memberService);
    modifCtrl = pmCtrl;
    frame = new JDialog(desktop.getFrame(), "détail planning");
    allMenus = all;

    panel = new GemPanel();
    panel.setLayout(new BorderLayout());

    headPanel = new GemBorderPanel();
    headPanel.setLayout(new GridLayout(0, 1));
    listPanel = new GemBorderPanel();
    listPanel.setLayout(new GridLayout(0, 1));
    menuPanel = new GemBorderPanel();
    menuPanel.setLayout(new GridLayout(0, 1));

    panel.add(headPanel, BorderLayout.NORTH);
    panel.add(listPanel, BorderLayout.CENTER);
    panel.add(menuPanel, BorderLayout.SOUTH);

    btClose = new GemMenuButton(GemCommand.CLOSE_CMD, this, "CloseLink");
    btClose.addActionListener(this);
    menuPanel.add(btClose);
    String s = BundleUtil.getLabel("Send.email.label");
    btWrite = new GemMenuButton(s, this, "Mailing");
    btGroupWrite = new GemMenuButton(s, this, "MailingGroup");
    frame.getContentPane().add(panel, BorderLayout.CENTER);
  }

  public ScheduleDetailCtrl(GemDesktop desktop, PlanModifCtrl pmCtrl) {
    this(desktop, pmCtrl, true);
  }

  /**
   * Loads schedule detail.
   *
   * @param event schedule detail event
   */
  public void loadSchedule(ScheduleDetailEvent event) {
    // Ce qui apparaît lors d'un clic sur une plage de planning. Le panneau est
    // divisé en 3 sections (entete, liste, menus).
    detailEvent = event;
    schedule = event.getSchedule();
    modifCtrl.setPlan(schedule);

    headPanel.removeAll();
    listPanel.removeAll();
    menuPanel.removeAll();
    title = schedule.getDate().toString() + " " + schedule.getStart().toString() + "-" + schedule.getEnd().toString();
    frame.setTitle(title);// || schedule instanceof WorkshopSchedule
    if (schedule instanceof CourseSchedule) {
      loadCourseSchedule(event);
    } else if (schedule instanceof BookingMemberSchedule) {
      loadBookingMemberSchedule(schedule);
    } else if (schedule instanceof MemberRehearsalSchedule) {
      loadMemberReahearsalSchedule(schedule);
    } else if (schedule instanceof BookingGroupSchedule) {
      loadBookingGroupSchedule(schedule);
    } else if (schedule instanceof GroupRehearsalSchedule) {
      loadGroupRehearsalSchedule(schedule);
    } else if (schedule instanceof WorkshopSchedule) {
      loadWorkshopSchedule(event);
    } else if (schedule instanceof GroupStudioSchedule) {
      loadStudioSchedule(schedule);
    } else if (schedule instanceof TechStudioSchedule) {
      loadTechnicianSchedule(event);
    } else if (schedule instanceof AdministrativeSchedule) {
      loadAdministrativeSchedule(event);
    } else if (schedule instanceof Schedule) {
      Schedule p = (Schedule) schedule;
      headPanel.add(new GemLabel("Saisie sur planning"));
      Vector<GemMenuButton> vb = modifCtrl.getMenuPlanning();
      for (int i = 0; i < vb.size(); i++) {
        menuPanel.add((GemMenuButton) vb.elementAt(i));
      }
    } else {
      headPanel.add(new GemLabel("Erreur Planning"));
    }

    if (schedule instanceof ScheduleObject) {
      StringBuilder buf = new StringBuilder(BundleUtil.getLabel("Room.label")).append(" ");
      buf.append(((ScheduleObject) schedule).getRoom().getName());
      GemMenuButton b = new GemMenuButton(buf.toString(), this, "RoomLink", ((ScheduleObject) schedule).getRoom());
      b.setEnabled(dataCache.authorize("Room.reading.auth"));
      headPanel.add(b);
    }

    menuPanel.add(btClose);

    frame.pack();

    Point pos = event.getPosition();
    int dw = desktop.getFrame().getWidth();
    int x = pos.x;
    if (x > (dw - 260)) {
      x = dw - 260;
    }
    int y = pos.y;
    if (y + frame.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().height) {
      y = pos.y - frame.getHeight() - 15;// to the top
      if (y < desktop.getFrame().getY()) { // above the top
        y = desktop.getFrame().getY() + 70;
      }
    }

    frame.setLocation(new Point(x, y));
    frame.setSize(260, frame.getHeight());
    frame.setVisible(true);

  }

  public void reloadFromLastEvent() {
    if (detailEvent != null) {
      loadSchedule(detailEvent);
    }
  }

  private void loadCourseSchedule(ScheduleDetailEvent de) {
    CourseSchedule p = (CourseSchedule) de.getSchedule();
    StringBuilder buf = new StringBuilder(BundleUtil.getLabel("Course.label")).append(" ");
    buf.append(p.getCourse().getTitle());

    GemButton b = new GemMenuButton(buf.toString(), this, "CourseLink", p.getCourse());
    b.setEnabled(dataCache.authorize("Course.reading.auth"));
    headPanel.add(b);

    buf = new StringBuilder(BundleUtil.getLabel("Teacher.label")).append(" ");
    buf.append(p.getTeacher().getFirstnameName());
    b = new GemMenuButton(buf.toString(), this, "TeacherLink", p.getTeacher());
    b.setEnabled(dataCache.authorize("Teacher.reading.auth"));
    headPanel.add(b);
    boolean collective = p.getCourse().isCollective();
    loadRanges(de.getRanges(), collective);
    if (allMenus) {
      // ajout des boutons de modification de planning (@see PlanModifCtrl)
      List<GemMenuButton> v = modifCtrl.getMenuCourse(p.getCourse().isCollective());
      for (GemMenuButton bt : v) {
        menuPanel.add(bt);
      }
    }
    menuPanel.add(btWrite);
  }

  private void loadRanges(Vector<ScheduleRangeObject> v, boolean collective) {
    if (v == null) {
      return;
    }
    if (collective) {
      Collections.sort(v, psComparator);
    }
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < v.size(); i++) {
      ScheduleRangeObject pl = v.elementAt(i);
      Person per = pl.getMember();

      if (!collective) {
        // Affichage de la plage horaire
        buf = new StringBuffer(pl.getStart().toString());
        buf.append("-").append(pl.getEnd());
      }
      if (per == null) {
        buf.append(" ").append(pl.getMemberId());
        listPanel.add(new GemMenuButton(buf.toString(), this, "NullMember", pl));
      } else if (per.getId() == 0) {
        buf.append(" ").append(BundleUtil.getLabel("Teacher.break.label"));
        listPanel.add(new GemMenuButton(buf.toString(), this, "BreakLink", pl));
      } else {
        if (collective) {
          buf = new StringBuffer(nameFirst ? per.getNameFirstname() : per.getFirstnameName());
          Member m = null;
          try {
            m = memberService.findMember(per.getId());
          } catch (SQLException ex) {
            GemLogger.logException(ex);
          }
          if (m != null) {
            String instrumentName = getInstrumentName(pl, per);
            if (instrumentName != null) {
              buf.append(" : ").append(instrumentName);
            }
          }
        } else {
          buf.append(" ").append(nameFirst ? per.getNameFirstname() : per.getFirstnameName());
        }
        GemMenuButton b = new GemMenuButton(buf.toString(), this, "MemberLink", pl);
        b.setEnabled(dataCache.authorize("Member.reading.auth"));
        listPanel.add(b);
      }
    }
  }

  private String getInstrumentName(ScheduleRangeObject pl, Person per) {
    Instrument instrument = null;
    try {
      instrument = atelierInstrumentsService.getAllocatedInstrument(pl.getAction(), per);
    } catch (Exception e) {
      GemLogger.logException(e);
    }
    return instrument != null ? instrument.getName() : "";
  }

  private void loadMemberReahearsalSchedule(Schedule sched) {
    MemberRehearsalSchedule p = (MemberRehearsalSchedule) sched;
    headPanel.add(new GemLabel(BundleUtil.getLabel("Member.rehearsal.label")));

    GemMenuButton b = getScheduleRangeButton(p.getMember());
    listPanel.add(b);

    Vector<GemMenuButton> modifButtons = modifCtrl.getMenuMemberRehearsal();
    for (int i = 0; i < modifButtons.size(); i++) {
      menuPanel.add((GemMenuButton) modifButtons.elementAt(i));
    }
  }

  private void loadBookingMemberSchedule(Schedule sched) {
    BookingMemberSchedule p = (BookingMemberSchedule) sched;
    headPanel.add(new GemLabel(BundleUtil.getLabel("Booking.label")));

    GemMenuButton b = getScheduleRangeButton(p.getMember());
    listPanel.add(b);
    Vector<GemMenuButton> modifButtons = modifCtrl.getMenuBooking();
    for (int i = 0; i < modifButtons.size(); i++) {
      menuPanel.add((GemMenuButton) modifButtons.elementAt(i));
    }

  }

  private void loadGroupRehearsalSchedule(Schedule plan) {
    GroupRehearsalSchedule p = (GroupRehearsalSchedule) plan;
    GemLabel l = new GemLabel(BundleUtil.getLabel("Group.rehearsal.label"));
    headPanel.add(l);

    StringBuilder buf = new StringBuilder(BundleUtil.getLabel("Group.label")).append(" ");
    buf.append(p.getGroup().getName());// unescape
    GemMenuButton b = new GemMenuButton(buf.toString(), this, "GroupLink", p.getGroup());
    headPanel.add(b);
    try {
      loadMusicianList(groupService.getMusicians(plan.getIdPerson()));
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    Vector<GemMenuButton> modifButtons = modifCtrl.getMenuGroupRehearsal();
    for (int i = 0; i < modifButtons.size(); i++) {
      menuPanel.add((GemMenuButton) modifButtons.elementAt(i));
    }
    menuPanel.add(btGroupWrite);//mailing button
  }

  private void loadBookingGroupSchedule(Schedule plan) {
    BookingGroupSchedule bg = (BookingGroupSchedule) plan;
    headPanel.add(new GemLabel(bg.getScheduleLabel()));
    StringBuilder buf = new StringBuilder(BundleUtil.getLabel("Group.label")).append(" ");
    buf.append(bg.getGroup().getName());
    GemMenuButton b = new GemMenuButton(buf.toString(), this, "GroupLink", bg.getGroup());
    headPanel.add(b);
    try {
      loadMusicianList(groupService.getMusicians(plan.getIdPerson()));
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    Vector<GemMenuButton> modifButtons = modifCtrl.getMenuBooking();
    for (int i = 0; i < modifButtons.size(); i++) {
      menuPanel.add((GemMenuButton) modifButtons.elementAt(i));
    }
    menuPanel.add(btGroupWrite);//mailing button
  }

  private void loadStudioSchedule(Schedule plan) {
    GroupStudioSchedule s = (GroupStudioSchedule) plan;
    headPanel.add(new GemLabel(s.getActivityLabel()));
    StringBuilder buf = new StringBuilder(BundleUtil.getLabel("Group.label")).append(" ");
    buf.append(s.getGroup().getName());// unescape
    GemMenuButton b = new GemMenuButton(buf.toString(), this, "GroupLink", s.getGroup());
    headPanel.add(b);
    try {
      loadMusicianList(groupService.getMusicians(plan.getIdPerson()));
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    Vector<GemMenuButton> modifButtons = modifCtrl.getMenuStudio(Schedule.STUDIO);
    for (int i = 0; i < modifButtons.size(); i++) {
      menuPanel.add((GemMenuButton) modifButtons.elementAt(i));
    }
    menuPanel.add(btGroupWrite);//mailing button
  }

  private void loadTechnicianSchedule(ScheduleDetailEvent de) {
    TechStudioSchedule p = (TechStudioSchedule) de.getSchedule();
    headPanel.add(new GemLabel(p.getScheduleDetail()));
    StringBuilder buf = new StringBuilder(BundleUtil.getLabel("Group.label")).append(" ");
    buf.append(p.getGroup().getName());// unescape
    GemMenuButton b = new GemMenuButton(buf.toString(), this, "GroupLink", p.getGroup());
    headPanel.add(b);
    loadTechnicianList(de.getRanges());
    Vector<GemMenuButton> modifButtons = modifCtrl.getMenuStudio(Schedule.TECH);
    for (int i = 0; i < modifButtons.size(); i++) {
      menuPanel.add((GemMenuButton) modifButtons.elementAt(i));
    }
    menuPanel.add(btWrite);//mailing button
  }

  private void loadWorkshopSchedule(ScheduleDetailEvent de) {
    WorkshopSchedule p = (WorkshopSchedule) de.getSchedule();
    StringBuffer buf = new StringBuffer(BundleUtil.getLabel("Workshop.label")).append(" ");
    buf.append(p.getScheduleLabel());
    GemMenuButton b = new GemMenuButton(buf.toString(), this, "WorkshopLink", p.getCourse());
    headPanel.add(b);

    buf = new StringBuffer(BundleUtil.getLabel("Teacher.label")).append(" ");
    buf.append(p.getTeacher().getFirstnameName());
    b = new GemMenuButton(buf.toString(), this, "TeacherLink", p.getTeacher());
    headPanel.add(b);

    Vector<ScheduleRangeObject> v = de.getRanges();
    for (int i = 0; v != null && i < v.size(); i++) {
      ScheduleRangeObject pg = v.elementAt(i);
      Person per = pg.getMember();
      buf = new StringBuffer(nameFirst ? per.getNameFirstname() : per.getFirstnameName());
      Member m = null;
      try {
        m = memberService.findMember(per.getId());
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      }
      if (m != null && m.getFirstInstrument() > 0) {
        String instrumentName = getInstrumentName(pg, per);
        if (instrumentName != null) {
          buf.append(" : ").append(instrumentName);
        }
      }
      listPanel.add(new GemMenuButton(buf.toString(), this, "MemberLink", pg));
    }
    List<GemMenuButton> vb = modifCtrl.getMenuCourse(true); // ajout des boutons de PlanModifCtrl
    for (GemMenuButton bt : vb) {
      menuPanel.add(bt);
    }
    menuPanel.add(btWrite);//mailing button
  }

  private void loadAdministrativeSchedule(ScheduleDetailEvent event) {
    AdministrativeSchedule p = (AdministrativeSchedule) event.getSchedule();
    headPanel.add(new GemLabel(BundleUtil.getLabel("Diary.label")));

    String n = p.getPerson().getFirstnameName();
    GemMenuButton b = new GemMenuButton(n, this, "PersonLink", p.getPerson());
    headPanel.add(b);
    StringBuilder sb = new StringBuilder();
    for (ScheduleRangeObject sr : event.getRanges()) {
      if (sr.getNote() > 0) {
        try {
          sr.setFollowUp(scheduleService.getFollowUp(sr.getNote()));
          if (sr.getMember().getId() == p.getPerson().getId()) {
            sb.append(sr.getStart()).append("-").append(sr.getEnd());
            sb.append(" : * ").append(sr.getFollowUp() != null ? sr.getFollowUp().getContent() : "");
            sb.append(" (").append(sr.getMember().getFirstnameName()).append(')');
            listPanel.add(new GemMenuButton(sb.toString(), this, "AdminEvent", sr));
          }
          sb.delete(0, sb.length());
        } catch (SQLException ex) {
          GemLogger.log(ex.getMessage());
        }
      }

    }
    for (ScheduleRangeObject sr : event.getRanges()) {
      if (sr.getMember().getId() != p.getPerson().getId()) {
        sb.append(sr.getStart()).append("-").append(sr.getEnd()).append(" : ");
        Person per = sr.getMember();
        sb.append(nameFirst ? per.getNameFirstname() : per.getFirstnameName());
        listPanel.add(new GemMenuButton(sb.toString(), this, "MemberLink", sr));
      }
      sb.delete(0, sb.length());
    }
    Vector<GemMenuButton> vb = modifCtrl.getMenuAdministrative(); // ajout des boutons de PlanModifCtrl
    for (int j = 0; j < vb.size(); j++) {
      menuPanel.add((GemMenuButton) vb.elementAt(j));
    }

  }

  /**
   * Gets a button labelled with session time range followed by member name.
   */
  GemMenuButton getScheduleRangeButton(Person per) {
    StringBuilder buf = new StringBuilder(schedule.getStart().toString());
    buf.append("-").append(schedule.getEnd());
    buf.append(" ").append(per.getFirstnameName());
    return (new GemMenuButton(buf.toString(), this, "PersonLink", per));
  }

  /**
   * Gets a button labelled with member name followed by his instrument.
   */
  GemMenuButton getMemberButton(Musician mus) {
    StringBuilder buf = new StringBuilder(mus.getFirstnameName());
    buf.append(" : ").append(dataCache.getInstrumentName(mus.getInstrument()));
    return (new GemMenuButton(buf.toString(), this, "PersonLink", mus));
  }

  /**
   * Adds as many buttons as members of a group or workshop.
   */
  private void loadMusicianList(List<Musician> v) {
    if (v == null) {
      return;
    }
    Collections.sort(v, personComparator);
    for (int i = 0; i < v.size(); i++) {
      listPanel.add(getMemberButton(v.get(i)));
    }
  }

  private void loadTechnicianList(Vector<ScheduleRangeObject> ranges) {
    if (ranges.size() > 0) {
      Collections.sort(ranges, psComparator);
    }
    for (ScheduleRangeObject sr : ranges) {
      Person p = sr.getMember();
      listPanel.add(new GemMenuButton(p == null ? "" : p.getFirstnameName(), this, "PersonLink", sr));
    }

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Course c;
    try {
      if ("CloseLink".equals(arg)) {
        frame.setVisible(false);
      } else if ("NullMember".equals(arg)) {
        ScheduleRange g = (ScheduleRange) ((GemMenuButton) evt.getSource()).getObject();
        MessagePopup.warning(null, MessageUtil.getMessage("member.null.exception", g.getMemberId()));
      } else if ("MemberLink".equals(arg)) {
        setWaitCursor();
        ScheduleRangeObject range = (ScheduleRangeObject) ((GemMenuButton) evt.getSource()).getObject();
        if (Schedule.COURSE == schedule.getType()) {
          c = ((CourseSchedule) schedule).getCourse();
          //if (!(evt.getModifiers() == InputEvent.BUTTON1_MASK)) {
          if ((evt.getModifiers() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {//ouverture du suivi élève touche MAJ
            setFollowUp(range, c);
            return;
          }
        }
        if ((evt.getModifiers() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
          deleteRange(range);
          return;
        }

        Person p = range.getMember();
        PersonFileEditor editor = ((GemDesktopCtrl) desktop).getPersonFileEditor(p.getId());
        if (editor != null) {
          desktop.setSelectedModule(editor);
        } else {
          setWaitCursor();
          PersonFile pf = (PersonFile) DataCache.findId(p.getId(), Model.PersonFile);
          loadPersonFile(pf);
        }

      } else if ("PersonLink".equals(arg)) {
        setWaitCursor();
        Person p = null;
        Object src = ((GemMenuButton) evt.getSource()).getObject();
        if (src instanceof ScheduleRangeObject) {
          ScheduleRangeObject range = (ScheduleRangeObject) ((GemMenuButton) evt.getSource()).getObject();
          if ((evt.getModifiers() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
            deleteRange(range);
            return;
          }
          p = range.getMember();
        } else if (src instanceof Person) {
          p = (Person) ((GemMenuButton) evt.getSource()).getObject();
        } else {
          return;
        }
        PersonFile pf = (PersonFile) DataCache.findId(p.getId(), Model.PersonFile);
        loadPersonFile(pf);
      } else if ("TeacherLink".equals(arg)) {
        if (!(evt.getModifiers() == InputEvent.BUTTON1_DOWN_MASK)) {
          TeacherBreakDlg dlg = new TeacherBreakDlg(desktop, (CourseSchedule) schedule);
          dlg.entry();
          if (dlg.isValidation()) {
            dlg.save();
          } else if (dlg.getError() != null) {
            MessagePopup.information(frame, dlg.getError());
          }
          return;
        }
        setWaitCursor();
        Person p = (Person) ((GemMenuButton) evt.getSource()).getObject();
        PersonFile pf = (PersonFile) DataCache.findId(p.getId(), Model.PersonFile);
        loadPersonFile(pf);
      } else if ("GroupLink".equals(arg)) {// ouverture fiche groupe
        setWaitCursor();
        Group g = (Group) ((GemMenuButton) evt.getSource()).getObject();
        GroupFileEditor groupEditor = new GroupFileEditor(g, schedule);
        desktop.addModule(groupEditor);
        frame.setLocation(getOffset(groupEditor.getView()));
      } else if ("RoomLink".equals(arg)) { // ouverture fiche salle
        setWaitCursor();
        Room s = (Room) ((GemMenuButton) evt.getSource()).getObject();
        RoomFileEditor roomEditor = new RoomFileEditor(s);
        roomEditor.setDate(schedule.getDate().getDate());
        desktop.addModule(roomEditor);
        frame.setLocation(getOffset(roomEditor.getView()));
      } else if ("CourseLink".equals(arg) || "WorkshopLink".equals(arg)) {
        setWaitCursor();
        c = (Course) ((GemMenuButton) evt.getSource()).getObject();
        if (!(evt.getModifiers() == InputEvent.BUTTON1_DOWN_MASK)) { // ouverture du suivi cours touche majuscule
          if (schedule.getNote() > 0) {
            ((ScheduleObject) schedule).setFollowUp(scheduleService.getFollowUp(schedule.getNote()));
          }
          CommonFollowUpDlg dlg = new CommonFollowUpDlg(desktop, scheduleService, (ScheduleObject) schedule, c.getTitle(), true);
          dlg.entry();
        } else {
          CourseCtrl courseCard = new CourseCtrl(desktop);
          courseCard.addActionListener((GemDesktopCtrl) desktop);
          courseCard.loadCard(c);
          desktop.addPanel("Cours " + c.getTitle(), courseCard);
          frame.setLocation(getOffset(desktop.getSelectedModule().getView()));
        }
      } else if ("Mailing".equals(arg)) {
        Vector<ScheduleRangeObject> ranges = detailEvent.getRanges();//plages
        String message = MAIL_UTIL.mailToMembers(ranges, schedule);
        if (message.length() > 0) {
          String info = MessageUtil.getMessage("members.without.email");
          new MessageDialog(frame, BundleUtil.getLabel("Information.label"), false, info, message);
        }
      } else if ("MailingGroup".equals(arg)) {
        List<Musician> mus = null;
        mus = groupService.getMusicians(schedule.getIdPerson());
        if (mus != null && mus.size() > 0) {
          String message = "";
          switch (schedule.getType()) {
            case Schedule.GROUP:
              message = MAIL_UTIL.mailToGroupMembers(mus);
              break;
            case Schedule.STUDIO:
              message = MAIL_UTIL.mailToGroupMembers(mus, schedule.getIdAction());
              break;
            default:
              message = MAIL_UTIL.mailToGroupMembers(mus);
              break;
          }
          //String message = (Schedule.GROUP == schedule.getType()) ? MAIL_UTIL.mailToGroupMembers(mus) : MAIL_UTIL.mailToGroupMembers(mus, schedule.getIdAction());
          if (message.length() > 0) {
            String info = MessageUtil.getMessage("group.members.without.email");
            new MessageDialog(desktop.getFrame(), BundleUtil.getLabel("Information.label"), false, info, message);
          }
        }
      } else if ("BreakLink".equals(arg)) {
        ScheduleRangeObject po = (ScheduleRangeObject) ((GemMenuButton) evt.getSource()).getObject();
        DateFr start = po.getDate();
        BreakSuppressionDlg dlg = new BreakSuppressionDlg(desktop.getFrame(), true, start, dataCache.getEndOfYear());
        if (dlg.isValidate()) {
          try {
            scheduleService.deleteBreak(po, start, dlg.getDate());
            desktop.postEvent(new ModifPlanEvent(this, start, dlg.getDate()));
          } catch (SQLException ex) {
            MessagePopup.warning(dlg, MessageUtil.getMessage("break.delete.exception"));
            GemLogger.logException(ex);
          }
        }
      } else if ("AdminEvent".equals(arg)) {
        GemLogger.log(Level.INFO, arg);
        ScheduleRangeObject range = (ScheduleRangeObject) ((GemMenuButton) evt.getSource()).getObject();
        new EditEventDlg(desktop, range, schedule, scheduleService);
      }
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    } catch (PlanningException pex) {
      GemLogger.logException(pex);
    } finally {
      setDefaultCursor();
    }
  }

  /**
   * Opens a followUp dialog.
   *
   * @param range the selected time slot
   * @param c the selected course
   * @throws PlanningException
   * @throws SQLException
   */
  private void setFollowUp(ScheduleRangeObject range, Course c) throws PlanningException, SQLException {

    if (range.getNote() != 0 && range.getFollowUp() == null) {
      range.setFollowUp(scheduleService.getFollowUp(range.getNote()));
    }
    FollowUpDlg dlg = new FollowUpDlg(desktop, range, c.getTitle(), false);
    dlg.entry();
    if (dlg.isValidation()) {
      scheduleService.updateFollowUp(range, dlg.getFollowUp());
      range.setFollowUp(dlg.getFollowUp());
    }

  }

  /**
   * Deletes the time slot of the student within the selected schedule.
   *
   * @param range the selected time slot
   * @throws PlanningException
   */
  private void deleteRange(ScheduleRangeObject range) throws PlanningException {
    if (MessagePopup.confirm(desktop.getFrame(), MessageUtil.getMessage("schedule.range.delete.confirmation"))) {
      scheduleService.deleteScheduleRange(range);
      desktop.postEvent(new ModifPlanEvent(this, range.getDate(), range.getDate()));
      frame.setVisible(false);
    }
  }

  /**
   * Loads the person file's editor.
   *
   * @param dossier
   */
  private void loadPersonFile(PersonFile dossier) {
    PersonFileEditor editor = new PersonFileEditor(dossier);
    desktop.addModule(editor);
    frame.setLocation(getOffset(editor.getView()));
  }

  /**
   * Gets the position to move the view after opening.
   *
   * @param view
   * @return a point
   */
  private Point getOffset(DefaultGemView view) {

    int dw = desktop.getFrame().getWidth();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    int x = view.getX() + view.getWidth();
    int y = view.getLocationOnScreen().y + view.getHeight();
    if (x > dw) {
      x = dw - 100;
    }
    if (y + frame.getHeight() > screenSize.getHeight()) {
      y = view.getLocationOnScreen().y + 88;// don't mask close and save buttons
//      y = (int) screenSize.getHeight() - frame.getHeight() - 100;// 100 = reserved space at bottom (for taskbar)
    }
    return new Point(x, y);
  }

  private void setWaitCursor() {
    JRootPane root = ((JRootPane) frame.getRootPane());
    root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    root.getGlassPane().setVisible(true);
  }

  private void setDefaultCursor() {
    JRootPane root = ((JRootPane) frame.getRootPane());
    root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    root.getGlassPane().setVisible(false);
  }

}
