/*
 * @(#)AgeRangeCardCtrl.java 2.7.k 05/03/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.k
 * @since 2.3.a
 */
public class AgeRangeCardCtrl
        extends CardCtrl
{

  private AgeRangeView view;
  private AgeRange range;

  public AgeRangeCardCtrl() {
    btPrev.setText(GemCommand.DELETE_CMD);
    view = new AgeRangeView();
    addCard("Fiche tranche age", view);
    select(0);
  }

  public void clear() {
    view.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof AgeRange)) {
      return false;
    }

    range = (AgeRange) o;
    view.setRange(range);

    return true;
  }

  @Override
  public boolean loadId(int id) {

    try {
      return loadCard(DataCache.findId(id, Model.AgeRange));
    } catch (SQLException ex) {
      GemLogger.logException("tranche.searchid.exception", ex);
      return loadCard(null);
    }
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        break;
    }
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
    }
    return true;
  }

  @Override
  public boolean prev() {

    if (MessagePopup.confirm(contentPane, MessageUtil.getMessage("age.range.delete.confirmation"), "Suppression tranche d'âge")) {
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.DELETE_CMD));
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
      }
    }
    return true;
  }

  @Override
  public boolean validation() {

    AgeRange nt = getRange();
    String msg = check(nt);
    if (msg != null) {
      MessagePopup.warning(view, msg);
      return false;
    }

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.APPLY_CMD));
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }

    return true;
  }

  private String check(AgeRange t) {

    String msg = "";

    int agemin = t.getAgemin();
    int agemax = t.getAgemax();

    if (agemin < 0 || agemax < 0) {
      msg += MessageUtil.getMessage("age.range.error.validation1") + "\n";
    }

    if (agemax == 0) {
      msg += MessageUtil.getMessage("age.range.error.validation2") + "\n";
    }

    if (agemax > 150) {
      msg = MessageUtil.getMessage("age.range.error.validation3") + "\n";
    }

    if (agemax <= agemin) {
      msg = MessageUtil.getMessage("age.range.error.validation4");
    }

    if (!t.getCode().matches("^\\p{Alnum}$")) {
      msg = MessageUtil.getMessage("code.alphanumeric.error");
    }


    return msg.isEmpty() ? null : msg;
  }

  public AgeRange getRange() {
    return view.getRange();
  }

  public void disableSuppression() {
    btPrev.setEnabled(false);
  }
}
