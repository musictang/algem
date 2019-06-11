/*
 * @(#)CDromView.java	2.6.a 25/09/12
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

import java.awt.GridBagLayout;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CDromView
        extends GemPanel
{

  private String id = "0";
  private GemNumericField no;
  private GemField album;
  private GemField artiste;
  private GemField label;
  private GemField ref;
  private GemField genre;

  public CDromView() {

    no = new GemNumericField(6);
    no.setEditable(false);
    album = new GemField(35);
    artiste = new GemField(30);
    label = new GemField(24);
    ref = new GemField(10);
    genre = new GemField(24);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel("No"), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel("Album"), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel("Artiste"), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel("Libelle"), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel("Ref."), 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel("Genre"), 0, 5, 1, 1, GridBagHelper.EAST);

    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(album, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(artiste, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(label, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(ref, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(genre, 1, 5, 1, 1, GridBagHelper.WEST);
  }

  public String getId() {
    return id;
  }

  public void setId(String i) {
    id = i;
  }

  public String getAlbum() {
    return album.getText();
  }

  public void setAlbum(String c) {
    album.setText(c);
  }

  public String getGenre() {
    return genre.getText();
  }

  public void setGenre(String l) {
    genre.setText(l);
  }

  public CDrom get() {
    CDrom cd = new CDrom();
    try {
      cd.setId(Integer.parseInt(no.getText()));
    } catch (Exception e) {
      cd.setId(0);
    };
    cd.setAlbum(album.getText());
    cd.setArtist(artiste.getText());
    cd.setLabel(label.getText());
    cd.setRef(ref.getText());
    cd.setGenre(genre.getText());

    return cd;
  }

  public void set(CDrom cd) {
    no.setText(String.valueOf(cd.getId()));
    album.setText(cd.getAlbum());
    artiste.setText(cd.getArtist());
    label.setText(cd.getLabel());
    ref.setText(cd.getRef());
    genre.setText(cd.getGenre());
  }

  public void clear() {
    no.setText("");
    album.setText("");
    artiste.setText("");
    label.setText("");
    ref.setText("");
    genre.setText("");
  }
}
