/*
 * @(#)FileUtil.java	2.8.n 04/10/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Component;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JFileChooser;
import net.algem.util.jdesktop.DesktopHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for file operations.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.0q
 */
public class FileUtil {

  public final static String FILE_SEPARATOR = System.getProperty("file.separator");
  public final static String LINE_SEPARATOR = System.getProperty("line.separator");

  /** Relative path for invoice footer file. */
  public final static String INVOICE_FOOTER_FILE = "/resources/doc/fact-pdp.txt";
  
  public final static String DOC_DIR = "/resources/doc/";

  /** Default margin in mm for printing. */
  private static int MARGIN = 5; //

  /** Default width in mm for printing. */
  private static int WIDTH = 210;

  /** Default heigth in mm for printing. */
  private static int HEIGHT = 297;// 

  /**
   * Gets a directory.
   * @param parent
   * @param command text of command
   * @param path default path
   * @return directory path
   */
  public static String getDir(Component parent, String command, String path) {

    JFileChooser chooser = getChooser(JFileChooser.DIRECTORIES_ONLY, path);
    File file = getFile(chooser, parent, command);
    return file == null ? null : file.getPath();
  }

  /**
   * Selects a file.
   * @param parent parent
   * @param command text of command
   * @param path default path
   * @return file path
   */
  public static String getFile(Component parent, String command, String path) {

    JFileChooser chooser = getChooser(JFileChooser.FILES_ONLY, path);
    File file = getFile(chooser, parent, command);
    return file == null ? null : file.getPath();
  }

  /**
   * Gets a file from some dir name and person's id.
   * @param dirPath relative directory path
   * @param idper contact's id
   * @return a file if one at least is found, null otherwhise
   */
  public static File findFile(String dirPath, final int idper) {

    File dirFile = getDirFile(dirPath);
    if (dirFile == null) {
      return null;
    }
    File[] files = dirFile.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        // without extension
        int idx = filename.lastIndexOf('.');
        return filename.substring(0, idx).toLowerCase().endsWith(String.valueOf(idper));
      }
    });
    if (files == null || files.length == 0) {
      return null;
    }
    return files[0].isFile() ? files[0] : null;

  }
  
  /**
   * Find the most recent file in the specified {@code dirname} for contact {@code idper}.
   * The name of this file must match the following pattern : {@code dirname.*idper\..*}.
   * 
   * @param parentDir parentDir below resources directory
   * @param dirName actual directory name
   * @param id contact id
   * @return a file if at list one is found, null otherwise.
   */
  public static File findLastFile(String parentDir, final String dirName, final int id) {

    File baseDir = getDirFile(parentDir+dirName);
    if (baseDir == null) {
      return null;
    }

    File[] files = baseDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        String lower = filename.toLowerCase();
        int idx = lower.lastIndexOf('.');
//        return lower.startsWith(dirName) && lower.substring(0,idx).endsWith(String.valueOf(id));
        return lower.substring(0,idx).endsWith(String.valueOf(id));
      }
    });
    if (files == null || files.length == 0) {
      return null;
    }
    Arrays.sort(files, new Comparator<File>()
    {
      @Override
      public int compare(File o1, File o2) {
        //assert(o1.getName().startsWith(DUE_DIR));
        //assert(o2.getName().startsWith(DUE_DIR));
        //int idx = dirname.length()+1;
        // DateFr d1 = new DateFr(o1.getName().substring(idx, idx + 10));
        // DateFr d2 = new DateFr(o2.getName().substring(idx, idx + 10));
        if (o1.lastModified() < o2.lastModified()) {
          return 1;
        } else if (o1.lastModified() > o2.lastModified()) {
          return -1;
        } else {
          return 0;
        }
      }
    });

    return files[0].isFile() ? files[0] : null;
  }
  
  /**
   * Gets a directory file from its name.
   * @param dir directory basename
   * @return a file
   */
  public static File getDirFile(String dir) {
    String path = FileUtil.DOC_DIR + dir;
    URL url = FileUtil.class.getResource(path);
    if (url != null) {
     File f = new File(url.getPath());
     return f.isDirectory() && f.canRead() ? f : null;
    }
    return null;
  }
  
  /**
   * Opens some file by java desktop.
   * @param path the path of the file to open
   */
  public static void open(DesktopHandler handler, String path) {
     try {
      ((DesktopOpenHandler) handler).open(path);
      } catch (DesktopHandlerException ex) {
        GemLogger.log(ex.getMessage());
        try {   
          String prog = BundleUtil.getLabel("Office.client");
          if (prog == null) {
            prog = "oowriter";
          }
          String [] command = {prog, path};
          Runtime.getRuntime().exec(command); 
        } catch (IOException ioe) {
          GemLogger.log(ioe.getMessage());
        }
      }
  }

  /**
   *
   * @param mode selection mode
   * @param path default path
   * @return
   */
  private static JFileChooser getChooser(int mode, String path) {
    JFileChooser fc = new JFileChooser(path);
    fc.setFileSelectionMode(mode);
    return fc;
  }

  private static File getFile(JFileChooser fc, Component parent, String command) {
    File file = null;
    int ret = fc.showDialog(parent, command);
    if (ret == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();
    }
    return file;
  }
  
  public static boolean confirmOverWrite(Component parent, File f) {
    if (f.exists()) {
      return MessagePopup.confirm(parent, MessageUtil.getMessage("file.overwrite.confirmation"));
    }
    return true;
  }

  /**
   * Escape backslashes in a string.
   * @param path
   * @return a string
   */
  public static String escapeBackSlashes(String path) {
      return path.replace("\\", "\\\\");
  }

  /**
   * Removes particular characters from a string.
   * @param s
   * @return a string
   */
  public static String replaceChars(String s) {
    char str [] = s.toCharArray();
    char from [] = {'À','Â','É','È','Ê','Ë','Î','Ï','Ô','Ö','Ù','Ü','\''};
    char to [] = {'A','A','E','E','E','E','I','I','O','O','U','U',' '};
    for (int i = 0 ; i < str.length; i++) {
      for (int j = 0 ; j < from.length; j++) {
        if (str[i] == from[j]) {
          str[i] = to[j];
        }
      }
    }
    return new String(str);

  }

  /**
   * Replace accents with their code in RTF export.
   * @param s 
   * @return a string
   */
  public static String rtfReplaceChars(String s) {
    
    s = s.replaceAll("é", "\\\\'e9");
    s = s.replaceAll("è", "\\\\'e8");
    s = s.replaceAll("ê", "\\\\'ea");
    s = s.replaceAll("ë", "\\\\'eb");

    s = s.replaceAll("à", "\\\\'e0");
    s = s.replaceAll("â", "\\\\'e2");
    s = s.replaceAll("ä", "\\\\'e4");

    s = s.replaceAll("î", "\\\\'ee");
    s = s.replaceAll("ï", "\\\\'ef");

    s = s.replaceAll("ô", "\\\\'f4");
    s = s.replaceAll("ö", "\\\\'f6");

    s = s.replaceAll("ù", "\\\\'f9");
    s = s.replaceAll("û", "\\\\'fb");
    s = s.replaceAll("ü", "\\\\'fc");
    s = s.replaceAll("ç", "\\\\'e7");

    return s;
  }

  /**
   * Gets the number of lines in a file.
   * @param path file path
   * @return an integer
   * @throws IOException
   */
  public static int getNumberOfLines(String path) throws IOException {

    FileReader r = new FileReader(path);
    LineNumberReader l = new LineNumberReader(new BufferedReader(r));
    while (l.readLine() != null) {}
    return l.getLineNumber();
  }

   /**
   * Printing configuration.
   * 
   * @param size page size
   * @param orientation page orientation
   * @return a set of attributes
   */
  public static PrintRequestAttributeSet getAttributeSet(MediaSizeName size, OrientationRequested orientation) {

    PrintRequestAttributeSet attSet = new HashPrintRequestAttributeSet();
    attSet.add(size);
    attSet.add(orientation);
    attSet.add(new MediaPrintableArea(MARGIN, MARGIN, WIDTH - (MARGIN * 2), HEIGHT - (MARGIN * 2), MediaSize.MM));

    return attSet;
  }

}