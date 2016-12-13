package org.openconcerto.module.algem.accounting;
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 OpenConcerto, by ILM Informatique. All rights reserved.
 *
 * The contents of this file are subject to the terms of the GNU General Public License Version 3
 * only ("GPL"). You may not use this file except in compliance with the License. You can obtain a
 * copy of the License at http://www.gnu.org/licenses/gpl-3.0.html See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each file.
 */

import org.openconcerto.erp.config.ComptaPropsConfiguration;
import org.openconcerto.erp.core.common.element.BanqueSQLElement;
import org.openconcerto.erp.core.common.ui.TotalCalculator;
import org.openconcerto.erp.core.finance.accounting.element.ComptePCESQLElement;
import org.openconcerto.erp.core.finance.accounting.element.JournalSQLElement;
import org.openconcerto.erp.generationEcritures.AnalytiqueCache;
import org.openconcerto.erp.generationEcritures.provider.AnalytiqueProvider;
import org.openconcerto.erp.generationEcritures.provider.AnalytiqueProviderManager;
import org.openconcerto.erp.preferences.DefaultNXProps;
import org.openconcerto.erp.preferences.GestionPieceCommercialePanel;
import org.openconcerto.sql.Configuration;
import org.openconcerto.sql.model.SQLBase;
import org.openconcerto.sql.model.SQLRow;
import org.openconcerto.sql.model.SQLRowAccessor;
import org.openconcerto.sql.model.SQLRowValues;
import org.openconcerto.sql.model.SQLSelect;
import org.openconcerto.sql.model.SQLTable;
import org.openconcerto.sql.users.UserManager;
import org.openconcerto.utils.ExceptionHandler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openconcerto.sql.model.IResultSetHandler;
import org.openconcerto.sql.model.SQLDataSource;
import org.openconcerto.sql.model.SQLField;

/**
 * Generation des ecritures comptables, permet l'ajout d'ecriture, la creation des mouvements
 *
 * @author Administrateur
 *
 */
public class CustomGenerationEcritures {

    protected static final SQLBase base = ((ComptaPropsConfiguration) Configuration.getInstance()).getSQLBaseSociete();
    protected static final SQLTable compteTable = base.getTable("COMPTE_PCE");
    protected static final SQLTable journalTable = base.getTable("JOURNAL");
    protected static final SQLTable ecritureTable = base.getTable("ECRITURE");
    protected static final SQLTable pieceTable = base.getTable("PIECE");
    protected static final SQLTable mouvementTable = base.getTable("MOUVEMENT");

    protected int idMvt;
    protected int idPiece;

    // Date d'ecriture
    protected Date date;

    // Libelle de l'ecriture
    protected String nom;

    private SQLRow rowAnalytiqueSource;

    // Map contenant les valeurs pour la SQLRowValues de la table Ecritures à ajouter
    public Map<String, Object> mEcritures = new HashMap<String, Object>();

    // public GenerationEcritures(SQLRow rowSource) {
    // this.rowAnalytiqueSource = rowSource;
    // }

    public void setRowAnalytiqueSource(SQLRow rowAnalytiqueSource) {
        this.rowAnalytiqueSource = rowAnalytiqueSource;
    }

    public SQLRow getRowAnalytiqueSource() {
        return this.rowAnalytiqueSource;
    }

    /**
     * Ajout d'une écriture et maj des totaux du compte associé
     *
     * @return Id de l'ecriture crée
     * @throws IllegalArgumentException
     */
    synchronized public SQLRow ajoutEcriture() throws IllegalArgumentException {

        long debit = ((Long) this.mEcritures.get("DEBIT")).longValue();
        long credit = ((Long) this.mEcritures.get("CREDIT")).longValue();

        // Report des valeurs pour accelerer les IListes
        Number n = (Number) this.mEcritures.get("ID_JOURNAL");
        if (n != null) {
            SQLRow rowJrnl = journalTable.getRow(n.intValue());
            if (rowJrnl == null) {
                throw new IllegalArgumentException("Le journal qui a pour ID " + n + " a été archivé.");
            }
            this.mEcritures.put("JOURNAL_NOM", rowJrnl.getString("NOM"));
            this.mEcritures.put("JOURNAL_CODE", rowJrnl.getString("CODE"));
        }

        Number n2 = (Number) this.mEcritures.get("ID_COMPTE_PCE");
        if (n2 != null) {
            SQLRow rowCpt = compteTable.getRow(n2.intValue());
            this.mEcritures.put("COMPTE_NUMERO", rowCpt.getString("NUMERO"));
            this.mEcritures.put("COMPTE_NOM", rowCpt.getString("NOM"));
        }

        if (debit != 0 && credit != 0) {
            // ExceptionHandler.handle("Le débit et le crédit ne peuvent pas être tous les 2
            // différents de 0. Debit : " + debit + " Credit : " + credit);
            throw new IllegalArgumentException("Le débit et le crédit ne peuvent pas être tous les 2 différents de 0. Debit : " + debit + " Credit : " + credit);
            // return -1;
        }

        if (debit < 0) {
            credit = -debit;
            debit = 0;
        }
        if (credit < 0) {
            debit = -credit;
            credit = 0;
        }

        this.mEcritures.put("DEBIT", Long.valueOf(debit));
        this.mEcritures.put("CREDIT", Long.valueOf(credit));

        // TODO checker que les ecritures sont entrees à une date correcte
        Date d = (Date) this.mEcritures.get("DATE");

        SQLTable tableExercice = Configuration.getInstance().getBase().getTable("EXERCICE_COMMON");
        SQLRow rowSociete = ((ComptaPropsConfiguration) Configuration.getInstance()).getRowSociete();
        SQLRow rowExercice = tableExercice.getRow(rowSociete.getInt("ID_EXERCICE_COMMON"));
        Date dDebEx = (Date) rowExercice.getObject("DATE_DEB");

        Date dCloture = (Date) rowExercice.getObject("DATE_CLOTURE");

        if (dCloture != null) {
            if (dCloture.after(d)) {
                System.err.println("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
                // ExceptionHandler.handle("Impossible de générer l'écriture pour la date " + d + ".
                // Cette période est cloturée.");
                throw new IllegalArgumentException("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
                // return -1;
            }
        } else {
            if (dDebEx.after(d)) {
                System.err.println("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
                // ExceptionHandler.handle("Impossible de générer l'écriture pour la date " + d + ".
                // Cette période est cloturée.");
                // return -1;
                throw new IllegalArgumentException("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
            }
        }
String analytique = (String) mEcritures.remove("POSTE_ANALYTIQUE_NOM");
        final SQLRowValues valEcriture = new SQLRowValues(CustomGenerationEcritures.ecritureTable, this.mEcritures);
        valEcriture.put("IDUSER_CREATE", UserManager.getInstance().getCurrentUser().getId());

        try {
            if (valEcriture.getInvalid() == null) {
                // ajout de l'ecriture
                SQLRow ecritureRow = valEcriture.insert();

                // TODO Analytique
SQLTable tablePosteAnalytique = Configuration.getInstance().getBase().getTable("POSTE_ANALYTIQUE");
SQLSelect selectPosteId = new SQLSelect();
selectPosteId.addSelect(tablePosteAnalytique.getField("ID"));
SQLField paNameField = tablePosteAnalytique.getField("NOM");
selectPosteId.setWhere(new org.openconcerto.sql.model.Where(paNameField, "=", analytique));
System.out.println(selectPosteId.asString());
final Number pn = (Number) tablePosteAnalytique.getBase().getDataSource().executeScalar(selectPosteId.asString());
if (pn == null) {
	//return null;
	throw new IllegalArgumentException(selectPosteId.asString());
}
int posteId = pn.intValue();

                //addAssocAnalytiqueFromProvider(ecritureRow, this.rowAnalytiqueSource);
                addAssocAnalytique(ecritureRow,posteId); // jm
                return ecritureRow;
            } else {
                System.err.println("GenerationEcritures.java :: Error in values for insert in table " + CustomGenerationEcritures.ecritureTable.getName() + " : " + valEcriture.toString());
                throw new IllegalArgumentException("Erreur lors de la génération des écritures données incorrectes. " + valEcriture);
            }
        } catch (SQLException e) {
            System.err.println("Error insert row in " + CustomGenerationEcritures.ecritureTable.getName() + " : " + e);
            final SQLException eFinal = e;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ExceptionHandler.handle("Erreur lors de la génération des écritures.", eFinal);
                }
            });
            e.printStackTrace();
        }

        return null;
    }

    private static SQLTable tableAssoc = null;
    private static SQLTable tablePoste = Configuration.getInstance().getDirectory().getElement("POSTE_ANALYTIQUE").getTable();

    public void addAssocAnalytique(SQLRow rowEcr, int idPoste) throws SQLException {
        if (tablePoste.getUndefinedID() == idPoste) {
            return;
        }
        if (tableAssoc == null) {
            tableAssoc = Configuration.getInstance().getDirectory().getElement("ASSOCIATION_ANALYTIQUE").getTable();
        }
        SQLRowValues rowVals = new SQLRowValues(tableAssoc);
        rowVals.put("ID_POSTE_ANALYTIQUE", idPoste);
        rowVals.put("POURCENT", 100.0);
        rowVals.put("ID_ECRITURE", rowEcr.getID());
        rowVals.put("MONTANT", rowEcr.getLong("DEBIT") - rowEcr.getLong("CREDIT"));
        rowVals.commit();

    }

    public void addAssocAnalytiqueFromProvider(SQLRow rowEcr, SQLRow rowSource) throws SQLException {

        String numeroCpt = rowEcr.getString("COMPTE_NUMERO");
        if (rowSource != null && !rowSource.getTable().getName().equalsIgnoreCase("SAISIE_KM") && (numeroCpt.startsWith("6") || numeroCpt.startsWith("7"))) {
            for (SQLRow axe : AnalytiqueCache.getCache().getAxes()) {
                // TODO Check if total = 100%
                int assoc = 0;
                Collection<AnalytiqueProvider> l = AnalytiqueProviderManager.getAll();
                for (AnalytiqueProvider analytiqueProvider : l) {
                    List<SQLRow> result = analytiqueProvider.addAssociation(axe, rowEcr, rowSource);
                    if (result != null) {
                        assoc += result.size();
                    }
                }

                if (assoc == 0) {
                    SQLRow poste = AnalytiqueCache.getCache().getDefaultPoste(axe.getID());
                    if (poste != null) {
                        if (tableAssoc == null) {
                            tableAssoc = Configuration.getInstance().getDirectory().getElement("ASSOCIATION_ANALYTIQUE").getTable();
                        }
                        SQLRowValues rowVals = new SQLRowValues(tableAssoc);
                        rowVals.put("ID_POSTE_ANALYTIQUE", poste.getID());
                        rowVals.put("POURCENT", 100.0);
                        rowVals.put("ID_ECRITURE", rowEcr.getID());
                        rowVals.put("MONTANT", rowEcr.getLong("DEBIT") - rowEcr.getLong("CREDIT"));
                        rowVals.put("GESTION_AUTO", Boolean.TRUE);
                        rowVals.commit();
                    }
                }
            }
        }
    }

    /**
     * Génération d'un groupe d'écritures respectant la partie double.
     *
     * @param l liste de SQLRowValues d'ecritures
     */
    public void genereEcritures(List<SQLRowValues> l) {

        if (l == null) {
            return;
        }
        try {

            // Test de validité des écritures
            for (SQLRowValues rowVals : l) {
                checkDateValide(rowVals);
                checkDebitCreditValide(rowVals);
            }
            if (isSoldeNul(l)) {

                for (SQLRowValues rowVals : l) {

                    rowVals.put("IDUSER_CREATE", UserManager.getInstance().getCurrentUser().getId());

                    // ajout de l'ecriture
                    rowVals.insert();
                }
            } else {
                throw new IllegalArgumentException("La partie double n'est pas respectée. Impossible de générer les écritures comptables.");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    ExceptionHandler.handle("", e);
                }
            });
        }
    }

    private void checkDateValide(SQLRowValues rowVals) throws Exception {
        Date d = (Date) rowVals.getObject("DATE");

        SQLTable tableExercice = Configuration.getInstance().getBase().getTable("EXERCICE_COMMON");
        SQLRow rowSociete = ((ComptaPropsConfiguration) Configuration.getInstance()).getRowSociete();
        SQLRow rowExercice = tableExercice.getRow(rowSociete.getInt("ID_EXERCICE_COMMON"));
        Date dDebEx = (Date) rowExercice.getObject("DATE_DEB");

        Date dCloture = (Date) rowExercice.getObject("DATE_CLOTURE");

        if (dCloture != null) {
            if (dCloture.after(d)) {
                System.err.println("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
                throw new Exception("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
                // return -1;
            }
        } else {
            if (dDebEx.after(d)) {
                System.err.println("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
                throw new Exception("Impossible de générer l'écriture pour la date " + d + ". Cette période est cloturée.");
            }
        }
    }

    private void checkDebitCreditValide(SQLRowValues rowVals) throws Exception {
        long debit = rowVals.getLong("DEBIT");
        long credit = rowVals.getLong("CREDIT");

        if (debit != 0 && credit != 0) {
            // ExceptionHandler.handle("Le débit et le crédit ne peuvent pas être tous les 2
            // différents de 0. Debit : " + debit + " Credit : " + credit);
            throw new Exception("Le débit et le crédit ne peuvent pas être tous les 2 différents de 0. Debit : " + debit + " Credit : " + credit);
            // return -1;
        }

        if (debit < 0) {
            credit = -debit;
            debit = 0;
        }
        if (credit < 0) {
            debit = -credit;
            credit = 0;
        }

        rowVals.put("DEBIT", Long.valueOf(debit));
        rowVals.put("CREDIT", Long.valueOf(credit));
    }

    private boolean isSoldeNul(List<SQLRowValues> l) {

        long debit = 0;
        long credit = 0;
        for (SQLRowValues rowVals : l) {
            debit += rowVals.getLong("DEBIT");
            credit += rowVals.getLong("CREDIT");
        }
        return (debit - credit == 0);
    }

    /**
     * Crée un mouvement et une nouvelle piecé associée
     *
     * @param source
     * @param idSource
     * @param idPere
     * @param nomPiece
     * @return id d'un nouveau mouvement
     * @throws SQLException
     */
    synchronized public int getNewMouvement(String source, int idSource, int idPere, SQLRowValues rowValsPiece) throws SQLException {
        SQLRow rowPiece = rowValsPiece.insert();
        return getNewMouvement(source, idSource, idPere, rowPiece.getID());

    }

    synchronized public int getNewMouvement(String source, int idSource, int idPere, String nomPiece) throws SQLException {

        SQLRowValues rowValsPiece = new SQLRowValues(pieceTable);
        rowValsPiece.put("NOM", nomPiece);
        return getNewMouvement(source, idSource, idPere, rowValsPiece);
    }

    protected TotalCalculator getValuesFromElement(SQLRow row, SQLTable foreign, BigDecimal portHT, SQLRow rowTVAPort, SQLTable tableEchantillon) {
        return getValuesFromElement(false, "T_PV_HT", row, foreign, portHT, rowTVAPort, tableEchantillon, null);
    }

    protected TotalCalculator getValuesFromElement(boolean achat, String fieldTotalHT, SQLRow row, SQLTable foreign, BigDecimal portHT, SQLRow rowTVAPort, SQLTable tableEchantillon,
            SQLRow defaultCompte) {

        TotalCalculator calc = new TotalCalculator("T_PA_HT", fieldTotalHT, null, achat, defaultCompte);
        String val = DefaultNXProps.getInstance().getStringProperty("ArticleService");
        Boolean bServiceActive = Boolean.valueOf(val);

        calc.setServiceActive(bServiceActive != null && bServiceActive);
        if (row.getTable().contains("ID_COMPTE_PCE_SERVICE") && !row.isForeignEmpty("ID_COMPTE_PCE_SERVICE")) {
            SQLRowAccessor serviceCompte = row.getForeign("ID_COMPTE_PCE_SERVICE");
            if (!serviceCompte.isUndefined()) {
                calc.setRowDefaultCptService(serviceCompte);
            }
        }
        if (row.getTable().contains("ID_COMPTE_PCE_VENTE") && !row.isForeignEmpty("ID_COMPTE_PCE_VENTE")) {
            SQLRowAccessor produitCompte = row.getForeign("ID_COMPTE_PCE_VENTE");
            if (!produitCompte.isUndefined()) {
                calc.setRowDefaultCptProduit(produitCompte);
            }
        }
        long remise = 0;
        BigDecimal totalAvtRemise = BigDecimal.ZERO;
        if (row.getTable().contains("REMISE_HT")) {
            remise = row.getLong("REMISE_HT");
            if (remise != 0) {
                List<SQLRow> rows = row.getReferentRows(foreign);
                for (SQLRow sqlRow : rows) {
                    calc.addLine(sqlRow, sqlRow.getForeign("ID_ARTICLE"), 1, false);
                }

                if (tableEchantillon != null) {
                    List<SQLRow> rowsEch = row.getReferentRows(tableEchantillon);
                    for (SQLRow sqlRow : rowsEch) {
                        calc.addEchantillon((BigDecimal) sqlRow.getObject("T_PV_HT"), sqlRow.getForeign("ID_TAXE"));
                    }
                }
                calc.checkResult();
                totalAvtRemise = calc.getTotalHT();
            }
        }

        calc.initValues();
        calc.setRemise(remise, totalAvtRemise);

        List<SQLRow> rows = row.getReferentRows(foreign);
        for (int i = 0; i < rows.size(); i++) {
            SQLRow sqlRow = rows.get(i);
            calc.addLine(sqlRow, sqlRow.getForeign("ID_ARTICLE"), i, i == rows.size() - 1);
        }

        if (tableEchantillon != null) {
            List<SQLRow> rowsEch = row.getReferentRows(tableEchantillon);
            for (SQLRow sqlRow : rowsEch) {
                calc.addEchantillon((BigDecimal) sqlRow.getObject("T_PV_HT"), sqlRow.getForeign("ID_TAXE"));
            }
        }
        if (rowTVAPort != null && !rowTVAPort.isUndefined()) {
            SQLRowValues rowValsPort = new SQLRowValues(foreign);
            rowValsPort.put("T_PV_HT", portHT);
            rowValsPort.put("QTE", 1);
            rowValsPort.put("ID_TAXE", rowTVAPort.getIDNumber());

            final SQLTable tablePrefCompte = Configuration.getInstance().getRoot().findTable("PREFS_COMPTE");
            final SQLRow rowPrefsCompte = tablePrefCompte.getRow(2);
            SQLRow rowDefaultCptPort;
            if (rowTVAPort.getFloat("TAUX") > 0) {
                rowDefaultCptPort = rowPrefsCompte.getForeign("ID_COMPTE_PCE_PORT_SOUMIS");
                if (rowDefaultCptPort == null || rowDefaultCptPort.isUndefined()) {
                    try {
                        rowDefaultCptPort = ComptePCESQLElement.getRowComptePceDefault("PortVenteSoumisTVA");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                rowDefaultCptPort = rowPrefsCompte.getForeign("ID_COMPTE_PCE_PORT_NON_SOUMIS");
                if (rowDefaultCptPort == null || rowDefaultCptPort.isUndefined()) {
                    try {
                        rowDefaultCptPort = ComptePCESQLElement.getRowComptePceDefault("PortVenteNonSoumisTVA");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            rowValsPort.putRowValues("ID_ARTICLE").put("ID_COMPTE_PCE", rowDefaultCptPort.getID());

            calc.addLine(rowValsPort, rowValsPort.getForeign("ID_ARTICLE"), 1, false);
        }
        calc.checkResult();
        return calc;
    }

    /**
     * Crée un nouveau mouvement associé à la piece d'id idPiece
     *
     * @param source
     * @param idSource
     * @param idPere
     * @param idPiece
     * @return id d'un nouveau mouvement
     * @throws SQLException
     */
    synchronized public int getNewMouvement(String source, int idSource, int idPere, int idPiece) throws SQLException {

        SQLTable mouvementTable = base.getTable("MOUVEMENT");

        // on calcule le nouveau numero de mouvement
        SQLSelect selNumMvt = new SQLSelect();
        selNumMvt.addSelect(mouvementTable.getField("NUMERO"), "MAX");

        String reqNumMvt = selNumMvt.asString();
        Object obNumMvt = base.getDataSource().executeScalar(reqNumMvt);

        int numMvt = 1;
        if (obNumMvt != null) {
            numMvt = Integer.parseInt(obNumMvt.toString());
        }
        numMvt++;

        // Creation du mouvement
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("SOURCE", source);
        m.put("IDSOURCE", Integer.valueOf(idSource));
        m.put("ID_MOUVEMENT_PERE", Integer.valueOf(idPere));
        this.idPiece = idPiece;
        m.put("ID_PIECE", Integer.valueOf(idPiece));
        m.put("NUMERO", Integer.valueOf(numMvt));

        SQLRowValues val = new SQLRowValues(mouvementTable, m);

        if (val.getInvalid() == null) {
            SQLRow row = val.insert();
            this.idMvt = row.getID();
            this.mEcritures.put("ID_MOUVEMENT", Integer.valueOf(this.idMvt));
        } else {
            throw new IllegalStateException("Error in values for insert in table " + val.getTable().getName() + " : " + val.toString());
        }

        System.err.println("Numero de mouvement généré : " + numMvt);

        return this.idMvt;
    }

    protected void displayMvtNumber() {
        if (DefaultNXProps.getInstance().getBooleanValue(GestionPieceCommercialePanel.SHOW_MOUVEMENT_NUMBER, false)) {
            final int numero = mouvementTable.getRow(idMvt).getInt("NUMERO");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "N° de mouvement associé : " + numero);
                }
            });
        }

    }

    /**
     * Définit le journal en fonction de la banque sélectionnée
     *
     * @param sqlRow sqlRow contenant la foreignKey Banque
     */
    protected void fillJournalBanqueFromRow(SQLRow sqlRow) {
        this.mEcritures.put("ID_JOURNAL", JournalSQLElement.BANQUES);
        if (!sqlRow.isForeignEmpty("ID_" + BanqueSQLElement.TABLENAME)) {
            SQLRow rowBanque = sqlRow.getForeignRow("ID_" + BanqueSQLElement.TABLENAME);
            if (!rowBanque.isForeignEmpty("ID_JOURNAL")) {
                SQLRow rowJournal = rowBanque.getForeignRow("ID_JOURNAL");
                this.mEcritures.put("ID_JOURNAL", rowJournal.getID());
            }
        }
    }

    /**
     * Définit le compte en fonction de la banque sélectionnée
     *
     * @param sqlRow sqlRow contenant la foreignKey Banque
     * @throws Exception
     */
    protected void fillCompteBanqueFromRow(SQLRow sqlRow, String defaultProps, boolean achat) throws Exception {

        int idPce = base.getTable("TYPE_REGLEMENT").getRow(2).getInt("ID_COMPTE_PCE_" + (achat ? "FOURN" : "CLIENT"));
        if (idPce <= 1) {
            idPce = ComptePCESQLElement.getIdComptePceDefault(defaultProps);
        }
        if (!sqlRow.isForeignEmpty("ID_" + BanqueSQLElement.TABLENAME)) {
            SQLRow rowBanque = sqlRow.getForeignRow("ID_" + BanqueSQLElement.TABLENAME);
            if (!rowBanque.isForeignEmpty("ID_COMPTE_PCE")) {
                SQLRow rowCompteBanque = rowBanque.getForeignRow("ID_COMPTE_PCE");
                idPce = rowCompteBanque.getID();
            }
        }

        this.mEcritures.put("ID_COMPTE_PCE", idPce);
    }
}
