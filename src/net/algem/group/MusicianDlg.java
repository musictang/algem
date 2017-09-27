/*
 * @(#)MusicianDlg.java	2.15.2 27/09/17
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
package net.algem.group;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import net.algem.config.Instrument;
import net.algem.config.InstrumentChoice;
import net.algem.config.InstrumentIO;
import net.algem.contact.Contact;
import net.algem.contact.ContactSelectEvent;
import net.algem.contact.Person;
import net.algem.contact.PersonFileSearchCtrl;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Musicians search dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 */
public class MusicianDlg
        extends PopupDlg
        implements ActionListener, GemEventListener
{

  private DataCache dataCache;
  private GemPanel back;
  private GemNumericField no;
  private GemField name;
  private GemField firstname;
  private InstrumentChoice instrument;
  private Musician mus;
  private ImageIcon icon;
  private GemButton btSearch;
  private GemDesktop desktop;
  private Component component;
  private int operation;

  public MusicianDlg(Component c, String t, GemDesktop desktop) {
    super(c, t, false);
    this.component = c;
    this.desktop = desktop;
    dataCache = desktop.getDataCache();

    no = new GemNumericField(6);
    no.addActionListener(this);

    name = new GemField(25);
    name.setEditable(false);
    firstname = new GemField(25);
    firstname.setEditable(false);
    instrument = new InstrumentChoice(dataCache.getInstruments());
    icon = ImageUtil.createImageIcon(ImageUtil.SEARCH_ICON);
    btSearch = new GemButton(icon);
    btSearch.setMargin(new Insets(0, 0, 0, 0));
    btSearch.setBorder(null);
    btSearch.addActionListener(this);

    back = new GemPanel();
    back.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(back);

    gb.add(new GemLabel(BundleUtil.getLabel("Number.abbrev.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(btSearch, 2, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 2, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(firstname, 1, 2, 2, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Instrument.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(instrument, 1, 3, 2, 1, GridBagHelper.WEST);

    init();
  }

  @Override
  public GemPanel getMask() {
    return back;
  }

  public void setPerson(Musician p) {
    mus = p;
    no.setEditable(false);
    no.setText(String.valueOf(p.getId()));
    name.setText(p.getName());
    firstname.setText(p.getFirstName());
    instrument.setKey(p.getInstrument());
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
    Musician m = new Musician(mus);
    m.setInstrument(Integer.parseInt(getField(3)));
    return m;
  }

  public void setOperation(int operation) {
    this.operation = operation;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    DataConnection dc = DataCache.getDataConnection();
    if (evt.getSource() == no) {
      try {
        int id = Integer.parseInt(no.getText());
        setMusician((Person) DataCache.findId(id, Model.Person), dc);
      } catch (Exception ex) {
        GemLogger.log(Level.SEVERE, ex.getMessage());
      }
    } else if (evt.getSource() == btSearch) {
      PersonFileSearchCtrl pfSearch = new PersonFileSearchCtrl(desktop, BundleUtil.getLabel("Contact.browser.label"), this);
      pfSearch.init();
      desktop.addPanel("Contact", pfSearch, GemModule.S_SIZE);
      dlg.setLocation(pfSearch.getLocation().x + pfSearch.getWidth(), dlg.getLocation().y);// shift the position to avoid overlapping
    } else if (evt.getActionCommand().equals(GemCommand.VALIDATE_CMD)) {
      ((MusicianListView) component).setMusician(get(), operation);
      super.actionPerformed(evt);// force closing
    } else {
      super.actionPerformed(evt);
    }
  }

  private void setMusician(Person p, DataConnection dc) throws SQLException {
    int inst = 0;
    List<Integer> li = InstrumentIO.find(p.getId(), Instrument.MUSICIAN, dc);
    if (li != null && li.size() > 0) {
      inst = li.get(0);
    } else {
      li = InstrumentIO.find(p.getId(), Instrument.MEMBER, dc);
      if (li != null && li.size() > 0) {
        inst = li.get(0);
      }
    }
    mus = new Musician(p);
    mus.setInstrument(inst);
    no.setText(String.valueOf(mus.getId()));
    name.setText(mus.getName());
    firstname.setText(mus.getFirstName());
    instrument.setKey(inst);
  }

  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof ContactSelectEvent) {
      Contact c = ((ContactSelectEvent) evt).getContact();
      try {
        setMusician(c, DataCache.getDataConnection());
      } catch (SQLException ex) {
        GemLogger.log(Level.SEVERE, ex.getMessage());
      }
    }
  }


}
