/*
 * @(#)CDrom.java	2.9.4.13 05/11/15
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

import java.util.Objects;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class CDrom
        implements java.io.Serializable
{

  private static final long serialVersionUID = 1L;
  
  private int id;
  private String artist;
  private String album;
  private String label;
  private String ref;
  private String genre;

  @Override
  public boolean equals(Object o) {
              if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CDrom c = (CDrom) o;
    return (album.equals(c.album)
            && artist.equals(c.artist)
            && ref.equals(c.ref)
            && label.equals(c.label));
  }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.artist);
        hash = 47 * hash + Objects.hashCode(this.album);
        hash = 47 * hash + Objects.hashCode(this.label);
        hash = 47 * hash + Objects.hashCode(this.ref);
        hash = 47 * hash + Objects.hashCode(this.genre);
        return hash;
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
