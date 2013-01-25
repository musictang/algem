/*
 * @(#)MailPanel.java	2.6.a 17/09/12
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
package net.algem.contact;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.ui.MessagePopup;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MailPanel
        extends InfoPanel
        implements ActionListener
{

  private int idper;
  private DesktopMailHandler mailer;

  public MailPanel(Email email, String label, DesktopMailHandler mailer) {
    super(label);
    setEmail(email);
    this.mailer = mailer;
    iButton.addActionListener(this);
    iArchive.addActionListener(this);
    iArchive.setToolTipText(BundleUtil.getLabel("Action.archive.label"));
    iField.addFocusListener(new FocusAdapter()
    {

      public void focusLost(FocusEvent evt) {
        if (!EmailField.check(iField.getText())) {
          MessagePopup.error(null,MessageUtil.getMessage("invalid.email.warning"));
        }
      }
    });
  }

  public Email getEmail() {
    Email e = new Email();
    e.setIdper(idper);
    e.setEmail(iField.getText().trim());
    e.setArchive(iArchive.isSelected());
    return e;
  }

  public void setEmail(Email email) {

    idper = email.getIdper();
    iField.setText(email.getEmail());
    iArchive.setSelected(email.isArchive());
    setEditable(iArchive.isSelected());

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == iButton) {
      String to = iField.getText();
      if (to.length() > 0) {
        mailer.send(to, null); // null pour bcc
      }
    } else if (e.getSource() == iArchive) {
      setEditable(iArchive.isSelected());
    }
  }

  private void setEditable(boolean selected) {
    iButton.setEnabled(!selected);
    iField.setEditable(!selected);
    iField.setBackground(!selected ? Color.white : InfoView.ARCHIVE_COLOR);
  }
}
