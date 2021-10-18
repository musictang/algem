/*
 * @(#)ExtendedModuleOrderTableModel.java	2.9.4.13 09/10/15
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
package net.algem.enrolment;

import java.text.NumberFormat;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.GemAmount;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.2.1 16/02/15
 */
public class ExtendedModuleOrderTableModel
        extends JTableModel<ExtendedModuleOrder>
{

  private final NumberFormat nf;

  public ExtendedModuleOrderTableModel() {
    this.nf = NumberFormat.getInstance();
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    header = new String[]{
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("First.name.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("Nickname.label"),
      BundleUtil.getLabel("Module.label"),
      BundleUtil.getLabel("Total.label"),
      BundleUtil.getLabel("Remaining.label"),
      BundleUtil.getLabel("Amount.label"),
      BundleUtil.getLabel("Deferred.income.label")
    };
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public int getIdFromIndex(int i) {
    ModuleOrder m = tuples.get(i);
    return m.getIdOrder();
  }

  @Override
  public Class getColumnClass(int col) {
    switch (col) {
      case 0:
        return DateFr.class;
      case 1:
      case 2:
      case 3:
      case 4:
        return String.class;
      case 5:
      case 6:
//        return Double.class;
      case 7:
      case 8:
        return GemAmount.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int line, int col) {
    ExtendedModuleOrder m = tuples.get(line);
    int rest = m.getTotalTime() - m.getCompleted();
    switch (col) {
      case 0:
        return m.getStart();
      case 1:
        return m.getFirstName() == null ? "" : m.getFirstName();
      case 2:
        return m.getName() == null ? "" : m.getName();
      case 3:
        return m.getNickName() == null ? "" : m.getNickName();
      case 4:
        return m.getTitle();
      case 5:
        return PricingPeriod.HOUR.equals(m.getPricing()) ? nf.format(Hour.minutesToDecimal(m.getTotalTime())) : nf.format(Hour.minutesToDecimal(m.getCompleted()));
      case 6:
        return rest <= 0 ? nf.format(0.0d) : nf.format(Hour.minutesToDecimal(rest));
      case 7:
        return nf.format(AccountUtil.round(m.getPaymentInfo(m.getTotalTime())));
      case 8:
        if (PricingPeriod.HOUR.equals(m.getPricing())) {
          return nf.format(AccountUtil.round(m.getPaymentInfo(rest)));
        } else {
          return nf.format(0.0d);
        }
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {

  }
}
