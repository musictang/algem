/*
 * @(#)MusicStyleCtrl.java	2.9.4.13 16/10/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import net.algem.group.GroupIO;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 * Manages categories of musical style.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class MusicStyleCtrl
        extends ParamTableCtrl
{

  private static final String COLUMN_KEY = "id";
  private static final String COLUMN_NAME = "libelle";
  private DataCache dataCache;

  public MusicStyleCtrl(GemDesktop _desktop) {
    super(_desktop, "Style musical", false, -1);
    dataCache = _desktop.getDataCache();
  }

  @Override
  public void load() {
    load(ParamTableIO.find(MusicStyleIO.TABLE, COLUMN_NAME, dc));
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
  public void suppression(Param p) throws SQLException, ParamException {
    MusicStyle ms = new MusicStyle(Integer.parseInt(p.getKey()), p.getValue());
    if (ms.getId() == 0) {
      throw new ParamException(MessageUtil.getMessage("musical.style.default.delete.exception"));
    }
    int used = ((GroupIO) DataCache.getDao(Model.Group)).findByStyle(ms.getId());
    if (used > 0) {
     throw new ParamException(MessageUtil.getMessage("musical.style.delete.exception",used));
    }
    else {
      if (MessagePopup.confirm(contentPane, MessageUtil.getMessage("param.delete.confirmation"))) {
        ParamTableIO.delete(MusicStyleIO.TABLE, COLUMN_KEY, p, dc);
        dataCache.remove(ms);
        desktop.postEvent(new MusicStyleEvent(this, GemEvent.SUPPRESSION, ms));
      } else {
        throw new ParamException();
      }
    }
  }
}
