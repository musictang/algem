/*
 * @(#)MemberEnrolmentWishEditor.java	2.17.0 22/03/19
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
package net.algem.enrolment;

import com.itextpdf.text.DocumentException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.algem.config.PageTemplateIO;
import net.algem.contact.Email;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.edition.WishConfirmationLetter;
import net.algem.edition.WishInformationLetter;
import net.algem.planning.PlanningService;
import net.algem.planning.wishes.EnrolmentWish;
import net.algem.planning.wishes.EnrolmentWishCtrl;
import net.algem.planning.wishes.EnrolmentWishTableModel;
import net.algem.planning.wishes.EnrolmentWishService;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Enrolment wish controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 22/03/19
 */
public class MemberEnrolmentWishEditor
        extends FileTab
        implements ActionListener {

    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final DateTimeFormatter timestampFileNameFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private boolean loaded;
    private PersonFile personFile;

    private EnrolmentWishTableModel tableModel;
    private MemberCurrentEnrolmentTableModel currentModel;
    private JTable detailTable;
    private JTable noteTable;
    private JTable currentTable;
    private JTextField mailInfoDate;
    private GemButton mailInfoButton;
    private JTextField mailConfirmDate;
    private GemButton mailConfirmButton;

    private final EnrolmentWishService wishService;
    private List<EnrolmentWish> wishes = new ArrayList();
    private final String[] dayNames = PlanningService.WEEK_DAYS;
    
    public MemberEnrolmentWishEditor(GemDesktop desktop, PersonFile pf) {
        super(desktop);
        wishService = new EnrolmentWishService(desktop.getDataCache());
        personFile = pf;

        tableModel = new EnrolmentWishTableModel(wishService.findStudentWishes(pf.getId()));
        currentModel = new MemberCurrentEnrolmentTableModel();
        detailTable = new JTable(tableModel);
        noteTable = new JTable(tableModel);
        currentTable = new JTable(currentModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel cm = detailTable.getColumnModel();
        detailTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_MAILDATE));
        detailTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_NOTE));
        cm.getColumn(0).setPreferredWidth(50); //date
        cm.getColumn(1).setPreferredWidth(40); //choix
        cm.getColumn(1).setCellRenderer(centerRenderer);
        cm.getColumn(2).setPreferredWidth(40); //retenu
        cm.getColumn(3).setPreferredWidth(150); // classe
        cm.getColumn(4).setPreferredWidth(40); //jour
        cm.getColumn(5).setPreferredWidth(40); //heure
        cm.getColumn(6).setPreferredWidth(40); //durée
        cm.getColumn(7).setPreferredWidth(120); //prof

        cm = noteTable.getColumnModel();
        noteTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_TEACHER));
        noteTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_DURATION));
        noteTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_HOUR));
        noteTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_DAY));
        noteTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_SELECTED));
        noteTable.removeColumn(cm.getColumn(EnrolmentWishTableModel.COLUMN_DATE));
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(40);
        cm.getColumn(2).setPreferredWidth(400);

        JTableHeader tHeader = detailTable.getTableHeader();
        tHeader.setFont(new Font("Arial", Font.BOLD, 10));
        ((DefaultTableCellRenderer) detailTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollDetail = new JScrollPane(detailTable);
        JScrollPane scrollNote = new JScrollPane(noteTable);

        scrollNote.setPreferredSize(new Dimension(1024, 120));
        scrollNote.setMaximumSize(new Dimension(1024, 120));

        GemPanel mainPanel = new GemPanel(new BorderLayout());
        GemPanel mailPanel = new GemPanel(new BorderLayout());
        GemPanel mailInfoPanel = new GemPanel(new GridLayout(1, 3));
        GemPanel mailConfirmPanel = new GemPanel(new GridLayout(1, 3));
        GemPanel actualPanel = new GemPanel(new BorderLayout());
        GemPanel detailPanel = new GemPanel(new BorderLayout());
        GemPanel notePanel = new GemPanel(new BorderLayout());

        actualPanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.enrolment.current.label")), BorderLayout.WEST);
        actualPanel.add(currentTable, BorderLayout.CENTER);

        notePanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.notes.label")), BorderLayout.NORTH);
        notePanel.add(scrollNote, BorderLayout.CENTER);

        mailInfoDate = new JTextField(15);
        //mailInfoDate.setText("mercredi 20 mars 2019");
        mailInfoDate.setEnabled(false);
        mailInfoButton = new GemButton(BundleUtil.getLabel("Enrolment.wish.mail.info.label"));
        mailInfoButton.setActionCommand("mailInfoButton");
        mailInfoPanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.mail.info.date.label")));
        mailInfoPanel.add(mailInfoDate, BorderLayout.EAST);
        mailInfoPanel.add(mailInfoButton, BorderLayout.CENTER);
        mailInfoButton.addActionListener(this);

        mailConfirmDate = new JTextField(15);
        //mailConfirmDate.setText("mercredi 20 mars 2019");
        mailConfirmDate.setEnabled(false);
        mailConfirmButton = new GemButton(BundleUtil.getLabel("Enrolment.wish.mail.confirm.label"));
        mailConfirmButton.setActionCommand("mailConfirmButton");
        mailConfirmPanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.mail.confirm.date.label")));
        mailConfirmPanel.add(mailConfirmDate, BorderLayout.EAST);
        mailConfirmPanel.add(mailConfirmButton, BorderLayout.CENTER);
        mailConfirmButton.addActionListener(this);

        mailPanel.add(mailInfoPanel, BorderLayout.NORTH);
        mailPanel.add(mailConfirmPanel, BorderLayout.SOUTH);
        
        detailPanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.details.label")), BorderLayout.NORTH);
        detailPanel.add(scrollDetail, BorderLayout.CENTER);

        mainPanel.add(detailPanel, BorderLayout.CENTER);
        mainPanel.add(notePanel, BorderLayout.NORTH);

        setLayout(new BorderLayout());
        add(actualPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(mailPanel, BorderLayout.SOUTH);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void load() {
        desktop.setWaitCursor();
        clear();

        wishes = wishService.findStudentWishes(personFile.getId()); //, new DateRange(dates.getStartFr(), dates.getEndFr()));

        for (EnrolmentWish w : wishes) {
            tableModel.addElement(w);
        }
        if (wishes.get(0).getDateMailInfo() != null) {
            mailInfoDate.setText(wishes.get(0).getDateMailInfo().format(timestampFormatter));
        } else {
            mailInfoDate.setText("Non envoyé");
        }
        if (wishes.get(0).getDateMailConfirm() != null) {
            mailConfirmDate.setText(wishes.get(0).getDateMailConfirm().format(timestampFormatter));
        } else {
            mailConfirmDate.setText("Non envoyé");
        }
        List<String[]> v = wishService.getCurrentEnrolmentForStudent(personFile.getId());
        for (int i = 0; i < v.size(); i++) {
            currentModel.addItem(v.get(i));
        }

        loaded = true;
        desktop.setDefaultCursor();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
//        System.out.println("ActionEvent:" + evt);
        if (evt.getActionCommand().equals("mailInfoButton")) {
            if (!MessagePopup.confirm(this, MessageUtil.getMessage("enrolment.wish.mail.info"))) {
                return;
            }

            SwingWorker sw = new SwingWorker() {
                boolean sended = false;
                LocalDateTime mailDate;
                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        mailDate = LocalDateTime.now();
                        List<Email> emails = personFile.getContact().getEmail();
                        
                        String f = createMailInfoPdf(personFile.getContact());
                        EnrolmentWishCtrl.sendInfoMail(f, emails.get(0).getEmail(), dataCache.getSchoolNextYearLabel());
                        
                        wishService.setMailInfoDate(personFile.getId(), mailDate);
                        sended = true;

                        //String saveFileName = "lettre-reinscription-" + personFile.getId() + "_"+mailDate.format(timestampFileNameFormatter)+".pdf";
                        //Files.copy(Paths.get(f), Paths.get(ConfigUtil.getConf(ConfigKey.LOG_PATH.getKey()),saveFileName));

                    } catch (Exception e) {
                        MessagePopup.warning(desktop.getFrame(), e.getMessage());
                        GemLogger.logException("MemberEnrolmentWishEditor sendMail:", e);
                    }
                    return null;
                }
                public void done() {
                    if (sended) {
                        mailInfoDate.setText(mailDate.format(timestampFormatter));
                    }
                }
            };
            sw.execute();
        }
        else if (evt.getActionCommand().equals("mailConfirmButton")) {
            if (!MessagePopup.confirm(this, MessageUtil.getMessage("enrolment.wish.mail.confirm"))) {
                return;
            }

            SwingWorker sw = new SwingWorker() {
                boolean sended = false;
                LocalDateTime mailDate;
                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        mailDate = LocalDateTime.now();
                        List<Email> emails = personFile.getContact().getEmail();
                        
                        String f = createMailConfirmPdf(personFile.getContact());
                        EnrolmentWishCtrl.sendConfirmMail(f, emails.get(0).getEmail());

                        wishService.setMailConfirmDate(personFile.getId(), mailDate);
                        sended = true;
                        
                        //String saveFileName = "lettre-confirmation-" + personFile.getId() + "_"+mailDate.format(timestampFileNameFormatter)+".pdf";
                        //Files.copy(Paths.get(f), Paths.get(ConfigUtil.getConf(ConfigKey.LOG_PATH.getKey()),saveFileName));
                    } catch (Exception e) {
                        MessagePopup.warning(desktop.getFrame(), e.getMessage());
                        GemLogger.logException("MemberEnrolmentWishEditor sendMail:", e);
                    }
                    return null;
                }
                public void done() {
                    if (sended) {
                        mailConfirmDate.setText(mailDate.format(timestampFormatter));
                    }
                }
            };
            sw.execute();
        }
    }

    private String createMailInfoPdf(Person student) throws IOException, DocumentException, SQLException {

        PageTemplateIO ptio = new PageTemplateIO(dataCache.getDataConnection());

        String periode = dataCache.getSchoolNextYearLabel();

        WishInformationLetter wl = new WishInformationLetter(ptio, periode, student, wishes);

        String filename = wl.toPDF();

        return filename;
    }

        private String createMailConfirmPdf(Person student) throws IOException, DocumentException, SQLException {
        
        PageTemplateIO ptio = new PageTemplateIO(dataCache.getDataConnection());

        String periode = dataCache.getSchoolNextYearLabel();

        WishConfirmationLetter wl = new WishConfirmationLetter(ptio, periode, student, wishes);

        String filename = wl.toPDF();

        return filename;
    }

    private void clear() {
        if (tableModel.getRowCount() > 0) {
            tableModel.clear();
        }
    }

}
