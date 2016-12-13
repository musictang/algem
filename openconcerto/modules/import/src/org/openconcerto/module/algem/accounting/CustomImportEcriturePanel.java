package org.openconcerto.module.algem.accounting;

import org.openconcerto.erp.config.ComptaPropsConfiguration;
import org.openconcerto.erp.core.finance.accounting.element.ComptePCESQLElement;
import org.openconcerto.erp.core.finance.accounting.element.JournalSQLElement;
import org.openconcerto.erp.core.finance.accounting.ui.SelectionJournalImportPanel;
import org.openconcerto.erp.generationEcritures.GenerationEcritures;
import org.openconcerto.erp.importer.ArrayTableModel;
import org.openconcerto.erp.importer.DataImporter;
import org.openconcerto.openoffice.ContentTypeVersioned;
import org.openconcerto.sql.Configuration;
import org.openconcerto.sql.model.ConnectionHandlerNoSetup;
import org.openconcerto.sql.model.DBRoot;
import org.openconcerto.sql.model.SQLDataSource;
import org.openconcerto.sql.model.SQLTable;
import org.openconcerto.sql.utils.SQLUtils;
import org.openconcerto.ui.DefaultGridBagConstraints;
import org.openconcerto.ui.ReloadPanel;
import org.openconcerto.ui.SwingThreadUtils;
import org.openconcerto.utils.ExceptionHandler;
import org.openconcerto.utils.GestionDevise;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CustomImportEcriturePanel extends JPanel {

    private final Map<String, Integer> mapJournal;
    private final SQLTable tableEcr;

    public CustomImportEcriturePanel() {
        super(new GridBagLayout());

        this.tableEcr = Configuration.getInstance().getDirectory().getElement("ECRITURE").getTable();
        this.mapJournal = new HashMap<String, Integer>();
        this.mapJournal.put("HA", JournalSQLElement.ACHATS);
        this.mapJournal.put("VE", JournalSQLElement.VENTES);
        this.mapJournal.put("BA", JournalSQLElement.BANQUES);
        this.mapJournal.put("CA", JournalSQLElement.CAISSES);
        this.mapJournal.put("OD", JournalSQLElement.OD);

        JLabel label = new JLabel("Import depuis un fichier CSV, XLS ou ODT.");
        JLabel label2 = new JLabel("Le fichier doit contenir les colonnes :");
        JLabel label3 = new JLabel(" - Date (format dd/MM/yy pour le CSV)");
        JLabel label4 = new JLabel(" - Journal");
        JLabel label5 = new JLabel(" - N° de compte");
        JLabel label6 = new JLabel(" - Nom de la pièce");
        JLabel label7 = new JLabel(" - Libellé");
        JLabel label8 = new JLabel(" - Débit");
        JLabel label9 = new JLabel(" - Crédit");
        final JButton button = new JButton("Sélectionner le ficher");
        GridBagConstraints c = new DefaultGridBagConstraints();
        c.gridwidth = 2;
        this.add(label, c);
        c.gridy++;
        this.add(label2, c);
        c.gridy++;
        this.add(label3, c);
        c.gridy++;
        this.add(label4, c);
        c.gridy++;
        this.add(label5, c);
        c.gridy++;
        this.add(label6, c);
        c.gridy++;
        this.add(label7, c);
        c.gridy++;
        this.add(label8, c);
        c.gridy++;
        this.add(label9, c);
        c.gridy++;
        c.gridwidth = 1;
        c.weightx = 1;
        final ReloadPanel rlPanel = new ReloadPanel();
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        this.add(rlPanel, c);
        c.gridx++;
        c.weightx = 0;
        this.add(button, c);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                final Frame frame = SwingThreadUtils.getAncestorOrSelf(Frame.class, CustomImportEcriturePanel.this);
                final FileDialog fd = new FileDialog(frame, "Import d'écritures", FileDialog.LOAD);
                fd.setFilenameFilter(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith("." + ContentTypeVersioned.SPREADSHEET.getExtension());
                    }
                });
                fd.setVisible(true);
                rlPanel.setMode(ReloadPanel.MODE_ROTATE);
                if (fd.getFile() != null) {

                    final DBRoot rootSociete = ((ComptaPropsConfiguration) ComptaPropsConfiguration.getInstance()).getRootSociete();
                    new Thread() {
                        public void run() {
                            try {
                                SQLUtils.executeAtomic(rootSociete.getDBSystemRoot().getDataSource(), new ConnectionHandlerNoSetup<Object, IOException>() {
                                    @Override
                                    public Object handle(final SQLDataSource ds) throws SQLException, IOException {

                                        try {
                                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                            importData(new File(fd.getDirectory(), fd.getFile()), "Import " + format.format(new Date()), frame);
                                        } catch (Exception exn) {
                                            if (exn.getMessage().toLowerCase().contains("file format")) {
                                                JOptionPane.showMessageDialog(CustomImportEcriturePanel.this, "Mauvais format de fichier");
                                            } else {
                                                ExceptionHandler.handle("Erreur pendant l'importation", exn);
                                            }
                                        }
                                        return null;
                                    }
                                });
                            } catch (IOException exn) {
                                ExceptionHandler.handle(frame, "Erreur lors de la lecture du fichier", exn);
                            } catch (SQLException exn) {
                                ExceptionHandler.handle(frame, "Erreur lors de l'insertion dans la base", exn);
                            }
                            frame.dispose();
                        }
                    }.start();
                }
            }
        });
    }

    public void importData(File f, String mvtName, final Frame owner) throws Exception {
        DataImporter importer = new DataImporter(this.tableEcr);
        importer.setSkipFirstLine(false);
        DateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
        ArrayTableModel m = importer.createModelFrom(f);

        CustomGenerationEcritures gen = new CustomGenerationEcritures();
        int idMvt = gen.getNewMouvement("", 1, 1, mvtName);

        long solde = 0;
        for (int i = 0; i < m.getRowCount(); i++) {

            final Object valueAt = m.getValueAt(i, 0);

            if (valueAt == null) {
                break;
            }
            final Date dateValue;
            if (valueAt.getClass().isAssignableFrom(Date.class)) {
                dateValue = (Date) valueAt;
            } else {
                if (valueAt.toString().trim().length() == 0) {
                    break;
                }
                dateValue = dF.parse(valueAt.toString());
            }
            // Mouvement
            gen.mEcritures.put("ID_MOUVEMENT", idMvt);

            // Cpt
            final String trim = m.getValueAt(i, 2).toString().trim();
            String numCompt = trim;
            if (trim.contains(".")) {
                numCompt = trim.substring(0, trim.indexOf('.'));
            }
            int idCpt = ComptePCESQLElement.getId(numCompt);
            gen.mEcritures.put("ID_COMPTE_PCE", idCpt);

            // Montant
            final String stringValueD = m.getValueAt(i, 5).toString();
            long montantD = GestionDevise.parseLongCurrency(stringValueD);
            final String stringValueC = m.getValueAt(i, 6).toString();
            long montantC = GestionDevise.parseLongCurrency(stringValueC);
            gen.mEcritures.put("CREDIT", montantC);
            gen.mEcritures.put("DEBIT", montantD);
            solde += montantD;
            solde -= montantC;

            System.err.println("(" + stringValueD + " : " + stringValueC + ") ---- (" + montantD + " : " + montantC + ")");
            // Journal

            final String valueJrnl = m.getValueAt(i, 1).toString();
            if (mapJournal.get(valueJrnl) == null) {

                try {
                    System.err.println("LOCKED");

                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            // try {
                            JDialog diag = new JDialog(owner);
                            diag.setModal(true);
                            diag.setContentPane(new SelectionJournalImportPanel(valueJrnl, mapJournal, null));
                            diag.setTitle("Import écritures");
                            diag.setLocationRelativeTo(null);
                            diag.pack();
                            diag.setVisible(true);
                        }
                    });

                    System.err.println("PASSED");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }

            gen.mEcritures.put("ID_JOURNAL", this.mapJournal.get(valueJrnl));

            // Date
            gen.mEcritures.put("DATE", dateValue);
gen.mEcritures.put("POSTE_ANALYTIQUE_NOM", m.getValueAt(i, 7).toString());
            String stringPiece = m.getValueAt(i, 3).toString();
            if (stringPiece != null && stringPiece.length() > 0 && stringPiece.contains(".")) {
                stringPiece = stringPiece.substring(0, stringPiece.indexOf('.'));
            }
            gen.mEcritures.put("NOM", m.getValueAt(i, 4).toString() + " " + stringPiece);

            gen.ajoutEcriture();
            
        }
        if (solde != 0) {
            throw new IllegalArgumentException("La partie double n'est respectée (solde = " + solde + "). Import annulé!");
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(owner, "Importation des écritures terminée");
            }
        });
    }

}

