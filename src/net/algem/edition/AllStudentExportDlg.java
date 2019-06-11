/*
 * @(#)AllStudentExportDlg.java	2.15.8 22/03/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import javax.swing.JLabel;
import net.algem.contact.Person;
import net.algem.room.EstabChoice;
import net.algem.room.Establishment;
import net.algem.util.BundleUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.9.2.1 17/02/15
 */
public class AllStudentExportDlg
extends StudentExportDlg
{

  private GemList<Establishment> allEstabList;
  private EstabChoice estab;

  public AllStudentExportDlg(GemDesktop desktop) {
    super(desktop);
  }

  @Override
  protected void setPanel() {
    allEstabList = desktop.getDataCache().getList(Model.Establishment);
    allEstabList.addElement(new Establishment(new Person(0, BundleUtil.getLabel("All.label"))));
    estab = new EstabChoice(allEstabList);
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Establishment.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(estab, 1, 2, 1, 1, GridBagHelper.WEST);

    nextRow = 3;
  }

  @Override
  public String getRequest() {
    // boxing required : null value may be returned
    Boolean pro = rdPro.isSelected() ? Boolean.valueOf(true) : (rdLeisure.isSelected() ? Boolean.valueOf(false) : null);
    return service.getStudent(dateRange.getStart(), dateRange.getEnd(), pro, estab.getKey());
  }

  @Override
  protected void close() {
    allEstabList.removeElement((Establishment) allEstabList.getItem(0));
    super.close();
  }

}
