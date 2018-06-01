/*
 * @(#) ChangeEnrolmentDateDlg.java Algem 2.15.8 21/03/2018
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.15.8 21/03/2018
 */
public class ChangeEnrolmentDateDlg
        extends AbstractEditDlg
{
  private DateFrField orig, changed;
  private GemDesktop desktop;

  public ChangeEnrolmentDateDlg(GemDesktop desktop, String title, boolean modal) {
    super(desktop.getFrame(), title, modal);
    this.desktop = desktop;
  }

  public void initUI(Order e) {
    orig = new DateFrField(e.getCreation());
    orig.setEditable(false);
    changed = new DateFrField(e.getCreation());

    JPanel p = new JPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);

    gb.add(new JLabel(BundleUtil.getLabel("Current.date.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("New.date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(orig, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(changed, 1, 1, 1, 1, GridBagHelper.WEST);
    JPanel buttons = new JPanel();
    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btOk);
    buttons.add(btCancel);

    setLayout(new BorderLayout());
    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.S_SIZE);
    pack();
    setLocationRelativeTo(desktop.getFrame());
  }

  DateFr getDate() {
    return changed.get();
  }

}
