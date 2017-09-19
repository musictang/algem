/*
 * @(#)DirectDebitIO.java 2.15.0 18/09/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.bank.BranchIO;
import net.algem.bank.RibIO;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.AddressIO;
import net.algem.contact.OrganizationIO;
import net.algem.contact.PersonIO;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.15.0
 * @since 2.8.r 08/01/14
 */
public class DirectDebitIO
{

  public static final String TABLE = "prlsepa";
  static final String SEQUENCE = "prlsepa_id_seq";
  static final String COLUMNS = "id,payeur,lastdebit,signature,recurrent,seqtype,rum";
  private DataConnection dc;

  public DirectDebitIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Find the last unlocked mandate for the payer {@literal idper}.
   *
   * @param idper payer's id
   * @return a mandate
   * @throws SQLException
   */
  DDMandate getMandate(int idper) throws SQLException {
    String query = "SELECT s.*, p.nom FROM " + TABLE + " s JOIN " + PersonIO.TABLE + " p ON (s.payeur = p.id)"
            + " WHERE s.payeur = " + idper
            + " AND s.seqtype != '" + DDSeqType.LOCK.name() + "'"
            + " ORDER BY id DESC LIMIT 1";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      return getMandateFromRs(rs);
    }
    return null;
  }

  DDMandate getMandateWithBic(int idper) throws SQLException {
    String query = "SELECT s.*, g.bic, p.nom FROM " + TABLE + " s JOIN " + PersonIO.TABLE + " p ON (s.payeur = p.id)"
            + " LEFT JOIN " + RibIO.TABLE + " r ON (s.payeur = r.idper) LEFT JOIN " + BranchIO.TABLE + " g ON (r.guichetid = g.id)"
            + " WHERE s.payeur = " + idper
            + " AND s.seqtype != '" + DDSeqType.LOCK.name() + "'"
            + " ORDER BY id DESC LIMIT 1";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      DDMandate m = getMandateFromRs(rs);
      m.setBic(rs.getString(8));
      m.setName(rs.getString(9));
      return m;
    }
    return null;
  }

  /**
   * Find the list of all unlocked mandates.
   *
   * @return a list of mandates
   * @throws SQLException
   */
  List<DDMandate> getMandates() throws SQLException {
    List<DDMandate> mandates = new ArrayList<DDMandate>();
    String query = "SELECT s.*, CASE WHEN p.onom IS NOT NULL AND trim(p.onom) != '' THEN p.onom ELSE p.nom END FROM "
            + TABLE + " s, " + PersonIO.VIEW + " p"
            + " WHERE s.payeur = p.id"
            + " AND s.seqtype != '" + DDSeqType.LOCK.name()
            + "' ORDER BY s.payeur";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      DDMandate dd = getMandateFromRs(rs);
      mandates.add(dd);
    }
    return mandates;
  }

  /**
   * Find the list of all the mandates of the {@literal payer}.
   *
   * @param payer payer's id
   * @return a list of mandates
   * @throws SQLException
   */
  List<DDMandate> getMandates(int payer) throws SQLException {
    List<DDMandate> mandates = new ArrayList<DDMandate>();
    String query = "SELECT s.*, p.nom FROM " + TABLE + " s, " + PersonIO.TABLE + " p"
            + " WHERE s.payeur = " + payer
            + " AND s.payeur = p.id"
            + " ORDER BY s.signature";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      DDMandate dd = getMandateFromRs(rs);
      mandates.add(dd);
    }
    return mandates;
  }

  /**
   * Creates an object of type DDMandate from a result set.
   *
   * @param rs the result set
   * @return a mandate
   * @throws SQLException
   */
  private DDMandate getMandateFromRs(ResultSet rs) throws SQLException {
    DDMandate dd = new DDMandate(rs.getInt(2));
    dd.setId(rs.getInt(1));
    dd.setLastDebit(new DateFr(rs.getDate(3)));
    dd.setDateSign(new DateFr(rs.getDate(4)));
    dd.setRecurrent(rs.getBoolean(5));
    dd.setSeqType(DDSeqType.valueOf(rs.getString(6).trim()));
    dd.setRum(rs.getString(7));
    dd.setName(TableIO.unEscape(rs.getString(8)));

    return dd;
  }

  /**
   * Gets SEPA transaction info for this {@literal payer}.
   *
   * @param payer payer's id
   * @return a result set
   * @throws SQLException
   */
  ResultSet getDDTransaction(int payer) throws SQLException {
    String query = "SELECT p.id, p.civilite,"
            + " CASE WHEN p.organisation > 0 AND p.id = p.organisation THEN (CASE WHEN o.raison IS NULL OR o.raison = ''  THEN o.nom ELSE o.raison END) ELSE p.nom END"
            + ", p.prenom, a.adr1, a.adr2, a.cdp, a.ville, s.id, s.rum, s.signature, s.seqtype, r.iban, g.bic"
            + " FROM " + PersonIO.TABLE + " p LEFT JOIN " + OrganizationIO.TABLE + " o ON p.organisation = o.idper"
            + " LEFT JOIN " + AddressIO.TABLE + " a ON p.id = a.idper"
            + " JOIN " + RibIO.TABLE + " r ON p.id = r.idper"
            + " JOIN " + BranchIO.TABLE + " g ON r.guichetid = g.id"
            + " JOIN " + TABLE + " s ON p.id = s.payeur"
            + " WHERE p.id = " + payer + " AND s.seqtype != '" + DDSeqType.LOCK.name() + "'";
    GemLogger.info(query);
    //9 id, 10 rum, 11 sign, 12 seqtype, 13 iban, 14 bic
    return dc.executeQuery(query);
  }

  /**
   * Retrieves the creditor's data.
   *
   * @return a directDebitCreditor instance
   */
  DirectDebitCreditor getCreditorInfo() {

    DirectDebitCreditor creditor = new DirectDebitCreditor();

    creditor.setFirmName(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getKey()));
    creditor.setBankHouse(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_BANKHOUSE_CODE.getKey()));
    creditor.setBankBranch(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_BANK_BRANCH.getKey()));
    creditor.setAccount(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_ACCOUNT.getKey()));
    creditor.setNne(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_CREDITOR_NNE.getKey()));
    creditor.setIban(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_IBAN.getKey()));
    creditor.setBic(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_BIC.getKey()));
    creditor.setIcs(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_ICS.getKey()));

    return creditor;
  }

  /**
   * Retrieves the firm's name.
   *
   * @return a name
   */
  String getFirmName() {
    return ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getKey());
  }

  /**
   * Search all the players who will be charged on {@literal date}.
   *
   * @param school default school
   * @param date date of payment
   * @param seqType sequence type
   * @return a result set
   * @throws SQLException
   */
  ResultSet getDirectDebit(int school, DateFr date, Enum seqType) throws SQLException {
    String query = "SELECT e.payeur, e.montant, e.analytique FROM " + OrderLineIO.TABLE + " e, prlsepa p"
            + " WHERE e.ecole = '" + school
            + "' AND e.reglement = 'PRL"
            + "' AND e.paye = 't"
            + "' AND e.transfert = 'f"
            + "' AND e.echeance = '" + date.toString()
            + "' AND e.payeur = p.payeur"
            + " AND p.seqtype != '" + DDSeqType.LOCK.name() + "' AND p.seqtype = '" + seqType.name()
            + "' ORDER BY e.payeur, e.echeance";

    return dc.executeQuery(query);

  }

  /**
   * Stores a new mandate.
   *
   * @param dd the mandate to store
   * @return the created mandate
   * @throws SQLException
   */
  void createMandate(DDMandate dd) throws SQLException {

    dd.setId(TableIO.nextId(SEQUENCE, dc));

    String query = "INSERT INTO " + TABLE + " VALUES("
            + dd.getId()
            + ", " + dd.getIdper()
            + ", " + (dd.getLastDebit() == null || DateFr.NULLDATE.equals(dd.getDateSign().toString()) ? "NULL" : "'" + dd.getLastDebit() + "'")
            + ", '" + dd.getDateSign()
            + "', " + (dd.isRecurrent() ? "TRUE" : "FALSE")
            + ", '" + dd.getSeqType().name()
            + "', '" + dd.getRum()
            + "')";

    dc.executeUpdate(query);

  }

  /**
   * Updates a mandate.
   *
   * @param dd the mandate to update
   * @throws SQLException
   */
  void update(DDMandate dd) throws SQLException {
    String query = "UPDATE " + TABLE + " SET signature = '" + dd.getDateSign()
            + "', recurrent = " + (dd.isRecurrent() ? "TRUE" : "FALSE")
            + ", seqtype = '" + dd.getSeqType().name()
            + "', rum = '" + dd.getRum()
            + "' WHERE id = " + dd.getId();
    dc.executeUpdate(query);
  }

  /**
   * Updates the sequence type of all these {@literal mandates} to {@literal seqType}.
   *
   * @param mandates
   * @param seqType
   * @throws SQLException
   */
  void updateSeqType(List<DDMandate> mandates, DDSeqType seqType) throws SQLException {
    String query = "UPDATE " + TABLE + " SET seqtype = '" + seqType.name() + "' WHERE id IN(";
    StringBuilder mList = new StringBuilder();
    for (DDMandate dd : mandates) {
      mList.append(dd.getId()).append(',');
    }
    if (mList.length() > 0) {
      mList.deleteCharAt(mList.length() - 1);
      query += mList.toString() + ")";
      dc.executeUpdate(query);
    }

  }

  /**
   * Method called after a successful direct debit ordering.
   *
   * @param mandates list of mandates' id
   * @throws SQLException
   */
  void updateToRcurSeqType(String mandates) throws SQLException {
    String query = "UPDATE " + TABLE + " SET seqtype = '" + DDSeqType.RCUR.name()
            + "' WHERE id IN(" + mandates + ")"
            + " AND seqtype = '" + DDSeqType.FRST.name() + "'";
    dc.executeUpdate(query);
  }

  void updateLastDebit(String ddDate, String mandates) throws SQLException {
    String query = "UPDATE " + TABLE + " SET lastdebit = '" + ddDate
            +  "' WHERE id IN(" + mandates + ")";
    dc.executeUpdate(query);
  }

  /**
   * Optionnaly delete a mandate.
   *
   * @param id the mandate's id
   * @throws SQLException
   */
  void deleteMandate(int id) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE lastdebit IS NULL AND id = " + id;
    dc.executeUpdate(query.toString());
  }


}
