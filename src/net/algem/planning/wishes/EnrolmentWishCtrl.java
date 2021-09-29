/*
 * @(#)EnrolmentWishCtrl.java	2.17.0p 02/07/19
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
package net.algem.planning.wishes;

import com.itextpdf.text.DocumentException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.PageTemplateIO;
import net.algem.contact.Email;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.member.Member;
import net.algem.contact.teacher.Teacher;
import net.algem.course.Course;
import net.algem.course.CourseChoiceTeacherModel;
import net.algem.edition.WishConfirmationLetter;
import net.algem.planning.CourseSchedule;
import net.algem.planning.CourseScheduleCtrl;
import net.algem.planning.CourseSchedulePrintDetail;
import net.algem.planning.DateFr;
import net.algem.planning.HourRange;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MailUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 * Tableau de réinscription pour Polynotes
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0p
 * @since 2.17.0 13/03/2019
 */
public class EnrolmentWishCtrl implements ActionListener, TableModelListener {

    public static final int DURATION_MINIMUM = 5;
    public static final int MAX_WISHES = 4;

    private final DateTimeFormatter timestampFileNameFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");

    private final GemDesktop desktop;
    private CourseScheduleCtrl csCtrl;

    private final DataCache dataCache;
    private EnrolmentWishView view;
    private EnrolmentWishService wishService;

    private List<EnrolmentWishParticularCourseLine> particularCourses;
    private List<EnrolmentWishGroupCourseLine> groupCourses;
    private List<EnrolmentWish> teacherWishes = new ArrayList();
    private List<EnrolmentWish> groupCourseWishes = new ArrayList();
    private Vector<? extends ScheduleObject> teacherCurrentPlanning = new Vector();
    private boolean teacherLoad;
    private boolean dayLoad;
    
    private EnrolmentWishParticularCourseTableModel particularCourseModel;
    private EnrolmentWishGroupCourseTableModel groupCourseModel;

    public EnrolmentWishCtrl(GemDesktop _desktop) {

        desktop = _desktop;
        dataCache = desktop.getDataCache();

        wishService = new EnrolmentWishService(dataCache);

        view = new EnrolmentWishView(BundleUtil.getLabel("Enrolment.wish.title.label"), wishService, this);

        wishService.setView(view);

        particularCourses = new ArrayList();
        Hour hour = new Hour("09:00");
        for (int i = 0; i <= 12 * 14; i++, hour.incMinute(EnrolmentWishParticularCourseTableModel.MINUTES_PER_ROW)) {
            particularCourses.add(new EnrolmentWishParticularCourseLine(new Hour(hour)));
        }
        particularCourseModel = new EnrolmentWishParticularCourseTableModel(particularCourses);
        
        view.initTableModel(particularCourseModel);
        
        groupCourses = new ArrayList();
        groupCourseModel = new EnrolmentWishGroupCourseTableModel(groupCourses);
        groupCourseModel.addTableModelListener(this);

        Action groupCourseRemoveStudent = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = Integer.valueOf(e.getActionCommand());
                if (MessagePopup.confirm(view, MessageUtil.getMessage("enrolment.wish.delete.confirmation", " de " + groupCourseModel.getValueAt(row, EnrolmentWishGroupCourseTableModel.COLUMN_STUDENT)))) {
                    try {
                        wishService.deleteGroupWish(groupCourseModel.getWish(row));
                        groupCourseModel.removeRow(row);
                    } catch (Exception ex) {
                        GemLogger.logException("EnrolmentWishCtrl.groupCourseRemoveStudent", ex);
                        MessagePopup.warning(view, ex.toString());
                    }
                }
            }
        };

        view.initTableModel(groupCourseModel, groupCourseRemoveStudent);

        view.setLocationRelativeTo(desktop.getFrame());
        view.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            LocalDate ds = view.getReferenceDate().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (ds.getDayOfWeek().getValue() != DayOfWeek.MONDAY.getValue()) {
                MessagePopup.information(view, MessageUtil.getMessage("enrolment.wish.date.error"));
                return;
            }

            if (evt.getActionCommand().equals("tableIncrement")) {
                tableInit();
                dayReload(false);
                tableReload();
            } else if (evt.getActionCommand().equals("teacherChoice")) {
                teacherLoad=true;
                wishService.setCurrentTeacher(view.getTeacherChoice()); //voir
                tableInit();
                teacherReload();
                dayReload(true);
                courseReload();
                tableReload();
                teacherLoad=false;
            } else if (evt.getActionCommand().equals("dayChoice")) {
                if (!teacherLoad) { 
                    dayLoad=true;
                    courseReload();
                    tableInit();
                    dayReload(true);
                    tableReload();
                    dayLoad=false;
                }
            } else if (evt.getActionCommand().equals("particularCourseChoice")) {
                if (!teacherLoad && !dayLoad) {
                    tableInit();
                    dayReload(teacherCurrentPlanning.size() == 0);
                    tableReload();
                }
            } else if (evt.getActionCommand().equals("groupCourseChoice")) {
                CourseSchedulePrintDetail csp = view.getGroupCourseChoice();
                view.setGroupCourseLabels(csp.getDowLabel(), csp.getStart(), csp.getEnd(), csp.getTeacher().getFirstnameName());
                view.setMaxPlaces(csp.getAction().getPlaces());
                groupCourseReload(csp);

            } else if (evt.getActionCommand().equals("EditablePanelDeleteButton")) {
                EnrolmentWishEditablePanel cell = (EnrolmentWishEditablePanel) SwingUtilities.getAncestorOfClass(EnrolmentWishEditablePanel.class, (Component) evt.getSource());
                if (cell.getStudentId() == 0) {
                    return;
                }
                try {
                    if (cell.isChecked()) {
                        throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.selected"));
                    }
                    if (cell.getChoice().isMailConfirmSended()) {
//demande du 9/7/2019           throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.maildone"));
                        MessagePopup.information(cell, MessageUtil.getMessage("enrolment.wish.error.maildone"));
                    }
                    if (MessagePopup.confirm(cell, MessageUtil.getMessage("enrolment.wish.delete.confirmation", " " + cell.getPreference() + " de " + cell.getStudentLabel()))) {
                        if (view.isTableEditing()) {
                            view.stopEditing();
                        }
                        wishService.deleteParticularWish(cell.getChoice());
                        EnrolmentWish w = new EnrolmentWish();
                        particularCourseModel.setValueAt(w, view.getSelectedRow(), view.getSelectedColumn());
                    }

                } catch (SQLException e) {
                    GemLogger.logException("EnrolmentWishCtrl EditablePanelDeleteButton", e);
                    MessagePopup.warning(cell, e.getMessage());
                } catch (EnrolmentWishException e) {
                    MessagePopup.warning(cell, e.getMessage());
                } finally {
                    if (view.isTableEditing()) {
                        view.stopEditing();
                    }
                }
            } else if (evt.getActionCommand().equals("EditablePanelShowButton")) {
                EnrolmentWishEditablePanel cell = (EnrolmentWishEditablePanel) SwingUtilities.getAncestorOfClass(EnrolmentWishEditablePanel.class, (Component) evt.getSource());
                EnrolmentWishFormDlg dlg = null;
                try {
                    if (cell.isChecked()) {
                        throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.selected"));
                    }
                    if (view.getParticularCourseChoice() < 0) {
                        throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.course.id"));
                    }
                    dlg = new EnrolmentWishFormDlg(view, wishService, particularCourseModel.getColumnName(view.getSelectedColumn()), view.getCurrentTeacherLabel(), view.getCurrentCourseLabel(), view.getDayChoiceLabel(), (Hour) particularCourseModel.getValueAt(view.getSelectedRow(), 0));
                    EnrolmentWish w = (EnrolmentWish) particularCourseModel.getValueAt(view.getSelectedRow(), view.getSelectedColumn());
                    if (view.getSelectedColumn() == EnrolmentWishParticularCourseTableModel.COLUMN_SAMEASCURRENT) {
                        EnrolmentCurrent current = (EnrolmentCurrent) particularCourseModel.getValueAt(view.getSelectedRow(), EnrolmentWishParticularCourseTableModel.COLUMN_CURRENT);
                        if (current.isCollectif()) {
                            throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.couse.collective"));
                        }
                        if (current.getStudent() == null) {
                            throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.course.null"));
                        }
                        dlg.setStudent(current.getStudent(), w.getPreference());
                        dlg.setDuration(current.getDuration());
                        //dlg.setOnlyPreferenceEditable(); //demande de Coralie 02/05/2019
                    } else {
                        if (cell.getStudentId() != 0) {
                            int pref = 0;
                            try {
                                pref = Integer.parseInt(cell.getPreference());
                            } catch (NumberFormatException ignore) {
                            }
                            dlg.setStudent(cell.getStudent(), pref);
                        } else {
                            dlg.setStudent(cell.getStudent(), 0);
                        }
                        if (cell.getDuration().toString().equals("     ")) {
                            dlg.setDuration(new Hour("00:00"));
                        } else {
                            dlg.setDuration(cell.getDuration());
                        }
                        dlg.setNote(cell.getNote());
                    }
                    dlg.setVisible(true);
                    if (dlg.isValidation()) {
                        if (dlg.getDuration().toMinutes() < DURATION_MINIMUM) {
                            throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.duration.min", DURATION_MINIMUM));
                        }
                        if (wishService.countStudentWishes(dlg.getStudent().getId(), view.getTeacherChoice(), view.getParticularCourseChoice()) >= MAX_WISHES) {
                            throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.max", MAX_WISHES));
                        }
                        EnrolmentWish w2 = wishService.checkOtherDuration(dlg.getStudent().getId(), dlg.getDuration(), w.getColumn());
                        if (w2 != null) {
                            MessagePopup.information(cell, MessageUtil.getMessage("enrolment.wish.duration.warning", new Object[]{w2.getDuration(), w2.getDayLabel(), w2.getHour()}));
                        }

                        w.setStudent(dlg.getStudent());
                        w.setDuration(dlg.getDuration());
                        w.setPreference((short) dlg.getPreference());
                        w.setNote(dlg.getNote());
                        Hour h = (Hour) particularCourseModel.getValueAt(view.getSelectedRow(), EnrolmentWishParticularCourseTableModel.COLUMN_HOUR);
                        wishService.updateParticularWish(view.getSelectedColumn() - EnrolmentWishParticularCourseTableModel.COLUMN_SAMEASCURRENT, h, w);
                        cell.setWish(w);
                    }
                    if (view.isTableEditing()) {
                        view.stopEditing();
                    }
                } catch (SQLException e) {
                    GemLogger.logException("EnrolmentWishCtrl EditablePanelShowButton", e);
                    MessagePopup.warning(cell, e.getMessage());
                } catch (EnrolmentWishException e) {
                    MessagePopup.warning(cell, e.getMessage());
                } finally {
                    if (view.isTableEditing()) {
                        view.stopEditing();
                    }
                    if (dlg != null) {
                        dlg.dispose();
                    }
                }
            } else if (evt.getActionCommand().equals("EditablePanelCheckbox")) {
                EnrolmentWishEditablePanel cell = (EnrolmentWishEditablePanel) SwingUtilities.getAncestorOfClass(EnrolmentWishEditablePanel.class, (Component) evt.getSource());
                try {
                    if (cell.getStudent() == null) {
                        throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.course.null"));
                    }
                    if (cell.isChecked()) {
                        checkDurationSelected(view.getSelectedRow(), cell.getDuration()); //throw Exception
                        wishService.checkOtherSelected(cell.getStudentId()); // throw Exception
                        EnrolmentSelected val = new EnrolmentSelected(cell.getDuration(), cell.getStudent());
                        int colChecked = particularCourseModel.getChecked(view.getSelectedRow());
                        if (colChecked > 0) {
                            wishService.selectParticularWish((EnrolmentWish) particularCourseModel.getValueAt(view.getSelectedRow(), colChecked), false);
                            particularCourseModel.unCheckColumn(view.getSelectedRow(), colChecked);
                        }
                        wishService.selectParticularWish(cell.getChoice(), true);
                        particularCourseModel.setValueAt(val, view.getSelectedRow(), EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                particularCourseModel.refreshStudent(cell.getStudentId());
                            }
                        });
                    } else {
                        if (cell.getChoice().isMailConfirmSended()) {
//demande du 9/7/2019       throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.error.maildone"));
                            MessagePopup.information(cell, MessageUtil.getMessage("enrolment.wish.error.maildone"));
                            
                        }
                        wishService.selectParticularWish(cell.getChoice(), false);
                        EnrolmentSelected val = new EnrolmentSelected();
                        particularCourseModel.setValueAt(val, view.getSelectedRow(), EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                particularCourseModel.refreshStudent(cell.getStudentId());
                            }
                        });
                    }
                } catch (SQLException e) {
                    GemLogger.logException("EnrolmentWishCtrl EditablePanelCheckbox", e);
                    MessagePopup.warning(cell, e.getMessage());
                } catch (EnrolmentWishException e) {
                    cell.setChecked(cell.isChecked() ? false : true);
                    MessagePopup.warning(cell, e.getMessage());
                } finally {
                    if (view.isTableEditing()) {
                        view.stopEditing();
                    }
                }
            } else if (evt.getActionCommand().equals("EditablePanelStudentButton")) {
                if (view.isTableEditing()) {
                    view.stopEditing();
                }
                EnrolmentWishEditablePanel cell = (EnrolmentWishEditablePanel) SwingUtilities.getAncestorOfClass(EnrolmentWishEditablePanel.class, (Component) evt.getSource());
                if (cell.getStudentId() != 0) {
                    showContactFile(cell.getStudentId());
                }
            } else if (evt.getActionCommand().equals("WishPanelMailButton")) {
                int colChecked = particularCourseModel.getChecked(view.getSelectedRow());
                if (colChecked < 0) {
                    return;
                }

                if (!MessagePopup.confirm(view, MessageUtil.getMessage("enrolment.wish.mail.confirm"))) {
                    return;
                }
                SwingWorker sw = new SwingWorker() {
                    boolean sended = false;
                    LocalDateTime mailDate;
                    int line;
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            EnrolmentWish cell = (EnrolmentWish) particularCourseModel.getValueAt(view.getSelectedRow(), colChecked);
                            PersonFile pf = (PersonFile) DataCache.findId(cell.getStudentId(), Model.PersonFile);

                            mailDate = LocalDateTime.now();
                            line = view.getSelectedRow();
                            Vector<Email> emails = pf.getContact().getEmail();

                            String f = createMailConfirmPdf(cell.getStudent());
                            sendConfirmMail(f, emails.elementAt(0).getEmail());

                            wishService.setMailConfirmDate(cell, mailDate);
                            sended = true;

                            //String saveFileName = "lettre-confirmation-" + cell.getStudent().getId() + "_"+mailDate.format(timestampFileNameFormatter)+".pdf";
                            //Files.copy(Paths.get(f), Paths.get(ConfigUtil.getConf(ConfigKey.LOG_PATH.getKey()),saveFileName));
                        } catch (Exception e) {
                            MessagePopup.warning(view, e.getMessage());
                            GemLogger.logException("EnrolmentWishCtrl WishPanelMailButton", e);
                        }
                        return null;
                    }
                    public void done() {
                        if (sended) {
                            particularCourseModel.setValueAt(mailDate, line, EnrolmentWishParticularCourseTableModel.COLUMN_MAILDATE);
                        }
                    }
                };
                sw.execute();
            } else if (evt.getActionCommand().equals("particularCourseSchedule")) {
                if (view.getCurrentParticularCourse() == null
                        || view.getCurrentParticularCourse().getId() == 0
                        || view.getCurrentTeacher() == null
                        || view.getCurrentTeacher().getId() == 0) {
                    return;
                }
                HourRange range = new HourRange();
                List<EnrolmentWish> wishes = wishService.getParticularCourseRange(view.getCurrentParticularCourse().getId(), view.getCurrentTeacher().getId(), view.getDayChoice());
                if (wishes.size() == 0) {
                    MessagePopup.warning(view, MessageUtil.getMessage("enrolment.wish.hour.novalidwish"));
                    return;
                }
                range.setStart(new net.algem.planning.Hour(wishes.get(0).getHour().toString()));
                int end = wishes.get(wishes.size()-1).getHour().toMinutes() + wishes.get(wishes.size()-1).getDuration().toMinutes(); 
                range.setEnd(new net.algem.planning.Hour(end));
                if (!range.isValid()) { 
                    MessagePopup.warning(view, MessageUtil.getMessage("enrolment.wish.plagerange.invalid", new Object[] { range.getStart(), range.getEnd()}));
                    return;
                }
                if (MessagePopup.confirm(view, MessageUtil.getMessage("enrolment.wish.schedule.confirmation", new Object[] { view.getCurrentCourseLabel()+"/"+view.getCurrentTeacherLabel()+"/"+view.getDayChoiceLabel(), range.getStart(), range.getEnd()}))) {
                    try {
                        showScheduleCtrl(view.getCurrentParticularCourse(), view.getCurrentTeacher(), view.getDayChoice(), range);
                    } catch (Exception ex) {
                        GemLogger.logException("EnrolmentWishCtrl ParticularCourseSchedule", ex);
                        MessagePopup.warning(view, ex.toString());
                    }
                }
            } else if (evt.getActionCommand().equals("Abandonner")) { // from CourseScheduleCtrl 
                desktop.removeModule(CourseScheduleCtrl.COURSE_SCHEDULING_KEY);
                csCtrl = null;
            } else if (evt.getActionCommand().equals("addStudentToGroupCourse")) {
                if (view.getMaxPlaces() <= groupCourseModel.getRowCount()) {
                    MessagePopup.information(view, MessageUtil.getMessage("enrolment.wish.group.addstudent.full", view.getMaxPlaces()));
                    return;
                }
                CourseSchedulePrintDetail csp = view.getGroupCourseChoice();
                if (csp == null) return;

                int student = view.getStudentChoice();
                try {
                    Person p = (Person)dataCache.findId(student, Model.Person);
                    Member m = (Member)dataCache.findId(student, Model.Member);

                    if (wishService.countStudentWishes(student, csp.getAction().getId()) > 0) {
                        MessagePopup.information(view, MessageUtil.getMessage("enrolment.wish.group.addstudent.duplicate", p));
                        return;
                    }

                    EnrolmentWish w = new EnrolmentWish();
                    w.setStudent(p);
                    w.setCourse(csp.getCourse().getId());
                    w.setDay((short)EnrolmentWishIO.isodow2dow(csp.getDow()));
                    w.setAction(csp.getAction().getId()); //getIdAction()); //getCourse().getId());
                    w.setHour(new Hour(csp.getStart().toString()));
                    w.setDuration(new Hour(csp.getEnd().toMinutes()-csp.getStart().toMinutes()));
                    w.setTeacher(csp.getIdPerson());
                    w.setPreference((short)(wishService.countStudentGroupWishes(student)+1));
                    wishService.insertGroupWish(w);

                    EnrolmentWishGroupCourseLine line = new EnrolmentWishGroupCourseLine(p);
                    line.setWish(w);
                    line.setBirthDate(m.getBirth());
                    line.setPractice(m.getPractice());
                    line.setInstrument(dataCache.getInstrumentName(m.getFirstInstrument()));
                    line.setSelected(false);
                    groupCourseModel.addElement(line);

                } catch (SQLException e) {
                    GemLogger.logException("EnrolmentWishCtrl.addStudentToGroupCourse", e);
                }
            } else {
                GemLogger.log("EnrolmentWishCtrl.actionPerformed unknown evt=" + evt);
            }
        } catch (Exception e) {
            GemLogger.log("EnrolmentWishCtrl.actionPerformed e=" + e);
            e.printStackTrace();
            MessagePopup.information(view, MessageUtil.getMessage("enrolment.wish.exception", e.getMessage()));
        }
    }

    @Override
    public void tableChanged(TableModelEvent evt) {
        if (evt.getSource() == groupCourseModel 
            && evt.getType() == TableModelEvent.UPDATE
            && evt.getFirstRow() == evt.getLastRow()
            && evt.getColumn() == EnrolmentWishGroupCourseTableModel.COLUMN_SELECTED) {
            int row = evt.getFirstRow();

            try {
                wishService.selectGroupWish(groupCourseModel.getWish(row), groupCourseModel.isSelected(row));
            } catch (SQLException e) {
                GemLogger.logException("EnrolmentWishCtrl.tableChanged", e);
            }
        }
    }
     
    public void groupCourseReload(CourseSchedulePrintDetail csp) {

        groupCourseWishes = wishService.getWishesFromCourse(csp.getAction().getId());   //getIdAction()); //.getCourse().getId());
        groupCourses = new ArrayList();

        for (EnrolmentWish w : groupCourseWishes) {
            try {
                Person p = (Person)dataCache.findId(w.getStudentId(), Model.Person);
                Member m = (Member)dataCache.findId(w.getStudentId(), Model.Member);
                
                EnrolmentWishGroupCourseLine line = new EnrolmentWishGroupCourseLine(p);
                line.setBirthDate(m.getBirth());
                line.setPractice(m.getPractice());
                line.setInstrument(dataCache.getInstrumentName(m.getFirstInstrument()));
                line.setSelected(w.isSelected());
                line.setWish(w);
                groupCourses.add(line);
            } catch (SQLException e) {
                GemLogger.logException("EnrolmentWishCtrl.groupCourseReload", e);
            }
        }
        groupCourseModel.load(groupCourses);
    }

    public void dayReload(boolean complete) {

        DateFr jour = view.getReferenceDate();
        jour.incDay(view.getDayChoice() - 2);

        try {
            if (complete) {
                teacherCurrentPlanning = wishService.getScheduleForTeacher(view.getTeacherChoice(), jour.toString());
            }
            for (int j = 0; j < teacherCurrentPlanning.size(); j++) {
                CourseSchedule p = (CourseSchedule) teacherCurrentPlanning.elementAt(j);
                EnrolmentWishParticularCourseLine line = particularCourses.get(particularCourseModel.getRowFromHour(new Hour(p.getStart().toString()), view.getTableIncrement()));
                EnrolmentCurrent current = new EnrolmentCurrent(new Hour(p.getStart().getLength(p.getEnd())));
                if (p.getCourse().isCollective()) {
                    current.setLibelle(p.getCourse().getTitle());
                } else {
                    current.setStudent(new Person(p.getMember().getId(), p.getMember().getName(), p.getMember().getFirstName(), "M"));
                }
                current.setCollectif(p.getCourse().isCollective());
                line.setCurrent(current);
            }
        } catch (SQLException e) {
            GemLogger.logException("EnrolmentWishCtrl.dayReload", e);
        }
    }

    public int courseReload() {
        CourseChoiceTeacherModel m = wishService.getCourseByTeacher(view.getTeacherChoice(), EnrolmentWishIO.dow2isodow(view.getDayChoice()));
        view.setParticularCourseChoiceModel(m);
        
        return m.getSize();
    }
    
    public void tableInit() {
        particularCourses = new ArrayList();
        int minutesPerRow = view.getTableIncrement();
        Hour hour = new Hour("09:00"); //TODO ERIC (23:00 - start)*(60/MPR)
        for (int i = 0; i <= (60 / minutesPerRow) * 14; i++, hour.incMinute(minutesPerRow)) { 
            particularCourses.add(new EnrolmentWishParticularCourseLine(new Hour(hour)));
        }
    }
    
    public void tableReload() {
        for (EnrolmentWish w : teacherWishes) {
            if (w.getDay() != view.getDayChoice() || w.getCourse() != view.getParticularCourseChoice()) {
                continue;
            }
            try {
                Person p = (Person)dataCache.findId(w.getStudentId(), Model.Person);
                EnrolmentWishParticularCourseLine line = particularCourses.get(particularCourseModel.getRowFromHour(w.getHour(), view.getTableIncrement()));
                if (w.isSelected()) {
                    EnrolmentSelected evt = new EnrolmentSelected(w.getDuration(), p);
                    line.setSelected(evt);
                }
                if (w.isMailConfirmSended()) {
                    line.setMailDate(w.getDateMailConfirm());
                }
                w.setStudent(p);
                if (w.getColumn() == 0) {
                    line.setSameAsCurrent(w);
                } else {
                    line.setChoice(w.getColumn() - 1, w);
                }
            } catch (SQLException e) {
                GemLogger.logException("EnrolmentWishCtrl.tableReload", e);
            }
        }
        particularCourseModel.load(particularCourses);
    }

    public void teacherReload() {
        teacherWishes = wishService.getWishesFromTeacher(view.getTeacherChoice());
    }

    public void checkDurationSelected(int row, Hour duration) throws EnrolmentWishException {
        EnrolmentWishParticularCourseLine ligne = (EnrolmentWishParticularCourseLine) particularCourses.get(row);
        Hour hourLigne = ligne.getHour();
        int nbLines = duration.toMinutes() / view.getTableIncrement();
        for (int i = 1; i < nbLines && row + i < particularCourses.size(); i++) {
            ligne = (EnrolmentWishParticularCourseLine) particularCourses.get(row + i);
            if (ligne.getSelected().getStudentId() != 0) {
                throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.hour.conflit.under", new Object[]{ligne.getSelected().getStudent(), ligne.getSelected().getDuration()}));
            }
        }
        for (int i = 1; row - i >= 0; i++) {
            ligne = (EnrolmentWishParticularCourseLine) particularCourses.get(row - i);
            if (ligne.getSelected().getStudentId() == 0) {
                continue;
            }
            Hour hprevious = ligne.getHour();
            if (hprevious.end(ligne.getSelected().getDuration()).gt(hourLigne)) {
                throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.hour.conflit.above", new Object[]{ligne.getSelected().getStudent(), hprevious.end(ligne.getSelected().getDuration())}));
            }
            break;
        }
    }

    public static boolean sendInfoMail(String file, String dest, String periode) throws MessagingException, IOException { 

        System.out.println("MemberEnrolmentWishEditor.sendMail f="+file+" dest="+dest);
        Session session = MailUtil.SmtpInitSession();
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ConfigUtil.getConf(ConfigKey.SMTP_SERVER_SENDER.getKey())));
        //2.17 TEST
        //message.addRecipient(Message.RecipientType.TO, new InternetAddress("eric@devmad.fr"));
        //2.17 PROD
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
        message.addRecipient(Message.RecipientType.CC, new InternetAddress("accueil@polynotes.org"));
        
        message.setSubject(MessageUtil.getMessage("enrolment.wish.mail.info.subject"));

        String msg = MessageUtil.getMessage("enrolment.wish.mail.info.text", periode);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        mimeBodyPart.setContent(msg, "text/html");

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(file));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentBodyPart);
        
        message.setContent(multipart);

        Transport.send(message);
        
        return true;
    }


    public static boolean sendConfirmMail(String fichier, String dest) throws MessagingException, IOException {
        
        Session session = MailUtil.SmtpInitSession();
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ConfigUtil.getConf(ConfigKey.SMTP_SERVER_SENDER.getKey())));
        //2.17 TEST
        //message.addRecipient(Message.RecipientType.TO, new InternetAddress("eric@devmad.fr"));
        //2.17 PROD
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
        message.addRecipient(Message.RecipientType.CC, new InternetAddress("accueil@polynotes.org"));

        message.setSubject(MessageUtil.getMessage("enrolment.wish.mail.confirm.subject"));

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(MessageUtil.getMessage("enrolment.wish.mail.confirm.text"), "text/html");

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(fichier));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentBodyPart);
        
        message.setContent(multipart);

        Transport.send(message);
        
        return true;
    }

    private void showContactFile(int id) {

        PersonFileEditor editor = desktop.getPersonFileEditor(id);
        if (editor != null) {
            desktop.setSelectedModule(editor);
        } else {
            try {
                desktop.setWaitCursor();
                PersonFile pf = (PersonFile) DataCache.findId(id, Model.PersonFile);
                editor = new PersonFileEditor(pf);
                desktop.addModule(editor);
            } catch (SQLException ex) {
                GemLogger.logException("EnrolmentWishCtrl.showContactFile", ex);
                MessagePopup.warning(view, ex.getMessage());
            } finally {
                desktop.setDefaultCursor();
            }
        }
    }

    private void showScheduleCtrl(Course c, Teacher t, int dow, HourRange range) {

        desktop.setWaitCursor();
        if (csCtrl == null || desktop.getModule(CourseScheduleCtrl.COURSE_SCHEDULING_KEY) == null) {
            csCtrl = new CourseScheduleCtrl(desktop);
            csCtrl.addActionListener(this);
            csCtrl.init();
            desktop.addPanel(CourseScheduleCtrl.COURSE_SCHEDULING_KEY, csCtrl);
        }
        desktop.setSelectedModule(CourseScheduleCtrl.COURSE_SCHEDULING_KEY);
        csCtrl.setReinscription(c, t, dow, range.getStart(), range.getEnd());

        view.setVisible(false);
        desktop.setDefaultCursor();
    }

    public void setVisible(boolean b) {
        view.setVisible(b);
    }
    
    private String createMailConfirmPdf(Person student) throws IOException, DocumentException, SQLException {
        
        PageTemplateIO ptio = new PageTemplateIO(dataCache.getDataConnection());

        String periode = dataCache.getSchoolNextYearLabel();

        List<EnrolmentWish> wishes = wishService.findStudentWishes(student.getId());

        WishConfirmationLetter wl = new WishConfirmationLetter(ptio, periode, student, wishes);

        String filename = wl.toPDF();

        return filename;
    }

}
