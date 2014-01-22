/*
 * @(#)DDMandateView.java 2.8.r 21/01/14
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

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.r
 * @since 2.8.r 08/01/14
 */
public class DDMandateView 
  extends GemPanel
{

  private DateFrField dateCreationField;
  private DateFrField dateSignField;
  private JComboBox seqType;
  private GemField rum;
//  private JCheckBox recur;
  private JRadioButton recurOff;
  private JRadioButton recurOn;
  private DDMandate mandate;

  public DDMandateView() {
    dateCreationField = new DateFrField();
    dateCreationField.setEditable(false);
    dateSignField = new DateFrField();
    seqType = new JComboBox(DDSeqType.values());
    seqType.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        DDSeqType seqType = (DDSeqType) e.getItem();
        if (seqType.equals(DDSeqType.OOFF)) {
          recurOff.setSelected(true);
        } else if (!seqType.equals(DDSeqType.LOCK)) {
          recurOn.setSelected(true);
        }
      }
      
    });
    rum = new GemField(20);
    rum.setEditable(false);
    recurOn = new JRadioButton(BundleUtil.getLabel("Direct.debit.recurrent.label"));
    recurOff = new JRadioButton(BundleUtil.getLabel("Direct.debit.OOFF.label"));
//    recur = new JCheckBox();
    recurOn.setBorder(null);
    recurOn.setToolTipText(BundleUtil.getLabel("Direct.debit.recurrent.tip"));
    recurOff.setBorder(null);
    recurOff.setToolTipText(BundleUtil.getLabel("Direct.debit.one.off.tip"));
    ActionListener recurActionListener = new RecurActionListener();
    recurOn.addActionListener(recurActionListener);
    recurOff.addActionListener(recurActionListener);
    ButtonGroup btGroup = new ButtonGroup();
    btGroup.add(recurOn);
    btGroup.add(recurOff);

    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;
    
    gb.add(new GemLabel(BundleUtil.getLabel("Direct.debit.last.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Direct.debit.signature.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Direct.debit.seq.type.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    GemLabel rumLabel = new GemLabel(BundleUtil.getLabel("Direct.debit.rum.label"));
    String rumTip = BundleUtil.getLabel("Direct.debit.rum.tip");
    rumLabel.setToolTipText(rumTip);
    rum.setToolTipText(rumTip);
    gb.add(rumLabel, 0, 3, 1, 1, GridBagHelper.WEST);
//    GemLabel recurLabel = new GemLabel(BundleUtil.getLabel("Direct.debit.recurrent.label"));
    
    gb.add(recurOn, 0, 4, 1, 1, GridBagHelper.WEST);
    
    gb.add(dateCreationField, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dateSignField, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(seqType, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(rum, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(recurOff, 1, 4, 1, 1, GridBagHelper.WEST);
  }
  
  void set(DDMandate dd, boolean multiple) {
    mandate = dd;
    seqType.setSelectedItem(dd.getSeqType());
    if (multiple) {
      filter();
    } else {
      dateCreationField.setDate(dd.getLastDebit());
      dateSignField.setDate(dd.getDateSign());
      rum.setText(dd.getRum());
      if (dd.isRecurrent()) {
        recurOn.setSelected(true);
      } else {
        recurOff.setSelected(true);
      }
    }
  }
  
  DDMandate get() {

    DDMandate dm = new DDMandate(mandate.getIdper());
    dm.setLastDebit(mandate.getLastDebit());
    dm.setId(mandate.getId());
    dm.setName(mandate.getName());
    
    dm.setDateSign(dateSignField.getDateFr());
    dm.setSeqType((DDSeqType) seqType.getSelectedItem());
    dm.setRum(rum.getText());
    if (recurOn.isEnabled()) {
      dm.setRecurrent(recurOn.isSelected() ? true : false);
    } else {
      dm.setRecurrent(mandate.isRecurrent());
    }
    
    return dm;
  }

  void clear() {
    mandate = null;
    dateSignField.setDate(new DateFr());
    seqType.setSelectedIndex(0);
    rum.setText(null);
    recurOn.setSelected(true);
  }
  
  private void filter() {
    dateSignField.setDate(new DateFr());
    dateSignField.setEnabled(false);
    rum.setText(null);
    rum.setEnabled(false);
    recurOn.setEnabled(false);
    recurOff.setEnabled(false);
  }
  
  private class RecurActionListener implements ActionListener {

     @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == recurOff) {
          seqType.setSelectedItem(DDSeqType.OOFF);
        } else if (e.getSource() == recurOn) {
          if (!mandate.getSeqType().equals(DDSeqType.OOFF)) {
            seqType.setSelectedItem(mandate.getSeqType());
          } else {
            seqType.setSelectedIndex(0);
          }
        }
      }
    
  }
}
