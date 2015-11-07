/*
 * @(#)ProgressMonitorHandler.java	2.9.4.13 06/11/15
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
package net.algem.util.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 06/11/15
 */
public class ProgressMonitorHandler<T, V>
  implements PropertyChangeListener {

  private final ProgressMonitor monitor;
  private final SwingWorker<T, V> task;

  public ProgressMonitorHandler(ProgressMonitor monitor, SwingWorker<T, V> task) {
    this.monitor = monitor;
    this.task = task;
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    // if the operation is finished or has been canceled by
    // the user, take appropriate action
    if (monitor.isCanceled()) {
      task.cancel(true);
    } else if (event.getPropertyName().equals("progress")) {
      // get the % complete from the progress event
      // and set it on the progress monitor
      int progress = ((Integer) event.getNewValue());
      monitor.setProgress(progress);
    }
  }

}
