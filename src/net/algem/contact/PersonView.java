/*
 * @(#)PersonView.java	2.15.0 25/07/2017
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.member.PersonSubscriptionCard;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.DataException;
import net.algem.util.module.GemModule;
import net.algem.util.ui.*;

/**
 * Person view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 */
public class PersonView
  extends GemPanel {

  private static final PhotoHandler PHOTO_HANDLER = new SimplePhotoHandler(DataCache.getDataConnection());

  private GemNumericField no;
  private GemField name;
  private GemField firstname;
  private CivilChoice civil;
  private JCheckBox cbImgRights;
  private JCheckBox cbPartner;
  private GemField orgName;
  private Organization organization;
  private GemField nickname;

  /** Picture frame location. */
  private JLabel photoField;

  /** Rest info on subscription card. */
  private GemLabel cardLabel;

  /** Rest time on subscription card. */
  private GemLabel cardInfo;

  private GridBagHelper gb;

  private short ptype = Person.PERSON;

  private Contact person;
  private final OrganizationIO orgIO;

  public PersonView() {
    orgIO = new OrganizationIO(DataCache.getDataConnection());
  }

  protected void init() {
    no = new GemNumericField(6);
    no.setEditable(false);
    no.setBackground(Color.LIGHT_GRAY);
    no.setMinimumSize(new Dimension(60, no.getPreferredSize().height));

    orgName = new GemField(true, 20);
    orgName.setMinimumSize(new Dimension(200, orgName.getPreferredSize().height));
    orgName.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        int length = orgName.getText().length();
        if (length >= 2) {
          findOrg(orgName);
        }
      }
    });

    name = new GemField(true, 20);
    name.setMaxChars(32);
    name.setMinimumSize(orgName.getMinimumSize());
    firstname = new GemField(true, 20);
    firstname.setMaxChars(32);
    firstname.setMinimumSize(orgName.getMinimumSize());
    nickname = new GemField(true, 20);
    nickname.setMaxChars(64);
    nickname.setMinimumSize(orgName.getMinimumSize());
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
    JLabel btOrgDetails = new JLabel(ImageUtil.createImageIcon("document-properties-symbolic-2.png"));
    btOrgDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btOrgDetails.setToolTipText(BundleUtil.getLabel("Organization.details.tip"));
    btOrgDetails.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!orgName.getText().isEmpty()) {
          System.out.println(organization.getId());
          setUpOrganization(organization.getId(), orgName.getText());
        }
      }
    });
    gb.add(photoPanel, 0, 0, 1, 6, GridBagHelper.NORTHWEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Organization.label")), 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(btOrgDetails, 3, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Nickname.label")), 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Person.civility.label")), 1, 5, 1, 1, GridBagHelper.WEST);

    gb.add(no, 2, 0, 1, 1, GridBagHelper.WEST);
    gb.add(orgName, 2, 1, 1, 1, GridBagHelper.WEST);
    gb.add(name, 2, 2, 1, 1, GridBagHelper.WEST);
    gb.add(firstname, 2, 3, 1, 1, GridBagHelper.WEST);
    gb.add(nickname, 2, 4, 1, 1, GridBagHelper.WEST);
    gb.add(civil, 2, 5, 1, 1, GridBagHelper.WEST);
    gb.add(cbImgRights, 2, 6, 1, 1, GridBagHelper.WEST);
    gb.add(cbPartner, 2, 7, 1, 1, GridBagHelper.WEST);
    gb.add(cardPanel, 2, 8, 1, 1, GridBagHelper.WEST);
  }

  public void findOrg(GemField field) {
    List<Organization> orgs;
    try {
      orgs = orgIO.find(field.getText());
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return;
    }
    int size = orgs.size();
    if (size > 0) {
      if (size == 1) {
        field.setText(orgs.get(0).getName());
      } else {
       SelectOrgDlg selectOrgDlg = new SelectOrgDlg(PopupDlg.getTopFrame(this), true);
        selectOrgDlg.loadResult(orgs);
        selectOrgDlg.initUI();
        Organization o = selectOrgDlg.getOrg();
        if (o != null) {
          field.setText(o.getName());
          organization = o;
        }
      }
    }
  }

  void set(Person p, String dir) {
    this.person = new Contact(p);
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
    organization = new Organization();
    organization.setId(p.getOrgId());
    organization.setName(p.getOrgName());
    orgName.setText(p.getOrgName());
    ptype = p.getType();
    loadPhoto(p);
  }

  private void setUpOrganization(int orgId, String name) {
    final JDialog dlg = new JDialog((Frame) null, BundleUtil.getLabel("Organization.details.label"), true);

    dlg.setSize(440, 240);
    final OrganizationPanel orgPanel;
    try {
      if (orgId > 0) {
        organization = orgIO.findId(orgId);
        if (!organization.getName().toLowerCase().equals(name.toLowerCase().trim())) {
          organization = orgIO.findId(name);
          if (organization == null) {
            organization = new Organization(0);
            organization.setName(name);
          } else {
            person.setOrgId(organization.getId());
            person.setOrgName(organization.getName());
            orgName.setText(organization.getName());
          }
        }
      } else if (orgName != null && !orgName.getText().isEmpty()) {
        // check name if exists
//        organization = new Organization();
        organization.setName(orgName.getText());
      }
      orgPanel = new OrganizationPanel(organization.getId());
      orgPanel.createUI();
      orgPanel.setDetails(organization);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return;
    }

    GemPanel btPanel = new GemPanel(new GridLayout(1, 2));
    GemButton btSave = new GemButton(GemCommand.SAVE_CMD);
    btSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int idper = Integer.parseInt(no.getText());
        if (idper == 0) {
          MessagePopup.warning(null, MessageUtil.getMessage("organization.null.id.warning"));
          dlg.setVisible(false);
          return;
        }
        organization = orgPanel.getDetails();
        try {
          updateOrganization(organization, orgIO);
          dlg.setVisible(false);
        } catch (SQLException ex) {
          GemLogger.logException(ex);
          if ("23505".equals(ex.getSQLState())) {
            MessagePopup.warning(null, MessageUtil.getMessage("organization.unique.violation.exception"));
          } else {
            MessagePopup.warning(null, ex.getMessage());
          }
        }
      }
    });

    GemButton btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dlg.setVisible(false);
      }
    });
    btPanel.add(btSave);
    btPanel.add(btCancel);

    dlg.add(orgPanel, BorderLayout.CENTER);
    dlg.add(btPanel, BorderLayout.SOUTH);
    dlg.pack();
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
  }

  private class OrganizationPanel extends JPanel {

    private int orgId;
    private GemField name = new GemField(20);
    private GemField companyName = new GemField(20);
    private GemField siret = new GemField(20);
    private GemField naf = new GemField(20);
    private GemField codeFP = new GemField(20);
    private GemField vatId = new GemField(20);

    public OrganizationPanel(int orgId) {
      this.orgId = orgId;
    }

    private void createUI() {
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      GridBagHelper gb2 = new GridBagHelper(this);
      JLabel cpNameL = new JLabel(BundleUtil.getLabel("ConfEditor.corporate.label"));

      JLabel siretL = new JLabel(BundleUtil.getLabel("Organization.SIRET.label"));
      siretL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.SIRET.number.label"));
      JLabel nafL = new JLabel(BundleUtil.getLabel("Organization.NAF.label"));
      nafL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.Code.NAF.label"));
      JLabel codeFPL = new JLabel(BundleUtil.getLabel("Organization.FP.label"));
      codeFPL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.Code.FP.label"));
      JLabel vatL = new JLabel(BundleUtil.getLabel("Organization.VAT.label"));
      vatL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.Code.VAT.label"));

      gb2.add(new JLabel(BundleUtil.getLabel("Name.label")), 0, 0, 1, 1, GridBagHelper.WEST);
      gb2.add(cpNameL, 0, 1, 1, 1, GridBagHelper.WEST);
      gb2.add(siretL, 0, 2, 1, 1, GridBagHelper.WEST);
      gb2.add(nafL, 0, 3, 1, 1, GridBagHelper.WEST);
      gb2.add(codeFPL, 0, 4, 1, 1, GridBagHelper.WEST);
      gb2.add(vatL, 0, 5, 1, 1, GridBagHelper.WEST);

      gb2.add(name, 1, 0, 1, 1, GridBagHelper.WEST);
      gb2.add(companyName, 1, 1, 1, 1, GridBagHelper.WEST);
      gb2.add(siret, 1, 2, 1, 1, GridBagHelper.WEST);
      gb2.add(naf, 1, 3, 1, 1, GridBagHelper.WEST);
      gb2.add(codeFP, 1, 4, 1, 1, GridBagHelper.WEST);
      gb2.add(vatId, 1, 5, 1, 1, GridBagHelper.WEST);

    }

    void setDetails(Organization org) {
      name.setText(org.getName());
      if (org.getId() > 0) {
        companyName.setText(org.getCompanyName());
        siret.setText(org.getSiret());
        naf.setText(org.getNafCode());
        codeFP.setText(org.getFpCode());
        vatId.setText(org.getVatCode());
      }
    }

    Organization getDetails() {
      Organization org = new Organization();
      org.setId(orgId);//XXX
      org.setName(name.getText());
      org.setCompanyName(companyName.getText());
      org.setSiret(siret.getText());
      org.setNafCode(naf.getText());
      org.setFpCode(codeFP.getText());
      org.setVatCode(vatId.getText());

      return org;
    }

  }

  private void updateOrganization(Organization target, OrganizationIO orgIO) throws SQLException {
    Organization o = orgIO.findId(target.getId());
    if (o == null) {
      orgIO.create(target);
    } else {
      orgIO.update(target);
    }

  }

  private void loadPhoto(Person p) {
    if (p.getId() == 0) {
      return;
    }
    if (Person.PERSON == p.getType() || Person.ROOM == p.getType()) {
      BufferedImage img = ImageUtil.getPhoto(p.getId());
      ImageIcon icon = (img == null ? null : new ImageIcon(img));
      photoField.setIcon(icon);
    }
  }

  private void savePhoto(int idper) {
    if (idper == 0) {
      return;
    }
    try {
      File file = FileUtil.getFile(
        this,
        BundleUtil.getLabel("FileChooser.selection"),
        ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey()),
        MessageUtil.getMessage("filechooser.image.filter.label"),
        "jpg", "jpeg", "JPG", "JPEG", "png", "PNG");
      if (file != null) {
        BufferedImage img = PHOTO_HANDLER.saveFromFile(idper, file);
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

    pr.setOrgId(organization != null ? organization.getId() : 0);
    pr.setOrgName(organization.getName().trim());

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
    orgName.setText(null);
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

  class SelectOrgDlg
          extends JDialog
          implements ActionListener
  {

    private OrganizationTableModel model;
    private JTable table;
    private GemButton btOk, btCancel;
    private Frame parent;
    private Organization org;

    SelectOrgDlg(Frame owner, boolean modal) {
      super(owner, BundleUtil.getLabel("Organization.label"), modal);
      this.parent = owner;
      model = new OrganizationTableModel();
      table = new JTable(model);
    }

    void initUI() {
      table.getColumnModel().getColumn(0).setPreferredWidth(80);
      table.getColumnModel().getColumn(1).setPreferredWidth(240);
      GemPanel buttons = new GemPanel(new GridLayout(1, 2));
      JScrollPane scroll = new JScrollPane(table);
      add(scroll, BorderLayout.CENTER);
      btOk = new GemButton(GemCommand.OK_CMD);
      btOk.addActionListener(this);
      btCancel = new GemButton(GemCommand.CANCEL_CMD);
      btCancel.addActionListener(this);
      buttons.add(btOk);
      buttons.add(btCancel);
      add(buttons, BorderLayout.SOUTH);
      setSize(GemModule.XS_SIZE);
      setLocationRelativeTo(parent);
      setVisible(true);
    }

    void setCity() {
      int row = table.getSelectedRow();
      if (row >= 0) {
        org = (Organization) model.getItem(row);
      }
    }

    Organization getOrg() {
      return org;
    }

    public void loadResult(List<Organization> result) {
      for (Organization c : result) {
        model.addItem(c);
      }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == btOk) {
        setCity();
      } else {
        org = null;
      }
      setVisible(false);
    }
  }

  /**
   *
   * @param gl
   * @deprecated
   */
//  public void showGroups(Collection<Group> gl) {
//    if (gl.isEmpty()) {
//      return;
//    }
//    GemPanel groupsPanel = new GemPanel();
//    for (final Group g : gl) {
//      JButton jb = new JButton(g.getName());
//      jb.addActionListener(new ActionListener() {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//          GroupFileEditor groupEditor = new GroupFileEditor(g);
//          desktop.addModule(groupEditor);
//        }
//      });
//      groupsPanel.add(jb);
//    }
//    gb.add(groupsPanel, 0, 7, 1, 1, GridBagHelper.WEST);
//
//  }
}
