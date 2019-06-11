/*
 * @(#)StudentExportDlg.java 2.15.8 22/03/18
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
package net.algem.edition;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import net.algem.contact.Person;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Abstract class for student export operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.6.a 06/11/2012
 */
public abstract class StudentExportDlg
  extends ExportDlg {

  protected JRadioButton rdLeisure, rdPro, rdAll;
  protected DateRangePanel dateRange;
  protected GridBagHelper gb;
  protected ExportService service;
  protected int nextRow;
  protected DataCache dataCache;
  protected JCheckBox directToMail;

  public StudentExportDlg(GemDesktop desktop) {
    this(desktop, BundleUtil.getLabel("Export.student.title"));
  }

  public StudentExportDlg(GemDesktop desktop, String title) {
    super(desktop, title);
    this.dataCache = desktop.getDataCache();
    service = new ExportService(DataCache.getDataConnection());
  }

  @Override
  public GemPanel getCriterion() {
    GemPanel outerPanel = new GemPanel();
    outerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    GemPanel pCriterion = new GemPanel();
    pCriterion.setLayout(new GridBagLayout());
    pCriterion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    outerPanel.add(pCriterion);

    gb = new GridBagHelper(pCriterion);

    dateRange = new DateRangePanel(desktop.getDataCache().getStartOfYear(), desktop.getDataCache().getEndOfYear());
    directToMail = new JCheckBox(BundleUtil.getLabel("Export.direct.to.mail.label"));
    directToMail.setToolTipText(BundleUtil.getLabel("Export.direct.to.mail.tip"));

    ButtonGroup status = new ButtonGroup();

    rdLeisure = new JRadioButton(BundleUtil.getLabel("Leisure.label"));
    rdLeisure.setBorder(null);
    rdPro = new JRadioButton(BundleUtil.getLabel("Pro.label"));
    rdAll = new JRadioButton(BundleUtil.getLabel("Leisure+Pro.label"));
    rdAll.setSelected(true);
    status.add(rdLeisure);
    status.add(rdPro);
    status.add(rdAll);

    GemPanel statusPanel = new GemPanel();

    statusPanel.add(rdLeisure);
    statusPanel.add(rdPro);
    statusPanel.add(rdAll);

    setPanel();
    if (nextRow > 0) {
      gb.add(new JLabel(BundleUtil.getLabel("Status.label")), 0, nextRow, 1, 1, GridBagHelper.WEST);
      gb.add(statusPanel, 1, nextRow, 1, 1, GridBagHelper.WEST);
      gb.add(directToMail, 1, nextRow + 1, 1, 1, GridBagHelper.WEST);
    }

    return outerPanel;
  }

  /**
   * Sets the specific panel for each subclass.
   */
  abstract protected void setPanel();

  /**
   * Gets the appropriate SQL request.
   *
   * @return a string
   */
  @Override
  abstract public String getRequest();

  @Override
  protected final void validation() {
    String query = getRequest();
    String path = null;
    if (file == null) {
      path = fileName.getText();
    } else {
      path = file.getPath();
    }
    Collections.sort(selectedOptions);
    try (PrintWriter out = new PrintWriter(new File(path), "UTF-16LE")) {
      List<Person> list = service.getContacts(query);
      if (directToMail.isSelected()) {
        DesktopMailHandler mail = new DesktopMailHandler();
        mail.send(service.getUserEmail(dataCache.getUser()), service.getBcc(list));
      } else {
        file = new File(fileName.getText());
        if (!writeFile()) {
          return;
        }

        int counter = service.printCSV(out, list, selectedOptions);
        MessagePopup.information(this, MessageUtil.getMessage("export.success.info", new Object[]{counter, path}));
      }
    } catch (IOException ie) {
      GemLogger.logException(ie);
      MessagePopup.warning(this, MessageUtil.getMessage("file.path.exception", fileName.getText()));
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    }

  }

  @Override
  protected String getFileName() {
    return BundleUtil.getLabel("Export.student.file");
  }
}
