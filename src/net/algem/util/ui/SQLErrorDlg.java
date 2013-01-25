/*
 * @(#)SQLErrorDlg.java	2.6.a 25/09/12
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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.swing.JDialog;
import net.algem.util.GemCommand;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class SQLErrorDlg
        implements ActionListener
{

  private JDialog dlg;
  private GemTextArea trace;
  private GemButton okButton;

  public SQLErrorDlg(Component c, Exception ex, String message) {
    if (message == null) {
      message = "no message";
    }

    dlg = new JDialog(PopupDlg.getTopFrame(c), true);

    trace = new GemTextArea();
    trace.setSize(300, 100);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter pos = new PrintWriter(bos);

    if (ex instanceof SQLException) {
      SQLException sqle = (SQLException) ex;
      pos.println("SQLException: " + message);

      while (sqle != null) {
        pos.println("  SQLState  : " + sqle.getSQLState());
        pos.println("  Message   : " + sqle.getMessage());
        pos.println("  Error code: " + sqle.getErrorCode());
        sqle = sqle.getNextException();
      }
    } else {
      pos.println("Exception: " + message);
    }

    /* FIXME que les premieres lignes net.algem (classfile+ligne)
     * pos.println("\nStackTrace"); ex.printStackTrace(pos);
     */
    pos.flush();

    trace.setText(bos.toString());

    okButton = new GemButton(GemCommand.OK_CMD);
    okButton.addActionListener(this);

    GemPanel p = new GemPanel();
    p.setLayout(new FlowLayout(FlowLayout.CENTER));
    p.add(new GemLabel("Erreur SQL"));

    dlg.getContentPane().add("North", p);
    dlg.getContentPane().add("Center", trace);
    dlg.getContentPane().add("South", okButton);

    dlg.pack();

    Point d = c.getLocation();
    dlg.setLocation(d.x + 20, d.y + 20);

    dlg.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      dlg.setVisible(false);
      //dlg.dispose();
    }
  }
}
