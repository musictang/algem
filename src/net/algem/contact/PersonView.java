/*
 * @(#)PersonView.java	2.11.1 07/10/16
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
package net.algem.contact;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.member.PersonSubscriptionCard;
import net.algem.group.Group;
import net.algem.group.GroupFileEditor;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.DataException;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Person view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 */
public class PersonView
  extends GemPanel {

  private static final PhotoHandler photoHandler = new SimplePhotoHandler(DataCache.getDataConnection());

  private GemNumericField no;
  private GemField name;
  private GemField firstname;
  private CivilChoice civil;
  private JCheckBox cbImgRights;
  private JCheckBox cbPartner;
  private GemField organization;
  private GemField nickname;

  /** Picture frame location. */
  private JLabel photoField;

  /** Rest info on subscription card. */
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
    no.setMinimumSize(new Dimension(60, no.getPreferredSize().height));

    organization = new GemField(true, 20);
    organization.setMinimumSize(new Dimension(200, organization.getPreferredSize().height));

    name = new GemField(true, 20);
    name.setMaxChars(32);
    name.setMinimumSize(organization.getMinimumSize());
    firstname = new GemField(true, 20);
    firstname.setMaxChars(32);
    firstname.setMinimumSize(organization.getMinimumSize());
    nickname = new GemField(true, 20);
    nickname.setMaxChars(64);
    nickname.setMinimumSize(organization.getMinimumSize());
    civil = new CivilChoice();

    cbImgRights = new JCheckBox(BundleUtil.getLabel("Person.img.rights.label"));
    cbImgRights.setToolTipText(BundleUtil.getLabel("Person.img.rights.tip"));
    cbImgRights.setBorder(null);
    cbPartner = new JCheckBox(BundleUtil.getLabel("Partner.info.label"));
    cbPartner.setToolTipText(BundleUtil.getLabel("Partner.info.tip"));
    cbPartner.setBorder(null);
    GemPanel cardPanel = new GemPanel();
    cardLabel = new GemLabel();
    cardInfo = new GemLabel();
    cardPanel.add(cardLabel);
    cardPanel.add(cardInfo);

    this.setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
    GemBorderPanel photoPanel = new GemBorderPanel(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    photoField = new JLabel();
    photoPanel.add(photoField);
    photoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    photoPanel.setToolTipText(BundleUtil.getLabel("Photo.add.tip"));
    photoPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent m) {
        savePhoto(Integer.parseInt(no.getText()));
      }
    });

    gb.add(photoPanel, 0, 0, 1, 6, GridBagHelper.NORTHWEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Organization.label")), 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Nickname.label")), 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Person.civility.label")), 1, 5, 1, 1, GridBagHelper.WEST);

    gb.add(no, 2, 0, 1, 1, GridBagHelper.WEST);
    gb.add(organization, 2, 1, 1, 1, GridBagHelper.WEST);
    gb.add(name, 2, 2, 1, 1, GridBagHelper.WEST);
    gb.add(firstname, 2, 3, 1, 1, GridBagHelper.WEST);
    gb.add(nickname, 2, 4, 1, 1, GridBagHelper.WEST);
    gb.add(civil, 2, 5, 1, 1, GridBagHelper.WEST);
    gb.add(cbImgRights, 2, 6, 1, 1, GridBagHelper.WEST);
    gb.add(cbPartner, 2, 7, 1, 1, GridBagHelper.WEST);
    gb.add(cardPanel, 2, 8, 1, 1, GridBagHelper.WEST);
  }

  void set(Person p, String dir) {
    no.setText(String.valueOf(p.getId()));
    if (p.getType() == Person.ROOM) {
      no.setToolTipText(MessageUtil.getMessage("person.view.change.contact.tip"));
    }
    name.setText(p.getName());
    firstname.setText(p.getFirstName());
    nickname.setText(p.getNickName());
    civil.setSelectedItem(p.getGender());
    cbImgRights.setSelected(p.hasImgRights());
    cbPartner.setSelected(p.isPartnerInfo());
    organization.setText(p.getOrganization());
    ptype = p.getType();
    loadPhoto(p);
  }

  private void loadPhoto(Person p) {
    if (p.getId() == 0) return;
    if (p.getType() == Person.PERSON || p.getType() == Person.ROOM) {
      BufferedImage img = photoHandler.load(p.getId());
      if (img == null) {
        photoFilter = new PhotoFileFilter(p.getId());
        BufferedImage orig = getPhoto(ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey()), p.getId());
        if (orig != null) {
          try {
            img = photoHandler.saveFromBuffer(p.getId(), orig);
          } catch (DataException ex) {
            GemLogger.log(ex.getMessage());
          }
        } else {
          img = getPhotoDefault();
        }
      }
      ImageIcon icon = (img == null ? null : new ImageIcon(img));
      photoField.setIcon(icon);
    }
  }

  private void savePhoto(int idper) {
    if (idper == 0) return;
    try {
      File file = FileUtil.getFile(
        this,
        BundleUtil.getLabel("FileChooser.selection"),
        ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey()),
        MessageUtil.getMessage("filechooser.image.filter.label"),
        "jpg", "jpeg", "JPG", "JPEG", "png", "PNG");
      if (file != null) {
        BufferedImage img = photoHandler.saveFromFile(idper, file);
        if (img != null) {
          photoField.setIcon(new ImageIcon(img));
        }
      }
    } catch (DataException ex) {
      GemLogger.logException(ex);
    }

  }

  /**
   * Gets an image icon with photo id of the current contact.
   *
   * @param orig original photo image
   * @return an image icon or null if no image has been processed
   * @deprecated
   */
  private ImageIcon getImageIcon(final BufferedImage orig) {
    ImageIcon icon = null;
    try {
      //InputStream is = getClass().getResourceAsStream(url.getPath());
      //BufferedImage bi1 = ImageIO.read(is);
      BufferedImage buffered = orig;
      //if (ImageUtil.PHOTO_ID_WIDTH != bi1.getWidth() || ImageUtil.PHOTO_ID_HEIGHT != bi1.getHeight()) {
      if (buffered.getHeight() > ImageUtil.PHOTO_ID_HEIGHT) {
        //System.out.println("rescaling !");
        buffered = ImageUtil.rescale(buffered);
        buffered = ImageUtil.cropPhotoId(buffered);
//
//        String dest = url.getFilePath();
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

  /**
   * Gets the photo of the person {@code idper} from {@code configDir}.
   *
   * @param configDir photo dir
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
      } 
      /*else { // default resource path USELESS
        for (String s : ImageUtil.DEFAULT_IMG_EXTENSIONS) {
          InputStream input = getClass().getResourceAsStream(ImageUtil.PHOTO_PATH + idper + s);
          if (input == null) {
            input = getClass().getResourceAsStream(ImageUtil.DEFAULT_PHOTO_ID);
          }
          return ImageIO.read(input);
        }
      }*/
    } catch (IOException ie) {
      GemLogger.logException(ie);
    } catch (IllegalArgumentException ia) {
      GemLogger.logException(ia);
    }
    return null;
  }

  private BufferedImage getPhotoDefault() {
    try {
      InputStream input = getClass().getResourceAsStream(ImageUtil.DEFAULT_PHOTO_ID);
      return ImageIO.read(input);
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }

  void showSubscriptionRest(PersonSubscriptionCard card) {
    if (card != null) {
      cardLabel.setText(BundleUtil.getLabel("Subscription.remaining.label") + " : ");
      cardInfo.setText(card.displayRestTime());
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
    organization.setText(null);
    firstname.setText("");
    nickname.setText("");
    civil.setSelectedIndex(0);
    cbImgRights.setSelected(false);
    cbPartner.setSelected(false);
    photoField.setText("");
  }

  public void filter(int f) {
    if (Person.ESTABLISHMENT == f) {
      firstname.setEnabled(false);
      civil.setEnabled(false);
      cbImgRights.setEnabled(false);
      cbPartner.setEnabled(false);
      nickname.setEnabled(false);
    }
  }

  public void showGroups(Collection<Group> gl) {
    if (gl.isEmpty()) {
      return;
    }
    GemPanel groupsPanel = new GemPanel();
    for (final Group g : gl) {
      JButton jb = new JButton(g.getName());
      jb.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          GroupFileEditor groupEditor = new GroupFileEditor(g);
          desktop.addModule(groupEditor);
        }
      });
      groupsPanel.add(jb);
    }
    gb.add(groupsPanel, 0, 7, 1, 1, GridBagHelper.WEST);

  }
  
  class PhotoFileFilter
          implements FileFilter
  {
    private final Pattern pattern;

    PhotoFileFilter(int idper) {
      pattern = Pattern.compile("^.*" + idper + "\\.(jpg|jpeg|JPG|JPEG|png|PNG)$");
    }

    @Override
    public boolean accept(File pathname) {
      return pattern.matcher(pathname.getName()).matches();
    }
  }

}
