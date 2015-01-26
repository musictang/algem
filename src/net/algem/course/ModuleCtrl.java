/*
 * @(#)ModuleCtrl.java	2.8.w 08/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
import net.algem.util.*;
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
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class ModuleCtrl
        extends CardCtrl
{

  protected GemDesktop desktop;
  protected DataCache dataCache;
  protected ModuleView view;
  protected Module module;

  public ModuleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    view = new ModuleView(dataCache);

    addCard(BundleUtil.getLabel("Module.label"), view);
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
      case 0:
        try {
          delete(view.get());
        } catch (ModuleException ex) {
          GemLogger.log(ex.getMessage());
          MessagePopup.warning(this, ex.getMessage());
          return false;
        }
        break;
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {

    Module m = view.get();
    for (CourseModuleInfo cm : m.getCourses()) {
      String err = isValid(cm);
      if (err != null) {
        MessagePopup.error(this, err);         
        return false;
      }
    }
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
  
  private String isValid(CourseModuleInfo cm) {
    String msg = "";
    if (cm.getCode().getId() < 1) {
      msg += MessageUtil.getMessage("course.module.info.invalid.code");
    }
    if (!cm.hasValidLength()) {
      msg += MessageUtil.getMessage("course.module.info.invalid.length",
              new Object[] {cm.getCode().getLabel(), CourseModuleInfo.MIN_LENGTH, CourseModuleInfo.MAX_LENGTH});
    }
    return msg.isEmpty() ? null : msg;
  }

  public void clear() {
    view.clear();
    //btPrev.setActionCommand(GemCommand.PREVIOUS_CMD);
    btPrev.setEnabled(false);
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

      if (module.getId() > 0) {
        btPrev.setText(GemCommand.DELETE_CMD);
        btPrev.setActionCommand(GemCommand.DELETE_CMD);
      } else {
        btPrev.setText("");
      }
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
      return loadCard(((ModuleIO) DataCache.getDao(Model.Module)).findId(id));
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
      return false;
    }
  }

  private boolean isValid(Module m) throws ModuleException {

    String e = "";
    
    if (m.getCode().length() < 1) {
      e += MessageUtil.getMessage("invalid.module.code");
    }
    
    if (m.getCourses() == null || m.getCourses().isEmpty()) {
      e += "\n" + MessageUtil.getMessage("module.course.create.exception");
    }
    
    if (m.getTitle().length() < 1 || m.getTitle().length() > ModuleIO.TITLE_MAX_LEN) {
      e += "\n" + MessageUtil.getMessage("invalid.name", new Object[]{1, ModuleIO.TITLE_MAX_LEN});
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

    DataConnection dc = DataCache.getDataConnection();
    try {
      ModuleService service = new ModuleService(dc);
      dc.setAutoCommit(false);
      if (m.getId() == 0) {
        service.create(m);
        dataCache.add(m);
        desktop.postEvent(new ModuleEvent(this, GemEvent.CREATION, m));
        MessagePopup.information(this, MessageUtil.getMessage("create.info"));
      } else {
        service.update(m);
        dataCache.update(m);
        desktop.postEvent(new ModuleEvent(this, GemEvent.MODIFICATION, m));
      }
      dc.commit();
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
      }
    } catch (SQLException ex) {
      dc.rollback();
      throw new ModuleException("Edit module : " + ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  private void delete(Module m) throws ModuleException {
    DataConnection dc = DataCache.getDataConnection();
    try {
      dc.setAutoCommit(false);
      ModuleService service = new ModuleService(dc);
      service.delete(m);
      dc.commit();
      dataCache.remove(m);
      desktop.postEvent(new ModuleEvent(this, GemEvent.SUPPRESSION, m));
      MessagePopup.information(this, MessageUtil.getMessage("delete.info"));
//      cancel();
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.NEW_SEARCH_CMD));
      }
    } catch (SQLException ex) {
      dc.rollback();
      throw new ModuleException("Edit module : " + ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }
}
