/*
 * @(#)CourseCodeCtrl.java	2.8.a 14/03/13
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.course.CourseCode;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 14/03/2013
 */
public class CourseCodeCtrl 
  extends ParamTableCtrl
 {
  
  private CourseCodeIO ccIO;

  public CourseCodeCtrl(GemDesktop _desktop) {
    super(_desktop, "Codes cours", false);
  }
  
  @Override
  public void setView(boolean activable) {
    table = new GemParamTableView(title, new GemParamTableModel());
    mask = new GemParamView(true, 6);
  }

  @Override
  protected boolean isKeyModif() {
    return false;
  }
  
  
  @Override
  public void load() {
    ccIO = new CourseCodeIO(dc);
//    try {
      List<GemParam> codes = desktop.getDataCache().getList(Model.CourseCode).getData();
      for(GemParam p : codes) {
        table.addRow(p);
      }
//      String query = "SELECT * FROM module_type ORDER BY id";
//      
//      ResultSet rs = dc.executeQuery(query);
//      while(rs.next()) {
//        CourseCode p = new CourseCode();
//        p.setId(rs.getInt(1));
//        p.setKey(rs.getString(2));
//        p.setValue(rs.getString(3));
//        table.addRow(p);
//      }
//    } catch (SQLException ex) {
//      GemLogger.logException(ex);
//    }
  }

  @Override
  public void modification(Param current, Param p) throws SQLException, ParamException {
    if (p instanceof GemParam) {
      CourseCode cc = new CourseCode((GemParam) p);
      ccIO.update(cc);
      desktop.getDataCache().update(cc);
      desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.COURSE_CODE, cc));
      
    }
  }

  @Override
  public void insertion(Param p) throws SQLException, ParamException {
    if (p instanceof GemParam) {
      CourseCode cc = new CourseCode((GemParam) p);
      ccIO.insert(cc);
      desktop.getDataCache().add(cc);
      desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.COURSE_CODE, cc));
    }
  }

  @Override
  public void suppression(Param p) throws Exception {
    if (p instanceof GemParam) {
      CourseCode cc = (CourseCode) p;
      ccIO.delete((CourseCode) p);
      desktop.getDataCache().remove(cc);
      desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.COURSE_CODE, cc));
    }
  }

}
