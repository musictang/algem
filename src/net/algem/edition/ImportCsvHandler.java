/*
 * @(#) ImportCsvHandler.java Algem 2.13.0 22/03/2017
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
 */
package net.algem.edition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.algem.contact.Address;
import net.algem.contact.Contact;
import net.algem.contact.ContactImport;
import net.algem.contact.Email;
import net.algem.contact.Telephone;
import net.algem.util.GemLogger;
import net.algem.util.SimpleCharsetDecoder;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.comment.CommentStartsWith;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 15/03/2017
 */
public class ImportCsvHandler {

  public static String IMPORT_FILE_NAME = "/home/jm/algem/src/git/trunk/doc/test-import1.csv";
  private static final int TEL1_TYPE = 1;
  private static final int TEL2_TYPE = 8;
  //public static String IMPORT_FILE_NAME = "/home/jm/dev/algem/git/doc/test-import1.csv";
  private String fileName;
  private Charset charset;

//  private ICsvListReader listReader;
  public ImportCsvHandler() {
  }

  public ImportCsvHandler(String fileName) throws FileNotFoundException {
    this.fileName = fileName;
    //listReader = new CsvListReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
  }

  /**
   * An example of reading a file with a variable number of columns using CsvListReader. It demonstrates that you can
   * still use cell processors, but you must execute them by calling the executeProcessors() method on the reader,
   * instead of supplying processors to the read() method. In this scenario, the last column (birthDate) is sometimes
   * missing.
   */
  public static void main(String[] args) throws Exception {

    final CellProcessor[] allProcessors = new CellProcessor[]{new UniqueHashCode(), // customerNo (must be unique)
      new NotNull(), // firstName
      new NotNull(), // lastName
      new ParseDate("dd/MM/yyyy")}; // birthDate

    final CellProcessor[] noBirthDateProcessors = new CellProcessor[]{allProcessors[0], // customerNo
      allProcessors[1], // firstName
      allProcessors[2]}; // lastName

    ICsvListReader listReader = null;
    try {
      listReader = new CsvListReader(new FileReader(IMPORT_FILE_NAME), new CsvPreference.Builder('"', ';', "\n").build());

      String[] header = listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
      System.out.printf("nombre de colonnes : %d\n", header.length);
      for (String s : header) {
        System.out.println(s.trim());
      }
      while ((listReader.read()) != null) {
        // use different processors depending on the number of columns
        final CellProcessor[] processors;
        if (listReader.length() == noBirthDateProcessors.length) {
          processors = noBirthDateProcessors;
        } else {
          processors = allProcessors;
        }

        final List<Object> customerList = listReader.executeProcessors(processors);
        System.out.println(String.format("lineNo=%s, rowNo=%s, columns=%s, customerList=%s",
          listReader.getLineNumber(), listReader.getRowNumber(), customerList.size(), customerList));
      }

    } finally {
      if (listReader != null) {
        listReader.close();
      }
    }
  }

  public ICsvListReader getReader() throws FileNotFoundException, UnsupportedEncodingException {
    //listReader = new CsvListReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
    InputStreamReader input = new InputStreamReader(new FileInputStream(fileName), charset != null ? charset.name() : "UTF-8");
    return new CsvListReader(input, new CsvPreference.Builder('"', ';', "\n").ignoreEmptyLines(true).skipComments(new CommentStartsWith("#")).build());
  }

  public List<String> getPreview(ICsvListReader listReader) throws IOException {
    if (listReader != null) {
      List<String> data = listReader.read();
      listReader.close();
      return data;
    }

    return null;
  }

  void setFile(File file) {
    this.fileName = file.getPath();
    this.charset = getCharset(file);
  }

  public List<ContactImport> create(CellProcessor[] processors, Map<String, Integer> map) throws IOException {
    ICsvListReader listReader = getReader();
    listReader.getHeader(true);
    List<ContactImport> contacts = new ArrayList<>();
    while ((listReader.read()) != null) {
      final List<Object> rowData = listReader.executeProcessors(processors);
      ContactImport c = new ContactImport();
      Contact p = null;
      int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[0]);

      if (idx > -1) {
        String sid = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[0]));
        c.setId(Integer.parseInt(sid));
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[1]);
      if (idx > -1) {
        c.setGender((String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[1])));
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[2]);
      if (idx > -1) {
        c.setName((String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[2])));
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[3]);
      if (idx > -1) {
        c.setFirstName((String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[3])));
      }
      
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[5]); // parent name
      if (idx > -1) {
        p = new Contact();
        p.setName((String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[5])));
      }
      
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[4]);
      if (idx > -1) {
        if (p != null) {
          p.setGender((String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[4])));
        }
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[6]);
      if (idx > -1) {
        if (p != null) {
          p.setFirstName((String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[6])));
        }
      }
      
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[7]);
      if (idx > -1) {
        Address a = new Address();
        String adr1 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[7]));
        a.setAdr1(adr1);
        if (p != null) {
          p.setAddress(a);
        } else {
          c.setAddress(a);
        }
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[8]);
      if (idx > -1) {
        String adr2 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[8]));
        if (c.getAddress() != null) {
          c.getAddress().setAdr2(adr2);
        } else if (p != null && p.getAddress() != null) {
          p.getAddress().setAdr2(adr2);
        }
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[9]);
      if (idx > -1) {
        String cdp = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[9]));
        if (c.getAddress() != null) {
          c.getAddress().setCdp(cdp);
        } else if (p != null && p.getAddress() != null) {
          p.getAddress().setCdp(cdp);
        }
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[10]);
      if (idx > -1) {
        String city = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[10]));
        if (c.getAddress() != null) {
          c.getAddress().setCity(city);
        } else if (p != null && p.getAddress() != null) {
          p.getAddress().setCity(city);
        }
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[11]);
      String tel1 = null;
      if (idx > -1) {
        tel1 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[11]));
        if (tel1 != null && tel1.length() > 0) {
          Vector<Telephone> tels = new Vector<Telephone>();
          Telephone t1 = new Telephone();
          t1.setNumber(tel1);
          t1.setTypeTel(TEL1_TYPE);
          t1.setIdx(0);
          tels.add(t1);
          if (p != null) {
            p.setTele(tels);
          } else {
            c.setTele(tels);
          }
        }
      }
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[12]);
      String tel2 = null;
      if (idx > -1) {
        tel2 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[12]));
        if (tel2 != null && tel2.length() > 0) {
          Telephone t2 = new Telephone();
          t2.setNumber(tel1);
          t2.setTypeTel(TEL2_TYPE);
          if (p == null) {
            if (c.getTele() != null) {
              t2.setIdx(1);
              c.getTele().add(t2);
            } else {
              Vector<Telephone> tels = new Vector<Telephone>();
              t2.setIdx(0);
              tels.add(t2);
              c.setTele(tels);
            }
          } else {
            if (p.getTele() != null) {
              t2.setIdx(1);
              p.getTele().add(t2);
            } else {
              Vector<Telephone> tels = new Vector<Telephone>();
              t2.setIdx(0);
              tels.add(t2);
              p.setTele(tels);
            }
          }
        }
      }

      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[13]);
      if (idx > -1) {
        String email1 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[13]));
        if (email1 != null && email1.length() > 0) {
          Vector<Email> emails = new Vector<Email>();
          Email m1 = new Email();
          m1.setEmail(email1);
          emails.add(m1);
          c.setEmail(emails);
        }
      }
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[14]);
      if (idx > -1) {
        if (p != null) {
          String email2 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[14]));
          if (email2 != null && email2.length() > 0) {
            Vector<Email> emails = new Vector<Email>();
            Email m2 = new Email();
            m2.setEmail(email2);
            emails.add(m2);
            p.setEmail(emails);
          }
        }
      }

      if (c.getName() != null) {
        c.setParent(p);
        contacts.add(c);
      }
    }

    return contacts;
  }

  public CellProcessor[] createProcessorFromSource(int length) {

    return new CellProcessor[length];
  }

  private Charset getCharset(File f) {
    try {
      String[] charsetsToBeTested = {"UTF-8", "windows-1252", "ISO-8859-1", "ISO-8859-15", "x-MacRoman"};
      SimpleCharsetDecoder cd = new SimpleCharsetDecoder();
      return cd.detectCharset(f, charsetsToBeTested);
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }
}
