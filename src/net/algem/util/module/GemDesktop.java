/*
 * @(#)GemDesktop.java	2.6.a 18/09/12
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
package net.algem.util.module;

import java.awt.Dimension;
import net.algem.util.DataCache;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.event.GemRemoteEvent;

/**
 * Gem desktop interface.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 06/07/2002
 */
public interface GemDesktop
        extends GemEventListener
{

  public void remoteEvent(GemRemoteEvent evt);

  @Override
  public void postEvent(GemEvent evt);

  public void addModule(GemModule module);

  public void addPanel(String label, java.awt.Container panel);

  public void addPanel(String s, java.awt.Container p, Dimension size);

  public boolean hasModule(String key);

  public void removeModule(GemModule module);

  public void removeModule(String key);

  public void removeCurrentModule();

  public GemModule getSelectedModule();

  public void setSelectedModule(String title);

  public void setSelectedModule(GemModule module);

  public void setWaitCursor();

  public void setDefaultCursor();

  public java.awt.Frame getFrame();

  public DataCache getDataCache();

  public void addGemEventListener(GemEventListener l);

  public void removeGemEventListener(GemEventListener l);
}
