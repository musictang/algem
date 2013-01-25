/*
 * @(#)MajoCtrl.java	1.0a 07/07/1999
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
package net.algem.opt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class MajoCtrl
        extends GemPanel
        implements ActionListener
{

  DataConnection dc;
  MajoView table;
  GemField status;

  public MajoCtrl(DataConnection _dc) {
    dc = _dc;

    this.setLayout(new BorderLayout());
    table = new MajoView("Majoration");
    table.addActionListener(this);

    add("Center", table);

    load();
  }

  public void load() {
    Vector v = MajorationIO.find("", dc);
    Enumeration enu = v.elements();
    while (enu.hasMoreElements()) {
      table.addRow((Majoration) enu.nextElement());
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.MODIFY_CMD)) {
      try {
        modification();
      } catch (Exception e) {
        GemLogger.logException("modification majoration", e, this);
      }
    } else if (cmd.equals(GemCommand.DELETE_CMD)) {
      try {
        suppression();
        clear();
      } catch (Exception e) {
        GemLogger.logException("suppresion majoration", e, this);
      }
    } else if (cmd.equals(GemCommand.ADD_CMD)) {
      try {
        insertion();
        clear();
      } catch (Exception e) {
        GemLogger.logException("insertion majoration", e, this);
      }
    }
  }

  int modification() throws SQLException {
    Majoration m = table.getMajo();
    MajorationIO.update(m, dc);

    table.modRow(m);
    clearField();
    return 1;
  }

  void insertion() throws SQLException {
    Majoration m = table.getMajo();
    MajorationIO.insert(m, dc);

    table.addRow(m);
    clearField();
  }

  void suppression() throws SQLException {
    Majoration m = table.getMajo();
    MajorationIO.delete(m, dc);

    table.deleteCurrent();
    clearField();
  }

  void clearField() {
    table.setId("0");
    table.setMode("");
    table.setPCent("");
  }

  void clear() {
//		table.clear();
  }
}
