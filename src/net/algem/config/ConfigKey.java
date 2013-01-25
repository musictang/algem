/*
 * @(#)ConfigKey.java 2.4.b 30/05/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.4.b
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

  /** Standing order issuer. */
  STANDING_ORDER_ISSUER("Compta.Prelevement.emetteur", BundleUtil.getLabel("ConfEditor.debiting.issuer.label")),
  
  /** Branch bank. */
  STANDING_ORDER_BANK_BRANCH("Compta.Prelevement.guichet", BundleUtil.getLabel("ConfEditor.debiting.branch.label")),
  
  /** Firm name. */
  STANDING_ORDER_FIRM_NAME("Compta.Prelevement.raison", BundleUtil.getLabel("ConfEditor.corporate.label")),

  /** Account. */
  STANDING_ORDER_ACCOUNT("Compta.Prelevement.compte", BundleUtil.getLabel("Account.label")),
  
  /** Bankhouse. */
  STANDING_ORDER_BANKHOUSE_CODE("Compta.Prelevement.etablissement", BundleUtil.getLabel("Establishment.label")),

  /** Document number. */
  ACCOUNTING_DOCUMENT_NUMBER("Compta.Numero.Piece", BundleUtil.getLabel("Document.number.label")),

  /** First invoice number. */
  ACCOUNTING_INVOICE_NUMBER("Compta.Numero.Facture", BundleUtil.getLabel("ConfEditor.invoice.number")),

  /** Default school. */
  DEFAULT_SCHOOL("Ecole.par.defaut", BundleUtil.getLabel("ConfEditor.default.school")),

  /** Default establishment. */
  DEFAULT_ESTABLISHMENT("Etablissement.par.defaut", BundleUtil.getLabel("ConfEditor.default.establishment"));
  
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
