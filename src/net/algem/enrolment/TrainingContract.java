/*
 * @(#) TrainingContract.java Algem 2.15.0 30/08/2017
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
 */

package net.algem.enrolment;

import java.util.Date;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/08/2017
 */
public class TrainingContract {

  private int id;
  private byte type;
  private int personId;
  private String label;
  private int orderId;
  private Date start;
  private Date end;
  private String funding;
  private double total;
  private double amount;
  private float internalVolume;
  private float externalVolume;
  private Date signDate;

  public TrainingContract() {
  }

  public TrainingContract(int id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + this.id;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrainingContract other = (TrainingContract) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public byte getType() {
    return type;
  }

  public void setType(byte type) {
    this.type = type;
  }

  public int getPersonId() {
    return personId;
  }

  public void setPersonId(int personId) {
    this.personId = personId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public String getFunding() {
    return funding;
  }

  public void setFunding(String funding) {
    this.funding = funding;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public float getInternalVolume() {
    return internalVolume;
  }

  public void setInternalVolume(float volume) {
    this.internalVolume = volume;
  }

  public float getExternalVolume() {
    return externalVolume;
  }

  public void setExternalVolume(float volume) {
    this.externalVolume = volume;
  }

  public Date getSignDate() {
    return signDate;
  }

  public void setSignDate(Date signDate) {
    this.signDate = signDate;
  }

  public Enum getEnumType() {
    switch(getType()) {
      case 1 :
        return TrainingContractType.BIPARTITE;
      case 2:
        return TrainingContractType.TRIPARTITE;
      default:
        return TrainingContractType.TRIPARTITE;
    }

  }

  enum TrainingContractType {
    BIPARTITE,
    TRIPARTITE
  }

}
