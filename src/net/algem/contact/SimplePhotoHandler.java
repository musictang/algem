/*
 * @(#) SimplePhotoHandler.java Algem 2.9.4.14 09/12/2015
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
 */
package net.algem.contact;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.model.DataException;
import net.algem.util.ui.ProgressMonitorHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.14 09/12/2015
 */
public class SimplePhotoHandler
        implements PhotoHandler
{

  private PhotoIO photoIO;
  private PropertyChangeListener listener;

  public SimplePhotoHandler(DataConnection dc, PropertyChangeListener listener) {
    this.photoIO = new PhotoIO(dc);
    this.listener = listener;
  }

  @Override
  public int importFilesFromDir(File dir) {
//    final File[] files = dir.listFiles();
//    int saved = 0;
    SwingWorker<Integer, Void> task = new PhotoImportTask(dir);
    
    task.addPropertyChangeListener(listener);
    task.execute();
    try {
      return task.get();
    } catch (InterruptedException ex) {
      Logger.getLogger(SimplePhotoHandler.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ExecutionException ex) {
      Logger.getLogger(SimplePhotoHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 0;
  }

  private int getIdFromFileName(String fileName) {
    String sub = fileName.trim().substring(0, fileName.lastIndexOf('.'));
    try {
      int id = Integer.parseInt(sub);
      return id;
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  @Override
  public BufferedImage load(int idper) {
    try {
      return photoIO.find(idper);
    } catch (DataException ex) {
      return null;
    }
  }

  @Override
  public BufferedImage save(int idper, File file) throws DataException {
    try {
      BufferedImage img = ImageIO.read(file);
      BufferedImage cropped = format(img);

      byte[] data = getBytesFromImage(cropped);
      photoIO.save(idper, data);
      return cropped;
    } catch (IOException | SQLException e) {
      throw new DataException(e.getMessage());
    }

  }

  private byte[] getBytesFromImage(BufferedImage img) {
    byte[] data = null;

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ImageIO.write(img, "jpg", out);
      out.flush();
      return out.toByteArray();
    } catch (IOException e) {
      GemLogger.log(e.getMessage());
      return null;
    }
  }

  private BufferedImage format(BufferedImage img) {
    BufferedImage bimg = ImageUtil.rescale(img);
    return ImageUtil.cropPhotoId(bimg);
  }

  @Override
  public BufferedImage resize(BufferedImage img) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  class PhotoImportTask
          extends SwingWorker<Integer, Void>
  {

    private File dir;

    public PhotoImportTask(File dir) {
      this.dir = dir;
    }

    @Override
    protected Integer doInBackground() throws Exception {
      File[] files = dir.listFiles();
      Map<Integer, byte[]> filtered = new HashMap<>();
      int k = 0;
      int size = files.length;
      for (File f : files) {
         try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {}
        try {
          BufferedImage buffered = ImageIO.read(f); // check if file is really an image
          if (buffered != null) {
            buffered = format(buffered);
            byte[] data = getBytesFromImage(buffered);
            int id = getIdFromFileName(f.getName());
            System.out.println(id);
            if (data != null && id > 0) {
              filtered.put(id, data);
            }
          }
          int p = k * 100 / size;
          setProgress(p);
          k++;
        } catch (IOException ex) {
          GemLogger.log(ex.getMessage());
        }
      }
      return photoIO.save(filtered);
//      return null;
    }
  }
    
  

}
