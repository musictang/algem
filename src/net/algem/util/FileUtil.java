/*
 * @(#)FileUtil.java	2.9.4.14 09/12/15
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
package net.algem.util;

import java.awt.Color;
import java.awt.Component;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.util.jdesktop.DesktopHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for file operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.0q
 */
public class FileUtil
{

  public final static String FILE_SEPARATOR = System.getProperty("file.separator");

  /** Relative path for invoice footer file. */
  public final static String INVOICE_FOOTER_FILE = "/resources/doc/fact-pdp.txt";

  public final static String DEFAULT_CSS_DIR = "/resources/css";

  public final static String DEFAULT_HELP_DIR = "/resources/doc/html";

//  public final static String DOC_DIR = "/resources/doc/";

  /** Default margin in mm for printing. */
  private static int MARGIN = 5; //

  /** Default width in mm for printing. */
  private static int WIDTH = 210;

  /** Default heigth in mm for printing. */
  private static int HEIGHT = 297;//

  /**
   * Gets a directory.
   *
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
   *
   * @param parent parent
   * @param command text of command
   * @param options array of options (path, extension name, extension, ...)
   *
   * @return file path
   */
  public static String getFilePath(Component parent, String command, String... options) {
    JFileChooser chooser = getChooser(JFileChooser.FILES_ONLY, options[0]);
    if (options[1] != null && options[2] != null) {
      chooser.setFileFilter(new FileNameExtensionFilter(options[1], options[2]));
    }
    File file = getFile(chooser, parent, command);
    return file == null ? null : file.getPath();
  }

  public static File getFile(Component parent, String command, String path, String description, String... extensions) {
    JFileChooser chooser = getChooser(JFileChooser.FILES_ONLY, path);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    if (extensions != null && extensions.length > 0) {
      chooser.setFileFilter(new FileNameExtensionFilter(description, extensions));
    }
    return getFile(chooser, parent, command);
  }

  private static File getFile(JFileChooser fc, Component parent, String command) {
    File file = null;
    int ret = fc.showDialog(parent, command);
    if (ret == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();
    }
    return file;
  }

  /**
   * Find the most recent file in the specified {@code dirname} for contact {@code idper}.
   * The name of this file must match the following pattern : {@code ^.*[0-9]*\..*$}.
   *
   * @param parent parent directory name
   * @param subDir actual directory name
   * @param id contact id
   * @return a file if at list one is found, null otherwise.
   */
  public static File findLastFile(String parent, final String subDir, final int id) {

    File baseDir = null;
    File[] files = null;

    ContactFileNameFilter fileNameFilter = new ContactFileNameFilter(id);

    if (parent != null) {
      baseDir = new File(parent + subDir);
      if (isValidDirectory(baseDir)) {
        files = baseDir.listFiles(fileNameFilter);
      }
    }

    if (files == null || files.length == 0) {
      return null;
    }
    Arrays.sort(files, new Comparator<File>()
    {
      @Override
      public int compare(File o1, File o2) {
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

  public static String getDocumentPath(ConfigKey config, DataConnection dc) {
    String path = ConfigUtil.getPath(config);
    if (path != null && !path.isEmpty()) {
      if (!path.endsWith(FILE_SEPARATOR)) {
        path += FILE_SEPARATOR;
      }
    }
    return path;
  }

  private static boolean isValidDirectory(File file) {
    return file != null && file.isDirectory() && file.canRead();
  }

  /**
   * Opens some file by java desktop.
   *
   * @param handler
   * @param path the path of the file to open
   */
  public static void open(DesktopHandler handler, String path) {
    try {
      ((DesktopOpenHandler) handler).open(path);
    } catch (DesktopHandlerException ex) {
      GemLogger.log(ex.getMessage());
    }
  }

  /**
   *
   * @param mode selection mode
   * @param path default path
   * @return a file chooser
   */
  private static JFileChooser getChooser(int mode, String path) {
    JFileChooser fc = new JFileChooser(path);
    fc.setFileSelectionMode(mode);
    return fc;
  }

  public static boolean confirmOverWrite(Component parent, File f) {
    if (f.exists()) {
      return MessagePopup.confirm(parent, MessageUtil.getMessage("file.overwrite.confirmation", f.getName()));
    }
    return true;
  }

  /**
   * Replace accents with their code in RTF export.
   *
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
   *
   * @param path file path
   * @return an integer
   * @throws IOException
   */
  public static int getNumberOfLines(String path) throws IOException {

    FileReader r = new FileReader(path);
    LineNumberReader l = new LineNumberReader(new BufferedReader(r));
    while (l.readLine() != null) {
    }
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

  public static String getHtmlHeader(String title, String css) {
    return "<!DOCTYPE html>\n<html>\n\t<head>\n\t\t<title>" + title + "</title>\n\t\t<meta charset=\"utf-8\" />\n\t\t<style type=\"text/css\">" + css + "</style>\n\t</head>\n\t<body>";
  }

  /**
   *
   * @param f file to print
   * @param flavor ex. DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_8, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_8
   * @throws PrintException if any exception is catched
   */
  public static void printFile(File f, DocFlavor flavor) throws PrintException {
    // TODO java.lang.IllegalArgumentException: services must be non-null and non-empty at javax.print.ServiceUI.printDialog(ServiceUI.java:167).
    //java.lang.NullPointerException at sun.print.ServiceDialog$PrintServicePanel.init(ServiceDialog.java:719)
    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    try {
      InputStream in = new FileInputStream(f);
      PrintService printService1[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
      PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
      PrintService service = ServiceUI.printDialog(null, 200, 200, printService1, defaultService, flavor, pras);

      DocPrintJob printJob = service.createPrintJob();
      DocAttributeSet das = new HashDocAttributeSet();
      Doc doc = new SimpleDoc(in, flavor, das);
      printJob.print(doc, pras);
      in.close();
    } catch (IOException ex) {
      throw new PrintException(ex.getMessage());
    }
  }

  /**
   * Helper method for opening a save file chooser dialog.
   *
   * @param component the AWT parent of the chooser dialog
   * @param extension the request extension (ex. "xls")
   * @param extensionName the displayed name for files (ex. "Excel files")
   * @param proposed suggested file name (may be null)
   * @return the selected File (may be null)
   */
  public static File getSaveFile(Component component, String extension, String extensionName, String proposed) {
    JFileChooser jFileChooser = new JFileChooser() {
      @Override
      public void approveSelection(){
        File f = getSelectedFile();
        if(f.exists() && getDialogType() == SAVE_DIALOG){
          int result = JOptionPane.showConfirmDialog(this,MessageUtil.getMessage("file.overwrite.confirmation", f.getName()),MessageUtil.getMessage("file.existing"),JOptionPane.YES_NO_CANCEL_OPTION);
          switch(result){
            case JOptionPane.YES_OPTION:
              super.approveSelection();
              return;
            case JOptionPane.NO_OPTION:
              return;
            case JOptionPane.CLOSED_OPTION:
              return;
            case JOptionPane.CANCEL_OPTION:
              cancelSelection();
              return;
          }
        }
        super.approveSelection();
      }
    };
    if (proposed != null && proposed.trim().length() > 0) {
      jFileChooser.setSelectedFile(new File(proposed.endsWith("." + extension) ? proposed : proposed + "." + extension));
    }
    jFileChooser.setFileFilter(new FileNameExtensionFilter(extensionName, extension));
    if (jFileChooser.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
      File destFile = jFileChooser.getSelectedFile();
      String path = destFile.getAbsolutePath();
      return path.endsWith("." + extension) ? destFile : new File(path + "." + extension);
    }
    return null;
  }

  /**
   * Prints Look & Feel default colors.
   */
  public static void printUIColors() {
    List<String> colorKeys = new ArrayList<>();
    Set<Entry<Object, Object>> entries = UIManager.getLookAndFeelDefaults().entrySet();
    for (Entry entry : entries) {
      if (entry.getValue() instanceof Color) {
        colorKeys.add((String) entry.getKey());
      }
    }
    // sort the color keys
    Collections.sort(colorKeys);

    // print the color keys
    for (String colorKey : colorKeys) {
      System.out.println(colorKey);
      System.out.println(UIManager.getColor(colorKey));
    }

  }
}