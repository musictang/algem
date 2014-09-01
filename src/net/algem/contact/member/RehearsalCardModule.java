/*
 * @(#)RehearsalCardModule.java 2.8.w 27/08/14
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
package net.algem.contact.member;

import java.awt.Container;
import java.awt.event.ActionEvent;
import net.algem.util.module.DefaultGemModule;
import net.algem.util.module.GemDesktopCtrl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class RehearsalCardModule
        extends DefaultGemModule
{

  public RehearsalCardModule(String label, Container p) {
    super(label, p);
    if (p instanceof RehearsalCardSearchCtrl) {
      ((RehearsalCardSearchCtrl) p).addActionListener(this);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    ((GemDesktopCtrl) desktop).actionPerformed(evt);
  }
}
