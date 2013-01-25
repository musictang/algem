/*
 * @(#)CostAccountCtrl.java	2.7.a 16/01/13
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

import java.sql.SQLException;
import java.util.Vector;
import net.algem.config.*;
import net.algem.util.module.GemDesktop;

/**
 * Cost account persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class CostAccountCtrl
        extends ParamTableCtrl
{

  public static String tableName = "analytique";
  public static String columnKey = "code";
  public static String columnName = "libelle";
  public static String columnFilter = "actif";

  public CostAccountCtrl(GemDesktop _desktop) {
    super(_desktop, "Comptes analytiques", true, true);
  }

  @Override
  public void load() {
    load(ActivableParamTableIO.find(tableName, columnName, columnFilter, dc).elements());
  }

  @Override
  public void modification(Param current, Param p) throws SQLException {
    if (p instanceof ActivableParam) {
      ActivableParam ap = (ActivableParam) p;
      ActivableParamTableIO.update(tableName, columnKey, columnName, columnFilter, ap, dc);
      desktop.getDataCache().update((CostAccount)ap);
    }
  }

  @Override
  public void insertion(Param p) throws SQLException {
    if (p instanceof ActivableParam) {
      ActivableParam ap = (ActivableParam) p;
      ActivableParamTableIO.insert(tableName, ap, dc);
      desktop.getDataCache().add((CostAccount)ap);
    }
  }

  @Override
  public void suppression(Param p) throws SQLException, AccountDeleteException {
    String where = OrderLineIO.COST_COLUMN + " = '" + p.getKey() + "'";
    Vector<OrderLine> e = OrderLineIO.find(where, 1, dc);
    if (e != null && e.size() > 0) {
      throw new AccountDeleteException();
    } else {
      ParamTableIO.delete(tableName, columnKey, p, dc);
      desktop.getDataCache().remove((CostAccount)p);
    }
  }
}
