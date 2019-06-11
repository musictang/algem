/*
 * @(#)Todo.java	2.9.4.13 05/11/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.opt;

import net.algem.planning.DateFr;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @deprecated
 */
public class Todo
        implements java.io.Serializable
{

  private static final long serialVersionUID = 1L;

  int id;
  int idper;
  short priorite;
  short categorie;
  String texte;
  DateFr echeance;
  boolean fait;
  DateFr faitle;
  int note;

  public Todo() {
  }

  public Todo(int _idper, String _texte) {
    idper = _idper;
    texte = _texte;
  }

  @Override
  public String toString() {
    return texte;
  }

  public void setId(int i) {
    id = i;
  }

  public int getId() {
    return id;
  }

  public void setIdPer(int i) {
    idper = i;
  }

  public int getIdPer() {
    return idper;
  }

  public void setPriorite(int i) {
    priorite = (short) i;
  }

  public void setPriorite(short i) {
    priorite = i;
  }

  public short getPriorite() {
    return priorite;
  }

  public void setCategorie(int i) {
    categorie = (short) i;
  }

  public void setCategorie(short i) {
    categorie = i;
  }

  public short getCategorie() {
    return categorie;
  }

  public void setTexte(String s) {
    texte = s;
  }

  public String getTexte() {
    return texte;
  }

  public void setEcheance(DateFr d) {
    echeance = d;
  }

  public DateFr getEcheance() {
    return echeance;
  }

  public void setFait(boolean b) {
    fait = b;
  }

  public boolean isFait() {
    return fait;
  }

  public void setFaitLe(DateFr d) {
    faitle = d;
  }

  public DateFr getFaitLe() {
    return faitle;
  }

  public void setNote(int n) {
    note = n;
  }

  public int getNote() {
    return note;
  }

  public boolean estValide() {
    return true;
  }
}
