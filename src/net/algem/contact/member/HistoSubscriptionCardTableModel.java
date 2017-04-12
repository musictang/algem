/*
 * @(#)HistoSubscriptionCardTableModel.java 2.9.4.12 01/09/15
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
 */
package net.algem.contact.member;

import java.sql.SQLException;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.JTableModel;

/**
 * Table model for subscription card.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 2.9.2 07/01/15
 */
public class HistoSubscriptionCardTableModel
        extends JTableModel<PersonSubscriptionCard>
{

  private final MemberService service;
  
  /**
   * Creates a model with header.
   * @param service business service instance
   */
  public HistoSubscriptionCardTableModel(MemberService service) {
    this.service = service;
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Module.label"),
      BundleUtil.getLabel("Subscription.remaining.label"),};
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Class getColumnClass(int col) {
    switch (col) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
        return DateFr.class;
      case 3:
        return Hour.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 3;
  }

  @Override
  public Object getValueAt(int line, int col) {
    PersonSubscriptionCard pc = tuples.elementAt(line);
    switch (col) {
      case 0:
        return pc.getId();
      case 1:
        return pc.getPurchaseDate();
      case 2:
        try {
          RehearsalPass c = (RehearsalPass) DataCache.findId(pc.getPassId(), Model.PassCard);
          return c == null ? BundleUtil.getLabel("Unknown.label") : c.getLabel();
        } catch (SQLException ex) {
          GemLogger.log(ex.getMessage());
          return null;
        }
      case 3:
        return new Hour(pc.getRest(), true);
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
    if (col != 3) {
      return;
    }
    PersonSubscriptionCard c = tuples.elementAt(line);
    int oldRest = c.getRest();
    try {
      c.setRest(((Hour) value).toMinutes());
      if (c.getRest() != oldRest) {
        service.update(c);
        modItem(line, c);
      }
    } catch (MemberException ex) {
      c.setRest(oldRest);
      GemLogger.log(ex.getMessage());
    }

  }

}
