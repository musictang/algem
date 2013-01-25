/*
 * @(#)ModuleCreateCtrl.java	2.6.a 03/08/12
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
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ModuleCreateCtrl
        extends ModuleCtrl {

  public ModuleCreateCtrl(GemDesktop _desktop) {
    super(_desktop);
  }
  
  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  @Override
  public boolean loadCard(Object p) {
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }
  
  @Override
  public void edit(Module m) throws ModuleException {
    
    try {
      ModuleService service = new ModuleService(dataCache.getDataConnection());
      service.create(m);
      dataCache.add(m);
      desktop.postEvent(new ModuleEvent(this, GemEvent.CREATION, m));
      MessagePopup.information(this, MessageUtil.getMessage("create.info"));
      cancel();
    } catch (SQLException sqe) {
      throw new ModuleException("Create module : " + sqe.getMessage());
    }
    
  }
}
