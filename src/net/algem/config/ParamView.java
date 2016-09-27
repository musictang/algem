/*
 * @(#)ParamView.java	2.10.0 15/06/2016
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 1.0a 07/07/1999
 */
public class ParamView
        extends GemBorderPanel
{

  protected GemField key;
  protected GemField value;
  protected JCheckBox active;
  protected GemLabel keyLabel;
  protected GemLabel valueLabel;

  private boolean activable;
  private GemButton btCancel;
  private GemButton btModify;
  private GemButton btDelete;

  public ParamView(boolean activable) {
    key = new GemField(20);
    key.setEditable(false);
    value = new GemField(30);
    this.activable = activable;

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btModify = new GemButton(GemCommand.SAVE_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btDelete);
    buttons.add(btCancel);
    buttons.add(btModify);

    GemPanel mask = new GemPanel();
    mask.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(mask);
    keyLabel = new GemLabel();
    valueLabel = new GemLabel();

    setLabels();

    gb.add(keyLabel, 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(valueLabel, 0, 1, 1, 1, GridBagHelper.EAST);

    gb.add(key, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(value, 1, 1, 3, 1, GridBagHelper.WEST);

    if (activable) {
      active = new JCheckBox(BundleUtil.getLabel("Active.label"));
      gb.add(active, 2, 0, 1, 1, GridBagHelper.WEST);
    }

    setLayout(new BorderLayout());
    add(mask, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  /**
   * Sets default labels.
   */
  protected void setLabels() {
    keyLabel.setText(BundleUtil.getLabel("Id.label"));
    valueLabel.setText(BundleUtil.getLabel("Label.label"));
  }

  public void addActionListener(ActionListener l) {
    btCancel.addActionListener(l);
    btModify.addActionListener(l);
    btDelete.addActionListener(l);
  }

  public void setKeyEditable(boolean _b) {
    key.setEditable(_b);
  }

  public void set(Param p) {
    key.setText(p.getKey());
    value.setText(p.getValue());
    if (activable && p instanceof ActivableParam) {
      active.setSelected(((ActivableParam)p).isActive());
    }
  }

  public Param get() {
    if (activable) {
      return new ActivableParam(key.getText(), value.getText(), active.isSelected());
    }
    return new Param(key.getText(), value.getText());
  }

  public void clear() {
    key.setText("");
    value.setText("");
    if (activable) {
      active.setSelected(false);
    }
  }
}
