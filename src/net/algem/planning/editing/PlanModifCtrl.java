/*
 * @(#)PlanModifCtrl.java	2.9.2 19/12/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.editing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import net.algem.config.GemParam;
import net.algem.contact.EmployeeIO;
import net.algem.contact.EmployeeType;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberService;
import net.algem.contact.teacher.SubstituteTeacherList;
import net.algem.contact.teacher.TeacherService;
import net.algem.course.Course;
import net.algem.group.GemGroupService;
import net.algem.group.Group;
import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemMenuButton;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for planning modification.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 1.0b 05/07/2002 lien salle et groupe
 */
public class PlanModifCtrl
        implements ActionListener
{

  private Calendar cal;
  private ScheduleObject plan;
  private UpdateCoursePlanCtrl updatePlanCtrl;
  private final GemDesktop desktop;
  private final DataCache dataCache;
  private final DataConnection dc;
  private final PlanningService service;
  private final MemberService memberService;

  public PlanModifCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    dc = DataCache.getDataConnection();
    memberService = new MemberService(dc);
    service = new PlanningService(dc);
    cal = Calendar.getInstance(Locale.FRANCE);
  }

  public void setPlan(Schedule p) {
    if (p instanceof ScheduleObject) {
      plan = (ScheduleObject) p;
    } else {
      System.out.println("PlanModifCtrl#setPlan " + p);
    }
    cal.setTime(p.getDate().getDate());
  }

  /**
   * Gets a list of buttons for course schedule modification.
   *
   * @return a list of buttons
   */
  public Vector<GemMenuButton> getCourseMenu() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.teacher.modification.label"), this, "ChangeTeacher"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.course.shifting.label"), this, "PutOffCourse"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.course.copy.label"), this, "CopyCourse"));
    if (dataCache.authorize("Course.schedule.modification.auth")) {
      v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.course.modification.label"), this, "ChangeCourse"));
    }
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.time.modification.label"), this, "ChangeScheduleLength"));
    if (dataCache.authorize("Course.schedule.modification.auth")) {
      v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.action.modification.label"), this, "ModifiyAction"));
    }
    if (dataCache.authorize("Schedule.suppression.auth")) {
      v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.suppression.label"), this, "DeletePlanning"));
    }

    /* v.add(new GemMenuButton("Replanifier ce cours", this, "Replanifier")); */
    return v;
  }

  /**
   * Gets a list of buttons for rehearsal schedule.
   *
   * @return a list of buttons
   */
  public Vector<GemMenuButton> getMenuMemberRehearsal() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.hour.modification.label"), this, "ChangeHour"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.rehearsal.cancellation.label"), this, "CancelRehearsal"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.paid.annotation.label"), this, "MarkPaid"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.unpaid.annotation.label"), this, "MarkNotPaid"));

    return v;
  }

  /**
   * Gets a list of buttons for group schedule modification.
   *
   * @return a list of buttons
   */
  public Vector<GemMenuButton> getMenuGroupRehearsal() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.hour.modification.label"), this, "ChangeHour"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.rehearsal.cancellation.label"), this, "CancelRehearsal"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.paid.annotation.label"), this, "MarkPaid"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.unpaid.annotation.label"), this, "MarkNotPaid"));

    return v;
  }

  /**
   * Gets a list of buttons for workshop schedule modification.
   *
   * @return a list of buttons
   */
  public Vector<GemMenuButton> getMenuWorkshop() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.teacher.modification.label"), this, "ChangeTeacher"));
    //v.add(new GemMenuButton("Changer de date", this, "ChangerDate"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.workshop.cancellation.label"), this, "CancelWorkshop"));

    return v;
  }

  /**
   * Gets a list of buttons for studio group schedule modification.
   *
   * @param type studio type
   * @return a list of buttons
   */
  public Vector<GemMenuButton> getMenuStudio(int type) {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.time.modification.label"), this, "ChangeScheduleLength"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Session.type.modification.label"), this, "ChangeSessionType"));
    if (Schedule.TECH == type) {
      v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.add.participant.label"), this, "AddParticipant"));
    }

    if (dataCache.authorize("Schedule.suppression.auth")) {
      v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.suppression.label"), this, "DeletePlanning"));
    }
    return v;
  }

  /**
   * Gets a list of buttons for schedule creation.
   *
   * @return a list of buttons
   */
  public Vector<GemMenuButton> getMenuPlanning() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();
    /* v.add(new GemMenuButton("Marquer salle indisponible", this, "InsertSalleNonDispo")); */
    return v;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    String arg = evt.getActionCommand();

    desktop.setWaitCursor();
    try {
      if (arg.equals("ChangeRoom")) {
        dialogChangeRoom();
      } else if (arg.equals("PutOffCourse")) {
        dialogPostponeCourse();
      } else if (arg.equals("CopyCourse")) {
        dialogCopyCourse();
      } else if (arg.equals("ChangeScheduleLength")) {
        dialogPlanningLength();
      } else if (arg.equals("ChangeCourse")) {
        dialogChangeCourse();
      } else if (arg.equals("DeletePlanning")) {
        if (dataCache.authorize("Schedule.suppression.auth")) {
          dialogPlanningSuppression();
        } else {
          MessagePopup.information(desktop.getFrame(), MessageUtil.getMessage("delete.exception") + MessageUtil.getMessage("rights.exception"));
        }
      } else if ("ModifiyAction".equals(arg)) {
        dialogModifyAction();
      } else if (arg.equals("ChangeHour")) {
        dialogChangeHour();
      } else if (arg.equals("ChangeTeacher")) {
        dialogChangeTeacher();
      } else if (arg.equals("AddParticipant")) {
        dialogAddParticipant(EmployeeType.TECHNICIAN);
      } else if (arg.equals("ChangeSessionType")) {
        dialogChangeSessionType();
      } else if (arg.equals("CancelWorkshop")) {
        dialogCancelWorkshop();
      } else if (arg.equals("CancelRehearsal")) {
        dialogCancelRehearsal();
      } else if (arg.equals("MarkPaid")) {
        service.markPaid(plan);
        desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
      } else if (arg.equals("MarkNotPaid")) {
        service.markNotPaid(plan);
        desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
      } else if (arg.equals(GemCommand.CANCEL_CMD)) {
        desktop.removeCurrentModule();
      }
      /*
       else if (arg.bufferEquals("Replanifier")) {
       dialogDeplacerCours();
       } else if (arg.equalsIgnoreCase("Replanifier.Validation")) {
       try {
       dataCache.setAutoCommit(false);
       Action upAction = updatePlanCtrl.get();
       cal.setTime(plan.getDate().getDate());
       Action action = getActionFrom(upAction, plan, cal.get(Calendar.DAY_OF_WEEK) - 1);
       if (service.deletePlanning(action) == 0) {// aucune plage élève n'existe pour ce planning
       updatePlanCtrl.save();
       dataCache.commit();
       desktop.postEvent(new ModifPlanEvent(this, action.getDateEnd(), action.getDateEnd()));
       updatePlanCtrl.cancel();
       }
       } catch (Exception ex) {
       System.err.println(ex.getMessage());
       dataCache.logException("Replanification cours", ex);
       dataCache.rollback();
       } finally {
       dataCache.setAutoCommit(true);
       }
       }
       */
    } catch (PlanningException ex) {
      GemLogger.log(ex.getMessage());
      MessagePopup.warning(desktop.getFrame(), ex.getMessage());
    } catch (SQLException sqe) {
      GemLogger.log(sqe.getMessage());
    } finally {
      desktop.setDefaultCursor();
    }
  }

  /** Calls hour modification dialog. */
  private void dialogChangeHour() {

    ModifPlanHourDlg dlg = new ModifPlanHourDlg(desktop.getFrame(), getLabel(plan), dataCache);

    dlg.setTitle(plan.getScheduleLabel());
    dlg.setDate(cal.getTime());
    dlg.setHour(plan.getStart(), plan.getEnd());

    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }

    DateFr start = dlg.getStart();
    DateFr end = dlg.getEnd();
    Hour hStart = dlg.getNewHourStart();
    Hour hEnd = dlg.getNewHourEnd();

    try {
      Vector<ScheduleTestConflict> v = service.checkHour(plan, start, end, hStart, hEnd);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits changement d'heure");
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflict((ScheduleTestConflict) v.elementAt(i));
        }
        cfd.show();
        return;
      }

      dc.setAutoCommit(false);
      changeHour(start, end, hStart, hEnd);
      if (ScheduleObject.MEMBER == plan.getType()) {
        memberService.updatePersonalSession(plan, hStart, hEnd);

      }
      dc.commit();
      desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));//XXX dlg.getDateEnd/Fin

    } catch (Exception e) {
      dc.rollback();
      GemLogger.logException("Changement d'heure", e);
    } finally {
      dc.setAutoCommit(true);
    }

  }

  /**
   * Changes time schedule start.
   * Only for members and groups rehearsals ???
   */
  private void changeHour(DateFr start, DateFr end, Hour hStart, Hour hEnd) throws Exception {
    String query = "UPDATE planning SET debut = '" + hStart + "', fin='" + hEnd + "'"
            + " WHERE action = " + plan.getIdAction()
            + " AND jour >= '" + start + "' AND jour <= '" + end + "'"
            + " AND ptype = " + plan.getType()
            + " AND debut = '" + plan.getStart() + "' AND fin = '" + plan.getEnd() + "'"
            + " AND lieux = " + plan.getIdRoom() + " AND idper = " + plan.getIdPerson();
    if (dc.executeUpdate(query) < 1) {
      throw new Exception("PLANNING UPDATE=0 " + query);
    }
    // Useless and query to rewrite ??
    /*if (plan instanceof CourseSchedule || plan instanceof WorkshopSchedule) {
     query = "UPDATE plage SET debut = '" + hStart + "',fin = '" + hEnd + "' WHERE cours = " + plan.getIdAction() + " AND jour >= '" + start + "' AND jour <= '" + end + "'";
     dc.executeUpdate(query);
     }*/
  }

  /**
   * Calls parameters modification dialog.
   */
  private void dialogModifyAction() {
    if (!(plan instanceof CourseSchedule)) {
      return;
    }
    try {
      Action a = ((CourseSchedule) plan).getAction();
      ModifPlanActionDlg dlg = new ModifPlanActionDlg(desktop, a);
      dlg.show();
      if (!dlg.isValidate()) {
        return;
      }
      a = dlg.get();
      a.setPlaces(maxPlaces(a.getPlaces(), plan.getRoom()));

      service.updateAction(a);
      dataCache.update(a);
      desktop.postEvent(new ModifPlanEvent(service, plan.getDate(), plan.getDate()));
    } catch (SQLException sqe) {
      MessagePopup.warning(null, sqe.getMessage());
    }
  }

  /**
   * Gets the maximum number of places available for this {@literal room}.
   *
   * @param np maximum number of places of the current action
   * @param room room instance
   * @return a short number
   */
  private short maxPlaces(short np, Room room) {

    short s = (short) room.getNPers();
    if (s > 0 && np > s) {
      np = s;
      MessagePopup.warning(null, MessageUtil.getMessage("max.room.place.warning", new Object[]{np}));
    }
    return np;
  }

  /**
   * Calls a dialog for changing course.
   */
  private void dialogChangeCourse() {
    ModifPlanCourseDlg dlg = new ModifPlanCourseDlg(desktop, plan);
    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }

    Action a = new Action(plan.getIdAction());
    a.setCourse(dlg.getCourse());
    a.setDateStart(dlg.getStart());
    a.setDateEnd(dlg.getEnd());

    try {
      Course oc = service.getCourseFromAction(plan.getIdAction());
      Course nc = service.getCourseFromId(a.getCourse());
      if (oc.isCollective() != nc.isCollective()) {
        MessagePopup.warning(null, MessageUtil.getMessage("invalid.course.modification"));
        return;
      }
      service.changeCourse(a);
      desktop.postEvent(new ModifPlanEvent(service, dlg.getStart(), dlg.getEnd()));
    } catch (PlanningException ex) {
      String msg = MessageUtil.getMessage("planning.course.modify.exception");
      MessagePopup.warning(null, msg + "\n" + ex.getMessage());
      GemLogger.logException(msg, ex);
    } catch (SQLException sqe) {
      System.err.println(sqe.getMessage());
    }
  }

  /**
   * Calls modification room dialog.
   */
  private void dialogChangeRoom() {
    ModifPlanRoomDlg dlg = new ModifPlanRoomDlg(desktop.getFrame(), getLabel(plan), dataCache);

    dlg.setTitle(plan.getScheduleLabel());
    dlg.setDate(cal.getTime());
    dlg.setRoom(plan.getIdRoom());

    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }

    DateFr start = dlg.getStart();
    DateFr end = dlg.getEnd();
    int roomId = dlg.getNewRoom();

    try {
      Vector<ScheduleTestConflict> v = service.checkChangeRoom(plan, start, end, roomId);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits changement de salle");
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflict(v.elementAt(i));
        }
        cfd.show();
        return;
      }
      if (plan instanceof StudioSchedule) {
        service.changeRoom(plan.getId(), roomId);
      } else {
        service.changeRoom(plan, start, end, roomId);
      }
      desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));//XXX dlg.getDateEnd/Fin
    } catch (PlanningException e) {
      GemLogger.logException("Change room", e);
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
      MessagePopup.warning(null, sqe.getMessage());
    }

  }

  /**
   * Calls modification teacher dialog.
   * Estab and course id are necessary for searching a substitute.
   */
  private void dialogChangeTeacher() {

    TeacherService teacherService = new TeacherService(dc);

    SubstituteTeacherList substitutes = new SubstituteTeacherList(teacherService.getSubstitutes(plan));
    ModifPlanTeacherDlg dlg = new ModifPlanTeacherDlg(desktop, substitutes, service);

    dlg.setDate(cal.getTime());
    dlg.set(plan);

    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }

    ScheduleObject range = dlg.getSchedule();
    DateFr start = dlg.getStart();
    DateFr end = dlg.getEnd();

    try {
      Vector<ScheduleTestConflict> v = service.checkChangeTeacher(plan, range, start, end);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Teacher.change.conflicts.label"));
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflict(v.elementAt(i));
        }
        cfd.show();
        return;
      }
      // on ne modifie que ce planning s'il en existe plusieurs partageant la même action à la même date.
      if (start.equals(end) && service.hasSiblings(plan) && MessagePopup.confirm(null, MessageUtil.getMessage("teacher.modification.single.schedule.confirmation"))) {
        //modifier cette fonction pour ajouter les insert dans la table absence
        service.changeTeacherForSchedule(plan, range, start);
      } else {
        //modifier cette fonction pour ajouter les insert dans la table absence
        service.changeTeacher(plan, range, start, end);
      }
      desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));//XXX dlg.getDateEnd/Fin
    } catch (PlanningException e) {
      MessagePopup.warning(null, e.getMessage());
    }
  }

  private void dialogAddParticipant(Enum cat) throws PlanningException {

    String where = ", " + EmployeeIO.TYPE_TABLE + " t  WHERE "
            + PersonIO.TABLE + ".id = t.idper AND t.idcat = " + cat.ordinal();
    List<Person> persons = PersonIO.find(where, DataCache.getDataConnection());
    if (persons.size() < 1) {
      throw new PlanningException("Aucun participant disponible");
    }
    AddParticipantDlg dlg = new AddParticipantDlg(desktop, plan, persons);
    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }
    ScheduleRange sr = new ScheduleRange();
    sr.setScheduleId(plan.getId());
    sr.setMemberId(dlg.getParticipant());
    sr.setStart(plan.getStart());
    sr.setEnd(plan.getEnd());
    service.addScheduleRange(sr);
    desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));

  }

  private void dialogChangeSessionType() throws PlanningException {
    GemParam gp = (GemParam) plan.getActivity();
    ChangeSessionTypeDlg dlg = new ChangeSessionTypeDlg(desktop, gp.getId());
    dlg.show();
    if (dlg.isEntryValid()) {
      try {
        plan.setNote(dlg.getType());
        service.updateSessionType(plan);
        desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
      } catch (SQLException ex) {
        throw new PlanningException(ex.getMessage());
      }
    }
  }

  private void dialogCopyCourse() {
    PostponeCourseDlg dlg = new PostponeCourseDlg(desktop, plan, service, "Schedule.course.copy.title");
    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }

    ScheduleObject newPlan = dlg.getSchedule();

    try {
      if (!testConflictCopyCourse(plan, newPlan)) {
        return;
      }
      ScheduleObject copy = new CourseSchedule(plan);
      copy.setDate(newPlan.getDate());
      copy.setStart(newPlan.getStart());
      copy.setEnd(newPlan.getEnd());
      copy.setIdRoom(newPlan.getIdRoom());
      copy.setNote(0);
      service.copyCourse(plan, copy);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    } catch (PlanningException e) {
      MessagePopup.warning(null, e.getMessage());
      GemLogger.logException("Copy course", e);
    }
  }

  /**
   * Calls course time modification dialog.
   * This dialog is runned when date or hour must be changed. If not, it is
   * preferable to call the dialog for room modification.
   */
  private void dialogPostponeCourse() {
    PostponeCourseDlg dlg = new PostponeCourseDlg(desktop, plan, service, "Schedule.course.shifting.title");
    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }

    ScheduleObject newPlan = dlg.getSchedule();
    Hour[] range = dlg.getRange();
//     if (!((Course) plan.getActivity()).isCollective()) {
//        Vector<ScheduleRange> ranges = ScheduleRangeIO.find("WHERE idplanning = " + plan.getId(), dc);
//        PersonListCtrl list = new PersonListCtrl();
//        list.addBlock(ranges);
//        // ouvrir un dialogue de sélection
//        // mettre à jour les plages en fonction des adhérents sélectionnés
//      }

    try {
      if (!testConflictCourse(plan, newPlan, range)) {
        return;
      }
      if (range[0].after(plan.getStart())) {
        if (range[1].before(plan.getEnd())) {
          service.postPoneCourseBetween(plan, newPlan, range);
        } else {
          service.postPoneCourseAfter(plan, newPlan, range[0]);
        }
      } else if (range[1].before(plan.getEnd())) {
        service.postPoneCourseBefore(plan, newPlan, range[1]);
      } else {
        service.postPoneCourse(plan, newPlan);
      }
      desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    } catch (PlanningException e) {
      MessagePopup.warning(null, e.getMessage());
      GemLogger.logException("Put off course", e);
    }
  }

  private boolean testConflictCourse(ScheduleObject plan, ScheduleObject newPlan, Hour[] range)
          throws SQLException {

    Vector<ScheduleTestConflict> v = service.checkRoomForSchedulePostpone(plan, newPlan);
    // room conflict
    if (v.size() > 0) {
      ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Room.conflict.label"));
      for (int i = 0; i < v.size(); i++) {
        cfd.addConflict(v.elementAt(i));
      }
      cfd.show();
      return false;
    }

    // teacher conflict
    v = service.checkTeacherForSchedulePostpone(plan, newPlan);
    if (v.size() > 0) {
      ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Teacher.conflict.label"));
      for (int i = 0; i < v.size(); i++) {
        cfd.addConflict(v.elementAt(i));
      }
      cfd.show();
      return false;
    }

    // TODO member conflict
    return true;
  }

  private boolean testConflictCopyCourse(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {

    Vector<ScheduleTestConflict> v = service.checkRoomForScheduleCopy(newPlan);

    if (v.size() > 0) {
      ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Room.conflict.label"));
      for (int i = 0; i < v.size(); i++) {
        cfd.addConflict(v.elementAt(i));
      }
      cfd.show();
      return false;
    }

    v = service.checkTeacherForScheduleCopy(plan, newPlan);
    if (v.size() > 0) {
      ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Teacher.conflict.label"));
      for (int i = 0; i < v.size(); i++) {
        cfd.addConflict(v.elementAt(i));
      }
      cfd.show();
      return false;
    }
    return true;
  }

  /**
   * @deprecated
   */
  private void dialogMoveCourse() {
    updatePlanCtrl = new UpdateCoursePlanCtrl(desktop, plan);
    updatePlanCtrl.init();
    updatePlanCtrl.addActionListener(this);
    desktop.addPanel("planification cours", updatePlanCtrl);
  }

  /**
   * Calls a dialog for compressing or extending a schedule.
   */
  private void dialogPlanningLength() {

    ModifPlanRangeDlg dlg = new ModifPlanRangeDlg(desktop.getFrame(), plan);
    dlg.show();
    if (!dlg.isValidate()) {
      return;
    }
    DateFr lastDay = dlg.getDateEnd();
    Hour hStart = dlg.getNewHourStart();
    Hour hEnd = dlg.getNewHourEnd();
    // Le plan ne doit pas empiéter sur des cours existants
    try {
      if (plan instanceof CourseSchedule) {
        Course c = (Course) plan.getActivity();
        if (!c.isCollective()) {
          Vector<ScheduleTestConflict> v = service.testRange(plan, hStart, hEnd, lastDay);
          if (v.size() > 0) {
            ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), MessageUtil.getMessage("time.slot.conflict.detail", v.size()));
            for (int i = 0; i < v.size(); i++) {
              cfd.addConflict(v.elementAt(i));
            }
            cfd.show();
            return;
          }
        }
      }

      Vector<ScheduleTestConflict> v = service.checkRoomForScheduleLengthModif(plan, hStart, hEnd, lastDay);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Room.conflict.label"));
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflict(v.elementAt(i));
        }
        cfd.show();
        return;
      }

      //TODO check teacher occupation in studio schedules
      if (plan.getType() != Schedule.TECH && plan.getType() != Schedule.STUDIO) {
        Vector<ScheduleTestConflict> conflicts = service.checkTeacherForScheduleLengthModif(plan, lastDay, hStart, hEnd);
        if (conflicts != null && conflicts.size() > 0) {
          ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), BundleUtil.getLabel("Teacher.conflict.label"));
          for (int i = 0; i < conflicts.size(); i++) {
            cfd.addConflict(conflicts.elementAt(i));
          }
          cfd.show();
          return;
        }
      }
    } catch (SQLException sqe) {
      GemLogger.log(sqe.getMessage());
      return;
    }

    try {
      service.modifyPlanningLength(plan, hStart, hEnd, lastDay);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
    } catch (PlanningException ex) {
      GemLogger.logException(ex);
    }
  }

  /** Not implemented. */
  private void dialogCancelWorkshop() {
    MessagePopup.information(desktop.getFrame(), "Not yet implemented");
  }

  private void dialogCancelRehearsal() {

    RehearsalCancelDlg dlg = new RehearsalCancelDlg(desktop.getFrame(), plan);
    dlg.show();
    if (dlg.isValidate()) {
      try {
        // suppression du planning
        service.deleteRehearsal(dlg.getDateStart(), dlg.getDateEnd(), plan);
        if (ScheduleObject.MEMBER == plan.getType()) {
          memberService.cancelPersonalSession(dataCache, plan);
        } else if (ScheduleObject.GROUP == plan.getType()) {
          // annulation échéance
          Group g = new GemGroupService(dc).find(plan.getIdPerson());
          if (g != null && g.getIdref() > 0) {
            int delay = GemGroupService.MIN_ANNULATION;
            boolean ok = true;
            if (!RehearsalUtil.isCancelledBefore(plan.getDate(), delay)) {
              if (!MessagePopup.confirm(null, MessageUtil.getMessage("rehearsal.payment.cancel.confirmation"), "Confirmation")) {
                ok = false;
              }
            }
            if (ok) {
              memberService.deleteOrderLine(dlg.getDateStart(), g.getIdref());
            }
          }
        }
        desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
      } catch (SQLException ex) {
        GemLogger.logException("rehearsal.delete.exception", ex);
      }
    }
  }

  /**
   * Calls a dialog for schedule(s) suppression.
   *
   * @since 1.1e
   */
  private void dialogPlanningSuppression() {
    SupprPlanningDlg dlg = new SupprPlanningDlg(desktop.getFrame(), plan);
    dlg.show();

    if (dlg.isValidate()) {
      Action action = new Action();
      action.setId(plan.getIdAction());
      action.setDateStart(dlg.getDateStart());
      action.setDateEnd(dlg.getDateEnd());
      try {
        if (action.getDateStart().equals(action.getDateEnd())) {
          if (service.hasSiblings(plan)) {
            if (plan instanceof StudioSchedule) {
              deleteStudioSchedule(plan, action);
            } else {
              deleteSchedule(plan, action);
            }
          } else {
            service.deletePlanning(action);
          }
        } else {
          deletePlanning(plan, action);
        }
        desktop.postEvent(new ModifPlanEvent(this, plan.getDate(), plan.getDate()));
      } catch (PlanningException ex) {
        GemLogger.logException(ex);
        MessagePopup.warning(null, MessageUtil.getMessage("delete.error") + "\n" + ex.getMessage());
      }

    }
  }

  /**
   * Deletes the schedule [@code plan} on a given date.
   * Optionnaly deletes all shared instances of this schedule.
   *
   * @param plan schedule to delete
   * @param a action schedule
   * @throws PlanningException
   */
  private void deleteSchedule(ScheduleObject plan, Action a) throws PlanningException {
    if (!MessagePopup.confirm(null, MessageUtil.getMessage("schedule.multi.suppression.confirmation"))) {
      service.deleteSchedule(a, plan);
    } else {
      service.deletePlanning(a);
    }
  }

  /**
   * Deletes the studio schedule [@code plan} on a given date.
   * Optionnaly deletes all shared instances of this schedule.
   *
   * @param plan schedule to delete
   * @param a action schedule
   * @throws PlanningException
   */
  private void deleteStudioSchedule(ScheduleObject plan, Action a) throws PlanningException {
    if (!MessagePopup.confirm(null, MessageUtil.getMessage("schedule.studio.suppression.confirmation"))) {
      service.deleteSchedule(plan);
    } else {
      service.deletePlanning(a);
    }
  }

  /**
   * Deletes a set of schedules between two dates.
   * Optionnaly deletes all shared instances of these schedules.
   *
   * @param plan specific schedule instance
   * @param a action schedule
   * @throws PlanningException
   */
  private void deletePlanning(ScheduleObject plan, Action a) throws PlanningException {
    if (service.hasSiblings(a) && !MessagePopup.confirm(null, MessageUtil.getMessage("schedule.multi.suppression.confirmation"))) {
      service.deleteSchedule(a, plan);
    } else {
      service.deletePlanning(a);
    }
  }

  /**
   *
   * @param a
   * @param plan
   * @param jour
   * @return an action
   * @deprecated
   */
  private Action getActionFrom(Action a, ScheduleObject plan, int jour) {
    Action action = new Action();
    action.setDay(jour);
    action.setCourse(plan.getIdAction());
    action.setTeacher(plan.getIdPerson());
    action.setDateStart(a.getDateStart());
    action.setDateEnd(a.getDateEnd());
    action.setHourStart(plan.getStart());
    action.setHourEnd(plan.getEnd());
    action.setRoom(plan.getIdRoom());
    return action;
  }

  private String getLabel(ScheduleObject plan) {
    switch (plan.getType()) {
      case ScheduleObject.MEMBER:
        return BundleUtil.getLabel("Schedule.person.modification.label");
      case ScheduleObject.GROUP:
        return BundleUtil.getLabel("Schedule.group.modification.label");
      default:
        return BundleUtil.getLabel("Schedule.default.modification.label");
    }
  }
}
