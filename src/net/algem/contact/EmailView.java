/*
 * @(#)EmailView.java	2.6.a 02/08/2012
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

import java.util.Vector;
import net.algem.util.BundleUtil;
import net.algem.util.jdesktop.DesktopMailHandler;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class EmailView extends InfoView
{

  private int rowcount = 1;
  private DesktopMailHandler mailHandler;

  public EmailView() {
    super(BundleUtil.getLabel("Email.label"), true);
    mailHandler = new DesktopMailHandler();
  }

  public void setEmails(Vector<Email> emails) {
    clearAll();
    if (emails != null && emails.size() > 0) {
      for (Email e : emails) {
        MailPanel pm = new MailPanel(e, getLabel(), mailHandler);
        rows.add(pm);
        add(pm);
      }
      revalidate();
    } else {
      addRow();
    }
  }

  public Vector<Email> getEmails() { 
    Vector<Email> v = new Vector<Email>();
    for (InfoPanel pm : rows) {
      Email e = ((MailPanel) pm).getEmail();
      if (e.getEmail() != null && !e.getEmail().isEmpty()) {
        v.add(e);
      }
    }
    if (v.size() > 0) {
      return v;
    }
    return null;
  }

  @Override
  public void clear() {
    super.clear();
    rowcount = 1;
  }

  @Override
  protected void addRow() {
    MailPanel pm = new MailPanel(new Email(), getLabel(), mailHandler);
    rows.add(pm);
    add(pm);
    revalidate();
  }

  private String getLabel() {
    return "Mail"+rowcount++;
  }
}
