/*
 * @(#)PostitView.java	2.13.2 03/05/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import net.algem.planning.DateFrField;
import net.algem.security.User;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Postit view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
 */
public class PostitView
        extends GemPanel
{

  private JComboBox type;
  private GemField issuer;
  private DateFrField term;
  private GemTextArea textArea;

  public PostitView() {
    type = new JComboBox(new String[]{
      BundleUtil.getLabel("Postit.internal.label"),
      BundleUtil.getLabel("Postit.urgent.internal.label"),
      BundleUtil.getLabel("Postit.external.label"),
      BundleUtil.getLabel("Postit.booking.label"),
    });

    issuer = new GemField(20);
    issuer.setMinimumSize(new Dimension(180,issuer.getPreferredSize().height));
    issuer.setEditable(false);

    term = new DateFrField();

    textArea = new GemTextArea(4, 25);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setMargin(new Insets(2,4,4,4));
    JScrollPane scroll = new JScrollPane (textArea);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scroll.setMinimumSize(new Dimension(270,80));

    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Type.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Issuer.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Term.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Message.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(type, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(issuer, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(term, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(scroll, 0, 4, 2, 2, GridBagHelper.BOTH, GridBagHelper.WEST);
  }

  public Postit get() {
    Postit p = new Postit();
    p.setText(textArea.getText());
    p.setTerm(term.getDateFr());
    p.setType(type.getSelectedIndex());
    return p;
  }

  void set(Postit p) {
    try {
      User u = (User) DataCache.findId(p.getIssuer(), Model.User);
      issuer.setText(u == null ? null : u.getFirstnameName());
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#set :"+ ex.getMessage());
    }
    type.setSelectedIndex(p.getType());
    term.set(p.getTerm());
    textArea.setText(p.getText());
  }

  void clear() {
    textArea.clear();
    type.setSelectedIndex(0);
  }
}
