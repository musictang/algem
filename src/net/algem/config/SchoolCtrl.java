/*
 * @(#)SchoolCtrl  2.9.4.13 16/10/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes All Rights Reserved.
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
import java.util.Vector;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class SchoolCtrl extends ParamTableCtrl
{

  public static final String TABLE = "ecole";
	public static final String SORT_COLUMN = "id";
	public static final String COLUMN_KEY = "id";

  private static final String SEQUENCE = "ecole_id_seq";
  private static final String COLUMN_NAME = "nom";
  
  public SchoolCtrl() {
  }

  public SchoolCtrl(GemDesktop _desktop) {
    super(_desktop, BundleUtil.getLabel("Menu.school.label"), false, -1);
  }

  @Override
  public void load() {
    load(ParamTableIO.find(TABLE, SORT_COLUMN, dc).elements());
  }

  @Override
  public void modification(Param current, Param p) throws SQLException {
    ParamTableIO.update(TABLE, COLUMN_KEY, COLUMN_NAME, p, dc);
  }

  @Override
  public void insertion(Param p) throws SQLException {
    ParamTableIO.insert(TABLE, SEQUENCE, p, dc);
  }

  @Override
  public void suppression(Param p) throws SQLException, ConfigException {
    //TODO check if school id is used in order lines
    Vector<Param> vp = ParamTableIO.find(TABLE, SORT_COLUMN, dc);
    if (vp != null && vp.size() <= 1) {
      throw new ConfigException(MessageUtil.getMessage("school.delete.info"));
    }
    ParamTableIO.delete(TABLE, COLUMN_KEY, p, dc);
  }

  public String find() {
    Param p = ParamTableIO.findBy(TABLE, " ORDER BY id LIMIT 1", dc);
    return p == null ? null : p.getValue();
  }
}
