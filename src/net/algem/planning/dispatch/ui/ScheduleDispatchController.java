package net.algem.planning.dispatch.ui;

import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.planning.Action;
import net.algem.planning.dispatch.model.ScheduleDispatch;
import net.algem.planning.dispatch.model.ScheduleDispatchService;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SQLErrorDlg;
import net.algem.util.ui.Toast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;

public class ScheduleDispatchController implements SubscriptionPatternDialog.OnSubscriptionPatternListener {
    private JLabel coursLabel;
    private JTable table1;
    private JButton okButton;
    private JButton btCancel;
    private JPanel panel;
    private JScrollPane scrollPane;

    private final GemDesktop desktop;
    private final Action courseAction;
    private final ScheduleDispatchService scheduleDispatchService;
    private ScheduleDispatch scheduleDispatch;
    private final ScheduleDispatchTableModel dataModel;
    private JDialog dialog;

    public ScheduleDispatchController(GemDesktop desktop, Action courseAction) {
        setupUI();
        this.desktop = desktop;
        this.courseAction = courseAction;
        this.scheduleDispatchService = desktop.getDataCache().getScheduleDispatchService();

        try {
            scheduleDispatch = scheduleDispatchService.loadScheduleDispatch(courseAction);
            Course course = (Course) DataCache.findId(courseAction.getCourse(), Model.Course);
            coursLabel.setText(course.getLabel());

            final JList<Person> rowHeader = new JList<>(new Vector(scheduleDispatch.getPersons()));
            rowHeader.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent me) {
                    Point p = me.getPoint();
                    int row = rowHeader.locationToIndex(p);
                    if (me.getClickCount() == 2) {
                        Person person = scheduleDispatch.getPersons().get(row);
                        showSubscriptionPatternDialog(person);
                    }
                }
            });

            scrollPane.setRowHeaderView(rowHeader);

            table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            dataModel = new ScheduleDispatchTableModel(scheduleDispatch);
            table1.setModel(dataModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveScheduleDispatch();
            }
        });

        btCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    public void run() {
        dialog = new JDialog(desktop.getFrame());
        dialog.setTitle(BundleUtil.getLabel("ScheduleDispatch.label"));
        dialog.getContentPane().add(getPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(desktop.getFrame());
        dialog.setVisible(true);
    }

    public void showSubscriptionPatternDialog(Person person) {
        SubscriptionPatternDialog dialog = new SubscriptionPatternDialog(this, person, scheduleDispatch.getSchedules());
        dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    @Override
    public void onSubscriptionPatternSelected(Person person, ScheduleDispatch.SubscriptionPattern pattern) {
        System.out.println(pattern);
        scheduleDispatch.subscribe(person, pattern);
        dataModel.fireTableDataChanged();
    }

    private void saveScheduleDispatch() {
        try {
            scheduleDispatchService.saveScheduleDispatch(courseAction, scheduleDispatch);
            Toast.showToast(desktop, MessageUtil.getMessage("schedule.range.dispatch.success"));
            dialog.dispose();
        } catch (Exception e) {
            GemLogger.logException(e);
            SQLErrorDlg.displayException(getPanel(), "Erreur", e);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    private void setupUI() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText(BundleUtil.getLabel("Course.label"));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 0, 0);
        panel.add(label1, gbc);
        coursLabel = new JLabel();
        coursLabel.setText("test");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 16, 0, 0);
        panel.add(coursLabel, gbc);
        scrollPane = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);
        panel.add(scrollPane, gbc);
        table1 = new JTable();
        scrollPane.setViewportView(table1);
        okButton = new JButton();
        okButton.setText(GemCommand.VALIDATE_CMD);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 8, 8);
        panel.add(okButton, gbc);

        btCancel = new JButton();
        btCancel.setText(GemCommand.CANCEL_CMD);
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 8, 8);
        panel.add(btCancel, gbc);
    }
}
