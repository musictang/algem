/*
 * @(#)PersonView.java	2.7.e 01/02/13
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

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.jnlp.DownloadService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import net.algem.contact.member.PersonSubscriptionCard;
import net.algem.group.Group;
import net.algem.group.GroupFileEditor;
import net.algem.util.BundleUtil;
import net.algem.util.ImageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.e
 */
public class PersonView
        extends GemBorderPanel
{

  private GemNumericField no;
  private GemField name;
  private GemField firstname;
  private CivilChoice civil;
  private JCheckBox cbImgRights;
  private JLabel photo;
  private GemLabel rhLabel;
  /** Nombre d'heures restantes sur carte abonnement. */
  private GemLabel nrh;
  private GemDesktop desktop;
  private GridBagHelper gb;

  public PersonView() {
    init();
  }

  public PersonView(GemDesktop desktop) {
    super();
    this.desktop = desktop;
    init();
  }

  private void init() {
    no = new GemNumericField(6);
    no.setEditable(false);
    no.setBackground(Color.lightGray);
    name = new GemField(true, 20);
    firstname = new GemField(true, 20);
    civil = new CivilChoice();
    cbImgRights = new JCheckBox();
    rhLabel = new GemLabel();
    nrh = new GemLabel();

    this.setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
    photo = new JLabel();
    gb.add(photo, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Person.civility.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Person.img.rights.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(rhLabel, 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(no, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(firstname, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(civil, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(cbImgRights, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(nrh, 1, 6, 1, 1, GridBagHelper.WEST);
  }

  public void set(Person pr) {
    no.setText(String.valueOf(pr.getId()));
    name.setText(pr.getName());
    firstname.setText(pr.getFirstName());
    civil.setSelectedItem(pr.getCivility());
    cbImgRights.setSelected(!pr.getImgRights()); // si false, sélectionner
    ImageIcon icon = setPhoto(pr.getId());
    if (icon != null) {
      photo.setIcon(icon);
    }

  }

  public ImageIcon setPhoto(int idper) {
    ImageIcon icon;

    String path = ImageUtil.PHOTO_PATH + idper + ".jpg";
    URL url = getClass().getResource(path);
    if (url == null) {
      return null;
    }
    try {
      //InputStream is = getClass().getResourceAsStream(url.getPath());
      //BufferedImage bi1 = ImageIO.read(is);
      BufferedImage bi1 = ImageIO.read(url);
      //if (ImageUtil.PHOTO_WIDTH != bi1.getWidth() || ImageUtil.PHOTO_HEIGHT != bi1.getHeight()) {
      if (bi1.getHeight() > ImageUtil.PHOTO_HEIGHT  && !isRunningJavaWebStart()) {

        System.out.println("rescaling !");
        BufferedImage bi2 = ImageUtil.rescale(bi1);
        BufferedImage bi3 = ImageUtil.formatPhoto(bi2);

        String dest = url.getFile();
        File oldFile = new File(dest);
        int index = dest.lastIndexOf(".");
        dest = dest.substring(0, index);
        oldFile.renameTo(new File(dest + "_" + System.currentTimeMillis() + ".jpg"));
        File newFile = new File(url.getPath());
        ImageIO.write(bi3, "jpg", newFile);
      }
      icon = new ImageIcon(url);

    } catch (Exception e) {
      e.printStackTrace();
      icon = null;
    }
    return icon;

  }

  public void setRemainingHours(PersonSubscriptionCard cap) {

    if (cap != null) {
      rhLabel.setText("Abonnement restant : ");
      nrh.setText(cap.displayRestTime());
    }

  }

  public Person get() {
    Person pr = new Person();
    try {
      pr.setId(Integer.parseInt(no.getText()));
    } catch (Exception e) {
      pr.setId(0);
    }

    pr.setName(name.getText());
    pr.setFirstName(firstname.getText());
    pr.setCivility((String) civil.getSelectedItem());
    pr.setImgRights(!cbImgRights.isSelected()); // si sélectionné refuse le droit à l'image
    //pr.setNote(note);
    return pr;
  }

  public void setId(int n) {
    no.setText(String.valueOf(n));
  }

  public int getId() {
    int id = 0;
    try {
      id = Integer.parseInt(no.getText());
    } catch (Exception e) {
    }

    return id;
  }

  public void clear() {
    no.setText("");
    name.setText("");
    firstname.setText("");
    civil.setSelectedIndex(0);
    cbImgRights.setSelected(false);
    //note=0;
  }

  public void filter(int f) {
    if (Person.ESTABLISHMENT == f) {
      firstname.setEnabled(false);
      civil.setEnabled(false);
      cbImgRights.setEnabled(false);
    }
  }

  public void showGroups(Collection<Group> gl) {
    if (gl.isEmpty()) {
      return;
    }
    GemPanel groupsPanel = new GemPanel();
    for (final Group g : gl) {
      JButton jb = new JButton(g.getName());
      jb.addActionListener(new ActionListener()
      {

        public void actionPerformed(ActionEvent e) {
          GroupFileEditor editeurGroupe = new GroupFileEditor(g, GemModule.GROUPE_DOSSIER_KEY);
          desktop.addModule(editeurGroupe);
        }
      });
      groupsPanel.add(jb);
    }
    gb.add(groupsPanel, 0, 7, 1, 1, GridBagHelper.WEST);

  }

  /**
   * Checks if java webstart is running.
   * 
   * @return true if running
   */
  private boolean isRunningJavaWebStart() {
    try {
      DownloadService ds = (DownloadService) ServiceManager.lookup("javax.jnlp.DownloadService");
      return ds != null;
    } catch (UnavailableServiceException e) {
      return false;
    }
  }
}
