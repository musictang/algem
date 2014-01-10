/*
 * @(#)DirectDebitService.java	2.8.r 09/01/14
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
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;

/**
 * Direct debit service.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 24/12/13
 */
public class DirectDebitService
{

  private DateFormat df = new SimpleDateFormat("MMM");
  private DirectDebitIO dao;

  public DirectDebitService(DataConnection dc) {
//    this.dc = dc;
    dao = new DirectDebitIO(dc);
  }

  ResultSet getDirectDebit(int school, DateFr datePrl, Enum seqType) throws SQLException {
    return dao.getDirectDebit(school, datePrl, seqType);

  }

  ResultSet getTransaction(int payer) throws SQLException {
    return dao.getDDTransaction(payer);
  }

  DirectDebitCreditor getCreditorInfo() {
    return dao.getCreditorInfo();
  }

  String getFirmName() {
    return dao.getFirmName();
  }

  String getPaymentInformationLabel(String label, DateFr dateOfPayment, int batchNumber) {

    /* DateFr d = new DateFr(ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey(), dc));
     * DateFr f = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey(), dc));
     *
     * return "COTIS " + school
     * + " " + String.valueOf(d.getYear()) + "-" + String.valueOf(f.getYear())
     * + " " + df.format(dateOfPayment.getDate())
     * + " : L" + batchNumber; */
    return label + " " + df.format(dateOfPayment.getDate()) + " : L" + batchNumber;
  }

  void updateToRcurSeqType(List<Integer> debtors) throws SQLException {
    StringBuilder list = new StringBuilder();
    for (Integer i : debtors) {
      list.append(i).append(",");
    }
    list.deleteCharAt(list.length() - 1);
    dao.updateToRcurSeqType(list.toString());
  }
  
  void update(DDMandate dd) throws SQLException {
    dao.update(dd);
  }
  
  void update(List<DDMandate> mandates, DDSeqType seqType) throws SQLException {
    dao.updateSeqType(mandates, seqType);
  }
  
  void deleteMandate(int id) throws SQLException {
    dao.deleteMandate(id);
  }

  List<DDMandate> getMandates() throws SQLException {
    return dao.getMandates();
  }
  
}
