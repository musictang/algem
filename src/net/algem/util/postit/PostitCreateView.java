/*
 * @(#)PostitCreateView.java	2.6.a 21/09/12
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
package net.algem.util.postit;

import java.awt.GridBagLayout;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.security.User;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemTextArea;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PostitCreateView
        extends GemBorderPanel
{

  private int userId;
  private List<User> users;
  private JComboBox type;
  private JComboBox receiver;
  private DateFrField term;
  private GemTextArea textArea;

  public PostitCreateView(int _userId, List<User> _users) {
    userId = _userId;
    users = _users;
    type = new JComboBox(new String[]{
      BundleUtil.getLabel("Notes.label"), 
      BundleUtil.getLabel("Urgent.label")
    });
    receiver = new JComboBox(new String[]{
      BundleUtil.getLabel("Private.label"), 
      BundleUtil.getLabel("Public.label")
    });

    for (int i = 2; i < users.size(); i++) {
      receiver.addItem(((User) users.get(i)).getLogin());
    }
    term = new DateFrField();
    textArea = new GemTextArea();

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Type.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Receiver.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Term.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Message.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(type, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(receiver, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(term, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(textArea, 0, 4, 2, 2, GridBagHelper.BOTH, 1.0, 1.0);
  }

  public Postit get() {
    Postit p = new Postit();
    p.setType(type.getSelectedIndex());
    int i = receiver.getSelectedIndex();

    if (i == 0) { // privé
      p.setReceiver(userId); //id de l'utilisateur courant
    } else if (i == 1) { // public
      p.setReceiver(0);
    } else {
      p.setReceiver(((User) users.get(i)).getId()); //id de l'utilisateur choisi
    }
    p.setTerm(term.getDateFr());
    p.setDay(new DateFr(new java.util.Date()));
    p.setText(textArea.getText());
    return p;
  }

  public void clear() {
    textArea.clear();
    type.setSelectedIndex(0);
    receiver.setSelectedIndex(0);
  }
}
