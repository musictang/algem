/*
 * @(#)RoomView.java	2.11.0 23/09/16
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import javax.swing.JCheckBox;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Room view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 1.0a 02/09/2001
 */
public class RoomView
        extends GemPanel
{

  private static String labels[] = {
    BundleUtil.getLabel("Number.label"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("Function.label"),
    BundleUtil.getLabel("Surface.label"),
    BundleUtil.getLabel("Place.number.label"),
    BundleUtil.getLabel("Establishment.label").substring(0, 4) + ".",
    BundleUtil.getLabel("Room.active.label"),
    BundleUtil.getLabel("Room.public.label"),
    BundleUtil.getLabel("Room.price.label"),
    BundleUtil.getLabel("Payer.label")
  };
  private GemNumericField no;
  private GemField name;
  private GemField function;
  private GemNumericField surf;
  private GemNumericField npers;
  private GemChoice estab;
  private JCheckBox active;
  private JCheckBox available;
  private RoomRateChoice rate;
  private RoomPayerCtrl payerCtrl;

  public RoomView(DataCache dataCache) {

    no = new GemNumericField(10);
    no.setEditable(false);
    name = new GemField(25);
    function = new GemField(25);
    surf = new GemNumericField(8);
    npers = new GemNumericField(8);
    try {
      estab = new EstabChoice(new GemList<Establishment>(EstablishmentIO.find(" ORDER BY p.nom", DataCache.getDataConnection())));
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
    rate = new RoomRateChoice(dataCache.getList(Model.RoomRate));
    active = new JCheckBox(labels[6], true);
    available = new JCheckBox(labels[7], false);
    available.setToolTipText(BundleUtil.getLabel("Room.public.tip"));
    payerCtrl = new RoomPayerCtrl();

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(labels[0]), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(labels[1]), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(labels[2]), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(labels[3]), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(labels[4]), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(labels[5]), 0, 5, 1, 1, GridBagHelper.WEST);
//    gb.add(new GemLabel(labels[6]), 0, 10, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(labels[8]), 0, 11, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(labels[9]), 0, 12, 1, 1, GridBagHelper.WEST);

    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(function, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(surf, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(npers, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(estab, 1, 5, 1, 1, GridBagHelper.WEST);
    GemPanel p = new GemPanel(new BorderLayout());
    active.setBorder(null);
    p.add(active, BorderLayout.WEST);
    p.add(available, BorderLayout.EAST);
    gb.add(p, 1, 10, 1, 1, GridBagHelper.WEST);
    gb.add(rate, 1, 11, 1, 1, GridBagHelper.WEST);
    gb.add(payerCtrl, 1, 12, 1, 1, GridBagHelper.WEST);
  }

  public void set(Room r) {
    no.setText(String.valueOf(r.getId()));
    name.setText(r.getName());
    function.setText(r.getFunction());
    surf.setText(String.valueOf(r.getSurface()));
    npers.setText(String.valueOf(r.getNPers()));
    estab.setKey(r.getEstab());
    active.setSelected(r.isActive());
    available.setSelected(r.isAvailable());
    setRate(r);
    payerCtrl.setRoom(r);

  }

  private void setRate(Room r) {
    if (r.getRate() == null) {
      rate.setSelectedIndex(0);
    } else {
      rate.setKey(r.getRate().getId());
    }
  }

  public Room get() {
    Room r = new Room();

    try {
      r.setId(Integer.parseInt(no.getText()));
    } catch (NumberFormatException e) {
      r.setId(0);
    }

    r.setName(name.getText());

    r.setFunction(function.getText());
    try {
      r.setSurface(Integer.parseInt(surf.getText()));
    } catch (NumberFormatException e) {
      r.setSurface(0);
    }
    try {
      r.setNPers(Integer.parseInt(npers.getText()));
    } catch (NumberFormatException e) {
      r.setNPers(0);
    }
    r.setEstab(estab.getKey());
    r.setActive(active.isSelected());
    r.setAvailable(available.isSelected());
    r.setRate((RoomRate) rate.getSelectedItem());
    r.setPayer(payerCtrl.getPayer());

    return r;
  }

  public void clear() {
    no.setText("");
    name.setText("");
    function.setText("");
    surf.setText("");
    npers.setText("");
    estab.setSelectedIndex(0);
    active.setSelected(true);
    payerCtrl.clear();
  }

}
