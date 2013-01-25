/*
 * @(#)PlanModifCtrl.java	2.7.a 16/01/13
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
package net.algem.planning.editing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import net.algem.contact.member.MemberService;
import net.algem.contact.teacher.SubstituteTeacherList;
import net.algem.contact.teacher.TeacherService;
import net.algem.course.Course;
import net.algem.group.Group;
import net.algem.group.GroupService;
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
 * @version 2.7.a
 * @since 1.0b 05/07/2002 lien salle et groupe
 */
public class PlanModifCtrl
        implements ActionListener
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private DataConnection dc;
  private Calendar cal;
  private ScheduleObject plan;
  private UpdateCoursePlanCtrl updatePlanCtrl;
  private PlanningService service;
  private MemberService memberService;

  public PlanModifCtrl(GemDesktop _desktop) {
    desktop = _desktop;
    dataCache = desktop.getDataCache();
    dc = dataCache.getDataConnection();
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
    cal.setTime(p.getDay().getDate());
  }

  /** Gets a list of buttons for course modification. */
  public Vector<GemMenuButton> getMenuCours() {
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

  /** Gets a list of buttons for rehearsal. */
  public Vector<GemMenuButton> getMenuMemberRehearsal() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.hour.modification.label"), this, "ChangeHour"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.rehearsal.cancellation.label"), this, "CancelRehearsal"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.paid.annotation.label"), this, "MarkPaid"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.unpaid.annotation.label"), this, "MarkNotPaid"));

    return v;
  }

  /** Gets a list of buttons for group modification. */
  public Vector<GemMenuButton> getMenuGroupRehearsal() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.hour.modification.label"), this, "ChangeHour"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.rehearsal.cancellation.label"), this, "CancelRehearsal"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.paid.annotation.label"), this, "MarkPaid"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.unpaid.annotation.label"), this, "MarkNotPaid"));

    return v;
  }

  /** Gets a list of buttons for workshop modification. */
  public Vector<GemMenuButton> getMenuWorkshop() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();

    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.room.modification.label"), this, "ChangeRoom"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.teacher.modification.label"), this, "ChangeTeacher"));
    //v.add(new GemMenuButton("Changer de date", this, "ChangerDate"));
    v.add(new GemMenuButton(BundleUtil.getLabel("Schedule.workshop.cancellation.label"), this, "CancelWorkshop"));

    return v;
  }

  /** Gets a list of buttons for planning creation. */
  public Vector<GemMenuButton> getMenuPlanning() {
    Vector<GemMenuButton> v = new Vector<GemMenuButton>();
    /* v.add(new GemMenuButton("Marquer salle indisponible", this, "InsertSalleNonDispo")); */

    return v;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    String arg = evt.getActionCommand();

    desktop.setWaitCursor();

    if (arg.equals("ChangeRoom")) {
      dialogChangeRoom();
    } else if (arg.equals("PutOffCourse")) {
      dialogPutOffCourse();
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
    } else if (arg.equals("CancelWorkshop")) {
      dialogCancelWorkshop();
    } else if (arg.equals("CancelRehearsal")) {
      dialogCancelRehearsal();
    } else if (arg.equals("MarkPaid")) {
      try {
        service.markPaid(plan);
        desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
      } catch (Exception ex) {
        GemLogger.logException("mark rehearsal paid", ex);
      }
    } else if (arg.equals("MarkNotPaid")) {
      try {
        service.markNotPaid(plan);
        desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
      } catch (Exception ex) {
        GemLogger.logException("mark rehearsal not paid", ex);
      }
    } else if (arg.equals(GemCommand.CANCEL_CMD)) {
      desktop.removeCurrentModule();
    }
    /*
    else if (arg.equals("Replanifier")) {
      dialogDeplacerCours();
    } else if (arg.equalsIgnoreCase("Replanifier.Validation")) {
      try {
        dataCache.setAutoCommit(false);
        Action upAction = updatePlanCtrl.get();
        cal.setTime(plan.getDay().getDate());
        Action action = getActionFrom(upAction, plan, cal.get(Calendar.DAY_OF_WEEK) - 1);
        if (service.deletePlanning(action) == 0) {// aucune plage élève n'existe pour ce planning
          updatePlanCtrl.save();
          dataCache.commit();
          desktop.postEvent(new ModifPlanEvent(this, action.getDateStart(), action.getDateEnd()));
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

    desktop.setDefaultCursor();
  }

  /** Calls hour modification dialog. */
  private void dialogChangeHour() {

    ModifPlanHourDlg dlg = new ModifPlanHourDlg(desktop.getFrame(), getLabel(plan), dataCache);

    dlg.setTitle(plan.getScheduleLabel());
    dlg.setDate(cal.getTime());
    dlg.setHour(plan.getStart(), plan.getEnd());

    dlg.entry();
    if (!dlg.isValidate()) {
      return;
    }

    DateFr start = dlg.getStart();
    DateFr end = dlg.getEnd();
    Hour hStart = dlg.getNewHourStart();
    Hour hEnd = dlg.getNewHourEnd();

    try {
      Vector<ScheduleTestConflict> v = service.testHour(plan, start, end, hStart, hEnd);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits changement d'heure");
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflit((ScheduleTestConflict) v.elementAt(i));
        }
        cfd.show();
        return;
      }

      dc.setAutoCommit(false);
      changeHour(start, end, hStart, hEnd);
      if (ScheduleObject.MEMBER_SCHEDULE == plan.getType()) {
        memberService.checkSubscriptionCard(plan, hStart, hEnd);
      }
      dc.commit();
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));//XXX dlg.getDateStart/Fin

    } catch (Exception e) {
      dc.rollback();
      GemLogger.logException("Changement d'heure", e);
    } finally {
      dc.setAutoCommit(true);
    }

  }

  /** Only for members and groups rehearsals ??? */
  private void changeHour(DateFr start, DateFr end, Hour hStart, Hour hEnd) throws Exception {
    String query = "UPDATE planning SET debut = '" + hStart + "',fin='" + hEnd + "'"
            + " WHERE action=" + plan.getIdAction()
            + " AND jour >= '" + start + "' AND jour <= '" + end + "'"
            + " AND ptype=" + plan.getType()
            + " AND debut='" + plan.getStart() + "' AND fin='" + plan.getEnd() + "'"
            + " AND lieux=" + plan.getPlace() + " AND idper=" + plan.getIdPerson();
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
      dlg.entry();
      if (!dlg.isValidate()) {
        return;
      }
      a = dlg.get();
      a.setPlaces(maxPlaces(a.getPlaces(), plan.getRoom()));

      service.updateAction(a);
      dataCache.update(a);
      desktop.postEvent(new ModifPlanEvent(service, plan.getDay(), plan.getDay()));
    } catch (SQLException sqe) {
      MessagePopup.warning(null, sqe.getMessage());
    }
  }

  private short maxPlaces(short np, Room salle) {

    short s = (short) salle.getNPers();
    if (s > 0 && np > s) {
      np = s;
      MessagePopup.warning(null, MessageUtil.getMessage("max.room.place.warning", new Object[]{np}));
    }
    return np;
  }

  private void dialogChangeCourse() {
    ModifPlanCourseDlg dlg = new ModifPlanCourseDlg(desktop, plan);
    dlg.entry();
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
    dlg.setRoom(plan.getPlace());

    dlg.entry();
    if (!dlg.isValidate()) {
      return;
    }

    DateFr start = dlg.getStart();
    DateFr end = dlg.getEnd();
    int roomId = dlg.getNewRoom();

    try {
      Vector<ScheduleTestConflict> v = service.testRoom(plan, start, end, roomId);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits changement de salle");
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflit(v.elementAt(i));
        }
        cfd.show();
        return;
      }
      service.changeRoom(plan, start, end, roomId);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));//XXX dlg.getDateStart/Fin
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
    ModifPlanTeacherDlg dlg = new ModifPlanTeacherDlg(desktop, plan, substitutes);

    dlg.setDate(cal.getTime());
    dlg.set(plan);

    dlg.entry();
    if (!dlg.isValidate()) {
      return;
    }

    ScheduleObject range = dlg.getSchedule();
    DateFr start = dlg.getStart();
    DateFr end = dlg.getEnd();

    try {
      Vector<ScheduleTestConflict> v = service.testTeacher(plan, range, start, end);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits changement de prof");
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflit(v.elementAt(i));
        }
        cfd.show();
        return;
      }
      service.changeTeacher(plan, range, start, end);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));//XXX dlg.getDateStart/Fin
    } catch (PlanningException e) {
      MessagePopup.warning(null, e.getMessage());
    }
  }

  private void dialogCopyCourse() {
    DeferCourseDlg dlg = new DeferCourseDlg(desktop.getFrame(), dataCache, plan, "Schedule.course.copy.title");
    dlg.entry();
    if (!dlg.isValidate()) {
      return;
    }

    DateFr day = dlg.getNewStart();
    Hour hStart = dlg.getNewHourStart();
    Hour hEnd = dlg.getNewHourEnd();
    int s = dlg.getNewRoom();

    try {
      if (!testConflictCourse(plan, day, hStart, hEnd, s)) {
        return;
      }
      ScheduleObject np = new CourseSchedule(plan);
      np.setDay(day);
      np.setStart(hStart);
      np.setEnd(hEnd);
      np.setPlace(s);
      np.setNote(0);
      service.copyCourse(plan, np);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    } catch (PlanningException e) {
      MessagePopup.warning(null, e.getMessage());
      GemLogger.logException("Copy course", e);
    }
  }

  /**
   * Calls course time modification dialog.
   * This dialog is runned when day or hour must be changed. If not, it is
   * preferable to call the dialog for room modification.
   *
   * @version 1.1b
   */
  private void dialogPutOffCourse() {
    DeferCourseDlg dlg = new DeferCourseDlg(desktop.getFrame(), dataCache, plan, "Schedule.course.shifting.title");
    dlg.entry();
    if (!dlg.isValidate()) {
      return;
    }

    DateFr jour = dlg.getNewStart();
    Hour hdeb = dlg.getNewHourStart();
    Hour hfin = dlg.getNewHourEnd();
    int s = dlg.getNewRoom();

    try {
      if (!testConflictCourse(plan, jour, hdeb, hfin, s)) {
        return;
      }
      service.putOffCourse(plan, jour, hdeb, hfin, s);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    } catch (PlanningException e) {
      MessagePopup.warning(null, e.getMessage());
      GemLogger.logException("Put off course", e);
    }
  }

  private boolean testConflictCourse(ScheduleObject plan, DateFr date, Hour debut, Hour fin, int salle)
          throws SQLException {
    Vector<ScheduleTestConflict> v = service.testRoom(date, debut, fin, salle);
    if (v.size() > 0) {
      ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits changement de salle");
      for (int i = 0; i < v.size(); i++) {
        cfd.addConflit(v.elementAt(i));
      }
      cfd.show();
      return false;
    }

    v = service.testTeacher(plan, date, debut, fin);
    if (v.size() > 0) {
      ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflit prof occupé");
      for (int i = 0; i < v.size(); i++) {
        cfd.addConflit(v.elementAt(i));
      }
      cfd.show();
      return false;
    }
    return true;
  }

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

    ModifPlanRangeDlg dlg = new ModifPlanRangeDlg(desktop.getFrame(), "Etendre/Compresser planning", plan);
    dlg.entry();
    if (!dlg.isValidate()) {
      return;
    }

    Hour hdeb = dlg.getNewHourStart();
    Hour hfin = dlg.getNewHourEnd();
    // Le plan ne doit pas empiéter sur des cours existants
    try {
      if (plan instanceof CourseSchedule) {
        int pl = service.testRange(hdeb, hfin, plan);
        if (pl > 0) {
          MessagePopup.warning(null, MessageUtil.getMessage("time.slot.conflict.detail", new Object[]{pl}));
          return;
        }
      }

      Vector<ScheduleTestConflict> v = service.testRoom(plan, hdeb, hfin);
      if (v.size() > 0) {
        ConflictListDlg cfd = new ConflictListDlg(desktop.getFrame(), "Conflits salle");
        for (int i = 0; i < v.size(); i++) {
          cfd.addConflit(v.elementAt(i));
        }
        cfd.show();
        return;
      }
    } catch (SQLException sqe) {
      System.err.println(sqe.getMessage());
      return;
    }
    DateFr lastDay = dlg.getDateStart();
    try {
      service.modifyPlanningRange(plan, hdeb, hfin, lastDay);
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
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
    dlg.entry();
    if (dlg.isValidate()) {
      try {
        // suppression du planning
        service.deleteRehearsal(dlg.getDateStart(), dlg.getDateEnd(), plan);
        if (ScheduleObject.MEMBER_SCHEDULE == plan.getType()) {
          memberService.editSubscriptionCard(dataCache, plan);
        } else if (ScheduleObject.GROUP_SCHEDULE == plan.getType()) {
          // annulation échéance
          Group g = new GroupService(dc).find(plan.getIdPerson());
          if (g != null && g.getIdref() > 0) {
            int delay = GroupService.MIN_ANNULATION;
            boolean ok = true;
            if (!RehearsalUtil.isCancelledBefore(plan.getDay(), delay)) {
              if (!MessagePopup.confirm(null, MessageUtil.getMessage("rehearsal.payment.cancel.confirmation"), "Confirmation")) {
                ok = false;
              }
            }
            if (ok) {
              memberService.deleteOrderLine(dlg.getDateStart(), g.getIdref());
            }
          }
        }
        desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
      } catch (SQLException ex) {
        GemLogger.logException("rehearsal.delete.exception", ex);
      }
    }
  }

  /**
   * Calls a dialog for planning suppression.
   *
   * @since 1.1e
   */
  private void dialogPlanningSuppression() {
    SupprPlanningDlg dlg = new SupprPlanningDlg(desktop.getFrame(), plan);
    dlg.entry();

    if (dlg.isValidate()) {
      Action action = new Action();
      action.setId(plan.getIdAction());
      action.setDateStart(dlg.getDateStart());
      action.setDateEnd(dlg.getDateEnd());
      try {
        deletePlanning(action);
      } catch (PlanningException ex) {
        MessagePopup.warning(null, MessageUtil.getMessage("delete.error") + " :\n" + ex.getMessage());
        GemLogger.logException(ex);
      }

    }
  }

  private void deletePlanning(Action action) throws PlanningException {
    int n = service.deletePlanning(action);
    if (n > 0) {
      String v = MessageUtil.getMessage("range.delete.warning", n);
      String plages = (n == 1) ? v : MessageUtil.getMessage("ranges.delete.warning", n);
      MessagePopup.information(desktop.getFrame(), MessageUtil.getMessage("delete.exception") + plages);
    } else {
      desktop.postEvent(new ModifPlanEvent(this, plan.getDay(), plan.getDay()));
    }

  }

  private Action getActionFrom(Action a, ScheduleObject plan, int jour) {
    Action action = new Action();
    action.setDay(jour);
    action.setCourse(plan.getIdAction());
    action.setTeacher(plan.getIdPerson());
    action.setDateStart(a.getDateStart());
    action.setDateEnd(a.getDateEnd());
    action.setHourStart(plan.getStart());
    action.setHourEnd(plan.getEnd());
    action.setRoom(plan.getPlace());
    return action;
  }

  private String getLabel(ScheduleObject plan) {
    switch (plan.getType()) {
      case ScheduleObject.MEMBER_SCHEDULE:
        return BundleUtil.getLabel("Schedule.person.modification.label");
      case ScheduleObject.GROUP_SCHEDULE:
        return BundleUtil.getLabel("Schedule.group.modification.label");
      default:
        return BundleUtil.getLabel("Schedule.default.modification.label");
    }
  }
}
