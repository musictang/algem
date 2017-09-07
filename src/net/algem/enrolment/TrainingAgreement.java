/*
 * @(#) TrainingAgreement.java Algem 2.15.0 07/09/17
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
 * @since 2.15.0 07/09/17
 */
public class TrainingAgreement {

  private int id;
  private byte type;
  private int personId;
  private int orgId;
  private String insurance;
  private String insuranceRef;
  private String label;
  private String season;
  private Date start;
  private Date end;
  private Date signDate;

  public TrainingAgreement() {
  }

  public TrainingAgreement(int id) {
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
    final TrainingAgreement other = (TrainingAgreement) obj;
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

  public int getOrgId() {
    return orgId;
  }

  public void setOrgId(int orgId) {
    this.orgId = orgId;
  }

  public String getInsurance() {
    return insurance;
  }

  public void setInsurance(String insurance) {
    this.insurance = insurance;
  }

  public String getInsuranceRef() {
    return insuranceRef;
  }

  public void setInsuranceRef(String insuranceRef) {
    this.insuranceRef = insuranceRef;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getSeason() {
    return season;
  }

  public void setSeason(String season) {
    this.season = season;
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

  public Date getSignDate() {
    return signDate;
  }

  public void setSignDate(Date signDate) {
    this.signDate = signDate;
  }

  public Enum getEnumType() {
    switch(getType()) {
      case 1 :
        return TrainingAgreementType.BIPARTITE;
      case 2:
        return TrainingAgreementType.TRIPARTITE;
      default:
        return TrainingAgreementType.TRIPARTITE;
    }

  }

  enum TrainingAgreementType {
    BIPARTITE,
    TRIPARTITE
  }

}
