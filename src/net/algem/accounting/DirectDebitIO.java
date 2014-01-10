/*
 * @(#)DirectDebitIO.java 2.8.r 09/01/14
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
package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.PersonIO;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.r
 * @since 2.8.r 08/01/14
 */
public class DirectDebitIO 

{
  static final String TABLE = "prlsepa";
  static final String COLUMNS = "id,payeur,creation,signature,recurrent,seqtype,rum";

  private DataConnection dc;

  public DirectDebitIO(DataConnection dc) {
    this.dc = dc;
  }
  
  ResultSet getDirectDebit(int school, DateFr datePrl, Enum seqType) throws SQLException {
    String query = "SELECT e.payeur, e.montant, e.analytique FROM echeancier2 e, prlsepa p"
            + " WHERE e.ecole = '" + school
            + "' AND e.reglement = 'PRL"
            + "' AND e.paye = 't"
            + "' AND e.echeance = '" + datePrl.toString()
            + "' AND e.payeur = p.payeur"
            + " AND p.seqtype != '" + DDSeqType.LOCK.name() + "' AND p.seqtype = '" + seqType.name() 
            + "' ORDER BY e.payeur, e.echeance";

    return dc.executeQuery(query);

  }
  
  /**
   * Method called after a successful direct debit ordering.
   * @param payers list of payers' id
   * @throws SQLException 
   */
  void updateToRcurSeqType(String payers) throws SQLException {
    String query = "UPDATE " + TABLE + " SET seqtype = '" + DDSeqType.RCUR
            + "' WHERE payeur IN(" + payers + ")"
            + " AND seqtype IN ('" + DDSeqType.FRST.name() + "', '" + DDSeqType.FMGR.name() + "', '" + DDSeqType.FDOM.name() + "')";
    dc.executeUpdate(query);
  }
  
  void update(DDMandate dd) throws SQLException {
    String query = "UPDATE " + TABLE + " SET signature = '" + dd.getDateSign() 
            + "', recurrent = " + (dd.isRecurrent() ? "TRUE" : "FALSE")
            + ", seqtype = '" + dd.getSeqType().name()
            + "', rum = '" + dd.getRum() 
            + "' WHERE id = " + dd.getId();
    dc.executeUpdate(query);
  }
  
  void updateSeqType(List<DDMandate> mandates, DDSeqType seqType) throws SQLException {
    StringBuilder query = new StringBuilder("UPDATE " + TABLE + " SET seqtype = '" + seqType.name() + "' WHERE id IN(");
    for (DDMandate dd : mandates) {
      query.append(dd.getId()).append(',');
    }
    if (query.length() > 0) {
      query.deleteCharAt(query.length()-1);
      query.append(')');
    }
    System.out.println(query);
    dc.executeUpdate(query.toString());
  }
  
  void deleteMandate(int id) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
    dc.executeUpdate(query.toString());
  }
  
  List<DDMandate> getMandates() throws SQLException {
    List<DDMandate> mandates = new ArrayList<DDMandate>();
    String query = "SELECT s.*, p.nom FROM " + TABLE + " s, " + PersonIO.TABLE + " p"
            + " WHERE s.payeur = p.id"
            + " AND s.seqtype != '" + DDSeqType.LOCK.name()
            + "' ORDER BY s.payeur";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      DDMandate dd = new DDMandate(rs.getInt(2));
      dd.setId(rs.getInt(1));
      dd.setCreation(new DateFr(rs.getDate(3)));
      dd.setDateSign(new DateFr(rs.getDate(4)));
      dd.setRecurrent(rs.getBoolean(5));
      dd.setSeqType(DDSeqType.valueOf(rs.getString(6).trim()));
      dd.setRum(rs.getString(7));
      dd.setName(TableIO.unEscape(rs.getString(8)));
      
      mandates.add(dd);
    }
    return mandates;
  }
  
  ResultSet getDDTransaction(int payer) throws SQLException {
    String query = "SELECT p.id, p.civilite, p.nom, p.prenom, a.adr1, a.adr2, a.cdp, a.ville,"
            + " s.id, s.rum, s.signature, s.seqtype, r.iban, g.bic"
            + " FROM personne p LEFT JOIN adresse a ON p.id = a.idper, rib r, guichet g, prlsepa s"
            + " WHERE p.id = " + payer
            + " AND p.id = r.idper AND r.guichetid = g.id"
            + " AND p.id = s.payeur"
            + " AND s.seqtype != '" + DDSeqType.LOCK.name() + "'";
    //9 id, 10 rum, 11 sign, 12 seqtype, 13 iban, 14 bic
    return dc.executeQuery(query);
  }
  
  DirectDebitCreditor getCreditorInfo() {

    DirectDebitCreditor creditor = new DirectDebitCreditor();

    creditor.setFirmName(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getKey(), dc));
    creditor.setBankHouse(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_BANKHOUSE_CODE.getKey(), dc));
    creditor.setBankBranch(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_BANK_BRANCH.getKey(), dc));
    creditor.setAccount(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_ACCOUNT.getKey(), dc));
    creditor.setNne(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_CREDITOR_NNE.getKey(), dc));
    creditor.setIban(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_IBAN.getKey(), dc));
    creditor.setBic(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_BIC.getKey(), dc));
    creditor.setIcs(ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_ICS.getKey(), dc));

    return creditor;
  }
  
  String getFirmName() {
    return ConfigUtil.getConf(ConfigKey.DIRECT_DEBIT_FIRM_NAME.getKey(), dc);
  }
}
