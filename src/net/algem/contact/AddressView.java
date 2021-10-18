/*
 * @(#)AddressView.java	2.13.0 31/03/17
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
package net.algem.contact;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Address panel entry.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 1.0a 07/07/1999
 */
public class AddressView
        extends GemBorderPanel implements ItemListener
{

  private GemField adr1;
  private GemField adr2;
  private GemField cdp;
  private GemField city;
  private JCheckBox archive;
  //final static Color ARCHIVE_COLOR = new Color(153, 153, 204); // #9999CC

  public AddressView() {
    init(true);
  }

  /**
   *
   * @param b border style
   * @param archive with archive
   */
  public AddressView(Border b, boolean archive) {
    super(b);
    init(archive);
  }

  public void setCodePostalCtrl(CodePostalCtrl ctrl) {
    cdp.addFocusListener(ctrl);
    cdp.addActionListener(ctrl);
    cdp.addKeyListener(ctrl);
    ctrl.setFields(city, cdp);
  }

  /**
   * Sets the address.
   * Archive component may be null.
   * @param ar the address to display
   */
  public void set(Address ar) {
    if (ar != null) {
      adr1.setText(ar.getAdr1());
      adr2.setText(ar.getAdr2());
      cdp.setText(ar.getCdp());
      city.setText(ar.getCity());
      if (archive != null) {
        archive.setSelected(ar.isArchive());
        if (archive.isSelected()) {
          setBgColor(InfoView.ARCHIVE_COLOR);
        }
      }
    }
  }

  public List<Address> getAll() {
    List<Address> v = new ArrayList<>();
    Address a = get();
    if (a != null) {
      v.add(a);
    }
    return v;
  }

  public Address get() {
    if (adr1.getText().length() < 1
            && adr2.getText().length() < 1
            && cdp.getText().length() < 1
            && city.getText().length() < 1) {
      return null;
    }
    Address ar = new Address(0, 0, adr1.getText(), adr2.getText(), cdp.getText(), city.getText());
    ar.setArchive(archive == null ? false : archive.isSelected());
    return ar;
  }

  public void setEditable(boolean val) {
    adr1.setEditable(val);
    adr2.setEditable(val);
    cdp.setEditable(val);
    city.setEditable(val);
    setBgColor(val ? Color.white : Color.lightGray);
  }

  public void setBgColor(Color c) {
    adr1.setBackground(c);
    adr2.setBackground(c);
    cdp.setBackground(c);
    city.setBackground(c);
  }

  public void clear() {
    adr1.setText("");
    adr2.setText("");
    cdp.setText("");
    city.setText("");
    archive.setSelected(false);
    setEditable(true);
  }

  public JCheckBox getArchive() {
    return archive;
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getStateChange() == ItemEvent.SELECTED) {
      setEditable(false);
      setBgColor(InfoView.ARCHIVE_COLOR);
    } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
      setEditable(true);
    }
  }

  private void init(boolean ar) {
    adr1 = new GemField(40, AddressIO.ADR1_LIMIT);
    adr1.setMinimumSize(new Dimension(340, adr1.getPreferredSize().height));
    adr2 = new GemField(40);
    adr2.setMinimumSize(new Dimension(340, adr2.getPreferredSize().height));
    cdp = new CodePostalField();
    cdp.setMinimumSize(new Dimension(60, cdp.getPreferredSize().height));
    city = new GemField(30);
    city.setMinimumSize(new Dimension(340, city.getPreferredSize().height));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = new Insets(0,0,4,4);
    
    gb.add(new GemLabel(BundleUtil.getLabel("Address1.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Address2.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Address.zip.code.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("City.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(adr1, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(adr2, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(cdp, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(city, 1, 3, 1, 1, GridBagHelper.WEST);
    if(ar) {
      archive = new JCheckBox("archive");
      archive.setBorder(null);
      archive.addItemListener(this);
      gb.add(archive, 4, 0, 1, 1, GridBagHelper.EAST);
    }
  }

}
