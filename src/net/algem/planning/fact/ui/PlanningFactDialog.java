package net.algem.planning.fact.ui;

import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.planning.fact.services.PlanningFact;
import net.algem.util.Option;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlanningFactDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxType;
    private JXDatePicker datePicker;
    private JTextField textFieldIdPlanning;
    private JTextField textFieldIdProf;
    private JTextArea textAreaComment;
    private JSpinner spinnerDuree;
    private JSpinner spinnerStatut;
    private JSpinner spinnerNiveau;
    private JTextArea textAreaDescription;
    private HourField hourField;

    private final Listener listener;

    private long factId = -1;

    private void createUIComponents() {
        datePicker = new JXDatePicker();
        datePicker.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
    }

    private void setupUI() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setPreferredSize(new Dimension(600, 352));
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
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
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
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 8;
        gbc.ipady = 8;
        gbc.insets = new Insets(8, 8, 8, 8);
        contentPane.add(panel3, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Date");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label1, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(datePicker, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Type");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label2, gbc);
        comboBoxType = new JComboBox();
        comboBoxType.setPreferredSize(new Dimension(120, 26));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(comboBoxType, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Id planning");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Id prof");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Commentaire");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label5, gbc);
        textFieldIdPlanning = new JTextField();
        textFieldIdPlanning.setPreferredSize(new Dimension(60, 24));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(textFieldIdPlanning, gbc);
        textFieldIdProf = new JTextField();
        textFieldIdProf.setPreferredSize(new Dimension(60, 24));
        textFieldIdProf.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(textFieldIdProf, gbc);
        textAreaComment = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 4, 8);
        panel3.add(textAreaComment, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Durée (minutes)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Statut");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Niveau");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Description");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel3.add(label9, gbc);
        spinnerDuree = new JSpinner();
        spinnerDuree.setPreferredSize(new Dimension(60, 26));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(spinnerDuree, gbc);
        spinnerStatut = new JSpinner();
        spinnerStatut.setPreferredSize(new Dimension(60, 26));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(spinnerStatut, gbc);
        spinnerNiveau = new JSpinner();
        spinnerNiveau.setPreferredSize(new Dimension(60, 26));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(spinnerNiveau, gbc);
        textAreaDescription = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 4, 8);
        panel3.add(textAreaDescription, gbc);
        hourField = new HourField();
        hourField.setPreferredSize(new Dimension(50, 24));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 4, 0);
        panel3.add(hourField, gbc);
    }


    interface Listener {
        boolean onPlanningFactValidated(PlanningFact fact);
    }

    public PlanningFactDialog(Listener listener, Option<PlanningFact> initialFact) {
        this.listener = listener;
        setupUI();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        comboBoxType.setModel(new DefaultComboBoxModel(PlanningFact.Type.values()));

        for (PlanningFact fact : initialFact) {
            setPlanningFact(fact);
        }

    }

    private Date getPickerDate() {
        Calendar c = new GregorianCalendar();
        c.setTime(datePicker.getDate());
        c.set(Calendar.HOUR_OF_DAY, hourField.get().getHour());
        c.set(Calendar.MINUTE, hourField.get().getMinute());
        return c.getTime();
    }

    public PlanningFact getPlanningFact() {
        return new PlanningFact(
                factId,
                getPickerDate(),
                (PlanningFact.Type) comboBoxType.getSelectedItem(),
                PlanningFactCRUDController.parseInt(textFieldIdPlanning).getOrElse(-1),
                PlanningFactCRUDController.parseInt(textFieldIdProf).getOrElse(-1),
                textAreaComment.getText(),
                (int) spinnerDuree.getValue(),
                (int) spinnerStatut.getValue(),
                (int) spinnerNiveau.getValue(),
                textAreaDescription.getText()
        );
    }

    public void setPlanningFact(PlanningFact planningFact) {
        factId = planningFact.getId();
        datePicker.setDate(planningFact.getDate());
        hourField.set(new Hour(planningFact.getDate().getHours(), planningFact.getDate().getMinutes()));
        comboBoxType.setSelectedItem(planningFact.getType());
        textFieldIdPlanning.setText("" + planningFact.getPlanning());
        textFieldIdProf.setText("" + planningFact.getProf());
        textAreaComment.setText(planningFact.getCommentaire());
        spinnerDuree.setValue(planningFact.getDureeMinutes());
        spinnerStatut.setValue(planningFact.getStatut());
        spinnerNiveau.setValue(planningFact.getNiveau());
        textAreaDescription.setText(planningFact.getPlanningDescription());
    }

    private void onOK() {
        if (listener.onPlanningFactValidated(getPlanningFact())) {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }
}
