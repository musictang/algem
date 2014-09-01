/*
 * @(#)DateTimeCtrl.java	2.8.v 21/05/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ToolTipManager;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.AbstractGemPanelCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * This controller is used to add or remove DateTimePanel components.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.t 11/04/14
 */
public class DateTimeCtrl
        extends AbstractGemPanelCtrl
{

  private List<DateTimePanel> panels;
  private final static int SPACING = 4;

  public DateTimeCtrl() {

    plus = new GemButton("+");
    plus.setMargin(new Insets(0, 4, 0, 4)); //reduction de la taille du bouton
    plus.addActionListener(this);
    plus.setToolTipText(GemCommand.ADD_CMD);
    ToolTipManager.sharedInstance().setInitialDelay(20);
    GemPanel top = new GemPanel(new BorderLayout());
    top.add(new GemLabel(BundleUtil.getLabel("DateTime.label")), BorderLayout.WEST);
    top.add(plus, BorderLayout.EAST);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(top);
    add(Box.createVerticalStrut(SPACING));
    panels = new ArrayList<DateTimePanel>();
    addPanel();
  }

  List<GemDateTime> getRanges() {
    List<GemDateTime> ranges = new ArrayList<GemDateTime>();
    for (DateTimePanel dp : panels) {
      ranges.add(new GemDateTime(dp.getDate(), dp.getHourRange()));
    }
    return ranges;
  }


  @Override
  public void addPanel() {
    DateTimePanel dt = new DateTimePanel();
    dt.addActionListener(this);
    panels.add(dt);
    add(panels.get(panels.size() - 1));
    add(Box.createVerticalStrut(SPACING));
  }

  @Override
  public void removePanel(GemPanel dt) {
    panels.remove((DateTimePanel) dt);
    ((DateTimePanel) dt).removeActionListener(this);
    remove(dt);
    revalidate();
  }

  @Override
  public void clear() {
    for (int i = 1; i < panels.size(); i++) {
      DateTimePanel dp = panels.get(i);
      panels.remove(dp);
      remove(dp);
    }
    panels.get(0).reset();
    revalidate();
  }
}
