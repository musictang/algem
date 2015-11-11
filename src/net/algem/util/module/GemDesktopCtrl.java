/*
 * @(#)GemDesktopCtrl.java	2.9.4.12 28/09/15
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.util.module;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import net.algem.contact.*;
import net.algem.edition.*;
import net.algem.group.GroupFileEditor;
import net.algem.planning.ReloadDetailEvent;
import net.algem.planning.ScheduleDetailCtrl;
import net.algem.planning.ScheduleDetailEvent;
import net.algem.planning.SelectDateEvent;
import net.algem.planning.day.DayScheduleCtrl;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.PlanModifCtrl;
import net.algem.security.UserService;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.event.GemRemoteEvent;
import net.algem.util.event.MessageEvent;
import net.algem.util.menu.*;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.postit.CreatePostitEvent;
import net.algem.util.postit.Postit;
import net.algem.util.postit.PostitCreateCtrl;
import net.algem.util.postit.PostitModule;
import net.algem.util.ui.FrameDetach;
import net.algem.util.ui.HtmlViewer;
import net.algem.util.ui.Toast;
import net.algem.util.ui.UIAdjustable;

/**
 * Algem desktop controller.
 * This is the working space of the application.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 1.0a 05/07/2002
 */
public class GemDesktopCtrl
        implements ActionListener, GemDesktop, UIAdjustable
{
  private final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
  private final Cursor defaultCursor = Cursor.getDefaultCursor();

  private Hashtable<String, GemModule> modules;
  private Hashtable<String, JMenuItem> menus;
  private DataCache dataCache;
  private DataConnection dc;
  private UserService userService;
  private final JDesktopPane desktop;
  private PostitModule postit;
  private PostitCreateCtrl postitCreate;
  private PlanModifCtrl modifCtrl;
  private ScheduleDetailCtrl detailCtrl;
  private HtmlViewer help = null;
  private JFrame frame;
  private JMenuBar mbar;
  private JMenu mFile;
  private JMenu mWindows;
  private JMenuItem miSaveUISettings;
  private JMenu mPerson;
  private JMenu mPlanning;
  private JMenu mCatalog;
  private JMenu mVarious;
  private JMenu mConfig;
  private JMenu mHelp;
  private ActionListener actionListener;
  private Properties props;
  private Preferences prefs;
  private boolean savePrefs;
  private EventListenerList listenerList = new EventListenerList();
  Socket dispatcher;
  ObjectInputStream iDispatcher;
  ObjectOutputStream oDispatcher;
  String remoteId;

  public GemDesktopCtrl(JFrame frame, DataCache dataCache, Properties props) {
    this.frame = frame;
    this.dataCache = dataCache;
    this.dc = DataCache.getDataConnection();
    userService = dataCache.getUserService();
    this.props = props;
    prefs = Preferences.userRoot().node("/algem/ui");

    modules = new Hashtable<String, GemModule>();
    menus = new Hashtable<String, JMenuItem>();
    mbar = createMenuBar();

    desktop = new JDesktopPane();
    desktop.setBackground(Color.gray);

    postit = new PostitModule(userService); // postit windows
    addModule(postit);
    postit.getView().setLocation(new java.awt.Point(0, 0));
    postit.getNewPostit();

    modifCtrl = new PlanModifCtrl(this);
    detailCtrl = new ScheduleDetailCtrl(this, modifCtrl);

    frame.setJMenuBar(mbar);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(desktop, BorderLayout.CENTER);

    frame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent evt) {
        try {
          close();
          System.exit(0);
        } catch (GemCloseVetoException e) {
          GemLogger.logException(e);
        }
      }
    });

    try {
      loadModules();
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
    try {
      setDispatcher();
    } catch (IOException e) {
      System.err.println("exception dispatcher:" + e);
      dispatcher = null;
    }
  } // end constructor

  /**
   * Loads serialized modules.
   * @throws IOException
   */
  private void loadModules() throws IOException {
    ObjectInputStream ois = null;
    String path = System.getProperty("user.home") + FileUtil.FILE_SEPARATOR;
    ois = new ObjectInputStream(new FileInputStream(path + ".gemdesktop"));

    try {
      java.util.List<GemModuleSID> serList = (ArrayList<GemModuleSID>) ois.readObject();
      for (GemModuleSID sid : serList) {
        if (sid != null) {
          if (sid.getModuleClass().equals(PersonFileEditor.class.getSimpleName())) {
            Contact c = ContactIO.findId(Integer.parseInt(sid.getSID()), dc);
            PersonFile pFile = new PersonFile(c);
            ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(pFile);
            PersonFileEditor editor = new PersonFileEditor(pFile);
            addModule(editor, true);
          } else if (sid.getModuleClass().equals(DayScheduleCtrl.class.getSimpleName())) {
            DayScheduleCtrl dayScheduleCtrl = new DayScheduleCtrl();
            addModule(dayScheduleCtrl);
            dayScheduleCtrl.setState(sid.getState());
            // location initiale du tableau jour.
            dayScheduleCtrl.getView().setLocation(110, 0);
            dayScheduleCtrl.mayBeMaximize();
          }
        }
      }
    } catch (Exception e) {
      GemLogger.logException("GemModuleSid", e);
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (IOException ex) {
          GemLogger.logException(ex);
        }
      }
    }
  }

  /**
   * Inits dispatcher and runs listening thread.
   * @throws IOException
   */
  private void setDispatcher() throws IOException {
    initDispatcher();
    new Thread(new Runnable() {

      @Override
      public void run() {
        int nerr = 0;
        for (;;) {
          GemRemoteEvent evt = null;
          try {
            evt = (GemRemoteEvent) iDispatcher.readObject();
            System.out.println("thread evt " + evt);
            remoteEvent(evt);
          } catch (Exception e) {
            if (++nerr > 2) {
              System.err.println("thread exception " + e);
              System.err.println("DECONNECTION DISPATCHER");
              Thread.currentThread().interrupt();
              return;
            }
          }
        }
      }
    }).start();

  }

  /**
   * Gets the postit module.
   *
   * @return a postitModule
   */
  public PostitModule getPostit() {
    return postit;
  }

  /**
   * Cleanup and closing.
   * @throws GemCloseVetoException
   */
  private void close() throws GemCloseVetoException {
    if (savePrefs) {
      storeUISettings();
    }
    ObjectOutputStream out = null;
    String path = System.getProperty("user.home") + FileUtil.FILE_SEPARATOR;
    try {
      out = new ObjectOutputStream(new FileOutputStream(path + ".gemdesktop"));
    } catch (IOException e) {
      GemLogger.logException(e);
    }
    // test ferme tous les modules
    Enumeration enu = modules.elements();
    // Sérialisation des modules ouverts dans une liste
    java.util.List<GemModuleSID> lm = new ArrayList<GemModuleSID>();
    while (enu.hasMoreElements()) {
      GemModule m = (GemModule) enu.nextElement();
      GemModuleSID moduleSID = new GemModuleSID(m.getClass().getSimpleName(), m.getSID(), m.getLabel());
      // save optional state
      moduleSID.setState(m.getState());
      lm.add(moduleSID);
      m.close();
    }
    if (out != null) {
      try {
        out.writeObject(lm);
        out.close();
      } catch (IOException e) {
        GemLogger.logException(e);
      }
    }
    // deconnexion
    dc.close();
    try {
      closeDispatcher();
    } catch (IOException ioe) {
      GemLogger.logException(ioe);
    }

  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void addGemEventListener(GemEventListener l) {
    listenerList.add(GemEventListener.class, l);
  }

  @Override
  public void removeGemEventListener(GemEventListener l) {
    listenerList.remove(GemEventListener.class, l);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Object src = evt.getSource();

    setWaitCursor();

    if (BundleUtil.getLabel("Menu.quit.label").equals(arg)) {
      savePrefs = (evt.getModifiers() & Event.SHIFT_MASK) == Event.SHIFT_MASK;
      try {
        close();
        System.exit(0);
      } catch (GemCloseVetoException e) {
      }
    } else if (BundleUtil.getLabel("Menu.postit.label").equals(arg)) {
      postitCreate = new PostitCreateCtrl(this, userService);
      postitCreate.addActionListener(this);
      addPanel(PostitCreateCtrl.POSTIT_CREATE_KEY, postitCreate);
    } else if (BundleUtil.getLabel("Menu.contact.label").equals(arg)) {
      ContactExportDlg dlg = new ContactExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.member.label").equals(arg)) {
      MemberExportDlg dlg = new MemberExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.teacher.label").equals(arg)) {
      TeacherExportDlg dlg = new TeacherExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.payer.label").equals(arg)) {
      PayerExportDlg dlg = new PayerExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Group.members.label").equals(arg)) {
      MusicianExportDlg dlg = new MusicianExportDlg(this);
      dlg.setVisible(true);

    } else if (BundleUtil.getLabel("Menu.student.all.label").equals(arg)) {
      AllStudentExportDlg dlg = new AllStudentExportDlg(this);
      dlg.setVisible(true);
    }
    else if (BundleUtil.getLabel("Menu.student.by.teacher.label").equals(arg)) {
      TeacherStudentExportDlg dlg = new TeacherStudentExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.student.by.course.label").equals(arg)) {
      CourseStudentExportDlg dlg = new CourseStudentExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.student.by.module.label").equals(arg)) {
      ModuleStudentExportDlg dlg = new ModuleStudentExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.student.by.instrument.label").equals(arg)) {
      InstrumentStudentExportDlg dlg = new InstrumentStudentExportDlg(this);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.hour.stat.label").equals(arg)) {
      HourStatDlg hourStatDlg = new HourStatDlg(getFrame(), dataCache);
      hourStatDlg.setVisible(true);
    } else if (BundleUtil.getLabel("Statistics.label").equals(arg)) {
      StatsExportDlg dlg = new StatsExportDlg(this);
//      addPanel("stats", dlg);
      dlg.setVisible(true);
    } else if (BundleUtil.getLabel("Menu.windows.iconify.all.label").equals(arg)) {
      iconify();
    } else if (BundleUtil.getLabel("Menu.windows.close.all.label").equals(arg)) {
      closeAll();
    } else if (BundleUtil.getLabel("Menu.windows.open.all.label").equals(arg)) {
      open();
    } else if (BundleUtil.getLabel("Menu.windows.cascade.label").equals(arg)) {
      cascade();
    } else if ("Menu.windows.detach".equals(arg)) {
      detachCurrent();
    } else if (GemCommand.CLOSE_CMD.equals(arg) || GemCommand.CANCEL_CMD.equals(arg)) {
      removeCurrentModule();
    } else if (src == miSaveUISettings) {
      storeUISettings();
      Toast.showToast(desktop, getUIInfo());
    } else {
      Iterator it = menus.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry me = (Map.Entry) it.next();
        if (src == me.getValue()) {
          GemModule mod = (GemModule) modules.get(me.getKey());
          if (mod != null) {
            setSelectedModule(mod);
          }
          break;
        }
      }
    }
    setDefaultCursor();
  }

  /**
   * Sends an event to the dispatcher and to listeners implementing GemEventListener.
   *
   * @param evt
   * @see net.algem.util.event.GemEventListener
   */
  @Override
  public void postEvent(GemEvent evt) {
    System.out.println("GemDesktopCtrl.postEvent:" + evt);

    if (evt instanceof ScheduleDetailEvent) {
      ScheduleDetailEvent pde = (ScheduleDetailEvent) evt;
      setWaitCursor();
      detailCtrl.loadSchedule(pde);
      setDefaultCursor();
    } else if (evt instanceof SelectDateEvent) {
    } else if (evt instanceof ReloadDetailEvent) {
      detailCtrl.reloadFromLastEvent();
    } else {
      if (evt instanceof ModifPlanEvent) {
        detailCtrl.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CloseLink"));
      }
      if (dispatcher != null) {
        try {
          GemRemoteEvent e = new GemRemoteEvent(evt, remoteId);
          System.out.println("GemDesktopCtrl.postEvent remoteId :" + remoteId + " e:" + e);
          oDispatcher.writeObject(e);
        } catch (Exception ex) {
          GemLogger.logException("erreur dispatch postEvent :", ex);
        }
      }
    }
    forwardEvent(evt);
  }

  /**
   * Forwards an event to the listeners.
   *
   * @param evt
   */
  public void forwardEvent(GemEvent evt) {
//        System.out.println("GemDesktopCtrl.forwardEvent:"+evt);
    Object[] listeners = listenerList.getListenerList();

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == GemEventListener.class) {
        GemEventListener l = (GemEventListener) listeners[i + 1];
        if (l != evt.getSource()) {
          System.out.println("GemDesktopCtrl.forwardEvent: POST to " + l);
          l.postEvent(evt);
        }
      }
    }
  }

  /**
   * Runs in internal thread when an event is received by the dispatcher.
   *
   * @param _evt
   */
  @Override
  public void remoteEvent(GemRemoteEvent _evt) {
    GemEvent evt = _evt.getEvent();
    System.out.println("GemDesktopCtrl.remoteEvent:" + _evt);
    if (evt instanceof MessageEvent) {
      JOptionPane.showMessageDialog(frame, ((MessageEvent) evt).getMessage(), "Message RMI", JOptionPane.INFORMATION_MESSAGE);
      return;
    } else if (evt instanceof CreatePostitEvent) {
      Postit p = ((CreatePostitEvent) evt).getPostit();
      if (p.getReceiver() > 0) {
        if (dataCache.getUser().getId() == p.getReceiver()) {
          postit.addPostit(((CreatePostitEvent) evt).getPostit());
        }
      } else {
        postit.addPostit(((CreatePostitEvent) evt).getPostit());
      }
      return;
    }
    // envoi vers le cache
    dataCache.remoteEvent(evt);
    forwardEvent(evt);
  }

  @Override
  public Frame getFrame() {
    return frame;
  }

  @Override
  public DataCache getDataCache() {
    return dataCache;
  }

  @Override
  public void addModule(GemModule module) {
    addModule(module, false);
  }

  public void addModule(GemModule module, boolean iconified) //public void addModule(GemModule _module, int _layer)
  {
    JMenuItem mItem = new JMenuItem(module.getLabel());
    menus.put(module.getLabel(), mItem);
    mItem.addActionListener(this);

    modules.put(module.getLabel(), module);
    module.setDesktop(this);	// createIHM
    DefaultGemView gemView = module.getView();
    desktop.add(gemView);

    gemView.setVisible(true);
    try {
      if (iconified) {
        gemView.setIcon(true);
      } else {
        gemView.setLocation(new Point(100, 10 + (modules.size() * 10))); // position x : 110 au lieu de orig : 80
        gemView.setSelected(true);
      }
    } catch (java.beans.PropertyVetoException e2) {
      System.err.println(e2.getMessage());
    }

    desktop.revalidate();
    mWindows.add(mItem);

  }

  @Override
  public void addPanel(String s, Container p) {
    GemModule m = new DefaultGemModule(s, p);
    addModule(m); 	//m.init();
  }

  @Override
  public void addPanel(String s, Container p, Dimension size) {
    GemModule m = new DefaultGemModule(s, p);
    addModule(m);
    m.setSize(size);
  }

  @Override
  public void removeModule(GemModule module) {
    if (module == null || module == postit) {
      return;
    }
    desktop.remove(module.getView());
    desktop.repaint();
    JMenuItem mItem = (JMenuItem) menus.get(module.getLabel());
    mWindows.remove(mItem);

    modules.remove(module.getLabel());
  }

  @Override
  public void removeModule(String key) {
    GemModule m = modules.get(key);
    removeModule(m);
  }

  @Override
  public void removeCurrentModule() {
    removeModule(getSelectedModule());
  }

  @Override
  public GemModule getSelectedModule() {
    DefaultGemView v = (DefaultGemView) desktop.getSelectedFrame();
    GemModule m = modules.get(v.getLabel());
    return m;
  }

  public GemModule getModule(String label) {
    return modules.get(label);
  }

  @Override
  public void setSelectedModule(String _label) {
    GemModule m = modules.get(_label);
    if (m != null) {
      setSelectedModule(m);
    }
  }

  @Override
  public void setSelectedModule(GemModule m) {
    try {
      if (m.getView().isIcon()) {
        m.getView().setIcon(false);
      }
      m.getView().setSelected(true);
    } catch (java.beans.PropertyVetoException ignore) {
    }
  }

  public PersonFileEditor getPersonFileEditor(int idper) {
    Enumeration<GemModule> mods = modules.elements();
    while (mods.hasMoreElements()) {
      GemModule m = mods.nextElement();
      if (m instanceof PersonFileEditor) {
        PersonFileEditor e = (PersonFileEditor) m;
        if (e.getDossierID() == idper) {
          return e;
        }
      }
    }
    return null;
  }

  /**
   * Gets a group module editor by group id.
   *
   * @param id group id
   * @return a group file editor
   */
  public GroupFileEditor getGroupFileEditor(int id) {
    Enumeration<GemModule> mods = modules.elements();
    while (mods.hasMoreElements()) {
      GemModule m = mods.nextElement();
      if (m instanceof GroupFileEditor) {
        GroupFileEditor e = (GroupFileEditor) m;
        if (e.getId() == id) {
          return e;
        }
      }
    }
    return null;
  }

  /**
   * Checks if a module already exists.
   *
   * @param key identification key for the module
   * @return true if exists
   */
  @Override
  public boolean hasModule(String key) {
    GemModule m = getModule(key);
    if (m != null) {
      setSelectedModule(m);
      return true;
    }
    return false;
  }

  /**
   * Sets wait cursor in <strong>foreground</strong>.
   */
  @Override
  public void setWaitCursor() {
    RootPaneContainer root = ((RootPaneContainer) desktop.getTopLevelAncestor());
    root.getGlassPane().setCursor(waitCursor);
    root.getGlassPane().setVisible(true);
    //frame.setCursor(waitCursor);
  }

  /**
   * Sets default cursor.
   */
  @Override
  public void setDefaultCursor() {
    RootPaneContainer root = ((RootPaneContainer) desktop.getTopLevelAncestor());
    root.getGlassPane().setCursor(defaultCursor);
    root.getGlassPane().setVisible(false);
//    frame.setCursor(defaultCursor); // cursor rest in background
  }

  /**
   * Creates socket and IO streams.
   *
   * @throws UnknownHostException
   * @throws IOException
   */
  private void initDispatcher() throws UnknownHostException, IOException {
    dispatcher = new Socket(props.getProperty("hostdispatcher"), DesktopDispatcher.DEFAULT_SOCKET_PORT);
    // dispatcher.getPort() -> DEFAULT_SOCKET_PORT
    GemLogger.log(Level.INFO, "Connexion dispatcher ok");
    InetAddress ia = dispatcher.getLocalAddress();
    remoteId = dataCache.getUser().getLogin() + "/" + ia.getHostName();
    GemLogger.log(Level.INFO, "remoteId " + remoteId);
    iDispatcher = new ObjectInputStream(dispatcher.getInputStream());
    oDispatcher = new ObjectOutputStream(dispatcher.getOutputStream());
  }

  /**
   * Closes socket.
   *
   * @throws IOException
   */
  private void closeDispatcher() throws IOException {
    if (dispatcher != null && !dispatcher.isClosed()) {
      dispatcher.close();
    }
  }

  /**
   * Gets main menu bar.
   *
   * @return a jMenuBar
   */
  private JMenuBar createMenuBar() {
    JMenuBar menuBar;
    JMenuItem menu;

    menuBar = new JMenuBar();

    mWindows = new JMenu(BundleUtil.getLabel("Menu.windows.label"));
    miSaveUISettings = getMenuItem("Store.ui.settings");
    mWindows.add(miSaveUISettings);
    mWindows.addSeparator();
    mWindows.add(getMenuItem("Menu.windows.detach"));

    menu = mWindows.add(new JMenuItem(BundleUtil.getLabel("Action.close.label")));
    menu.addActionListener(this);
    menu = mWindows.add(new JMenuItem(BundleUtil.getLabel("Menu.windows.close.all.label")));
    menu.addActionListener(this);
    menu = mWindows.add(new JMenuItem(BundleUtil.getLabel("Menu.windows.iconify.all.label")));
    menu.addActionListener(this);
    menu = mWindows.add(new JMenuItem(BundleUtil.getLabel("Menu.windows.open.all.label")));
    menu.addActionListener(this);
    menu = mWindows.add(new JMenuItem(BundleUtil.getLabel("Menu.windows.cascade.label")));
    menu.addActionListener(this);
    mWindows.addSeparator();

    mFile = new JMenu(BundleUtil.getLabel("Menu.file.label"));
    mFile.setMnemonic('f');
    menu = mFile.add(new JMenuItem(BundleUtil.getLabel("Menu.postit.label"), 'p'));
    menu.addActionListener(this);
    mFile.addSeparator();
    JMenu mExport = new JMenu(BundleUtil.getLabel("Menu.export.label"));
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.contact.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.member.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.teacher.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.payer.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Group.members.label")));
    menu.addActionListener(this);
    mExport.addSeparator();
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.student.all.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.student.by.teacher.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.student.by.course.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.student.by.module.label")));
    menu.addActionListener(this);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Menu.student.by.instrument.label")));
    menu.addActionListener(this);
    mExport.addSeparator();

    JMenuItem miAccountHourStat = new JMenuItem(BundleUtil.getLabel("Menu.hour.stat.label"));
    miAccountHourStat.addActionListener(this);
     if (!dataCache.authorize("Accounting.hours.stat.auth")) {
      miAccountHourStat.setEnabled(false);
    }
    mExport.add(miAccountHourStat);
    menu = mExport.add(new JMenuItem(BundleUtil.getLabel("Statistics.label")));
    menu.addActionListener(this);
    mFile.add(mExport);

    menu = mFile.add(new JMenuItem(BundleUtil.getLabel("Menu.quit.label"), 'q'));
    menu.addActionListener(this);

    menuBar.add(mFile);
    menuBar.add(mWindows);

    mCatalog = new MenuCatalog(this);
    mCatalog.setMnemonic('c');
    menuBar.add(mCatalog);

    mPerson = new MenuSearch(this);
    mPerson.setMnemonic('e');
    menuBar.add(mPerson);

    mPlanning = new MenuPlanning(this);
    mPlanning.setMnemonic('p');
    menuBar.add(mPlanning);

    mVarious = new MenuAccounting(this);
    mVarious.setMnemonic('d');
    menuBar.add(mVarious);

    mConfig = new MenuConfig(this);
    menuBar.add(mConfig);
    menuBar.add(Box.createHorizontalGlue());

    mHelp = new MenuHelp(this);
    mHelp.setMnemonic('A');
    menuBar.add(mHelp);

    /* JMenu mAide = new JMenu("Aide"); menu = mAide.add(new JMenuItem("Module
     * courant",'c')); menu.addActionListener(this); menu = mAide.add(new
     * JMenuItem("Index",'i')); menu.addActionListener(this); menu =
     * mAide.add(new JMenuItem("Recherche",'r')); menu.addActionListener(this);
     *
     * menu = mAide.add(new JMenuItem("A propos",'p'));
     * menu.addActionListener(this); mAide.setMnemonic('A');
     */
    //jmb.setHelpMenu(m);

    return menuBar;
  }

  private void detachCurrent() {
    System.out.println("detacheCurrent");
    GemModule m = getSelectedModule();
    if (m == null || m == postit) {
      return;
    }

    System.out.println("detacheCurrent remove:" + m);
    removeModule(m);

    Container c = m.getView().getContentPane();
    System.out.println("detacheCurrent content:" + c);
    FrameDetach fd = new FrameDetach(this, m.getLabel(), c);
    fd.setSize(c.getSize());
    fd.setVisible(true);
  }

  private void open() {
    JInternalFrame frames[] = desktop.getAllFrames();
    for (int i = 0; i < frames.length; i++) {
      if (frames[i].isIcon()
              && frames[i] != postit.getView()) {
        try {
          frames[i].setIcon(false);
        } catch (PropertyVetoException ignore) {
          GemLogger.logException(ignore);
        }
      }
    }
  }

  private void iconify() {
    JInternalFrame frames[] = desktop.getAllFrames();
    for (int i = 0; i < frames.length; i++) {
      if (!frames[i].isIcon()
              && frames[i] != postit.getView()) {
        try {
          frames[i].setIcon(true);
        } catch (PropertyVetoException ignore) {
          GemLogger.logException(ignore);
        }
      }
    }
  }

  private void closeAll() {
    JInternalFrame frames[] = desktop.getAllFrames();
    for (int i = 0; i < frames.length; i++) {
      if (!frames[i].isClosed()
              && frames[i] != postit.getView()) {
        try {
          frames[i].setClosed(true);
          frames[i].dispose();
        } catch (PropertyVetoException ignore) {
          GemLogger.logException(ignore);
        }
      }
    }
  }

  private void cascade() {
    JInternalFrame frames[] = desktop.getAllFrames();
    int x = 110;
    int y = 0;

    for (int i = 0; i < frames.length; i++) {
      if (!frames[i].isIcon()
              && frames[i] != postit.getView()) {
        frames[i].setLocation(x, y);
        frames[i].toFront();
        x += 10;
        y += 25;
        try {
          frames[i].setSelected(true);
        } catch (PropertyVetoException ignore) {
        }
      }
    }
  }

  private JMenuItem getMenuItem(String id) {
    JMenuItem m = new JMenuItem(BundleUtil.getLabel(id + ".label"));
    m.setMnemonic(BundleUtil.getLabel(id + ".mnemo").charAt(0));
    m.getAccessibleContext().setAccessibleDescription(BundleUtil.getLabel(id + ".info"));
    m.setActionCommand(id);
    m.addActionListener(this);

    return m;
  }
  /*
   * void showHelpViewer(String url)
   *
   * {
   * String urlPrefix = getLabel("Url.doc.location"); if (aide == null ||
   * !aide.isVisible()) { aide = new HtmlViewer(urlPrefix+url);
   * aide.setVisible(true); } else { try { aide.linkActivated(new
   * URL(urlPrefix+url)); } catch (Exception e) { } }
   *
   * } */

  @Override
  public void storeUISettings() {
    Rectangle bounds = frame.getBounds();
    prefs.putInt("desktop.w", bounds.width);
    prefs.putInt("desktop.h", bounds.height);
    prefs.putInt("desktop.x", bounds.x);
    prefs.putInt("desktop.y", bounds.y);
  }

  @Override
  public String getUIInfo() {
    Rectangle b = frame.getBounds();
    StringBuilder sb = new StringBuilder("<html>");
    sb.append(BundleUtil.getLabel("New.size.label")).append(" : ").append(b.width).append('x').append(b.height);
    sb.append("<br />").append(BundleUtil.getLabel("Position.label")).append(" : ").append(b.x).append(';').append(b.y);
    sb.append("</html>");
    return sb.toString();
  }
}
