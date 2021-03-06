/*
 * @(#)Algem.java   2.17.6 12/07/2021
 *
 * Copyright (c) 1999-2021 Musiques Tangentes. All Rights Reserved.
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
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.InsetsUIResource;
import net.algem.config.Company;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.ThemeConfig;
import net.algem.contact.OrganizationIO;
import net.algem.security.AuthDlg;
import net.algem.security.User;
import net.algem.util.*;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.MessagePopup;
import org.apache.commons.codec.binary.Base64;

/**
 * Main class.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.6
 */
public class Algem
{
  public static final String APP_VERSION = "2.17.6";
  public static final List<LookAndFeelInfo> ALTERNATIVE_LAF = new ArrayList<>();
  private static final int DEF_WIDTH = 1080;// (850,650) => ancienne taille
  private static final int DEF_HEIGHT = 780;
  private static final int BOOT_ICON_MAX_WIDTH = 128;
  private static final int BOOT_ICON_MAX_HEIGHT = 128;
  private static final Point DEF_LOCATION = new Point(70, 30);
  private static final String[] ADDITIONAL_PROPERTIES = {
    "local.properties",
    System.getProperty("user.home") + FileUtil.FILE_SEPARATOR + ".algem" + FileUtil.FILE_SEPARATOR + "preferences"
  };

  private JFrame frame;
  private DataCache cache;
  private String driverName = "org.postgresql.Driver";
  private String hostName = "localhost";
  private String baseName = "algem";
  private static Properties props;
  private static final Font MY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
  private DataConnection dc;

  public Algem() {
    Locale.setDefault(Locale.FRENCH);
    props = new Properties();
  }

  /**
   * Check if a given feature is enabled in current configuration.
   *
   * <p>
   * Feature keys are prefixed in properties files by <code>'feature.'</code></p>
   *
   * @param featureName The name of the feature to check
   * @return whether this feature is enabled
   */
  public static boolean isFeatureEnabled(String featureName) {
    return Boolean.parseBoolean(props.getProperty("feature." + featureName, "false"));
  }

  public static File getScriptsPath() {
    Preferences prefs = Preferences.userRoot().node("/algem/paths");
    String path = prefs.get("scripts.path", ConfigUtil.getConf(ConfigKey.SCRIPTS_PATH.getKey()));
    File prefDir = new File(path).getAbsoluteFile();
    if (prefDir.isDirectory()) {
      return prefDir;
    }
    if (props == null) {
      return null;
    }
    String pathName = props.getProperty("scripts_path", "scripts");
    return pathName == null ? null : new File(props.getProperty("scripts_path", "scripts")).getAbsoluteFile();
  }

  private void init(String configFile, final String host, final String base, String login) throws IOException {

    // opening configuration file
    try { // local file
      props.load(new FileInputStream(configFile));
    } catch (FileNotFoundException fe) {
      try { //url
        props.load(new URL(configFile).openStream());
      } catch (MalformedURLException ex) {
        System.err.println(ex);
      }
    } catch (IOException e) {
      MessagePopup.error(null, e.getMessage() + ">>" + configFile);
      System.exit(1);
    }

    // optional properties file $HOME/.algem/preferences
    setAdditionalProperties();
    setUIProperties();
    setLocale(props);

    /* -------------------------- */
    /* Initialisation driver JDBC */
    /* -------------------------- */
    try {
      setDB(host, base, props);
    } catch (SQLException ex) {
      MessagePopup.error(frame, ex.getMessage());
      System.exit(2);
    }
    cache = DataCache.getInstance(dc, login);// !important before Logger
    /* -------------------------- */
    /* Logger initialisation      */
    /* -------------------------- */
    String logPath = ConfigUtil.getConf(ConfigKey.LOG_PATH.getKey()) + "/algem.log";
    String msg = "Algem version " + APP_VERSION + "\nJava version " + System.getProperty("java.version");
    if (logPath != null) {
      try {
        GemLogger.set(new File(logPath).getPath());
      } catch (IOException ex1) {
        System.err.println(ex1.getMessage());
        //ex1.printStackTrace();
        try {
          setDefaultLogFile();
        } catch (IOException ex2) {
          //ex2.printStackTrace();
          System.err.println(ex2.getMessage());
        }
      }
    }
    GemLogger.log(Level.INFO, "net.algem.Algem", "main", msg);

    final GemBoot gemBoot = new GemBoot(new OrganizationIO(dc));

    /* ------------------------ */
    /* Test login user validity */
    /* ------------------------ */
    boolean auth = "true".equalsIgnoreCase(props.getProperty("auth"));

    if (auth || login == null) {//authentication required
      checkAuthUser(gemBoot.getFrame());
    } else {
      checkUnauthUser(login);
    }

    cache.load(gemBoot);
    /* ------------------------------------------------ */
    /* Creates the frame of the application             */
    /* ------------------------------------------------ */
    setDesktop();
    gemBoot.close();
  }

  /**
   *
   * @param u user name
   * @param pass user pass
   * @param auth authentication config
   * @deprecated
   */
  private void checkUser(String u, String pass, boolean auth) {
    User currentUser = cache.getUser();
    if (currentUser == null) {
      MessagePopup.error(null, MessageUtil.getMessage("unknown.login", u));
      System.exit(4);
    } else if (auth) {
      if (!cache.getUserService().authenticate(currentUser, pass)) {
        MessagePopup.error(null, MessageUtil.getMessage("authentication.failure"));
        System.exit(5);
      }
    }
  }

  /**
   *
   * @param login
   * @param pass
   * @return true if authentication succeeded
   * @deprecated
   */
  private boolean authenticate(String login, String pass) {
    return cache.getUserService().authenticate(login, pass);
  }

  /**
   * Presents a dialog to authenticate user.
   *
   * @param parent parent frame
   */
  private void checkAuthUser(Frame parent) {
    String login = null;
    String pass = null;
    boolean success = false;
    int trials = 1;
    do {
      AuthDlg dlg = new AuthDlg(parent);
      if (dlg.isValidation()) {
        login = dlg.getLogin();
        pass = dlg.getPass();
      } else {
        System.exit(5);
      }
      if (login.length() > 0 && pass.length() > 0 && cache.getUserService().authenticate(login, pass)) {
        success = true;
      } else if (trials < 3) {
        MessagePopup.error(parent, MessageUtil.getMessage("authentication.failure"));
      }
      trials++;
    } while (success == false && trials <= 3);
    if (success) {
      cache.setUser(login);
    } else {
      MessagePopup.error(parent, MessageUtil.getMessage("unknown.login", login));
      System.exit(5);
    }
  }

  /**
   * Checks if this @{code login} is valid.
   * This method must be called when authentication is disabled in properties.
   *
   * @param login a login string
   */
  private void checkUnauthUser(String login) {
    cache.setUser(login);
    if (cache.getUser() == null) {
      MessagePopup.error(null, MessageUtil.getMessage("unknown.login", login));
      System.exit(4);
    }
  }

  /**
   * Creates a log file into temp folder.
   *
   * @throws IOException
   */
  private void setDefaultLogFile() throws IOException {
    GemLogger.set("%t/algem.log");
  }

  private void setDesktop() {
    String title = "Algem" + "(" + APP_VERSION + ")/" + props.getProperty("appClient");
    //          + " - Utilisateur système " +System.getProperty("user.name")
    // + " - jdbc://" + hostName + "/" + baseName;

    frame = new JFrame(title);
    Preferences prefs = Preferences.userRoot().node("/algem/ui");
    frame.setSize(prefs.getInt("desktop.w", DEF_WIDTH), prefs.getInt("desktop.h", DEF_HEIGHT));
    frame.setLocation(prefs.getInt("desktop.x", DEF_LOCATION.x), prefs.getInt("desktop.y", DEF_LOCATION.y));
    checkVersion(frame);

    GemDesktopCtrl desktop = new GemDesktopCtrl(frame, cache, props);
    frame.setVisible(true);
  }

  /**
   * Sets additional user properties.
   * If the file does not exist, the relevant resource is loaded from the jar.
   */
  private void setAdditionalProperties() {
    for (String path : ADDITIONAL_PROPERTIES) {
      Properties p = new Properties();
      try {
        p.load(new FileInputStream(path));
        props.putAll(p);
        GemLogger.info("Loaded properties " + path);
      } catch (FileNotFoundException e) {
        GemLogger.log(e.getMessage());
        InputStream input = getClass().getClassLoader().getResourceAsStream(path);
        if (input != null) {
          try {
            p.load(input);
            props.putAll(p);
            GemLogger.info("Loaded internal properties " + path);
          } catch (IOException ex) {
            GemLogger.log(ex.getMessage());
          }
        }
      } catch (IOException e) {
        GemLogger.logException(e);
        MessagePopup.error(null, e.getMessage());
        System.exit(3);
      }
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

    String dbPass = props.getProperty("dbpass");

    if (dbPass != null) {
      byte[] pass64 = Base64.decodeBase64(dbPass);
      dbPass = new String(pass64).trim();
    }

    String port = props.getProperty("port");
    int dbport = (port != null) ? Integer.parseInt(port) : 0;

    dc = new DataConnection(host, dbport, base, dbPass);
//    dc = new DataConnectionSpy(host, dbport, base, dbPass);

    String ssl = props.getProperty("ssl");
    if (ssl != null && "true".equalsIgnoreCase(ssl)) {
      dc.setSsl(true);
    }
    String cacert = props.getProperty("cacert");
    if (cacert != null && "true".equalsIgnoreCase(cacert)) {
      dc.setCacert(true);
    }
    dc.connect();
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
        System.exit(6);
      } finally {
        dc.setAutoCommit(true);
      }
    }
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

  private void setUIProperties() {
    String laf = ThemeConfig.THEME_PREF.get("theme", "javax.swing.plaf.metal.MetalLookAndFeel");
    if (laf == null) {
      laf = props.getProperty("lookandfeel");
    }
    if (laf != null) {
      setLafProperties(laf);
    }
    // load alternatives look and feel
    for (Object o : props.keySet()) {
      String k = (String) o;
      if (k.startsWith("lookandfeel")) {
        String clazz = props.getProperty(k);
        final String lafName = k.substring(k.lastIndexOf('.') + 1);
        ALTERNATIVE_LAF.add(new ThemeConfig.GemLafInfo(lafName, clazz));
      }
    }

    String s = props.getProperty("couleur.fond");
    if (s != null) {
      frame.setBackground(Color.decode(s));
    }

    s = props.getProperty("couleur.char");
    if (s != null) {
      frame.setForeground(Color.decode(s));
    }

    if (!isFeatureEnabled("native_fonts")) {
      initUIFonts();
    }
    ToolTipManager.sharedInstance().setInitialDelay(20);
  }

  public static void setLafProperties(final String lafClassName) {
    try {
      System.out.println("lafClassName " + lafClassName);

    /*
      if (lafClassName.startsWith("com.jtattoo")) {
        switch (lafClassName) {
          case "com.jtattoo.plaf.acryl.AcrylLookAndFeel":
            com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.aero.AerolLookAndFeel":
            com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel":
            com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel":
            com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.fast.FastLookAndFeel":
            com.jtattoo.plaf.fast.FastLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.graphite.GraphiteLookAndFeel":
            com.jtattoo.plaf.graphite.GraphiteLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.luna.LunaLookAndFeel":
            com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.mint.MintLookAndFeel":
            com.jtattoo.plaf.mint.MintLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.smart.SmartLookAndFeel":
            com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
          case "com.jtattoo.plaf.texture.TextureLookAndFeel":
            com.jtattoo.plaf.texture.TextureLookAndFeel.setTheme("Default", "INSERT YOUR LICENSE KEY HERE", "Algem");
            break;
        }
      } */
      UIManager.setLookAndFeel(lafClassName);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      GemLogger.log("look&feel exception : " + ex.getMessage());
    }

    String lafName = UIManager.getLookAndFeel().getName();
    UIDefaults def = UIManager.getLookAndFeelDefaults();
    def.put("ProgressMonitor.progressText", BundleUtil.getLabel("Running.job.label"));
    switch (lafName) {
      case "Nimbus":
        //def.put("Button.contentMargins", new InsetsUIResource(4, 3, 4, 3)); //  default : (6,14,6,14)
        def.put("TextField.contentMargins", new InsetsUIResource(4, 4, 4, 4)); //  default : (6,6,6,6)
//          def.put("Table.alternateRowColor", new Color(224,224,224));// default :  #f2f2f2 (242,242,242)
        def.put("TableHeader.font", MY_FONT);
        def.put("TableHeader:\"TableHeader.renderer\".contentMargins", new InsetsUIResource(2, 2, 2, 2)); // default: (2,5,4,5)
        def.put("Table.font", MY_FONT); // default : Font SansSerif 12
        def.put("Table.showGrid", true); // default: false
        def.put("Table.cellNoFocusBorder", new InsetsUIResource(2, 2, 2, 2)); // Border Insets(2,5,2,5)
        break;
      case "Acryl":
      case "Aero":
      case "Aluminium":
      case "Bernstein":
      case "Fast":
      case "Graphite":
      case "Smart":
      case "Texture":
        def.put("TableHeader.font", MY_FONT);
        def.put("TableHeader:\"TableHeader.renderer\".contentMargins", new InsetsUIResource(2, 2, 2, 2)); // default: (2,5,4,5)
        def.put("Table.font", MY_FONT); // default : Font SansSerif 12
        def.put("TextField.font", MY_FONT);
        def.put("ComboBox.font", MY_FONT);
//        def.put("TextArea.font", MY_FONT.deriveFont(12));
//        def.put("TextPane.font", MY_FONT.deriveFont(12));
        break;
      case "Windows":
      case "Windows Classic":
        def.put("TextArea.font", def.getFont("Label.font").deriveFont(Font.PLAIN, 12));
        break;
    }
  }

  private static void initUIFonts() {
    if ("Metal".equals(UIManager.getLookAndFeel().getName())) {
      Font fsans = new Font("Lucida Sans", Font.PLAIN, 12);
      Font bold = fsans.deriveFont(Font.BOLD);

      UIManager.put("Menu.font", bold);
      UIManager.put("MenuBar.font", bold);
      UIManager.put("MenuItem.font", bold);
      UIManager.put("Label.font", bold);
      UIManager.put("Button.font", bold);
      UIManager.put("ToggleButton.font", bold);
      UIManager.put("ComboBox.font", bold);
      UIManager.put("TabbedPane.font", bold);
      UIManager.put("CheckBox.font", bold);
      UIManager.put("CheckBoxMenuItem.font", bold);
      UIManager.put("TitledBorder.font", bold);
      //UIManager.put("TitledBorder.font", fsans.deriveFont(Font.BOLD + Font.ITALIC));
      UIManager.put("RadioButton.font", bold);
      UIManager.put("List.font", bold);
    }
  }

  public class GemBoot
  {

    private JLabel label;
    private JFrame frame;

    public GemBoot(OrganizationIO orgIO) {
      frame = new JFrame("Algem (" + APP_VERSION + ")");
      ImageIcon icon = null;
      try {
        final Company comp = orgIO.getDefault();
        byte[] data = comp.getLogo();
        if (data != null) {
          BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
          icon = ImageUtil.getRescaledIcon(img, BOOT_ICON_MAX_WIDTH, BOOT_ICON_MAX_HEIGHT);
        } else  {
          icon = ImageUtil.createImageIcon(ImageUtil.ALGEM_LOGO);
        }
      } catch (SQLException | IOException ex) {
        GemLogger.log(ex.getMessage());
        icon = ImageUtil.createImageIcon(ImageUtil.ALGEM_LOGO);
      }

      label = new JLabel("", JLabel.LEFT);
      frame.setSize(420, 160);
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

    String userArg = null;
    String hostArg = null;
    String baseArg = null;
    String confArg = null;

    if (args.length > 0) {
      confArg = args[0];
    }
    if (args.length > 1) {
      userArg = args[1];
    }
    if (args.length > 2) {
      hostArg = args[2];
    }
    if (args.length > 3) {
      baseArg = args[3];
    }

    try {
      Algem appli = new Algem();
      appli.init(confArg, hostArg, baseArg, userArg);
    } catch (Exception ex) {
      StackTraceElement[] trace = ex.getStackTrace();
      String st = trace.length == 0 ? "" : trace[0].toString();
      String msg = ex.getMessage();
      JOptionPane.showMessageDialog(null,
              MessageUtil.getMessage("application.create.error") + " :\n" +  ex.getClass().getName() + "\n" + st + (msg == null ? "" : "\n"+msg),
              "Erreur",
              JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
      System.exit(7);
    }
  }

  /**
   * Pour implémentation temporaire des modifs client Ecole Pays Roi Morvan.
   *
   * @return a string representing the client
   * @author ERIC
   *
   * @since 2.9.4.12
   */
  public static String getAppClient() {
    return props.getProperty("appClient");
  }

}
