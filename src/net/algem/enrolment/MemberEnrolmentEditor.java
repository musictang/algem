/*
 * @(#)MemberEnrolmentEditor.java 2.7.a 26/11/12
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
package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import net.algem.contact.PersonFile;
import net.algem.course.Course;
import net.algem.course.Module;
import net.algem.course.ModuleIO;
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
 * @version 2.7.a
 * @since 1.0b 06/09/2001
 */
public class MemberEnrolmentEditor
        extends FileTab
        implements ActionListener
{

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
  private JMenuItem m1, m2, m3, m4;

  /** New enrolment button. */
  private GemButton btEnrolment;
  
  private TreePath currentSelection;
  private ActionListener acListener;
  private final static String STOP = BundleUtil.getLabel("Course.stop.label");
  private final static String COURSE_MODIF = BundleUtil.getLabel("Course.define.label");
  private final static String HOUR_MODIF = BundleUtil.getLabel("Course.hour.modification.label");
  private final static String NEW_WORKSHOP = BundleUtil.getLabel("New.workshop.label");
  private final static String NONE_ENROLMENT = MessageUtil.getMessage("enrolment.empty.list");
  private EnrolmentService service;

  public MemberEnrolmentEditor(GemDesktop _desktop, ActionListener _listener, PersonFile _dossier) {

    super(_desktop);
    dossier = _dossier;
    acListener = _listener;
    service = new EnrolmentService(desktop.getDataCache());

    title = new GemLabel("Inscriptions adhérent");
    validation = false;

    popup = new JPopupMenu();
    popup.add(m1 = new JMenuItem(STOP));
    popup.add(m2 = new JMenuItem(COURSE_MODIF));
    popup.add(m4 = new JMenuItem(NEW_WORKSHOP));
    m1.addActionListener(this);
    m2.addActionListener(this);
    m4.addActionListener(this);

    tree = new JTree(new DefaultMutableTreeNode(NONE_ENROLMENT));
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    tree.addTreeSelectionListener(new TreeSelectionListener()
    {

      public void valueChanged(TreeSelectionEvent e) {
        currentSelection = e.getNewLeadSelectionPath();
      }
    });
    
    tree.addMouseListener(new MenuPopupListener(tree, popup) {
      @Override
      public void maybeShowPopup(MouseEvent e) {
        if (currentSelection != null && currentSelection.getLastPathComponent() instanceof CourseEnrolmentNode) {
          super.maybeShowPopup(e);
        }
      }
    });

    model = (DefaultTreeModel) tree.getModel();
    view = new JScrollPane(tree);
    btEnrolment = new GemButton("Nouvelle Inscription");
    btEnrolment.setActionCommand("AdherentInscription.Create");
    btEnrolment.addActionListener(acListener);

    setLayout(new BorderLayout());
    add(title, BorderLayout.NORTH);
    add(view, BorderLayout.CENTER);
    add(btEnrolment, BorderLayout.SOUTH);
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
          ModuleOrder cm = enu.nextElement();
          EnrolmentModuleNode nmi = new EnrolmentModuleNode(cm);
          Vector<CourseOrder> v = service.getCourseOrder(i.getId(), cm.getModule());
          for (int k = 0; k < v.size(); k++) {
            CourseOrder cc = v.elementAt(k);
            int jj = service.getCourseDayMember(cc.getAction(), cc.getDateStart(), i.getMember());
            CourseEnrolmentNode nci = new CourseEnrolmentNode(cc, jj);
            nmi.add(nci);
          }
          ni.add(nmi);
        } catch (SQLException ex) {
          GemLogger.logException(getClass().getName()+"#load.run", ex);
        } 
      }
      root.add(ni);

    }
    model.setRoot(root);
    expand();
    desktop.setDefaultCursor();
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
    } else if (s.equals(HOUR_MODIF)) {
      if (currentSelection == null) {
        return;
      }
      changeHour();
    } else if (s.equals(NEW_WORKSHOP)) {
      addATP();
    }

  }

  private void changeHour() {

    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    Order cmd = ((EnrolmentNode) path[i - 3]).getOrder();
    ChangeHourCourseDlg dlg2;
    try {
      dlg2 = new ChangeHourCourseDlg(desktop, service, cmd, cc);
      dlg2.setVisible(true);
    } catch (SQLException ex) {
      GemLogger.log(MemberEnrolmentEditor.class.getName(), "changeHeure", ex);
    }

  }

  private void addATP() {
    Object[] path;
    try {
      path = currentSelection.getPath();
    } catch (NullPointerException npe) {
      return;
    }
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    ModuleOrder cm = ((EnrolmentModuleNode) path[i - 2]).getModule();
    // Verifier que le module comprend une option atelier
    try {
      Module f = ((ModuleIO) DataCache.getDao(Model.Module)).findId(cm.getModule());
      if (!f.withSelectiveWorkshop()) {
        if (!MessagePopup.confirm(view, MessageUtil.getMessage("atp.create.confirmation"),
                BundleUtil.getLabel("Confirmation.title"))) {
          return;
        }
      }

      CourseOrder cc = createATP(cm);
      if (courseDlg == null) {
        courseDlg = new CourseEnrolmentDlg(desktop, service, dossier.getId());
      }
      courseDlg.clear();
      courseDlg.setCode(cc.getCode());
      courseDlg.loadEnrolment(cc);

      view.setCursor(Cursor.getDefaultCursor());
      courseDlg.entry();
      if (courseDlg.isValid()) {
        if (!MessagePopup.confirm(view,
                MessageUtil.getMessage("enrolment.update.confirmation", new Object[]{cc.getDateStart()}),
                BundleUtil.getLabel("Confirmation.title"))) {
          return;
        }
        modifyCourseOrder(cc, courseDlg);
        service.modifyCourse(cc, dossier.getId());
        desktop.postEvent(new ModifPlanEvent(this, cc.getDateStart(), cc.getDateEnd()));
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));

      }
    } catch (EnrolmentException ex) {
      MessagePopup.warning(this, ex.getMessage());
    } catch (SQLException sqe) {
      MessagePopup.warning(this, getClass().getName() + "#ajouterATP :\n" + sqe.getMessage());
    }
  }

  private CourseOrder createATP(ModuleOrder cm) throws SQLException {
    CourseOrder cc = new CourseOrder();

    cc.setIdOrder(cm.getId());
    cc.setModule(cm.getModule());
    cc.setCode(Course.ATP_CODE);
    cc.setDateStart(cm.getStart());
    cc.setDateEnd(cm.getEnd());
    Course c = service.getCourseUndefined(Course.ATP_CODE);
    if (c != null) {
      cc.setAction(service.getActionFromCourse(c.getId()));
    }

    cc.setStart(new Hour("00:00:00"));
    cc.setEnd(new Hour("02:00:00"));
    return cc;
  }

  private void stopCourse() {

    StopCourseDlg dlg2 = null;
    Object[] path = currentSelection.getPath();
    int i = path.length;
    if (!(path[i - 1] instanceof CourseEnrolmentNode)) {
      return;
    }
    CourseOrder cc = ((CourseEnrolmentNode) path[i - 1]).getCourseOrder();
    Order cmd = ((EnrolmentNode) path[i - 3]).getOrder();
    try {
      PlanningService planningService = new PlanningService(dc);
      Course c = planningService.getCourseFromAction(cc.getAction());
      if (c.isUndefined()) {
        MessagePopup.information(this, MessageUtil.getMessage("course.invalid.choice"));
        return;
      }

      dlg2 = new StopCourseDlg(desktop, cmd, cc, c);
      dlg2.setVisible(true);
    } catch (SQLException ex) {
      System.err.println(getClass().getName() + "#arretCours :" + ex.getMessage());
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
      JOptionPane.showMessageDialog(desktop.getFrame(),
              MessageUtil.getMessage("course.invalid.choice"),
              BundleUtil.getLabel("Warning.label"),
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    view.setCursor(new Cursor(Cursor.WAIT_CURSOR));

    if (courseDlg == null) {
      courseDlg = new CourseEnrolmentDlg(desktop, service, dossier.getId());
    }
    courseDlg.clear();
    courseDlg.setCode(cc.getCode());
    try {
      courseDlg.loadEnrolment(cc);

      courseDlg.entry();
      if (courseDlg.isValid()) {
        if (!MessagePopup.confirm(desktop.getFrame(),
                MessageUtil.getMessage("enrolment.update.confirmation", cc.getDateStart()))
                ) {
          view.setCursor(Cursor.getDefaultCursor());
          return;
        }
        modifyCourseOrder(cc, courseDlg);//XXX problème si la ligne à définir n'existe pas au préalable
        service.modifyCourse(cc, dossier.getId());
        desktop.postEvent(new ModifPlanEvent(this, cc.getDateStart(), cc.getDateEnd()));
        // Rafraichissement de la vue inscription
        desktop.postEvent(new EnrolmentUpdateEvent(this, dossier.getId()));

      }
    } catch (EnrolmentException e) {
      MessagePopup.warning(view, e.getMessage());
    } finally {
      view.setCursor(Cursor.getDefaultCursor());
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

  private void modifyCourseOrder(CourseOrder cc, CourseEnrolmentDlg dlg) {
    cc.setModule(Integer.parseInt(dlg.getField(1)));
    cc.setAction(Integer.parseInt(dlg.getField(2)));
    cc.setTitle(dlg.getField(3));
    cc.setDay(Integer.parseInt(dlg.getField(4)));

    if (Course.ATP_CODE == cc.getCode()) {
      DateFr d = new DateFr(dlg.getField(7));
      cc.setDateStart(d);
      cc.setDateEnd(d);
    }
    Hour start=  new Hour(dlg.getField(5));
    Hour duration = new Hour(dlg.getField(6));
    cc.setStart(start);
    cc.setEnd(start.end(duration.toMinutes()));
  }
}
