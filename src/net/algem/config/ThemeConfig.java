/*
 * @(#)ThemeConfig.java 2.9.4.0 26/06/2015
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import net.algem.Algem;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 26/06/2015
 */
public class ThemeConfig
{

  public static Preferences THEME_PREF = Preferences.userRoot().node("/algem/theme");
  private String theme;
  private JDialog view;
  private JComboBox themes;
  private GemButton btOk;
  private GemButton btCancel;
  private static final GemLafInfo[] additionalLAF = {
    new GemLafInfo("Liquid", "com.birosoft.liquid.LiquidLookAndFeel","Mosfet Liquid KDE 3.x theme"),
    //  new GemLAF("SeaGlass","com.seaglasslookandfeel.SeaGlassLookAndFeel"),
    new GemLafInfo("Acrylique", "com.jtattoo.plaf.acryl.AcrylLookAndFeel", "JTattoo Acrilyque"),
    new GemLafInfo("Smart", "com.jtattoo.plaf.smart.SmartLookAndFeel", "JTattoo Smart"),
    new GemLafInfo("Aero", "com.jtattoo.plaf.aero.AeroLookAndFeel", "JTattoo Aero"),
    new GemLafInfo("Aluminium", "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel", "JTattoo Aluminium"),
    new GemLafInfo("Bernstein", "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel", "JTattoo Bernstein"),
    new GemLafInfo("Fast", "com.jtattoo.plaf.fast.FastLookAndFeel", "JTattoo Fast"),
    new GemLafInfo("Graphite", "com.jtattoo.plaf.graphite.GraphiteLookAndFeel", "JTattoo Graphite"), //new GemLAF("HiFi","com.jtattoo.plaf.hifi.HiFiLookAndFeel"),
  //new GemLAF("Luna","com.jtattoo.plaf.luna.LunaLookAndFeel"),
  //new GemLAF("Mint","com.jtattoo.plaf.mint.MintLookAndFeel"),
  //new GemLAF("Noire","com.jtattoo.plaf.noire.NoireLookAndFeel")
  };

  public ThemeConfig(GemDesktop desktop) {
    view = new JDialog(desktop.getFrame());
    themes = new JComboBox();
    final JTextArea description = new JTextArea(4, 20);
    themes.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        description.setText(((GemLafInfo) themes.getSelectedItem()).getDescription());
      }
    
    });
    
    view.setLayout(new BorderLayout());
    view.setTitle("Theme modification");
    GemPanel t = new GemPanel(new FlowLayout());
    t.add(new GemLabel(BundleUtil.getLabel("Theme.label")));
    t.add(themes);
    view.add(t, BorderLayout.NORTH);
    GemPanel d = new GemPanel();
    d.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    d.add(description);
    view.add(d, BorderLayout.CENTER);
    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    buttons.add(btOk = new GemButton(GemCommand.VALIDATE_CMD));
    buttons.add(btCancel = new GemButton(GemCommand.CANCEL_CMD));
    ActionListener listener = new ThemeListener();
    btOk.addActionListener(listener);
    btCancel.addActionListener(listener);

    view.add(buttons, BorderLayout.SOUTH);
    view.setSize(new Dimension(300,200));
    view.setLocationRelativeTo(desktop.getFrame());

    view.setVisible(true);
  }

  public String getTheme() {
    return (String) themes.getSelectedItem();
  }
  
  private void setTheme(String name) {
    

      for (int i = 0 ; i < themes.getItemCount(); i++) {
        LookAndFeelInfo item = (LookAndFeelInfo) themes.getItemAt(i);

      if (item.getName().equals(name)) {
        themes.setSelectedIndex(i);
        break;
      }
    }
  }

  private LookAndFeelInfo[] getInstalledLAF() {
    return UIManager.getInstalledLookAndFeels();
  }

  public void setLAF() {
    for (UIManager.LookAndFeelInfo look : getInstalledLAF()) {
      themes.addItem(new GemLafInfo(look.getName(), look.getClassName(), getDescription(look.getName())));
    }
    for (LookAndFeelInfo look : additionalLAF) {
      themes.addItem(look);
    }
    System.out.println(UIManager.getLookAndFeel().getClass().getName());
    setTheme(UIManager.getLookAndFeel().getName());
  }
  
  private String getDescription(String name) {
    switch(name) {
      case "Metal" : return "Thème par défaut";
      case "GTK+" : return "Thème Solaris, Linux GTK+ 2.2";
      case "Motif" : return "Thème Solaris, Linux inspiré du gestionnaire de bureaux Motif";
      case "Windows" :
      case "Windows XP":
      case "Windows Vista":
        return "Thème Windows natif";
      case "Macintosh" : return "Thème Mac natif";
    }
    return name;
  }

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
        setTheme(laf);
        THEME_PREF.put("theme", laf.getClassName());
      }
      view.setVisible(false);
    }

  }

  private static class GemLafInfo
          extends LookAndFeelInfo
  {
    private String description;

    public GemLafInfo(String name, String className, String desc) {
      super(name, className);
      this.description = desc;
    }

    public String getDescription() {
      return description;
    }

    @Override
    public String toString() {
      return getName();
    }
  }

}
