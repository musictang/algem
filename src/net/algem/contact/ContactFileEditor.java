/*
 * @(#)ContactFileEditor.java	2.9.4.6 03/06/15
 * 
 * Copyright (c) 1998-2015 Musiques Tangentes. All Rights Reserved.
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
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import net.algem.config.Category;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.ParamTableIO;
import net.algem.contact.member.PersonSubscriptionCard;
import net.algem.util.model.Reloadable;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Contact editor view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 1.0a 09/07/2002
 */
public class ContactFileEditor
        extends FileTab
        implements ItemListener, Reloadable {

  private Contact contact;
  private GemLabel note;
  private PersonView personView;
  private TelView teleView;
  private AddressView addressView;
  private boolean linkTelAddress;
  private JCheckBox cbTelAddress;
  private GridBagHelper gb;

  private GemPanel infosView;
  private EmailView emailView;
  private WebSiteView websiteView;

  public ContactFileEditor(GemDesktop _desktop) {
    super(_desktop);

    note = new GemLabel();
    note.setForeground(java.awt.Color.red);
    personView = new PersonView();
    infosView = new GemBorderPanel();
    //vueTele = new TeleViewOld(dc);
    teleView = new TelView(ParamTableIO.find(Category.TELEPHONE.getTable(), Category.TELEPHONE.getCol(), dc));
    emailView = new EmailView();

    websiteView = new WebSiteView(ParamTableIO.find(Category.SITEWEB.getTable(), Category.SITEWEB.getCol(), dc));

    infosView.setLayout(new BoxLayout(infosView, BoxLayout.Y_AXIS));
    infosView.add(teleView);
    infosView.add(emailView);
    infosView.add(websiteView);

    JScrollPane scp = new JScrollPane(infosView);
    scp.setBorder(null);

    GemPanel addressPanel = new GemPanel();
    addressPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    addressView = new AddressView();
    addressView.setBorder(null);
    addressPanel.add(addressView);

    this.setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
    GemPanel gp = new GemPanel(new GridLayout(1,1));
    gp.add(personView);
    gp.add(scp);
    gb.add(gp, 1, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(new JLabel(" "), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(addressPanel, 0, 3, 2, 1, GridBagHelper.BOTH, GridBagHelper.WEST);
    gb.add(note, 0, 4, 2, 1, GridBagHelper.WEST);

  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getSource() == cbTelAddress) {
      setLinkTelAddress(cbTelAddress.isSelected());
    }
  }

  public void clear() {
    contact = null;
    personView.clear();
    teleView.clear();
    emailView.clear();
    websiteView.clear();
    addressView.clear();
    
    if (cbTelAddress != null) {
      cbTelAddress.removeItemListener(this);
      remove(cbTelAddress);
      validate();
      cbTelAddress = null;
    }
    linkTelAddress = false;
    note.setText("");
  }

  public void setCodePostalCtrl(CodePostalCtrl ctrl) {
    addressView.setCodePostalCtrl(ctrl);
  }

  public int getId() {
    return personView.getId();
  }

  public void setID(int i) {
    personView.setId(i);
  }

  public void setNote(Note n) {
    if (n != null) {
      String s = n.getText().replace('\n', ' ');
      note.setText(s);
    }
  }

  /**
   * Load infos from contact in the corresponding views.
   * @param c contact
   */
  public void set(Contact c) {
    contact = c;
    setPerson(c);
    if (c != null) {
      setAddress(c.getAddress());
      setTele(c.getTele());
      setEmail(c.getEmail());
      websiteView.setSites(c.getSites());
    }
  }

  public void setSubscriptionRest(PersonSubscriptionCard card) {
    personView.showSubscriptionRest(card);
  }

  /**
   * Gets contact from view.
   * @return a Contact
   */
  public Contact getContact() {
    Contact c = new Contact(personView.get());
    c.setAddress(getAddress());
    c.setTele(getTele());
    c.setEmail(getEmail());
    c.setWebSites(getSites());

    return c;
  }

  public void setPerson(Person p) {
    if (p != null) {
      String configDir = ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey());
      personView.set(p, configDir);
    }
  }

  public Person getPerson() {
    return personView.get();
  }

  public void setLinkTelAddress(boolean b) {
    addressView.setEditable(!b);
    addressView.getArchive().setEnabled(!b);
    teleView.setEditable(!b);
    linkTelAddress = b;
  }

  public void setLinkTelAddress(Vector a, Vector t, JCheckBox cb) {
    if (a != null && a.size() > 0) {
      addressView.set((Address) a.elementAt(0));
    }
    addressView.setEditable(false);
    addressView.getArchive().setEnabled(false);
    teleView.setLien(t);
    teleView.setEditable(false);
    gb.add(cb, 0, 2, 2, 1, GridBagHelper.CENTER);
    cbTelAddress = cb;
    linkTelAddress = true;
    revalidate();
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

  public void setTele(Vector<Telephone> t) {
    teleView.setTels(t);
  }
  
    public Vector<Telephone> getTele() {
    if (linkTelAddress == false) {
      return teleView.getTels();
    }
    return null;
  }

  public void setEmail(Vector<Email> ve) {
    emailView.setEmails(ve);
  }

  public Vector<Email> getEmail() {
    return emailView.getEmails();
  }
  
  public void setSites(List<WebSite> sites) {
    websiteView.setSites(new Vector<WebSite>(sites));
  }
  
  public Vector<WebSite> getSites() {
    return websiteView.getSites();
  }

  @Override
  public boolean isLoaded() {
    return contact != null;
  }

  @Override
  public void load() {

  }

  /**
   * Refresh view.
   * Implementing net.algem.ihm.dossier.Reloadable
   * @param d dossier
   */
  @Override
  public void reload(PersonFile d) {
    clear();
    set(d.getOldContact());
  }

  /**
   * Filtering contact view.
   * @param f int person type
   */
  public void filter(int f) {
    personView.filter(f);
  }
}
