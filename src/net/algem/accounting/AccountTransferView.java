/*
 * @(#)AccountTransferView.java	2.8.w 08/07/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import net.algem.config.ModeOfPaymentCtrl;
import net.algem.config.ParamChoice;
import net.algem.config.ParamTableIO;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Dialog tranfer view for accounting.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
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

  public AccountTransferView(DataCache dataCache) {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    GemPanel mainPanel = new GemPanel(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    gb = new GridBagHelper(mainPanel);

    payment = new JComboBox(ParamTableIO.getValues(ModeOfPaymentCtrl.TABLE,
            ModeOfPaymentCtrl.COLUMN_NAME,
            DataCache.getDataConnection()));
    schoolChoice = new ParamChoice(dataCache.getList(Model.School).getData());

    Date now = new Date();
    dateRange = new DateRangePanel(new DateFr(now), new DateFr(now));
    csv = new JCheckBox(MessageUtil.getMessage("csv.export.label"));
    csv.setBorder(null);

    gb.add(new JLabel(BundleUtil.getLabel("Period.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel(BundleUtil.getLabel("School.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel(BundleUtil.getLabel("Mode.of.payment.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(dateRange, 1, 0, 2, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 1, 2, 1, GridBagHelper.WEST);
    gb.add(payment, 1, 2, 2, 1, GridBagHelper.WEST);
    gb.add(csv, 1, 3, 2, 1, GridBagHelper.WEST);

    add(mainPanel, BorderLayout.CENTER);
  }

  public DateFr getDateStart() {
    return dateRange.getStartFr();
  }

  public DateFr getDateEnd() {
    return dateRange.getEndFr();
  }

  public int getSchool() {
    return schoolChoice.getKey();
  }

  public String getModeOfPayment() {
    return (String) payment.getSelectedItem();
  }

  public boolean withCSV() {
    return csv.isSelected();
  }
}
