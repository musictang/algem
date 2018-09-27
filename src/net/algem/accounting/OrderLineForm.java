/*
 * @(#) OrderLineForm.java Algem 2.15.10 27/09/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.accounting;

import net.algem.planning.DateFr;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.10
 * @since 2.15.10 27/09/2018
 */
class OrderLineForm {

  private double totalMax;
  private int refMaxLength;
  private String accountingExportFormat;
  private String payer;
  private String label;
  private double total;
  private String modeOfPayment;
  private DateFr date;

  private String documentRef;

  public OrderLineForm(double max, int refMaxLength, String accountingExportFormat) {
    this.totalMax = max;
    this.refMaxLength = refMaxLength;
    this.accountingExportFormat = accountingExportFormat;
  }

  OrderLineForm payer(String payer) {
    this.payer = payer;
    return this;
  }

  OrderLineForm label(String label) {
    this.label = label;
    return this;
  }

  OrderLineForm total(double total) {
    this.total = total;
    return this;
  }

  OrderLineForm modeOfPayment(String modeOfPayment) {
    this.modeOfPayment = modeOfPayment;
    return this;
  }

  OrderLineForm documentRef(String documentRef) {
    this.documentRef = documentRef;
    return this;
  }

  OrderLineForm date(DateFr start) {
    this.date = start;
    return this;
  }


  public String getPayer() {
    return payer;
  }

  public String getLabel() {
    return label;
  }

  public double getTotal() {
    return total;
  }

  public double getTotalMax() {
    return totalMax;
  }

  public String getModeOfPayment() {
    return modeOfPayment;
  }

  public DateFr getDate() {
    return new DateFr(date);
  }

  public String getDocumentRef() {
    return documentRef;
  }

  public int getRefMaxLength() {
    return refMaxLength;
  }

  public String getAccountingExportFormat() {
    return accountingExportFormat;
  }

  public void setRefMaxLength(int refMaxLength) {
    this.refMaxLength = refMaxLength;
  }

}
