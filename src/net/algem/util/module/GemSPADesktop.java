/*
 * @(#)GemSPADesktop.java	3.0.0  13/09/2021
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
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
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.text.Position;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.algem.contact.*;
import net.algem.edition.*;
import net.algem.enrolment.EnrolmentListCtrl;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.ExtendeModuleOrderListCtrl;
import net.algem.enrolment.ExtendedModuleOrderTableModel;
import net.algem.group.GroupSearchCtrl;
import net.algem.planning.day.DayScheduleCtrl;
import net.algem.planning.month.MonthScheduleCtrl;
import net.algem.util.*;
import net.algem.util.menu.MenuAccounting;
import net.algem.util.menu.MenuCatalog;
import net.algem.util.menu.MenuConfig;
import net.algem.util.menu.MenuHelp;
import net.algem.util.menu.MenuPlanning;
import net.algem.util.menu.MenuSearch;
import static net.algem.util.menu.MenuSearch.CONTACT_BROWSER_KEY;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.postit.PostitCreateCtrl;
import net.algem.util.postit.PostitModule2;
import net.algem.util.ui.FrameDetach;
import net.algem.util.ui.GemTreeNode;
import net.algem.util.ui.Toast;

/**
 * Algem Single Page desktop controller. This is the working space of the
 * application.
 *
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 * @since 3.0.0
 */
public class GemSPADesktop extends AbstractDesktopCtrl {

    private final JSplitPane desktop;
    private final JTree tree;
    private final Container pages;
    private final CardLayout pagesCard;
    private final GemTreeNode root;

    private GemModule currentModule;

    private JPanel postitPanel;

    private GemTreeNode planningNode;
    private GemTreeNode contactNode;
    private GemTreeNode rechercheNode;
    private GemTreeNode inscriptionNode;
    private GemTreeNode moduleNode;
    private GemTreeNode diversNode;

    private DayScheduleCtrl dayScheduleCtrl;
    private MonthScheduleCtrl monthScheduleCtrl;
    private PersonFileSearchCtrl contact;
    private GroupSearchCtrl group;

    public GemSPADesktop(JFrame frame, DataCache dataCache, Properties props) {
        super(frame, dataCache, props);

        JSplitPane droit = new JSplitPane();
        droit.setLayout(new BorderLayout());

        pages = new Container();
        pagesCard = new CardLayout();
        pages.setLayout(pagesCard);
        pages.setMinimumSize(new Dimension(800, 600));
        root = new GemTreeNode("Algem3");
        tree = new JTree(root);
        tree.setMinimumSize(new Dimension(200, 600));
        tree.setBackground(net.algem.Algem.BGCOLOR_DESKTOP);

        postitPanel = new JPanel(new BorderLayout());
        postitPanel.setMinimumSize(new Dimension(800, 80));

        droit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pages, postitPanel);
        droit.setResizeWeight(0.85);

        desktop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, droit);
        setContentPane(desktop);

        initMenuBar();

        addPostit();

        planningNode = new GemTreeNode("Planning");
        root.add(planningNode);

        contactNode = new GemTreeNode("Contacts");
//        rechercheNode = new GemTreeNode("Rechercher");
//        contactNode.add(rechercheNode);
        root.add(contactNode);

        inscriptionNode = new GemTreeNode("Inscriptions");
        root.add(inscriptionNode);

        moduleNode = new GemTreeNode("Modules");
        root.add(moduleNode);

        diversNode = new GemTreeNode("Divers");
        root.add(diversNode);

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                GemTreeNode node = (GemTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                if (node.getUserObject().equals("Planning") && !hasModule(BundleUtil.getLabel(MenuPlanning.MENU_DAY_KEY + ".label"))) {
                    if (!hasModule("Jour2")) {
                        DayScheduleCtrl dayPlanning = new DayScheduleCtrl("Jour2");
                        dayPlanning.setLabel("Jour2");
                        addModuleSPA(planningNode, dayPlanning, true);
                    }
                } else {
                    pagesCard.show(pages, node.getUserObject().toString());
                    currentModule = modules.get(node.getUserObject().toString());
                }

            }
        });
        monthScheduleCtrl = new MonthScheduleCtrl(MenuPlanning.MENU_MONTH_KEY);
        monthScheduleCtrl.setRemovable(false);
        addModuleSPA(planningNode, monthScheduleCtrl, true);

        dayScheduleCtrl = new DayScheduleCtrl(MenuPlanning.MENU_DAY_KEY);
        dayScheduleCtrl.setRemovable(false);
        addModuleSPA(planningNode, dayScheduleCtrl, true);

        contact = new PersonFileSearchCtrl(this, null);
        contact.addActionListener(this);
        contact.init();
        contact.setSize(640, 480);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        contact.setBorder(blackline);
        addPanelSPA(contactNode, CONTACT_BROWSER_KEY, contact, false);

        EnrolmentListCtrl enrolmentList = new EnrolmentListCtrl(this);
        //enrolmentList.addActionListener(this);
        addPanelSPA(inscriptionNode, MenuCatalog.ENROLMENT_BROWSER_KEY, enrolmentList, false);

        final EnrolmentService service = new EnrolmentService(dataCache);
        final ExtendeModuleOrderListCtrl orderListCtrl = new ExtendeModuleOrderListCtrl(this, service, new ExtendedModuleOrderTableModel());
        orderListCtrl.load(dataCache.getStartOfYear().getDate(), dataCache.getEndOfYear().getDate());
        addPanelSPA(inscriptionNode, MenuCatalog.ORDERED_BROWSER_KEY, orderListCtrl, false);

        try {
            loadModules();
        } catch (IOException ex) {
            GemLogger.logException(ex);
        }

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
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
                            addModuleSPA(contactNode, editor, true);

                        }
                    }
                }
            }
        } catch (Exception e) {
            GemLogger.logException("GemModuleSid", e);
        }
    }

    @Override
    protected void addPostit() {
//        GemLogger.info("GemDesltop2Ctrl.addPostit");
        postitModule = new PostitModule2(userService); // postit windows
        addPostitModule((PostitModule2) postitModule);

        postitScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        postitScheduledExecutor.scheduleAtFixedRate(this::loadPostits, 0, 5, TimeUnit.MINUTES);

    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String arg = evt.getActionCommand();
        Object src = evt.getSource();

        //System.out.println("GemSPADesktop.actionPerformed:" + evt);

        setWaitCursor();

        if (BundleUtil.getLabel(GemCommand.CLOSE_CMD).equals(arg)
                || BundleUtil.getLabel("Menu.quit.label").equals(arg)) {
            System.out.println("GemDPADesktop.actionPerformed try close/exit");
            savePrefs = (evt.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
            try {
                close();
                System.exit(0);
            } catch (GemCloseVetoException e) {
                GemLogger.log("GemSPADesktop CloseVetoException");
            }
        } else if (BundleUtil.getLabel("Menu.postit.label").equals(arg)) {
            postitCreate = new PostitCreateCtrl(this, userService);
            postitCreate.addActionListener(this);
            addPanel(PostitCreateCtrl.POSTIT_CREATE_KEY, postitCreate);
        } else if (BundleUtil.getLabel("Menu.import.csv.contacts.label").equals(arg)) {
            ImportCsvCtrl importCtrl = new ImportCsvCtrl(new ImportCsvHandler());
            importCtrl.addActionListener(this);
            importCtrl.createUI();
            addPanel("Menu.import.csv.contacts", importCtrl, new Dimension(905, 640));
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
        } else if ("Menu.windows.detach".equals(arg)) {
            detachCurrent();
        } else if (GemCommand.CLOSE_CMD.equals(arg) || GemCommand.CANCEL_CMD.equals(arg)) {
            GemLogger.log("GemDSPAesktop CANCEL/CLOSE:" + src);
            GemLogger.log("Call removeCurrent:" + getSelectedModule());
            removeCurrentModule();
        } else if (src == miSaveUISettings) {
            storeUISettings();
            Toast.showToast(desktop, getUIInfo());
        } else {
            Iterator it = menus.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                GemLogger.log("GemSPADesktop.actionPerformed menu.iterator:" + me);

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
    protected void detachCurrent() {
        GemModule m = getSelectedModule();
        if (m == null || m == postitModule) {
            return;
        }

        removeModule(m);

        Container c = m.getView().getContentPane();
        FrameDetach fd = new FrameDetach(this, m, c);
        fd.setSize(c.getSize());
        fd.setVisible(true);
    }

    @Override
    public void addPanel(String s, Container p) {
        GemLogger.info("GemSPADesktop.addPanel Classe:" + p.getClass().getName());
        GemModule m = new DefaultGemModule(s, p);
        addModule(m);
    }

    public void addModule(GemModule module, boolean iconified) { //called by old module
        GemLogger.info("GemSPADesktop.addModule Classe:" + module.getClass().getName());
        switch (module.getClass().getName()) {
            case "net.algem.contact.PersonFileEditor":
                addModuleSPA(contactNode, module, true);
                break;
            default:
                addModuleSPA(moduleNode, module, true);
        }
    }

    public void addPanelSPA(GemTreeNode node, String s, Container p, boolean removable) {
        GemModule m = new DefaultGemModule(s, p);
        m.setRemovable(removable);
        addModuleSPA(node, m, true);
    }

    @Override
    public void retourModule(GemModule module, boolean node) {
        //System.out.println("GemSPADesktop.retourModule:" + module+" node="+module.getTreeNode());
        addModuleSPA(module.getTreeNode(), module, true);
    }

    @Override
    public void showModule(String label) {
        if (hasModule(label)) {
            pagesCard.show(pages, BundleUtil.getLabel(label + ".label"));
        }
    }

    public void resetPath() {
        TreePath path = tree.getNextMatch("Planning jour", 0, Position.Bias.Forward);
        tree.setSelectionPath(path);
    }

    
    public void addModuleSPA(GemTreeNode treeNode, GemModule module, boolean withNode) {

        String labKey = module.getLabel().startsWith("Fiche:") ? module.getLabel() : BundleUtil.getLabel(module.getLabel() + ".label");
        if (hasModule(labKey)) {
            GemLogger.info("GemSPADesktop duplicate addModule " + labKey);
            pagesCard.show(pages, labKey);
            currentModule = modules.get(labKey);
            GemLogger.info("setCurrent:" + currentModule);
            return;
        }
        modules.put(labKey, module);
        module.setDesktop2(this);	// createIHM
        module.setTreeNode(treeNode);

        BasicInternalFrameUI bi = (BasicInternalFrameUI) module.getView().getUI();
        bi.setNorthPane(null);

        pages.add(labKey, module.getView());
        pagesCard.show(pages, labKey);

        if (withNode) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            GemTreeNode node = new GemTreeNode(labKey);
            model.insertNodeInto(node, treeNode, 0);
            tree.setSelectionPath(new TreePath(node.getPath()));
            module.setNode(node);
        }
        currentModule = module;
        //System.out.println("addModuleSPA currentModule="+currentModule);


    }

    public void addPostitModule(PostitModule2 module) {

        modules.put(module.getLabel(), module);
        module.setDesktop2(this);	// createIHM

        postitPanel.add(module.getPanel());

    }

    @Override
    public void removeModule(GemModule module) {
        if (module == null || module == postitModule || !module.isRemovable()) {
            return;
        }
        pages.remove(module.getView());
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath tp = tree.getPathForRow(i);
        }
        if (module.getNode() != null) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            try {
                model.removeNodeFromParent(module.getNode()); // TODO voir PersonFileEditor.closeModule()
            } catch (Exception e) {
                GemLogger.logException("GSPA removeModule:" + module, e);
            }
        }
        String labKey = module.getLabel().startsWith("Fiche:") ? module.getLabel() : BundleUtil.getLabel(module.getLabel() + ".label");
        modules.remove(labKey);
        resetPath();
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

        mCatalog = new MenuCatalog(this, 2);
        mCatalog.setMnemonic('c');
        mbar.add(mCatalog);

        mPerson = new MenuSearch(this);
        mPerson.setMnemonic('e');
        mbar.add(mPerson);

        mPlanning = new MenuPlanning(this, 2);
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
    }

    @Override
    public GemModule getSelectedModule() {
        return currentModule;
    }

}
