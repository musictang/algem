/*
 * @(#)InstrumentStudentExportDlg.java 2.10.0 20/05/16
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import javax.swing.JLabel;
import net.algem.config.InstrumentChoice;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GridBagHelper;

/**
 * Export dialog for contact infos of students playing the instrument selected.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.6.d 06/11/2012
 */
public class InstrumentStudentExportDlg
  extends StudentExportDlg
{

  private GemChoice instrument;
  
  public InstrumentStudentExportDlg(GemDesktop desktop) {
    super(desktop);
  }

  @Override
  protected void setPanel() {
    
    instrument = new InstrumentChoice(desktop.getDataCache().getInstruments());
    instrument.setPreferredSize(typeContact.getPreferredSize());
    
    gb.add(new JLabel(BundleUtil.getLabel("Instrument.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(instrument, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(typeContact, 1, 2, 1, 1, GridBagHelper.WEST);
    
    nextRow = 3;
  }

  /**
   * The query is about the people practicing the selected instrument,
   * regardless of the courses they are registered during the period.
   * @return a SQL-string
   */
  @Override
  public String getRequest() {
    Boolean pro = rdPro.isSelected() ? Boolean.valueOf(true) : (rdLeisure.isSelected() ? Boolean.valueOf(false) : null);
    return service.getContactQueryByInstrument(instrument.getKey(), dateRange.getStart(), dateRange.getEnd(), pro);
  }

}
