/*
 * @(#)DirectDebitService.java	2.8.r 01/01/14
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;

/**
 * Direct debit service. 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 24/12/13
 */
public class DirectDebitService {

	private DataConnection dc;
	private DateFormat df = new SimpleDateFormat("MMM");

	public DirectDebitService(DataConnection dc) {
		this.dc = dc;
	}

	ResultSet getDirectDebit(int school, DateFr datePrl, Enum seqType) throws SQLException {
		String query = "SELECT e.payeur, e.montant, e.analytique FROM echeancier2 e, prlsepa p"
			+ " WHERE e.ecole = '" + school + "' AND e.reglement = 'PRL' AND e.paye = 't' AND e.echeance = '" + datePrl.toString() + "'"
			+ " AND e.payeur = p.payeur AND p.seqtype = '" + seqType + "'"
			+ " ORDER BY e.payeur, e.echeance";

		return dc.executeQuery(query);

	}
	
	ResultSet getTx(int payer) throws SQLException {
		String query = "SELECT p.id, p.civilite, p.nom, p.prenom, a.adr1, a.adr2, a.cdp, a.ville,"
			+ " s.rum, s.signature, s.seqtype, s.ics, r.iban, g.bic"
			+ " FROM personne p LEFT JOIN adresse a ON p.id = a.idper, rib r, guichet g, prlsepa s"
			+ " WHERE p.id = " + payer
			+ " AND p.id = r.idper AND r.guichetid = g.id"
			+ " AND p.id = s.payeur";
		//9 rum, 10 sign, 11 seqtype, 12 ics, 13 iban, 14 bic
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
	
	String getPaymentInformationLabel(String label, DateFr dateOfPayment, int batchNumber) {

		/*DateFr d = new DateFr(ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey(), dc));
		DateFr f = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey(), dc));

		return "COTIS " + school 
			+ " " + String.valueOf(d.getYear()) + "-" + String.valueOf(f.getYear())
			+ " " + df.format(dateOfPayment.getDate())
			+ " : L" + batchNumber;*/
		return label + " " + df.format(dateOfPayment.getDate()) + " : L" + batchNumber;
	}
	
	void updateToRcurSeqType(List<Integer> debtors) throws SQLException {
		StringBuilder list = new StringBuilder();
		for (Integer i : debtors) {
			list.append(i).append(",");
		}
		list.deleteCharAt(list.length() -1);
		String query = "UPDATE prlsepa SET seqtype = '" + DirectDebitSeqType.RCUR 
			+ "' WHERE payeur IN(" + list.toString() + ")"
			+ " AND seqtype = 'FRST'" ;
		dc.executeUpdate(query);
	}
}
