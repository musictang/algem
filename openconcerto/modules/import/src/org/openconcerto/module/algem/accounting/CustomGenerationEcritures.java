/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016-2017 Musiques Tangentes. All rights reserved.
 *
 * The contents of this file are subject to the terms of the GNU General Public License Version 3
 * only ("GPL"). You may not use this file except in compliance with the License. You can obtain a
 * copy of the License at http://www.gnu.org/licenses/gpl-3.0.html See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each file.
 */
package org.openconcerto.module.algem.accounting;
import org.openconcerto.erp.config.ComptaPropsConfiguration;
import org.openconcerto.erp.generationEcritures.GenerationEcritures;
import org.openconcerto.sql.Configuration;
import org.openconcerto.sql.model.SQLRow;
import org.openconcerto.sql.model.SQLRowValues;
import org.openconcerto.sql.model.SQLSelect;
import org.openconcerto.sql.model.SQLTable;
import org.openconcerto.sql.users.UserManager;
import org.openconcerto.utils.ExceptionHandler;

import java.sql.SQLException;
import java.util.Date;
import javax.swing.SwingUtilities;
import org.openconcerto.sql.model.SQLField;

/**
 * Implémentation de la classe GenerationEcritures.
 * Generation des ecritures comptables, permet l'ajout d'ecriture, la creation des mouvements
 *
 * @author Administrateur
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.1 19/09/17
 * @since 1.0 15/12/2016
 * @see org.openconcerto.erp.generationEcritures.GenerationEcritures
 */
public class CustomGenerationEcritures extends GenerationEcritures{

    private SQLRow rowAnalytiqueSource;

    /**
     * Ajout d'une écriture et maj des totaux du compte associé
     *
     * @return Id de l'ecriture crée
     * @throws IllegalArgumentException
     */
    @Override
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

        SQLTable tableExercice = base.getTable("EXERCICE_COMMON");
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

                // Analytique
                SQLTable tablePosteAnalytique = Configuration.getInstance().getBase().getTable("POSTE_ANALYTIQUE");
                SQLSelect selectPosteId = new SQLSelect();
                selectPosteId.addSelect(tablePosteAnalytique.getField("ID"));
                SQLField paNameField = tablePosteAnalytique.getField("NOM");
                selectPosteId.setWhere(new org.openconcerto.sql.model.Where(paNameField, "=", analytique));
                final Number pn = (Number) tablePosteAnalytique.getBase().getDataSource().executeScalar(selectPosteId.asString());
                int posteId = 1;//default if null or not found
                if (pn != null) {
                	posteId = pn.intValue();
                }
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

}
