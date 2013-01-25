/*
 * @(#)ColorPreviewCtrl.java	2.6.a 03/10/12
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
package net.algem.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ColorPreviewCtrl extends GemPanel implements ActionListener {

  private ColorPreview preview;
  private GemDesktop desktop;

  public ColorPreviewCtrl(GemDesktop desktop, ColorPreview cp) {
    preview = cp;
    Dimension d = new Dimension(400, 200);
    this.desktop = desktop;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    preview.setPreferredSize(d);

    JPanel helpPanel = new JPanel();
    JLabel h1 = new JLabel(MessageUtil.getMessage("prefs.background.color.tip"));
    JLabel h2 = new JLabel(MessageUtil.getMessage("prefs.foreground.color.tip"));

    helpPanel.setPreferredSize(new Dimension(d));
    helpPanel.add(h1);
    helpPanel.add(h2);

    JPanel commandPanel = new JPanel(new GridLayout(1, 3));

    GemButton reset = new GemButton("Reset");
    reset.setToolTipText("Restaurer les couleurs par défaut");
    reset.addActionListener(this);
    GemButton ok = new GemButton(GemCommand.VALIDATE_CMD);
    ok.addActionListener(this);
    GemButton abandon = new GemButton(GemCommand.CANCEL_CMD);
    abandon.addActionListener(this);

    commandPanel.add(reset);
    commandPanel.add(ok);
    commandPanel.add(abandon);

    add(preview);
    add(helpPanel);
    add(commandPanel);
  }

  public void close() {
    actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (GemCommand.VALIDATE_CMD.equals(e.getActionCommand())) {
      Map<ColorPlan, Color> m = preview.getColors();

      for (Map.Entry<ColorPlan, Color> entry : m.entrySet()) {
        preview.getPrefs().setColor(entry.getKey(), entry.getValue());
      }
      close();
    } else if (GemCommand.CANCEL_CMD.equals(e.getActionCommand())) {
      desktop.removeCurrentModule();
    } else if ("Reset".equals(e.getActionCommand())) {
      if (MessagePopup.confirm(this, MessageUtil.getMessage("prefs.color.reset.warning"))) {
        for (ColorPlan k : ColorPlan.values()) {
          preview.getPrefs().setColor(k, k.getDefaultColor());
        }
        close();
      }
    }
  }
}
