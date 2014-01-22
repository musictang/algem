/*
 * @(#)DirectDebitService.java	2.8.r 21/01/14
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
import java.util.Date;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

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
  private static volatile DirectDebitService INSTANCE;

  private DirectDebitService(DataConnection dc) {
    dao = new DirectDebitIO(dc);
  }

  public static DirectDebitService getInstance(DataConnection dc) {
    if (INSTANCE == null) {
      synchronized (DirectDebitService.class) {
        if (INSTANCE == null) {
          INSTANCE = new DirectDebitService(dc);
        }
      }
    }
    return INSTANCE;
  }

  ResultSet getDirectDebit(int school, DateFr datePrl, Enum seqType) throws SQLException {
    return dao.getDirectDebit(school, datePrl, seqType);
  }

  ResultSet getTransaction(int payer) throws SQLException {
    return dao.getDDTransaction(payer);
  }

  public DirectDebitCreditor getCreditorInfo() {
    return dao.getCreditorInfo();
  }

  String getFirmName() {
    return dao.getFirmName();
  }

  String getTxInformationLabel(String label, DateFr dateOfPayment) {
    return label + " " + df.format(dateOfPayment.getDate());
  }

  void updateToRcurSeqType(List<Integer> firstDebited) throws SQLException {
    StringBuilder list = new StringBuilder();
    for (Integer i : firstDebited) {
      list.append(i).append(",");
    }
    list.deleteCharAt(list.length() - 1);
    dao.updateToRcurSeqType(list.toString());
  }
  
  void updateLastDebit(DateFr date, List<Integer> debited) throws SQLException {
    StringBuilder list = new StringBuilder();
    for (Integer i : debited) {
      list.append(i).append(",");
    }
    list.deleteCharAt(list.length() - 1);
    dao.updateLastDebit(date.toString(), list.toString());
  }

  void update(DDMandate dd) throws SQLException {
    dao.update(dd);
  }

  void update(List<DDMandate> mandates, DDSeqType seqType) throws SQLException, DDMandateException {
    for (DDMandate dd : mandates) {
      if (dd.isRecurrent() && seqType.equals(DDSeqType.OOFF)) {
        throw new DDMandateException(MessageUtil.getMessage("direct.debit.edit.ooff.warning", seqType));
      } else if (!dd.isRecurrent() && !seqType.equals(DDSeqType.OOFF)) { 
        throw new DDMandateException(MessageUtil.getMessage("direct.debit.edit.rcur.warning", seqType));
      }
    }
    dao.updateSeqType(mandates, seqType);
  }

  void deleteMandate(DDMandate dd) throws SQLException, DDMandateException {
    if (dd.isSuppressible()) {
      dao.deleteMandate(dd.getId());
    } else {
      throw new DDMandateException(MessageUtil.getMessage("direct.debit.delete.mandate.exception"));
    }
  }

  public List<DDMandate> getMandates() throws SQLException {
    return dao.getMandates();
  }

  public List<DDMandate> getMandates(int payer) throws SQLException {
    return dao.getMandates(payer);
  }

  public DDMandate getMandate(int idper) throws DDMandateException {
    try {
      DDMandate dd = dao.getMandate(idper);
      if (dd != null && !dd.isValid()) {
        dd.setSeqType(DDSeqType.LOCK);
        dao.update(dd);
        return null;
      } 
      return dd;
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new DDMandateException(MessageUtil.getMessage("direct.debit.retrieve.mandate.exception"));
    }
  }

  public void createMandate(int idper) throws DDMandateException {
    DDMandate dd = new DDMandate(idper);
    
    dd.setLastDebit(null);
    dd.setDateSign(new DateFr(new Date()));
    dd.setRecurrent(true); // default
    dd.setSeqType(DDSeqType.FRST);
    dd.setRum(RumGenerator.generateRum(String.valueOf(idper), dd.getDateSign().toString()));
    try {
      dao.createMandate(dd);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new DDMandateException(MessageUtil.getMessage("direct.debit.create.mandate.exception"));
    }
  }
  
}
