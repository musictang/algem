/*
 * @(#)FileTab.java	2.6.f 12/11/12
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
package net.algem.util.ui;

import java.util.Collection;
import net.algem.planning.PlanningService;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;

/**
 * Generic tab.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.f
 */
public abstract class FileTab
        extends GemBorderPanel
{

  protected GemDesktop desktop;
  protected DataCache dataCache;
  protected DataConnection dc;
  protected PlanningService planningService;

  public FileTab(GemDesktop _desktop) {
    desktop = _desktop;
    dataCache = _desktop.getDataCache();
    dc = dataCache.getDataConnection();
    planningService = new PlanningService(dc);
  }

  public abstract boolean isLoaded();

  public abstract void load();

  protected <T extends Object> void load(Collection<T> c) {
    System.out.println("To overload");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
