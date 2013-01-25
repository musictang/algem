/*
 * @(#)ChercheCRromView.java	2.6.a 25/09/12
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
import java.awt.event.ActionEvent;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class CDromSearchView
        extends SearchView
{

  GemNumericField numero;
  GemField album;
  GemField artiste;
  GemField genre;
  GemPanel masque;

  public CDromSearchView() {
  }

  public GemPanel init() {
    masque = new GemPanel();
    masque.setLayout(new GridBagLayout());

    numero = new GemNumericField(6);
    numero.addActionListener(this);
    album = new GemField(30);
    album.addActionListener(this);
    artiste = new GemField(25);
    genre = new GemField(25);

    btSearch = new GemButton("Chercher");
    btSearch.addActionListener(this);
    btErase = new GemButton("Effacer");
    btErase.addActionListener(this);

    GridBagHelper gb = new GridBagHelper(masque);
    gb.add(new GemLabel("numéro"), 0, 0, 1, 1, gb.EAST);
    gb.add(new GemLabel("album"), 0, 1, 1, 1, gb.EAST);
    gb.add(new GemLabel("artiste"), 0, 2, 1, 1, gb.EAST);
    gb.add(new GemLabel("genre"), 0, 3, 1, 1, gb.EAST);

    gb.add(numero, 1, 0, 1, 1, gb.WEST);
    gb.add(album, 1, 1, 1, 1, gb.WEST);
    gb.add(artiste, 1, 2, 1, 1, gb.WEST);
    gb.add(genre, 1, 3, 1, 1, gb.WEST);

    return masque;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (actionListener == null) {
      return;
    }
    if (evt.getSource() == numero
            || evt.getSource() == album
            || evt.getSource() == artiste) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Chercher"));
    } else {
      actionListener.actionPerformed(evt);
    }
  }

  @Override
  public String getField(int n) {
    String s = null;
    switch (n) {
      case 0:
        s = numero.getText();
        break;
      case 1:
        s = album.getText();
        break;
      case 2:
        s = artiste.getText();
        break;
    }
    if (s != null && s.length() > 0) {
      return s;
    } else {
      return null;
    }
  }

  @Override
  public void clear() {
    numero.setText("");
    album.setText("");
    artiste.setText("");
    genre.setText("");
  }
}
