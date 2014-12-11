/*
 * @(#)HourEmployeeView.java  2.9.1 08/12/14
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
package net.algem.edition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import net.algem.config.*;
import net.algem.contact.EmployeeType;
import net.algem.contact.EmployeeTypePanel;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Entry panel for hours of teacher activity exportation.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.8.v 10/06/14
 */
public class HourEmployeeView
        extends JPanel
{

  private DateRangePanel dateRange;
  private JCheckBox detail;
  private ParamChoice schoolChoice;
  private EmployeeTypePanel employeeType;

  private String[] sortingInfos = {
    MessageUtil.getMessage("employee.sorting.by.establishment.tip"),
    MessageUtil.getMessage("employee.sorting.by.date.tip"),
    MessageUtil.getMessage("employee.sorting.by.member.tip")
  };
  
  private final ButtonGroup btSortingGroup;

  public HourEmployeeView(final HourEmployeeDlg dlg, GemList<Param> schools, GemList<GemParam> employeeTypes) {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    GemPanel body = new GemPanel(new GridBagLayout());
    body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagHelper gb = new GridBagHelper(body);
    gb.insets = GridBagHelper.SMALL_INSETS;
    employeeType = new EmployeeTypePanel(employeeTypes);
    employeeType.setType(EmployeeType.TEACHER.ordinal());
    employeeType.addActionListener(dateRange);
    employeeType.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        dlg.setPath(getType(), getSorting());
      }
    });

    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.set(Calendar.DAY_OF_MONTH, 1);
    DateFr start = new DateFr(cal.getTime());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    DateFr end = new DateFr(cal.getTime());
    dateRange = new DateRangePanel(start, end);
    detail = new JCheckBox();
    detail.setBorder(null);

    schoolChoice = new ParamChoice(schools.getData());
    int defaultSchool = Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey()));
    schoolChoice.setKey(defaultSchool);
    //TODO ajouter option 'Autoriser les plannings vides' : cf. ateliers extérieurs)
    
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Period.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("School.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Detail.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(employeeType, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(detail, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(Box.createRigidArea(new Dimension(100,20)), 1, 4, 1, 1, GridBagHelper.WEST);

    GemPanel sortingPanel = new GemPanel();
    sortingPanel.setLayout(new BoxLayout(sortingPanel, BoxLayout.Y_AXIS));
    sortingPanel.setBorder(BorderFactory.createTitledBorder("Tri par défaut"));

    btSortingGroup = new ButtonGroup();
    final JRadioButton r1 = new JRadioButton("Tri par établissement (fichier .txt au format texte)");
    r1.setActionCommand(HourEmployeeDlg.SORTING_CMD[0]);
    r1.setSelected(true);
    final JRadioButton r2 = new JRadioButton("Tri simple par date (fichier .csv au format tableur)");
    r2.setActionCommand(HourEmployeeDlg.SORTING_CMD[1]);
    final JRadioButton r3 = new JRadioButton("Tri par élève/cours (fichier .csv au format tableur)");
    r3.setActionCommand(HourEmployeeDlg.SORTING_CMD[2]);
    
    final JTextArea status = new JTextArea(5, 30);
    status.setEditable(false);
    status.setLineWrap(true);
    status.setWrapStyleWord(true);
    status.setBackground(body.getBackground());
    status.setText(sortingInfos[0]);

    ActionListener radioBtListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == r1) {
          status.setText(sortingInfos[0]);
          dlg.setPath(getType(), HourEmployeeDlg.SORTING_CMD[0]);
        } else if (e.getSource() == r2) {
          status.setText(sortingInfos[1]);
          dlg.setPath(getType(), HourEmployeeDlg.SORTING_CMD[1]);
        } else if (e.getSource() == r3) {
          status.setText(sortingInfos[2]);
          dlg.setPath(getType(), HourEmployeeDlg.SORTING_CMD[2]);
        } 
      }
    };
    r1.addActionListener(radioBtListener);
    r2.addActionListener(radioBtListener);
    r3.addActionListener(radioBtListener);
    
    btSortingGroup.add(r1);
    btSortingGroup.add(r2);
    btSortingGroup.add(r3);

    sortingPanel.add(r1);
    sortingPanel.add(r2);
    sortingPanel.add(r3);

    gb.add(sortingPanel, 0, 5, 2, 1, GridBagHelper.WEST);
    gb.add(status, 0, 6, 2, 1, GridBagHelper.WEST);
    add(body, BorderLayout.CENTER);

  }

  public DateFr getDateStart() {
    return dateRange.getStartFr();
  }

  public DateFr getDateEnd() {
    return dateRange.getEndFr();
  }

  public Param getSchool() {
    return (Param) schoolChoice.getSelectedItem();
  }

  public boolean withDetail() {
    return detail.isSelected();
  }

  public int getType() {
    return employeeType.getType();
  }
  
  String getSorting() {
    return btSortingGroup.getSelection().getActionCommand();
  }

}
