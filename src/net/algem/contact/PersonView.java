/*
 * @(#)PersonView.java	2.9.1 13/11/14
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
package net.algem.contact;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.algem.contact.member.PersonSubscriptionCard;
import net.algem.group.Group;
import net.algem.group.GroupFileEditor;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Person view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 */
public class PersonView
        extends GemBorderPanel
{

  private GemNumericField no;
  private GemField name;
  private GemField firstname;
  private CivilChoice civil;
  private JCheckBox cbImgRights;
  private JCheckBox cbPartner;
  private GemField organization;
  private GemField nickname;
  private JLabel photoField;
  private GemLabel cardLabel;
  /** Rest time on subscription card. */
  private GemLabel cardInfo;
  private GemDesktop desktop;
  private GridBagHelper gb;
  private short ptype = Person.PERSON;
  private FileFilter photoFilter;
  

  public PersonView() {
    init();
  }

  private void init() {
    no = new GemNumericField(6);
    no.setEditable(false);
    no.setBackground(Color.lightGray);
    organization = new GemField(true, 20);
    name = new GemField(true, 20);
    firstname = new GemField(true, 20);
    nickname = new GemField(true, 20);
    civil = new CivilChoice();
    
    
    cbImgRights = new JCheckBox(BundleUtil.getLabel("Person.img.rights.label"));
    cbImgRights.setToolTipText(BundleUtil.getLabel("Person.img.rights.tip"));
    cbImgRights.setBorder(null);
    cbPartner = new JCheckBox(BundleUtil.getLabel("Partner.info.label"));
    cbPartner.setToolTipText(BundleUtil.getLabel("Partner.info.tip"));
    cbPartner.setBorder(null);
    
    cardLabel = new GemLabel();
    cardInfo = new GemLabel();

    this.setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
    GemBorderPanel photoPanel = new GemBorderPanel(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    photoField = new JLabel();
    photoPanel.add(photoField);
//    photoField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(photoPanel, 0, 0, 1, 6, GridBagHelper.NORTHWEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Organization.label")), 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Nickname.label")), 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Person.civility.label")), 1, 5, 1, 1, GridBagHelper.WEST);

    gb.add(no, 2, 0, 2, 1, GridBagHelper.WEST);
    gb.add(organization, 2, 1, 2, 1, GridBagHelper.WEST);
    gb.add(name, 2, 2, 2, 1, GridBagHelper.WEST);
    gb.add(firstname, 2, 3, 2, 1, GridBagHelper.WEST);
    gb.add(nickname, 2, 4, 2, 1, GridBagHelper.WEST);
    gb.add(civil, 2, 5, 2, 1, GridBagHelper.WEST);
    gb.add(cbImgRights, 2, 6, 1, 1, GridBagHelper.WEST);
    gb.add(cbPartner, 2, 7, 1, 1, GridBagHelper.WEST);
    gb.add(cardLabel, 2, 8, 1, 1, GridBagHelper.WEST);
    gb.add(cardInfo, 3, 9, 1, 1, GridBagHelper.WEST);
  }

  void set(Person p, String dir) {
    no.setText(String.valueOf(p.getId()));
    name.setText(p.getName());
    firstname.setText(p.getFirstName());
    nickname.setText(p.getNickName());
    civil.setSelectedItem(p.getGender());
    cbImgRights.setSelected(p.hasImgRights());
    cbPartner.setSelected(p.isPartnerInfo());
    organization.setText(p.getOrganization());
    if (p.getType() == Person.PERSON) {
      photoFilter = new PhotoFileFilter(p.getId());
      BufferedImage orig = getPhoto(dir,p.getId());
//      ImageIcon icon = null;
//      if (orig == null) {
//        icon = new ImageIcon();
//      }
//      else  {
       ImageIcon icon = (orig == null ? null : getImageIcon(orig));
//      }
      photoField.setIcon(icon);
    }
    ptype = p.getType();
    
  }

  /*private ImageIcon setPhoto(URL url) {
    ImageIcon icon = null;
    try {
      //InputStream is = getClass().getResourceAsStream(url.getPath());
      //BufferedImage bi1 = ImageIO.read(is);
      BufferedImage bi1 = ImageIO.read(url);
      //if (ImageUtil.PHOTO_ID_WIDTH != bi1.getWidth() || ImageUtil.PHOTO_ID_HEIGHT != bi1.getHeight()) {
      if (bi1.getHeight() > ImageUtil.PHOTO_ID_HEIGHT && !isRunningJavaWebStart()) {
        //System.out.println("rescaling !");
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
      GemLogger.logException(e);
    }
    return icon;

  }*/
  
  /**
   * Gets an image icon with photo id of the current contact.
   * @param orig original photo image
   * @return an image icon or null if no image has been processed
   */
  private ImageIcon getImageIcon(final BufferedImage orig) {
    ImageIcon icon = null;
    try {
      //InputStream is = getClass().getResourceAsStream(url.getPath());
      //BufferedImage bi1 = ImageIO.read(is);
      BufferedImage buffered = orig;
      //if (ImageUtil.PHOTO_ID_WIDTH != bi1.getWidth() || ImageUtil.PHOTO_ID_HEIGHT != bi1.getHeight()) {
      if (buffered.getHeight() > ImageUtil.PHOTO_ID_HEIGHT) {// && !isRunningJavaWebStart()) {
        //System.out.println("rescaling !");
        buffered = ImageUtil.rescale(buffered);
        buffered = ImageUtil.formatPhoto(buffered);
//
//        String dest = url.getFile();
//        File oldFile = new File(dest);
//        int index = dest.lastIndexOf(".");
//        dest = dest.substring(0, index);
//        oldFile.renameTo(new File(dest + "_" + System.currentTimeMillis() + ".jpg"));
//        File newFile = new File(url.getPath());
//        ImageIO.write(bi3, "jpg", newFile);
      }
      icon = new ImageIcon(buffered);

    } catch (Exception e) {
      GemLogger.logException(e);
    }
    return icon;

  }
  
  /*private URL getPhotoURL(int idper, String dir) {
    URL url = null;
//    if (dir.endsWith("/") || dir.endsWith("\\")) {
//      dir = dir.substring(0, dir.length() -1);
//    }
    String suffix = FileUtil.FILE_SEPARATOR + idper + ".jpg";
    

    File dirp = new File(dir);
    File[] files = null;
    if (dirp.isDirectory() && dirp.canRead()) {
     files = dirp.listFiles(photoFilter);
    }
    if (files != null && files.length > 0) {
      try {
        url = files[0].toURI().toURL();
      } catch (MalformedURLException ex) {
        GemLogger.logException(ex);
      }
    } else { // default resource path
      File defaultDir = new File(ImageUtil.PHOTO_PATH);
      files = defaultDir.listFiles(photoFilter);
      try {
        url = files[0].toURI().toURL();
      } catch (MalformedURLException ex) {
        GemLogger.logException(ex);
      }
//      String path = ImageUtil.PHOTO_PATH + suffix;
//      url = getClass().getResource(path);
    }
    return url;
  }*/
  
  /**
   * Gets the photo {@code idper} image from photo {@code configDir}.
   * @param configDir photo config dir
   * @param idper person's id
   * @return a buffered image if a resource has been found or null otherwhise
   */
  private BufferedImage getPhoto(String configDir, int idper) {

    File dir = new File(configDir);
    File[] files = null;
    if (dir.isDirectory() && dir.canRead()) {
     files = dir.listFiles(photoFilter);
    }
    try {
      if (files != null && files.length > 0) {
        return ImageIO.read(files[0]);
      } else { // default resource path
        for (String s : ImageUtil.DEFAULT_IMG_EXTENSIONS) {
          InputStream input = getClass().getResourceAsStream(ImageUtil.PHOTO_PATH + idper + s);
          if (input == null) {
            input = getClass().getResourceAsStream(ImageUtil.DEFAULT_PHOTO_ID);
          }
          return ImageIO.read(input);
        }
      }
    } catch(IOException ie) {
      GemLogger.logException(ie);
    } catch(IllegalArgumentException ia) {
      GemLogger.logException(ia);
    }
    return null;
  }


  void setRemainingHours(PersonSubscriptionCard cap) {

    if (cap != null) {
      cardLabel.setText("Abonnement restant : ");
      cardInfo.setText(cap.displayRestTime());
    }

  }

  public Person get() {
    Person pr = new Person();
    try {
      pr.setId(Integer.parseInt(no.getText()));
    } catch (NumberFormatException e) {
      pr.setId(0);
    }
    pr.setType(ptype);
    pr.setName(name.getText());
    pr.setFirstName(firstname.getText());
    pr.setNickName(nickname.getText().isEmpty() ? null : nickname.getText().trim());
    pr.setGender((String) civil.getSelectedItem());
    pr.setImgRights(cbImgRights.isSelected());
    pr.setPartnerInfo(cbPartner.isSelected());
    pr.setOrganization(organization.getText().isEmpty() ? null : organization.getText().trim());
    //pr.setNote(note);
    return pr;
  }

  public void setId(int n) {
    no.setText(String.valueOf(n));
  }

  public int getId() {
    try {
      return Integer.parseInt(no.getText());
    } catch (NumberFormatException e) {
      GemLogger.log(Level.WARNING, e.getMessage());
      return 0;
    }
  }

  public void clear() {
    no.setText("");
    name.setText("");
    firstname.setText("");
    nickname.setText("");
    civil.setSelectedIndex(0);
    cbImgRights.setSelected(false);
    cbPartner.setSelected(false);
    photoField.setText("");
    //note=0;
  }

  public void filter(int f) {
    if (Person.ESTABLISHMENT == f) {
      firstname.setEnabled(false);
      civil.setEnabled(false);
      cbImgRights.setEnabled(false);
      cbPartner.setEnabled(false);
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
          GroupFileEditor groupEditor = new GroupFileEditor(g, GemModule.GROUPE_DOSSIER_KEY);
          desktop.addModule(groupEditor);
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
//  private boolean isRunningJavaWebStart() {
//    try {
//      DownloadService ds = (DownloadService) ServiceManager.lookup("javax.jnlp.DownloadService");
//      return ds != null;
//    } catch (UnavailableServiceException e) {
//      return false;
//    }
//  }

  class PhotoFileFilter
          implements FileFilter
  {
    private Pattern pattern;

    PhotoFileFilter(int idper) {
      pattern = Pattern.compile("^" + idper + "\\.(jpg|jpeg|JPG|JPEG|png|PNG)$");
    }

    @Override
    public boolean accept(File pathname) {
      return pattern.matcher(pathname.getName()).matches();
    }
  }

}
