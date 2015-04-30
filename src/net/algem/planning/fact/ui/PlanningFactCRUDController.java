package net.algem.planning.fact.ui;

import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.fact.services.PlanningFact;
import net.algem.planning.fact.services.PlanningFactDAO;
import net.algem.util.GemLogger;
import net.algem.util.Option;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SQLErrorDlg;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
        $$$setupUI$$$();
        dao = desktop.getDataCache().getPlanningFactDAO();
        refresh();
        appliquerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedFact();
            }
        });
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
                getDateFr(datePickerStart), getDateFr(datePickerEnd)
        );
    }

    private static Option<DateFr> getDateFr(JXDatePicker datePickerStart) {
        return datePickerStart.getDate() != null ? Option.of(new DateFr(datePickerStart.getDate())) : Option.<DateFr>none();
    }

    private static Option<Integer> parseInt(JTextField textField) {
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


    public static TableModel resultSetToTableModel(ResultSet rs) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int numberOfColumns = metaData.getColumnCount();
        Vector columnNames = new Vector();

        // Get the column names
        for (int column = 0; column < numberOfColumns; column++) {
            columnNames.addElement(metaData.getColumnLabel(column + 1));
        }

        // Get all rows.
        Vector rows = new Vector();

        while (rs.next()) {
            Vector newRow = new Vector();

            for (int i = 1; i <= numberOfColumns; i++) {
                newRow.addElement(rs.getObject(i));
            }

            rows.addElement(newRow);
        }

        return new DefaultTableModel(rows, columnNames);
    }

    private void createUIComponents() {
        datePickerStart = new JXDatePicker();
        datePickerEnd = new JXDatePicker();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        datePickerStart.setFormats(dateFormat);
        datePickerEnd.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
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
                    SQLErrorDlg.displayException(getPanel(), "Erreur de suppression", e);
                }
            }
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 5, new Insets(8, 16, 8, 16), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Date début");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Date fin");
        panel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ajouterButton = new JButton();
        ajouterButton.setText("Ajouter");
        panel1.add(ajouterButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modifierButton = new JButton();
        modifierButton.setText("Modifier");
        panel1.add(modifierButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        supprimerButton = new JButton();
        supprimerButton.setText("Supprimer");
        panel1.add(supprimerButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JLabel label3 = new JLabel();
        label3.setText("Id prof");
        panel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldIdProf = new JTextField();
        panel.add(textFieldIdProf, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Id planning");
        panel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldIdPlanning = new JTextField();
        textFieldIdPlanning.setText("");
        panel.add(textFieldIdPlanning, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel.add(datePickerStart, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel.add(datePickerEnd, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appliquerButton = new JButton();
        appliquerButton.setText("Appliquer");
        panel.add(appliquerButton, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
