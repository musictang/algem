/*
 * @(#)InstrumentCtrl.java 2.9.4.13 16/10/15
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
import java.util.Collections;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class InstrumentCtrl
        extends ParamTableCtrl
{

  public final static InstrumentComparator instrumentComparator = new InstrumentComparator();
  public static final String TABLE = "instrument";
  public static final String SEQUENCE = "idinstrument";
  public static final String COLUMN_KEY = "id";
  public static final String COLUMN_NAME = "nom";
  private DataCache dataCache;

  public InstrumentCtrl(GemDesktop _desktop) {
    super(_desktop, "Instruments", false);
    dataCache = _desktop.getDataCache();
  }

  @Override
  public void load() {
    load(ParamTableIO.find(TABLE, COLUMN_NAME, dc));
  }

  @Override
  public void modification(Param _current, Param _p) throws SQLException {
    ParamTableIO.update(TABLE, COLUMN_KEY, COLUMN_NAME, _p, dc);
    Instrument i = new Instrument(Integer.parseInt(_p.getKey()), _p.getValue());
    int index = dataCache.getInstruments().indexOf(i);
    if (index != -1) {
      dataCache.getInstruments().set(index, i);
      Collections.sort(dataCache.getInstruments(), instrumentComparator);
    }

  }

  @Override
  public void insertion(Param _p) throws SQLException {
    ParamTableIO.insert(TABLE, SEQUENCE, _p, dc);
    Instrument i = new Instrument(Integer.parseInt(_p.getKey()), _p.getValue());
    dataCache.getInstruments().add(i);
    Collections.sort(dataCache.getInstruments(), instrumentComparator);
  }

  @Override
  public void suppression(Param p) throws SQLException, ParamException {
    Instrument i = new Instrument(Integer.parseInt(p.getKey()), p.getValue());
    if (i.getId() == 0) {
      throw new ParamException(MessageUtil.getMessage("instrument.default.delete.exception"));
    }
    int used = InstrumentIO.findUsed(i.getId(), dc);
    if (used > 0) {
      throw new ParamException(MessageUtil.getMessage("instrument.delete.exception", used));
    } else {
      if (MessagePopup.confirm(contentPane, MessageUtil.getMessage("param.delete.confirmation"))) {
        ParamTableIO.delete(TABLE, COLUMN_KEY, p, dc);
        dataCache.getInstruments().remove(i);
      } else {
        throw new ParamException();
      }
    }
  }
  
}
