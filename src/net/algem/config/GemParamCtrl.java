/*
 * @(#)GemParamCtrl.java 2.6.a 18/09/12
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

package net.algem.config;

import java.sql.SQLException;
import net.algem.planning.ActionService;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 06/07/12
 */
public abstract class GemParamCtrl 
  extends ParamTableCtrl 
{

  protected ActionService service;
  
  public GemParamCtrl(GemDesktop _desktop, String title) {
    super(_desktop, title, true);    
  }
  
  @Override
  public void setView(boolean activable) {
    table = new GemParamTableView(title, new GemParamTableModel());
    mask = new GemParamView();
  }
  
  @Override
  public boolean isKeyModif() {
    return true;
  }
  
  protected boolean isValidInsert(GemParam n) throws ParamException, SQLException {
    if (isNullCode(n)) {
      throw new ParamException(MessageUtil.getMessage("none.level.existing.code", new Object[] {n.getCode()}));
    }
    return isValid(n);
  }
  
  protected boolean isValidUpdate(GemParam n) throws ParamException, SQLException {
    if (n.getId() == 0 && !isNullCode(n)) {
      throw new ParamException(MessageUtil.getMessage("none.level.code.warning"));
    } 
    return isValid(n);
  }
  
  private boolean isNullCode(GemParam n) {
    return (GemParam.NONE.equals(n.getCode()));
  }
  
  protected abstract boolean isValid(GemParam n) throws ParamException, SQLException;
  
}
