/*
 * @(#)StopCourseFromModuleDlg.java	2.8.w 05/09/14
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
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import net.algem.enrolment.ModuleOrder;
import net.algem.enrolment.StopCourseAbstractDlg;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;

/**
 * Dialog used to stop a module.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:damien.loustau@gmail.com">Damien Loustau</a>
 * @version 2.8.w
 * @since 1.0a 27/09/2001
 */
public class StopCourseFromModuleDlg
        extends StopCourseAbstractDlg
{

  private boolean validation;

  public StopCourseFromModuleDlg(GemDesktop desktop, ModuleOrder moduleOrder) {
    super(desktop.getFrame(), BundleUtil.getLabel("Module.stop.label"), true);
    view = new StopCourseView(moduleOrder.getTitle());

    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btOk);
    buttons.add(btCancel);

    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.XXS_SIZE);
    setLocationRelativeTo(desktop.getFrame());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    validation = (evt.getSource() == btOk);
    close();
  }

  /**
   * Gets the stop date.
   * @return a date
   */
  public DateFr getEndDate() {
    return checkDate(view.getDateStart());
  }

  /**
   * Gets the state of validation.
   * @return true if validation
   */
  public boolean isValidation() {
    return validation;
  }
 
}
