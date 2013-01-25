/*
 * @(#)Maintenance.java	1.0a 02/09/2001
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

import java.util.Date;
import net.algem.planning.DateFr;

/**
 * table maintenance
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class Maintenance
        implements java.io.Serializable
{

  public static String[] types = {"Modif", "BUG", "Idée"};
  DateFr jour;
  String personne;
  int type;
  String texte;
  boolean fait;

  public Maintenance() {
  }

  public Maintenance(DateFr _jour, String _personne, int _type, String _texte) {
    jour = _jour;
    personne = _personne;
    type = _type;
    texte = _texte;
  }

  public Maintenance(String _personne, int _type, String _texte) {
    this(new DateFr(new Date()), _personne, _type, _texte);
  }

  public void setJour(DateFr s) {
    jour = s;
  }

  public DateFr getJour() {
    return jour;
  }

  public void setPersonne(String s) {
    personne = s;
  }

  public String getPersonne() {
    return personne;
  }

  public void setType(int s) {
    type = s;
  }

  public int getType() {
    return type;
  }

  public void setFait(boolean s) {
    fait = s;
  }

  public boolean getFait() {
    return fait;
  }

  public void setTexte(String s) {
    texte = s;
  }

  public String getTexte() {
    return texte;
  }

  public boolean equals(Maintenance v) {
    return (v != null
            && jour.equals(v.jour)
            && personne.equals(v.personne)
            && texte.equals(v.texte));
  }

  @Override
  public String toString() {
    return jour + " " + personne + " " + texte;
  }
}
