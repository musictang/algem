/*
 * @(#)ContactExportDlg.java	2.15.0 26/07/2017
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
package net.algem.edition;

import java.awt.Dialog;
import javax.swing.JComboBox;
import net.algem.contact.Person;
import net.algem.contact.member.MemberIO;
import net.algem.room.RoomIO;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemPanel;

/**
 * Export mailing contact.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 1.0a 14/12/1999
 */
public class ContactExportDlg
        extends ExportDlg
{

  private static final String CONTACT_TITLE = BundleUtil.getLabel("Export.contact.title");
  private static Object[] criteria = {
    MessageUtil.getMessage("export.criterium.contact.all"),
    MessageUtil.getMessage("export.criterium.contact.organization"),
    MessageUtil.getMessage("export.criterium.contact.not.member"),
    MessageUtil.getMessage("export.criterium.contact.room")
  };
  private GemPanel pCriterion;
  private JComboBox criterion;



  public ContactExportDlg(GemDesktop desktop) {
    super(desktop, CONTACT_TITLE);
    this.desktop = desktop;
  }

  public ContactExportDlg(Dialog _parent) {
    super(_parent, CONTACT_TITLE);
  }

	@Override
  public GemPanel getCriterion() {
    pCriterion = new GemPanel();

    criterion = new JComboBox(criteria);
    pCriterion.add(criterion);

    return pCriterion;
  }

	@Override
  public String getRequest() {
    switch (criterion.getSelectedIndex()) {
      case 1:
        return "WHERE p.id > 0 AND p.ptype = " + Person.PERSON + " AND (p.organisation IS NOT NULL OR length(p.organisation) > 0)";
      case 2:
        return "WHERE p.id > 0 AND p.ptype = " + Person.PERSON + " AND p.id NOT IN(SELECT idper FROM " + MemberIO.TABLE + ")";
      case 3:
        return "WHERE p.id > 0 AND p.id IN(SELECT idper FROM " + RoomIO.TABLE + " WHERE nom !~* 'RATTRAP')";
      default:
        return "WHERE p.id > 0 AND p.ptype = " + Person.PERSON;
    }
  }

  @Override
  protected String getFileName() {
    return BundleUtil.getLabel("Export.contact.file");
  }
}
