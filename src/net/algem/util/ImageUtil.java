/*
 * @(#)ImageUtil.java	2.15.0 20/09/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.algem.accounting.AccountUtil;
import net.algem.config.Company;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.PhotoHandler;
import net.algem.contact.SimplePhotoHandler;
import net.algem.util.model.DataException;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;

/**
 * Utility class for image operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 */
public class ImageUtil
{

  public static final String IMAGE_PATH = "/resources/images/";
  public static final String PHOTO_PATH = "/resources/photos/";
  public static final String DEFAULT_PHOTO_ID = "/resources/images/idphoto.png";
  public static final String DEFAULT_PDF_TEMPLATE = "/resources/images/pdf.png";
  public static final String ALGEM_LOGO = "logo.png";
  public static final String SEARCH_ICON = "edit-find.png";
  public static final String CAL_ICON = "cal.png";
  public static final String NO_CONFLICT_ICON = "feuvert.gif";
  public static final String CONFLICT_ICON = "feurouge.gif";
  public static final String DELETE_ICON = "quitter_trans_12x12.png";
  public static final String TAB_CLOSING_ICON = "quitter_trans_12x12.png";
  public static final String [] DEFAULT_IMG_EXTENSIONS = {".jpg",".jpg",".JPG",".JPEG",".png",".PNG"};
  public static final int PHOTO_ID_HEIGHT = 130;
  private static final int PHOTO_ID_WIDTH = 100;
  private static final PhotoHandler PHOTO_HANDLER = new SimplePhotoHandler(DataCache.getDataConnection());

  public ImageUtil() {
  }

  /**
   * Redimensionne les photos proportionnellement à la hauteur d'une photo d'identité (PHOTO_ID_HEIGHT).
   * @param img BufferedImage
   * @return BufferedImage
   */
  public static BufferedImage rescale(BufferedImage img) {
  	return rescale(img, PHOTO_ID_WIDTH, PHOTO_ID_HEIGHT);
  }


  /**
   * Proportional resizing of an image.
   * @param img
   * @param nw new width
   * @param nh new heigth
   * @return a buffered image
   */
  public static BufferedImage rescale(BufferedImage img, int nw, int nh) {
  	int w = img.getWidth();
    int h = img.getHeight();
    double width = (double) w;
    double height = (double) h;
    double rapport = 1;
    int newWidth = 0;
    if (width >= height) {
      rapport = width / height;
      newWidth = (int) (nh * rapport);
    } else {
      rapport = height / width;
      newWidth = (int) (nh / rapport);
    }
    BufferedImage dimg = new BufferedImage(newWidth, nh, img.getType());
    Graphics2D g = dimg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    /*g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);*/
    g.drawImage(img, 0, 0, newWidth, nh, 0, 0, w, h, null);
    g.dispose();
    return dimg;
  }

  public static BufferedImage rescaleSmooth(BufferedImage img, int nw, int nh) {
    Image imgSmall = img.getScaledInstance(nw, -nh, Image.SCALE_SMOOTH);
    BufferedImage dimg = new BufferedImage(imgSmall.getWidth(null), imgSmall.getHeight(null), img.getType());
    Graphics2D g = dimg.createGraphics();
    g.drawImage(imgSmall, 0, 0, null);
    g.dispose();
    return dimg;
  }

  /**
   * Cropping width to comply to passport photo.
   * @param img BufferedImage
   * @return BufferedImage
   */
  public static BufferedImage cropPhotoId(BufferedImage img) {
    try {
      int w = img.getWidth();
      int h = img.getHeight();
      int x = 0;
      //System.out.println("w = "+w+" h = "+h);
      double width = img.getWidth();
      if (width > PHOTO_ID_WIDTH) {
        double half_width = width / 2;
        //System.out.println("half width "+half_width);
        x = (int) (half_width - (PHOTO_ID_WIDTH / 2));
        //System.out.println("x = "+x);
        BufferedImage img2 = img.getSubimage(x, 0, PHOTO_ID_WIDTH, h);
        return img2;
      } else {
        return img;
      }
    } catch (Exception e) {
      GemLogger.log("#formatPhoto "+e.getMessage());
      return null;
    }
  }

  /**
   * Converts a number of points in mm.
   * @param points
   * @return value in mm
   */
  public static double toMM(int points) {
    double res = (points / 72d) * 25.4;
    return AccountUtil.round(res);
  }

  /**
   * Converts mm value in inches.
   * @param mm
   * @return value in inches
   */
  public static double toInch(double mm) {
    double res = (mm / 25.4);
    return AccountUtil.round(res);
  }

  /**
   * Converts mm value in points.
   * @param mm
   * @return a value in points
   */
  public static int mmToPoints(double mm) {
    double val = (mm / 25.4) * 72;
    return (int) Math.round(val);
    //return (int) Math.round(inchesToPoints(toInch(mm)));
  }

  /**
   * Gets the icon with name {@literal file}.
   * @param file file name
   * @return an imageicon
   */
  public static ImageIcon createImageIcon(String file) {
    ImageIcon img = null;
    String path = IMAGE_PATH + file;
    URL url = new ImageUtil().getClass().getResource(path);
    if (url != null) {
      img = new ImageIcon(url);
    } 
    return img;
  }

  public static BufferedImage getPhoto(int idper) {
    BufferedImage img = PHOTO_HANDLER.load(idper);
    if (img == null) {
      BufferedImage orig = getPhotoFromConfigDir(ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey()), new PhotoFileFilter(idper));
      if (orig != null) {
        try {
          img = PHOTO_HANDLER.saveFromBuffer(idper, orig);
        } catch (DataException ex) {
          GemLogger.log(ex.getMessage());
        }
      } else {
        img = getPhotoDefault();
      }
    }
    return img;
  }

  public static String getStampPath(Company comp) {
    try {
      File stamp = File.createTempFile("stamp_", ".png");
      byte[] data = comp.getStamp();
      if (data == null) {
        return "";
      }
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      BufferedImage img = ImageIO.read(in);

      ImageIO.write(img, "png", stamp);

      return stamp.getPath().replaceAll("\\\\", "/");
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return "";
    }
  }
  
  public static String getLogoAsBase64(Company comp) {
    if (comp == null) return "";
    byte[] data = comp.getLogo();
    return data == null ? "" : Base64.encodeBase64String(data);
  }

  /**
   * Gets the photo matching this {@code filter}.
   *
   * @param configDir configured photo directory
   * @param filter file name filter
   * @return a buffered image if a resource has been found or null otherwhise
   */
  private static BufferedImage getPhotoFromConfigDir(String configDir, FileFilter filter) {

    File dir = new File(configDir);
    File[] files = null;
    if (dir.isDirectory() && dir.canRead()) {
      files = dir.listFiles(filter);
    }
    try {
      if (files != null && files.length > 0) {
        return ImageIO.read(files[0]);
      }
      /*else { // default resource path USELESS
        for (String s : ImageUtil.DEFAULT_IMG_EXTENSIONS) {
          InputStream input = getClass().getResourceAsStream(ImageUtil.PHOTO_PATH + idper + s);
          if (input == null) {
            input = getClass().getResourceAsStream(ImageUtil.DEFAULT_PHOTO_ID);
          }
          return ImageIO.read(input);
        }
      }*/
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
    return null;
  }

  public static BufferedImage getPhotoDefault() {
    try {
      InputStream input = new ImageUtil().getClass().getResourceAsStream(DEFAULT_PHOTO_ID);
      return ImageIO.read(input);
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }

  /**
   * Converts inches to points (1/72 inch) value.
   * @param inches
   * @return a value in points
   */
  private static double inchesToPoints(double inches) {
    return inches * 72;
  }

}
