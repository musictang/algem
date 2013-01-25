/*
 * @(#)MusicianExportDlg.java 2.6.g 19/11/12
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
import javax.swing.JLabel;
import net.algem.config.InstrumentChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.g
 * @since 2.6.a 07/11/2012
 */
public class MusicianExportDlg
  extends StudentExportDlg 
{

  private GemChoice instrument;

  public MusicianExportDlg(Frame _parent, DataCache dc) {
    super(_parent, BundleUtil.getLabel("Group.members.label"), dc);
  }
  
  @Override
  protected void setPanel() {
    instrument = new InstrumentChoice(dataCache.getInstruments());
    instrument.setToolTipText(MessageUtil.getMessage("export.musicians.tip", BundleUtil.getLabel("None.label")));

    GemPanel p = new GemPanel();
    p.add(typeContact);
    gb.add(new JLabel(BundleUtil.getLabel("Instrument.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(instrument, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Period.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(dateRange, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(p, 1, 2, 1, 1, GridBagHelper.WEST);
  }

  @Override
  public String getRequest() {
    return service.getMusicianByInstrument(instrument.getKey(), dateRange.getStart(), dateRange.getEnd());
  }

}
