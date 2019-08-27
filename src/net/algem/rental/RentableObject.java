/*
 * @(#)RentableObject.java	2.17.1 29/08/2019
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

import java.time.LocalDate;
import java.util.Date;
import net.algem.course.*;
import net.algem.util.model.GemModel;

/**
 * RentableObject entity.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
public class RentableObject
        implements GemModel
{

  /** Break identification. */
  public static final int BREAK = 0;

  /** Minimum length of title. */
  public static final int MIN_TITLE_LENGTH = 1;

  /** Maximum length of title. */
  public static final int MAX_TITLE_LENGTH = 32;

  /** Maximum length of label. */
  public static final int MAX_LABEL_LENGTH = 32;

  private static final long serialVersionUID = 1L;

  protected int id;
  protected Date dateAchat;
  protected String type;
  protected String marque;
  protected String identification;
  protected String description;
  protected String vendeur;
  protected boolean actif;

  public RentableObject() {
  }

  /**
   * Creates a course with id {@code id}.
   * @param id course id
   */
  public RentableObject(int id) {
    this.id = id;
  }

  /**
   * Creates a course with name {@code s}.
   * @param s the title of the course
   */
  public RentableObject(String s) {
    id = 0;
    type = s;
  }

  @Override
  public String toString() {
    return type+" "+marque+" "+identification;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RentableObject other = (RentableObject) obj;
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

    public Date getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendeur() {
        return vendeur;
    }

    public void setVendeur(String vendeur) {
        this.vendeur = vendeur;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }


}
