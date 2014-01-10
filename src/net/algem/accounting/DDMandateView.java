/*
 * @(#)DDMandateView.java 2.8.r 08/01/14
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
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
  private JCheckBox recur;
  private DDMandate mandate;

  public DDMandateView() {
    dateCreationField = new DateFrField();
    dateCreationField.setEditable(false);
    dateSignField = new DateFrField();
    seqType = new JComboBox(DDSeqType.values());
    rum = new GemField(20);
    rum.setEditable(false);
    recur = new JCheckBox();
    recur.setBorder(null);
    
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;
    
    gb.add(new GemLabel(BundleUtil.getLabel("Direct.debit.creation.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Direct.debit.signature.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Direct.debit.seq.type.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    GemLabel rumLabel = new GemLabel(BundleUtil.getLabel("Direct.debit.rum.label"));
    String rumTip = BundleUtil.getLabel("Direct.debit.rum.tip");
    rumLabel.setToolTipText(rumTip);
    rum.setToolTipText(rumTip);
    gb.add(rumLabel, 0, 3, 1, 1, GridBagHelper.WEST);
    GemLabel recurLabel = new GemLabel(BundleUtil.getLabel("Direct.debit.recurrent.label"));
    recurLabel.setToolTipText(BundleUtil.getLabel("Direct.debit.recurrent.tip"));
    gb.add(recurLabel, 0, 4, 1, 1, GridBagHelper.WEST);
    
    gb.add(dateCreationField, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dateSignField, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(seqType, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(rum, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(recur, 1, 4, 1, 1, GridBagHelper.WEST);
  }
  
  void set(DDMandate dd, boolean multiple) {
    mandate = dd;
    seqType.setSelectedItem(dd.getSeqType());
    if (multiple) {
      filter();
    } else {
      dateCreationField.setDate(dd.getCreation());
      dateSignField.setDate(dd.getDateSign());
      rum.setText(dd.getRum());
      recur.setSelected(dd.isRecurrent());
    }
  }
  
  DDMandate get() {

    mandate.setDateSign(dateSignField.getDateFr());
    mandate.setSeqType((DDSeqType) seqType.getSelectedItem());
    mandate.setRum(rum.getText());
    mandate.setRecurrent(recur.isSelected());
    
    return mandate;
  }

  void clear() {
    mandate = null;
    dateSignField.setDate(new DateFr());
    seqType.setSelectedIndex(0);
    rum.setText(null);
    recur.setSelected(false);
  }
  
  private void filter() {
    dateSignField.setDate(new DateFr());
    dateSignField.setEnabled(false);
    rum.setText(null);
    rum.setEnabled(false);
    recur.setSelected(false);
    recur.setEnabled(false);
  }
}
