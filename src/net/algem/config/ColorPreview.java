/*
 * @(#)ColorPreview.java	2.6.a 03/10/12
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
 * Display and modification panel for the planning colors.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ColorPreview extends GemPanel
{
  private ColorPrefs prefs;
  private static int w = 120;
  private JPanel ciPanel;
  private JPanel plPanel1;
  private JPanel ccPanel;
  private JPanel cciPanel;
  private JPanel rgPanel;
  private JPanel riPanel;
  private JPanel atPanel;
  private JPanel rtPanel;
  private JLabel ciLabel = new JLabel(BundleUtil.getLabel("Month.schedule.course.tab"));
  private JLabel ccLabel = new JLabel(BundleUtil.getLabel("Month.schedule.collective.course.tab"));
  private JLabel cciLabel = new JLabel(BundleUtil.getLabel("Course.instrument.collective.label"));
  private JLabel plLabel1 = new JLabel(BundleUtil.getLabel("Member.range.label"));
  private JLabel rgLabel = new JLabel(BundleUtil.getLabel("Group.rehearsal.label"));
  private JLabel riLabel = new JLabel(BundleUtil.getLabel("Member.rehearsal.label"));
  private JLabel atLabel = new JLabel(BundleUtil.getLabel("Workshop.label"));
  private JLabel rtLabel = new JLabel(BundleUtil.getLabel("Catching.up.label"));

  private Font f;
  private ColorPlanListener pColorListener;
  private ColorLabelListener lColorListener;

  public ColorPreview(ColorPrefs prefs)
  {
    this.prefs = prefs;
    setLayout(new GridLayout(1,4));
    f = new Font("Helvetica", Font.PLAIN, 10);
    pColorListener = new ColorPlanListener();
    lColorListener = new ColorLabelListener();
    initComponents();
  }

  /**
   * Retrieves the preferred colors.
   * @return a ColorPrefs instance
   */
  public ColorPrefs getPrefs()
  {
    return prefs;
  }

  private void initComponents(){
    int h = 100;
    Dimension dsize = new Dimension(w,h);

    JPanel p1 = new JPanel();
    p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
    ciPanel = initPanel(ciLabel, ColorPlan.COURSE_INDIVIDUAL, 60);
    plPanel1 = initPanel(plLabel1, ColorPlan.RANGE, 40);

    p1.add(ciPanel);
    p1.add(plPanel1);

    JPanel p2 = new JPanel();
    p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
    p2.setPreferredSize(dsize);
    ccPanel = initPanel(ccLabel, ColorPlan.COURSE_CO, 40);
    cciPanel = initPanel(cciLabel, ColorPlan.INSTRUMENT_CO, 30);
    rgPanel = initPanel(rgLabel, ColorPlan.GROUP_REHEARSAL, 30);
    p2.add(ccPanel);
    p2.add(cciPanel);
    p2.add(rgPanel);

    JPanel p3 = new JPanel();
    p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
    p3.setPreferredSize(dsize);
    riPanel = initPanel(riLabel, ColorPlan.MEMBER_REHEARSAL, 30);
    atPanel = initPanel(atLabel, ColorPlan.WORKSHOP, 70);
    p3.add(riPanel);
    p3.add(atPanel);

    JPanel p4 = new JPanel();
    p4.setLayout(new BoxLayout(p4,BoxLayout.Y_AXIS));
    rtPanel = initPanel(rtLabel, ColorPlan.CATCHING_UP, 60);
    p4.add(rtPanel);

    add(p1);
    add(p2);
    add(p3);
    add(p4);

  }

  /**
   * Retrieves background and foreground colors.
   * @return une Map
   */
  public Map<ColorPlan, Color> getColors() {

    Map<ColorPlan, Color> colors = new EnumMap<ColorPlan, Color>(ColorPlan.class);

    colors.put(ColorPlan.COURSE_INDIVIDUAL, ciPanel.getBackground());
    colors.put(ColorPlan.COURSE_CO, ccPanel.getBackground());
    colors.put(ColorPlan.INSTRUMENT_CO, cciPanel.getBackground());
    colors.put(ColorPlan.CATCHING_UP, rtPanel.getBackground());
    colors.put(ColorPlan.RANGE, plPanel1.getBackground());
    colors.put(ColorPlan.WORKSHOP, atPanel.getBackground());
    colors.put(ColorPlan.GROUP_REHEARSAL, rgPanel.getBackground());
    colors.put(ColorPlan.MEMBER_REHEARSAL, riPanel.getBackground());

    colors.put(ColorPlan.COURSE_INDIVIDUAL_LABEL, ciLabel.getForeground());
    colors.put(ColorPlan.COURSE_CO_LABEL, ccLabel.getForeground());
    colors.put(ColorPlan.INSTRUMENT_CO_LABEL, cciLabel.getForeground());
    colors.put(ColorPlan.CATCHING_UP_LABEL, rtLabel.getForeground());
    colors.put(ColorPlan.WORKSHOP_LABEL, atLabel.getForeground());
    colors.put(ColorPlan.GROUP_LABEL, rgLabel.getForeground());
    colors.put(ColorPlan.MEMBER_LABEL, riLabel.getForeground());

    return colors;
  }

  /*@Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setFont(f);
    texte = "";
    int x = 0;
    int y = 0;
    int h = 100;
    Color c = g.getColor();
		g.setColor(prefs.getColor(ColorPlan.COURSE_INDIVIDUAL));
		g.fillRect(x,y,w,h);
		g.setColor(prefs.getColor(ColorPlan.COURS_LABEL));
    f = g.getFontMetrics();
    texte = "Cours individuel";
   
    g.drawString(texte, getXpos(x), 25);
    g.setColor(prefs.getColor(ColorPlan.COURSE_CO));
    x += w;
		g.fillRect(x,y,w,h);
    g.setColor(prefs.getColor(ColorPlan.COURS_LABEL));
    texte = "Cours collectif";
    g.drawString(texte, getXpos(x), 25);
		g.setColor(c);

  }

  private int getXpos(int offset) {
    return offset + ((w - f.stringWidth(texte)) / 2);
  }*/

  /**
   * Gets the differents color areas and labels.
   * @param jlb label
   * @param pc a color enumeration
   * @param h height
   * @return a panel
   */
  private JPanel initPanel(JLabel jlb, ColorPlan pc, int h) {
    Color bg = null;
    Color fg = null;
    
    switch(pc) {
      case COURSE_INDIVIDUAL :
        bg = prefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
        fg = prefs.getColor(ColorPlan.COURSE_INDIVIDUAL_LABEL);
        break;
      case COURSE_CO :
        bg = prefs.getColor(ColorPlan.COURSE_CO);
        fg = prefs.getColor(ColorPlan.COURSE_CO_LABEL);
        break;
      case INSTRUMENT_CO :
        bg = prefs.getColor(ColorPlan.INSTRUMENT_CO);
        fg = prefs.getColor(ColorPlan.INSTRUMENT_CO_LABEL);
        break;  
      case CATCHING_UP :
        bg = prefs.getColor(ColorPlan.CATCHING_UP);
        fg = prefs.getColor(ColorPlan.CATCHING_UP_LABEL);
        break;
      case WORKSHOP :
        bg = prefs.getColor(ColorPlan.WORKSHOP);
        fg = prefs.getColor(ColorPlan.WORKSHOP_LABEL);
        break;
      case ACTION :
        bg = prefs.getColor(ColorPlan.ACTION);
        fg = prefs.getColor(ColorPlan.ACTION_LABEL);
        break;
      case GROUP_REHEARSAL :
        bg = prefs.getColor(ColorPlan.GROUP_REHEARSAL);
        fg = prefs.getColor(ColorPlan.GROUP_LABEL);
        break;
      case MEMBER_REHEARSAL :
        bg = prefs.getColor(ColorPlan.MEMBER_REHEARSAL);
        fg = prefs.getColor(ColorPlan.MEMBER_LABEL);
        break;
      case RANGE :
        bg = prefs.getColor(ColorPlan.RANGE);
        fg = prefs.getColor(ColorPlan.LABEL);
        break;
      default:
        bg = Color.GRAY;
        fg = Color.BLACK;
        break;
    }

    JPanel jp = new JPanel();
    jp.setPreferredSize(new Dimension(w,h));
    jp.setBackground(bg);
    setLabel(jlb, fg);
    jp.add(jlb);
    jp.addMouseListener(pColorListener);
    return jp;
  }

  /**
   * Sets a label.
   * @param label
   * @param c color
   */
  private void setLabel(JLabel label, Color c) {
    label.setFont(f);
    label.setForeground(c);
    label.addMouseListener(lColorListener);
  }

  
}

