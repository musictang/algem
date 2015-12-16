/*
 * @(#) SimplePhotoHandler.java Algem 2.9.4.14 13/12/2015
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

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.DataException;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.ProgressMonitorHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.14 09/12/2015
 */
public class SimplePhotoHandler
  implements PhotoHandler {

  private PhotoIO photoIO;
  private Component parent;

  public SimplePhotoHandler(Component parent, DataConnection dc) {
    this(dc);
    this.parent = parent;
  }

  public SimplePhotoHandler(DataConnection dc) {
    this.photoIO = new PhotoIO(dc);
  }

  @Override
  public void importFilesFromDir(File dir) {
    final ProgressMonitor monitor = new ProgressMonitor(parent, BundleUtil.getLabel("Loading.label"), "", 1, 100);
    monitor.setProgress(1);
    monitor.setMillisToDecideToPopup(1);
    monitor.setMillisToPopup(1);

    SwingWorker<Integer, Void> task = new PhotoImportTask(dir);
    task.addPropertyChangeListener(new ProgressMonitorHandler(monitor, task));
    task.execute();
  }

  @Override
  public void exportFilesToDir(File dir) {
    final ProgressMonitor monitor = new ProgressMonitor(parent, BundleUtil.getLabel("Loading.label"), "", 1, 100);
    monitor.setProgress(1);
    monitor.setMillisToDecideToPopup(1);
    monitor.setMillisToPopup(1);

    SwingWorker<Integer, Void> task = new PhotoExportTask(dir);
    task.addPropertyChangeListener(new ProgressMonitorHandler(monitor, task));
    task.execute();
  }

  private int getIdFromFileName(String fileName) {
    try {
      String sub = fileName.substring(0, fileName.lastIndexOf('.'));
      int id = Integer.parseInt(sub);
      return id;
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
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

  class PhotoImportTask
    extends SwingWorker<Integer, Void> {

    private File dir;

    public PhotoImportTask(File dir) {
      this.dir = dir;
    }

    @Override
    protected Integer doInBackground() throws Exception {
      File[] files = dir.listFiles();
      Map<Integer, byte[]> filtered = new TreeMap<>();
      int k = 0;
      int size = files.length;
      for (File f : files) {
        try {
          if (f.length() > Math.pow(2, 20)) { // limit length to 1Mio
            size--;
            continue;
          }
          BufferedImage buffered = ImageIO.read(f); // check if file is really an image
          if (buffered != null) {
            int id = getIdFromFileName(f.getName());
            if (id > 0) {
              buffered = format(buffered);
              byte[] data = getBytesFromImage(buffered);
              if (data != null) {
                filtered.put(id, data);
              }
            }
          }
          int p = ++k * 100 / size;
          setProgress(p);
        } catch (IOException ex) {
          GemLogger.log(ex.getMessage());
        }
      }
      return photoIO.save(filtered);
    }

    @Override
    public void done() {
      int saved = -1;
      String error = "";
      try {
        saved = get();
      } catch (InterruptedException | ExecutionException ex) {
        error = ex.getMessage();
        GemLogger.log(error);
      }
      if (saved >= 0) {
        MessagePopup.information(parent, MessageUtil.getMessage("photos.imported", saved));
      } else {
        MessagePopup.error(parent, MessageUtil.getMessage("saving.exception") + ":\n" + error);
      }
    }

  }

  class PhotoExportTask
    extends SwingWorker<Integer, Void> {

    private File dir;

    public PhotoExportTask(File dir) {
      this.dir = dir;
    }

    @Override
    protected Integer doInBackground() throws Exception {

      Map<Integer, byte[]> saved = photoIO.findAll();

      int exported = 0;
      int k = 0;
      int size = saved.size();
      String extension = ".jpg";
      for (Map.Entry<Integer, byte[]> entry : saved.entrySet()) {
        ByteArrayInputStream in = new ByteArrayInputStream(entry.getValue());
        if (in.available() > 0) {
          String name = String.valueOf(entry.getKey()) + extension;
          Path path = Paths.get(dir.getAbsolutePath() + FileUtil.FILE_SEPARATOR + name);
          Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
          exported++;
        }
        int p = ++k * 100 / size;
        setProgress(p);
      }
      return exported;
    }

    @Override
    public void done() {
      int exported = -1;
      String error = "";
      try {
        exported = get();
      } catch (InterruptedException | ExecutionException ex) {
        error = ex.getMessage();
        GemLogger.log(error);
      }
      if (exported >= 0) {
        MessagePopup.information(parent, MessageUtil.getMessage("files.exported", exported));
      } else {
        MessagePopup.error(parent, MessageUtil.getMessage("export.exception") + ":\n" + error);
      }
    }

  }

}
