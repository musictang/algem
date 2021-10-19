/*
 * @(#)MaritalStatusCtrl.java 2.9.3 24/02/2015
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
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3
 * @since 2.9.3 24/02/2015
 */
public class MaritalStatusCtrl
        extends GemParamCtrl
{

  private GemParamService paramService;

  public MaritalStatusCtrl(GemDesktop desktop) {
    super(desktop, BundleUtil.getLabel("Marital.status.label"));
    GemParamIO io = new MaritalStatusIO(dc);
    this.paramService = new DefaultGemParamService();
    paramService.setDAO(io);
  }

  @Override
  protected boolean isValid(GemParam n) throws ParamException, SQLException {
    return n.getCode() != null && n.getCode().length() == 1 && n.getLabel() != null && n.getLabel().length() > 0;
  }

  @Override
  public void load() {
    try {
      load(paramService.load());
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void modification(Param current, Param p) throws SQLException, ParamException {
      paramService.update((GemParam) p);
      MaritalStatus m = new MaritalStatus((GemParam) p);
      desktop.getDataCache().update(m);
  }

  @Override
  public void insertion(Param p) throws SQLException, ParamException {
    paramService.insert((GemParam) p);
    MaritalStatus m = new MaritalStatus((GemParam) p);
    desktop.getDataCache().add(m);
  }

  @Override
  public void suppression(Param p) throws Exception {
    paramService.delete((GemParam) p);
    MaritalStatus m = new MaritalStatus((GemParam) p);
    desktop.getDataCache().remove(m);
  }

}
