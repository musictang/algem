/*
 * @(#)MemberEnrolmentEditor.java 2.8.t 02/05/14
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
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
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
import net.algem.course.*;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.planning.editing.ChangeHourCourseDlg;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.StopCourseDlg;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.menu.MenuPopupListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Enrolment editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
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
  private final static String NONE_ENROLMENT = MessageUtil.getMessage("enrolment.empty.list");
  private final static String COURSE_DATE = BundleUtil.getLabel("Course.date.modification.label");

  private PersonFile dossier;
  private DefaultMutableTreeNode root;
  private JPopupMenu popup;
  private JTree tree;
  private DefaultTreeModel model;
  private JScrollPane view;
  private GemLabel title;
  private boolean loaded;
  private boolean validation;
  private CourseEnrolmentDlg courseDlg;
  private JMenuItem m1, m2, m3, m4, m5, m6, m7;
  /** New enrolment button. */
  private GemButton btEnrolment;
  private TreePath currentSelection;
  private ActionListener acListener;
  private EnrolmentService service;
  private ModuleDlg moduleDlg;


  public MemberEnrolmentEditor(GemDesktop _desktop, ActionListener _listener, PersonFile _dossier) {

    super(_desktop);
    dossier = _dossier;
    acListener = _listener;
    service = new EnrolmentService(desktop.getDataCache());

    title = new GemLabel(BundleUtil.getLabel("Member.enrolment.label"));
    validation = false;

    popup = new JPopupMenu();
    popup.add(m1 = new JMenuItem(STOP));
    popup.add(m2 = new JMenuItem(COURSE_MODIF));
    popup.add(m3 = new JMenuItem(HOUR_MODIF));
    popup.add(m7 = new JMenuItem(COURSE_DATE));
    popup.add(m4 = new JMenuItem(NEW_COURSE));
    popup.add(m5 = new JMenuItem(NEW_MODULE));
    popup.add(m6 = new JMenuItem(MODULE_DEL));

    m1.addActionListener(this);
    m2.addActionListener(this);
    m3.addActionListener(this);
    m4.addActionListener(this);
    m5.addActionListener(this);
    m6.addActionListener(this);
    m7.addActionListener(this);

    tree = new JTree(new DefaultMutableTreeNode(NONE_ENROLMENT));
    //tree.setCellRenderer(new MyRenderer());//XXX ne fonctionne pas
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
            setEnabledPopupMenu(true);
          } else {
            setEnabledPopupMenu(false);
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

  private void setEnabledPopupMenu(boolean e) {
    if (e) {
      m1.setEnabled(true);
      m2.setEnabled(true);
      m3.setEnabled(true);
      m4.setEnabled(false);
      m5.setEnabled(false);
      m6.setEnabled(false);
      m7.setEnabled(true);
    } else {
      m1.setEnabled(false);
      m2.setEnabled(false);
      m3.setEnabled(false);
      m4.setEnabled(false);
      m5.setEnabled(true);
      m6.setEnabled(false);
      m7.setEnabled(false);
    }
  }

  private void setModulePopupMenu() {
    m1.setEnabled(false);
    m2.setEnabled(false);
    m3.setEnabled(false);
    m4.setEnabled(true);
    m5.setEnabled(false);
    m6.setEnabled(true);
    m7.setEnabled(false);
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
    //DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
    for (int j = 0; j < ins.size(); j++) {
      Enrolment i = ins.elementAt(j);
      EnrolmentNode ni = new EnrolmentNode(i);

      Enumeration<ModuleOrder> enu = i.getModule().elements();
      while (enu.hasMoreElements()) {
        try {
          ModuleOrder mo = enu.nextElement();//probleme apres inscription
          ModuleEnrolmentNode mnode = new ModuleEnrolmentNode(mo);
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
//    Order cmd = ((EnrolmentNode) path[i - 3]).getOrder();
    if (cc.getAction() == 0) {
      return;
    }
    try {
      PlanningService planningService = new PlanningService(dc);
      Course c = planningService.getCourseFromAction(cc.getAction());
      if (c.isUndefined()) {
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

    CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    if (!cc.getStart().equals(new Hour("00-00-00"))) {
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
      code = (GemParam) DataCache.findId(cc.getCode(), Model.CourseCode);
      cmi.setCode(code);
      cmi.setTimeLength(cc.getTimeLength());
      courseDlg.setCourseInfo(cmi);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    courseDlg.clear();
    courseDlg.setCode(cc.getCode());
    try {
      courseDlg.loadEnrolment(cc);
      courseDlg.entry();
      if (courseDlg.isValidation()) {
        if (!MessagePopup.confirm(desktop.getFrame(),
                MessageUtil.getMessage("enrolment.update.confirmation", cc.getDateStart()))) {
          view.setCursor(Cursor.getDefaultCursor());
          return;
        }
        modifyCourseOrder(cc, courseDlg);
        service.modifyCourse(cc, dossier.getId());
        desktop.postEvent(new ModifPlanEvent(this, cc.getDateStart(), cc.getDateEnd()));
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

  private void modifyCourseOrder(CourseOrder cc, CourseEnrolmentDlg dlg) {
    cc.setModuleOrder(Integer.parseInt(dlg.getField(1)));
    cc.setAction(Integer.parseInt(dlg.getField(2)));
    cc.setTitle(dlg.getField(3));
    cc.setDay(Integer.parseInt(dlg.getField(4)));

    if (CourseCodeType.ATP.getId() == cc.getCode()) {
      DateFr d = new DateFr(dlg.getField(7));
      cc.setDateStart(d);
      cc.setDateEnd(d);
    }
    Hour start = new Hour(dlg.getField(5));
    Hour length = new Hour(dlg.getField(6));
    cc.setStart(start);
    cc.setEnd(start.end(length.toMinutes()));
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
      }
      moduleDlg.show();
      if (!moduleDlg.isValidation()) {
        return;
      }
      int idModule = Integer.parseInt(moduleDlg.getField(0));

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

      String school = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey(), dc);
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
    co.setStart(new Hour("00:00"));
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
    mo.setSelectedModule(Integer.parseInt(moduleDlg.getField(7)));
    mo.setStart(new DateFr(moduleDlg.getField(2)));
    mo.setEnd(new DateFr(moduleDlg.getField(3)));
    mo.setPrice(Double.parseDouble(moduleDlg.getField(4)));
    mo.setModeOfPayment(moduleDlg.getField(5));
    mo.setPayment(moduleDlg.getField(6));
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

  private void expand() {
    tree.setCellRenderer(new CellRenderer());
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

  class MyRenderer
          extends DefaultTreeCellRenderer
  {

    public MyRenderer() {
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
      JComponent c = (JComponent) super.getTreeCellRendererComponent(tree, value, selected, exp, leaf, row, hasFocus);
      if (leaf && value instanceof CourseEnrolmentNode) {
        CourseOrder co = ((CourseEnrolmentNode) value).getCourseOrder();
        if (co != null && co.getAction() == 0) {
          c.setOpaque(true);
          c.setFont(getFont().deriveFont(Font.ITALIC));
          c.setBackground(Color.RED);
        }
      }
      return c;
    }
  }

}
