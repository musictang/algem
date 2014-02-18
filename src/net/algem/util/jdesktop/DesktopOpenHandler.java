/*
 * @(#)DesktopOpenHandler.java 2.8.s 18/02/14
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
package net.algem.util.jdesktop;

import java.io.File;
import java.io.IOException;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

/**
 * Java desktop handler for file opening.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.s
 */
public class DesktopOpenHandler
        extends DesktopHandler
{

  public DesktopOpenHandler() {
  }

  public void open(String... paths) throws DesktopHandlerException {
    if (!isOpenSupported()) {
      GemLogger.log("Desktop Open not supported");
      executeClient(paths);
    }
    try {
      for (String path : paths) {
        getDesktop().open(new File(path));
      }
    } catch (Exception e) {
      GemLogger.log(e.getMessage() + " : Desktop Open Exception");
      executeClient(paths);
    }
  }
  
  
  /**
   * System-level execution alternative.
   * 
   * @param path
   * @throws DesktopHandlerException 
   */
  void executeClient(String... paths) throws DesktopHandlerException {
    if (paths == null || paths.length == 0) {
      throw new DesktopHandlerException(MessageUtil.getMessage("empty.path.exception"));
    }
    //MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
    String prog = null;
    try {
      for (String p : paths) {
        //String mtype = mimeTypes.getContentType(p); // not used because application/octet-stream returned is too general
        if (p.endsWith("pdf")) {
          prog = BundleUtil.getLabel("Pdf.client");
        } else {
          prog = BundleUtil.getLabel("Office.client");
        }
        if (prog == null) {
          prog = "oowriter";// default application as a last resort
        }
        String[] command = {prog, p};
        Runtime.getRuntime().exec(command);
      }
    } catch (IOException ioe) {
      throw new DesktopHandlerException(ioe.getMessage());
    }
  }

}

