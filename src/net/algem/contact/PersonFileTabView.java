/*
 * @(#)PersonFileTabView.java  2.11.3 17/10/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes All Rights Reserved.
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
package net.algem.contact;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.algem.accounting.DDMandate;
import net.algem.accounting.DDPrivateMandateCtrl;
import net.algem.accounting.DirectDebitService;
import net.algem.bank.*;
import net.algem.contact.member.*;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherEditor;
import net.algem.contact.teacher.TeacherFollowUpEditor;
import net.algem.edition.DirectDebitRequest;
import net.algem.enrolment.EnrolmentEvent;
import net.algem.enrolment.MemberEnrolmentEditor;
import net.algem.group.GemGroupService;
import net.algem.group.Group;
import net.algem.group.Musician;
import net.algem.group.PersonFileGroupView;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.model.Reloadable;
import net.algem.util.module.FileTabView;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Tab view container for person file.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 */
public class PersonFileTabView
        extends FileTabView
        implements ChangeListener, ItemListener, PersonFileListener //GemEventListener
{

  private static final String CONTACT_TAB_TITLE = BundleUtil.getLabel("Contact.label");
  private static final String MEMBER_TAB_F_TITLE = BundleUtil.getLabel("Person.member.followup.tab.label");
  private static final String MEMBER_TAB_TITLE = BundleUtil.getLabel("Member.label");
  private static final String TEACHER_TAB_TITLE = BundleUtil.getLabel("Person.teacher.tab.label");
  private static final String TEACHER_TAB_F_TITLE = BundleUtil.getLabel("Person.teacher.followup.tab.label");
  private static final String BANK_TAB_TITLE = BundleUtil.getLabel("Person.bank.editing.label");
  private static final String BAND_TAB_TITLE = BundleUtil.getLabel("Groups.label");
  private static final String HISTO_REHEARSAL_TAB_TITLE = BundleUtil.getLabel("Person.rehearsal.history.tab.label");
  private static final String HISTO_SUBSCRIPTIONS_TAB_TITLE = BundleUtil.getLabel("Subscriptions.label");

  private PersonFile dossier, parent;
  private ContactFileEditor contactFileEditor;
  private JCheckBox cbTelAdresse;
  private MemberEditor memberEditor;
  private TeacherEditor teacherEditor;
  private EmployeeEditor employeeEditor;
  private RibView ribView;
  private MemberFollowUpEditor memberFollowUpEditor;
  private TeacherFollowUpEditor teacherFollowUpEditor;
  private MemberEnrolmentEditor enrolmentEditor;
  private HistoRehearsalView histoRehearsalView;
  private PersonFileGroupView groupView;
  private GemButton saveBt, closeBt;
  private Note note;
  private GemToolBar mainToolbar;
  private GemToolBar closeToolbar;
  private BankBranchIO bankBranchIO;
  private HistoSubscriptionCard histoSubscriptionCard;
  private final MemberService memberService;

  public PersonFileTabView(GemDesktop desktop, PersonFile dossier, ActionListener listener) {
    super(desktop, "Person");
    this.desktop.addGemEventListener(this);
    this.listener = listener;
    this.dossier = dossier;
    this.memberService = new MemberService(DataCache.getDataConnection());
  }

  public MemberEnrolmentEditor getEnrolmentView() {
    return enrolmentEditor;
  }

  public MemberFollowUpEditor getMemberFollowUp() {
    return memberFollowUpEditor;
  }

  public TeacherFollowUpEditor getTeacherFollowUp() {
    return teacherFollowUpEditor;
  }

  public void init() {
    addActionListener(listener);

    setLayout(new BorderLayout());
    mainToolbar = new GemToolBar(false);
    closeToolbar = new GemToolBar(false);

    mainToolbar.addIcon(listener,
            BundleUtil.getLabel("Note.icon"),
            GemCommand.NOTE_CMD,
            BundleUtil.getLabel("Note.icon.tip"));
    mainToolbar.addIcon(listener,
            BundleUtil.getLabel("Member.schedule.payment.icon"),
            "Member.schedule.payment",
            BundleUtil.getLabel("Member.schedule.payment.tip"));
    if (dataCache.authorize("Person.rehearsal.scheduling.auth")) {
      mainToolbar.addIcon(listener,
              BundleUtil.getLabel("Person.rehearsal.scheduling.icon"),
              "Person.rehearsal.scheduling",
              BundleUtil.getLabel("Person.rehearsal.scheduling.tip"));
    }

    // modification des espacements par défaut de la partie haute de l'onglet.
    UIManager.put("TabbedPane.tabInsets", TabPanel.DEFAULT_INSETS);

    wTab = new TabPanel();
    wTab.addChangeListener(this);

    add(wTab, BorderLayout.CENTER);

    contactFileEditor = new ContactFileEditor(desktop);
    contactFileEditor.setCodePostalCtrl(new CodePostalCtrl(DataCache.getDataConnection()));
    if (dossier.getContact() != null) {
      contactFileEditor.set(dossier.getContact());
      // Window title : name followed by member id
      setTitle(getTitle() + dossier.getContact().toString() + " " + dossier.getContact().getId());
      try {
        note = NoteIO.findId(dossier.getId(), dossier.getContact().getType(), DataCache.getDataConnection());
      } catch (NoteException ex) {
        GemLogger.logException(ex);
      }
      if (note != null) {
        contactFileEditor.setNote(note);
      }
      // Rest on last subscription
      PersonSubscriptionCard lastCard = memberService.getLastSubscription(dossier.getId(), false);
      dossier.setSubscriptionCard(lastCard);
      contactFileEditor.setSubscriptionRest(lastCard);
    }

    wTab.addItem(contactFileEditor, CONTACT_TAB_TITLE);

    if (dossier.getMember() != null) {
      try {
        addMemberTab();
        memberEditor.set(dossier.getMember());
        if (dossier.getContact().getAddress() == null && dossier.getContact().getTele() == null) {
          Vector<Address> addressLink = AddressIO.findId(dossier.getMember().getPayer(), DataCache.getDataConnection());
          Vector<Telephone> phoneLink = TeleIO.findId(dossier.getMember().getPayer(), DataCache.getDataConnection());

          if (addressLink.size() > 0 || phoneLink.size() > 0) {
            cbTelAdresse = new JCheckBox(MessageUtil.getMessage("payer.link.info", dossier.getMember().getPayer()), true);
            cbTelAdresse.addItemListener(this);
            // adherent lié info
            contactFileEditor.setLinkTelAddress(addressLink, phoneLink, cbTelAdresse);
          }
        }
      } catch (SQLException ex) {
        GemLogger.log(getClass().getName(), "init", ex);
      }
    } else if (parent != null) {
      cbTelAdresse = new JCheckBox(MessageUtil.getMessage("payer.link.info", parent.getId()), true);
      cbTelAdresse.addItemListener(this);
      contactFileEditor.setLinkTelAddress(parent.getContact().getAddressAll(), parent.getContact().getTele(), cbTelAdresse);
    }

    if (dossier.getTeacher() != null) {
      addTeacherTab(listener);
//      teacherEditor.load();
    }

    if (dossier.getRib() != null) {
      addBankTab();
    }
    
    saveBt = closeToolbar.addIcon(BundleUtil.getLabel("Contact.save.icon"),
            GemCommand.SAVE_CMD,
            BundleUtil.getLabel("Save.tip"));
    closeBt = closeToolbar.addIcon(BundleUtil.getLabel("Contact.close.icon"),
            GemCommand.CLOSE_CMD,
            BundleUtil.getLabel("Close.tip"));

    saveBt.addActionListener(listener);
    closeBt.addActionListener(listener);

    GemBorderPanel toolbar = new GemBorderPanel();

    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
    toolbar.add(mainToolbar);
    JPanel right = new JPanel(new BorderLayout());
    right.add(Box.createHorizontalGlue(), BorderLayout.WEST);
    right.add(closeToolbar, BorderLayout.EAST);
    toolbar.add(right);

    add(toolbar, BorderLayout.NORTH);
    wTab.setSelectedIndex(0);
    Preferences prefs = Preferences.userRoot().node("/algem/ui");
    setSize(new Dimension(prefs.getInt("personfileeditor.w",GemModule.XXL_SIZE.width), prefs.getInt("personfileeditor.h",GemModule.XXL_SIZE.height)));
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getSource() == cbTelAdresse) {
      contactFileEditor.setLinkTelAddress(cbTelAdresse.isSelected());
    }
  }

  /**
   *
   * @param _evt
   * @see ChangeListener
   */
  @Override
  public void stateChanged(ChangeEvent _evt) {
    FileTab tab = (FileTab) wTab.getSelectedComponent();
    if (!tab.isLoaded()) {
      tab.load();
    }
  }

  /**
   * Calls reload in components of type Reloadable.
   *
   * @param d la fiche personne
   */
  public void reload(PersonFile d) {

    for (Component cp : wTab.getComponents()) {
      if (cp instanceof Reloadable) {
        ((Reloadable) cp).reload(d);
      }
    }
  }

  /**
   * @param evt event
   * @see net.algem.contact.PersonFileListener (Used only for subscription card).
   */
  @Override
  public void contentsChanged(PersonFileEvent evt) {
    if (evt.getType() == PersonFileEvent.CONTENTS_CHANGED) {
      PersonFile pf = ((PersonFile) evt.getSource());
      contactFileEditor.set(pf.getContact());
    }
    if (evt.getType() == PersonFileEvent.CONTACT_CHANGED) {
      Contact c = ((Contact) evt.getSource());
      contactFileEditor.set(c);
    } else if (evt.getType() == PersonFileEvent.MEMBER_ADDED) {
      Member m = ((Member) evt.getSource());
      memberEditor.set(m);
    } else if (evt.getType() == PersonFileEvent.TEACHER_ADDED) {
      Teacher t = ((Teacher) evt.getSource());
//      teacherEditor.set(t);
    } else if (evt.getType() == PersonFileEvent.BANK_ADDED) {
      Rib b = ((Rib) evt.getSource());
//      ribView.setRib(b);
    } else if (evt.getType() == PersonFileEvent.SUBSCRIPTION_CARD_CHANGED) {
      PersonSubscriptionCard card = (PersonSubscriptionCard) evt.getSource();
      if (card != null) {
        if (histoSubscriptionCard != null) {
          histoSubscriptionCard.load();
        }
        contactFileEditor.setSubscriptionRest(card);
      }
    }
  }

  GemButton addIcon(String command) {
    return mainToolbar.addIcon(BundleUtil.getLabel(command + ".icon"), command, BundleUtil.getLabel(command + ".tip"));
  }

  /**
   * Adds a member tab.
   * If the member is a student, follow up and enrolment tabs are also added.
   *
   * @see #completeMember()
   */
  void addMemberTab() {

    if (memberEditor == null) {
      memberEditor = new MemberEditor(desktop, dossier.getId());
      if (parent != null) {
        String name = "";
        if (parent.getContact() != null) {
          String org = parent.getContact().getOrgName();
          name = (org == null || org.isEmpty()) ? parent.getContact().getFirstnameName() : org;
        }
        memberEditor.setPayer(parent.getId(), name);
      } // ajout 2.Oi payeur lui-même par défaut
      else {
        memberEditor.setPayer(dossier.getId(), BundleUtil.getLabel("Himself.label"));
      }
      wTab.addItem(memberEditor, MEMBER_TAB_TITLE);
    } else {
      wTab.addItem(memberEditor, MEMBER_TAB_TITLE);//"Fiche Adherent"
    }
    try {
      //les vues inscription et suivi ne sont pas ajoutées si l'adhérent n'existe pas encore
      if ((Member) DataCache.findId(dossier.getId(), Model.Member) != null) {
        completeMember();
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    addTab(memberEditor);
  }

  /**
   * Adds employee tab.
   */
  void addEmployeeTab() {

    employeeEditor = new EmployeeEditor(desktop, listener);
    employeeEditor.setEmployee(dossier.getId());
    wTab.addItem(employeeEditor, BundleUtil.getLabel("Employee.label"));
    addTab(employeeEditor);
  }

  Employee getEmployee() {
    return employeeEditor == null ? null : employeeEditor.get();
  }

  void updateEmployee() {
    if (employeeEditor != null) {
      employeeEditor.update();
    }
  }

  void deleteEmployee() {
    if (MessagePopup.confirm(this, MessageUtil.getMessage("employee.delete.confirmation"))) {
      employeeEditor.delete();
      removeTab(employeeEditor);
    }
  }

  boolean hasEmployeeChanged() {
    return employeeEditor != null && employeeEditor.hasChanged();
  }

  void addMandates(final DirectDebitService ddService, final int payer) {

    DDPrivateMandateCtrl ddMandateCtrl = new DDPrivateMandateCtrl(desktop, ddService)
    {

      @Override
      public void load() {
        try {
          List<DDMandate> mandates = ddService.getMandates(payer);
          if (mandates != null) {
            listCtrl.loadResult(mandates);
          }
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
      }

      @Override
      protected void print() {
        // double check existing RIB : very optional here
        Rib d = dossier.getRib();
        Rib r = null;
        if (d != null) {
          r = RibIO.findId(dossier.getRib().getId(), dc);
        }
        if (d == null || r == null || !r.equals(d)) {
          MessagePopup.warning(this, MessageUtil.getMessage("rib.error.printing"));
          return;
        }

        boolean printOrderLines = true;

        DDMandate dd = listCtrl.getMandate();
        if (dd == null) {
          MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
          return;
        }
        if (!MessagePopup.confirm(this, MessageUtil.getMessage("standing.order.print.warning"), "Confirmation")) {
          printOrderLines = false;
        }
        DirectDebitRequest ddRequest = new DirectDebitRequest(this, dd, printOrderLines);
        ddRequest.edit(dossier, getBranchBank(), BundleUtil.getLabel("Menu.debiting.label"), dataCache);
      }
    };

    ddMandateCtrl.load();
    ddMandateCtrl.addActionListener(listener);
    wTab.addItem(ddMandateCtrl, BundleUtil.getLabel("Direct.debit.label"));
    addTab(ddMandateCtrl);
    activate(false, "Payer.debiting");
  }

  void addBankTab() {
    if (ribView == null) {
      BankBranchIO branchIO = new BankBranchIO(DataCache.getDataConnection());
      ribView = new RibView(desktop, dossier.getId());
      ribView.setRib(dossier.getRib());
      ribView.setBankCodeCtrl(new BankCodeCtrl(DataCache.getDataConnection(), branchIO));
      ribView.setPostalCodeCtrl(new CodePostalCtrl(DataCache.getDataConnection()));
      if (dossier.getRib() != null) {
        BankBranch bb = branchIO.findId(dossier.getRib().getBranchId());
        if (bb != null) {
          ribView.setBankBranch(bb);
        }
      }
    }

    wTab.addItem(ribView, BANK_TAB_TITLE);

    mainToolbar.addIcon(listener,
            BundleUtil.getLabel("Payer.debiting.icon"),
            "Payer.debiting",
            BundleUtil.getLabel("Payer.debiting.tip"));
    addTab(ribView);
  }

  void addTeacherTab(ActionListener listener) {
    if (teacherEditor == null) {
      teacherEditor = new TeacherEditor(desktop, dossier);
      teacherEditor.addActionListener(listener);
    }
    if (teacherFollowUpEditor == null) {
      teacherFollowUpEditor = new TeacherFollowUpEditor(desktop, dossier);
    }

    wTab.addItem(teacherEditor, TEACHER_TAB_TITLE);//"Fiche Enseignant");
    wTab.addItem(teacherFollowUpEditor, TEACHER_TAB_F_TITLE);//"Suivi Enseignant");

    mainToolbar.addIcon(listener, BundleUtil.getLabel("Teacher.hour.icon"), "Employee.hours", BundleUtil.getLabel("Employee.hours.tip"));
    mainToolbar.addIcon(listener, BundleUtil.getLabel("Teacher.break.icon"),"Teacher.break",BundleUtil.getLabel("Teacher.break.tip"));
    mainToolbar.addIcon(listener, BundleUtil.getLabel("Teacher.presence.icon"), "Teacher.presence", BundleUtil.getLabel("Teacher.presence.tip"));
    addTab(teacherEditor);
  }

  /**
   * Activates or desactivates an icon.
   *
   * @param b
   * @param command
   */
  void activate(boolean b, String command) {
    mainToolbar.setEnabled(b, command);
  }

  /**
   * Adds enrolment and follow-up tabs.
   *
   * @since 2.0ma by jm
   */
  void completeMember() {
    if (enrolmentEditor == null) {
      enrolmentEditor = new MemberEnrolmentEditor(desktop, listener, dossier);
    }
    if (memberFollowUpEditor == null) {
      memberFollowUpEditor = new MemberFollowUpEditor(desktop, dossier);
    }
    wTab.addItem(enrolmentEditor, BundleUtil.getLabel("Person.enrolment.tab.label"));//inscription
    wTab.addItem(memberFollowUpEditor, MEMBER_TAB_F_TITLE);//suivi

    mainToolbar.addIcon(listener,
            BundleUtil.getLabel("Member.file.icon"),
            "Member.file",
            BundleUtil.getLabel("Member.file.tip"));
    mainToolbar.addIcon(listener,
            BundleUtil.getLabel("Member.card.icon"),
            "Member.card",
            BundleUtil.getLabel("Member.card.tip"));
  }

  // TODO EG a voir
  public void clear() {
    contactFileEditor.clear();
    if (memberEditor != null) {
      wTab.remove(memberEditor);
      memberEditor.clear();
      memberEditor = null;
    }
//    wTab.setSelectedIndex(0);
    if (cbTelAdresse != null) {
      cbTelAdresse.removeItemListener(this);
      cbTelAdresse = null;
    }
    if (teacherEditor != null) {
      wTab.remove(teacherEditor);
      teacherEditor.clear();
      teacherEditor = null;
    }
    clearRib();
  }

  void clearRib() {
    if (ribView != null) {
      mainToolbar.removeIcon("Payer.debiting");
      ribView.clear();
      ribView = null;
    }
  }

  @Override
  public void postEvent(GemEvent evt) {
    if (dossier.getMember() == null) {
      return;
    }
    if (evt instanceof EnrolmentEvent) {
      int id = ((EnrolmentEvent) evt).getDossierId();
      if (id != 0 && id == dossier.getMember().getId()) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            enrolmentEditor.load();
          }
        });
      }
    }
  }

  /**
   * Updates the number of memberships.
   *
   * @param n the number
   * @since 2.2.b
   */
  void setMemberShipNumber(int n) {
    memberEditor.setMembershipNumber(n);
  }

  Contact getContact() {
    return contactFileEditor.getContact();
  }

  Member getMemberFile() {
    return memberEditor == null ? null : memberEditor.getMember();
  }

  Teacher getTeacher() {
    return teacherEditor == null ? null : teacherEditor.get();
  }

  Rib getRibFile() {
    return ribView == null ? null : ribView.getRib();
  }

  /**
   * Gets the enrolment editor.
   *
   * @author Jean-Marc Gobat
   * @return an editor
   * @since 2.0j
   */
  MemberEnrolmentEditor getMemberEnrolment() {
    return enrolmentEditor;
  }

  /**
   * Activates the opening of enrolment view.
   *
   * @param activation
   */
  void activateEnrolment(boolean activation) {
    if (enrolmentEditor != null) {
      enrolmentEditor.setEnabled(activation);
    }
  }

  boolean isNewBank() {
    return ribView == null ? false : ribView.isNewBank();
  }

  boolean isNewBranchOfBank() {
    return ribView == null ? false : ribView.isNewBranch();
  }

  Bank getBank() {
    return ribView == null ? null : ribView.getBank();
  }

  BankBranch getBranchBank() {
    return ribView == null ? null : ribView.getBankBranch();
  }

  void setBranchBank(BankBranch a) {
    ribView.setBankBranch(a);
  }

  /**
   * Payer modification.
   *
   * @param _parent
   */
  void setParent(PersonFile _parent) {
    parent = _parent;
    memberEditor.setPayer(parent.getId(), parent.getContact().getFirstnameName());

  }

  void setSaveState(boolean state) {
    saveBt.setEnabled(!state);
  }

  void removeMemberIcons() {
    mainToolbar.removeIcon("Member.file");
    mainToolbar.removeIcon("Member.card");
  }

  void closeTeacher() {
    removeTab(teacherFollowUpEditor);
    removeTab(teacherEditor);
    removeTeacherIcons();
  }

  void removeTeacher() {
    closeTeacher();
    teacherFollowUpEditor = null;
    teacherEditor.clear();
    teacherEditor = null;

  }

  private void removeTeacherIcons() {
    mainToolbar.removeIcon("Employee.hours");
    mainToolbar.removeIcon("Teacher.break");
    mainToolbar.removeIcon("Teacher.presence");
  }

  void addRehearsalHistoryTab() {
    if (histoRehearsalView == null) {
      histoRehearsalView = new HistoRehearsalView(desktop, listener, dossier.getId());
    }
    desktop.setWaitCursor();
    histoRehearsalView.load(true);
    wTab.addItem(histoRehearsalView, HISTO_REHEARSAL_TAB_TITLE);
    addTab(histoRehearsalView);
    desktop.setDefaultCursor();
  }

  boolean addHistoSubscriptionTab() {
    if (histoSubscriptionCard == null) {
      histoSubscriptionCard = new HistoSubscriptionCard(desktop, dossier.getId(), listener, memberService);
    }  
    histoSubscriptionCard.load();
    if (!histoSubscriptionCard.isLoaded()) {
      return false;
    }
    wTab.addItem(histoSubscriptionCard, HISTO_SUBSCRIPTIONS_TAB_TITLE);
    addTab(histoSubscriptionCard);
    return true;
  }

  void removeSubscriptionTab() {
    removeTab(histoSubscriptionCard);
  }

  /**
   * Adds a tab for groups the member belongs to.
   * Only contacts of type <code>PERSON</code> may belong to a group.
   *
   * @param selectionFlag if true, tab is selected in view
   * @return true if tab opened
   */
  boolean addGroupsTab(boolean selectionFlag) {
    if (dossier == null || dossier.getContact() == null) {
      return false;
    }
    if (dossier.getContact().getType() != Contact.PERSON
            || dossier.getGroups() == null
            || dossier.getGroups().isEmpty()) {
      return false;
    }
    if (groupView == null) {
      Vector<Musician> vm = new Vector<Musician>();
      for (Group g : dossier.getGroups()) {
        //Musicien m = g.getMusician(dossier.getId());
        Musician m = getMusician(g, dossier.getId());
        if (m != null) {
          vm.addElement(m);
        }
      }
      if (vm.size() > 0) {
        PersonFileGroupListCtrl groupList = new PersonFileGroupListCtrl();
        groupList.addBlock(vm);
        groupView = new PersonFileGroupView(desktop, groupList);
      }
    } else {
      groupView.load();
    }
    addTab(groupView, BAND_TAB_TITLE, selectionFlag);

    return true;
  }

  void setNote(Note nd) {
    note = nd;
    contactFileEditor.setNote(nd);
  }

  void setID(int i) {
    contactFileEditor.setID(i);
  }

  Note getNote() {
    return note;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " : " + dossier.getId();
  }

  @Override
  public void close() {
    clear();
  }

  /**
   * Gets the musician {@literal idper} belonging to group {@literal g}.
   *
   * @param g the group
   * @param idper person id
   * @return a musician
   */
  private Musician getMusician(Group g, int idper) {

    try {
      Vector<Musician> vm = new GemGroupService(DataCache.getDataConnection()).getMusicians(g);
      for (Musician m : vm) {
        if (m.getId() == idper) {
          return m;
        }
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return null;
  }
}
