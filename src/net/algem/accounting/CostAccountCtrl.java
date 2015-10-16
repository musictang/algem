/*
 * @(#)CostAccountCtrl.java	2.9.4.13 16/10/15
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
package net.algem.accounting;

import java.sql.SQLException;
import net.algem.config.*;
import net.algem.util.module.GemDesktop;

/**
 * Cost account persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class CostAccountCtrl
        extends ParamTableCtrl
{

  public static String tableName = "analytique";
  public static String columnKey = "code";
  public static String columnName = "libelle";
  public static String columnFilter = "actif";

  private AccountingService service;
  
  public CostAccountCtrl(GemDesktop _desktop) {
    super(_desktop, "Comptes analytiques", true, true, -1);
    service = new AccountingService(dc);
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
      
      CostAccount ca = new CostAccount(ap);
      desktop.getDataCache().update(ca);
    }
  }

  @Override
  public void insertion(Param p) throws SQLException {
    if (p instanceof ActivableParam) {
      ActivableParam ap = (ActivableParam) p;
      ActivableParamTableIO.insert(tableName, ap, dc);
      
      CostAccount ca = new CostAccount(ap);
      desktop.getDataCache().add(ca);
    }
  }

  @Override
  public void suppression(Param p) throws AccountDeleteException {
    service.delete(p);
    CostAccount ca = new CostAccount(p);
    desktop.getDataCache().remove(ca);
  }
}
