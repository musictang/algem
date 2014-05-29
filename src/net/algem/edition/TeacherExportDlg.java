/*
 * @(#)TeacherExportDlg.java	2.6.a 02/08/2012
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
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemPanel;

/**
 * Export mailling teachers.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 14/12/1999
 */
public class TeacherExportDlg
        extends ExportDlg
{

  private GemPanel pCriterion;
  private JComboBox cbCriterion;
  private static Object[] criteria = {
    MessageUtil.getMessage("export.criterium.teacher.all"),
    MessageUtil.getMessage("export.criterium.teacher.active"),
    MessageUtil.getMessage("export.criterium.teacher.year")
  };

  public TeacherExportDlg(Frame _parent, DataCache _cache) {
    super(_parent, TEACHER_TITLE, _cache);
  }

  public TeacherExportDlg(Dialog _parent, DataCache _cache) {
    super(_parent, TEACHER_TITLE, _cache);
  }

	@Override
  public GemPanel getCriterion() {
    pCriterion = new GemPanel();

    cbCriterion = new JComboBox(criteria);
    pCriterion.add(cbCriterion);

    return pCriterion;
  }

	@Override
  public String getRequest() {
    String query = null;

    switch (cbCriterion.getSelectedIndex()) {
      case 0: // tous les profs
        //query = "where id in (SELECT p.id from personne p,adresse a,prof e where e.idper=p.id and p.id=a.idper and a.archive='f')";
        query = "where id in (SELECT idper FROM prof)";// tous les profs
        break;
      case 1: // tous les profs actifs
        //query = "where id in (SELECT p.id from personne p,adresse a,prof e where e.idper=p.id and p.id=a.idper and e.actif='t')";
        query = "where id in (SELECT idper FROM prof WHERE actif = 't')";// tous les profs actifs
        break;
      case 2: // les intervenants de l'année
        //query = "where id in (SELECT distinct p.id from personne p,adresse a,prof e, cours c where e.idper=p.id and p.id=a.idper and c.prof=p.id and c.start >='" + dc.getStartOfYear() + "')";
        query = "where id in (SELECT DISTINCT e.idper FROM prof e, planning p WHERE e.idper=p.idper AND (p.ptype = " + Schedule.COURSE + " OR p.ptype = " + Schedule.WORKSHOP + ") AND p.jour >='" + dataCache.getStartOfYear() + "')";
        break;
    }

    return query;
  }
}
