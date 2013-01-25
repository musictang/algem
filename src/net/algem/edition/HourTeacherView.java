/*
 * @(#)HourTeacherView.java	2.6.a 02/08/2012
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

import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.DataConnection;
import net.algem.config.SchoolCtrl;
import net.algem.config.Param;
import net.algem.config.ParamChoice;
import net.algem.config.ParamTableIO;
import net.algem.util.ui.GridBagHelper;

/**
 * Entry panel for hours of teacher activity exportation.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class HourTeacherView
        extends JPanel
{

  private DateFrField dateStart;
  private DateFrField dateEnd;
  private JCheckBox detail;
  private ParamChoice schoolChoice;

  public HourTeacherView(DataConnection dc) {
    setLayout(new java.awt.GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());
    detail = new JCheckBox();
    //ecole = new JComboBox(ecoles);
    schoolChoice = new ParamChoice(ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dc));

    JPanel dates = new JPanel();
    dates.add(dateStart);
    dates.add(new JLabel("Au"));
    dates.add(dateEnd);

    gb.add(new JLabel("Période"), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel("Ecole"), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel("Détail"), 0, 2, 1, 1, GridBagHelper.EAST);

    gb.add(dates, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(detail, 1, 2, 1, 1, GridBagHelper.WEST);

    //setSize(500,400);
  }

  public DateFr getDateStart() {
    return dateStart.getDateFr();
  }

  public DateFr getDateEnd() {
    return dateEnd.getDateFr();
  }

  public Param getSchool() {
    return (Param) schoolChoice.getSelectedItem();
    //return ecoleChoix.getKey();
  }

  public boolean withDetail() {
    return detail.isSelected();
  }
}
