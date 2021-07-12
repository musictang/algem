/*
 * @(#)EnrolmentWishView.java	2.17.0 16/03/19
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Person;
import net.algem.contact.PersonChoice;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.Course;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceTeacherModel;
import net.algem.course.CourseScheduleChoice;
import net.algem.planning.CourseSchedulePrintDetail;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.day.DayChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.ui.ButtonColumn;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoiceModel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.Toast;

/**
 * Tableau de réinscription pour Polynotes
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0l
 * @since 2.17.0
 */
public class EnrolmentWishView extends JFrame implements EnrolmentWishIHM, ActionListener {

    public static final int ENROLMENT_WISH_WINDOW_HEIGHT = 900;
    private static final Dimension ENROLMENT_WISH_DIMENSION = new Dimension(1500, ENROLMENT_WISH_WINDOW_HEIGHT);
    public static final int ENROLMENT_WISH_WINDOW_EDITMODE_HEIGHT = 900;
    private static final Dimension ENROLMENT_WISH_EDITMODE_DIMENSION = new Dimension(1200, ENROLMENT_WISH_WINDOW_EDITMODE_HEIGHT);
    public static final int ENROLMENT_WISH_WINDOW_MAILMODE_HEIGHT = 900;
    private static final Dimension ENROLMENT_WISH_MAILMODE_DIMENSION = new Dimension(820, ENROLMENT_WISH_WINDOW_MAILMODE_HEIGHT);
    public static final int ENROLMENT_WISH_WINDOW_GROUPMODE_HEIGHT = 700;
    private static final Dimension ENROLMENT_WISH_GROUPMODE_DIMENSION = new Dimension(900, ENROLMENT_WISH_WINDOW_MAILMODE_HEIGHT);
    private static Dimension particularDimension;
    private static Dimension groupDimension;
    
    final static String GROUP_COURSES_CARD = "GroupCourses";
    final static String PARTICULAR_COURSES_CARD = "ParticularCourses";
    
    private DataCache dataCache;

    private EnrolmentWishService wishService;
    private ActionListener listener;

    private DateFrField referenceDate;
    private GemPanel referencePanel;
    private TeacherChoice teacherChoice;
    private DayChoice dayChoice;
    private CourseChoice particularCourseChoice;
    private int maxPlaces;
    private PersonChoice studentChoice;
    private JTable particularCourseTable;
    private GemPanel topParticularCoursePanel;
    private TableColumnModel particularCourseColumnModel;
    private TableColumn selected, maildate, liste1, liste2, liste3, liste4;

    private CourseScheduleChoice groupCourseChoice;
    private JTable groupCourseTable;
    private TableColumnModel groupCourseColumnModel;
    private GemField dayLabel;
    private GemField hourLabel;
    private GemField durationLabel;
    private GemField teacherLabel;
    private GemField placeLabel;
    private GemButton addStudent;

    private CardLayout cardLayout;
    private GemPanel cards;
    
    private JMenu mOptions;
    private JMenuItem miPrint;
    private JMenuItem miQuit;
    private JMenuItem miSaveUISettings;
    private boolean savePrefs;
    private JCheckBoxMenuItem miZoomMode;
    private boolean zoomMode;
    private JCheckBoxMenuItem miMailMode;
    private boolean mailMode;
    private JCheckBoxMenuItem miEditMode;
    private boolean editMode = true; // mode d'affichage par défaut
    private JCheckBoxMenuItem miGroupCourses;
    private boolean groupCourseMode;
    private JCheckBoxMenuItem miParticularCourses;
    private boolean particularCourseMode = true;
    private GemButton particularCourseSchedule;
    private final Preferences prefs = Preferences.userRoot().node("/algem/ui");

    List<EnrolmentWish> teacherWishes = new ArrayList();
    EnrolmentWishParticularCourseTableModel model;

    public EnrolmentWishView(String title, EnrolmentWishService wishService, ActionListener listener ) {

        super(title);

        this.wishService = wishService;
        this.listener = listener;
        dataCache = DataCache.getInitializedInstance();

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setLayout(new BorderLayout());

        JMenuBar mBar = new JMenuBar();
        JMenu mFile = createJMenu("Menu.file");
        miQuit = getMenuItem("Menu.quit");
        miPrint = getMenuItem("Menu.print");

        mFile.add(miPrint);
        mFile.add(miQuit);

        JMenu mCourseType = new JMenu(BundleUtil.getLabel("Course.type.label"));

        miGroupCourses = new JCheckBoxMenuItem(BundleUtil.getLabel("Month.schedule.collective.course.tab"), groupCourseMode); //TODO //FIXME créer un label
        miGroupCourses.setSelected(false);
        miGroupCourses.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                groupCourseMode = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        miParticularCourses = new JCheckBoxMenuItem(BundleUtil.getLabel("Month.schedule.course.tab"), groupCourseMode); //TODO //FIXME créer un label
        miParticularCourses.setSelected(true);
        miParticularCourses.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                particularCourseMode = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        mCourseType.add(miParticularCourses);
        mCourseType.add(miGroupCourses);
        
        mOptions = new JMenu(BundleUtil.getLabel("Display.label"));
        
        miZoomMode = new JCheckBoxMenuItem(BundleUtil.getLabel("Enrolment.wish.zoommode.label"), zoomMode);
        miZoomMode.setSelected(false);
        miZoomMode.setToolTipText(BundleUtil.getLabel("Enrolment.wish.zoommode.tip"));
        miZoomMode.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                zoomMode = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        miMailMode = new JCheckBoxMenuItem(BundleUtil.getLabel("Enrolment.wish.mailmode.label"), mailMode);
        miMailMode.setSelected(mailMode);
        miMailMode.setToolTipText(BundleUtil.getLabel("Enrolment.wish.mailmode.tip"));
        miMailMode.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mailMode = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        miEditMode = new JCheckBoxMenuItem(BundleUtil.getLabel("Enrolment.wish.editmode.label"), editMode);
        miEditMode.setSelected(editMode);
        miEditMode.setToolTipText(BundleUtil.getLabel("Enrolment.wish.editmode.tip"));
        miEditMode.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                editMode = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        miSaveUISettings = dataCache.getMenu2("Store.ui.settings");
        miSaveUISettings.setActionCommand("Store.ui.settings");
        miSaveUISettings.addActionListener(this);

        mOptions.add(miZoomMode);
        mOptions.add(miMailMode);
        mOptions.add(miEditMode);
        mOptions.add(miSaveUISettings);

        mBar.add(mFile);
        mBar.add(mCourseType);
        mBar.add(mOptions);

        // event for the Ctrl
        miQuit.addActionListener(listener);
        miZoomMode.setActionCommand("tableIncrement");
        miZoomMode.addActionListener(listener);
        // event managed locally
        miSaveUISettings.addActionListener(this);
        miMailMode.addActionListener(this);
        miEditMode.addActionListener(this);
        miGroupCourses.setActionCommand("groupCourses");
        miGroupCourses.addActionListener(this);
        miParticularCourses.setActionCommand("particularCourses");
        miParticularCourses.addActionListener(this);

        setJMenuBar(mBar);

        referenceDate = new DateFrField("02-03-2020");
        referenceDate.setActionCommand("referenceDate");
        referenceDate.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(FocusEvent e) {
                    setGroupCourseChoiceModel(new GemChoiceModel(new GemList(wishService.getWeekGroupCourses(getReferenceDate(), getEndReferenceDate()))));
                }
                @Override
                public void focusGained(FocusEvent e) {
                }
        });        
        referenceDate.addActionListener(this);
        referencePanel = new GemPanel();
        referencePanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.refweek.label")));
        referencePanel.add(referenceDate);
        
        dayChoice = new DayChoice();
        teacherChoice = new TeacherChoice(wishService.getTeachers(), true);
        particularCourseChoice = new CourseChoice(new CourseChoiceTeacherModel(new GemList())); //wishService.getCourseByTeacher(0, 0));
        groupCourseChoice = new CourseScheduleChoice(new GemList(wishService.getWeekGroupCourses(getReferenceDate(), getEndReferenceDate())));


        dayChoice.setActionCommand("dayChoice");
        dayChoice.addActionListener(listener);
        teacherChoice.setActionCommand("teacherChoice");
        teacherChoice.addActionListener(listener);
        particularCourseChoice.setActionCommand("particularCourseChoice");
        particularCourseChoice.addActionListener(listener);
        groupCourseChoice.setActionCommand("groupCourseChoice");
        groupCourseChoice.addActionListener(listener);
        studentChoice = new PersonChoice(wishService.getStudentsWithOrders());
        particularCourseSchedule = new GemButton((BundleUtil.getLabel("Enrolment.wish.schedule.label")));
        particularCourseSchedule.setActionCommand("particularCourseSchedule");
        particularCourseSchedule.addActionListener(listener);

        topParticularCoursePanel = new GemPanel();
        topParticularCoursePanel.add(referencePanel);
        topParticularCoursePanel.add(new GemLabel(BundleUtil.getLabel("Teacher.label")));
        topParticularCoursePanel.add(teacherChoice);
        topParticularCoursePanel.add(new GemLabel(BundleUtil.getLabel("Day.label")));
        topParticularCoursePanel.add(dayChoice);
        topParticularCoursePanel.add(new GemLabel(BundleUtil.getLabel("Course.label")));
        topParticularCoursePanel.add(particularCourseChoice);
        topParticularCoursePanel.add(particularCourseSchedule);

        particularCourseTable = new JTable();
        particularCourseTable.setBackground(Color.white);

        GemPanel topGroupCoursePanel = new GemPanel(new BorderLayout());
        GemPanel tgcp1 = new GemPanel();
        GemPanel tgcp2 = new GemPanel();
        hourLabel = new GemField(5);
        durationLabel = new GemField(5);
        dayLabel = new GemField(8);
        placeLabel = new GemField(2);
        teacherLabel = new GemField(30);
        hourLabel.setEditable(false);
        durationLabel.setEditable(false);
        dayLabel.setEditable(false);
        teacherLabel.setEditable(false);
        tgcp1.add(new GemLabel(BundleUtil.getLabel("Course.label")));
        tgcp1.add(groupCourseChoice);
        tgcp1.add(new GemLabel(BundleUtil.getLabel("Day.label")));
        tgcp1.add(dayLabel);
        tgcp1.add(new GemLabel(BundleUtil.getLabel("Hour.label")));
        tgcp1.add(hourLabel);
        tgcp1.add(new GemLabel(BundleUtil.getLabel("Duration.label")));
        tgcp1.add(durationLabel);
        tgcp2.add(new GemLabel(BundleUtil.getLabel("Teacher.label")));
        tgcp2.add(teacherLabel);
        tgcp2.add(new GemLabel(BundleUtil.getLabel("Course.nplaces.label")));
        tgcp2.add(placeLabel);
        topGroupCoursePanel.add(tgcp1, BorderLayout.NORTH);
        topGroupCoursePanel.add(tgcp2, BorderLayout.SOUTH);

        GemPanel bottomGroupPanel = new GemPanel();
        addStudent = new GemButton(BundleUtil.getLabel("Action.add.label"));
        addStudent.setActionCommand("addStudentToGroupCourse");
        addStudent.addActionListener(listener);
        bottomGroupPanel.add(new GemLabel(BundleUtil.getLabel("Enrolment.wish.group.addstudent.label")));
        bottomGroupPanel.add(studentChoice);
        bottomGroupPanel.add(addStudent);
        
        groupCourseTable = new JTable();
        groupCourseTable.setBackground(Color.white);
        
        cardLayout = new CardLayout();
        cards = new GemPanel(cardLayout);
        
        GemPanel particularPanel = new GemPanel(new BorderLayout());
        
        particularPanel.add(topParticularCoursePanel, BorderLayout.NORTH);
        particularPanel.add(new JScrollPane(particularCourseTable), BorderLayout.CENTER);

        GemPanel groupPanel = new GemPanel(new BorderLayout());
        
        groupPanel.add(topGroupCoursePanel, BorderLayout.NORTH);
        groupPanel.add(new JScrollPane(groupCourseTable), BorderLayout.CENTER);
        groupPanel.add(bottomGroupPanel, BorderLayout.SOUTH);
        
        cards.add(particularPanel, PARTICULAR_COURSES_CARD);
        cards.add(groupPanel, GROUP_COURSES_CARD);
        cardLayout.show(cards, PARTICULAR_COURSES_CARD);
        
        add(cards, BorderLayout.CENTER);
        particularDimension = new Dimension(prefs.getInt("wish_enrolment.w", ENROLMENT_WISH_DIMENSION.width), prefs.getInt("wish_enrolment.h", ENROLMENT_WISH_DIMENSION.height));
        groupDimension = new Dimension(prefs.getInt("wish_enrolment_group.w", ENROLMENT_WISH_GROUPMODE_DIMENSION.width), prefs.getInt("wish_enrolment_group.h", ENROLMENT_WISH_GROUPMODE_DIMENSION.height));
        
        setSize(particularDimension);
    }

    public void initTableModel(EnrolmentWishParticularCourseTableModel model) {
        particularCourseTable.setModel(model);

        particularCourseColumnModel = particularCourseTable.getColumnModel();

        EnrolmentWishEditableCell choiceCell = new EnrolmentWishEditableCell(wishService, listener);
        EnrolmentWishCell selectedCell = new EnrolmentWishCell(listener);

        particularCourseTable.setDefaultRenderer(EnrolmentSelected.class, selectedCell);
        particularCourseTable.setDefaultRenderer(EnrolmentCurrent.class, new EnrolmentCurrentCell());
        particularCourseTable.setDefaultRenderer(EnrolmentWish.class, choiceCell);
        particularCourseTable.setDefaultEditor(EnrolmentSelected.class, selectedCell);
        particularCourseTable.setDefaultEditor(EnrolmentWish.class, choiceCell);

//      particularCourseTable.setRowHeight(40);        
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_HOUR).setMaxWidth(50);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CURRENT).setPreferredWidth(150);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED).setPreferredWidth(150);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_MAILDATE).setPreferredWidth(120);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(GemLabel.CENTER);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_MAILDATE).setCellRenderer(centerRenderer);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_SAMEASCURRENT).setPreferredWidth(200);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CHOICE1).setPreferredWidth(200);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CHOICE2).setPreferredWidth(200);
        particularCourseTable.getColumnModel().getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CHOICE3).setPreferredWidth(200);

        JTableHeader tHeader = particularCourseTable.getTableHeader();
        tHeader.setReorderingAllowed(false);
        tHeader.setFont(new Font("Arial", Font.BOLD, 12));
        ((DefaultTableCellRenderer) particularCourseTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(GemLabel.CENTER);

        if (editMode) {
            hideMail();
        }
        if (mailMode) {
            hideList();
        }
    }

    public void initTableModel(EnrolmentWishGroupCourseTableModel model, Action removeStudent) {
        groupCourseTable.setModel(model);

        groupCourseColumnModel = groupCourseTable.getColumnModel();

        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_NUMBER).setMaxWidth(50);
        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_STUDENT).setPreferredWidth(250);
        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_SELECTED).setPreferredWidth(100);
        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_AGE).setPreferredWidth(80);
        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_INSTRUMENT).setPreferredWidth(120);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(GemLabel.CENTER);
        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_PRACTICE).setCellRenderer(centerRenderer);
        groupCourseTable.getColumnModel().getColumn(EnrolmentWishGroupCourseTableModel.COLUMN_PRACTICE).setPreferredWidth(60);

        ButtonColumn deleteButton = new ButtonColumn(groupCourseTable, removeStudent, EnrolmentWishGroupCourseTableModel.COLUMN_DELETE);

        JTableHeader tHeader = groupCourseTable.getTableHeader();
        tHeader.setReorderingAllowed(false);
        tHeader.setFont(new Font("Arial", Font.BOLD, 12));
        ((DefaultTableCellRenderer) groupCourseTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(GemLabel.CENTER);

    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == miMailMode) {
            if (mailMode) {
                if (editMode) {
                    showMail();
                    miEditMode.setSelected(false);
                }
                hideList();
            } else {
                showListe();
            }
        } else if (evt.getSource() == miEditMode) {
            if (editMode) {
                if (mailMode) {
                    showListe();
                    miMailMode.setSelected(false);
                }
                hideMail();
            } else {
                showMail();
            }
        } else if (evt.getActionCommand().equals("Store.ui.settings")) {
            storeUISettings();
            Toast.showToast(getRootPane(), getUIInfo());
        } else if (evt.getActionCommand().equals("Menu.print")) {
            try {
                MessageFormat footer = new MessageFormat("Page - {0}");
                MessageFormat header = new MessageFormat(getCurrentTeacherLabel()+","+getDayChoiceLabel() + ","+getCurrentCourseLabel());
                particularCourseTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            } catch (PrinterException ex) {
                MessagePopup.warning(this, MessageUtil.getMessage("enrolment.wish.printing.error", ex.getMessage()));
            }
        } else if (evt.getSource() == miGroupCourses) {
            if (particularCourseMode) {
                    particularDimension = this.getSize();
                    miParticularCourses.setSelected(false);
                    mOptions.setEnabled(false);
                    cardLayout.show(cards, GROUP_COURSES_CARD);
                    setSize(groupDimension);
            } else {
                    miGroupCourses.setSelected(true);
            }
        } else if (evt.getSource() == miParticularCourses) {
            if (groupCourseMode) {
                    groupDimension = this.getSize();
                    miGroupCourses.setSelected(false);
                    mOptions.setEnabled(true);
                    cardLayout.show(cards, PARTICULAR_COURSES_CARD);
                    setSize(particularDimension);
            } else {
                    miParticularCourses.setSelected(true);
            }
        } else if (evt.getActionCommand().equals("Menu.quit")) {
            setVisible(false);
            dispose();
        }
    }

    public int getSelectedRow() {
        return particularCourseTable.getSelectedRow();
    }

    public int getSelectedColumn() {
        return (editMode ? particularCourseTable.getSelectedColumn() + 2 : particularCourseTable.getSelectedColumn());
    }

    public boolean isTableEditing() {
        return particularCourseTable.isEditing();
    }

    public void stopEditing() {
        particularCourseTable.getCellEditor().stopCellEditing(); // need before updateParticularWish

    }

    @Override
    public int getTeacherChoice() {
        return teacherChoice.getKey();
    }

    @Override
    public int getStudentChoice() {
        return studentChoice.getKey();
    }

    @Override
    public String getCurrentTeacherLabel() {
        Object o = teacherChoice.getSelectedItem();
        if (o == null) {
            return "";
        } else {
            return ((Person) o).toString();
        }
    }

    @Override
    public Teacher getCurrentTeacher() {
        return (Teacher) teacherChoice.getSelectedItem();
    }

    @Override
    public int getDayChoice() {
        return dayChoice.getKey();
    }

    @Override
    public String getDayChoiceLabel(int i) {
        return dayChoice.getItemAt(i).toString();
    }

    @Override
    public String getDayChoiceLabel() {
        return (String) dayChoice.getSelectedItem().toString();
    }

    @Override
    public int getParticularCourseChoice() {
        return particularCourseChoice.getKey();
    }

    @Override
    public CourseSchedulePrintDetail getGroupCourseChoice() {
        return (CourseSchedulePrintDetail)groupCourseChoice.getSelectedItem();
    }

    public void setGroupCourseLabels(String day, net.algem.planning.Hour debut, net.algem.planning.Hour fin, String teacher) {
        dayLabel.setText(day);
        hourLabel.setText(debut.toString());
        durationLabel.setText(new Hour(fin.toMinutes()-debut.toMinutes()).toString());
        teacherLabel.setText(teacher);
    }

    public void setMaxPlaces(short nplaces) {
        maxPlaces = nplaces;
        placeLabel.setText(Integer.toString(nplaces));
    }   
    
    public int getMaxPlaces() {
        return maxPlaces;
    }   
    
    @Override
    public String getCurrentCourseLabel() {
        Object o = particularCourseChoice.getSelectedItem();
        if (o == null) {
            return "";
        } else {
            return ((Course) o).toString();
        }
    }

    @Override
    public Course getCurrentParticularCourse() {
        return (Course)particularCourseChoice.getSelectedItem();
    }

    public void setParticularCourseChoiceModel(GemChoiceModel model) {
        particularCourseChoice.setModel(model);
        if (model.getSize() > 0) {
            particularCourseChoice.setSelectedIndex(0);
        }
    }

    public void setGroupCourseChoiceModel(GemChoiceModel model) {
        groupCourseChoice.setModel(model);
        if (model.getSize() > 0) {
            groupCourseChoice.setSelectedIndex(0);
        }
    }

    public DateFr getReferenceDate() {
        return referenceDate.getDateFr();
    }

    public DateFr getEndReferenceDate() {
        DateFr end = referenceDate.getDateFr();
        end.incDay(6);
        return end;
    }

    public int getTableIncrement() {
        return (zoomMode ? 15 : 5);
    }

    private void hideList() {
        topParticularCoursePanel.remove(referencePanel);
        liste1 = particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_SAMEASCURRENT);
        liste2 = particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CHOICE1);
        liste3 = particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CHOICE2);
        liste4 = particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CHOICE3);
        particularCourseColumnModel.removeColumn(liste1);
        particularCourseColumnModel.removeColumn(liste2);
        particularCourseColumnModel.removeColumn(liste3);
        particularCourseColumnModel.removeColumn(liste4);

        particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED).setWidth(200);
        particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CURRENT).setWidth(200);
        setSize(new Dimension(prefs.getInt("wish_enrolment.mail.w", ENROLMENT_WISH_MAILMODE_DIMENSION.width), prefs.getInt("wish_enrolment.mail.h", ENROLMENT_WISH_MAILMODE_DIMENSION.height)));
    }

    private void showListe() {
        topParticularCoursePanel.add(referencePanel);
        particularCourseColumnModel.addColumn(liste1);
        particularCourseColumnModel.addColumn(liste2);
        particularCourseColumnModel.addColumn(liste3);
        particularCourseColumnModel.addColumn(liste4);
        particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED).setWidth(170);
        particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_CURRENT).setWidth(170);
        setSize(new Dimension(prefs.getInt("wish_enrolment.w", ENROLMENT_WISH_DIMENSION.width), prefs.getInt("wish_enrolment.h", ENROLMENT_WISH_DIMENSION.height)));
    }

    private void hideMail() {
        maildate = particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_MAILDATE);
        selected = particularCourseColumnModel.getColumn(EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED);
        particularCourseColumnModel.removeColumn(maildate);
        particularCourseColumnModel.removeColumn(selected);
        setSize(new Dimension(prefs.getInt("wish_enrolment.edit.w", ENROLMENT_WISH_EDITMODE_DIMENSION.width), prefs.getInt("wish_enrolment.edit.h", ENROLMENT_WISH_EDITMODE_DIMENSION.height)));

    }

    private void showMail() {
        particularCourseColumnModel.addColumn(selected);
        particularCourseColumnModel.moveColumn(particularCourseColumnModel.getColumnCount() - 1, EnrolmentWishParticularCourseTableModel.COLUMN_SELECTED);
        particularCourseColumnModel.addColumn(maildate);
        particularCourseColumnModel.moveColumn(particularCourseColumnModel.getColumnCount() - 1, EnrolmentWishParticularCourseTableModel.COLUMN_MAILDATE);
        setSize(new Dimension(prefs.getInt("wish_enrolment.w", ENROLMENT_WISH_DIMENSION.width), prefs.getInt("wish_enrolment.h", ENROLMENT_WISH_DIMENSION.height)));
    }

    public JMenuItem getMenuItem(String menu) {
        JMenuItem m = dataCache.getMenu2(menu);
        m.setActionCommand(menu);
        m.addActionListener(this);

        return m;
    }

    public JMenu createJMenu(String nom) {
        JMenu m = new JMenu(BundleUtil.getLabel(nom + ".label"));
        m.setMnemonic(BundleUtil.getLabel(nom + ".mnemo").charAt(0));

        return m;
    }

    public void storeUISettings() {
        Rectangle bounds = getBounds();
        String prefix = "wish_enrolment";
        if (particularCourseMode) {
            if (editMode) {
                prefix += "_edit";
            }
            if (mailMode) {
                prefix += "_mail";
            }
        }
        if (groupCourseMode) {
            prefix += "_group";
        }
        prefs.putInt(prefix + ".w", bounds.width);
        prefs.putInt(prefix + ".h", bounds.height);
    }

    public String getUIInfo() {
        Dimension d = getSize();
        return BundleUtil.getLabel("New.size.label") + " : " + d.width + "x" + d.height;
    }

}
