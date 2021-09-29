/*
 * @(#)PostitModule2.java	3.0.0 10/09/2021
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
 * Copyright (c) 1999-2021 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.postit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import net.algem.planning.DateFr;
import net.algem.security.UserService;
import net.algem.util.GemLogger;
import net.algem.util.module.DefaultGemView;
import net.algem.util.module.GemModule;
import static net.algem.planning.day.DayScheduleCtrl.DAY_SCHEDULE_WINDOW_HEIGHT;

/**
 * Internal frame used to display postits.
 *
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 3.0.0
 */
public class PostitModule2
        extends PostitModule
{

  public static final int POSTIT_MODULE_HEIGHT = 110;

  public PostitModule2(String label) {
    super(label);
   }

  public PostitModule2(UserService service) {
    super(service);
  }

  @Override
  public void init() {
      GemLogger.info("PostitModule2.init");
      POSTIT_SIZE = new Dimension(600, POSTIT_MODULE_HEIGHT);
    view = new DefaultGemView(desktop, "Postit");

    postitCanvas = new PostitCanvas2();
    postitCanvas.addActionListener(this);
    lastRead = 0;
    view.add(postitCanvas, BorderLayout.CENTER);
    view.addVetoableChangeListener(this);
    view.setSize(POSTIT_SIZE);
  }

  public Container getPanel() {
      GemLogger.info("PostitModule2.getPanel");
    return postitCanvas;
  }


}
