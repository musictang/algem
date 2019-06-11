/*
 * @(#)CourseModulePanel.java	2.8.t 16/04/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

package net.algem.course;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import net.algem.config.GemParam;
import net.algem.config.GemParamChoice;
import net.algem.contact.InfoPanel;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.model.GemList;
import net.algem.util.ui.ButtonRemove;
import net.algem.util.ui.GemChoiceModel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.a 13/03/2013
 */
public class CourseModulePanel
  extends InfoPanel
{
  private CourseModuleInfo info;
  private GemParamChoice code;
  private HourField hf;

  public CourseModulePanel(CourseModuleInfo info, GemList<CourseCode> codeList, ActionListener listener) {
    this.info = info;
    setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.MEDIUM_INSETS;

    hf = new HourField(new Hour(info.getTimeLength()));
    if (info.getCode() != null
      && (info.getCode().getId() == CourseCodeType.ATP.getId()
      || info.getCode().getId() == CourseCodeType.STG.getId())
      ) {
      hf.setEnabled(false);
    }
    hf.setToolTipText(BundleUtil.getLabel("Course.length.tip"));

    code = new GemParamChoice(new GemChoiceModel<CourseCode>(codeList));
    code.setKey(info.getIdCode());
    code.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        int key = code.getKey();
        if (key == CourseCodeType.ATP.getId() || key == CourseCodeType.STG.getId()) {
          hf.setEnabled(false);
        } else {
          hf.setEnabled(true);
        }
      }

    });

    ButtonRemove minus = new ButtonRemove(this);
    minus.setToolTipText(GemCommand.DELETE_CMD);
    minus.addActionListener(listener);

    gb.add(Box.createVerticalStrut(4), 0, 0, 4, 1, GridBagHelper.WEST);
    gb.add(code, 0, 1, 2, 1, GridBagHelper.WEST);
    gb.add(hf, 2, 1, 1, 1);
    gb.add(minus, 3, 1, 1, 1, GridBagHelper.EAST);

  }

  public CourseModuleInfo get() {
    info.setCode((GemParam) code.getSelectedItem());
    info.setTimeLength(hf.get().toMinutes());
    return info;
  }

}
