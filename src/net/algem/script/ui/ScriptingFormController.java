package net.algem.script.ui;

import net.algem.script.common.Script;
import net.algem.script.common.ScriptArgument;
import net.algem.script.directory.ScriptDirectoryService;
import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptImplFile;
import net.algem.script.execution.ScriptExecutorService;
import net.algem.script.execution.ScriptExportService;
import net.algem.script.execution.models.ScriptResult;
import net.algem.script.execution.models.ScriptUserArguments;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SQLErrorDlg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class ScriptingFormController {
    private JPanel panel1;
    private JTree tree1;
    private JButton runButton;
    private JTable resultTable;
    private JTableX argumentsTable;
    private JProgressBar progressBar1;
    private JButton buttonExport;
    private JProgressBar progressBar2;
    private JTextArea labelDescription;

    private ScriptDirectoryService scriptDirectoryService;
    private ScriptExecutorService scriptExecutorService;
    private Script script;
    private ScriptArgumentTableModel argumentTableModel;
    private ScriptResult scriptResult;
    private final ScriptExportService scriptExportService;

    public ScriptingFormController(GemDesktop desktop) {
        DataCache dataCache = desktop.getDataCache();
        scriptDirectoryService = dataCache.getScriptDirectoryService();
        scriptExecutorService = dataCache.getScriptExecutorService();
        scriptExportService = dataCache.getScriptExportService();

        setupUI();
        loadScripts();

        tree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    TreePath selPath = tree1.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        Object selectedComponent = selPath.getLastPathComponent();
                        if (selectedComponent instanceof ScriptImplFile) {
                            openScript((ScriptImplFile) selectedComponent);
                        }
                    }
                }
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScript();
            }
        });

        buttonExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportScriptResult();
            }
        });
    }

    private void runScript() {
        if (script != null && argumentTableModel != null) {
            final ScriptUserArguments userArguments = argumentTableModel.getUserArguments();
            progressBar1.setVisible(true);
            new Thread() {
                @Override
                public void run() {
                    try {
                        scriptResult = scriptExecutorService.executeScript(script, userArguments);
                        resultTable.setModel(new ScriptResultTableModel(scriptResult));
                    } catch (Exception e) {
                        e.printStackTrace(); // TODO show error message
                        SQLErrorDlg.displayException(getPanel(), "Erreur durant l'éxécution du script", e);
                    }
                    progressBar1.setVisible(false);
                }
            }.start();

        }
    }

    private void exportScriptResult() {
        if (scriptResult != null) {
            final File outFile = FileUtil.getSaveFile(getPanel(), "csv", "Fichiers CSV", "export.csv");
            progressBar2.setVisible(true);
            new Thread() {
                @Override
                public void run() {
                    try {
                        scriptExportService.exportScriptResult(scriptResult, outFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SQLErrorDlg.displayException(getPanel(), "Erreur durant l'exportation du script", e);
                    }
                    progressBar2.setVisible(false);
                }
            }.start();
        }
    }

    private void openScript(ScriptImplFile scriptFile) {
        try {
            script = scriptDirectoryService.loadScript(scriptFile);
            labelDescription.setText(script.getDescription());
            List<ScriptArgument> arguments = script.getArguments();
            argumentTableModel = new ScriptArgumentTableModel(arguments);
            argumentsTable.setModel(argumentTableModel);
            argumentsTable.setCellEditorFactory(new ScriptArgumentTableModel.MyCellEditorFactory(arguments));
            argumentsTable.getColumnModel().getColumn(1).setCellRenderer(new ScriptArgumentTableModel.MyCellRenderer(arguments));
            resultTable.setModel(new DefaultTableModel());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JPanel getPanel() {
        return panel1;
    }

    private void loadScripts() {
        ScriptDirectory availableScripts = scriptDirectoryService.getAvailableScripts();
        tree1.setModel(new ScriptDirectoryTreeModel(availableScripts));
    }

    private void createUIComponents() {
        tree1 = new JTree() {
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof ScriptDirectory) {
                    return ((ScriptDirectory) value).getDirectory().getName();
                } else if (value instanceof ScriptImplFile) {
                    return ((ScriptImplFile) value).getCodeFile().getName();
                } else {
                    return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
                }
            }
        };
    }

    private void setupUI() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMinimumSize(new Dimension(1024, 768));
        panel1.setOpaque(true);
        panel1.setPreferredSize(new Dimension(1024, 768));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(250);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(splitPane1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        splitPane1.setLeftComponent(panel2);
        panel2.setBorder(BorderFactory.createTitledBorder("Scripts disponible"));
        tree1.setMaximumSize(new Dimension(250, 100));
        tree1.setPreferredSize(new Dimension(250, 100));
        tree1.setRequestFocusEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(tree1, gbc);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(800, 800));
        splitPane1.setRightComponent(rightPanel);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.insets = new Insets(6, 6, 6, 6);
        rightPanel.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder("Résultats"));
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.insets = new Insets(6, 6, 6, 6);
        panel3.add(scrollPane1, gbc);
        resultTable = new JTable();
        scrollPane1.setViewportView(resultTable);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.insets = new Insets(6, 6, 6, 6);
        rightPanel.add(panel4, gbc);
        panel4.setBorder(BorderFactory.createTitledBorder("Paramètres"));
        argumentsTable = new JTableX();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 6;
        gbc.ipady = 6;
        gbc.insets = new Insets(6, 6, 6, 6);
        panel4.add(argumentsTable, gbc);
        runButton = new JButton();
        runButton.setText("Executer");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 8);
        rightPanel.add(runButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        rightPanel.add(spacer1, gbc);
        progressBar1 = new JProgressBar();
        progressBar1.setIndeterminate(true);
        progressBar1.setPreferredSize(new Dimension(40, 12));
        progressBar1.setString("En cours");
        progressBar1.setStringPainted(true);
        progressBar1.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(progressBar1, gbc);
        buttonExport = new JButton();
        buttonExport.setText("Exporter");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 8);
        rightPanel.add(buttonExport, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        rightPanel.add(spacer2, gbc);
        progressBar2 = new JProgressBar();
        progressBar2.setIndeterminate(true);
        progressBar2.setPreferredSize(new Dimension(40, 12));
        progressBar2.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(progressBar2, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 6, 6, 6);
        rightPanel.add(panel5, gbc);
        panel5.setBorder(BorderFactory.createTitledBorder("Description"));
        labelDescription = new JTextArea();
        labelDescription.setEditable(false);
        labelDescription.setFont(UIManager.getFont("Label.font"));
        labelDescription.setLineWrap(true);
        labelDescription.setRows(0);
        labelDescription.setText("");
        labelDescription.setWrapStyleWord(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(labelDescription, gbc);
    }
}
