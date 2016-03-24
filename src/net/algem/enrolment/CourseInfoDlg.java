/*
 * @(#)CourseInfoDlg.java	2.9.6 24/03/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import net.algem.config.GemParam;
import net.algem.config.GemParamChoice;
import net.algem.course.CourseCodeType;
import net.algem.course.CourseModuleInfo;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.6
 * @since 2.8.a 26/03/2013
 */
public class CourseInfoDlg
  extends AbstractEditDlg
{
  private GemParamChoice code;
  private HourField hf;
  private DateRangePanel datePanel;

  public CourseInfoDlg(GemDesktop desktop, boolean modal, String title, DateFr date) {
    super(desktop.getFrame(), title, modal);
    code = new GemParamChoice(desktop.getDataCache().getList(Model.CourseCode));
    code.setSelectedIndex(0);
    if (date != null) {
      datePanel  = new DateRangePanel(date);
    } else {
      datePanel  = new DateRangePanel(new DateFr(new Date()));
    }
    hf = new HourField();
    code.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (code.getKey() == CourseCodeType.ATP.getId() || code.getKey() == CourseCodeType.STG.getId()) {
          hf.setEnabled(false);
        } else {
          hf.setEnabled(true);
        }
      }

    });
    setLayout(new BorderLayout());


    GemPanel infoPanel = new GemPanel();
    infoPanel.setLayout(new GridBagLayout());
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    GridBagHelper gb = new GridBagHelper(infoPanel);
    gb.insets = GridBagHelper.SMALL_INSETS;

    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    gb.add(new JLabel(BundleUtil.getLabel("Date.label")), 0,0,1,1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0,1,1,1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Duration.label")), 0,2,1,1, GridBagHelper.WEST);

    gb.add(datePanel, 1,0,1,1, GridBagHelper.WEST);
    gb.add(code, 1,1,1,1, GridBagHelper.WEST);
    gb.add(hf, 1,2,1,1, GridBagHelper.WEST);

    btOk = new GemButton(GemCommand.OK_CMD);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btOk.addActionListener(this);
    btCancel.addActionListener(this);
    buttons.add(btOk);
    buttons.add(btCancel);
    add(infoPanel, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.XXS_SIZE);
    pack();
    setLocationRelativeTo(desktop.getFrame());
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btOk) {
      validation = check();
    } else {
      validation = false;
    }
    close();
  }

  CourseModuleInfo getCourseInfo() throws SQLException {
    CourseModuleInfo info = new CourseModuleInfo();
    info.setCode((GemParam) DataCache.findId(code.getKey(), Model.CourseCode));
    info.setTimeLength(hf.getHour().toMinutes());
    return info;
  }

  void setDate(DateFr d) {
    datePanel.setDate(d);
  }

  DateFr getDate() {
    return datePanel.get();
  }

  public boolean check() {
    if (code.getKey() <= 0) {
      return false;
    }
    if (code.getKey() != CourseCodeType.ATP.getId() && code.getKey() != CourseCodeType.STG.getId()) {
      return hf.getHour().toMinutes() > 0;
    }
    return true;
  }

}
