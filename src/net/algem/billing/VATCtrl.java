/*
 * @(#)VATCtrl  2.14.0 08/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.table.TableColumnModel;
import net.algem.accounting.Account;
import net.algem.accounting.AccountIO;
import net.algem.config.Param;
import net.algem.config.ParamTableCtrl;
import net.algem.config.ParamTableView;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.JTableModel;

/**
 * Taxes controller.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.3.a 30/01/12
 */
public class VATCtrl
  extends ParamTableCtrl {

  private VatIO vatIO;

  public VATCtrl(GemDesktop desktop) {
    super(desktop, BundleUtil.getLabel("Invoice.item.vat.label"), false, 1);
    this.vatIO = new VatIO(DataCache.getDataConnection());
  }

  @Override
  public void load() {
    try {
      List<Vat> taxes = vatIO.load();
      load(Collections.enumeration(taxes));
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void modification(Param current, Param p) throws SQLException {
    Vat t = (Vat) p;
    vatIO.update(t);
    desktop.getDataCache().update(t);
    desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.VAT, t));
  }

  @Override
  public void insertion(Param p) throws SQLException {
    Vat t = (Vat) p;
    vatIO.insert(t);
    desktop.getDataCache().add(t);
    desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.VAT, t));

  }

  @Override
  public void suppression(Param p) throws SQLException {
    Vat t = (Vat) p;
    vatIO.delete(t.getId());
    desktop.getDataCache().remove(t);
    desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.VAT, t));
  }

  @Override
  public void setView(boolean activable) {
    table = new VatTableView(title, new VatTableModel());
    table.setColumnModel();
    Vector<Account> accounts = new Vector<>();
    try {
      accounts = AccountIO.find(false, dc);// false: include inactive accounts
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    mask = new VATView(accounts);
  }

  class VatTableView extends ParamTableView {

    public VatTableView(String title, JTableModel<Vat> model) {
      super(title, model);
    }

    @Override
    public void setColumnModel() {
      TableColumnModel cm = table.getColumnModel();
      cm.getColumn(0).setPreferredWidth(25);
      cm.getColumn(1).setPreferredWidth(400);
    }
  }
}
