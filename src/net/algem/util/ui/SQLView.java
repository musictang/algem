/*
 * @(#)SQLView.java	2.6.a 25/09/12
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import net.algem.util.DataCache;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class SQLView
        extends GemPanel
        implements ActionListener
{

  private DataCache dataCache;
  private Statement stmt;
  private GemTextArea sqledit;
  private GemButton btSend;

  public SQLView() {

    setLayout(new BorderLayout());
    add("South", btSend = new GemButton("Envoyer"));
    btSend.addActionListener(this);
    sqledit = new GemTextArea();
    add("Center", sqledit);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Envoyer")) {
      String query = sqledit.getText();
      doQuery(query);
    }
  }

  void doQuery(String query) {
    System.out.println("doQuery:" + query);
  }
}
