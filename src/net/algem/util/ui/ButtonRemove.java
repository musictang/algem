/*
 * @(#)ButtonRemove.java	2.8.a 14/03/13
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
 * 
 */
package net.algem.util.ui;

import java.awt.Component;
import net.algem.util.GemCommand;
import net.algem.util.ImageUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 13/03/2013
 */
public class ButtonRemove 
  extends GemButton

{

  private Component container;

  public ButtonRemove(Component container) {
    this();
    this.container = container;
  }

  public ButtonRemove() {
    super(ImageUtil.createImageIcon(ImageUtil.DELETE_ICON));
    setActionCommand(GemCommand.REMOVE_CMD);
  }

  public Component getContainer() {
    return container;
  }

}
