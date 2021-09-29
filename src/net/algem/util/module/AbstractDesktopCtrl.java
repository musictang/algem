/*
 * @(#)AbstractDesktopCtrl.java	3.0.0  10/09/2021
 *                              2.17.0 26/03/2019
 *                              2.16.0 05/03/2019
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import net.algem.contact.*;
import net.algem.group.GroupFileEditor;
import net.algem.planning.ReloadDetailEvent;
import net.algem.planning.ScheduleDetailCtrl;
import net.algem.planning.ScheduleDetailEvent;
import net.algem.planning.SelectDateEvent;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.PlanModifCtrl;
import net.algem.security.UserService;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.event.GemRemoteEvent;
import net.algem.util.event.MessageEvent;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.postit.CreatePostitEvent;
import net.algem.util.postit.Postit;
import net.algem.util.postit.PostitCreateCtrl;
import net.algem.util.postit.PostitModule;
import net.algem.util.ui.FrameDetach;
import net.algem.util.ui.HtmlViewer;
import net.algem.util.ui.UIAdjustable;

/**
 * Algem desktop controller. This is the working space of the application.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 3.0.0
 */
public abstract class AbstractDesktopCtrl
        implements ActionListener, GemDesktop, UIAdjustable {

    protected static final Object POSTIT_LOCK = new Object();
    protected final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    protected final Cursor defaultCursor = Cursor.getDefaultCursor();

    protected Hashtable<String, GemModule> modules;

    protected DataCache dataCache;
    protected DataConnection dc;
    protected UserService userService;

    protected PostitModule postitModule;
    protected PostitCreateCtrl postitCreate;

    protected PlanModifCtrl modifCtrl;
    protected ScheduleDetailCtrl detailCtrl;

    protected Hashtable<String, JMenuItem> menus;

    protected HtmlViewer help = null;

    protected JFrame frame;

    protected JMenuBar mbar;
    protected JMenu mFile;
    protected JMenu mWindows;
    protected JMenuItem miSaveUISettings;
    protected JMenu mPerson;

    protected JMenu mPlanning;
    protected JMenu mCatalog;
    protected JMenu mVarious;
    protected JMenu mConfig;
    protected JMenu mHelp;

    protected ActionListener actionListener;
    protected Properties props;
    protected Preferences prefs;
    protected boolean savePrefs;
    protected EventListenerList listenerList = new EventListenerList();
    protected ScheduledExecutorService postitScheduledExecutor;

    Socket dispatcher;
    ObjectInputStream iDispatcher;
    ObjectOutputStream oDispatcher;
    String remoteId;

    public AbstractDesktopCtrl(JFrame frame, DataCache dataCache, Properties props) {
        this.frame = frame;
        this.dataCache = dataCache;
        this.dc = DataCache.getDataConnection();
        userService = dataCache.getUserService();
        this.props = props;
        prefs = Preferences.userRoot().node("/algem/ui");

        modules = new Hashtable<String, GemModule>();
        menus = new Hashtable<String, JMenuItem>();
        mbar = createMenuBar();

        modifCtrl = new PlanModifCtrl(this);
        detailCtrl = new ScheduleDetailCtrl(this, modifCtrl);

        frame.setJMenuBar(mbar);
        frame.getContentPane().setLayout(new BorderLayout());
        //frame.getContentPane().add(desktop, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
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
            launchDispatcher();
        } catch (IOException e) {
            System.err.println("exception dispatcher:" + e);
            dispatcher = null;
        }
        /*
    try {
      loadModules();
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
         */
    } // end constructor

    protected void setContentPane(JComponent pane) {
        frame.getContentPane().add(pane, BorderLayout.CENTER);
    }

    protected void addPostit() {
        postitModule = new PostitModule(userService); // postit windows
        addModule(postitModule);
        postitModule.getView().setLocation(new java.awt.Point(0, 0));

        postitScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        postitScheduledExecutor.scheduleAtFixedRate(this::loadPostits, 0, 5, TimeUnit.MINUTES);

    }

    @Override
    public void loadPostits() {
        if (postitModule != null) {
            synchronized (POSTIT_LOCK) {
                postitModule.clear();// lastRead == 0
                postitModule.loadPostits(userService.getPostits(dataCache.getUser().getId(), 0));
                Postit bookings = userService.getBookingAlert();
                if (bookings != null) {
                    postitModule.addPostit(bookings);
                }
            }
        }
    }

    /**
     * Inits dispatcher and runs listening thread.
     *
     * @throws IOException
     */
    protected void launchDispatcher() throws IOException {
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
                        nerr = 0;   //ERIC 2.17 
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
        return postitModule;
    }

    /**
     * Cleanup and closing.
     *
     * @throws GemCloseVetoException
     */
    protected void close() throws GemCloseVetoException {
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
        if (postitScheduledExecutor != null) {
            postitScheduledExecutor.shutdown();
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

    /**
     * Sends an event to the dispatcher and to listeners implementing
     * GemEventListener.
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
//        System.out.println("AbstractDesktopCtrl.forwardEvent:"+evt);
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i
                -= 2) {
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
                    postitModule.addPostit(((CreatePostitEvent) evt).getPostit());
                }
            } else {
                postitModule.addPostit(((CreatePostitEvent) evt).getPostit());
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

    public abstract void addModule(GemModule module, boolean iconified);
    public abstract void retourModule(GemModule module, boolean arbre);

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
    public abstract void removeModule(GemModule module);

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
    public abstract GemModule getSelectedModule();

    @Override //ERIC 2.17
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
@Override
public void showModule(String key) {
    
}
    /**
     * Sets wait cursor in <strong>foreground</strong>.
     */
    @Override
    public void setWaitCursor() {
        RootPaneContainer root = ((RootPaneContainer) frame.getRootPane().getTopLevelAncestor());
        root.getGlassPane().setCursor(waitCursor);
        root.getGlassPane().setVisible(true);
        //frame.setCursor(waitCursor);
    }

    /**
     * Sets default cursor.
     */
    @Override
    public void setDefaultCursor() {
        RootPaneContainer root = ((RootPaneContainer) frame.getRootPane().getTopLevelAncestor());
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
    protected void initDispatcher() throws UnknownHostException, IOException {
        dispatcher = new Socket(props.getProperty("hostdispatcher"), DesktopDispatcher.DEFAULT_SOCKET_PORT);
        // dispatcher.getPort() -> DEFAULT_SOCKET_PORT
        //InetAddress ia = dispatcher.getLocalAddress();
        remoteId = dataCache.getUser().getLogin() + "/" + dispatcher.toString();
        GemLogger.log(Level.INFO, "remoteId " + remoteId);
        iDispatcher = new ObjectInputStream(dispatcher.getInputStream());
        oDispatcher = new ObjectOutputStream(dispatcher.getOutputStream());
        GemLogger.log(Level.INFO, "Connexion dispatcher ok");
    }

    /**
     * Closes socket.
     *
     * @throws IOException
     */
    protected void closeDispatcher() throws IOException {
        if (dispatcher != null && !dispatcher.isClosed()) {
            dispatcher.close();
        }
    }

    /**
     * Gets main menu bar.
     *
     * @return a jMenuBar
     */
    protected abstract void initMenuBar();

        /**
     * Gets main menu bar.
     *
     * @return a jMenuBar
     */
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenuItem menu;

        System.out.println("Bundle:"+BundleUtil.getLabel("Menu.file.label"));
        menuBar = new JMenuBar();

        mFile = new JMenu(BundleUtil.getLabel("Menu.file.label"));
        mFile.setMnemonic('f');
        menu = mFile.add(new JMenuItem(BundleUtil.getLabel("Menu.postit.label"), 'p'));
        menu.addActionListener(this);
        mFile.addSeparator();

        

        menu = mFile.add(new JMenuItem(BundleUtil.getLabel("Menu.quit.label"), 'q'));
        menu.addActionListener(this);

        menuBar.add(mFile);

        return menuBar;
    }
        


    protected void detachCurrent() {
        System.out.println("detacheCurrent");
        GemModule m = getSelectedModule();
        if (m == null || m == postitModule) {
            return;
        }

        removeModule(m);
        m.setNode(null);

        Container c = m.getView().getContentPane();
        FrameDetach fd = new FrameDetach(this, m, c);
        fd.setSize(c.getSize());
        fd.setVisible(true);
    }

    protected JMenuItem getMenuItem(String id) {
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
