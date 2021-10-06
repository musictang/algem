/*
 * @(#)MemberEnrolmentEditor.java 2.16.0 05/03/19
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
package net.algem.enrolment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import net.algem.accounting.NullAccountException;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.GemParam;
import net.algem.contact.PersonFile;
import net.algem.contact.member.MemberService;
import net.algem.course.Module;
import net.algem.course.*;
import net.algem.planning.CourseSchedule;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleRangeObject;
import net.algem.planning.editing.ChangeHourCourseDlg;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.StopCourseAbstractDlg;
import net.algem.planning.editing.StopCourseDlg;
import net.algem.planning.editing.StopCourseFromModuleDlg;
import net.algem.planning.editing.StopCourseView;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.menu.MenuPopupListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Enrolment editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.16.0
 * @since 1.0b 06/09/2001
 */
public class MemberEnrolmentEditor
  extends FileTab
  implements ActionListener {

  private final static String ENROLMENT_DATE_CHANGE = BundleUtil.getLabel("Enrolment.date.change.label");
  private final static String STOP = BundleUtil.getLabel("Course.stop.label");
  private final static String COURSE_MODIF = BundleUtil.getLabel("Course.define.label");
  private final static String REDEFINE = BundleUtil.getLabel("Course.stop.and.define.label");
  private final static String HOUR_MODIF = BundleUtil.getLabel("Course.hour.modification.label");
  private final static String COURSE_ADD = BundleUtil.getLabel("New.course.label");
  private final static String MODULE_ADD = BundleUtil.getLabel("New.module.label");
  private final static String MODULE_DEL = BundleUtil.getLabel("Module.delete.label");
  private final static String MODULE_STOP = BundleUtil.getLabel("Module.stop.label");
  private final static String MODULE_TIME_CHANGE = BundleUtil.getLabel("Module.time.change.label");
  private final static String MODULE_DATE_CHANGE = BundleUtil.getLabel("Module.date.change.label");
  private final static String NONE_ENROLMENT = MessageUtil.getMessage("enrolment.empty.list");
  private final static String COURSE_DATE = BundleUtil.getLabel("Course.date.modification.label");
  private final static String PRINT_ORDER = BundleUtil.getLabel("Order.print.detail.label");

  private PersonFile dossier;
  private DefaultMutableTreeNode root;
  private JPopupMenu popup;
  private JTree tree;
  private DefaultTreeModel model;
  private JScrollPane view;
  private GemLabel title;
  private boolean loaded;
  private CourseEnrolmentDlg courseDlg;
  private JMenuItem m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12;
  /** New enrolment button. */
  private GemButton btEnrolment;
  private TreePath currentSelection;
  private ActionListener acListener;
  private EnrolmentService service;
  private ModuleDlg moduleDlg;
  private DefaultTreeCellRenderer cellRenderer;

  public MemberEnrolmentEditor(GemDesktop desktop, ActionListener listener, PersonFile dossier) {

    super(desktop);
    this.dossier = dossier;
    acListener = listener;
    this.service = new EnrolmentService(desktop.getDataCache());

    title = new GemLabel(BundleUtil.getLabel("Member.enrolment.label"));

    popup = new JPopupMenu();
    popup.add(m0 = new JMenuItem(ENROLMENT_DATE_CHANGE));
    popup.add(m10 = new JMenuItem(PRINT_ORDER));
    popup.addSeparator();
    popup.add(m5 = new JMenuItem(MODULE_ADD));
    popup.add(m6 = new JMenuItem(MODULE_DEL));
    popup.add(m8 = new JMenuItem(MODULE_STOP));
    popup.add(m9 = new JMenuItem(MODULE_TIME_CHANGE));
    popup.add(m11 = new JMenuItem(MODULE_DATE_CHANGE));
    popup.addSeparator();
    popup.add(m1 = new JMenuItem(STOP));
    popup.add(m2 = new JMenuItem(COURSE_MODIF));
    popup.add(m12 = new JMenuItem(REDEFINE));
    popup.add(m3 = new JMenuItem(HOUR_MODIF));
    popup.add(m7 = new JMenuItem(COURSE_DATE));
    popup.add(m4 = new JMenuItem(COURSE_ADD));


    m0.addActionListener(this);
    m1.addActionListener(this);
    m2.addActionListener(this);
    m3.addActionListener(this);
    m4.addActionListener(this);
    m5.addActionListener(this);
    m6.addActionListener(this);
    m7.addActionListener(this);
    m8.addActionListener(this);
    m9.addActionListener(this);
    m10.addActionListener(this);
    m11.addActionListener(this);
    m12.addActionListener(this);

    cellRenderer = new EnrolmentTreeCellRenderer();
    tree = new JTree(new DefaultMutableTreeNode(NONE_ENROLMENT));
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        currentSelection = e.getNewLeadSelectionPath();
      }
    });

    tree.addMouseListener(new MenuPopupListener(tree, popup) {

      @Override
      public void maybeShowPopup(MouseEvent e) {

        if (currentSelection != null) {
          Object node = currentSelection.getLastPathComponent();
          if (node.getClass() == DefaultMutableTreeNode.class) {
            return;
          }
          if (node.getClass() == ModuleEnrolmentNode.class) {
            setModulePopupMenu();
          } else if (node instanceof CourseEnrolmentNode) {
            setCoursePopupMenu(true);
          } else {
            setCoursePopupMenu(false);
          }
          super.maybeShowPopup(e);
        }
      }
    });

    model = (DefaultTreeModel) tree.getModel();
    view = new JScrollPane(tree);
    btEnrolment = new GemButton(BundleUtil.getLabel("New.enrolment.label"));
    btEnrolment.setActionCommand("MemberEnrolmentCreate");
    btEnrolment.addActionListener(acListener);

    setLayout(new BorderLayout());
    add(title, BorderLayout.NORTH);
    add(view, BorderLayout.CENTER);
    add(btEnrolment, BorderLayout.SOUTH);
  }

  /**
   * Enable or disable some menus depending on the state of {@literal e}.
   *
   * @param e true or false
   */
  private void setCoursePopupMenu(boolean e) {
    if (e) {
      m1.setEnabled(true);
      m2.setEnabled(true);
      m3.setEnabled(true);
      m4.setEnabled(false);
      m5.setEnabled(false);
      m6.setEnabled(false);
      m7.setEnabled(true);
      m8.setEnabled(false);
      m9.setEnabled(false);
      m10.setEnabled(false);
      m11.setEnabled(false);
      m12.setEnabled(true);
    } else {
      m0.setEnabled(true);
      m1.setEnabled(false);
      m2.setEnabled(false);
      m3.setEnabled(false);
      m4.setEnabled(false);
      m5.setEnabled(true);
      m6.setEnabled(false);
      m7.setEnabled(false);
      m8.setEnabled(false);
      m9.setEnabled(false);
      m10.setEnabled(true);
      m11.setEnabled(false);
      m12.setEnabled(false);
    }
  }

  /**
   * Sets the popup menu to suit module stopping.
   */
  private void setModulePopupMenu() {
    m0.setEnabled(false);
    m1.setEnabled(false);
    m2.setEnabled(false);
    m3.setEnabled(false);
    m4.setEnabled(true);
    m5.setEnabled(false);
    m6.setEnabled(true);
    m7.setEnabled(false);
    m8.setEnabled(true);
    m9.setEnabled(true);
    m10.setEnabled(false);
    m11.setEnabled(true);
    m12.setEnabled(false);
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    desktop.setWaitCursor();
    loaded = true;
    final List<Enrolment> ins = service.getEnrolments(dossier.getId());
    if (ins == null || ins.size() < 1) {
      root = new DefaultMutableTreeNode(NONE_ENROLMENT);
      model.setRoot(root);
      desktop.setDefaultCursor();
      return;
    }

    root = new DefaultMutableTreeNode(BundleUtil.getLabel("Person.enrolment.tab.label") + " : " + dossier.getContact().getFirstnameName());
    for (int j = 0; j < ins.size(); j++) {
      Enrolment i = ins.get(j);
      EnrolmentNode ni = new EnrolmentNode(i);

      Enumeration<ModuleOrder> enu = i.getModule().elements();
      while (enu.hasMoreElements()) {
        try {
          ModuleOrder mo = enu.nextElement();//probleme apres inscription
          ModuleEnrolmentNode mnode = new ModuleEnrolmentNode(mo);
          // line below is commented : not needed
          //mnode.setLastScheduledDate(new DateFr(service.getLastScheduleByModuleOrder(dossier.getId(), mo.getId())));
          if (mo.getTotalTime() > 0) {
            // do not restrict to end date
            mnode.setCompleted(service.getCompletedTime(dossier.getId(), mo.getId(), mo.getStart().getDate()));
//            mnode.setCompleted(service.getCompletedTime(dossier.getId(), mo.getId(), dataCache.getStartOfYear().getDate()));//XXX problème au changement d'année
          }

          if (mo.isStopped()) {
            mnode.setInfo(" <font color=\"#666666\">" + BundleUtil.getLabel("Module.stopped.label") + " : " + mo.getEnd().toString() + "</font>");
          }
          Vector<CourseOrder> courseOrders = service.getCourseOrder(i.getId(), mo.getId());
          for (int k = 0; k < courseOrders.size(); k++) {
            CourseOrder cc = courseOrders.elementAt(k);
            int jj = service.getCourseDayMember(cc.getAction(), cc.getDateStart(), i.getMember());
            //auto update of end date
            DateFr last = new DateFr(service.getLastSchedule(dossier.getId(), cc.getId()));
            // do not update if end date before last date (end date may be changed when course order is stopped by the end of year)
            if (!DateFr.NULLDATE.equals(last.toString()) && !last.afterOrEqual(cc.getDateEnd())) {
              cc.setDateEnd(last);
              //service.update(cc); // no need to update (displayed in real time)
            }
            //
            if (cc.getTitle() == null && cc.getAction() == 0) {
              cc.setTitle(getUndefinedLabel(cc));
            }
            CourseEnrolmentNode nci = new CourseEnrolmentNode(cc, jj);
            mnode.add(nci);
          }
          ni.add(mnode);
        } catch (SQLException ex) {
          GemLogger.logException(getClass().getName() + "#load.run", ex);
        }
      }
//      renderer.setFont(renderer.getFont().deriveFont(Font.ITALIC));
      root.add(ni);

    }
    model.setRoot(root);
    expand();
    desktop.setDefaultCursor();
  }

  public static String getUndefinedLabel(CourseOrder cc) throws SQLException {
    StringBuilder t = new StringBuilder("[");
    GemParam p = (GemParam) DataCache.findId(cc.getCode(), Model.CourseCode);
    t.append(p == null ? "!" : p.getLabel());
    t.append(" ");
    t.append(BundleUtil.getLabel("To.define.label"));
    t.append("]");

    return t.toString();
  }

  @Override
  public void setEnabled(boolean b) {
    btEnrolment.setEnabled(b);
  }

  @Override
  public String toString() {
    int id = dossier == null ? 0 : dossier.getId();
    return getClass().getSimpleName() + " : " + id;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String s = evt.getActionCommand();
    if (ENROLMENT_DATE_CHANGE.equals(s)) {
      changeDateOfEnrolment();
    } else if (STOP.equals(s)) {
      if (currentSelection == null) {
        return;
      }
      stopCourse();
    } else if (COURSE_MODIF.equals(s)) {
      if (currentSelection == null) {
        return;
      }
      modifCourse();
    } else if (REDEFINE.equals(s)) {
      if (currentSelection == null) {
        return;
      }
      stopAndDefine();
    } else if (COURSE_DATE.equals(s)) {
      if (currentSelection == null) {
        return;
      }
      changeDateOfCourseOrder();
    } else if (MODULE_STOP.equals(s)) {
      if (currentSelection == null) {
        return;
      }
      stopModule();
    } else if (HOUR_MODIF.equals(s)) {
      if (currentSelection == null) {
        return;
      }
      changeHour();
    } else if (COURSE_ADD.equals(s)) {
      addCourse();
    } else if (MODULE_ADD.equals(s)) {
      addModule();
    } else if (MODULE_DEL.equals(s)) {
      deleteModuleOrder();
    } else if (MODULE_TIME_CHANGE.equals(s)) {
      changeModuleTime();
    } else if (MODULE_DATE_CHANGE.equals(s)) {
      changeModuleDate();
    } else if (PRINT_ORDER.equals(s)) {
      printOrder();
    }

  }

  private void changeHour() {

    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseOrder co = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();

    if (co.getStart().equals(new Hour(Hour.NULL_HOUR))) {
      return;
    }

    ChangeHourCourseDlg dlg2;
    try {
      Course c = service.getCourse(co.getAction());
      if (c == null || c.isCollective()) {
        MessagePopup.warning(this, MessageUtil.getMessage("member.enrolment.hour.modif.warning"));
        return;
      }
      dlg2 = new ChangeHourCourseDlg(desktop, service, co, dossier.getId());
      dlg2.init();
      dlg2.setVisible(true);
    } catch (SQLException ex) {
      GemLogger.log(MemberEnrolmentEditor.class.getName(), "#changeHour", ex);
    }

  }

  private void addCourse() {
    Object[] path;
    try {
      path = currentSelection.getPath();
    } catch (NullPointerException npe) {
      return;
    }
    int i = path.length;
    if (!(path[i - 1] instanceof ModuleEnrolmentNode)) {
      return;
    }
    ModuleOrder mo = ((ModuleEnrolmentNode) path[i - 1]).getModule();

    CourseInfoDlg dlg = new CourseInfoDlg(desktop, true, BundleUtil.getLabel("Course.add.label"), null);
    if (!dlg.isValidation()) {
      return;
    }
    try {
      CourseModuleInfo cm = dlg.getCourseInfo();
      if (dlg.getDate() != null) {
        cm.setDate(dlg.getDate());
      }
      CourseOrder co = createCourse(mo, cm);
      if (courseDlg == null) {
        courseDlg = new CourseEnrolmentDlg(desktop, service, dossier.getId());
      }
      courseDlg.clear();
      courseDlg.setCourseInfo(cm);
      courseDlg.loadEnrolment(co);

      view.setCursor(Cursor.getDefaultCursor());
      courseDlg.entry();
      if (courseDlg.isValidation()) {
        if (!MessagePopup.confirm(view,
          MessageUtil.getMessage("enrolment.update.confirmation", co.getDateStart()),
          BundleUtil.getLabel("Confirmation.title"))) {
          return;
        }
        modifyCourseOrder(co, courseDlg);
        service.createCourse(co, dossier.getId());
        desktop.postEvent(new ModifPlanEvent(this, co.getDateStart(), co.getDateEnd()));
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      }
    } catch (EnrolmentException ex) {
      MessagePopup.warning(this, ex.getMessage());
    } catch (SQLException sqe) {
      MessagePopup.warning(this, getClass().getName() + "#addCourse :\n" + sqe.getMessage());
    }
  }

  private CourseOrder createCourse(ModuleOrder mo, CourseModuleInfo cm) {
    CourseOrder co = new CourseOrder();
    co.setIdOrder(mo.getIdOrder());
    co.setModuleOrder(mo.getId());
    co.setDateStart(cm.getDate() == null ? new DateFr(new Date()) : cm.getDate());
    co.setDateEnd(mo.getEnd());
    co.setStart(new Hour("00:00"));
    co.setEnd(new Hour(cm.getTimeLength()));
    co.setCourseModuleInfo(cm);
    co.setCode(cm.getIdCode());// important !

    return co;
  }

  private void stopCourse() {

    StopCourseDlg stopDlg = null;
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    if (cc.getAction() == 0) {
      if (MessagePopup.confirm(this, MessageUtil.getMessage("course.suppression.confirmation"))) {
        service.stopCourse(cc.getId());
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      }
      return;
    }
    try {
      Course c = planningService.getCourseFromAction(cc.getAction());
      if (c == null || c.isUndefined()) {
        MessagePopup.information(this, MessageUtil.getMessage("course.invalid.choice"));
        return;
      }

      stopDlg = new StopCourseDlg(desktop, dossier.getId(), cc, c, service);
      stopDlg.setVisible(true);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "#stopCourse :", ex.getMessage());
    }
  }

  /**
   * Stops a course order and redefine it in one step.
   */
  private void stopAndDefine() {
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseModuleInfo cmi = new CourseModuleInfo();
    GemParam code = null;
    try {
      final CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
      if (cc.getAction() == 0) {
        MessagePopup.warning(this, MessageUtil.getMessage("course.invalid.choice"));
        return;
      }
      if (cc.getDateEnd().before(dataCache.getStartOfYear())) {
        MessagePopup.warning(this, MessageUtil.getMessage("date.out.of.period"));
        return;
      }

      final Course c = planningService.getCourseFromAction(cc.getAction());
      if (!c.isCollective() || CourseCodeType.ATP.getId() == cc.getCode()) {
        MessagePopup.warning(this, MessageUtil.getMessage("member.enrolment.stop.and.redefine.warning"));
        return;
      }
      code = (GemParam) DataCache.findId(cc.getCode(), Model.CourseCode);
      cmi.setCode(code);
      cmi.setTimeLength(cc.getTimeLength());

      StopCourseDateDlg stopDlg = new StopCourseDateDlg(desktop.getFrame(), REDEFINE, c.toString(), true);
      final DateFr now = stopDlg.getDate();
      if (now == null) {
        return;
      }
      List<CourseSchedule> all = service.getSchedules(cmi, now, cc.getAction(), 0);
      StopAndDefineDlg dlg = new StopAndDefineDlg(desktop.getFrame(), true);
      dlg.createUI(all);
      if (!dlg.isValidation()) {
        return;
      }
      final CourseSchedule selected = dlg.getCourse();
      //arreter cours
      /*final DateFr s = new DateFr(now);
      while (Calendar.MONDAY != s.getDayOfWeek()) {
        s.incDay(1);
      }*/
      final CourseOrder co = new CourseOrder();
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          service.stopCourse(dossier.getId(), cc, c, now, false);
          //new course order
          co.setIdOrder(cc.getIdOrder());
          co.setModuleOrder(cc.getModuleOrder());
          co.setAction(selected.getAction().getId());
          co.setStart(selected.getStart());
          co.setEnd(selected.getEnd());
          co.setDateStart(selected.getDate());// date du premier planning
          co.setDateEnd(dataCache.getEndOfYear());
          co.setCode(cc.getCode());
          System.out.println(co);
          service.createCourse(co, dossier.getId());

          return null;
        }
      });
      desktop.postEvent(new ModifPlanEvent(this, now, co.getDateEnd()));
      desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } catch (Exception ex) {
      GemLogger.logException(ex);
    }
  }

  private void changeDateOfCourseOrder() {
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    if (!cc.getStart().equals(new Hour("00-00-00"))) {
      MessagePopup.error(this, MessageUtil.getMessage("course.invalid.choice"));
      return;
    }
    DateFr oldDate = cc.getDateStart();
    CourseInfoDlg dlg = new CourseInfoDlg(desktop, true, BundleUtil.getLabel("Course.date.modification.label"), oldDate);

    if (dlg.getDate() != null && !dlg.getDate().equals(oldDate)) {
      cc.setDateStart(dlg.getDate());
      try {
        service.update(cc);
      } catch (SQLException e) {
        GemLogger.log(e.getMessage());
      }
    }
  }

  private void changeDateOfEnrolment() {
    System.out.println("Modification date d'inscription");
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof EnrolmentNode)) {
      return;
    }
    EnrolmentNode node = (EnrolmentNode) path[i - 1];
    Order order = node.getOrder();

    ChangeEnrolmentDateDlg dlg = new ChangeEnrolmentDateDlg(desktop, BundleUtil.getLabel("Enrolment.date.change.label"), true);
    dlg.initUI(order);
    dlg.setVisible(true);
    if (dlg.isValidation()) {
      order.setCreation(dlg.getDate());
      try {
        service.updateOrderDate(order);
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      } catch (SQLException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }
    }

  }

  private void modifCourse() {
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }

    CourseOrder co = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    if (!co.getStart().equals(new Hour("00-00-00"))) {
      MessagePopup.error(this, MessageUtil.getMessage("course.invalid.choice"));
      return;
    }

    view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    if (courseDlg == null) {
      courseDlg = new CourseEnrolmentDlg(desktop, service, dossier.getId());
    }
    CourseModuleInfo cmi = new CourseModuleInfo();
    GemParam code = null;
    try {
      code = (GemParam) DataCache.findId(co.getCode(), Model.CourseCode);
      cmi.setCode(code);
      cmi.setTimeLength(co.getTimeLength());
      courseDlg.setCourseInfo(cmi);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    courseDlg.clear();
    courseDlg.setCode(co.getCode());
    try {
      courseDlg.loadEnrolment(co);
      courseDlg.entry();
      if (courseDlg.isValidation()) {
        if (!MessagePopup.confirm(desktop.getFrame(),
          MessageUtil.getMessage("enrolment.update.confirmation", co.getDateStart()))) {
          view.setCursor(Cursor.getDefaultCursor());
          return;
        }
        modifyCourseOrder(co, courseDlg);
        service.modifyCourse(co, dossier.getId());
        desktop.postEvent(new ModifPlanEvent(this, co.getDateStart(), co.getDateEnd()));
        // Rafraichissement de la vue inscription
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      }
    } catch (EnrolmentException e) {
      desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));// possibly refresh view
      MessagePopup.warning(view, e.getMessage());
    } finally {
      view.setCursor(Cursor.getDefaultCursor());
    }

  }

  private void modifyCourseOrder(CourseOrder co, CourseEnrolmentDlg dlg) {
    co.setModuleOrder(Integer.parseInt(dlg.getField(1)));
    co.setAction(Integer.parseInt(dlg.getField(2)));
    co.setTitle(dlg.getField(3));
    co.setDay(Integer.parseInt(dlg.getField(4)));

    if (CourseCodeType.ATP.getId() == co.getCode()) {
      DateFr d = new DateFr(dlg.getField(7));
      co.setDateStart(d);
      co.setDateEnd(d);
    }
    Hour start = new Hour(dlg.getField(5));
    Hour length = new Hour(dlg.getField(6));
    co.setStart(start);
    co.setEnd(start.end(length.toMinutes()));
  }

  /**
   * Adds a module.
   */
  public void addModule() {
    EnrolmentOrderUtil orderUtil = new EnrolmentOrderUtil(dossier, dc);
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if ((path[i - 1] instanceof ModuleEnrolmentNode) || (path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }

    Order order = ((EnrolmentNode) path[i - 1]).getOrder();

    try {
      if (moduleDlg == null) {
        moduleDlg = new ModuleDlg(this, dossier, service, dataCache);
      } else {
        moduleDlg.reset();
      }
      moduleDlg.show();
      if (!moduleDlg.isValidation()) {
        return;
      }
      int idModule = (Integer) moduleDlg.getField(0);

      ModuleOrder mo = new ModuleOrder();
      mo.setIdOrder(order.getId());

      Module m = ((ModuleIO) DataCache.getDao(Model.Module)).findId(idModule);
      addModule(mo, m);
      service.create(mo);

      for (CourseModuleInfo info : m.getCourses()) {
        addCourse(mo, info);
      }
      orderUtil.setTotalOrderLine(mo.getPaymentAmount());

      String school = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
      try {
        int n = orderUtil.saveOrderLines(mo, Integer.parseInt(school), false);
        orderUtil.updateModuleOrder(n, mo);
      } catch (NullAccountException ex) {
        GemLogger.logException(ex);
      }
      desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
    } catch (SQLException ex) {
      MessagePopup.warning(this, "#addModule " + ex.getMessage());
    }
  }// end addModule

  private void addCourse(ModuleOrder mo, CourseModuleInfo cm) throws SQLException {
    CourseOrder co = new CourseOrder();
    co.setIdOrder(mo.getIdOrder());
    co.setTitle(cm.getCode().getLabel());
    co.setDay(0);//dimanche
    co.setModuleOrder(mo.getId());
    co.setStart(new Hour("00:00"));// XXX "00:00" ??
    co.setEnd(new Hour(cm.getTimeLength()));
    co.setCourseModuleInfo(cm);
    co.setDateStart(mo.getStart());
    co.setDateEnd(mo.getEnd());
//    co.setRoom(0);// salle à définir

    service.create(co);
  }

  /**
   * Adds a module.
   *
   * @param mo module order
   * @param m module
   */
  private void addModule(ModuleOrder mo, Module m) {

    mo.setTitle(m.getTitle());
    mo.setPayer(dossier.getMember().getPayer());
    mo.setModule(m.getId());
    mo.setSelectedModule((Integer) moduleDlg.getField(7));
    mo.setStart(new DateFr((DateFr) moduleDlg.getField(2)));
    mo.setEnd(new DateFr((DateFr) moduleDlg.getField(3)));
    mo.setPrice(((Number) moduleDlg.getField(4)).doubleValue());
    mo.setPaymentAmount(((Number) moduleDlg.getField(10)).doubleValue());
    mo.setModeOfPayment((String) moduleDlg.getField(5));
    mo.setPayment((PayFrequency) moduleDlg.getField(6));
    mo.setPricing((PricingPeriod) moduleDlg.getField(9));
    if (PricingPeriod.HOUR.equals(mo.getPricing())) {
      mo.setTotalTime(Hour.decimalToMinutes((Double) moduleDlg.getField(8)));
    } else {
      mo.setTotalTime(0);
    }

    mo.setNOrderLines(1);

  }

  private void deleteModuleOrder() {
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof ModuleEnrolmentNode)) {
      return;
    }

    ModuleOrder mo = ((ModuleEnrolmentNode) path[i - 1]).getModule();
    if (MessagePopup.confirm(this, MessageUtil.getMessage("module.delete.confirmation", mo.getTitle()))) {
      try {
        service.delete(mo, dossier.getId());
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      } catch (EnrolmentException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }
    }
  }

  private void stopModule() {

    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof ModuleEnrolmentNode)) {
      return;
    }
    ModuleEnrolmentNode node = ((ModuleEnrolmentNode) path[i - 1]);
    List<CourseOrder> orders = new ArrayList<CourseOrder>();
    for (Enumeration e = node.children(); e.hasMoreElements();) {
      TreeNode n = (TreeNode) e.nextElement();
      if (n instanceof CourseEnrolmentNode) {
        orders.add(((CourseEnrolmentNode) n).getCourseOrder());
      }
    }

    ModuleOrder moduleOrder = node.getModule();

    StopCourseFromModuleDlg dlg2 = new StopCourseFromModuleDlg(desktop, moduleOrder);
    dlg2.setVisible(true);
    if (!dlg2.isValidation()) {
      return;
    }
    DateFr stop = dlg2.getEndDate();
    DateFr origEnd = new DateFr(moduleOrder.getEnd()); // backup original end date
    boolean hasStopped = moduleOrder.isStopped(); // backup stopped status
    moduleOrder.setEnd(stop);
    moduleOrder.setStopped(true);
    try {
      service.stopModule(moduleOrder, orders, dossier.getId(), stop);
      desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      desktop.postEvent(new ModifPlanEvent(this, stop, dataCache.getEndOfYear()));
      //TODO service.pauseModule(moduleOrder);
    } catch (EnrolmentException ex) {
      moduleOrder.setEnd(origEnd);
      moduleOrder.setStopped(hasStopped);
      MessagePopup.warning(this, ex.getMessage());
    }
  }

  /**
   * Changes the time length of the selected module.
   * This modification is only valid if the pricing period is the hour.
   */
  private void changeModuleTime() {
    Object[] path = currentSelection.getPath();
    if (!isModuleNode(path)) {
      return;
    }
    ModuleEnrolmentNode node = ((ModuleEnrolmentNode) path[path.length - 1]);
    ModuleOrder mo = node.getModule();
    if (!PricingPeriod.HOUR.equals(mo.getPricing())) {
      MessagePopup.warning(this, MessageUtil.getMessage("module.time.change.warning"));
      return;
    }
    ChangeModuleTimeDlg dlg = new ChangeModuleTimeDlg(desktop, BundleUtil.getLabel("Module.time.change.label"));
    int oldTime = mo.getTotalTime();
    dlg.set(mo.getTotalTime());
    dlg.setVisible(true);
    if (dlg.isValidation()) {
      mo.setTotalTime(dlg.get());
      try {
        service.update(mo);
      } catch (SQLException ex) {
        mo.setTotalTime(oldTime);
        GemLogger.log(ex.getMessage());
        MessagePopup.warning(this, ex.getMessage());
      }
    }
  }

  /**
   * Changes the start date and/or the end date of the selected module.
   */
  private void changeModuleDate() {
    Object[] path = currentSelection.getPath();
    if (!isModuleNode(path)) {
      return;
    }
    ModuleEnrolmentNode node = ((ModuleEnrolmentNode) path[path.length - 1]);
    ModuleOrder mo = node.getModule();
    ChangeModuleDateDlg dlg = new ChangeModuleDateDlg(desktop, BundleUtil.getLabel("Module.date.change.label"), true);
    dlg.initUI(mo);
    dlg.setVisible(true);
    if (dlg.isValidation()) {
      mo.setStart(dlg.getRange().getStart());
      mo.setEnd(dlg.getRange().getEnd());
      try {
        service.update(mo);
      } catch (SQLException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }
    }
  }

  /**
   * Prints or browse selected enrolment information (including the list of activities).
   */
  private void printOrder() {
    PrintWriter pw = null;
    File temp = null;
    String extension = ".html";
    try {
      Object[] path = currentSelection.getPath();
      int i = path.length;
      if ((path[i - 1] instanceof ModuleEnrolmentNode) || (path[i - 1] instanceof CourseEnrolmentNode)) {
        return;
      }
      desktop.setWaitCursor();
      EnrolmentNode node = (EnrolmentNode) path[i - 1];
      Order order = node.getOrder();

      if (node.getChildCount() >= 0) {
        MemberService memberService = new MemberService(dc);
        temp = File.createTempFile(BundleUtil.getLabel("Enrolment.label") + "-" + order.getId() + "_", extension);
        pw = new PrintWriter(temp, StandardCharsets.UTF_8.name());
        pw.println(FileUtil.getHtmlHeader(BundleUtil.getLabel("Enrolment.label"), getCss()));
        pw.println(catchEnrolmentInfo(node));
        //pw.println(catchActivity(order.getCreation(), dataCache.getEndOfYear()));//XXX probleme date fin si cours programmés après
        List<ScheduleRangeObject> ranges = getActivityRanges(dossier.getId(), order.getCreation(), null, getActions(node), memberService);
        pw.println(fillActivityFull(ranges));
//        pw.println(catchActivity(dossier.getId(), order.getCreation(), null, getActions(node), memberService));
        pw.println("</body></html>");
      }
      if (pw != null) {
        //FileUtil.printFile(temp, DocFlavor.INPUT_STREAM.AUTOSENSE);//XXX prints in plain text only
        try {
          if (temp != null) {
            DesktopBrowseHandler browser = new DesktopBrowseHandler();
            browser.browse(temp.toURI().toString());
          }
        } catch (DesktopHandlerException de) {
          GemLogger.log(de.getMessage());
        } 
      }

    } catch (FileNotFoundException ex) {
      GemLogger.log(ex.getMessage());
      MessagePopup.error(this, MessageUtil.getMessage("file.not.found.exception") + " :\n" + ex.getMessage());
    } catch (IOException ex) {
      GemLogger.log(ex.getMessage());
    } finally {
      pw.close();
      desktop.setDefaultCursor();
    }
  }

  /**
   * Get the list of actions registered for this order command.
   *
   * @param node enrolment node order
   * @return a comma-separated list of actions
   */
  private String getActions(TreeNode node) {
    List<Integer> actions = new ArrayList<>();
    for (Enumeration e = node.children(); e.hasMoreElements();) {
      TreeNode n = (TreeNode) e.nextElement();
      if (n instanceof ModuleEnrolmentNode) {
        for (Enumeration c = n.children(); c.hasMoreElements();) {
          TreeNode cn = (TreeNode) c.nextElement();
          if (cn instanceof CourseEnrolmentNode) {
            actions.add(((CourseEnrolmentNode) cn).getCourseOrder().getAction());
          }
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    if (actions.isEmpty()) {
      sb.append("-1");
      return sb.toString();
    }
    for (int a : actions) {
      sb.append(a).append(',');
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   * Returns the list of activities performed by member from {@literal start}
   * and corresponding to this list of {@code actions}.
   *
   * @param idper
   * @param start starting date
   * @param end
   * @param actions a comma-separated list of actions' id
   * @param service
   * @return a html-formatted string
   * @deprecated
   */
  public static String catchActivity(int idper, DateFr start, DateFr end, String actions, MemberService service) {
    try {
      StringBuilder sb = new StringBuilder();
      Vector<ScheduleRangeObject> ranges;
      if (end == null) {
        ranges = service.findFollowUp(idper, start.getDate(), actions);
      } else {
        ranges = service.findFollowUp(idper, start.getDate(), end.getDate(), actions);
      }

      /*sb.append("<table><thead>");
      sb.append("<tr><th>").append(BundleUtil.getLabel("Activity.label")).append("</th><th>")
              .append(BundleUtil.getLabel("Teacher.label")).append("</th><th>")
              .append(BundleUtil.getLabel("Room.label")).append("</th><th>")
              .append(BundleUtil.getLabel("Follow.up.label")).append("</th><th>") // individual follow up
              .append(BundleUtil.getLabel("Date.label")).append("</th><th>")
              .append(BundleUtil.getLabel("Start.label")).append("</th><th>")
              .append(BundleUtil.getLabel("End.label")).append("</th><th>")
              .append(BundleUtil.getLabel("Duration.label")).append("</th></tr></thead><tbody>");
      int min = 0;
      for (ScheduleRangeObject r : ranges) {
        Hour hs = r.getStart();
        Hour he = r.getEnd();
        min += hs.getLength(he);
        String note = r.getFollowUp() == null ? "" : r.getFollowUp().getContent();
        sb.append("<tr><td>")
                .append(r.getActivity()).append("</td><td>")
                .append(r.getTeacher().getFirstnameName()).append("</td><td>")
                .append(r.getRoom().getName()).append("</td><td>")
                .append(note == null ? "" : note).append("</td><td>")
                .append(r.getDate()).append("</td><td>")
                .append(r.getStart()).append("</td><td>")
                .append(r.getEnd()).append("</td><td>")
                .append(Hour.format(hs.getLength(he))).append("</td></tr>");
      }
      sb.append("</tbody><tfoot><tr><td colspan=\"7\">Total</td><td> ").append(Hour.format(min)).append("</td></tr>");
      sb.append("</tfoot></table>");

      return sb.toString();
       */
      return fillActivityAMPM(ranges, new String[]{"14:00", ""});
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      return "";
    }
  }

  public static List<ScheduleRangeObject> getActivityRanges(int idper, DateFr start, DateFr end, String actions, MemberService service) {
    List<ScheduleRangeObject> ranges = new Vector<>();
    try {
      if (end == null) {
        ranges = service.findFollowUp(idper, start.getDate(), actions);
      } else {
        ranges = service.findFollowUp(idper, start.getDate(), end.getDate(), actions);
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
    return ranges;
  }

  /**
   * Complete the activity of the student {@code idper} with the list of his rehearsals.
   *
   * @param idper member's id
   * @param start start date
   * @param end end date
   * @param service member service
   * @param individual if true, include individual rehearsals
   * @param group if true, include group rehearsals
   * @return a list of ScheduleRangeObject
   */
  public static List<ScheduleRangeObject> completeActivityRanges(int idper, DateFr start, DateFr end, MemberService service, boolean individual, boolean group) {
    List<ScheduleRangeObject> ranges = new ArrayList<>();
    try {
      List<ScheduleRangeObject> memberRanges = service.getMemberRehearsals(idper, start.getDate(), end.getDate(), individual, group);
      ranges.addAll(memberRanges);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return ranges;

  }

  public static String fillActivityFull(List<ScheduleRangeObject> ranges) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table><thead>");
    sb.append("<tr><th>").append(BundleUtil.getLabel("Activity.label")).append("</th><th>")
      .append(BundleUtil.getLabel("Teacher.label")).append("</th><th>")
      .append(BundleUtil.getLabel("Room.label")).append("</th><th>")
      .append(BundleUtil.getLabel("Signature.label")).append("</th><th>") // individual follow up
      .append(BundleUtil.getLabel("Date.label")).append("</th><th>")
      .append(BundleUtil.getLabel("Start.label")).append("</th><th>")
      .append(BundleUtil.getLabel("End.label")).append("</th><th>")
      .append(BundleUtil.getLabel("Duration.label")).append("</th></tr></thead><tbody>");
    int min = 0;
    for (ScheduleRangeObject r : ranges) {
      Hour hs = r.getStart();
      Hour he = r.getEnd();
      min += hs.getLength(he);
      //String note = r.getFollowUp() == null ? "" : r.getFollowUp().getContent();
      String abs = r.getFollowUp().isAbsent() ? "ABS" : r.getFollowUp().isExcused() ? "EXC" : "";
      sb.append("<tr><td>")
        .append(r.getActivity()).append("</td><td>")
        .append(r.getTeacher().getFirstnameName()).append("</td><td>")
        .append(r.getRoom().getName()).append("</td><td>")
        //.append(note == null ? "" : note).append("</td><td>")
        .append(abs.isEmpty() ? "" : abs)
        .append("</td><td>")
        .append(r.getDate()).append("</td><td>")
        .append(r.getStart()).append("</td><td>")
        .append(r.getEnd()).append("</td><td>")
        .append(Hour.format(hs.getLength(he))).append("</td></tr>");
    }
    sb.append("</tbody><tfoot><tr><td colspan=\"7\">Total</td><td> ").append(Hour.format(min)).append("</td></tr>");
    sb.append("</tfoot></table>");
    return sb.toString();
  }

  public static String fillActivityAMPM(List<ScheduleRangeObject> ranges, String[] options) {
    Hour pm = new Hour(options[0] == null || Hour.NULL_HOUR.equals(options[0]) ? "14:00" : options[0]);
    DateFr currentDate;
    List<SigningSheetDaySchedule> sheetDays = new ArrayList<>();
    double sdTotalPreAM = 0.0;
    double sdTotalPrePM = 0.0;
    double sdTotalAbsAM = 0.0;
    double sdTotalAbsPM = 0.0;
    if (ranges.isEmpty()) {
      currentDate = new DateFr(DateFr.NULLDATE);
    } else {
      currentDate = ranges.get(0).getDate();
    }
    for (ScheduleRangeObject r : ranges) {
      if (r.getDate().equals(currentDate)) {
        Hour hs = r.getStart();
        Hour he = r.getEnd();
        int min = hs.getLength(he);
        // distinguer repets (up is null)
        FollowUp up = r.getFollowUp();
        if (hs.before(pm)) {
          if (up != null && (up.isAbsent() || up.isExcused())) {
            sdTotalAbsAM += min / 60.0;
          } else {
            sdTotalPreAM += min / 60.0;
          }
        } else if (up != null && (up.isAbsent() || up.isExcused())) {
          sdTotalAbsPM += min / 60.0;
        } else {
          sdTotalPrePM += min / 60.0;
        }
      } else {
        SigningSheetDaySchedule sheetDay = new SigningSheetDaySchedule();
        sheetDay.setDay(currentDate);
        sheetDay.setTotalAbsAM(sdTotalAbsAM);
        sheetDay.setTotalAbsPM(sdTotalAbsPM);
        sheetDay.setTotalPreAM(sdTotalPreAM);
        sheetDay.setTotalPrePM(sdTotalPrePM);
        sheetDays.add(sheetDay);
        //reset
        currentDate = r.getDate();
        sdTotalPreAM = 0.0;
        sdTotalPrePM = 0.0;
        sdTotalAbsAM = 0.0;
        sdTotalAbsPM = 0.0;

        Hour hs = r.getStart();
        Hour he = r.getEnd();
        int min = hs.getLength(he);
        FollowUp up = r.getFollowUp();
        if (hs.before(pm)) {
          if (up != null && (up.isAbsent() || up.isExcused())) {
            sdTotalAbsAM += min / 60.0;
          } else {
            sdTotalPreAM += min / 60.0;
          }
        } else if (up != null && (up.isAbsent() || up.isExcused())) {
          sdTotalAbsPM += min / 60.0;
        } else {
          sdTotalPrePM += min / 60.0;
        }
      }
    }
    if (ranges.size() > 0) {
      SigningSheetDaySchedule sheetDay = new SigningSheetDaySchedule();
      sheetDay.setDay(currentDate);
      sheetDay.setTotalAbsAM(sdTotalAbsAM);
      sheetDay.setTotalAbsPM(sdTotalAbsPM);
      sheetDay.setTotalPreAM(sdTotalPreAM);
      sheetDay.setTotalPrePM(sdTotalPrePM);
      sheetDays.add(sheetDay);
    }

    StringBuilder sb = new StringBuilder();

    String thead = "<table class=\"content\"><thead><tr><th rowspan=\"2\">" + BundleUtil.getLabel("Date.label") + "</th><th colspan=\"2\">" + BundleUtil.getLabel("Morning.label") + "</th><th rowspan=\"2\">Signature</th><th colspan=\"2\">" + BundleUtil.getLabel("Afternoon.label") + "</th><th rowspan=\"2\">" + BundleUtil.getLabel("Signature.label") + "</th></tr><tr><td>" + BundleUtil.getLabel("Present.abbrev.label") + "</td><td>" + BundleUtil.getLabel("Absent.abbrev.label") + "</td><td>" + BundleUtil.getLabel("Present.abbrev.label") + "</td><td>" + BundleUtil.getLabel("Absent.abbrev.label") + "</td></tr></thead>";
    sb.append(thead);
    sb.append("<tbody>");

    double totalPreAM = 0.0;
    double totalAbsAM = 0.0;
    double totalPrePM = 0.0;
    double totalAbsPM = 0.0;
    for (SigningSheetDaySchedule d : sheetDays) {
      totalPreAM += d.getTotalPreAM();
      totalAbsAM += d.getTotalAbsAM();
      totalPrePM += d.getTotalPrePM();
      totalAbsPM += d.getTotalAbsPM();
      sb.append("<tr><td>").append(d.getDay()).append("</td>");

      sb.append("<td>").append(d.getTotalPreAM() > 0 ? String.format("%.2f", d.getTotalPreAM()) : "").append("</td>");
      sb.append("<td>").append(d.getTotalAbsAM() > 0 ? String.format("%.2f", d.getTotalAbsAM()) : "").append("</td>");
      sb.append("<td></td>");
      sb.append("<td>").append(d.getTotalPrePM() > 0 ? String.format("%.2f", d.getTotalPrePM()) : "").append("</td>");
      sb.append("<td>").append(d.getTotalAbsPM() > 0 ? String.format("%.2f", d.getTotalAbsPM()) : "").append("</td>");
      sb.append("<td></td></tr>");
    }

    sb.append("</tbody><tfoot><tr><th>TOTAL</th>");
    sb.append("<td>").append(String.format("%.2f", totalPreAM)).append("</td>");
    sb.append("<td>").append(String.format("%.2f", totalAbsAM)).append("</td>");
    sb.append("<td></td>");
    sb.append("<td>").append(String.format("%.2f", totalPrePM)).append("</td>");
    sb.append("<td>").append(String.format("%.2f", totalAbsPM)).append("</td>");

    //print total
    double totalPre = totalPreAM + totalPrePM;
    double totalAbs = totalAbsAM + totalAbsPM;
    sb.append("<td class=\"total\">Total ").append(BundleUtil.getLabel("Present.abbrev.label")).append("&nbsp;:").append(String.format("%8.2f", totalPre).replace(" ", "&nbsp;")).append("<br />");
    sb.append("Total ").append(BundleUtil.getLabel("Absent.abbrev.label")).append("&nbsp;:").append(String.format("%8.2f", totalAbs).replace(" ", "&nbsp;")).append("</td>");

    sb.append("<tr><th class=\"signature\" colspan=\"7\">").append(BundleUtil.getLabel("Name.and.quality.of.training.manager.label")).append(" : ").append(options[1]).append("</th></tr>");
    sb.append("<tr><th class=\"signature\" colspan=\"7\">").append(BundleUtil.getLabel("Signature.label")).append(" :</th></tr>");
    sb.append("</tfoot></table>");
    return sb.toString();
  }

  /**
   * Returns main infos about this enrolment {@literal node}.
   *
   * @param node enrolment node
   * @return a html-formatted string
   */
  private String catchEnrolmentInfo(EnrolmentNode node) {
    StringBuilder sb = new StringBuilder();
    String nickName = dossier.getContact().getNickName();
    sb.append("<h1>").append(dossier.getContact().getFirstnameName()).append(" : ").append(nickName == null ? "" : nickName).append("</h1>");
    sb.append("<h2>")
      .append(BundleUtil.getLabel("Enrolment.label")).append(" n° ")
      .append(node.getOrder().getId()).append(' ')
      .append(BundleUtil.getLabel("Date.From.label").toLowerCase()).append(' ')
      .append(node.getOrder().getCreation())
      .append("</h2>");
    for (Enumeration e = node.children(); e.hasMoreElements();) {
      TreeNode n = (TreeNode) e.nextElement();
      if (n instanceof ModuleEnrolmentNode) {
        // print info commmande module
        sb.append("<ul>");
        String moduleInfo = ((ModuleEnrolmentNode) n).toString();
        moduleInfo = moduleInfo.substring(6, moduleInfo.lastIndexOf('<'));// do not include <html></html> tag
        sb.append("<li>").append(moduleInfo).append("</li>");
        if (n.getChildCount() >= 0) {
          sb.append("<ul>");
          // print info commande cours
          for (Enumeration c = n.children(); c.hasMoreElements();) {
            TreeNode cn = (TreeNode) c.nextElement();
            if (cn instanceof CourseEnrolmentNode) {
              sb.append("<li>").append(((CourseEnrolmentNode) cn).toString()).append("</li>");
            }
          }
          sb.append("</ul>");
        }
        sb.append("</ul>");
      }
    }

    return sb.toString();

  }

  /**
   * Gets the css style used to print enrolment information.
   *
   * @return a css-formatted string
   */
  public static String getCss() {
    return " body {font-family: Arial, Helvetica, sans-serif;font-size: 1em}"
      + " table {width: 100%;border-spacing: 0;border-collapse: collapse;border:1px solid Gray;font-size: 0.8em}"
      + " td, th { border-left: 1px solid Gray;text-align :left }"
      + " tbody td, tbody th { border-bottom: 1px solid LightGray}"
      + " tbody tr:last-child td {border-bottom: 1px solid Gray}"
      + " thead td, thead th, tfoot td, tfoot th { border-bottom: 1px solid Gray}"
      + " tbody tr:nth-child(even) {background-color: #E6E6E6 !important}"
      + " tbody tr:nth-child(odd) {background-color: #FFF}"
      + " body  ul li {font-weight: bold}"
      + " body  ul ul li {font-weight: normal}"
      + " h1 {font-size: 1.2em}"
      + " h2 {font-size : 1.1em}"
      + " ul {font-size: 0.9em;line-height: 1.4em}"
      + " h1, h2 {background-color: #CCC !important}";
//      + " -webkit-print-color-adjust:exact;";
  }

  /**
   * Expands the last branch of the tree.
   */
  private void expand() {
    tree.setCellRenderer(cellRenderer);
    // on récupère le nombre de lignes visibles
    int x = tree.getRowCount();
    // on récupère le dernier TreePath
    TreePath tp = tree.getPathForRow(x - 1);
    // on récupère le dernier node visible (la dernière inscription)
    TreeNode node = (TreeNode) tp.getLastPathComponent();
    // on expand le path pour tous ses enfants (les différents modules)
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = tp.pathByAddingChild(n);
        tree.expandPath(path);
      }
    }
    tree.setSelectionRow(x - 1);
    tree.scrollRowToVisible(x - 1); // doesn't seem to work
  }

  private boolean isModuleNode(Object[] path) {
    return path[path.length - 1] instanceof ModuleEnrolmentNode;
  }

  private class StopCourseDateDlg
    extends StopCourseAbstractDlg {

    private DateFr date;

    public StopCourseDateDlg(Frame owner, String title, String course, boolean modal) {
      super(owner, title, modal);

      view = new StopCourseView(course);

      btOk = new GemButton(GemCommand.VALIDATION_CMD);
      btOk.addActionListener(this);
      btCancel = new GemButton(GemCommand.CANCEL_CMD);
      btCancel.addActionListener(this);

      JPanel buttons = new JPanel();
      buttons.setLayout(new GridLayout(1, 1));
      buttons.add(btOk);
      buttons.add(btCancel);

      setLayout(new BorderLayout());
      add(view, BorderLayout.CENTER);
      add(buttons, BorderLayout.SOUTH);
      setSize(GemModule.XXS_SIZE);
      setLocationRelativeTo(owner);
      setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == btOk) {
        this.date = view.getDateStart();
      } else {
        this.date = null;
      }
      close();
    }

    public DateFr getDate() {
      return date;
    }

  }

}
