/*
 * @(#)ThemeConfig.java 2.9.4.12 29/09/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import net.algem.Algem;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Preferred theme selection.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 2.9.4.9 26/06/2015
 */
public class ThemeConfig
{

  public static Preferences THEME_PREF = Preferences.userRoot().node("/algem/theme");
  private String theme;
  private JDialog view;
  private JComboBox themes;
  private JTextArea description;
  private GemButton btOk;
  private GemButton btCancel;
  private JLabel imageLabel;

  public ThemeConfig(GemDesktop desktop) {
    view = new JDialog(desktop.getFrame());
    view.setTitle(BundleUtil.getLabel("Theme.modification.label"));
    view.setSize(new Dimension(710, 400));

    themes = new JComboBox(new ComboBoxThemeModel());
    Insets margin = new Insets(5, 5, 5, 5);
    description = new JTextArea();
    description.setEditable(false);
    description.setMargin(margin);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    themes.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e) {
        String name = ((LookAndFeelInfo) e.getItem()).getName();
        GemLogger.info(name);
        description.setText(getDescription(name));
        setPreview(name);
      }
    });
    JScrollPane sp = new JScrollPane(description);

    GemPanel t = new GemPanel(new FlowLayout());
    t.add(new GemLabel(BundleUtil.getLabel("Theme.label")));
    t.add(themes);

    JTextPane info = new JTextPane();
    info.setMargin(margin);
    info.setEditable(false);
    info.setFont(info.getFont().deriveFont(Font.BOLD));
    info.setText(MessageUtil.getMessage("laf.modification.info"));
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, info);

    splitPane.setResizeWeight(0.5);
    splitPane.setDividerLocation(0.6);

    GemPanel leftPane = new GemPanel();
    leftPane.setLayout(new BorderLayout());
    leftPane.setPreferredSize(new Dimension(220, 340));
    leftPane.add(t, BorderLayout.NORTH);
    leftPane.add(splitPane, BorderLayout.CENTER);

    imageLabel = new JLabel((Icon) null, SwingConstants.CENTER);
    GemPanel rightPane = new GemPanel(new BorderLayout());
    rightPane.setPreferredSize(new Dimension(220, 340));
    rightPane.add(new JLabel(BundleUtil.getLabel("Preview.label"), JLabel.CENTER), BorderLayout.NORTH);
    rightPane.add(imageLabel, BorderLayout.CENTER);

    JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
    mainPanel.setResizeWeight(0.5);
    mainPanel.setDividerLocation(0.5);

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    buttons.add(btOk = new GemButton(GemCommand.VALIDATE_CMD));
    buttons.add(btCancel = new GemButton(GemCommand.CANCEL_CMD));
    ActionListener listener = new ThemeListener();
    btOk.addActionListener(listener);
    btCancel.addActionListener(listener);

    view.setLayout(new BorderLayout());
    view.add(mainPanel, BorderLayout.NORTH);
    view.add(buttons, BorderLayout.SOUTH);

    view.setLocationRelativeTo(desktop.getFrame());
    view.setVisible(true);
  }

  /**
   * Gets the selected theme.
   *
   * @return the name of selected look and feel
   */
  public String getTheme() {
    return (String) themes.getSelectedItem();
  }

  /**
   * Sets the last stored theme.
   *
   * @param name of the look and feel
   */
  private void setActualTheme(String name) {
    for (int i = 0; i < themes.getItemCount(); i++) {
      LookAndFeelInfo item = (LookAndFeelInfo) themes.getItemAt(i);
      if (item.getClassName().equals(name)) {
        themes.setSelectedIndex(i);
        break;
      }
    }
  }

  public void setLAF() {
    for (UIManager.LookAndFeelInfo look : UIManager.getInstalledLookAndFeels()) {
      themes.addItem(new GemLafInfo(look.getName(), look.getClassName()));
    }
    List<LookAndFeelInfo> alt = Algem.getAlternativeLaf();
    if (alt.size() > 0) {
      Collections.sort(alt, new Comparator<LookAndFeelInfo>() {
        @Override
        public int compare(LookAndFeelInfo o1, LookAndFeelInfo o2) {
          return o1.toString().compareTo(o2.toString());
        }

      });
      themes.addItem(new GemLafInfo("**"+MessageUtil.getMessage("alternative.themes.item")+"**", ""));
      for (LookAndFeelInfo look : alt) {
        themes.addItem(look);
      }
    }
    String actualLaF = UIManager.getLookAndFeel().getClass().getName();
    GemLogger.info(actualLaF);
    //GemLogger.info(UIManager.getLookAndFeel().getName());

    setActualTheme(actualLaF);
  }

  /**
   * Gets the description of the selected look and feel.
   *
   * @param name look and feel name
   * @return a i18n-string
   */
  private String getDescription(String name) {
    switch (name) {
      case "CDE/Motif":
        return MessageUtil.getMessage("laf.description.motif");
      case "Metal":
        return MessageUtil.getMessage("laf.description.metal");
      case "GTK+":
        return MessageUtil.getMessage("laf.description.gtk");
      case "Windows":
        return MessageUtil.getMessage("laf.description.windows");
      case "Windows XP":
        return MessageUtil.getMessage("laf.description.xp");
      case "Windows Vista":
        return MessageUtil.getMessage("laf.description.vista");
      case "Macintosh":
        return MessageUtil.getMessage("laf.description.mac");
      case "Liquid":
        return MessageUtil.getMessage("laf.description.liquid");
      case "Nimbus":
        return MessageUtil.getMessage("laf.description.nimbus");
      case "Acryl":
        return MessageUtil.getMessage("laf.description.acryl");
      case "Aero":
        return MessageUtil.getMessage("laf.description.aero");
      case "Aluminium":
        return MessageUtil.getMessage("laf.description.aluminium");
      case "Bernstein":
        return MessageUtil.getMessage("laf.description.bernstein");
      case "Fast":
        return MessageUtil.getMessage("laf.description.fast");
      case "Graphite":
        return MessageUtil.getMessage("laf.description.graphite");
      case "Luna":
        return MessageUtil.getMessage("laf.description.luna");
      case "Mint":
        return MessageUtil.getMessage("laf.description.mint");
      case "Smart":
        return MessageUtil.getMessage("laf.description.smart");
      case "Texture":
        return MessageUtil.getMessage("laf.description.texture");
      case "JGoodiesPlastic":
        return MessageUtil.getMessage("laf.description.jgoodies.plastic");
      case "JGoodiesPlastic3D":
        return MessageUtil.getMessage("laf.description.jgoodies.plastic.3d");

      default:
        return name;
    }
  }

  /**
   * Displays a preview of the selected Look and feel.
   *
   * @param lafName look and file name
   */
  private void setPreview(String lafName) {
    String fileName;
    switch (lafName) {
      case "CDE/Motif":
        fileName = "laf.CDE.png";
        break;
      case "Windows Classic":
        fileName = "laf.WindowsClassic.png";
        break;
      case "Windows XP":
        fileName = "laf.WindowsXP.png";
        break;
      case "JGoodies Plastic 3D":
        fileName = "laf.JGoodiesPlastic3D.png";
        break;
      case "JGoodies Plastic":
        fileName = "laf.JGoodiesPlastic.png";
        break;
      default:
        System.out.println("laf." + lafName + ".png");
        fileName = "laf." + lafName + ".png";
        break;
    }
    ImageIcon img = ImageUtil.createImageIcon(fileName);
    if (img == null) {
      img = ImageUtil.createImageIcon("laf.help.png");
    }
    imageLabel.setIcon(img);
  }

  /**
   * Private listener for theme selection.
   */
  private class ThemeListener
          implements ActionListener
  {

    public void setTheme(LookAndFeelInfo laf) {
      Algem.setLafProperties(laf.getClassName());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == btOk) {
        LookAndFeelInfo laf = (LookAndFeelInfo) themes.getSelectedItem();
//        setTheme(laf);
        THEME_PREF.put("theme", laf.getClassName());
        MessagePopup.information(view, MessageUtil.getMessage("laf.modification.warning"));
      }
      view.setVisible(false);
    }

  }

  /**
   * This subclass of LookAndFeelInfo only redefines the toString method.
   */
  public static class GemLafInfo
          extends LookAndFeelInfo
  {

    public GemLafInfo(String name, String className) {
      super(name, className);
    }

    @Override
    public String toString() {
      return getName();
    }
  }

  private class ComboBoxThemeModel extends DefaultComboBoxModel<GemLafInfo> {
    public ComboBoxThemeModel() {}
    public ComboBoxThemeModel(Vector<GemLafInfo> items) {
        super(items);
    }
    @Override
    public void setSelectedItem(Object item) {
        if (item.toString().startsWith("**"))
            return;
        super.setSelectedItem(item);
    };
}


}
