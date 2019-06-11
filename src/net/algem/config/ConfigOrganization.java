/*
 * @(#)ConfigOrganization.java 2.15.8 14/03/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.MaskFormatter;
import net.algem.contact.Address;
import net.algem.contact.AddressView;
import net.algem.contact.Organization;
import net.algem.contact.OrganizationIO;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Organization parameters and contact.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.2.p 23/01/12
 */
public class ConfigOrganization
  extends ConfigPanel {

  private static final int MAX_LOGO_WIDTH = 200;
  private static final int MAX_LOGO_HEIGHT = 200;

  private Config c10;

  /** Organization name. */
  //private GemField name;
  private GemButton btName;
  private JFormattedTextField siret;
  private JFormattedTextField naf;
  private JFormattedTextField forPro;
  private JFormattedTextField tva;
  private JLabel logo;
  private JLabel stamp;
  private JButton btLogo;
  private JButton btStamp;

  /** Address. */
  private AddressView address;

  /** Domain name. */
  private JTextField domainName;

  private OrganizationIO orgIO;
  private GemDesktop desktop;

  public ConfigOrganization(String title, Map<String, Config> cm, OrganizationIO orgIO, GemDesktop desktop) {
    super(title, cm);
    this.desktop = desktop;
    this.orgIO = orgIO;
    init();
  }

  private void init() {
    try {
      final Company comp = orgIO.getDefault();
      final int idper = comp.getContact().getId();
      content = new GemPanel(new BorderLayout());

      JPanel panel = new JPanel(new GridBagLayout());
      GridBagHelper gb = new GridBagHelper(panel);
      btName = new GemButton(comp.getContact().getOrganization().getName());
      btName.setPreferredSize(new Dimension(200, btName.getPreferredSize().height));
      btName.setToolTipText(MessageUtil.getMessage("open.company.profile.tip"));
      btName.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (idper > 0) {
            showContactFile(idper);
          } else {
            MessagePopup.warning(desktop.getFrame(), MessageUtil.getMessage("company.no.contact.warning", comp.getOrg().getName()));
          }
        }
      });
      Organization org = comp.getOrg();
      MaskFormatter siretMask = MessageUtil.createFormatter("### ### ### #####");
      siretMask.setValueContainsLiteralCharacters(false);
      siret = new JFormattedTextField(siretMask);
      Dimension fieldDim = new Dimension(200, siret.getPreferredSize().height);
      siret.setPreferredSize(fieldDim);
      siret.setEditable(false);
      siret.setValue(org.getSiret());

      JTextField referent = new JTextField();
      referent.setEditable(false);
      referent.setPreferredSize(fieldDim);
      referent.setText(comp.getReferent().getFirstnameName());

      naf = new JFormattedTextField(MessageUtil.createFormatter("AAAAA"));
      naf.setPreferredSize(fieldDim);
      naf.setEditable(false);
      naf.setValue(org.getNafCode());

      MaskFormatter forProMask = MessageUtil.createFormatter("## ## ##### ##");
      forProMask.setValueContainsLiteralCharacters(false);
      forPro = new JFormattedTextField(forProMask);
      forPro.setPreferredSize(fieldDim);
      forPro.setEditable(false);
      forPro.setValue(org.getFpCode());

      MaskFormatter tvaMask = MessageUtil.createFormatter("AA AA AAA AAA AAA AAAAA");
      tvaMask.setValueContainsLiteralCharacters(false);
      tva = new JFormattedTextField(tvaMask);
      tva.setPreferredSize(fieldDim);
      tva.setEditable(false);
      tva.setValue(org.getVatCode());

      domainName = new JTextField();
      domainName.setPreferredSize(fieldDim);
      domainName.setText(comp.getDomain());
      c10 = new Config(ConfigKey.ORGANIZATION_DOMAIN.getKey(), comp.getDomain());

      address = new AddressView(null, false);
      Address a = comp.getContact().getAddress();
      address.set(a);
      address.setEditable(false);

      gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 0, 1, 1, GridBagHelper.WEST);
      gb.add(btName, 1, 0, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("Referent.label")), 0, 1, 1, 1, GridBagHelper.WEST);
      gb.add(referent, 1, 1, 1, 1, GridBagHelper.WEST);

      JLabel siretL = new JLabel(BundleUtil.getLabel("Organization.SIRET.label"));
      siretL.setToolTipText(ConfigKey.SIRET_NUMBER.getLabel());
      gb.add(siretL, 0, 2, 1, 1, GridBagHelper.WEST);
      gb.add(siret, 1, 2, 1, 1, GridBagHelper.WEST);
      JLabel nafL = new JLabel(BundleUtil.getLabel("Organization.NAF.label"));
      nafL.setToolTipText(ConfigKey.CODE_NAF.getLabel());
      gb.add(nafL, 0, 3, 1, 1, GridBagHelper.WEST);
      gb.add(naf, 1, 3, 1, 1, GridBagHelper.WEST);
      JLabel fpL = new JLabel(BundleUtil.getLabel("Organization.FP.label"));
      fpL.setToolTipText(ConfigKey.CODE_FP.getLabel());
      gb.add(fpL, 0, 4, 1, 1, GridBagHelper.WEST);
      gb.add(forPro, 1, 4, 1, 1, GridBagHelper.WEST);
      JLabel tvaL = new JLabel(BundleUtil.getLabel("Organization.VAT.label"));
      tvaL.setToolTipText(ConfigKey.CODE_TVA.getLabel());
      gb.add(tvaL, 0, 5, 1, 1, GridBagHelper.WEST);
      gb.add(tva, 1, 5, 1, 1, GridBagHelper.WEST);
      JLabel domainL = new GemLabel(ConfigKey.ORGANIZATION_DOMAIN.getLabel());
      domainL.setToolTipText(BundleUtil.getLabel("ConfEditor.organization.domain.tip"));
      gb.add(domainL, 0, 6, 1, 1, GridBagHelper.WEST);
      gb.add(domainName, 1, 6, 1, 1, GridBagHelper.WEST);

      gb.add(Box.createVerticalStrut(20), 0, 7, 2, 1);
      gb.add(address, 0, 8, 2, 1);

      content.add(panel, BorderLayout.CENTER);

      JPanel imagePanel = new JPanel(new GridLayout(1, 2, 10, 0));
      imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
      Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
      final ImageIcon defIcon = ImageUtil.createImageIcon("image-x-generic-symbolic.png");
      MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          Object src = e.getSource();
          if (src instanceof JLabel) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (src == logo) {
              saveThumbnail(logo, OrganizationIO.LOGO_COL);
            } else {
              saveThumbnail(stamp, OrganizationIO.STAMP_COL);
            }
            setCursor(Cursor.getDefaultCursor());
          }
        }

      };
      ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          Object src = e.getSource();
          if (src instanceof JButton) {
            try {
              if (src == btLogo) {
                resetThumbnail(logo, OrganizationIO.LOGO_COL, defIcon);
              } else {
                resetThumbnail(stamp, OrganizationIO.STAMP_COL, defIcon);
              }
            } catch (SQLException ex) {
              GemLogger.logException(ex);
            }
          }
        }
      };
      JPanel p1 = new JPanel(new BorderLayout());
      p1.add(new JLabel(BundleUtil.getLabel("Logo.label"), SwingConstants.CENTER), BorderLayout.NORTH);
      logo = new JLabel("", SwingConstants.CENTER);
      logo.setToolTipText(GemCommand.DEFINE_CMD);

      logo.setIcon(getThumbnail(comp, OrganizationIO.LOGO_COL, defIcon));
      logo.setCursor(hand);
      logo.addMouseListener(mouseAdapter);
      p1.add(logo, BorderLayout.CENTER);

      JPanel btPanel1 = new JPanel();
      btLogo = new GemButton(GemCommand.DELETE_CMD);
      btLogo.addActionListener(listener);
      btPanel1.add(btLogo);
      p1.add(btPanel1, BorderLayout.SOUTH);

      JPanel p2 = new JPanel(new BorderLayout());
//     p2.setPreferredSize(logoSize);
      p2.add(new JLabel(BundleUtil.getLabel("Stamp.label"), SwingConstants.CENTER), BorderLayout.NORTH);
      stamp = new JLabel("", SwingConstants.CENTER);
      stamp.setToolTipText(GemCommand.DEFINE_CMD);
      Icon tb = getThumbnail(comp, OrganizationIO.STAMP_COL, defIcon);
      stamp.setIcon(getThumbnail(comp, OrganizationIO.STAMP_COL, defIcon));
      stamp.setSize(tb.getIconWidth(), tb.getIconHeight());
      stamp.setCursor(hand);
      stamp.addMouseListener(mouseAdapter);
      p2.add(stamp, BorderLayout.CENTER);
      JPanel btPanel2 = new JPanel();
      btStamp = new GemButton(GemCommand.DELETE_CMD);
      btStamp.addActionListener(listener);
      btPanel2.add(btStamp);
      p2.add(btPanel2, BorderLayout.SOUTH);

      imagePanel.add(p1);
      imagePanel.add(p2);

      content.add(imagePanel, BorderLayout.SOUTH);

      add(content);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c10.setValue(domainName.getText());
    conf.add(c10);
    return conf;
  }

  private Icon getThumbnail(Company comp, String type, Icon def) {
    try {
      byte[] data = null;
      if (OrganizationIO.LOGO_COL.equals(type)) {
        data = comp.getLogo();
      } else {
        data = comp.getStamp();
      }
      if (data == null) {
        return def;
      }
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      BufferedImage img = ImageIO.read(in);
      return ImageUtil.getRescaledIcon(img, MAX_LOGO_WIDTH, MAX_LOGO_HEIGHT);
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return def;
    }

  }

  private void resetThumbnail(JLabel label, String type, Icon def) throws SQLException {
    orgIO.saveImage(type, null);
    label.setIcon(def);
  }

  private void saveThumbnail(JLabel label, String type) {
    File file = FileUtil.getFile(
      this,
      BundleUtil.getLabel("FileChooser.selection"),
      ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey()),
      MessageUtil.getMessage("filechooser.image.filter.label"),
      "jpg", "jpeg", "JPG", "JPEG", "png", "PNG");
    if (file != null) {
      try {
        byte[] data = Files.readAllBytes(file.toPath());
        System.out.println("img data  " + data.length);
        if (data.length > 1024 * 1024) { //1Mo
          MessagePopup.warning(this, MessageUtil.getMessage("file.too.large.warning", "1 Mo"));
          return;
        }
        orgIO.saveImage(type, data);
        BufferedImage img = ImageIO.read(file);
        if (img != null) {
          label.setIcon(ImageUtil.getRescaledIcon(img, MAX_LOGO_HEIGHT, MAX_LOGO_HEIGHT));
        }
      } catch (IOException | SQLException e) {
        GemLogger.logException(e);
      }
    }

  }



  private void showContactFile(int id) {
    PersonFileEditor editor = ((GemDesktopCtrl) desktop).getPersonFileEditor(id);
    if (editor != null) {
      desktop.setSelectedModule(editor);
    } else {
      try {
        desktop.setWaitCursor();
        PersonFile pf = (PersonFile) DataCache.findId(id, Model.PersonFile);
        editor = new PersonFileEditor(pf);
        desktop.addModule(editor);
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      } finally {
        desktop.setDefaultCursor();
      }
    }
  }

}
