/*
 * @(#)VATCtrl  2.9.4.13 16/10/15
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
package net.algem.billing;

import java.sql.SQLException;
import net.algem.config.Param;
import net.algem.config.ParamTableCtrl;
import net.algem.config.ParamTableIO;
import net.algem.config.ParamTableView;
import net.algem.util.BundleUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;

/**
 * Persistence for VAT.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.a 30/01/12
 */
public class VATCtrl
        extends ParamTableCtrl
{

  public static String tableName = "tva";
  public static String seqName = "tva_id_seq";
  public static String columnKey = "id";
  public static String columnValue = "pourcentage";
  public static String sortColumn = "pourcentage";

  public VATCtrl(GemDesktop _desktop) {
    super(_desktop, BundleUtil.getLabel("Invoice.item.vat.label"), false, 1);
  }

  @Override
  public void load() {
    load(ParamTableIO.find(tableName, sortColumn, dc).elements());
  }

  @Override
  public void modification(Param current, Param p) throws SQLException {
    ParamTableIO.update(tableName, columnKey, columnValue, p, dc);
    desktop.getDataCache().update((Vat) p);
    desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.VAT, (Vat) p));
  }

  @Override
  public void insertion(Param p) throws SQLException {
    ParamTableIO.insert(tableName, seqName, p, dc);
    desktop.getDataCache().add((Vat) p);
    desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.VAT, (Vat) p));
  }

  @Override
  public void suppression(Param p) throws SQLException {
    ParamTableIO.delete(tableName, columnKey, p, dc);
    desktop.getDataCache().remove((Vat) p);
    desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.VAT, (Vat) p));
  }

  public String find() {
    Param p = ParamTableIO.findBy(tableName, " ORDER BY id LIMIT 1", dc);
    return p == null ? null : p.getValue();
  }

  @Override
  public void setView(boolean activable) {
    table = new ParamTableView(title, new VatTableModel());
    mask = new VATView(activable);
  }
}
