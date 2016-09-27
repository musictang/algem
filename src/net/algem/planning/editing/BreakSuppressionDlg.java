/*
 * @(#)BreakSuppressionDlg.java 2.6.a 21/09/12
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
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.*;

/**
 * Break suppression dialog.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.d 07/10/11
 */
public class BreakSuppressionDlg
        extends JDialog
        implements ActionListener
{

  private GemButton btCancel;
  private GemButton btValidation;
  private GemField from;
  private DateRangePanel to;
  private boolean validation;
  private static final String title = MessageUtil.getMessage("break.delete.dialog.title");

  /**
   * Opens the dialog for break suppression.
   * End date {@code e} should be the end of school year by default.
   * @param owner parent
   * @param modal
   * @param b start date
   * @param e end date
   */
  public BreakSuppressionDlg(Frame owner, boolean modal, DateFr b, DateFr e) {
    super(owner, title, modal);
    setLayout(new BorderLayout());

    GemPanel content = new GemPanel();
    content.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(content);

    from = new GemField(b.toString());
    from.setEditable(false);

    to = new DateRangePanel(e, BorderFactory.createEmptyBorder());

    gb.add(new GemLabel(BundleUtil.getLabel("Date.From.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(from, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.To.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(to, 1, 1, 1, 1, GridBagHelper.WEST);

    add(content, BorderLayout.CENTER);

    btValidation = new GemButton(BundleUtil.getLabel("Action.validation.label"));
    btValidation.addActionListener(this);
    btCancel = new GemButton(BundleUtil.getLabel("Action.cancel.label"));
    btCancel.addActionListener(this);

    GemPanel cmdPanel = new GemPanel();
    cmdPanel.setLayout(new GridLayout(1, 1));
    cmdPanel.add(btValidation);
    cmdPanel.add(btCancel);

    add(cmdPanel, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(owner);
    setVisible(true);
  }

  public boolean isValidate() {
    return validation;
  }

  public DateFr getDate() {
    return to.get();
  }

  private boolean isValidInput() {
    return DateFr.isValid(to.get()) && to.get().afterOrEqual(new DateFr(from.getText()));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(BundleUtil.getLabel("Action.validation.label"))) {
      if (!isValidInput()) {
        MessagePopup.warning(null, MessageUtil.getMessage("break.end.date.error"));
        return;
      }
      validation = true;
    } else {
      validation = false;
    }
    this.dispose();
  }
}
