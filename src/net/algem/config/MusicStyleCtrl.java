/*
 * @(#)MusicStyleCtrl.java	2.6.a 12/09/12
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
import net.algem.util.DataCache;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MusicStyleCtrl
        extends ParamTableCtrl
{

  private final static String COLUMN_KEY = "id";
  private final static String COLUMN_NAME = "libelle";
  private DataCache dataCache;

  public MusicStyleCtrl(GemDesktop _desktop) {
    super(_desktop, "Style musical", false);
    dataCache = _desktop.getDataCache();
  }

  @Override
  public void load() {
    load(ParamTableIO.find(MusicStyleIO.TABLE, COLUMN_NAME, dc).elements());
  }

  @Override
  public void modification(Param _current, Param _p) throws SQLException {
    ParamTableIO.update(MusicStyleIO.TABLE, COLUMN_KEY, COLUMN_NAME, _p, dc);
    MusicStyle ms = new MusicStyle(Integer.parseInt(_p.getKey()), _p.getValue());
    dataCache.update(ms);
    desktop.postEvent(new MusicStyleEvent(this, GemEvent.MODIFICATION, ms));
  }

  @Override
  public void insertion(Param _p) throws SQLException {
    ParamTableIO.insert(MusicStyleIO.TABLE, MusicStyleIO.SEQUENCE, _p, dc);
    MusicStyle ms = new MusicStyle(Integer.parseInt(_p.getKey()), _p.getValue());
    dataCache.add(ms);
    desktop.postEvent(new MusicStyleEvent(this, GemEvent.CREATION, ms));
  }

  @Override
  public void suppression(Param _p) throws SQLException {
    ParamTableIO.delete(MusicStyleIO.TABLE, COLUMN_KEY, _p, dc);
    MusicStyle ms = new MusicStyle(Integer.parseInt(_p.getKey()), _p.getValue());
    dataCache.remove(ms);
    desktop.postEvent(new MusicStyleEvent(this, GemEvent.SUPPRESSION, ms));
  }
}
