/*
 * @(#)TeacherEditor.java	2.7.a 26/11/12
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
package net.algem.contact.teacher;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import net.algem.contact.InstrumentView;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Teacher editor tab.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class TeacherEditor
        extends FileTab
{

  private GemField cert1;
  private GemField cert2;
  private GemField cert3;
  private JCheckBox active;
  private int id;
  private InstrumentView instrument;

  public TeacherEditor(GemDesktop _desktop, int _id) {
    super(_desktop);

    id = _id;

    cert1 = new GemField(32);
    cert2 = new GemField(32);
    cert3 = new GemField(32);

    active = new JCheckBox();
    instrument = new InstrumentView(dataCache.getInstruments());
    GemPanel p = new GemPanel();
    p.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;
    
    gb.add(new GemLabel(BundleUtil.getLabel("Certificate.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Active.label")), 0, 4, 1, 1, GridBagHelper.WEST);

    gb.add(instrument, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(cert1, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(cert2, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(cert3, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(active, 1, 4, 1, 1, GridBagHelper.WEST);

    this.setLayout(new BorderLayout());
    add(p, BorderLayout.CENTER);
  }

  public void set(Teacher t, int _id) {
    id = _id;
    instrument.set(t.getInstruments());
    cert1.setText(t.getCertificate1());
    cert2.setText(t.getCertificate2());
    cert3.setText(t.getCertificate3());
    active.setSelected(t.isActive());
  }

  public Teacher getTeacher() {
    Teacher t = new Teacher(id);
    t.setInstruments(instrument.get());
    t.setCertificate1(cert1.getText());
    t.setCertificate2(cert2.getText());
    t.setCertificate3(cert3.getText());
    t.setActive(active.isSelected());

    return t;
  }

  public void clear() {
    cert1.setText("");
    cert2.setText("");
    cert3.setText("");
    //instrument.clear();
  }

  @Override
  public boolean isLoaded() {
    return id != 0;
  }

  @Override
  public void load() {
  }
}
