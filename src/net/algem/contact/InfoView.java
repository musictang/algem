/*
 * @(#)InfoView.java	2.8.k 23/07/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

package net.algem.contact;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 */
public abstract class InfoView
  extends GemPanel implements ActionListener
{

  protected List<InfoPanel> rows;
  private GemButton btAdd;
  public final static Color ARCHIVE_COLOR = new Color(153, 153, 204); // #9999CC

  public InfoView(String label, boolean border) {
    rows = new ArrayList<InfoPanel>();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    GemPanel header = new GemPanel();
    if (border) {
      setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
    header.add(new JLabel(label));
    header.add(Box.createHorizontalGlue());
    btAdd = new GemButton("+");
    btAdd.setToolTipText(MessageUtil.getMessage("add.entry"));
    btAdd.setMargin(new Insets(0, 4, 0, 4)); //reduction de la taille du bouton
    btAdd.addActionListener(this);
    header.add(btAdd);
    add(header);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    addRow();
  }

  protected void clearAll() {
    for(InfoPanel p : rows) {
      remove(p);
    }
    rows.clear();
    revalidate();
  }

  protected void setEditable(boolean b) {
    btAdd.setEnabled(b);
  }

  protected abstract void addRow();

  public void clear() {
    for (int i = rows.size()-1 ; i >= 0 ; i--) {
      remove(rows.remove(i));
    }

    /*InfoPanel  p = rows.get(0);
    if (p.iArchive != null) {
      p.iArchive.setSelected(false);
    }
    if (p.iBouton != null) {
      p.iBouton.setEnabled(true);
    }
    if (p.choix != null) {
      p.choix.setEnabled(true);
      p.choix.setSelectedIndex(0);
    }
    p.iField.setText(null);*/
  }
}
