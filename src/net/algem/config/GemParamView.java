/*
 * @(#)GemParamView.java 2.6.a 24/09/12
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

package net.algem.config;

import net.algem.util.BundleUtil;

/**
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 22/06/2012
 */
public class GemParamView 

extends ParamView
{

  private int id;
  
  public GemParamView() {
    super(false);
    key.setEditable(true);
    key.setColumns(1);
  }

  @Override
  public void set(Param p) {
    super.set(p);
    id = ((GemParam) p).getId();
  }

  @Override
  public void setLabels() {
    keyLabel.setText(BundleUtil.getLabel("Code.label"));
    valueLabel.setText(BundleUtil.getLabel("Label.label"));
  }

  @Override
  public Param get() {
    return new GemParam(id, key.getText(), value.getText());
  }
}
