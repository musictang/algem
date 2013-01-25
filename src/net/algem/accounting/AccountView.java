/*
 * @(#)AccountView.java	2.6.a 13/09/2012
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
package net.algem.accounting;

import net.algem.util.BundleUtil;
import net.algem.config.Param;
import net.algem.config.ParamView;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.c 09/03/12
 */
public class AccountView
        extends ParamView
{

  private int id;

  public AccountView() {
    super(true);
  }

  @Override
  public void set(Param p) {
    super.set(p);
    id = ((Account) p).getId();
  }

  @Override
  public void setLabels() {
    keyLabel.setText(BundleUtil.getLabel("Account.number.label"));
    valueLabel.setText(BundleUtil.getLabel("Label.label"));
  }

  @Override
  public Param get() {
    return new Account(id, key.getText(), value.getText(), active.isSelected());
  }
}
