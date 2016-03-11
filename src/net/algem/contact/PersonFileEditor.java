/*
 * @(#)PersonFileEditor 2.9.4.13 02/11/15
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
package net.algem.contact;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.*;
import net.algem.accounting.*;
import net.algem.bank.*;
import net.algem.billing.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.member.*;
import net.algem.contact.teacher.TeacherEditor;
import net.algem.contact.teacher.TeacherEvent;
import net.algem.contact.teacher.TeacherIO;
import net.algem.edition.*;
import net.algem.enrolment.MemberEnrolmentDlg;
import net.algem.group.PersonFileGroupView;
import net.algem.planning.TeacherBreakDlg;
import net.algem.planning.month.MonthScheduleTab;
import net.algem.security.User;
import net.algem.security.UserCreateDlg;
import net.algem.security.UserException;
import net.algem.security.UserService;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.menu.PersonFileMenuItem;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import net.algem.util.module.FileEditor;
import net.algem.util.module.FileTabView;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.*;

/**
 * Person file editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class PersonFileEditor
        extends FileEditor
        implements PersonFileListener, UIAdjustable
{

  private JMenuBar mBar;
  private JMenu mFile;
  private JMenu mOptions;
  private JMenu mHelp;
  private JMenuItem miAbout;
  private JMenuItem miDoc;
  private JMenuItem miDelete;
  private JMenuItem miLogin;
  private JMenuItem miMember, miTeacher, miBank, miEmployee;
  private JMenuItem miPassRehearsal, miRehearsal, miHistoPass;
  private JMenuItem miHistoRehearsal;
  private JMenuItem miHistoInvoice;
  private JMenuItem miHistoQuote;
  private JMenuItem miMonthPlanning;
  private JMenuItem miGroups;
  private JMenuItem miSaveUISettings;
  private PersonFile dossier, parent;
  private PersonFileTabView personFileView;
  private PersonFileListCtrl memberList;
  private HistoInvoice histoInvoice;
  private OrderLineEditor orderLineEditor;
  private DataConnection dc;
  private static BankBranchIO BANK_BRANCH_IO;
  private boolean savePrefs;
  private final Preferences prefs = Preferences.userRoot().node("/algem/ui");

  public PersonFileEditor() {
    super("Fiche: ");
    dossier = new PersonFile();
  }

  public PersonFileEditor(PersonFile dossier) {
    super("Fiche:" + dossier.getContact().toString());
    this.dossier = dossier;
  }

  public PersonFileEditor(PersonFile dossier, PersonFile parent) {
    this(dossier);
    this.parent = parent;
  }

  /**
   *
   * @param dossier
   * @deprecated
   */
  public void setDossierPersonne(PersonFile dossier) {
    this.dossier = dossier;
    personFileView.contentsChanged(new PersonFileEvent(dossier, PersonFileEvent.CONTENTS_CHANGED));
  }

  /**
   *
   * @return person id as string
   */
  @Override
  public String getSID() {
    return String.valueOf(dossier.getId());
  }

  public int getDossierID() {
    return dossier.getId();
  }

  public PersonFileTabView getPersonView() {
    return personFileView;
  }

  /**
   * Called by {@link net.algem.util.module.GemDesktop#addModule(net.algem.util.module.GemModule) }.
   */
  @Override
  public void init() {

    super.init();

    dc = DataCache.getDataConnection();
    BANK_BRANCH_IO = new BankBranchIO(dc);

    desktop.addGemEventListener(this);
    view = personFileView = new PersonFileTabView(desktop, dossier, this);
    if (parent != null) {//???
      personFileView.setParent(parent);
    }
    personFileView.init();

    initMenus();

    // search for linked members
    checkAttachedMembers();

    // search for payer
    checkPayer();

    // search for groups
    if (personFileView.addGroupsTab(false)) {
      miGroups.setEnabled(false);
    }

    // search of number of memberships
    checkMembershipNumber();
  }

  /**
   * @param evt
   * @see PersonFileListener
   *
   */
  @Override
  public void contentsChanged(PersonFileEvent evt) {
    System.out.println("PersonFileEditor.contentChanged:" + evt);
    if (PersonFileEvent.MEMBER_ADDED == evt.getType()) {
      PersonFile d = ((PersonFile) evt.getSource());
      addMenuDossier("Adhérent", d);
    } else if (PersonFileEvent.SUBSCRIPTION_CARD_CHANGED == evt.getType()) {
      personFileView.contentsChanged(evt);
    }

  }

  /**
   * @param evt
   * @see GemEventListener
   */
  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof InvoiceEvent) {
      Invoice f = ((InvoiceEvent) evt).getInvoice();
      if (dossier.getId() == f.getPayer() || dossier.getId() == f.getMember()) {
        if (orderLineEditor != null) {
          orderLineEditor.postEvent(evt);
        }
      }
    } // TODO rehearsal orderline update
    /*if (evt instanceof OrderLineEvent) {
      OrderLine ol = ((OrderLineEvent) evt).getOrder();
      if (dossier.getId() == ol.getPayer() || dossier.getId() == ol.getMember()) {
        if (orderLineEditor == null) {
          ((GemButton) evt.getSource()).setEnabled(false);
        }
        orderLineEditor = null;
        personFileView.removeTab(orderLineEditor);

        dlgSchedulePayment();
      }

    }*/

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();

    Object src = evt.getSource();
    // On sauve au préalable l'éventuel nouveau contact avant d'executer les actions des menus.
    if (dossier.getId() == 0
            && !arg.equals(GemCommand.SAVE_CMD)
            && !arg.equals(GemCommand.CLOSE_CMD)) {

      updatePersonFile();
      String msg = dossier.hasErrors();
      if (msg != null) {
        new ErrorDlg(personFileView, msg);
        return;
      }
      msg = checkContact(MessageUtil.getMessage("contact.save.warning"));
      if (MessagePopup.confirm(personFileView, msg, BundleUtil.getLabel("Contact.save.label"))) {
        dossier.setOldValues();// important
        save();
        personFileView.setID(dossier.getId());
      } else {
        return;
      }
    }
    if ("Member.reading".equals(arg)) {
      personFileView.addMemberTab();
      miMember.setEnabled(false);
    }
    else if ("Teacher".equals(arg)) {
      personFileView.addTeacherTab(this);
      miTeacher.setEnabled(false);
    } else if ("TeacherDelete".equals(arg)) {
      if (!dataCache.authorize("Teacher.suppression.auth")) {
        MessagePopup.warning(view, MessageUtil.getMessage("teacher.delete.authorization.warning"));
        return;
      }
      try {
        deleteTeacher();
       } catch (SQLException ex) {
        GemLogger.logException(ex);
      }
    } else if ("Person.group.tab".equals(arg) || "Groups".equals(arg)) {
      if (personFileView.addGroupsTab(true)) {
        miGroups.setEnabled(false);
      }
    } else if ("Person.bank.editing".equals(arg)) {
      personFileView.addBankTab();
      miBank.setEnabled(false);
      // export dossier
    } else if ("Member.file".equals(arg)) {
      showPersonFile();
    } else if ("Member.card".equals(arg)) {
      MemberCardEditor cardEditor = new MemberCardEditor(desktop, dossier);
      cardEditor.edit();
    } else if (GemCommand.NOTE_CMD.equals(arg)) {
      NoteDlg nd = new NoteDlg(desktop);
      nd.loadNote(personFileView.getNote(), dossier.getContact());
      nd.show();
      personFileView.setNote(nd.getNote());
    } else if ("Employee".equals(arg)) {
      personFileView.addEmployeeTab();
      miEmployee.setEnabled(false);
    } else if ("EmployeeDelete".equals(arg)) {
      personFileView.deleteEmployee();
      miEmployee.setEnabled(dataCache.authorize("Employee.editing.auth"));
    } else if ("Technician".equals(arg)) {
      updateTechnician(dossier.getId());
    } else if ("Member.schedule.payment".equals(arg)) {
      ((GemButton) evt.getSource()).setEnabled(false);
      dlgSchedulePayment();
    } else if ("Payer.debiting".equals(arg)) {
      try {
        showMandates();
      }  catch (DDMandateException ex) {
        MessagePopup.error(view, ex.getMessage());
      }
    } else if ("Login.creation".equals(arg)) {
      dlgLogin();
    } else if ("Lié".equals(arg)) {
      Person p = new Person(dossier.getContact().getName());
      PersonFile d = new PersonFile(new Contact(p));
      PersonFileEditor ed = new PersonFileEditor(d, dossier);
      d.addPersonFileListener(this);
      desktop.addModule(ed);
    } else if ("Teacher.break".equals(arg)) {
      TeacherBreakDlg dlg = new TeacherBreakDlg(desktop, dossier.getId());
      dlg.entry();
      if (dlg.isValidation()) {
        dlg.save();
      } else if (dlg.getError() != null) {
        MessagePopup.information(desktop.getFrame(), dlg.getError());
      }
    } else if ("Teacher.presence".equals(arg)) {
      AttendanceSheetDlg dlg = new AttendanceSheetDlg(personFileView, dataCache, dossier.getTeacher());
    } else if ("Employee.hours".equals(arg)) {
      String fileName = "heures_" + dossier.getContact().getFirstName() + "_" + dossier.getContact().getName() + ".txt";
      HourEmployeeDlg hoursDlg = new HourEmployeeDlg(desktop.getFrame(), dossier.getId(), dataCache);
      hoursDlg.init(fileName, dc);
    } // clic sur le bouton/icone Fermer la fiche
    else if (GemCommand.CLOSE_CMD.equals(arg)) { // GemCommand.
      savePrefs = (evt.getModifiers() & Event.SHIFT_MASK) == Event.SHIFT_MASK;
      try {
        close();
      } catch (GemCloseVetoException i) {
        System.err.println("GemCloseVetoException");
      }
    } // clic sur le bouton Enregistrer
    else if (GemCommand.SAVE_CMD.equals(arg)) {
      savePersonFile();
    }
    else if ("Contact.suppression".equals(arg)) {
      suppressPerson();
    } else if ("Person.pass.scheduling".equals(arg)) {
      MemberRehearsalPassCtrl dlg = new MemberRehearsalPassCtrl(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Rehearsal.pass.label"));
      miPassRehearsal.setEnabled(false);
    } else if ("Person.rehearsal.scheduling".equals(arg)) {
      MemberRehearsalCtrl dlg = new MemberRehearsalCtrl(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Rehearsal.label"));
      miRehearsal.setEnabled(false);
      personFileView.activate(false, "Person.rehearsal.scheduling");
    } else if ("Rehearsal.history".equals(arg)) {
      personFileView.addRehearsalHistoryTab();
      miHistoRehearsal.setEnabled(false);
    } else if ("Histo.pass".equals(arg)) {
      desktop.setWaitCursor();
      if (personFileView.addHistoSubscriptionTab()) {
        miHistoPass.setEnabled(false);
      } else {
        MessagePopup.warning(personFileView, MessageUtil.getMessage("no.subscription.warning"));
      }
      desktop.setDefaultCursor();
    } else if (HistoSubscriptionCard.CLOSE_CMD.equals(arg)) {
      personFileView.removeSubscriptionTab();
      miHistoPass.setEnabled(true);
    } else if ("Menu.month.schedule".equals(arg)) {
      MonthScheduleTab dlg = new MonthScheduleTab(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Menu.month.schedule.label"));
      miMonthPlanning.setEnabled(false);
    } else if ("AdherentRepetitionPonctuelle.Abandon".equals(arg) || "AdherentRepetitionPonctuelle.Validation".equals(arg)) {
      personFileView.removeTab((MemberRehearsalCtrl) src);
      personFileView.activate(true, "Person.rehearsal.scheduling");
      miRehearsal.setEnabled(true);
    } else if ("AdherentForfaitRepetition.Abandon".equals(arg) || "AdherentForfaitRepetition.Validation".equals(arg)) {
      personFileView.removeTab((MemberRehearsalPassCtrl) src);
      miPassRehearsal.setEnabled(true);
    } else if ("MemberEnrolmentCreate".equals(arg)) {
      MemberEnrolmentDlg dlg = new MemberEnrolmentDlg(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Course.registration.label"));
      personFileView.activateEnrolment(false);// désactivation du bouton nouvelle inscription
    } else if ("MemberEnrolmentCancel".equals(arg) || "MemberEnrolmentValidation".equals(arg)) {
      personFileView.removeTab((MemberEnrolmentDlg) src);
      personFileView.activateEnrolment(true);//réactivation du bouton de nouvelle inscription
    } else if ("HistoFacture.Abandon".equals(arg)) {
      personFileView.removeTab((HistoInvoice) src);
      miHistoInvoice.setEnabled(true);
    } else if ("HistoDevis.Abandon".equals(arg)) {
      personFileView.removeTab((HistoQuote) src);
      miHistoQuote.setEnabled(true);
    } else if ("CtrlAbandonFacture".equals(arg)) {
      personFileView.removeTab((InvoiceEditor) src);
    } else if ("CtrlAbandonDevis".equals(arg)) {
      personFileView.removeTab((QuoteEditor) src);
    } else if ("HistoRepet.Abandon".equals(arg)) {
      miHistoRehearsal.setEnabled(true);
      personFileView.removeTab((HistoRehearsalView) src);
    } else if (DDPrivateMandateCtrl.CLOSE_COMMAND.equals(arg)) {
      personFileView.removeTab((DDPrivateMandateCtrl) src);
      personFileView.activate(true, "Payer.debiting");
    } else if ("Invoice.history".equals(arg)) {
      int payer = getPayer();
      if (payer > 0) {
        histoInvoice = addHistoInvoice(dossier.getId());
        if (histoInvoice != null) {
          histoInvoice.addActionListener(this);
          personFileView.addTab(histoInvoice, FileTabView.HISTO_INVOICE_TAB_TITLE);
          miHistoInvoice.setEnabled(false);
        } else {
          MessagePopup.information(view, MessageUtil.getMessage("no.invoice.recorded"));
        }
      }
    } else if (FileTabView.INVOICE_TAB_TITLE.equals(arg)) {
      addInvoice(src);
    } else if (FileTabView.ESTIMATE_TAB_TITLE.equals(arg)) {
      addQuotation(src);
    } else if ("Quotation.history".equals(arg)) {
        int payer = getPayer();
        if (payer > 0) {
          HistoQuote histoQuote = getHistoQuotation(dossier.getId());
          if (histoQuote != null) {
            if (histoQuote.isLoaded()) {
              histoQuote.addActionListener(this);
              personFileView.addTab(histoQuote, FileTabView.HISTO_ESTIMATE_TAB_TITLE);
              miHistoQuote.setEnabled(false);
            } else {
              MessagePopup.information(view, MessageUtil.getMessage("no.quote.recorded"));
            }
          }
        }
      } else if (CloseableTab.CLOSE_CMD.equals(arg)) {
      closeTab(src);
    } else if (src == miSaveUISettings) {
      storeUISettings();
      Toast.showToast(desktop, getUIInfo());
    } else {
      super.actionPerformed(evt);
    }

  }

  /**
   * Deletes this contact as a teacher.
   * Only teachers who have had no course should be removed.
   * @throws SQLException
   */
  private void deleteTeacher() throws SQLException {
    TeacherIO dao = (TeacherIO) DataCache.getDao(Model.Teacher);
    int c = dao.hasSchedules(dossier.getId());
    if (c > 0) {
      MessagePopup.warning(view, MessageUtil.getMessage("teacher.delete.warning", c));
    } else {
      dao.delete(dossier.getId());
      dataCache.remove(dossier.getTeacher());
      desktop.postEvent(new TeacherEvent(this, GemEvent.SUPPRESSION, dossier.getTeacher()));
      dossier.removeTeacher();
      personFileView.removeTeacher();
      miTeacher.setEnabled(true);
    }
  }

  /**
   * Opens if exist the list of direct debit mandates of the payer.
   * @throws DDMandateException
   */
  private void showMandates() throws DDMandateException {
    if (dossier == null || dossier.getRib() == null) {
      MessagePopup.error(personFileView, MessageUtil.getMessage("payer.invalid.warning"));
      return;
    }
    dossier.setRib(personFileView.getRibFile());// get rib from view
    if (dossier.hasChanged()) {// non enregistrement éventuel du rib
      MessagePopup.warning(personFileView, MessageUtil.getMessage("rib.error.printing"));
      return;
    }
    DirectDebitService ddService = DirectDebitService.getInstance(dc);
    int payer = dossier.getMember() == null ? dossier.getId() : dossier.getMember().getPayer();
    DDMandate dd = ddService.getMandate(payer);
    if (dd == null) {
      if (payer > 0 && MessagePopup.confirm(view, MessageUtil.getMessage("direct.debit.create.mandate.confirmation", payer))) {
        ddService.createMandate(payer);
      } else {
        return;
      }
    }
    personFileView.addMandates(ddService, payer);
  }

  /**
   * Gets the payer id.
   *
   * @return an integer representing the payer
   */
  private int getPayer() {
    int payer = 0;
    if (dossier.getMember() != null) {
      payer = dossier.getMember().getPayer();
    }
    return payer == 0 ? dossier.getId() : payer;
  }

  /**
   * Gets data view.
   */
  private void updatePersonFile() {

    dossier.setContact(personFileView.getContact());

    if (dossier.getMember() == null) {// nouvel onglet
      dossier.addMember(personFileView.getMemberFile());
    } else {
      dossier.setMember(personFileView.getMemberFile());
    }

    dossier.setTeacher(personFileView.getTeacher());
    dossier.setRib(personFileView.getRibFile());

    BankBranch a = personFileView.getBranchBank();
    if (a != null && a.getBicCode() != null && !a.getBicCode().isEmpty()) {
      try {
        BankBranch b = BANK_BRANCH_IO.findId(a.getId());
        if (b != null && !a.getBicCode().equals(b.getBicCode())) {
          BANK_BRANCH_IO.update(a.getId(), a.getBicCode());
        }
      } catch (SQLException ex) {
        GemLogger.log(Level.WARNING, ex.getMessage());
      }
    }

  }

  void updateTechnician(int idper) {
    try {
      String query = "INSERT INTO " + EmployeeIO.TYPE_TABLE + " VALUES(" + dossier.getId() + ")";
      dc.executeUpdate(query);
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
  }

  /**
   * Inserts or updates dossier.
   *
   * @return true if no errors
   */
  boolean save() {
     if (!dataCache.authorize("Contact.modification.auth")) {
      MessagePopup.information(personFileView, MessageUtil.getMessage("rights.exception"));
      return false;
    }
    int currentId = dossier.getId();
    try {
      dc.setAutoCommit(false);
      if (personFileView.isNewBank()) {
        Bank b = personFileView.getBank();
        if (b.isValid()) {
          if (BankIO.findCode(b.getCode(), dc) == null) {
            BankIO.insert(b, dc);
          }
        }
      }

      if (personFileView.isNewBranchOfBank()) {
        BankBranch a = personFileView.getBranchBank();
        if (a != null && a.isValid()) {
          BANK_BRANCH_IO.insert(a);
          personFileView.setBranchBank(a); // maj id guichet
        }
      }

      personFileView.updateEmployee();

      Vector<String> logs = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).update(dossier);
      if (logs.contains(PersonFileIO.CONTACT_CREATE_EVENT)) {
        dataCache.add(dossier.getContact());
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.CONTACT, dossier.getContact()));
      }
      if (logs.contains(PersonFileIO.CONTACT_UPDATE_EVENT)) {
        dataCache.update(dossier.getContact());
        desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.CONTACT, dossier.getContact()));
      }
      if (logs.contains(PersonFileIO.TEACHER_CREATE_EVENT)) {
        dataCache.add(dossier.getTeacher());
        desktop.postEvent(new TeacherEvent(this, GemEvent.CREATION, dossier.getTeacher()));
      }
      if (logs.contains(PersonFileIO.TEACHER_UPDATE_EVENT) && dossier.getTeacher() != null) {
        dataCache.update(dossier.getTeacher()); // mise à jour liste professeurs
        desktop.postEvent(new TeacherEvent(this, GemEvent.MODIFICATION, dossier.getTeacher()));
      }
      if (logs.contains(PersonFileIO.MEMBER_CREATE_EVENT)) {
        dataCache.add(dossier.getMember());
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.MEMBER, dossier.getMember()));
      }
      if (logs.contains(PersonFileIO.MEMBER_UPDATE_EVENT)) {
        dataCache.update(dossier.getMember());
        desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.MEMBER, dossier.getMember()));
      }
      dc.commit();
    } catch (SQLException e1) {
      dc.rollback();
      dossier.setId(currentId);
      GemLogger.logException(e1.getMessage(), e1);
      JOptionPane.showMessageDialog(personFileView,
              "identifiant = " + dossier.getId() + "<br>" + e1,
              "Erreur mise à jour dossier :",
              JOptionPane.ERROR_MESSAGE);
      return false;
    } catch(DDMandateException ex){
      MessagePopup.warning(view, ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }

    return true;
  }

  private boolean hasChanged() {
    return dossier.hasChanged() || personFileView.hasEmployeeChanged();
  }

  void dlgLogin() {
    UserService service = dataCache.getUserService();
    personFileView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    UserCreateDlg dlg = new UserCreateDlg(personFileView, "login", dossier.getContact());
    User u = service.findId(dossier.getId());
    if (u != null) {
      dlg.setUser(u);
    }
    personFileView.setCursor(Cursor.getDefaultCursor());
    dlg.display();
    if (!dlg.isValidation()) {
      return;
    }
    String error = null;
    try {

      if (u == null) {
        u = dlg.getUser();
        service.create(u);
        dataCache.add(u);
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.USER, u));
        MessagePopup.information(view, MessageUtil.getMessage("user.creation.success"));
      } else {
        User nu = dlg.getUser();
        if (service.update(nu, u)) {
          dataCache.update(nu);
          desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.USER, nu));
          MessagePopup.information(view, MessageUtil.getMessage("modification.success.label"));
        }
      }
    } catch (SQLException e) {
      error = MessageUtil.getMessage("user.creation.failure");
      GemLogger.logException(error, e, personFileView);
      MessagePopup.warning(view, error);
    } catch (UserException ue) {
      error = MessageUtil.getMessage("user.pass.creation.failure");
      GemLogger.logException(error, ue);
      MessagePopup.warning(view, error);
    }
  }

  void dlgSchedulePayment() {
    if (dossier == null) {
      return;
    }

    personFileView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    OrderLineTableModel orderTableModel = new OrderLineTableModel();
    Person p = dossier.getContact();

    // chargement des échéances
    //un adhérent a pu avoir plusieurs payeurs
    if (dossier.getMember() != null) {
      // si l'adhérent n'est pas son propre payeur
      if (dossier.getMember().getPayer() != dossier.getId()) {
        p = ((PersonIO) DataCache.getDao(Model.Person)).findById(dossier.getMember().getPayer());// le payeur de l'adherent
        if (p == null) {
          p = dossier.getContact();
        }
        orderTableModel.load(OrderLineIO.findByMember(dossier.getId(), p.getId(), dc));
      } else {
        orderTableModel.load(OrderLineIO.findByMemberOrPayer(dossier.getId(), p.getId(), dc));
      }
    } else {
      orderTableModel.load(OrderLineIO.findByMemberOrPayer(dossier.getId(), p.getId(), dc));
    }

    addSchedulePayment(orderTableModel, p);

    personFileView.setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Ajout d'un item vers l'adhérent lié dans le menu fichier.
   *
   * @param _label le titre de l'item
   * @param _dossier
   * @deprecated
   */
  public void addMenuDossier(String _label, PersonFile _dossier) {
    PersonFileMenuItem m = new PersonFileMenuItem(_label, _dossier);
    m.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent evt) {
        PersonFile d = ((PersonFileMenuItem) evt.getSource()).getPersonFile();
        PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(d.getId());
        if (m == null) {
          PersonFileEditor editeur = new PersonFileEditor(d);
          desktop.addModule(editeur);
        } else {
          desktop.setSelectedModule(m);
        }
      }
    });
    mFile.add(m);
  }

  private void addPayerFile(String _label, final PersonFile _dossier) {
    GemButton b = personFileView.addIcon("Member.payer");
    b.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent evt) {
        PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(_dossier.getId());
        if (m == null) {
          PersonFileEditor editor = new PersonFileEditor(_dossier);
          desktop.addModule(editor);
        } else {
          desktop.setSelectedModule(m);
        }
      }
    });
  }

  /**
   * Adds schedule payment editor.
   * @param t table model
   * @param p payer
   */
  private void addSchedulePayment(OrderLineTableModel t, Person p) {

    orderLineEditor = new OrderLineEditor(desktop, t);
    orderLineEditor.init();
    orderLineEditor.addActionListener(this);
    if (dossier.getMember() == null) {
      orderLineEditor.setPayerId(dossier.getId());
    } else {
      orderLineEditor.setMemberId(dossier.getId());
      orderLineEditor.setPayerId(dossier.getMember().getPayer());
    }

    if (dossier.getMember() == null || dossier.getMember().getPayer() == dossier.getId()) {
      orderLineEditor.setLabel(dossier.getContact().getFirstnameName());
    } else {
      String org = p.getOrganization();
      orderLineEditor.setLabel(org != null && org.trim().length() > 0 ? org : p.getFirstnameName());
    }
    personFileView.addTab(orderLineEditor, BundleUtil.getLabel("Person.schedule.payment.tab.label"));
  }

  private void initMenus() {
    mBar = new JMenuBar();
    mFile = new JMenu(BundleUtil.getLabel("Menu.file.label"));
    miDelete = getMenuItem("Contact.suppression");
    mOptions = createJMenu("Menu.options");

    mHelp = new JMenu(BundleUtil.getLabel("Menu.help.label"));
    miAbout = new JMenuItem(BundleUtil.getLabel("About.label"));
    miDoc = new JMenuItem(BundleUtil.getLabel("Menu.doc.label"));

    mFile.add(miDelete);

    mOptions.add(miMember = getMenuItem("Member.reading"));
    miMember.setToolTipText(BundleUtil.getLabel("Member.tab.tip"));
    mOptions.add(miTeacher = getMenuItem("Teacher"));
    miTeacher.setToolTipText(BundleUtil.getLabel("Teacher.tab.tip"));
    mOptions.add(miEmployee = getMenuItem("Employee"));
    miEmployee.setEnabled(dataCache.authorize("Employee.editing.auth"));
    miEmployee.setToolTipText(BundleUtil.getLabel("Employee.tab.tip"));

    mOptions.add(miGroups = getMenuItem("Groups"));
    //Désactivation conditionnelle des menus Adherent, Prof et Bank
    if (dossier.getMember() != null) {
      miMember.setEnabled(false);
    }
    if (dossier.getTeacher() != null) {
      miTeacher.setEnabled(false);
    }

    mOptions.addSeparator();
    mOptions.add(miRehearsal = getMenuItem("Person.rehearsal.scheduling"));
    mOptions.add(miPassRehearsal = getMenuItem("Person.pass.scheduling"));
    miHistoPass = new JMenuItem(BundleUtil.getLabel("Subscriptions.label"));

    miHistoPass.setActionCommand("Histo.pass");
    miHistoPass.addActionListener(this);
    mOptions.add(miHistoPass);

    mOptions.addSeparator();
    mOptions.add(miHistoRehearsal = getMenuItem("Rehearsal.history"));
    mOptions.add(miMonthPlanning = getMenuItem("Menu.month.schedule"));

    mOptions.addSeparator();
    mOptions.add(miHistoInvoice = getMenuItem("Invoice.history"));
    mOptions.add(miHistoQuote = getMenuItem("Quotation.history"));

    mOptions.addSeparator();
    mOptions.add(miBank = getMenuItem("Person.bank.editing"));
    if (dossier.getRib() != null) {
      miBank.setEnabled(false);
    }
    mOptions.addSeparator();
    mOptions.add(miLogin = getMenuItem("Login.creation"));
    mOptions.addSeparator();
    mOptions.add(miSaveUISettings = getMenuItem("Store.ui.settings"));
    mHelp.add(miAbout);
    mHelp.add(miDoc);

    mBar.add(mFile);
    mBar.add(mOptions);
    mBar.add(Box.createHorizontalGlue());
    mBar.add(mHelp);

    personFileView.setJMenuBar(mBar);
  }

  /**
   * Checks and updates if necessary the number of memberships.
   *
   */
  private void checkMembershipNumber() {
    int a = 0;
    int nba = 0;
    if (dossier.getMember() != null) {
      a = dossier.getMember().getMembershipCount();
      try {
        nba = AccountUtil.getMemberShips(dossier.getMember().getId(), dc);
      } catch (SQLException ex) {
        nba = a;
      }
      // en cas de mise en service tardive du logiciel (des adhésions ayant été enregistrées indépendamment d'algem),
      // le nombre d'adhésions enregistré dans les échéances pourra être < au nombre réel.
      personFileView.setMemberShipNumber((nba > a) ? nba : a);//mise à jour de la view
    }
  }

  /**
   * Payer identification.
   */
  private void checkPayer() {
    if (dossier.getMember() != null && dossier.getMember().getPayer() != dossier.getId()) {
      Contact c = ContactIO.findId(dossier.getMember().getPayer(), dc);
      if (c != null) {
        PersonFile d = new PersonFile(c);
        try {
          ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(d);
        } catch (SQLException ex) {
          GemLogger.logException(MessageUtil.getMessage("record.completion.exception"), ex);
        }
        //addMenuDossier("payeur", d);
        addPayerFile("Payeur", d);// ajout jm 2.0ma
      } else { //Attention aux cas où le payeur auquel est lié l'adhérent n'existe pas
        MessagePopup.information(personFileView,
                MessageUtil.getMessage("not.existing.payer.link", dossier.getMember().getPayer()));
        personFileView.setParent(dossier);
      }
    }
  }

  /**
   * Opens a tab for linked members.
   */
  private void checkAttachedMembers() {
    if (setMemberList() > 0) {
      //final GemButton b = personFileView.addLinkedMembersIcon();
      final GemButton b = personFileView.addIcon("Payer.members");
      b.addActionListener(new ActionListener()
      {

        public void actionPerformed(ActionEvent evt) {
          ListCtrl list = PersonFileEditor.this.getMemberList();
          MemberListTab memberList = new MemberListTab(desktop, list);
          personFileView.addTab(memberList, BundleUtil.getLabel("Person.linked.members.tab.label"));
          b.setEnabled(false);
        }
      });
    }
  }

  /**
   * Gets the number of linked members.
   */
  private int setMemberList() {
    Vector<PersonFile> v = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findMembers("WHERE payeur = " + dossier.getId() + " AND id != payeur");
    memberList = new PersonFileListCtrl();

    for (int i = 0; i < v.size(); i++) {
      PersonFile d = v.elementAt(i);
      ContactIO.complete(d.getContact(), dc);
    }
    if (v != null && v.size() > 0) {
      memberList.addBlock(v);
      return v.size();
    }
    return 0;
  }

  /**
   * Gets the list of linked members.
   *
   * @return a {@link ListCtrl } instance
   */
  private ListCtrl getMemberList() {
    return memberList;
  }

  /**
   * Checking homonyms.
   */
  private boolean contactExists(Contact c) {
    boolean found = false;
    String where = "";
    if (c.getName() != null && c.getName().length() > 0) {
      where = " WHERE lower(nom) = E'" + TableIO.escape(c.getName().toLowerCase()) + "'";
      if (c.getFirstName() != null && c.getFirstName().length() > 0) {
        where +=  " AND lower(prenom) = E'" + TableIO.escape(c.getFirstName().toLowerCase()) + "'";
      }
      if (ContactIO.findId(where, dc) != null) {
        return true;
      }
    }

    if (c.getOrganization() != null && c.getOrganization().length() > 0) {
      where = " WHERE lower(organisation) = E'" + TableIO.escape(c.getOrganization().toLowerCase()) + "'";
      found = ContactIO.findId(where, dc) != null;
    }
    return found;
  }

  /**
   * Checks if a contact exists.
   * If he is new, we should check that it does not already exist.
   *
   * @return a message
   */
  private String checkContact(String message) {
    if (dossier.getContact().getId() == 0 && contactExists(dossier.getContact())) {
      return MessageUtil.getMessage("contact.create.warning");
    }
    return message;
  }

  /**
   * Opens an external document with the contact's main data.
   */
  private void showPersonFile() {
    String path = null;
    ExportMemberRTF rtfExport = null;
    try {
      rtfExport = new ExportMemberRTF(desktop, System.getProperty("java.io.tmpdir"), dossier);
      rtfExport.edit();

      ExportPayeurRTF fic2 = new ExportPayeurRTF(System.getProperty("java.io.tmpdir"), dossier, dc);
      path = fic2.getPath();
      fic2.edit();
      // jm	java Desktop rtf handler
      DesktopOpenHandler handler = new DesktopOpenHandler();
      handler.open(rtfExport.getPath(), path);
    } catch (DesktopHandlerException de) {
      GemLogger.log(de.getMessage());
    } catch (FileNotFoundException fe) {
      GemLogger.log(fe.getMessage());
      JOptionPane.showMessageDialog(view, path, fe.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
  }

  private void savePersonFile() {

    Object[] backup = dossier.backUpOldValues();
    updatePersonFile();
    String msg = dossier.hasErrors();
    if (msg != null) {
      MessagePopup.error(personFileView, msg);
      return;
    }
    if (hasChanged()) {
      msg = checkContact(MessageUtil.getMessage("update.warning"));
      if (MessagePopup.confirm(personFileView, msg, "Enregistrement du dossier:" + dossier.getId())) {
        save();
        dossier.setOldValues();
        personFileView.setID(dossier.getId());//mise à jour de la view dans le cas d'un nouveau contact(2.0c)
        //dossier.clearOldValues();
        // jm 2.0ma ajout automatique des onglets inscription et suivi lors de l'enregistrement d'un nouvel adhérent
        if (dossier.getMember() != null && personFileView.getMemberEnrolment() == null) {
          personFileView.completeMember();
        }
      } else {
        dossier.restoreOldValues(backup);
        personFileView.reload(dossier);
      }
      //vuePersonne.setSaveState(true);
    } else {
      dossier.restoreOldValues(backup);
      JOptionPane.showMessageDialog(personFileView,
              MessageUtil.getMessage("no.update.info"),
              BundleUtil.getLabel("Warning.label"),
              JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Contact suppression.
   * Suppression is checked {@link ContactIO#checkDelete(net.algem.ctrl.DataCache, net.algem.modele.contact.Contact) }
   * }
   */
  private void suppressPerson() {
    // interdire la suppression aux personnes non autorisées
    if (!dataCache.authorize("Contact.suppression.auth")) {
      MessagePopup.information(personFileView, MessageUtil.getMessage("rights.exception"));
      return;
    }
    if (MessagePopup.confirm(personFileView, MessageUtil.getMessage("contact.delete.confirmation", dossier.getId()))) {
      try {
        dc.setAutoCommit(false);
        new ContactIO(dc).delete(dossier.getContact());
        dc.commit();
        try {
          view.close();
          view.removeActionListener(this);
          desktop.removeGemEventListener(this);
          desktop.removeModule(this);
        } catch (GemCloseVetoException e) {
          GemLogger.logException(e);
        }
      } catch (ContactDeleteException ex) {
        MessagePopup.warning(view, ex.getMessage());
      } catch (SQLException ex) {
        GemLogger.logException("delete contact", ex);
        dc.rollback();
      } finally {
        dc.setAutoCommit(true);
      }
    }
  }

  /**
   * Closes the module.
   * Click on closing icon.
   *
   * @throws net.algem.util.model.GemCloseVetoException
   */
  @Override
  public void close() throws GemCloseVetoException {
    updatePersonFile();
    String msg = dossier.hasErrors();
    if (msg != null) {
      MessagePopup.error(personFileView, msg);
    } else {
      if (hasChanged()) {
        msg = checkContact(MessageUtil.getMessage("update.warning"));
        if (MessagePopup.confirm(personFileView, msg, MessageUtil.getMessage("closing.record.info", dossier.getId()))) {
          save();
        }
      } else {
        System.out.println(MessageUtil.getMessage("no.update.info"));
      }
    }
    if (savePrefs) {
      storeUISettings();
    }
    personFileView.clear();
    closeModule();

  }

  private void closeTab(Object source) {
    String classname = null;

    if (source instanceof String) {
      classname = (String) source;
    } else {
      return;
    }

    if (RibView.class.getSimpleName().equals(classname)) {
      savePersonFile();// on enregistre (le rib) par précaution
      personFileView.clearRib();
      miBank.setEnabled(true);
    } else if (MonthScheduleTab.class.getSimpleName().equals(classname)) {
      miMonthPlanning.setEnabled(true);
    } else if (TeacherEditor.class.getSimpleName().equals(classname)) {
      personFileView.closeTeacher();
      miTeacher.setEnabled(true);
    } else if (MemberEditor.class.getSimpleName().equals(classname)) {
      personFileView.removeTab(personFileView.getEnrolmentView());
      personFileView.removeTab(personFileView.getMemberFollowUp());
      personFileView.removeMemberIcons();
      miMember.setEnabled(true);
    } else if (OrderLineEditor.class.getSimpleName().equals(classname)) {
      personFileView.activate(true, "Member.schedule.payment");
      desktop.removeGemEventListener(orderLineEditor); //XXX unused
    } else if (HistoRehearsalView.class.getSimpleName().equals(classname)) {
      miHistoRehearsal.setEnabled(true);
    } else if (PersonFileGroupView.class.getSimpleName().equals(classname)) {
      miGroups.setEnabled(true);
    } else if (MemberEnrolmentDlg.class.getSimpleName().equals(classname)) {
      personFileView.activateEnrolment(true);
    } else if (MemberRehearsalCtrl.class.getSimpleName().equals(classname)) {
      miRehearsal.setEnabled(true);
      personFileView.activate(true, "Person.rehearsal.scheduling");
    } else if (MemberRehearsalPassCtrl.class.getSimpleName().equals(classname)) {
      miPassRehearsal.setEnabled(true);
    } else if (HistoInvoice.class.getSimpleName().equals(classname)) {
      miHistoInvoice.setEnabled(true);
    } else if (HistoQuote.class.getSimpleName().equals(classname)) {
      miHistoQuote.setEnabled(true);
    } else if (MemberListTab.class.getSimpleName().equals(classname)) {
      personFileView.activate(true, "Payer.members");
    } else if (EmployeeEditor.class.getSimpleName().equals(classname)) {
      miEmployee.setEnabled(dataCache.authorize("Employee.editing.auth"));
    } else if ("net.algem.contact.PersonFileTabView$1".equals(classname)) {
      personFileView.activate(true, "Payer.debiting");
    } else if (HistoSubscriptionCard.class.getSimpleName().equals(classname)) {
      miHistoPass.setEnabled(true);
    }
  }

  private void closeModule() throws GemCloseVetoException {
    view.close();
    view.removeActionListener(this);
    desktop.removeGemEventListener(this);
    desktop.removeGemEventListener(personFileView);
    personFileView.clear();
    desktop.removeModule(this);
  }

  /**
   * Adds a tab for invoice editing / creating.
   *
   * @param source
   */
  private void addInvoice(Object source) {

    Invoice inv = new Invoice(dossier, dataCache.getUser());
    inv.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey())));
    if (source != null && source instanceof OrderLineEditor) {
      BillingUtil.setInvoiceOrderLines(inv, ((OrderLineEditor) source).getInvoiceSelection());
    }

    InvoiceEditor editor = new InvoiceEditor(desktop, billingService, inv);
    editor.addActionListener(this);
    editor.load();
    personFileView.addTab(editor, FileTabView.INVOICE_TAB_TITLE);
  }

  /**
   * Quote editing tab.
   *
   * @param source not used
   */
  private void addQuotation(Object source) {
    Quote q = new Quote(dossier, dataCache.getUser());
    q.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey())));
    /*
     * if (source != null && source instanceof OrderLineEditor) {
     * AccountUtil.setQuoteOrderLines(d, ((OrderLineEditor)
     * source).getInvoiceSelection()); }
     */
    QuoteEditor editor = new QuoteEditor(desktop, billingService, q);
    editor.addActionListener(this);
    editor.load();
    personFileView.addTab(editor, FileTabView.ESTIMATE_TAB_TITLE);
  }

  @Override
  public void storeUISettings() {
    Rectangle bounds = getView().getBounds();
    prefs.putInt("personfileeditor.w", bounds.width);
    prefs.putInt("personfileeditor.h", bounds.height);
  }

  @Override
  public String getUIInfo() {
    Dimension d = view.getSize();
    return BundleUtil.getLabel("New.size.label") + " : " + d.width + "x" + d.height;
  }
}
