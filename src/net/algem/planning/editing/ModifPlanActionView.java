/*
 * @(#)ModifPlanActionView.java 2.12.0 13/03/17
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

package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import net.algem.config.*;
import net.algem.contact.Note;
import net.algem.contact.Person;
import net.algem.planning.Action;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * View planification parameters.
 * Modification of status, level, age range and number of places.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.5.a 22/06/12
 */
class ModifPlanActionView
  extends GemPanel
{
  private GemParamChoice status;
  private GemParamChoice level;
  private GemParamChoice ageRange;
  private GemNumericField places;
  private GemPanel bgColorPanel;
  private JTextArea noteArea;
  private int initialColor;
  private Note memo;
  private Color defaultBgColor;

  public ModifPlanActionView(DataCache dataCache, Action a, Color defaultColor) throws SQLException {
    status = new GemParamChoice(new GemChoiceModel<Status>(dataCache.getList(Model.Status))); 
    status.setKey(a.getStatus().getId());
    level = new GemParamChoice(new GemChoiceModel<Level>(dataCache.getList(Model.Level)));
    
    level.setKey(a.getLevel().getId());
    ageRange = new GemParamChoice(new GemChoiceModel<AgeRange>(dataCache.getList(Model.AgeRange)));
    ageRange.setKey(a.getAgeRange().getId());
    
    places = new GemNumericField(2);
    places.setText(String.valueOf(a.getPlaces()));
    
    Dimension prefSize = new Dimension(ageRange.getPreferredSize().width, ageRange.getPreferredSize().height);
    status.setPreferredSize(prefSize);
    level.setPreferredSize(prefSize);
    ageRange.setPreferredSize(prefSize);
    
    this.defaultBgColor = defaultColor;
    
    GemPanel colorPanel = new GemPanel(new BorderLayout());
        
    bgColorPanel = new GemPanel();
    bgColorPanel.addMouseListener(new ColorPlanListener());
    bgColorPanel.setPreferredSize(places.getPreferredSize());
    bgColorPanel.setBorder(places.getBorder());

    if (a.getColor() != 0) {
      initialColor = a.getColor();
      setBgColor(new Color(a.getColor()));
    } else {
      initialColor = defaultBgColor.getRGB();
      setBgColor(defaultBgColor);
    }
    
    colorPanel.add(bgColorPanel, BorderLayout.WEST);
    JCheckBox bgRestore = new JCheckBox(BundleUtil.getLabel("Color.default.label"));
    bgRestore.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        JCheckBox src = (JCheckBox) e.getSource();
        setBgColor(src.isSelected() ? defaultBgColor : new Color(initialColor));
      }
    });
    colorPanel.add(bgRestore, BorderLayout.EAST);
    
    noteArea = new JTextArea(4,16);
    noteArea.setLineWrap(true);
    noteArea.setWrapStyleWord(true);
    noteArea.setBorder(places.getBorder());
    if (a.getNote() != null) {
      memo = a.getNote();
      noteArea.setText(a.getNote().getText());
    }

    GemPanel p = new GemPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = new Insets(4, 2, 4, 2);

        gb.add(new GemLabel(BundleUtil.getLabel("Status.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(status, 1, 0, GridBagHelper.REMAINDER, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Level.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(level, 1, 1, GridBagHelper.REMAINDER, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Menu.age.range.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(ageRange, 1, 2, GridBagHelper.REMAINDER, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Place.number.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(places, 1, 3, GridBagHelper.REMAINDER, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Color.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    colorPanel.setPreferredSize(new Dimension(colorPanel.getPreferredSize().width, places.getPreferredSize().height));
    gb.add(colorPanel, 1, 4, GridBagHelper.REMAINDER, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Note.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(noteArea, 1, 5, GridBagHelper.REMAINDER, 1, GridBagHelper.BOTH, GridBagHelper.WEST);

    gb.add(new JLabel(""), 1, 6, 1, 1, GridBagHelper.NONE, GridBagConstraints.BASELINE_TRAILING, 1,0);
    gb.add(new JLabel(""), 2, 6, 1, 1, GridBagHelper.NONE, GridBagConstraints.BASELINE_TRAILING);

    setLayout(new BorderLayout());
    add(p, BorderLayout.CENTER);
  }
  
  private void setBgColor(Color color) {
    bgColorPanel.setBackground(color);
  }

  GemParam getLevel() {
    return (GemParam) level.getSelectedItem();
  }

  GemParam getStatus() {
    return (GemParam) status.getSelectedItem();
  }

  AgeRange getRange() {
    return (AgeRange) ageRange.getSelectedItem();
  }

  short getPlaces() {
    try {
      return Short.parseShort(places.getText());
    } catch (NumberFormatException nfe) {
      return 0;
    }
  }
  
  int getColor() {
    int c = bgColorPanel.getBackground().getRGB();
    return c != initialColor ? c : initialColor;
  }
  
  Note getNote() {
    String n = noteArea.getText();
    if (n.isEmpty()) {
      if (memo == null) {
        return null;
      } else {
        memo.setIdPer(0);
        return memo; // delete
      }
    }
    if (memo == null) {
      return new Note(0, n, Person.ACTION); // create
    } else {
      return new Note(memo.getId(), memo.getIdPer(), n, Person.ACTION); // update
    }
  }
  
  boolean isEntryValid() {
    short p = getPlaces();
    return getLevel().getId() >= 0 && getStatus().getId() >= 0 && p >= 0 && p < 500;
  }

}
