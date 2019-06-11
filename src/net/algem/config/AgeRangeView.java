/*
 * @(#)AgeRangeView.java 2.6.a 25/09/12
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
 */
package net.algem.config;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.4
 */
public class AgeRangeView extends GemBorderPanel
        implements FocusListener {
  
  private GemField label;
  private JFormattedTextField agemin;
  private JFormattedTextField agemax;
  private JFormattedTextField source;
  private GemField code;
  private NumberFormat format;
  private AgeRange range;
  protected ActionListener actionListener;
  
  public AgeRangeView() {
    label = new GemField(15);
    code = new GemField(1);
    code.setColumns(1);
    format = NumberFormat.getNumberInstance(Locale.getDefault());
    format.setMinimumFractionDigits(0);
    format.setMaximumFractionDigits(0);
    
    NumberFormatter nf = new NumberFormatter(format);
    nf.setValueClass(Integer.class);
    
    Dimension dim = new Dimension(50, 20);
    
    agemin = new JFormattedTextField(nf);
    agemin.setPreferredSize(dim);
    agemax = new JFormattedTextField(nf);
    agemax.setPreferredSize(dim);
    
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = new Insets(2, 5, 2, 5);
    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Age.range.min.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Age.range.max.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Code.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(label, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(agemin, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(agemax, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(code, 1, 3, 3, 1, GridBagHelper.WEST);
    label.setEditable(false);
    
    agemin.addFocusListener(this);
    agemax.addFocusListener(this);
    
  }
  
  public void setRange(AgeRange range) {
    this.range = range;
    label.setText(range.getLabel());
    agemin.setValue(range.getAgemin());
    agemax.setValue(range.getAgemax());
    code.setText(range.getCode());
  }
  
  public AgeRange getRange() {
    AgeRange t = new AgeRange();
    if (range != null) {
      t.setId(range.getId());
    } else {
      t.setId(0);
    }
    t.setLabel(label.getText());
    t.setAgemin((Integer) agemin.getValue());
    t.setAgemax((Integer) agemax.getValue());
    t.setCode(code.getText());
    
    return t;
  }
  
  public void clear() {
    range = null;
    label.setText(null);
    agemin.setValue(0);
    agemax.setValue(0);
    code.setText(null);
    agemin.requestFocus();
  }
  
  @Override
  public void focusLost(FocusEvent e) {
    modifyLabel(e);
  }

  @Override
  public void focusGained(FocusEvent e) {
    source = (JFormattedTextField) e.getSource();
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        source.selectAll();
      }
    });
  }
  
  void modifyLabel(FocusEvent e) {
    label.setText(MessageUtil.getMessage("age.range.include.tip", 
            new Object[]{agemin.getText(), agemax.getText()}));
  }
}
