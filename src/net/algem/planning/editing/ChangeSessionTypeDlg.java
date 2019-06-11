/*
 * @(#)ChangeSessionTypeDlg.java	2.8.v 16/06/14
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

package net.algem.planning.editing;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import net.algem.config.GemParamChoice;
import net.algem.config.GemParamModel;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Dialog to change the type of a studio session.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 16/06/14
 */
public class ChangeSessionTypeDlg 
  extends ModifPlanDlg
{

  private GemChoice type;
  private int prevType;
  
  public ChangeSessionTypeDlg(GemDesktop desktop, int id) {
    super(desktop.getFrame());
    type = new GemParamChoice(new GemParamModel(desktop.getDataCache().getList(Model.StudioType)));
    type.setKey(id);
    prevType = id;
    
    dlg = new JDialog(desktop.getFrame(), true);
    GemPanel gp = new GemPanel(new FlowLayout(FlowLayout.LEFT));
    
    gp.add(new GemLabel(BundleUtil.getLabel("Type.label")));
    gp.add(type);
    gp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    addContent(gp, "Session.type.modification.label");
    dlg.setSize(GemModule.XS_SIZE);
  }

  
  @Override
  public boolean isEntryValid() {
    if (type.getKey() < 0) {
      MessagePopup.warning(dlg, MessageUtil.getMessage("invalid.choice"));
      return false;
    } else if (type.getKey() == prevType) {
      return false;
    } 
    return true;
  }

  @Override
  public boolean isValidate() {
    return isEntryValid();
  }
  
  int getType() {
    return type.getKey();
  }

}
