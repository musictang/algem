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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import net.algem.util.DataConnection;
import net.algem.util.ImageUtil;
import net.algem.util.model.DataException;

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

  public SimplePhotoHandler(DataConnection dc) {
    this.photoIO = new PhotoIO(dc);
  }


  @Override
  public void importFiles() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      BufferedImage img = ImageIO.read(file);
      BufferedImage bimg = ImageUtil.rescale(img);
      bimg = ImageUtil.formatPhoto(bimg);

      ImageIO.write(bimg, "jpg", out);
      out.flush();
      byte[] data = out.toByteArray();
      photoIO.save(idper, data);
      return bimg;
    } catch (IOException | SQLException e) {
      throw new DataException(e.getMessage());
    }

  }

  @Override
  public BufferedImage resize(BufferedImage img) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
