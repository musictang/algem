package net.algem.planning.fact.ui;

import net.algem.contact.teacher.Teacher;
import net.algem.planning.*;
import net.algem.planning.fact.services.ReplanifyCommand;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.Option;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.ui.GemChoiceModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ReplanifyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxProf;
    private JComboBox comboBoxRoom;
    private DateFrField fieldDate;
    private HourField fieldHour;
    private JTextArea textAreaMessage;
    private JTextArea textAreaComment;

    private final ControllerCallbacks callbacks;
    private final Schedule schedule;

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setPreferredSize(new Dimension(350, 400));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
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
        buttonCancel.setText("Cancel");
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
        gbc.ipady = 8;
        gbc.insets = new Insets(8, 8, 8, 8);
        contentPane.add(panel3, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Nouveau prof");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label1, gbc);
        comboBoxProf = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboBoxProf.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(comboBoxProf, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Nouvelle salle");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label2, gbc);
        comboBoxRoom = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(comboBoxRoom, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Nouvelle Date");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Nouvelle heure");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label4, gbc);
        fieldHour = new HourField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(fieldHour, gbc);
        fieldDate = new DateFrField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(fieldDate, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Commentaire");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label5, gbc);
        textAreaComment = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(textAreaComment, gbc);
        textAreaMessage = new JTextArea();
        textAreaMessage.setEditable(false);
        textAreaMessage.setFont(new Font(textAreaMessage.getFont().getName(), textAreaMessage.getFont().getStyle(), textAreaMessage.getFont().getSize()));
        textAreaMessage.setLineWrap(true);
        textAreaMessage.setText("");
        textAreaMessage.setWrapStyleWord(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(textAreaMessage, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public interface ControllerCallbacks {
        Option<String> validateCommand(ReplanifyCommand cmd);

        String getMessageForReplanifyCommand(ReplanifyCommand cmd);

        boolean onReplanifyCommandSelected(ReplanifyCommand cmd, String comment);
    }

    public ReplanifyDialog(ControllerCallbacks callbacks, Schedule schedule) {
        setupUI();
        this.callbacks = callbacks;
        this.schedule = schedule;
        setContentPane(contentPane);
        setTitle("Replanification");
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

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        GemList<Teacher> allTeachers = DataCache.getInitializedInstance().getList(Model.Teacher);
        java.util.List<Teacher> otherTeachers = new ArrayList<>();
        for (Teacher teacher : allTeachers.getData()) {
            if (teacher.getId() != schedule.getIdPerson()) {
                otherTeachers.add(teacher);
            }
        }
        comboBoxProf.setModel(new GemChoiceModel(new GemList(otherTeachers)));

        GemList<Room> allRooms = DataCache.getInitializedInstance().getList(Model.Room);
        java.util.List<Room> otherRooms = new ArrayList<>();
        for (Room room : allRooms.getData()) {
            if (room.getId() != schedule.getIdRoom() && !room.isCatchingUp()) {
                otherRooms.add(room);
            }
        }
        comboBoxRoom.setModel(new GemChoiceModel(new GemList(otherRooms)));

        fieldDate.set(schedule.getDate());
        fieldHour.set(schedule.getStart());


        ActionListener l = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        };
        comboBoxProf.addActionListener(l);
        comboBoxRoom.addActionListener(l);

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refresh();
            }
        };
        fieldDate.getDocument().addDocumentListener(documentListener);
        fieldHour.getDocument().addDocumentListener(documentListener);

        refresh();
    }

    private void refresh() {
        ReplanifyCommand replanifyCommand = getReplanifyCommand();

        Option<String> error = callbacks.validateCommand(replanifyCommand);
        if (error.isPresent()) {
            textAreaMessage.setText(error.get());
            buttonOK.setEnabled(false);
        } else {
            String message = callbacks.getMessageForReplanifyCommand(replanifyCommand);
            textAreaMessage.setText(message);
            buttonOK.setEnabled(true);
        }

    }

    public ReplanifyCommand getReplanifyCommand() {
        Room room = (Room) comboBoxRoom.getSelectedItem();
        Option<Integer> optRoom = room != null && room.getId() != 0 ? Option.of(room.getId()) : Option.<Integer>none();
        Teacher teacher = (Teacher) comboBoxProf.getSelectedItem();
        Option<Integer> optTeacher = teacher != null && teacher.getId() != 0 ? Option.of(teacher.getId()) : Option.<Integer>none();
        DateFr date = fieldDate.getDateFr();
        Option<DateFr> optDate = !date.equals(schedule.getDate()) ? Option.of(date) : Option.<DateFr>none();
        Hour hour = fieldHour.getHour();
        Option<Hour> optHour = !hour.equals(schedule.getStart()) ? Option.of(hour) : Option.<Hour>none();
        return new ReplanifyCommand(schedule, optTeacher, optRoom, optDate, optHour);
    }

    private void onOK() {
        if (callbacks.onReplanifyCommandSelected(getReplanifyCommand(), textAreaComment.getText())) {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

}
