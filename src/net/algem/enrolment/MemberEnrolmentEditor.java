/*
 * @(#)MemberEnrolmentEditor.java 2.9.1 26/11/14
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
package net.algem.enrolment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import net.algem.course.*;
import net.algem.planning.DateFr;
import net.algem.planning.DateRange;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleRangeObject;
import net.algem.planning.editing.ChangeHourCourseDlg;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.StopCourseDlg;
import net.algem.planning.editing.StopCourseFromModuleDlg;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.menu.MenuPopupListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Enrolment editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 1.0b 06/09/2001
 */
public class MemberEnrolmentEditor
        extends FileTab
        implements ActionListener
{

  private final static String STOP = BundleUtil.getLabel("Course.stop.label");
  private final static String COURSE_MODIF = BundleUtil.getLabel("Course.define.label");
  private final static String HOUR_MODIF = BundleUtil.getLabel("Course.hour.modification.label");
  private final static String NEW_COURSE = BundleUtil.getLabel("New.course.label");
  private final static String NEW_MODULE = BundleUtil.getLabel("New.module.label");
  private final static String MODULE_DEL = BundleUtil.getLabel("Module.delete.label");
  private final static String MODULE_STOP = BundleUtil.getLabel("Module.stop.label");
  private final static String MODULE_TIME_CHANGE = BundleUtil.getLabel("Module.time.change.label");
  private final static String NONE_ENROLMENT = MessageUtil.getMessage("enrolment.empty.list");
  private final static String COURSE_DATE = BundleUtil.getLabel("Course.date.modification.label");
  private final static String PRINT_ORDER = GemCommand.PRINT_CMD;

  private PersonFile dossier;
  private DefaultMutableTreeNode root;
  private JPopupMenu popup;
  private JTree tree;
  private DefaultTreeModel model;
  private JScrollPane view;
  private GemLabel title;
  private boolean loaded;
  private CourseEnrolmentDlg courseDlg;
  private JMenuItem m1, m2, m3, m4, m5, m6, m7, m8, m9, m10;
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
    popup.add(m1 = new JMenuItem(STOP));
    popup.add(m2 = new JMenuItem(COURSE_MODIF));
    popup.add(m3 = new JMenuItem(HOUR_MODIF));
    popup.add(m7 = new JMenuItem(COURSE_DATE));
    popup.add(m4 = new JMenuItem(NEW_COURSE));
    popup.addSeparator();
    popup.add(m5 = new JMenuItem(NEW_MODULE));
    popup.add(m6 = new JMenuItem(MODULE_DEL));
    popup.add(m8 = new JMenuItem (MODULE_STOP));
    popup.add(m9 = new JMenuItem (MODULE_TIME_CHANGE));
    popup.addSeparator();
    popup.add(m10 = new JMenuItem(PRINT_ORDER));

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

    cellRenderer = new EnrolmentTreeCellRenderer();
    tree = new JTree(new DefaultMutableTreeNode(NONE_ENROLMENT));
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(new TreeSelectionListener()
    {

      public void valueChanged(TreeSelectionEvent e) {
        currentSelection = e.getNewLeadSelectionPath();
      }
    });

    tree.addMouseListener(new MenuPopupListener(tree, popup)
    {

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
    } else {
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
    }
  }

  /**
   * Sets the popup menu to suit module stopping.
   */
  private void setModulePopupMenu() {
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
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    desktop.setWaitCursor();
    loaded = true;
    final Vector<Enrolment> ins = service.getEnrolments(dossier.getId());
    if (ins != null && ins.size() < 1) {
      root = new DefaultMutableTreeNode(NONE_ENROLMENT);
      model.setRoot(root);
      desktop.setDefaultCursor();
      return;
    }

    root = new DefaultMutableTreeNode(BundleUtil.getLabel("Person.enrolment.tab.label") + " : " + dossier.getContact().getFirstnameName());
    for (int j = 0; j < ins.size(); j++) {
      Enrolment i = ins.elementAt(j);
      EnrolmentNode ni = new EnrolmentNode(i);

      Enumeration<ModuleOrder> enu = i.getModule().elements();
      while (enu.hasMoreElements()) {
        try {
          ModuleOrder mo = enu.nextElement();//probleme apres inscription
          ModuleEnrolmentNode mnode = new ModuleEnrolmentNode(mo);
          if (mo.getTotalTime() > 0) {
            mnode.setCompleted(service.getCompletedTime(dossier.getId(), mo.getId()));
          }

          if (mo.isStopped()) {
            mnode.setInfo(" -> [[" + mo.getEnd().toString() + "]]");
          }
          Vector<CourseOrder> v = service.getCourseOrder(i.getId(), mo.getId());
          for (int k = 0; k < v.size(); k++) {
            CourseOrder cc = v.elementAt(k);
            int jj = service.getCourseDayMember(cc.getAction(), cc.getDateStart(), i.getMember());
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

  private String getUndefinedLabel(CourseOrder cc) throws SQLException {
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

    if (s.equals(STOP)) {
      if (currentSelection == null) {
        return;
      }
      stopCourse();
    } else if (s.equals(COURSE_MODIF)) {
      if (currentSelection == null) {
        return;
      }
      modifCourse();
    } else if (s.equals(COURSE_DATE)) {
      if (currentSelection == null) {
        return;
      }
      changeDateOfCourseOrder();
    } else if (s.equals (MODULE_STOP)) {
      if (currentSelection == null) {
        return;
      }
      stopModule();
    } else if (s.equals(HOUR_MODIF)) {
      if (currentSelection == null) {
        return;
      }
      changeHour();
    } else if (s.equals(NEW_COURSE)) {
      addCourse();
    } else if (s.equals(NEW_MODULE)) {
      addModule();
    } else if (s.equals(MODULE_DEL)) {
      deleteModuleOrder();
    } else if (s.equals(MODULE_TIME_CHANGE)) {
      changeModuleTime();
    } else if (s.equals(GemCommand.PRINT_CMD)) {
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

    CourseInfoDlg dlg = new CourseInfoDlg(desktop, true, null);
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

    StopCourseDlg dlg2 = null;
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    if (cc.getAction() == 0 && MessagePopup.confirm(this, MessageUtil.getMessage("course.suppression.confirmation"))) {;
      service.stopCourse(cc.getId());
      desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));
      return;
    }
    try {
      Course c = planningService.getCourseFromAction(cc.getAction());
      if (c == null || c.isUndefined()) {
        MessagePopup.information(this, MessageUtil.getMessage("course.invalid.choice"));
        return;
      }

      dlg2 = new StopCourseDlg(desktop, dossier.getId(), cc, c);
      dlg2.setVisible(true);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "#stopCourse :", ex.getMessage());
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
    CourseInfoDlg dlg = new CourseInfoDlg(desktop, true, oldDate);

    if (dlg.getDate() != null && !dlg.getDate().equals(oldDate)) {
      cc.setDateStart(dlg.getDate());
      try {
        service.update(cc);
      } catch (SQLException e) {
        GemLogger.log(e.getMessage());
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
    EnrolmentOrderUtil enrolmentOrder = new EnrolmentOrderUtil(dossier, dc);
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
      // TODO désactiver l'ajout de lignes d'échéance à l'ajout d'un module
      enrolmentOrder.setTotalBase(mo.getPrice());

      String school = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
      try {
        int n = enrolmentOrder.saveOrderLines(mo, Integer.parseInt(school));
        enrolmentOrder.updateModuleOrder(n, mo);
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
      mo.setTotalTime(((Hour) moduleDlg.getField(8)).toMinutes());
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
      if(n instanceof CourseEnrolmentNode) {
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
    dlg.set(new Hour(mo.getTotalTime()));
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
        temp = File.createTempFile(BundleUtil.getLabel("Enrolment.label") + "-" + order.getId() + "_", extension);
        pw = new PrintWriter(temp);
        pw.println(FileUtil.getHtmlHeader(BundleUtil.getLabel("Enrolment.label"), getCss()));
        pw.println(catchEnrolmentInfo(node));
        pw.println(catchActivity(order.getCreation(), dataCache.getEndOfYear()));
        pw.println("</body></html>");
      }
      if (pw != null) {
        pw.close();
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
      desktop.setDefaultCursor();
    }
  }

  /**
   * Returns the list of activities performed by member between {@code start} and {@code end} dates.
   * @param start starting date
   * @param end ending date
   * @return a html-formatted string
   */
  private String catchActivity(DateFr start, DateFr end){
    try {
      MemberService memberService = new MemberService(dc);
      StringBuilder sb = new StringBuilder();
      Vector<ScheduleRangeObject> ranges = memberService.findFollowUp(dossier.getId(), new DateRange(start, end));
      sb.append("<table><thead>");
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
        String note = r.getNote1();
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
      sb.append("</tbody><tfoot><tr><td colspan=\"7\">" + "Total" + "</td><td> ").append(Hour.format(min)).append("</td></tr>");
      sb.append("</tfoot></table>");
      return sb.toString();
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      return "";
    }
  }

  /**
   * Returns main infos about this enrolment {@code node}.
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
        sb.append("<li>").append(((ModuleEnrolmentNode) n).toString()).append("</li>");
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
   * @return a css-formatted string
   */
  private String getCss() {
    return "body {font-family: Arial, Helvetica, sans-serif}"
            + " table {width: 100%;border-spacing: 0;border-collapse: collapse;border:1px solid Gray;font-size: smaller}"
            + " td, th { border-left: 1px solid Gray;text-align :left }"
            + " tbody td, tbody th { border-bottom: 1px solid LightGray}"
            + " tbody tr:last-child td {border-bottom: 1px solid Gray}"
            + " thead td, thead th, tfoot td, tfoot th { border-bottom: 1px solid Gray}"
            + " tbody tr:nth-child(even) {background-color: #E6E6E6 !important}"
            + " tbody tr:nth-child(odd) {background-color: #FFF}"
            + " body  ul li {font-weight: bold}"
            + " body  ul ul li {font-weight: normal}"
            + " h1 {font-size: 1.5em}"
            + " h2 {font-size : 1.2em}"
            + " ul {font-size: 1em;line-height: 1.4em}"
            + " h1, h2 {background-color: #CCC !important}"
            + " -webkit-print-color-adjust:exact;print-color-adjust: exact;";
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

}
