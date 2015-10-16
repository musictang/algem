/*
 * @(#)ModeOfPaymentCtrl  2.9.4.13 16/10/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes All Rights Reserved.
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

import java.sql.SQLException;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;

/**
 * Persistence of modes of payment.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class ModeOfPaymentCtrl
        extends ParamTableCtrl
{

  public static final String TABLE = "reglement";
  public static final String SEQUENCE = "reglement_id_seq";
  public static final String COLUMN_KEY = "id";
  public static final String COLUMN_NAME = "mode";

  public ModeOfPaymentCtrl(GemDesktop _desktop) {
    super(_desktop, "Modes de règlement", false, 6);
  }

  @Override
  public void load() {
    load(ParamTableIO.find(TABLE, COLUMN_NAME, dc).elements());
  }

  @Override
  public void modification(Param current, Param p) throws SQLException, ParamException {
    check(p);
    ParamTableIO.update(TABLE, COLUMN_KEY, COLUMN_NAME, p, dc);
  }

  @Override
  public void insertion(Param p) throws SQLException, ParamException {
    check(p);
    ParamTableIO.insert(TABLE, SEQUENCE, p, dc);
  }

  @Override
  public void suppression(Param p) throws SQLException {
    ParamTableIO.delete(TABLE, COLUMN_KEY, p, dc);
  }

  private void check(Param p) throws ParamException {
    if (p.getValue().length() > 4) {
      throw new ParamException(MessageUtil.getMessage("mode.of.payment.label.length.warning"));
    }
  }
}
