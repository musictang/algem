/*
 * @(#)DefaultGemModule.java 2.8.w 27/08/14
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

package net.algem.util.module;

import java.awt.Container;

/**
 * Basic algem module.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 27/08/14
 */
public class DefaultGemModule 
  extends GemModule
{

  public DefaultGemModule(String label) {
    super(label);
  }

  public DefaultGemModule(String label, Container p) {
    super(label, p);
  }

  @Override
  public void init() {
    view = new DefaultGemView(desktop, label);
    view.setSize(DEFAULT_SIZE);
    view.getContentPane().add("Center", container);
  }

}
