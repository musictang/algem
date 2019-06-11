/*
 * @(#)InfoPanel.java	2.13.0 22/03/2017
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

package net.algem.contact;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;
import javax.swing.JCheckBox;
import net.algem.config.Param;
import net.algem.config.ParamChoice;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 */
public class InfoPanel extends GemPanel {

  protected static Insets PADDING = new Insets(0,5,0,5);
  protected static Insets LEFT_SPACING = new Insets(0,4,0,0);
  protected static Insets RIGHT_SPACING = new Insets(0,0,0,4);
  protected GemButton iButton;
  protected GemField iField;
  protected JCheckBox iArchive;
  protected ParamChoice iChoice;
  protected GridBagHelper gb;
  protected int col = 4;

  public InfoPanel() {
    setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
//    gb.insets = RIGHT_SPACING;
  }

  public InfoPanel (String label) {
    this();
    init(label);
  }

  protected void init(String label) {

    iButton = new GemButton(label);
    iButton.setMargin(PADDING);
    iField = new GemField();

    iArchive = new JCheckBox();
    iArchive.setBorder(null);
    gb.add(iButton, 0,0,1,1, RIGHT_SPACING, GridBagHelper.HORIZONTAL, 0.0, 0.0);
    gb.add(iField, 1,0,3,1, RIGHT_SPACING, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
    gb.add(iArchive, 4,0,1,1, GridBagConstraints.HORIZONTAL, 0.0, 0.0);
  }

  protected void init(Vector<Param> v, boolean withArchive) {

    iChoice = new ParamChoice(v);
    iField = new GemField();
    gb.add(iChoice, 0,0,1,1, RIGHT_SPACING, GridBagConstraints.HORIZONTAL, 0.0, 0.0);
    gb.add(iField, 1,0,3,1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
    if (withArchive) {
      iArchive = new JCheckBox();
      iArchive.setBorder(null);
      gb.add(iArchive, col++,0,1,1, LEFT_SPACING, GridBagConstraints.EAST);
    }

  }

  protected void addButton(String label, String tooltip) {
    this.addButton(label);
    iButton.setToolTipText(tooltip);
  }

  protected void addButton(String label) {
    iButton = new GemButton(label);
    iButton.setMargin(PADDING);
    gb.add(iButton, col,0,1,1, LEFT_SPACING, GridBagHelper.EAST);
    revalidate();
  }


}
