/*
 * @(#)MenuConfig.java 2.9.4.13 06/10/15
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

package net.algem.util.menu;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.algem.bank.BankSearchCtrl;
import net.algem.bank.BranchCreateCtrl;
import net.algem.bank.BranchSearchCtrl;
import net.algem.config.*;
import net.algem.contact.CityCtrl;
import net.algem.contact.member.RehearsalPass;
import net.algem.contact.member.RehearsalPassIO;
import net.algem.contact.member.RehearsalPassListCtrl;
import net.algem.course.ModulePresetDlg;
import net.algem.planning.VacationCtrl;
import net.algem.room.EstabCreateCtrl;
import net.algem.room.EstabSearchCtrl;
import net.algem.security.RightsSearchCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.DefaultGemModule;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;

/**
 * Configuration menu.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.6.a 12/10/2012
 */
public class MenuConfig 
  extends GemMenu
{

  private static final HashMap<String, String> menus = new HashMap<String, String>();
  static {
    initLabels();
  }
  
  private JMenu mEstab;
  private JMenuItem miEstabBrowse;
  private JMenuItem msEstabCreate;
  private JMenuItem mBankBranchBrowse;
  private JMenuItem mBankBranchCreate;
  private JMenuItem mVacancy;
  private JMenu mParameters;
  private JMenu mAdmin;
  private JMenuItem miRightsBrowse;
  
  public MenuConfig(GemDesktop _desktop) {
    super(menus.get("Menu.configuration.label"), _desktop);
    mParameters = new JMenu(menus.get("Menu.parameters.label"));
    mParameters.add(getItem(new JMenuItem(menus.get("Menu.general.parameters.label")), "Configuration.management.auth"));
    mParameters.addSeparator();
    mParameters.add(new JMenuItem(menus.get("Menu.instrument.label")));
    mParameters.add(new JMenuItem(menus.get("Menu.style.label")));
    mParameters.addSeparator();
    mParameters.add(new JMenuItem(menus.get("Menu.occupational.cat.label")));
    mParameters.add(new JMenuItem(menus.get("Marital.status.label")));
    
    mParameters.addSeparator();
    
    mParameters.add(new JMenuItem(menus.get("Menu.course.codes.label")));
    mParameters.add(new JMenuItem(menus.get("Status.label")));
    mParameters.add(new JMenuItem(menus.get("Level.label")));
    mParameters.add(new JMenuItem(BundleUtil.getLabel("Menu.age.range.label")));
    mParameters.addSeparator();
    
    mParameters.add(new JMenuItem(menus.get("Menu.telephone.type.label")));
    mParameters.add(new JMenuItem(menus.get("Menu.city.label")));
    mParameters.add(new JMenuItem(menus.get("Menu.web.site.cat.label")));   
    mParameters.addSeparator();
    
    mParameters.add(getItem(new JMenuItem(menus.get("Menu.color.label")), "Color.preferences.auth"));
    mParameters.add(new JMenuItem(menus.get("Theme.label")));
    add(mParameters);
    addSeparator();
    
    add(new JMenuItem(menus.get("Menu.school.label")));
    mEstab = new JMenu(menus.get("Menu.establishment.label"));
    miEstabBrowse = mEstab.add(GemCommand.VIEW_EDIT_CMD);
    msEstabCreate = mEstab.add(GemCommand.CREATE_CMD);
    if (!dataCache.authorize("Establishment.creation.auth")) {
      msEstabCreate.setEnabled(false);
    }
    add(mEstab);
    addSeparator();
    
    add(new JMenuItem(menus.get("Menu.bank.label")));
    JMenu mGuichet = new JMenu(menus.get("Menu.branch.bank.label"));
    mBankBranchBrowse = mGuichet.add(BundleUtil.getLabel("Action.view.edit.label"));
    mBankBranchCreate = mGuichet.add(BundleUtil.getLabel("Action.create.label"));
    add(mGuichet);
    addSeparator();
       
    add(new JMenuItem(menus.get("Menu.card.label")));
    mVacancy = new JMenu(menus.get("Menu.holidays.label"));
    mVacancy.add(new JMenuItem(menus.get("Menu.holidays.cat.label")));
    mVacancy.add(new JMenuItem(menus.get("Menu.periods.label")));
    add(mVacancy);
    addSeparator();
    
    mAdmin = new JMenu(menus.get("Menu.admin.label"));
    miRightsBrowse = getItem(new JMenuItem(BundleUtil.getLabel("Rights.management.label")), "Rights.management.auth");
    mAdmin.add(miRightsBrowse);
    mAdmin.addSeparator();
    mAdmin.add(new JMenuItem(menus.get("Menu.cache.label")));
    /* mSauve = new JMenu("Base de donnée"); mSauve.getItem(new JMenuItem("Sauvegarde
     * (dump de la base)")); mSauve.getItem(new JMenuItem("Restauration depuis
     * un dump")); mSauve.getItem(new JMenuItem("Création")); */
    add(mAdmin);
    setListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    
    Object src = e.getSource();
    String arg = e.getActionCommand();
    DataConnection dc = DataCache.getDataConnection();
    desktop.setWaitCursor();
    if (menus.get("Menu.general.parameters.label").equals(arg)) {
      ConfigEditor confEditor = new ConfigEditor(desktop);
      desktop.addPanel(ConfigEditor.GLOBAL_CONFIG_KEY, confEditor, GemModule.L_SIZE);
    } else if (menus.get("Menu.instrument.label").equals(arg)) {
      InstrumentCtrl instrumentCtrl = new InstrumentCtrl(desktop);
      instrumentCtrl.load();
      desktop.addPanel("Menu.instrument", instrumentCtrl);
    } else if (menus.get("Menu.style.label").equals(arg)) {
      MusicStyleCtrl styleCtrl = new MusicStyleCtrl(desktop);
      styleCtrl.load();
      desktop.addPanel("Menu.style", styleCtrl);
    } else if (menus.get("Menu.occupational.cat.label").equals(arg)) {
      CategoryOccupCtrl occupCatCtrl = new CategoryOccupCtrl(desktop);
      occupCatCtrl.load();
      desktop.addPanel("Menu.occupational.cat", occupCatCtrl);
    } else if (menus.get("Marital.status.label").equals(arg)) {
      MaritalStatusCtrl maritalStatus = new MaritalStatusCtrl(desktop);
      maritalStatus.load();
      desktop.addPanel("Marital.status", maritalStatus);
    } else if (menus.get("Menu.web.site.cat.label").equals(arg)) {
      CategoryWebSiteCtrl siteCatCtrl = new CategoryWebSiteCtrl(desktop);
      siteCatCtrl.load();
      desktop.addPanel("Menu.web.site.cat", siteCatCtrl);
    } else if (menus.get("Menu.course.codes.label").equals(arg)) {
      CourseCodeCtrl ccCtrl = new CourseCodeCtrl(desktop);
      ccCtrl.load();
      desktop.addPanel("Course.code", ccCtrl);
    } else if (menus.get("Status.label").equals(arg)) {
      StatusCtrl statutCtrl = new StatusCtrl(desktop, arg);
      statutCtrl.load();
      desktop.addPanel("Status", statutCtrl);
    } else if (menus.get("Level.label").equals(arg)) {
      LevelCtrl levelCtrl = new LevelCtrl(desktop, arg);
      levelCtrl.load();
      desktop.addPanel("Level", levelCtrl);
    } else if (menus.get("Menu.age.range.label").equals(arg)) {
      AgeRangeSearchCtrl ageRangeCtrl = new AgeRangeSearchCtrl(desktop);
      ageRangeCtrl.addActionListener(this);
      ageRangeCtrl.init();
      desktop.addPanel("Menu.age.range", ageRangeCtrl);
    } else if (menus.get("Menu.telephone.type.label").equals(arg)) {
      TelephoneTypeCtrl typeTelephoneCtrl = new TelephoneTypeCtrl(desktop);
      typeTelephoneCtrl.load();
      desktop.addPanel("Menu.telephone.type", typeTelephoneCtrl);
    }  else if (menus.get("Menu.city.label").equals(arg)) {
      CityCtrl cityCtrl = new CityCtrl(desktop);
      desktop.addPanel("Menu.city", cityCtrl);
    } else if (menus.get("Menu.color.label").equals(arg)) {
      ColorPreviewCtrl cp = new ColorPreviewCtrl(desktop, new ColorPreview(new ColorPrefs()));
      desktop.addPanel("Menu.color", cp);
    } else if(menus.get("Theme.label").equals(arg)) {
      ThemeConfig themePref = new ThemeConfig(desktop);
      themePref.setLAF();
//        desktop.addPanel(themePref, this);
    }else if (menus.get("Menu.school.label").equals(arg)) {
      SchoolCtrl schoolCtrl = new SchoolCtrl(desktop);
      schoolCtrl.load();
      desktop.addPanel("Menu.school", schoolCtrl);
    } else if (src == msEstabCreate) {
      EstabCreateCtrl estabCreate = new EstabCreateCtrl(desktop);
      estabCreate.addActionListener(this);
      estabCreate.init();
      desktop.addPanel("Establishment.create", estabCreate);
      desktop.getSelectedModule().setSize(GemModule.M_SIZE);
    } else if (src == miEstabBrowse) {
      EstabSearchCtrl estabBrowse = new EstabSearchCtrl(desktop, BundleUtil.getLabel("Establishment.browser.label"));
      estabBrowse.addActionListener(this);
      estabBrowse.init();
      estabBrowse.load();
      desktop.addPanel("Establishment.browser", estabBrowse);
      desktop.getSelectedModule().setSize(GemModule.XXL_SIZE);
    } else if (menus.get("Menu.card.label").equals(arg)) {
      RehearsalPassListCtrl ctrl = new RehearsalPassListCtrl(desktop, false);
      try {
        Vector<RehearsalPass> v = RehearsalPassIO.findAll(" ORDER BY libelle", dc);
        ctrl.loadResult(v);
        GemModule m = new DefaultGemModule("Menu.card", ctrl);
        desktop.addModule(m);
      } catch (SQLException ex) {
        GemLogger.log(ex.getMessage());
      }
    } else if (menus.get("Menu.bank.label").equals(arg)) {
      BankSearchCtrl bankCtrl = new BankSearchCtrl(dc);
      bankCtrl.addActionListener(this);
      bankCtrl.init();
      desktop.addPanel("Menu.bank", bankCtrl);
    } else if (src == mBankBranchBrowse) {
      BranchSearchCtrl branchSearchCtrl = new BranchSearchCtrl(dc);
      branchSearchCtrl.addActionListener(this);
      branchSearchCtrl.init();
      desktop.addPanel("Menu.branch.bank", branchSearchCtrl);
    } else if (src == mBankBranchCreate) {
      BranchCreateCtrl branchCreate = new BranchCreateCtrl(dc);
      branchCreate.addActionListener(this);
      branchCreate.init();
      desktop.addPanel("Menu.branch.bank.create", branchCreate);
    } else if (menus.get("Menu.holidays.cat.label").equals(arg)) {
      CategoryVacancyCtrl vacancyCatCtrl = new CategoryVacancyCtrl(desktop);
      vacancyCatCtrl.load();
      desktop.addPanel("Menu.holidays.cat", vacancyCatCtrl);
    } else if (menus.get("Menu.periods.label").equals(arg)) {
      VacationCtrl vacancyCtrl = new VacationCtrl(dataCache);
      desktop.addPanel("Menu.periods", vacancyCtrl);
    } else if (src == miRightsBrowse) {
      RightsSearchCtrl rightsCtrl = new RightsSearchCtrl(dataCache);
      rightsCtrl.addActionListener(this);
      rightsCtrl.init();
      desktop.addPanel("Rights.management", rightsCtrl);
    } else if (menus.get("Menu.cache.label").equals(arg)) {
      dataCache.load(null);
    } 
    /*else if (source == miServer) {
      ServeurCtrl serverCtrl = new ServeurCtrl(dataCache.getDataConnection());
      desktop.addPanel("DataCache", serverCtrl);
    } */
    else if (GemCommand.CANCEL_CMD.equals(arg)) {
      desktop.removeCurrentModule();
    }
    desktop.setDefaultCursor();
  }
  
  private static void initLabels() {

    menus.put("Menu.configuration.label", BundleUtil.getLabel("Menu.configuration.label"));
    menus.put("Menu.establishment.label", BundleUtil.getLabel("Menu.establishment.label"));
    menus.put("Menu.school.label", BundleUtil.getLabel("Menu.school.label"));
    menus.put("Menu.bank.label", BundleUtil.getLabel("Menu.bank.label"));
    menus.put("Menu.branch.bank.label", BundleUtil.getLabel("Menu.branch.bank.label"));
    menus.put("Menu.holidays.label", BundleUtil.getLabel("Menu.holidays.label"));
    menus.put("Menu.holidays.cat.label", BundleUtil.getLabel("Menu.holidays.cat.label"));
    menus.put("Menu.periods.label", BundleUtil.getLabel("Menu.periods.label"));
    menus.put("Menu.card.label", BundleUtil.getLabel("Menu.card.label"));
    menus.put("Menu.parameters.label", BundleUtil.getLabel("Menu.parameters.label"));
    menus.put("Menu.general.parameters.label", BundleUtil.getLabel("Menu.general.parameters.label"));
    menus.put("Menu.instrument.label", BundleUtil.getLabel("Menu.instrument.label"));   
    menus.put("Menu.style.label", BundleUtil.getLabel("Menu.style.label"));
    menus.put("Menu.occupational.cat.label", BundleUtil.getLabel("Menu.occupational.cat.label"));
    menus.put("Marital.status.label", BundleUtil.getLabel("Marital.status.label"));
    menus.put("Menu.web.site.cat.label", BundleUtil.getLabel("Menu.web.site.cat.label"));
    menus.put("Status.label", BundleUtil.getLabel("Status.label"));
    menus.put("Level.label", BundleUtil.getLabel("Level.label"));
    menus.put("Menu.age.range.label", BundleUtil.getLabel("Menu.age.range.label"));
    menus.put("Menu.telephone.type.label", BundleUtil.getLabel("Menu.telephone.type.label"));
    menus.put("Menu.city.label", BundleUtil.getLabel("Menu.city.label"));
    menus.put("Menu.color.label", BundleUtil.getLabel("Menu.color.label"));
    menus.put("Menu.admin.label", BundleUtil.getLabel("Menu.administration.label"));
    menus.put("Menu.cache.label", BundleUtil.getLabel("Menu.cache.label"));
    menus.put("Menu.course.codes.label", BundleUtil.getLabel("Menu.course.codes.label"));
    menus.put("Theme.label", BundleUtil.getLabel("Theme.label"));

  }
}
