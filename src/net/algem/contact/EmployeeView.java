/*
 * @(#)EmployeeView.java 2.8.m 09/09/13
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
package net.algem.contact;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import net.algem.config.ColorPrefs;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.8.m 02/09/13
 */
public class EmployeeView
        extends GemPanel
{

  private GemNumericField idper;
  private GemField insee;
  private DateFrField birth;
  private GemField place;
  private GemField guso;
  private EmployeeService service;

  public EmployeeView(final EmployeeService service) {
    idper = new GemNumericField(6);
    idper.setEditable(false);
    insee = new GemField(13, 15);
    this.service = service;
    insee.addFocusListener(new FocusListener()
    {
      @Override
      public void focusLost(FocusEvent e) {
         markInsee();
      }
      @Override
      public void focusGained(FocusEvent e) {
         markInsee();
      }
      
    });
    birth = new DateFrField();
    place = new GemField(true, 20);
    place.setToolTipText(BundleUtil.getLabel("Place.of.birth.tip"));
    guso = new GemField(13, 10);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Id.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Insee.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Guso.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.of.birth.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Place.of.birth.label")), 0, 4, 1, 1, GridBagHelper.WEST);

    gb.add(idper, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(insee, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(guso, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(birth, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(place, 1, 4, 1, 1, GridBagHelper.WEST);

  }

  void set(Employee e) {
    idper.setText(String.valueOf(e.getIdPer()));
    String n = e.getInsee() == null ? null : e.getInsee().trim();
    insee.setText(n);
    if (n != null && !n.isEmpty()) {
      markInsee();
    }
    birth.setDate(new DateFr(e.getDateBirth()));
    place.setText(e.getPlaceBirth());
    guso.setText(e.getGuso() == null ? null : e.getGuso().trim());
  }

  public Employee get() {
    Employee e = new Employee(Integer.parseInt(idper.getText()));
    e.setInsee(insee.getText().trim().toUpperCase());
    DateFr d = birth.getDateFr();
    if (d.equals(DateFr.NULLDATE)) {
      d = null;
    }
    e.setDateBirth(d == null ? null : new DateFr(d));
    e.setPlaceBirth(place.getText().trim().toUpperCase());
    e.setGuso(guso.getText().trim().toUpperCase());

    return e;
  }

  String getInsee() {
    return insee.getText().trim().toUpperCase();
  }

  void clear() {
    insee.setText(null);
    birth.setDate(new DateFr());
    place.setText(null);
    guso.setText(null);
  }
  
  private void markInsee() {
    boolean valid = service.checkNir(insee.getText().trim().toUpperCase());
    insee.setBackground(valid ? Color.WHITE : ColorPrefs.ERROR_BG_COLOR);
  }
}
