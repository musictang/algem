/*
 * @(#)RemovablePanel.java	2.8.v 21/05/14
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

package net.algem.util.ui;

import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import net.algem.util.GemCommand;
import net.algem.util.ImageUtil;

/**
 * Abstract panel with removing button.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 21/05/14
 */
public abstract class RemovablePanel 
  extends GemPanel
{
  protected GemRemovingButton removeBt;

  public RemovablePanel() {
    ImageIcon resetIcon = ImageUtil.createImageIcon(ImageUtil.DELETE_ICON);
    removeBt = new GemRemovingButton(resetIcon, this);
    removeBt.setActionCommand(GemCommand.REMOVE_CMD);
    removeBt.setToolTipText(GemCommand.REMOVE_CMD);
  }
  
  public void addActionListener(ActionListener listener) {
    removeBt.addActionListener(listener);
  }

  public void removeActionListener(ActionListener listener) {
    removeBt.removeActionListener(listener);
  }

}
