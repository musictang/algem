/*
 * @(#)RentalOperation.java	2.17.1 29/08/2019
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.rental;

import net.algem.planning.DateFr;
import net.algem.util.model.GemModel;

/**
 * RentableObject entity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
public class RentalOperation
        implements GemModel {

    /**
     * Break identification.
     */
    public static final int BREAK = 0;

    /**
     * Maximum length of label.
     */
    public static final int MAX_LABEL_LENGTH = 64;

    private static final long serialVersionUID = 1L;

    protected int id;
    protected int rentableObjectId;
    protected String rentableObjectName;
    protected DateFr startDate;
    protected DateFr endDate;
    protected int memberId;
    protected String memberName;
    protected int amount;
    protected String description;

    public RentalOperation() {
    }

    @Override
    public String toString() {
        return rentableObjectName + " " + memberName + " " + startDate + " " + endDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RentalOperation other = (RentalOperation) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int i) {
        id = i;
    }

    public int getRentableObjectId() {
        return rentableObjectId;
    }

    public void setRentableObjectId(int rentableObjectId) {
        this.rentableObjectId = rentableObjectId;
    }

    public String getRentableObjectName() {
        return rentableObjectName;
    }

    public void setRentableObjectName(String rentableObjectName) {
        this.rentableObjectName = rentableObjectName;
    }

    public DateFr getStartDate() {
        return startDate;
    }

    public void setStartDate(DateFr startDate) {
        this.startDate = startDate;
    }

    public DateFr getEndDate() {
        return endDate;
    }

    public void setEndDate(DateFr endDate) {
        this.endDate = endDate;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
