/*
 * @(#) ImportCsvHandler.java Algem 2.13.0 31/03/2017
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
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
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.comment.CommentStartsWith;
import org.supercsv.exception.SuperCsvCellProcessorException;
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

  private static final int TEL1_TYPE = 1;
  private static final int TEL2_TYPE = 8;
  private String fileName;
  private Charset charset;
  private int errors;

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

  int getErrors() {
    return errors;
  }

  public List<ContactImport> create(CellProcessor[] processors, Map<String, Integer> map) throws IOException {
    ICsvListReader listReader = getReader();
    listReader.getHeader(true);
    errors = 0;
    List<ContactImport> contacts = new ArrayList<>();
    while ((listReader.read()) != null) {
      try {
        final List<Object> rowData = listReader.executeProcessors(processors);

        ContactImport c = new ContactImport();
        Contact p = null;
        String sid = getField(rowData, map, 0);
        c.setId(sid == null ? 0 : Integer.parseInt(sid));
        String gender = getField(rowData, map, 1);
        c.setGender(gender == null ? "" : gender);
        c.setName(getField(rowData, map, 2));
        c.setFirstName(getField(rowData, map, 3));

        int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[4]);// contact birth date
        if (idx > -1) {
          c.setBirthDate((Date) rowData.get(idx));
        }

        c.setInstrument(getField(rowData, map, 5));

        idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[8]); // parent name
        if (idx > -1) {
          p = new Contact();
          p.setName((String) rowData.get(idx));
        }

        idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[6]); // parent id
        if (idx > -1) {
          String pid = (String) rowData.get(idx);
          if (p != null) {
            p.setId(Integer.parseInt(pid));
          }
        }
        idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[7]); // parent gender
        if (idx > -1) {
          if (p != null) {
            p.setGender((String) rowData.get(idx));
          }
        }

        idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[9]); //parent first name
        if (idx > -1) {
          if (p != null) {
            p.setFirstName((String) rowData.get(idx));
          }
        }

        setAddress(rowData, map, c, p);
        setTels(rowData, map, c, p);
        setEmails(rowData, map, c, p);

        if (c.getName() != null) {
          c.setParent(p);
          contacts.add(c);
        }
      } catch (SuperCsvCellProcessorException pex) {
          GemLogger.log(pex.getMessage());
          errors++;
      }
    }

    return contacts;
  }

  private String getField(List<Object> rowData, Map<String, Integer> map, int index) {
    int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[index]); // contact id
    if (idx > -1) {
      return (String) rowData.get(idx);
    }
    return null;
  }

  private void setAddress(List<Object> rowData, Map<String, Integer> map, ContactImport c, Contact p) {
    int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[10]);
    if (idx > -1) {
      String adr1 = (String) rowData.get(idx);
      if (adr1 != null) {
        Address a = new Address();
        a.setAdr1(adr1);
        if (p != null) {
          p.setAddress(a);
        } else {
          c.setAddress(a);
        }
      } else {return;}
    }

    idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[11]);
    if (idx > -1) {
      String adr2 = (String) rowData.get(idx);
      if (c.getAddress() != null) {
        c.getAddress().setAdr2(adr2);
      } else if (p != null && p.getAddress() != null) {
        p.getAddress().setAdr2(adr2);
      }
    }

    idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[12]);
    if (idx > -1) {
      String cdp = (String) rowData.get(idx);
      if (c.getAddress() != null) {
        c.getAddress().setCdp(cdp);
      } else if (p != null && p.getAddress() != null) {
        p.getAddress().setCdp(cdp);
      }
    }

    idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[13]);
    if (idx > -1) {
      String city = (String) rowData.get(idx);
      if (c.getAddress() != null) {
        c.getAddress().setCity(city);
      } else if (p != null && p.getAddress() != null) {
        p.getAddress().setCity(city);
      }
    }
  }

  private void setTels(List<Object> rowData, Map<String, Integer> map, ContactImport c, Contact p) {
    int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[14]);
      String tel1 = null;
      if (idx > -1) {
        tel1 = (String) rowData.get(idx);
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
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[15]);
      String tel2 = null;
      if (idx > -1) {
        tel2 = (String) rowData.get(idx);
        if (tel2 != null && tel2.length() > 0) {
          Telephone t2 = new Telephone();
          t2.setNumber(tel2);
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

  }

  private void setEmails(List<Object> rowData, Map<String, Integer> map, ContactImport c, Contact p) {
    int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[16]);
    if (idx > -1) {
      String email1 = (String) rowData.get(idx);
      if (email1 != null && email1.length() > 0) {
        Vector<Email> emails = new Vector<Email>();
        Email m1 = new Email();
        m1.setEmail(email1);
        emails.add(m1);
        c.setEmail(emails);
      }
    }
    idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[17]);
    if (idx > -1) {
      if (p != null) {
        String email2 = (String) rowData.get(idx);
        if (email2 != null && email2.length() > 0) {
          Vector<Email> emails = new Vector<Email>();
          Email m2 = new Email();
          m2.setEmail(email2);
          emails.add(m2);
          p.setEmail(emails);
        }
      }
    }
  }

  public CellProcessor[] createProcessorFromSource(int length) {
    return new CellProcessor[length];
  }

  /**
   * Tries to detect the charset of the file {@code f}.
   * @param f file
   * @return the detected charset
   */
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
