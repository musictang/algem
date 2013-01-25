/*
 * @(#)ModuleCtrl.java	2.7.a 26/11/12
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
package net.algem.course;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Module controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class ModuleCtrl
        extends CardCtrl {

  protected GemDesktop desktop;
  protected DataCache dataCache;
  protected ModuleView view;
  protected Module module;

  public ModuleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    view = new ModuleView();
    addCard("Module", view);
    select(0);
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
    switch (step) {
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {

    Module m = view.get();
    try {
      if (isValid(m)) {
       edit(m);
      }
    } catch (ModuleException e1) {
      MessagePopup.error(this, e1.getMessage());
      GemLogger.logException("Edition module", e1);
      return false;
    }

    return true;
  }

  public void clear() {
    view.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof Module)) {
      return false;
    }

    module = (Module) o;
    try {
      view.set(module);
      select(0);
    } catch (Exception e) {
      GemLogger.logException("lecture ficher module", e);
      return false;
    }
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(((ModuleIO)DataCache.getDao(Model.Module)).findId(id));
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
      return false;
    }
  }

  private boolean isValid(Module m) throws ModuleException {
    if (getClass() == ModuleSearchDeleteCtrl.class) {
      return true;
    }
    String e = "";
    if (m.getCode().length() < 8) {
      e += MessageUtil.getMessage("invalid.module.code");
    }
    if (m.getTitle().length() < 1 || m.getTitle().length() > ModuleIO.TITLE_MAX_LEN) {
      e += "\n" + MessageUtil.getMessage("invalid.name", new Object[] {1, ModuleIO.TITLE_MAX_LEN});
    }
    if (m.getBasePrice() < 0.0) {
      e += "\n" + MessageUtil.getMessage("invalid.module.price");
    }
    if (m.getMonthReducRate() < 0.0 || m.getMonthReducRate() > 100.0) {
      e += "\n" + MessageUtil.getMessage("invalid.module.month.reduc");
    }
    if (m.getQuarterReducRate() < 0.0 || m.getQuarterReducRate() > 100.0) {
      e += "\n" + MessageUtil.getMessage("invalid.module.trim.reduc");
    }
    if (!e.isEmpty()) {
      throw new ModuleException(e);
    }
    return true;
  }

  protected void edit(Module m) throws ModuleException {
    try {
      ModuleService service = new ModuleService(dataCache.getDataConnection());
      service.update(m);
      dataCache.update(m);
      desktop.postEvent(new ModuleEvent(this, GemEvent.MODIFICATION, m));
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
      }
      
    } catch (SQLException ex) {
      throw new ModuleException("Update module : " + ex.getMessage());
    }
  }
}
