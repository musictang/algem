package net.algem.planning.fact.ui;

import net.algem.planning.DateFr;
import net.algem.planning.fact.services.PlanningFact;
import net.algem.planning.fact.services.PlanningFactDAO;
import net.algem.util.GemLogger;
import net.algem.util.Option;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SQLErrorDlg;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.List;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;

public class PlanningFactCRUDController {
    private JTable table1;
    private JButton ajouterButton;
    private JButton modifierButton;
    private JButton supprimerButton;
    private JPanel panel;
    private JTextField textFieldIdProf;
    private JTextField textFieldIdPlanning;
    private JButton appliquerButton;
    private JXDatePicker datePickerStart;
    private JXDatePicker datePickerEnd;

    private final PlanningFactDAO dao;
    private List<PlanningFact> data;

    public PlanningFactCRUDController(GemDesktop desktop) {
        setupUI();
        dao = desktop.getDataCache().getPlanningFactDAO();
        refresh();
        appliquerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateDialog();
            }
        });
        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showModifyDialog();
            }
        });
        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedFact();
            }
        });

        table1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showModifyDialog();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void showCreateDialog() {
        PlanningFactDialog dialog = new PlanningFactDialog(new PlanningFactDialog.Listener() {

            @Override
            public boolean onPlanningFactValidated(PlanningFact fact) {
                try {
                    dao.insert(fact);
                    refresh();
                    return true;
                } catch (Exception e) {
                    GemLogger.logException(e);
                    SQLErrorDlg.displayException(getPanel(), "Erreur", e);
                }
                return false;
            }
        }, Option.<PlanningFact>none());
        dialog.setLocationRelativeTo(getPanel());
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showModifyDialog() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow != -1 && data != null) {
            PlanningFact fact = data.get(table1.convertRowIndexToModel(selectedRow));
            PlanningFactDialog dialog = new PlanningFactDialog(new PlanningFactDialog.Listener() {

                @Override
                public boolean onPlanningFactValidated(PlanningFact fact) {
                    try {
                        dao.update(fact);
                        refresh();
                        return true;
                    } catch (Exception e) {
                        GemLogger.logException(e);
                        SQLErrorDlg.displayException(getPanel(), "Erreur", e);
                    }
                    return false;
                }
            }, Option.of(fact));
            dialog.setLocationRelativeTo(getPanel());
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    private void refresh() {
        try {
            data = dao.findAsList(getQuery());
            TableModel model = new FactTableModel(data);
            table1.setModel(model);
            table1.setRowSorter(new TableRowSorter<>(model));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PlanningFactDAO.Query getQuery() {
        return new PlanningFactDAO.Query(
                parseInt(textFieldIdPlanning), parseInt(textFieldIdProf),
                getDateFr(datePickerStart), getDateFr(datePickerEnd),
                Option.<PlanningFact.Type>none());
    }

    private static Option<DateFr> getDateFr(JXDatePicker datePickerStart) {
        return datePickerStart.getDate() != null ? Option.of(new DateFr(datePickerStart.getDate())) : Option.<DateFr>none();
    }

    public static Option<Integer> parseInt(JTextField textField) {
        String text = textField.getText();
        if (text != null && text.trim().length() > 0) {
            try {
                return Option.of(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                return Option.none();
            }
        } else {
            return Option.none();
        }
    }

    private void createUIComponents() {
        datePickerStart = new JXDatePicker();
        datePickerEnd = new JXDatePicker();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        datePickerStart.setFormats(dateFormat);
        datePickerEnd.setFormats(new SimpleDateFormat("dd-MM-yyyy"));
    }

    private void deleteSelectedFact() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow != -1 && data != null) {
            PlanningFact fact = data.get(table1.convertRowIndexToModel(selectedRow));
            boolean confirmation = MessagePopup.confirm(getPanel(), "<html><b>Voulez vous supprimer cet événement ?</b>\n" +
                    "L'événement sera définitivement supprimé.", "Confirmation de suppression");

            if (confirmation) {
                try {
                    dao.delete(fact.getId());
                    refresh();
                } catch (Exception e) {
                    GemLogger.logException(e);
                    SQLErrorDlg.displayException(getPanel(), MessageUtil.getMessage("delete.error"), e);
                }
            }
        }
    }

    private void setupUI() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText("Date début");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Date fin");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(label2, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(panel1, gbc);
        ajouterButton = new JButton();
        ajouterButton.setText(GemCommand.ADD_CMD);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ajouterButton, gbc);
        modifierButton = new JButton();
        modifierButton.setText(GemCommand.MODIFY_CMD);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(modifierButton, gbc);
        supprimerButton = new JButton();
        supprimerButton.setText(GemCommand.DELETE_CMD);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(supprimerButton, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);
        panel.add(scrollPane1, gbc);
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JLabel label3 = new JLabel();
        label3.setText("Id prof");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(label3, gbc);
        textFieldIdProf = new JTextField();
        textFieldIdProf.setMinimumSize(new Dimension(50, 24));
        textFieldIdProf.setPreferredSize(new Dimension(50, 24));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(textFieldIdProf, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Id planning");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(label4, gbc);
        textFieldIdPlanning = new JTextField();
        textFieldIdPlanning.setMinimumSize(new Dimension(50, 24));
        textFieldIdPlanning.setPreferredSize(new Dimension(50, 24));
        textFieldIdPlanning.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(textFieldIdPlanning, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(datePickerStart, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(datePickerEnd, gbc);
        appliquerButton = new JButton();
        appliquerButton.setText("Appliquer");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 0, 0);
        panel.add(appliquerButton, gbc);
    }
}
