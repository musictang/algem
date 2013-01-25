/*
 * @(#)WorkshopDlg.java	2.7.a 22/11/12
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
package net.algem.course;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.algem.config.InstrumentChoice;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.group.Musician;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class WorkshopDlg
        extends PopupDlg
        implements ActionListener
{

  private DataCache dataCache;
  private GemBorderPanel background;
  private GemNumericField no;
  private GemField name;
  private GemField firstname;
  private InstrumentChoice instrument;
  private Person member;

  public WorkshopDlg(Component c, String t, DataCache _dc) {
    super(c, t);

    dataCache = _dc;

    no = new GemNumericField(6);
    no.addActionListener(this);

    name = new GemField(30);
    name.setEditable(false);
    firstname = new GemField(25);
    firstname.setEditable(false);

    instrument = new InstrumentChoice(dataCache.getInstruments());

    background = new GemBorderPanel();
    background.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(background);

    gb.add(new GemLabel("No"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(firstname, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Instrument.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(instrument, 1, 3, 1, 1, GridBagHelper.WEST);

    init();
  }

  @Override
  public GemPanel getMask() {
    return background;
  }

  public String getField(int n) {
    switch (n) {
      case 0:
        return no.getText();
      case 1:
        return name.getText();
      case 2:
        return firstname.getText();
      case 3:
        return String.valueOf(instrument.getKey());
    }
    return null;
  }

  public String[] getFields() {
    String[] fields = new String[4];

    fields[0] = no.getText();
    fields[1] = name.getText();
    fields[2] = firstname.getText();
    fields[3] = String.valueOf(instrument.getKey());

    return fields;
  }

  public Musician get() {
    Musician m = new Musician(member);
    m.setInstrument(instrument.getKey());
    return m;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == no) {
      try {
        member = ((PersonIO) DataCache.getDao(Model.Person)).findId(no.getText());
        if (member != null) {
          name.setText(member.getName());
          firstname.setText(member.getFirstName());
          instrument.setSelectedIndex(0);
        }
      } catch (Exception ex) {
        GemLogger.logException(ex);
      }
    } else {
      super.actionPerformed(evt);
    }
  }
}
