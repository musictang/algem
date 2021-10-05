/*
 * @(#)LevelCtrl.java 2.9.4.13 15/10/15
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

package net.algem.config;

import java.sql.SQLException;
import net.algem.planning.ActionIO;
import net.algem.planning.ActionService;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.5.a 22/06/2012
 */
public class LevelCtrl 
  extends GemParamCtrl 
{
  
  public LevelCtrl(GemDesktop desktop, String title) {
    super(desktop, title);
  }

  @Override
  public void load() {
    service = new ActionService(desktop.getDataCache());
    try {
      load(service.getLevelAll().elements());
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void modification(Param current, Param p) throws SQLException, ParamException {
    if (p instanceof GemParam) {
      Level level = new Level((GemParam) p);
      if (isValidUpdate(level) && level.getId() > 0) {
        service.updateLevel(level);
        desktop.getDataCache().update(level);
        desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.LEVEL, level));
      }
    }
  }

  @Override
  public void insertion(Param p) throws SQLException, ParamException {
    if (p instanceof GemParam) {
      Level level = new Level((GemParam) p);
      if (isValidInsert(level)) {
        service.insertLevel(level);
        p.setId(level.getId());// important
        desktop.getDataCache().add(level);
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.LEVEL, level));
      }
    }
  }

  @Override
  public void suppression(Param p) throws Exception {
    if (p instanceof GemParam) {
      Level level = new Level((GemParam) p);
      if (level.getId() == 0) {
        throw new ParamException(MessageUtil.getMessage("level.default.delete.exception"));
      }
      int used = ((ActionIO) DataCache.getDao(Model.Action)).haveLevel(level.getId());
      if (used > 0) {
        throw new ParamException(MessageUtil.getMessage("level.delete.exception", used));
      }
      if (MessagePopup.confirm(contentPane, MessageUtil.getMessage("param.delete.confirmation"))) {
        service.deleteLevel((GemParam)p);
        desktop.getDataCache().remove(level);
        desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.LEVEL, level));
      } else {
        throw new ParamException();
      }
    }
  }
  
  @Override
  protected boolean isValid(GemParam n) throws ParamException, SQLException {
    String msg = service.verifyLevel(n);
    if (msg != null) {
      throw new ParamException(msg);
    }
    return true;
  }

}
