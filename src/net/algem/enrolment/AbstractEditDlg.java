/*
 * @(#)AbstractEditDlg.java 2.9.6 24/03/16
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.enrolment;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.ui.GemButton;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.6
 * @since 2.9.6 22/03/16
 */
abstract class AbstractEditDlg 
        extends JDialog
        implements ActionListener
{
  protected GemButton btOk;
  protected GemButton btCancel;
  protected boolean validation;

  public AbstractEditDlg(Frame owner) {
    super(owner);
  }

  public AbstractEditDlg(Frame owner, boolean modal) {
    super(owner, modal);
  }

  public AbstractEditDlg(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    validation = (e.getSource() == btOk);
    close();
  }
  
  protected void close() {
    setVisible(false);
    dispose();
  }
  
  public boolean isValidation() {
    return validation;
  }

}
