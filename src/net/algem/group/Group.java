/*
 * @(#)Group.java	2.6.a 31/07/12
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
package net.algem.group;

import java.util.Vector;
import net.algem.config.MusicStyle;
import net.algem.contact.Contact;
import net.algem.contact.Note;
import net.algem.contact.WebSite;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemModel;

/**
 * Generally, a group is an ensemble of persons.
 * Initially, this class refered to the musical band.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class Group
        implements GemModel
{

  static final byte REFERENT = 1;
  static final byte MANAGER = 2;
  static final byte TOURNEUR = 3;
  
  private int id;
  
  /** Group name. */
  private String name;
  
  /** Person in charge id. */
  private int idref;
  
  /** Manager id. */
  private int idman;
  
  /** Booker id. */
  private int idbook;
  
  private Contact referent;
  private Contact manager;
  private Contact booker; // tourneur
  
  private Note note;
  
  private Vector<WebSite> sites;
  private MusicStyle style;
  private Vector<Musician> musicians;

  public Group() {
  }

  public Group(Vector<WebSite> sites) {
    this.sites = sites;
  }

  public Group(int n) {
    this(n, "", 0, 0, 0, 0);
  }

  public Group(String n) {
    this(0, n, 0, 0, 0, 0);
  }

  /**
   * 
   * @param i group id
   * @param n name
   * @param st style
   * @param r referent
   * @param m manager
   * @param b booker
   */
  public Group(int i, String n, int st, int r, int m, int b) {
    id = i;
    name = n;
    this.style = new MusicStyle(st,MessageUtil.getMessage("musical.style.default.label"));
    idref = r;
    idman = m;
    idbook = b;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object g) {
    if (g == null) {
      return false;
    }
    if (getClass() != g.getClass()) {
      return false;
    }
    final Group other = (Group) g;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + this.id;
    return hash;
  }

  public boolean equiv(Group g) {
    if (g == null) {
      return false;
    }

    if ((this.name == null) ? (g.name != null) : !this.name.equals(g.name)) {
      return false;
    }
    if (this.idref != g.idref) {
      return false;
    }
    if (this.idman != g.idman) {
      return false;
    }
    if (this.idbook != g.idbook) {
      return false;
    }

    if ((this.style == null) ? (g.style != null) : !this.style.equals(g.style)) {
      return false;
    }

    return true;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  public String getName() {
    return name;
  }

  public void setName(String l) {
    name = l;
  }

  public MusicStyle getStyle() {
    return style;
  }

  public void setStyle(MusicStyle style) {
    this.style = style;
  }

  public int getIdref() {
    return idref;
  }

  public int getIdman() {
    return idman;
  }

  public int getIdbook() {
    return idbook;
  }

  public void setIdContact(int ref, int man, int tour) {
    this.idref = ref;
    this.idman = man;
    this.idbook = tour;
  }
  
  public void setContact(Contact ref, Contact man, Contact tour) {
    this.referent = ref;
    this.manager = man;
    this.booker = tour;
  }

  public Contact getReferent() {
    return referent;
  }

  public Contact getManager() {
    return manager;
  }

  public Contact getBooker() {
    return booker;
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public Vector<WebSite> getSites() {
    return sites;
  }

  public void setSites(Vector<WebSite> sites) {
    this.sites = sites;
  }

  public void setMusicians(Vector<Musician> musiciens) {
    this.musicians = musiciens;
  }

  public Vector<Musician> getMusicians() {
    return musicians;
  }

  public Musician getMusician(int idper) {
    for (Musician m : musicians) {
      if (m.getId() == idper) {
        return m;
      }
    }
    return null;
  }
}
