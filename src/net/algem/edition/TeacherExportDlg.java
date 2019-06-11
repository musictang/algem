/*
 * @(#)TeacherExportDlg.java	2.15.0 26/07/2017
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
import net.algem.planning.Schedule;
import net.algem.room.EstabChoice;
import net.algem.room.Establishment;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemPanel;

/**
 * Export mailling teachers.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 1.0a 14/12/1999
 */
public class TeacherExportDlg
        extends ExportDlg
{

  private static final String TEACHER_TITLE = BundleUtil.getLabel("Export.teacher.title");
  private static Object[] criteria = {
    MessageUtil.getMessage("export.criterium.teacher.all"),
    MessageUtil.getMessage("export.criterium.teacher.active"),
    MessageUtil.getMessage("export.criterium.teacher.year")
  };
  private GemPanel pCriterion;
  private JComboBox cbCriterion;
  private EstabChoice estab;
  private GemList<Establishment> allEstabList;

  public TeacherExportDlg(GemDesktop desktop) {
    super(desktop, TEACHER_TITLE);
  }

  public TeacherExportDlg(Dialog _parent) {
    super(_parent, TEACHER_TITLE);
  }

	@Override
  public GemPanel getCriterion() {
    pCriterion = new GemPanel();
    cbCriterion = new JComboBox(criteria);
    pCriterion.add(cbCriterion);
    allEstabList = desktop.getDataCache().getList(Model.Establishment);
    allEstabList.addElement(new Establishment(new Person(0, BundleUtil.getLabel("All.label"))));
    estab = new EstabChoice(allEstabList);
    pCriterion.add(estab);
    return pCriterion;
  }

	@Override
  public String getRequest() {
    String query = null;
    int e = estab.getKey();
    switch (cbCriterion.getSelectedIndex()) {
      case 0: // tous les profs
        query = "WHERE p.id > 0 AND p.id IN (SELECT idper FROM prof)";
        if (e > 0) {
          query += " AND p.id IN (SELECT DISTINCT e.idper FROM prof e, planning pl, salle s"
                  + " WHERE e.idper = pl.idper"
                  + " AND pl.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
                  + " AND pl.lieux = s.id AND s.etablissement = " + e + ")";
        }
        break;
      case 1: // tous les profs actifs
        query = "WHERE p.id > 0 AND p.id IN (SELECT idper FROM prof WHERE actif = 't')";
        if (e > 0) {
          query += " AND p.id IN (SELECT DISTINCT e.idper FROM prof e, planning pl, salle s"
                  + " WHERE e.idper = pl.idper"
                  + " AND pl.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
                  + " AND pl.lieux = s.id AND s.etablissement = " + e + ")";
        }
        break;
      case 2: // les intervenants de l'année
        query = "WHERE p.id > 0 AND p.id IN (SELECT DISTINCT e.idper FROM prof e, planning pl, salle s"
                + " WHERE e.idper = pl.idper"
                + " AND pl.lieux = s.id"
                + " AND pl.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
                + " AND pl.jour >= '" + desktop.getDataCache().getStartOfYear() + "'";
        if (e > 0) {
          query += " AND s.etablissement = " + e;
        }
        query += ")";
        break;
    }

    return query;
  }

  @Override
  protected String getFileName() {
    return BundleUtil.getLabel("Export.teacher.file");
  }

  @Override
  protected void close() {
    allEstabList.removeElement((Establishment) allEstabList.getItem(0));
    super.close();
  }
}
