package net.algem.planning.fact.ui;

import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.ui.GemChoiceModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AbsenceToCatchUpDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxRoom;
    private JTextArea textAreaComment;

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setAlignmentY(0.0f);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
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
        final JLabel label1 = new JLabel();
        label1.setText("<html>Le planning sera mis en salle de rattrapage <br>et un événement d'absence à rattraper sera enregistré</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        contentPane.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Salle :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 0, 8);
        contentPane.add(label2, gbc);
        comboBoxRoom = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 0, 8);
        contentPane.add(comboBoxRoom, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Commentaire");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 0, 8);
        contentPane.add(label3, gbc);
        textAreaComment = new JTextArea();
        textAreaComment.setFocusCycleRoot(false);
        textAreaComment.setMinimumSize(new Dimension(0, 200));
        textAreaComment.setPreferredSize(new Dimension(0, 200));
        textAreaComment.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 0, 8);
        contentPane.add(textAreaComment, gbc);
    }

    public interface RoomSelectedListener {
        void onRoomSelected(Room room, String comment);
    }

    @SuppressWarnings("unchecked")
    public AbsenceToCatchUpDialog(final RoomSelectedListener listener, DataCache dataCache) {
        setupUI();
        setContentPane(contentPane);
        setModal(true);
        setTitle("Absence à rattraper");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Room room = (Room) comboBoxRoom.getSelectedItem();
                if (room != null) {
                    listener.onRoomSelected(room, textAreaComment.getText());
                    dispose();
                }
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


        java.util.List<Room> rooms = dataCache.getList(Model.Room).getData();
        java.util.List<Room> catchUpRooms = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isCatchingUp()) catchUpRooms.add(r);
        }
        comboBoxRoom.setModel(new GemChoiceModel(new GemList(catchUpRooms)));
        if (catchUpRooms.size() > 0) comboBoxRoom.setSelectedIndex(0);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                textAreaComment.requestFocusInWindow();
            }
        });


    }

    private void onCancel() {
        dispose();
    }
}
