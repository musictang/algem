/*
 * @(#)ContactView.java	2.6.a 17/09/12
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
package net.algem.contact;

import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import net.algem.config.Category;
import net.algem.util.DataConnection;
import net.algem.config.ParamTableIO;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ContactView
        extends GemPanel
{

  private GemLabel note;
  private PersonView personView;
  private TeleView teleView;
  private AddressView addressView;
  private boolean linkTelAddress;
  private JCheckBox cbTelAdresse;
  private GridBagHelper gb;
  private GemBorderPanel infosView;
  private EmailView emailView;
  private WebSiteView websiteView;

  public ContactView(DataConnection dc) {
    note = new GemLabel();
    note.setForeground(java.awt.Color.red);
    personView = new PersonView();
    teleView = new TeleView(ParamTableIO.find(Category.TELEPHONE.getTable(), Category.TELEPHONE.getCol(), dc));
    emailView = new EmailView();
    websiteView = new WebSiteView(ParamTableIO.find(Category.SITEWEB.getTable(), Category.SITEWEB.getCol(), dc));
    infosView = new GemBorderPanel();
    infosView.setLayout(new BoxLayout(infosView, BoxLayout.Y_AXIS));
    infosView.add(teleView);
    infosView.add(emailView);
    infosView.add(websiteView);
    addressView = new AddressView();
    JScrollPane scroll = new JScrollPane(infosView);

    this.setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);

    gb.add(personView, 0, 0, 1, 1, GridBagHelper.BOTH, 0.4, 1.0);
    gb.add(scroll, 1, 0, 1, 1, GridBagHelper.BOTH, 0.6, 1.0);
    gb.add(new JLabel(" "), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(addressView, 0, 2, 2, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(note, 0, 3, 2, 1, GridBagHelper.WEST);

  }

  public void setCodePostalCtrl(CodePostalCtrl ctrl) {
    addressView.setCodePostalCtrl(ctrl);
  }

  public int getId() {
    return personView.getId();
  }

  public void setNote(Note n) {
    if (n != null) {
      String s = n.getText().replace('\n', ' ');
      note.setText(s);
    }
  }

  public void set(Contact c) {
    setPerson(c);
    setAddress(c.getAddress());
    setTele(c.getTele());
    websiteView.setSites(c.getSites());
  }

  public void setPerson(Person p) {
    if (p != null) {
      personView.set(p);
    }
  }

  public Person getPerson() {
    return personView.get();
  }

  public void setLinkTelAddress(boolean b) {
    addressView.setEditable(!b);
    linkTelAddress = b;
  }

  public void setLinkTelAddress(Vector a, Vector t, JCheckBox cb) {
    addressView.set((Address) a.elementAt(0));
    addressView.setEditable(false);
    gb.add(cb, 1, 1, 1, 1, GridBagHelper.EAST);
    validate();
    cbTelAdresse = cb;
    linkTelAddress = true;
  }

  public void setAddress(Address a) {
    if (a != null) {
      addressView.set(a);
    }
  }

  public Vector getAddressAll() {
    if (linkTelAddress == false) {
      return addressView.getAll();
    } else {
      return null;
    }
  }

  public Address getAddress() {
    if (linkTelAddress == false) {
      return addressView.get();
    } else {
      return null;
    }
  }

  public void setTele(Vector t) {
    teleView.setTels(t);
  }

  public void setEmail(Vector<Email> ve) {
    emailView.setEmails(ve);
  }

  public Vector<Email> getEmail() {
    return emailView.getEmails();
  }

  public Vector<Telephone> getTele() {
    if (linkTelAddress == false) {
      return teleView.getTels();
    }
    return null;
  }

  public Vector<WebSite> getSites() {
    return websiteView.getSites();
  }

  public void clear() {
    personView.clear();
    addressView.clear();
    teleView.clear();
    emailView.clear();
    websiteView.clear();
    if (cbTelAdresse != null) {
      remove(cbTelAdresse);
      validate();
      cbTelAdresse = null;
    }
    linkTelAddress = false;
    note.setText("");
  }
}
