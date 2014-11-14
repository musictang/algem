/*
 * @(#)ChangeModuleTimeDlg.java	2.9.1 14/11/14
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
package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GridBagHelper;

/**
 * Time length modification dialog box.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.9.1 14/11/14
 */
public class ChangeModuleTimeDlg

        extends JDialog
        implements ActionListener
{

  private final HourField orig;
  private final HourField hours;
  private boolean validation;
  private final GemButton btOk;
  private final GemButton btCancel;

  public ChangeModuleTimeDlg(GemDesktop desktop, String title) {
    super(desktop.getFrame(), title, true);
    
    orig = new HourField("01:00", true);
    orig.setEnabled(false);
    hours = new HourField("01:00", true);
    JPanel p = new JPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new JLabel(BundleUtil.getLabel("Action.back.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("New.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(orig, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(hours, 1, 1, 1, 1, GridBagHelper.WEST);

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
    setSize(GemModule.XXS_SIZE);
    setLocationRelativeTo(desktop.getFrame());
  }

  /**
   * Sets the actual time.
   * @param h actual time
   */
  void set(Hour h) {
    orig.set(h);
    hours.set(h);
  }

  /**
   * Gets the new time in minutes.
   * @return a length in minutes
   */
  int get() {
    return hours.getHour().toMinutes();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    validation = (evt.getSource() == btOk);
    close();
  }

  /**
   * Gets the state of validation.
   *
   * @return true if validation
   */
  public boolean isValidation() {
    return validation;
  }

  /**
   *  Closes this dialog.
   */
  private void close() {
    setVisible(false);
    dispose();
  }

}
