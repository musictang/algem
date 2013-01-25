/*
 * @(#)AccountTransferView.java	2.7.a 05/12/12
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
package net.algem.accounting;

import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import net.algem.config.ModeOfPaymentCtrl;
import net.algem.config.ParamChoice;
import net.algem.config.ParamTableIO;
import net.algem.config.SchoolCtrl;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Dialog tranfer view for accounting.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class AccountTransferView
        extends GemPanel
{

  protected JComboBox payment;
  protected ParamChoice schoolChoice;
  protected DateRangePanel dateRange;
  protected JCheckBox csv;
  protected GridBagHelper gb;

  public AccountTransferView() {
    
  }
  
  public AccountTransferView(DataConnection dc) {
    setLayout(new java.awt.GridBagLayout());
    gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    payment = new JComboBox(ParamTableIO.getValues(ModeOfPaymentCtrl.TABLE, ModeOfPaymentCtrl.COLUMN_NAME, dc));
    schoolChoice = new ParamChoice(ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dc));

    Date now = new Date();
    dateRange = new DateRangePanel(new DateFr(now), new DateFr(now));
    csv = new JCheckBox(MessageUtil.getMessage("csv.export.label"));

    gb.add(new JLabel("Période"), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel("Ecole"), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel("Reglement"), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(dateRange, 1, 0, 2, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 1, 2, 1, GridBagHelper.WEST);
    gb.add(payment, 1, 2, 2, 1, GridBagHelper.WEST);
    gb.add(csv, 1, 3, 2, 1, GridBagHelper.WEST);
  }

  public DateFr getDateStart() {
    return dateRange.getStartFr();
  }

  public DateFr getDateEnd() {
    return dateRange.getEndFr();
  }

  public String getSchool() {
    return schoolChoice.getValue();
  }

  public String getModeOfPayment() {
    return (String) payment.getSelectedItem();
  }

  public boolean withCSV() {
    return csv.isSelected();
  }

}
