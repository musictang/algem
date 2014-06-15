/*
 * @(#)ConfigKey.java 2.8.v 12/06/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.config;

import net.algem.util.BundleUtil;

/**
 * Configuration keys enumeration.
 * First arg represents key in the table, second arg represents the label.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.1.k 07/07/11
 */
public enum ConfigKey
{

  /** Organization name. */
  ORGANIZATION_NAME("Organisation.Nom", BundleUtil.getLabel("ConfEditor.organization.name.label")),

  /** Organization first address. */
  ORGANIZATION_ADDRESS1("Organisation.Adresse1", BundleUtil.getLabel("ConfEditor.organization.address1.label")),

  /** Organization second address. */
  ORGANIZATION_ADDRESS2("Organisation.Adresse2", BundleUtil.getLabel("ConfEditor.organization.address2.label")),

  /** Organization postal code. */
  ORGANIZATION_ZIPCODE("Organisation.Cdp", BundleUtil.getLabel("ConfEditor.organization.zipcode.label")),

  /** Organization city. */
  ORGANIZATION_CITY("Organisation.Ville", BundleUtil.getLabel("ConfEditor.organization.city.label")),

   /** SIRET number. */
  SIRET_NUMBER("Numero.SIRET", BundleUtil.getLabel("ConfEditor.organization.SIRET.number.label")),

  /** NAF Code. */
  CODE_NAF("Code.NAF", BundleUtil.getLabel("ConfEditor.organization.Code.NAF.label")),

   /** VAT Code. */
  CODE_TVA("Code.TVA", BundleUtil.getLabel("ConfEditor.organization.Code.VAT.label")),

  /** Pro formation code. */
  CODE_FP("Code.FP", BundleUtil.getLabel("ConfEditor.organization.Code.FP.label")),

  /** Start of school year. */
  BEGINNING_YEAR("Date.DebutAnnee", BundleUtil.getLabel("ConfEditor.date.start.label")),

  /** End of school year. */
  END_YEAR("Date.FinAnnee", BundleUtil.getLabel("ConfEditor.date.end.label")),

  /** Start of period. */
  BEGINNING_PERIOD("Date.DebutPeriode", BundleUtil.getLabel("ConfEditor.date.start.period.label")),

  /** End of period. */
  END_PERIOD("Date.FinPeriode", BundleUtil.getLabel("ConfEditor.date.end.period.label")),

  /** End peak hour - start full rate for rehearsals. */
  OFFPEAK_HOUR("FinHeureCreuse", BundleUtil.getLabel("ConfEditor.offpeak.time.label")),

  /** Teacher management. */
  TEACHER_MANAGEMENT("GestionProf", BundleUtil.getLabel("ConfEditor.teacher.management.label")),

  /** Course management. */
  COURSE_MANAGEMENT("GestionCours", BundleUtil.getLabel("ConfEditor.course.management.label")),

  /** Workshop management. */
  WORKSHOP_MANAGEMENT("GestionAtelier", BundleUtil.getLabel("ConfEditor.workshop.management.label")),

  /** Log path. */
  LOG_PATH("LogPath", BundleUtil.getLabel("ConfEditor.log.path.label")),

  /** Export path. */
  EXPORT_PATH("ExportPath", BundleUtil.getLabel("ConfEditor.export.path.label")),

  /** Direct debit creditor NNE. */
  DIRECT_DEBIT_CREDITOR_NNE("Compta.prelevement.emetteur", BundleUtil.getLabel("ConfEditor.debiting.issuer.label")),

  /** Direct debit creditor's bank branch code. */
  DIRECT_DEBIT_BANK_BRANCH("Compta.prelevement.guichet", BundleUtil.getLabel("ConfEditor.debiting.branch.label")),

  /** Direct debit creditor's firm name. */
  DIRECT_DEBIT_FIRM_NAME("Compta.prelevement.raison", BundleUtil.getLabel("ConfEditor.corporate.label")),

  /** Direct debit creditor's account. */
  DIRECT_DEBIT_ACCOUNT("Compta.prelevement.compte", BundleUtil.getLabel("Account.label")),

  /** Direct debit creditor's bankhouse code. */
  DIRECT_DEBIT_BANKHOUSE_CODE("Compta.prelevement.etablissement", BundleUtil.getLabel("Establishment.label")),

	/** Direct debit creditor's ICS. */
  DIRECT_DEBIT_ICS("Compta.prelevement.ics", BundleUtil.getLabel("ConfEditor.debiting.ics.label")),

	/** Direct debit creditor's IBAN. */
  DIRECT_DEBIT_IBAN("Compta.prelevement.iban", BundleUtil.getLabel("Iban.label")),

	/** Direct debit creditor's BIC. */
  DIRECT_DEBIT_BIC("Compta.prelevement.bic", BundleUtil.getLabel("Bic.code.label")),

  /** Document number. */
  ACCOUNTING_DOCUMENT_NUMBER("Compta.Numero.Piece", BundleUtil.getLabel("Document.number.label")),

  /** First invoice number. */
  ACCOUNTING_INVOICE_NUMBER("Compta.Numero.Facture", BundleUtil.getLabel("ConfEditor.invoice.number")),

  /** Default school. */
  DEFAULT_SCHOOL("Ecole.par.defaut", BundleUtil.getLabel("ConfEditor.default.school")),

  /** Default establishment. */
  DEFAULT_ESTABLISHMENT("Etablissement.par.defaut", BundleUtil.getLabel("ConfEditor.default.establishment")),

  DEFAULT_STUDIO("Studio.par.defaut", BundleUtil.getLabel("ConfEditor.default.studio")),

  /** Photos path. */
  PHOTOS_PATH("Dossier.Photos", BundleUtil.getLabel("ConfEditor.photos.path.label")),

  EMPLOYEES_PATH("Dossier.Salaries", BundleUtil.getLabel("ConfEditor.employees.path.label")),

  GROUPS_PATH("Dossier.Groupes", BundleUtil.getLabel("ConfEditor.groups.path.label")),

  INVOICE_FOOTER("Pied.de.page.Facture", BundleUtil.getLabel("Menu.invoice.footer.label")),

  ACCOUNTING_EXPORT_FORMAT("Compta.format.export", BundleUtil.getLabel("ConfEditor.accounting.export.format.label")),

  ACCOUNTING_DOSSIER_NAME("Compta.nom.dossier", BundleUtil.getLabel("ConfEditor.accounting.export.dossier.label")),

  ORGANIZATION_DOMAIN("Organisation.domaine", BundleUtil.getLabel("ConfEditor.organization.domain.label"));

  private final String key;
  private final String label;

  ConfigKey(String key, String label) {
    this.key = key;
    this.label = label;
  }

  public String getKey() {
    return key;
  }

  public String getLabel() {
    return label;
  }
}
