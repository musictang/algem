/*
 * @(#)RentView.java	2.17.1 29/08/19
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

import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
public class RentView
        extends GemPanel
{

  private GemNumericField no;
  private GemField instrument;
  private GemField marque;
  private GemField identification;

  private GemField description;
  private GemField vendeur;
  private DateFrField dateAchat;
  private JCheckBox actif;
  
  public RentView() {

    no = new GemNumericField(6);
    no.setEditable(false);
    no.setBackground(Color.lightGray);
    instrument = new GemField(24, 32);
    marque = new GemField(24, 32);
    identification = new GemField(32, 48);
    description = new GemField(48,64);
    vendeur = new GemField(24, 32);
    dateAchat = new DateFrField();
    actif = new JCheckBox();
    actif.setBorder(null);
    actif.setSelected(true);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Instrument.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Marque.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Identification.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Description.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Vendeur.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("DateAchat.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Active.label")), 0, 7, 1, 1, GridBagHelper.WEST);

    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(instrument, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(marque, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(identification, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(description, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(vendeur, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(dateAchat, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(actif, 1, 7, 1, 1, GridBagHelper.WEST);
  }

  public int getId() {
    return Integer.parseInt(no.getText());
  }

  public void set(RentableObject o) {
    no.setText(String.valueOf(o.getId()));
    instrument.setText(o.getType());
    marque.setText(o.getMarque());
    identification.setText(o.getIdentification());
    description.setText(o.getDescription());
    vendeur.setText(o.getVendeur());
    dateAchat.set(o.getDateAchat());
    actif.setSelected(o.isActif());
  }

  public RentableObject get() {
    RentableObject o = new RentableObject();

    try {
      o.setId(Integer.parseInt(no.getText()));
    } catch (NumberFormatException e) {
      o.setId(0);
    }

    o.setType(instrument.getText());
    o.setMarque(marque.getText());
    o.setIdentification(identification.getText());
    o.setDescription(description.getText());
    o.setVendeur(vendeur.getText());
    o.setDateAchat(dateAchat.getDate() == null ? new DateFr() : dateAchat.get());
    o.setActif(actif.isSelected());

    return o;
  }

  public void clear() {
    no.setText("");
    instrument.setText("");
    marque.setText("");
    identification.setText("");
    description.setText("");
    vendeur.setText("");
    dateAchat.setText("");
    actif.setSelected(true);
  }
}
