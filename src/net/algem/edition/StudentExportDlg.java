/*
 * @(#)StudentExportDlg.java 2.6.g 19/11/12
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

package net.algem.edition;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import net.algem.contact.Person;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Abstract class for student export operations.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.g
 * @since 2.6.a 06/11/2012
 */
public abstract class StudentExportDlg
  extends ExportDlg
{
 
  /** Type of info (all, mail only). */
  protected JComboBox typeContact;
  
  protected DateRangePanel dateRange; 
  protected GridBagHelper gb;
  protected ExportService service;

  public StudentExportDlg(Frame _parent, DataCache dc) {
    this(_parent, BundleUtil.getLabel("Export.student.title"), dc);
  }
  
  public StudentExportDlg(Frame _parent, String title, DataCache dc) {
    super(_parent, title, dc);
    service = new ExportService(dc.getDataConnection());
  }
  
  @Override
  public GemPanel getCriterion() {
    GemPanel pCriterion = new GemPanel();
    pCriterion.setLayout(new GridBagLayout());

    gb = new GridBagHelper(pCriterion);
    gb.insets = GridBagHelper.SMALL_INSETS;
    
    
    dateRange = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfYear());
    String[] category = {
      BundleUtil.getLabel("Contact.full.information.label"),
      BundleUtil.getLabel("Email.label")
    };
    typeContact = new JComboBox(category);
    setPanel();
    
    return pCriterion;
  }
  
  /**
   * Sets the specific panel for each subclass.
   */
  abstract protected void setPanel();
  
  /**
   * Gets the appropriate SQL request.
   * @return a string
   */
  @Override
  abstract public String getRequest();
  
  @Override
  protected final void validation() {
    PrintWriter out = null;
    int counter = 0;
    String query = getRequest();
    String path = null;
    if (file == null) {
      path = fileName.getText();
    } else {
      path = file.getPath();
    }
    try {
        out = new PrintWriter(new FileWriter(path));
        List<Person> list = service.getContacts(query);
        if (typeContact.getSelectedIndex() == 0) {
          counter = service.printCSV(out, list);
          MessagePopup.information(this, MessageUtil.getMessage("export.success.info", new Object[]{counter, path}));
        } else {
          DesktopMailHandler mail = new DesktopMailHandler();
          mail.send(service.getUserEmail(dataCache.getUser()), service.getBcc(list));
        }
    } catch(IOException ie) {
      GemLogger.logException(ie);
      MessagePopup.warning(this, MessageUtil.getMessage("file.path.exception", fileName.getText()));
    } catch(SQLException sqe) {
      GemLogger.logException(sqe);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
  
}
