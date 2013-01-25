/*
 * @(#)CDrom.java	2.6.a 25/09/12
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

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CDrom
        implements java.io.Serializable
{

  private int id;
  private String artist;
  private String album;
  private String label;
  private String ref;
  private String genre;

  public boolean equals(CDrom c) {
    return (c != null
            && album.equals(c.album)
            && artist.equals(c.artist)
            && ref.equals(c.ref)
            && label.equals(c.label));
  }

  @Override
  public String toString() {
    return album + " " + artist;
  }

  public void setId(int i) {
    id = i;
  }

  public int getId() {
    return id;
  }

  public void setArtist(String s) {
    artist = s;
  }

  public String getArtist() {
    return artist;
  }

  public void setAlbum(String s) {
    album = s;
  }

  public String getAlbum() {
    return album;
  }

  public void setLabel(String s) {
    label = s;
  }

  public String getLabel() {
    return label;
  }

  public void setRef(String s) {
    ref = s;
  }

  public String getRef() {
    return ref;
  }

  public void setGenre(String s) {
    genre = s;
  }

  public String getGenre() {
    return genre;
  }
}
