/*
 * @(#)DDMandate.java	2.8.r 16/01/14
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

import java.util.Date;
import net.algem.planning.DateFr;
import net.algem.util.model.GemModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 29/12/13
 */
public class DDMandate
        implements GemModel
{

  private int id;
  private int idper;
  private String name;
  private String rum;
  private DateFr lastDebit;
  private DateFr dateSign;
  private String iban;
  private String bic;
  private String ics;
  private DDSeqType seqType;
  private boolean recurrent;
  private static final double EXPIRATION_LIMIT = -36.0;

  public DDMandate(int idper) {
    this.idper = idper;
  }

  public int getIdper() {
    return idper;
  }

  String getBic() {
    return bic;
  }

  void setBic(String bic) {
    this.bic = bic;
  }

  public DateFr getLastDebit() {
    return lastDebit;
  }

  public void setLastDebit(DateFr last) {
    this.lastDebit = last;
  }
  
  boolean isSuppressible() {
    return lastDebit == null || DateFr.NULLDATE.equals(lastDebit.toString());
  }

  public DateFr getDateSign() {
    return dateSign;
  }

  public void setDateSign(DateFr dateSign) {
    this.dateSign = dateSign;
  }

  String getIban() {
    return iban;
  }

  void setIban(String iban) {
    this.iban = iban;
  }

  String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  public String getRum() {
    return rum;
  }

  void setRum(String rum) {
    this.rum = rum;
  }

  String getIcs() {
    return ics;
  }

  void setIcs(String icsCreditor) {
    this.ics = icsCreditor;
  }

  DDSeqType getSeqType() {
    return seqType;
  }

  void setSeqType(DDSeqType seqType) {
    this.seqType = seqType;
  }

  public boolean isRecurrent() {
    return recurrent;
  }

  public void setRecurrent(boolean recurrent) {
    this.recurrent = recurrent;
  }
  
  public boolean isValid() {
   if (lastDebit == null || DateFr.NULLDATE.equals(lastDebit.toString())) {
     return true;
   }
   Date now = new Date();
   return DateFr.monthsBetween(now, lastDebit.getDate()) >  EXPIRATION_LIMIT;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }
}
