package net.algem.planning.dispatch.ui;

import net.algem.contact.Person;
import net.algem.planning.Schedule;
import net.algem.planning.dispatch.model.ScheduleDispatch;
import net.algem.planning.dispatch.model.SubscribeAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SubscriptionPatternDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton inscrireRadioButton;
    private JRadioButton désinscrireRadioButton;
    private JSpinner spinner1;
    private JComboBox comboBox1;
    private final ButtonGroup radioGroup;

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("OK");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("Annuler");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonCancel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel3.setAlignmentX(0.0f);
        panel3.setAlignmentY(0.0f);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        contentPane.add(panel3, gbc);
        inscrireRadioButton = new JRadioButton();
        inscrireRadioButton.setSelected(true);
        inscrireRadioButton.setText("Inscrire");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(inscrireRadioButton, gbc);
        désinscrireRadioButton = new JRadioButton();
        désinscrireRadioButton.setText("Désinscrire");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(désinscrireRadioButton, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Toutes les");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 0, 0);
        panel3.add(label1, gbc);
        spinner1 = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 0, 0);
        panel3.add(spinner1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("sessions");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 0, 0);
        panel3.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("A partir de");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 0, 0);
        panel3.add(label3, gbc);
        comboBox1 = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 0, 0);
        panel3.add(comboBox1, gbc);
    }

    public interface OnSubscriptionPatternListener {
        void onSubscriptionPatternSelected(Person person, ScheduleDispatch.SubscriptionPattern pattern);
    }

    private final OnSubscriptionPatternListener listener;
    private final Person person;
    private final List<Schedule> schedules;

    public SubscriptionPatternDialog(final OnSubscriptionPatternListener listener, final Person person, List<Schedule> schedules) {
        setupUI();
        this.listener = listener;
        this.person = person;
        this.schedules = schedules;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle(person.getFirstnameName());

        spinner1.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        comboBox1.setModel(new DefaultComboBoxModel(new Vector(schedules)));
        comboBox1.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Schedule s = (Schedule) value;
                return super.getListCellRendererComponent(list, s.getDate(), index, isSelected, cellHasFocus);
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SubscribeAction action = radioGroup.isSelected(inscrireRadioButton.getModel()) ? SubscribeAction.SUBSCRIBE : SubscribeAction.UNSUBSCRIBE;
                ScheduleDispatch.SubscriptionPattern subscriptionPattern = new ScheduleDispatch.SubscriptionPattern(action, (Schedule) comboBox1.getSelectedItem(), (int) spinner1.getValue());
                listener.onSubscriptionPatternSelected(person, subscriptionPattern);
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        radioGroup = new ButtonGroup();
        radioGroup.add(inscrireRadioButton);
        radioGroup.add(désinscrireRadioButton);

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
