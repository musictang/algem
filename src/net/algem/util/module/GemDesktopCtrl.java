/*
 * @(#)GemDesktopCtrl.java	3.0.0  13/09/2021
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
import java.awt.event.InputEvent;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.*;
import net.algem.contact.*;
import net.algem.edition.*;
import net.algem.planning.day.DayScheduleCtrl;
import net.algem.util.*;
import net.algem.util.menu.*;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.postit.PostitCreateCtrl;
import net.algem.util.postit.PostitModule;
import net.algem.util.ui.Toast;

/**
 * Algem desktop controller. This is the working space of the application.
 *
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 * @since 3.0.0
 */
public class GemDesktopCtrl extends AbstractDesktopCtrl {

    private final JDesktopPane desktop;

    public GemDesktopCtrl(JFrame frame, DataCache dataCache, Properties props) {
        super(frame, dataCache, props);

        desktop = new JDesktopPane();
        desktop.setBackground(net.algem.Algem.BGCOLOR_DESKTOP);
        setContentPane(desktop);
        initMenuBar();
        addPostit();

        try {
            loadModules();
        } catch (IOException ex) {
            GemLogger.logException(ex);
        }
    } // end constructor

    /**
     * Loads serialized modules.
     *
     * @throws IOException
     */
    private void loadModules() throws IOException {

        String path = System.getProperty("user.home") + FileUtil.FILE_SEPARATOR;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + ".gemdesktop"))) {
            java.util.List<GemModuleSID> serList = (ArrayList<GemModuleSID>) ois.readObject();

            for (GemModuleSID sid : serList) {
                if (sid != null) {
                    if (sid.getModuleClass().equals(PersonFileEditor.class
                            .getSimpleName())) {
                        int id = Integer.parseInt(sid.getSID());
                        if (id <= 0) {
                            continue;
                        }
                        Contact c = ContactIO.findId(id, dc);
                        if (c != null) {
                            PersonFile pFile = new PersonFile(c);
                            ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(pFile);
                            PersonFileEditor editor = new PersonFileEditor(pFile);
                            addModule(editor, true);

                        }
                    } else if (sid.getModuleClass().equals(DayScheduleCtrl.class
                            .getSimpleName())) {
                        DayScheduleCtrl dayScheduleCtrl = new DayScheduleCtrl("TableauJour");
                        addModule(dayScheduleCtrl);
                        dayScheduleCtrl.setState(sid.getState());
                        // location initiale du tableau jour.
                        dayScheduleCtrl.getView().setLocation(PostitModule.POSTIT_MODULE_WIDTH, 0);
                        dayScheduleCtrl.mayBeMaximize();
                    }
                }
            }
        } catch (Exception e) {
            GemLogger.logException("GemModuleSid", e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String arg = evt.getActionCommand();
        Object src = evt.getSource();

        setWaitCursor();

        if (BundleUtil.getLabel("Menu.quit.label").equals(arg)) {
            savePrefs = (evt.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
            try {
                close();
                System.exit(0);
            } catch (GemCloseVetoException e) {
            }
        } else if (BundleUtil.getLabel("Menu.postit.label").equals(arg)) {
            postitCreate = new PostitCreateCtrl(this, userService);
            postitCreate.addActionListener(this);
            addPanel(PostitCreateCtrl.POSTIT_CREATE_KEY, postitCreate);
        } else if (BundleUtil.getLabel("Menu.import.csv.contacts.label").equals(arg)) {
            ImportCsvCtrl importCtrl = new ImportCsvCtrl(new ImportCsvHandler());
            importCtrl.addActionListener(this);
            importCtrl.createUI();
            addPanel("Menu.import.csv.contacts", importCtrl.getContentPane(), new Dimension(905, 640));
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
        } else if (BundleUtil.getLabel("Menu.student.by.teacher.label").equals(arg)) {
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

    @Override
    public void retourModule(GemModule module, boolean iconified) {
        addModule(module, iconified);
    }
    
    @Override
    public void addModule(GemModule module, boolean iconified) //public void addModule(GemModule _module, int _layer)
    {
        JMenuItem mItem = new JMenuItem(module.getLabel());
        menus.put(module.getLabel(), mItem);
        mItem.addActionListener(this);

        modules.put(module.getLabel(), module);
        module.setDesktop1(this);	// createIHM
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
    public void removeModule(GemModule module) {
        if (module == null || module == postitModule) {
            return;
        }
        desktop.remove(module.getView());
        desktop.repaint();
        JMenuItem mItem = (JMenuItem) menus.get(module.getLabel());
        mWindows.remove(mItem);

        modules.remove(module.getLabel());
    }

    /**
     * Gets main menu bar.
     *
     * @return a jMenuBar
     */
    protected void initMenuBar() {
        JMenuItem menu;

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

        JMenu mImport = new JMenu(BundleUtil.getLabel("Menu.import.label"));
        menu = mImport.add(new JMenuItem(BundleUtil.getLabel("Menu.import.csv.contacts.label")));
        menu.addActionListener(this);
        mFile.add(mImport);
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

        mbar.add(mWindows);

        mCatalog = new MenuCatalog(this);
        mCatalog.setMnemonic('c');
        mbar.add(mCatalog);

        mPerson = new MenuSearch(this);
        mPerson.setMnemonic('e');
        mbar.add(mPerson);

        mPlanning = new MenuPlanning(this);
        mPlanning.setMnemonic('p');
        mbar.add(mPlanning);

        mVarious = new MenuAccounting(this);
        mVarious.setMnemonic('d');
        mbar.add(mVarious);

        mConfig = new MenuConfig(this);
        mbar.add(mConfig);
        mbar.add(Box.createHorizontalGlue());

        mHelp = new MenuHelp(this);
        mHelp.setMnemonic('A');
        mbar.add(mHelp);

        /* JMenu mAide = new JMenu("Aide"); menu = mAide.add(new JMenuItem("Module
     * courant",'c')); menu.addActionListener(this); menu = mAide.add(new
     * JMenuItem("Index",'i')); menu.addActionListener(this); menu =
     * mAide.add(new JMenuItem("Recherche",'r')); menu.addActionListener(this);
     *
     * menu = mAide.add(new JMenuItem("A propos",'p'));
     * menu.addActionListener(this); mAide.setMnemonic('A');
         */
        //jmb.setHelpMenu(m);
    }

    @Override
    public GemModule getSelectedModule() { //ERIC TableauJour vs Menu.Day.Schedule
        DefaultGemView v = (DefaultGemView) desktop.getSelectedFrame();
        if (v == null) {
            return null;
        }

        Set<Entry<String, GemModule>> entrySet = modules.entrySet();
        for (Entry<String, GemModule> m : entrySet) {
            if (m.getValue().getView().getLabel().equals(v.getLabel())) {
                return m.getValue();
            }
        }
        return null;

        /*
    GemModule m = modules.get(v.getLabel());
    return m;
         */
    }

    private void open() {
        JInternalFrame frames[] = desktop.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].isIcon()
                    && frames[i] != postitModule.getView()) {
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
                    && frames[i] != postitModule.getView()) {
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
                    && frames[i] != postitModule.getView()) {
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
                    && frames[i] != postitModule.getView()) {
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

}
