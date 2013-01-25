/*
 * @(#)ContactExportDlg.java	2.6.a 02/10/12
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
package net.algem.edition;

import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JComboBox;
import net.algem.contact.Person;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemPanel;

/**
 * Export mailing contact.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 14/12/1999
 */
public class ContactExportDlg
        extends ExportDlg
{

  private GemPanel pCriterion;
  private JComboBox criterion;
  private static Object[] criteria = {
    MessageUtil.getMessage("export.criterium.contact.all")
  };

  public ContactExportDlg(Frame _parent, DataCache _cache) {
    super(_parent, CONTACT_TITLE, _cache);
  }

  public ContactExportDlg(Dialog _parent, DataCache _cache) {
    super(_parent, CONTACT_TITLE, _cache);
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
    String query = null;
     // L'export est volontairement permissif. On sélectionne un contact indépendemment de 
     // l'existence d'une adresse, d'un numéro de téléphone ou d'un email.
     // On ne tient pas compte non plus du critère "archive" de l'adresse, si elle existe.
    switch (criterion.getSelectedIndex()) {
      case 0: // tous les contacts
        //query = "where id in (SELECT p.id from personne p,adresse a where p.ptype=1 and arch='f' and p.id=a.idper)";
        query = "WHERE id IN (SELECT DISTINCT id FROM personne WHERE ptype = " + Person.PERSON + " AND id > 0)";
        break;
    }

    return query;
  }
}
