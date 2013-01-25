/*
 * @(#)Algem.java	2.7.d 24/01/13
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
package net.algem;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.*;
import net.algem.security.User;
import net.algem.util.*;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Main class.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.d
 */
public class Algem
{

  public static final String APP_VERSION = "2.7.d";
  private static final int DEF_WIDTH = 1080;// (850,650) => ancienne taille
  private static final int DEF_HEIGHT = 780;
  private static final Point DEF_LOCATION = new Point(70, 30);
  
  private JFrame frame;
  private DataCache cache;
  private User user;
  private String driverName = "org.postgresql.Driver";
  private String hostName = "localhost";
  private String baseName = "algem";
  private GemDesktopCtrl desktop;
  private Properties props;
  private DataConnection dc;

  public Algem() {
    Locale.setDefault(Locale.FRENCH);
    props = new Properties();
  }

  private void init(String configFile, final String host, final String base, String login) {

    final GemBoot gemBoot = new GemBoot();
    // opening configuration file
    try {
      props.load(new FileInputStream(configFile));
    } catch (IOException e) {
      MessagePopup.error(gemBoot.getFrame(), e.getMessage() + ">>" + System.getProperty("user.dir"));
      System.exit(1);
    }

    // optional properties file $HOME/.algem/preferences
    setUserProperties();

    setLocale(props);

    /* -------------------------- */
    /* Initialisation driver JDBC */
    /* -------------------------- */
    try {
      setDB(host, base, props);
    } catch (SQLException ex) {
      MessagePopup.error(frame, ex.getMessage());
      System.exit(4);
    }
    /* -------------------------- */
    /* Logger initialisation      */
    /* -------------------------- */
    //String logPath = ConfigUtil.getConf(ConfigKey.LOG_PATH.getKey(), dc);
    URL url = getClass().getResource("/Journaux/algem.log");
    String msg = "Algem version " + APP_VERSION + "\nJava version " + System.getProperty("java.version");
    try {
      if (url != null) {
        GemLogger.set(url.getPath());
        GemLogger.log(Level.INFO, "net.algem.Algem", "main", msg);
      } 
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    }

    cache = DataCache.getInstance(dc, login);
    
    /* ------------------------ */
    /* Test login user validity */
    /* ------------------------ */
    checkUser(login);
    
    cache.load(gemBoot);

    /* ------------------------------------------------ */
    /* Creates the frame of the application             */
    /* ------------------------------------------------ */
    setDesktop();
    
    gemBoot.close();

  }

  private void setDesktop() {
    frame = new JFrame("Algem/" + props.getProperty("appClient") + "(" + APP_VERSION + ") jdbc://" + hostName + "/" + baseName);
    frame.setSize(DEF_WIDTH, DEF_HEIGHT);
    frame.setLocation(DEF_LOCATION);
    checkVersion(frame);

    desktop = new GemDesktopCtrl(frame, cache, props);
    frame.setVisible(true);
  }

  private void setUserProperties() {

    try {
      String prefix = System.getProperty("user.home") + FileUtil.FILE_SEPARATOR + ".algem" + FileUtil.FILE_SEPARATOR;
      Properties p = new Properties();
      p.load(new FileInputStream(prefix + "preferences"));
      props.putAll(p);
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
      MessagePopup.error(null, e.toString());
      System.exit(2);
    }
  }

  private void setLocale(Properties props) {
    String language = props.getProperty("language");
    String country = props.getProperty("pays");
    if (language != null && country != null) {
      Locale.setDefault(new Locale(language, country));
    }
  }

  private void setDB(String host, String base, Properties props) throws SQLException {

    String s = props.getProperty("driver");
    if (s != null) {
      driverName = s;
    }

    if (host == null) {
      host = props.getProperty("host");
    }

    if (base == null) {
      base = props.getProperty("base");
    }

    if (host == null && base == null) {
      dc = new DataConnection();
    } else if (host == null) {
      dc = new DataConnection(DataConnection.DEF_HOST, base);
    } else if (base == null) {
      dc = new DataConnection(host, DataConnection.DEF_DB_NAME);
    } else {
      dc = new DataConnection(host, base);
    }

    dc.connect();
  }

  private void checkUser(String u) {
    if (cache.getUser() == null) {
      MessagePopup.error(null, MessageUtil.getMessage("unknown.login", u));
      System.exit(5);
    }
  }

  private void checkVersion(JFrame frame) {
    String v = cache.getVersion();
    if (!v.equals(APP_VERSION)) {
      String mv = MessageUtil.getMessage("version.create.info", APP_VERSION);
      try {
        dc.setAutoCommit(false);
        updateVersionFrom(v);
        mv += MessageUtil.getMessage("update.info");
        JOptionPane.showMessageDialog(frame,
                mv, MessageUtil.getMessage("version.update.label"),
                JOptionPane.INFORMATION_MESSAGE);
        dc.commit();
      } catch (SQLException ex) {
        dc.rollback();
        mv += MessageUtil.getMessage("version.update.exception");
        mv += ex.getMessage();
        JOptionPane.showMessageDialog(frame,
                mv, MessageUtil.getMessage("version.update.label"),
                JOptionPane.ERROR_MESSAGE);
        System.exit(5);
      } finally {
        dc.setAutoCommit(true);
      }
    }
  }

  public void initUserDir(String path, Properties _props) throws Exception {
    throw new Exception("initUserDir Not Implemented");
  }

  /**
   * 
   * @param v
   * @throws SQLException
   * @deprecated 
   */
  private void updateVersionFrom(String v) throws SQLException {
    System.out.println("UPDATE version v = " + v + " app = " + APP_VERSION);
    String query;

    if (v.equals("inconnue")) {
      query = "CREATE TABLE version (version char(8))";
      dc.executeUpdate(query);
      query = "INSERT INTO version VALUES('" + APP_VERSION + "')";
      dc.executeUpdate(query);
    } 
    query = "UPDATE version SET version = '" + APP_VERSION + "'";
    dc.executeUpdate(query);
  }

  public class GemBoot
  {

    private JLabel label;
    private JFrame frame;

    public GemBoot() {
      frame = new JFrame("Algem (" + APP_VERSION + ")");
      ImageIcon icon = ImageUtil.createImageIcon(ImageUtil.ALGEM_LOGO);

      label = new JLabel("", JLabel.LEFT);
      frame.setSize(420, 150);
      frame.setLocation(100, 100);

      frame.add(new JLabel(icon), BorderLayout.WEST);
      frame.add(label, BorderLayout.EAST);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
    }

    public void setMessage(String msg) {
      label.setText(msg);
    }

    private JFrame getFrame() {
      return frame;
    }

    private void close() {
      frame.setVisible(false);
      frame.dispose();
    }
  }

  public static void main(String args[]) {

    Algem appli;
    String user = null;
    String cache = null;
    String base = null;
    String conf = null;

    if (args.length > 0) {
      conf = args[0];
    }
    if (args.length > 1) {
      user = args[1];
    }
    if (args.length > 2) {
      cache = args[2];
    }
    if (args.length > 3) {
      base = args[3];
    }

    Font fsans = new Font("Lucida Sans", Font.BOLD, 12);
    Font fserif = new Font(Font.SERIF, Font.BOLD + Font.ITALIC, 12);

    UIManager.put("Menu.font", fsans);
    UIManager.put("MenuBar.font", fsans);
    UIManager.put("MenuItem.font", fsans);
    UIManager.put("Label.font", fsans);
    UIManager.put("Button.font", fsans);
    UIManager.put("ToggleButton.font", fsans);
    UIManager.put("ComboBox.font", fsans);
    UIManager.put("TabbedPane.font", fsans);
    UIManager.put("CheckBox.font", fsans);
    UIManager.put("CheckBoxMenuItem.font", fsans);
    UIManager.put("TitledBorder.font", fsans.deriveFont(Font.BOLD + Font.ITALIC));

    try {
      appli = new Algem();
      appli.init(conf, cache, base, user);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
              ex.toString(),
              "Erreur et conflit en création de l'appli",
              JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
      System.exit(6);
    }
  }
}

// JUST FOR HISTORY INFO : DO NOT UNCOMMENT
/*
  System.setSecurityManager(new RMISecurityManager()); 
  try { 
    dc =  (DataCacheObjet) Naming.lookup("//www/DataCache"); 
  } catch (Exception e) {
    System.err.println("Erreur Lookup "+e); 
  }
 */
