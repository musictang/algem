/*
 * @(#)ModifPlanActionDlg.java 2.7.a 07/01/13
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

package net.algem.planning.editing;

import java.sql.SQLException;
import javax.swing.JDialog;
import net.algem.planning.Action;
import net.algem.util.module.GemDesktop;

/**
 * Dialog for changing planification parameters.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.5.a 22/06/12
 */
class ModifPlanActionDlg 
  extends ModifPlanDlg
{

  private ModifPlanActionView pv;
  private int id;
  private int courseId;
  
  public ModifPlanActionDlg(GemDesktop desktop, Action a) throws SQLException {
    super(desktop.getFrame());
    id = a.getId();
    courseId = a.getCourse();
//    ActionService service = new ActionService(desktop.getDataCache().getDataConnection());
    pv = new ModifPlanActionView(desktop.getDataCache(), a);
    dlg = new JDialog(parent, true);
    addContent(pv, null);
  }

  @Override
  public boolean isEntryValid() {
    return pv.isEntryValid();
  }

  @Override
  public boolean isValidate() {
    return validation;
  }
  
  public Action get() {
    Action a = new Action();
    a.setId(id);
    a.setCourse(courseId);
    a.setLevel(pv.getLevel());
    a.setPlaces(pv.getPlaces());
    a.setAgeRange(pv.getRange());
    a.setStatus(pv.getStatus());
    
    return a;
  }


}
