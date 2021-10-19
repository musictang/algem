/*
 * @(#)RentCtrl.java	2.17.1 29/08/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.rental;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SearchCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
public class RentCtrl
        extends CardCtrl
{

  private RentView v;
  private RentalView rv;
  private RentableObject rentable;
  private String [] errors = new String[3];

  private final GemDesktop desktop;
  private final DataCache dataCache;
  private final RentalService service;

  public RentCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    service = new RentalService(dataCache.getDataConnection());

    v = new RentView();
    rv = new RentalView(desktop);
    btNext.setToolTipText(BundleUtil.getLabel("Rental.list.label"));
    addCard("", v);
    addCard(BundleUtil.getLabel("Rental.list.label"), rv);

    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        btPrev.setText(GemCommand.PREVIOUS_CMD);
        btPrev.setActionCommand(GemCommand.PREVIOUS_CMD);
        btNext.setToolTipText(null);
        break;
    }
    return true;
  }

  @Override
  public boolean prev() {
    switch (step) {
      case 0:

        try {
          if (dataCache.authorize("Rent.suppression.auth")) {
            service.delete(v.get());
            dataCache.remove(rentable);
            desktop.postEvent(new RentEvent(this, GemEvent.SUPPRESSION, rentable));
          } else {
            MessagePopup.warning(contentPane, MessageUtil.getMessage("action.authorization.error", dataCache.getUser().getLogin()));
          }
          close();
        } catch (RentException cex) {
          MessagePopup.warning(contentPane, cex.getMessage());
          return false;
        } catch (SQLException ex) {
          GemLogger.logException(ex);
          return false;
        }
      case 1:
        btPrev.setActionCommand(GemCommand.DELETE_CMD);
        btPrev.setText(GemCommand.DELETE_CMD);
        select(step - 1);
        btNext.setToolTipText(BundleUtil.getLabel("Rental.list.label"));
        break;
      default:
        select(step - 1);
        btNext.setToolTipText(BundleUtil.getLabel("Rental.list.label"));
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {
    rentable = v.get();
    if (rentable == null || !isValid(rentable)) {
      String msg = "";
      for (String e : errors) {
        if (e != null) {
          msg += e + "\n";
        }
      }
      MessagePopup.error(v, msg);
      errors = new String[3];
      return prev();
    }

    try {
      if (rentable.getId() == 0) {
        service.create(rentable);
        dataCache.add(rentable);
        desktop.postEvent(new RentEvent(this, GemEvent.CREATION, rentable));
      } else {
        service.update(rentable);
        dataCache.update(rentable);
        desktop.postEvent(new RentEvent(this, GemEvent.MODIFICATION, rentable));
      }
    } catch (SQLException e1) {
      GemLogger.logException(getClass().getSimpleName() + "#validation", e1, contentPane);
      return false;
    }
    cancel();
    return true;
  }
  
   @Override
  public boolean cancel() {

    if (actionListener != null) {
      if (actionListener instanceof SearchCtrl) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
      } else if (actionListener instanceof GemDesktop) {
        clear();
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
      }
    }

    return true;
  }

  private boolean isValid(RentableObject c) {

    boolean ok = true;

    String t = c.getType();

    if (t == null || t.length() < RentableObject.MIN_TITLE_LENGTH || t.length() > RentableObject.MAX_TITLE_LENGTH) {
      ok = false;
      errors[0] = MessageUtil.getMessage("rentable.invalid.type",
              new Object[] {RentableObject.MIN_TITLE_LENGTH, RentableObject.MAX_TITLE_LENGTH} );
    }

    if (c.getIdentification() != null && c.getIdentification().length() > RentableObject.MAX_IDENT_LENGTH) {
      ok = false;
      errors[1] = MessageUtil.getMessage("rentable.invalid.ident", RentableObject.MAX_IDENT_LENGTH);
    }

    if (c.getDescription() != null && c.getDescription().length() > RentableObject.MAX_DESC_LENGTH) {
      ok = false;
      errors[2] = MessageUtil.getMessage("rentable.invalid.desc", RentableObject.MAX_DESC_LENGTH);
    }

    return ok;

  }

  public void clear() {
    v.clear();
    rv.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();

    if (o == null || !(o instanceof RentableObject)) {
      return false;
    }

    rentable = (RentableObject) o;

    v.set(rentable);

    if (rentable.getId() > 0) {
      btPrev.setText(GemCommand.DELETE_CMD);
      btPrev.setActionCommand(GemCommand.DELETE_CMD);
    } else {
      btPrev.setText("");
    }
    select(0);
    rv.load(rentable.getId(), rentable.toString());
    btNext.setToolTipText(BundleUtil.getLabel("Rental.list.label"));
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(DataCache.findId(id, Model.RentableObject));
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#loadId :" + ex.getMessage());
    }
    return false;
  }

  private void close() {
     if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
  }

}
