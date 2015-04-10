/*
 * @(#)ColorPreview.java	2.9.4.0 24/03/2015
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemPanel;

/**
 * Display and modification panel of the planning colors.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 */
public class ColorPreview extends GemPanel {

  private ColorPrefs prefs;
  private static final int w = 120;

  private final JLabel courseLabel = new JLabel(BundleUtil.getLabel("Month.schedule.course.tab"));
  private final JLabel courseCoLabel = new JLabel(BundleUtil.getLabel("Month.schedule.collective.course.tab"));
  private final JLabel courseCoInstrLabel = new JLabel(BundleUtil.getLabel("Course.instrument.collective.label"));
  private final JLabel memberRangeLabel = new JLabel(BundleUtil.getLabel("Member.range.label"));
  private final JLabel groupRehearsalLabel = new JLabel(BundleUtil.getLabel("Group.rehearsal.label"));
  private final JLabel memberRehearsalLabel = new JLabel(BundleUtil.getLabel("Member.rehearsal.label"));
  private final JLabel workshopLabel = new JLabel(BundleUtil.getLabel("Workshop.label"));
  private final JLabel trainingLabel = new JLabel(BundleUtil.getLabel("Training.course.label"));
  private final JLabel catchUpLabel = new JLabel(BundleUtil.getLabel("Catching.up.label"));
  private final JLabel studioLabel = new JLabel(BundleUtil.getLabel("Studio.label"));
  private final JLabel administrativeLabel = new JLabel(BundleUtil.getLabel("Administrative.label"));

  private JPanel coursePanel;
  private JPanel memberRangePanel;
  private JPanel coCoursePanel;
  private JPanel coCourseInstrPanel;
  private JPanel groupRehearsalPanel;
  private JPanel memberRehearsalPanel;
  private JPanel workshopPanel;
  private JPanel catchUpPanel;
  private JPanel trainingPanel;
  private JPanel studioPanel;
  private JPanel administrativePanel;

  private Font f;
  private ColorPlanListener pColorListener;
  private ColorLabelListener lColorListener;

  public ColorPreview(ColorPrefs prefs) {
    this.prefs = prefs;
    setLayout(new GridLayout(1, 4));
    f = new Font("Helvetica", Font.PLAIN, 10);
    pColorListener = new ColorPlanListener();
    lColorListener = new ColorLabelListener();
    initComponents();
  }

  /**
   * Gets the preferred colors.
   *
   * @return a ColorPrefs instance
   */
  public ColorPrefs getPrefs() {
    return prefs;
  }

  private void initComponents() {
    int h = 100;
    Dimension dsize = new Dimension(w, h);

    JPanel p1 = new JPanel();
    p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
    coursePanel = initPanel(courseLabel, ColorPlan.COURSE_INDIVIDUAL, 60);
    memberRangePanel = initPanel(memberRangeLabel, ColorPlan.RANGE, 40);

    p1.add(coursePanel);
    p1.add(memberRangePanel);

    JPanel p2 = new JPanel();
    p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
    p2.setPreferredSize(dsize);
    coCoursePanel = initPanel(courseCoLabel, ColorPlan.COURSE_CO, 40);
    coCourseInstrPanel = initPanel(courseCoInstrLabel, ColorPlan.INSTRUMENT_CO, 30);
    groupRehearsalPanel = initPanel(groupRehearsalLabel, ColorPlan.GROUP_REHEARSAL, 30);
    p2.add(coCoursePanel);
    p2.add(coCourseInstrPanel);
    p2.add(groupRehearsalPanel);

    JPanel p3 = new JPanel();
    p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
    p3.setPreferredSize(dsize);
    memberRehearsalPanel = initPanel(memberRehearsalLabel, ColorPlan.MEMBER_REHEARSAL, 20);
    workshopPanel = initPanel(workshopLabel, ColorPlan.WORKSHOP, 40);
    trainingPanel = initPanel(trainingLabel, ColorPlan.TRAINING, 40);
    p3.add(memberRehearsalPanel);
    p3.add(workshopPanel);
    p3.add(trainingPanel);

    JPanel p4 = new JPanel();
    p4.setLayout(new BoxLayout(p4, BoxLayout.Y_AXIS));
    studioPanel = initPanel(studioLabel, ColorPlan.STUDIO, 30);
    catchUpPanel = initPanel(catchUpLabel, ColorPlan.CATCHING_UP, 30);
    administrativePanel = initPanel(administrativeLabel, ColorPlan.ADMINISTRATIVE, 40);
    p4.add(studioPanel);
    p4.add(catchUpPanel);
    p4.add(administrativePanel);

    add(p1);
    add(p2);
    add(p3);
    add(p4);

  }

  /**
   * Retrieves background and foreground colors.
   *
   * @return une Map
   */
  public Map<ColorPlan, Color> getColors() {

    Map<ColorPlan, Color> colors = new EnumMap<ColorPlan, Color>(ColorPlan.class);

    colors.put(ColorPlan.COURSE_INDIVIDUAL, coursePanel.getBackground());
    colors.put(ColorPlan.COURSE_CO, coCoursePanel.getBackground());
    colors.put(ColorPlan.INSTRUMENT_CO, coCourseInstrPanel.getBackground());
    colors.put(ColorPlan.CATCHING_UP, catchUpPanel.getBackground());
    colors.put(ColorPlan.RANGE, memberRangePanel.getBackground());
    colors.put(ColorPlan.WORKSHOP, workshopPanel.getBackground());
    colors.put(ColorPlan.TRAINING, trainingPanel.getBackground());
    colors.put(ColorPlan.GROUP_REHEARSAL, groupRehearsalPanel.getBackground());
    colors.put(ColorPlan.MEMBER_REHEARSAL, memberRehearsalPanel.getBackground());
    colors.put(ColorPlan.STUDIO, studioPanel.getBackground());
    colors.put(ColorPlan.ADMINISTRATIVE, administrativePanel.getBackground());

    colors.put(ColorPlan.COURSE_INDIVIDUAL_LABEL, courseLabel.getForeground());
    colors.put(ColorPlan.COURSE_CO_LABEL, courseCoLabel.getForeground());
    colors.put(ColorPlan.INSTRUMENT_CO_LABEL, courseCoInstrLabel.getForeground());
    colors.put(ColorPlan.CATCHING_UP_LABEL, catchUpLabel.getForeground());
    colors.put(ColorPlan.WORKSHOP_LABEL, workshopLabel.getForeground());
    colors.put(ColorPlan.TRAINING_LABEL, trainingLabel.getForeground());
    colors.put(ColorPlan.GROUP_LABEL, groupRehearsalLabel.getForeground());
    colors.put(ColorPlan.MEMBER_LABEL, memberRehearsalLabel.getForeground());
    colors.put(ColorPlan.STUDIO_LABEL, studioLabel.getForeground());
    colors.put(ColorPlan.ADMINISTRATIVE_LABEL, administrativeLabel.getForeground());

    return colors;
  }

  /**
   * Gets the differents color areas and labels.
   *
   * @param jlb label
   * @param pc a color enumeration
   * @param h height
   * @return a panel
   */
  private JPanel initPanel(JLabel jlb, ColorPlan pc, int h) {
    Color bg = null;
    Color fg = null;

    switch (pc) {
      case COURSE_INDIVIDUAL:
        bg = prefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
        fg = prefs.getColor(ColorPlan.COURSE_INDIVIDUAL_LABEL);
        break;
      case COURSE_CO:
        bg = prefs.getColor(ColorPlan.COURSE_CO);
        fg = prefs.getColor(ColorPlan.COURSE_CO_LABEL);
        break;
      case INSTRUMENT_CO:
        bg = prefs.getColor(ColorPlan.INSTRUMENT_CO);
        fg = prefs.getColor(ColorPlan.INSTRUMENT_CO_LABEL);
        break;
      case CATCHING_UP:
        bg = prefs.getColor(ColorPlan.CATCHING_UP);
        fg = prefs.getColor(ColorPlan.CATCHING_UP_LABEL);
        break;
      case WORKSHOP:
        bg = prefs.getColor(ColorPlan.WORKSHOP);
        fg = prefs.getColor(ColorPlan.WORKSHOP_LABEL);
        break;
      case TRAINING:
        bg = prefs.getColor(ColorPlan.TRAINING);
        fg = prefs.getColor(ColorPlan.TRAINING_LABEL);
        break;
      case ACTION:
        bg = prefs.getColor(ColorPlan.ACTION);
        fg = prefs.getColor(ColorPlan.ACTION_LABEL);
        break;
      case GROUP_REHEARSAL:
        bg = prefs.getColor(ColorPlan.GROUP_REHEARSAL);
        fg = prefs.getColor(ColorPlan.GROUP_LABEL);
        break;
      case MEMBER_REHEARSAL:
        bg = prefs.getColor(ColorPlan.MEMBER_REHEARSAL);
        fg = prefs.getColor(ColorPlan.MEMBER_LABEL);
        break;
      case RANGE:
        bg = prefs.getColor(ColorPlan.RANGE);
        fg = prefs.getColor(ColorPlan.LABEL);
        break;
      case STUDIO:
        bg = prefs.getColor(ColorPlan.STUDIO);
        fg = prefs.getColor(ColorPlan.STUDIO_LABEL);
        break;
      case ADMINISTRATIVE:
        bg = prefs.getColor(ColorPlan.ADMINISTRATIVE);
        fg = prefs.getColor(ColorPlan.ADMINISTRATIVE_LABEL);
        break;
      default:
        bg = Color.GRAY;
        fg = Color.BLACK;
        break;
    }

    JPanel jp = new JPanel();
    jp.setPreferredSize(new Dimension(w, h));
    jp.setBackground(bg);
    setLabel(jlb, fg);
    jp.add(jlb);
    jp.addMouseListener(pColorListener);
    return jp;
  }

  /**
   * Sets a label.
   *
   * @param label
   * @param c color
   */
  private void setLabel(JLabel label, Color c) {
    label.setFont(f);
    label.setForeground(c);
    label.addMouseListener(lColorListener);
  }

}
